package com.delhight.backend.service;

import com.delhight.backend.model.NearbyStation;
import com.delhight.backend.model.RouteLeg;
import com.delhight.backend.model.RouteVariant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Clean implementation of the Delhight routing algorithm that produces at most 9 route variants:
 * For top-3 source stations and top-3 destination stations, compute:
 *   piece1 = best walking OR driving (drive only if walking > walkThresholdMeters)
 *   piece2 = transit (rail) with fixed departure time (10:00 local)
 *   piece3 = best walking OR driving (drive only if walking > walkThresholdMeters)
 *
 * Each station-pair yields exactly one RouteVariant (piece1 + piece2 + piece3).
 */
@Service
public class RouteComputationService {

    private final GeocodingService geocodingService;
    private final NearestStationsService nearestStationsService;
    private final DirectionsService directionsService;

    // configurable via application.properties (defaults provided)
    @Value("${delhight.routing.topStations:3}")
    private int topStations;

    @Value("${delhight.routing.walkThresholdMeters:400}")
    private int walkThresholdMeters;

    @Value("${delhight.routing.autoRatePerKmRs:12.0}")
    private double autoRatePerKmRs;

    @Value("${delhight.routing.maxVariants:9}")
    private int maxVariants;

    public RouteComputationService(GeocodingService geocodingService,
                                   NearestStationsService nearestStationsService,
                                   DirectionsService directionsService) {
        this.geocodingService = geocodingService;
        this.nearestStationsService = nearestStationsService;
        this.directionsService = directionsService;
    }

    /**
     * Public entry: compute routes for free-text addresses.
     */
    public List<RouteVariant> computeRoutes(String originAddress, String destAddress) {
        double[] o = geocodingService.geocodeAddress(originAddress);
        double[] d = geocodingService.geocodeAddress(destAddress);

        if (o == null || d == null) return List.of();

        return computeRoutes(o[0], o[1], d[0], d[1], originAddress, destAddress);
    }

    /**
     * Core algorithm using coordinates.
     */
    public List<RouteVariant> computeRoutes(double oLat, double oLng,
                                            double dLat, double dLng,
                                            String originName, String destName) {

        // find nearest stations (top N)
        List<NearbyStation> originStations = nearestStationsService.findNearestStations(oLat, oLng, topStations);
        List<NearbyStation> destStations = nearestStationsService.findNearestStations(dLat, dLng, topStations);

        List<RouteVariant> variants = new ArrayList<>();

        // departure time fixed to 10:00 local (approx) — use 0 if you want "now"
        long departureTimeEpochSeconds = fixedDepartureTimeEpochSeconds();

        int idCounter = 1;

        for (NearbyStation so : originStations) {
            for (NearbyStation sd : destStations) {

                // --- PIECE 1: origin -> so  (walking preferred; if > threshold, use driving instead)
                RouteLeg piece1Walk = directionsService.getRouteLeg(
                        oLat, oLng,
                        so.getLat(), so.getLng(),
                        "walking", 0L,
                        originName, so.getName()
                );

                RouteLeg piece1Final = piece1Walk;
                if (piece1Walk == null) {
                    // if walking failed, try driving
                    RouteLeg p1drive = directionsService.getRouteLeg(
                            oLat, oLng,
                            so.getLat(), so.getLng(),
                            "driving", 0L,
                            originName, so.getName()
                    );
                    piece1Final = p1drive;
                } else if (piece1Walk.getDistanceMeters() > walkThresholdMeters) {
                    // walking too long → replace with driving (single surviving leg)
                    RouteLeg p1drive = directionsService.getRouteLeg(
                            oLat, oLng,
                            so.getLat(), so.getLng(),
                            "driving", 0L,
                            originName, so.getName()
                    );
                    if (p1drive != null) piece1Final = p1drive;
                    // if driving failed for some reason, keep walking as fallback
                }

                if (piece1Final == null) {
                    // can't reach this station; skip this pair
                    continue;
                }

                // --- PIECE 2: metro transit so -> sd (transit_mode=rail, departure_time set)
                RouteLeg piece2Transit = directionsService.getRouteLeg(
                        so.getLat(), so.getLng(),
                        sd.getLat(), sd.getLng(),
                        "transit", departureTimeEpochSeconds,
                        so.getName(), sd.getName()
                );

                if (piece2Transit == null) {
                    // no transit path between these two stations according to Google; skip pair
                    continue;
                }

                // --- PIECE 3: sd -> destination (same logic as piece1)
                RouteLeg piece3Walk = directionsService.getRouteLeg(
                        sd.getLat(), sd.getLng(),
                        dLat, dLng,
                        "walking", 0L,
                        sd.getName(), destName
                );

                RouteLeg piece3Final = piece3Walk;
                if (piece3Walk == null) {
                    RouteLeg p3drive = directionsService.getRouteLeg(
                            sd.getLat(), sd.getLng(),
                            dLat, dLng,
                            "driving", 0L,
                            sd.getName(), destName
                    );
                    piece3Final = p3drive;
                } else if (piece3Walk.getDistanceMeters() > walkThresholdMeters) {
                    RouteLeg p3drive = directionsService.getRouteLeg(
                            sd.getLat(), sd.getLng(),
                            dLat, dLng,
                            "driving", 0L,
                            sd.getName(), destName
                    );
                    if (p3drive != null) piece3Final = p3drive;
                }

                if (piece3Final == null) {
                    // can't reach destination from this station; skip pair
                    continue;
                }

                // --- Build RouteVariant (exactly one per station-pair)
                RouteVariant variant = new RouteVariant();
                variant.setId("r" + (idCounter++) + "-" + (so.getPlaceId() != null ? so.getPlaceId() : so.getName())
                        + "-" + (sd.getPlaceId() != null ? sd.getPlaceId() : sd.getName()));

                List<RouteLeg> legs = new ArrayList<>();
                legs.add(piece1Final);
                legs.add(piece2Transit);
                legs.add(piece3Final);

                variant.setLegs(legs);

                // compute totals
                long totalDurationSeconds = 0L;
                double totalDistanceMeters = 0.0;
                double totalCostRs = 0.0;

                for (RouteLeg leg : legs) {
                    if (leg == null) continue;
                    totalDurationSeconds += leg.getDurationSeconds();
                    totalDistanceMeters += leg.getDistanceMeters();
                }

                // cost: transit fare if available + static auto fare for driving legs
                // piece2Transit may contain fareText (e.g., "₹25.00")
                double transitFare = parseFareTextToDouble(piece2Transit.getFareText());
                totalCostRs += transitFare;

                // for driving legs, compute static fare per-km
                for (RouteLeg leg : legs) {
                    if (leg == null) continue;
                    String mode = leg.getMode();
                    if (mode != null && mode.equalsIgnoreCase("driving")) {
                        double distKm = leg.getDistanceMeters() / 1000.0;
                        totalCostRs += Math.round(distKm * autoRatePerKmRs);
                    }
                }

                variant.setTotalDurationSeconds(totalDurationSeconds);
                variant.setTotalDistanceMeters(totalDistanceMeters);
                variant.setTotalCostRs(totalCostRs);

                // friendly summary
                String summary = buildSummaryText(piece1Final, piece2Transit, piece3Final);
                variant.setSummaryText(summary);

                variants.add(variant);
            }
        }

        // sort and return top-K (by duration then cost)
        variants.sort(Comparator.comparingLong(RouteVariant::getTotalDurationSeconds)
                .thenComparingDouble(RouteVariant::getTotalCostRs));

        if (variants.size() <= maxVariants) return variants;
        return new ArrayList<>(variants.subList(0, maxVariants));
    }

    // Helper: parse fare string like "₹32.00" → 32.0
    private double parseFareTextToDouble(String fareText) {
        if (fareText == null || fareText.isBlank()) return 0.0;
        try {
            String cleaned = fareText.replaceAll("[^0-9.]", "");
            if (cleaned.isEmpty()) return 0.0;
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Build a short human-friendly summary for UI
    private String buildSummaryText(RouteLeg p1, RouteLeg p2, RouteLeg p3) {
        StringBuilder sb = new StringBuilder();
        if (p1 != null) sb.append(modeLabel(p1.getMode())).append(" → ");
        if (p2 != null) {
            sb.append("METRO");
            // try to include line name if available from transit segments
            try {
                if (p2.getTransitSegments() != null && !p2.getTransitSegments().isEmpty()) {
                    String line = p2.getTransitSegments().get(0).getLineName();
                    if (line != null && !line.isBlank()) sb.append(" (").append(line).append(")");
                }
            } catch (Exception ignored) {}
            sb.append(" → ");
        }
        if (p3 != null) sb.append(modeLabel(p3.getMode()));
        return sb.toString();
    }

    private String modeLabel(String mode) {
        if (mode == null) return "move";
        switch (mode.toLowerCase()) {
            case "walking": return "Walk";
            case "driving": return "Auto";
            case "transit": return "Metro";
            default: return mode;
        }
    }

    /**
     * Fixed departure time at 10:00 local (approx) — returns epoch seconds.
     * Uses system default zone for simplicity.
     */
    private long fixedDepartureTimeEpochSeconds() {
        java.time.ZonedDateTime now = java.time.ZonedDateTime.now();
        java.time.ZonedDateTime tenAm = now.withHour(10).withMinute(0).withSecond(0).withNano(0);
        // if 10am already passed today, leave it as next day's 10am? For demo we can use today's 10am.
        return tenAm.toEpochSecond();
    }
}
