package com.delhight.backend.service;

import com.delhight.backend.model.RouteLeg;
import com.delhight.backend.model.TransitSegment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Google Directions implementation.
 *
 * - Calls the Google Directions HTTP API using WebClient.
 * - Parses the first route and returns a RouteLeg with detailed transit extraction.
 *
 * Notes:
 * - Defensive parsing: fields are optional; code uses optXXX before reading values.
 * - For transit segments it extracts: line name, short name, agency, vehicle type, trip short name,
 *   departure/arrival stop names, times, headsign, num_stops, intermediate stops[], stop_ids[] and platform if present.
 */
@Service
public class GoogleDirectionsService implements DirectionsService {

    private final WebClient webClient;

    @Value("${google.api.key:}")
    private String googleApiKey;

    private static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json";

    public GoogleDirectionsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public RouteLeg getRouteLeg(double fromLat, double fromLng,
                                double toLat, double toLng,
                                String mode, long departureTime,
                                String fromName, String toName) {

        String origin = fromLat + "," + fromLng;
        String destination = toLat + "," + toLng;

        String url = DIRECTIONS_URL +
                "?origin=" + origin +
                "&destination=" + destination +
                "&mode=" + mode +
                "&key=" + googleApiKey;

        if ("transit".equalsIgnoreCase(mode)) {
            url += "&transit_mode=rail";
        }
        if (departureTime > 0) {
            url += "&departure_time=" + departureTime;
        }

        Mono<String> mono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);

        String body;
        try {
            body = mono.block();
        } catch (Exception ex) {
            // If request failed, return null to let caller decide
            return null;
        }

        if (body == null || body.isEmpty()) return null;

        JSONObject root = new JSONObject(body);
        String status = root.optString("status", "");
        if (!"OK".equalsIgnoreCase(status)) {
            return null;
        }

        JSONArray routes = root.optJSONArray("routes");
        if (routes == null || routes.length() == 0) return null;

        JSONObject firstRoute = routes.getJSONObject(0);

        String overviewPolyline = null;
        if (firstRoute.has("overview_polyline")) {
            JSONObject op = firstRoute.optJSONObject("overview_polyline");
            if (op != null) overviewPolyline = op.optString("points", null);
        }

        JSONArray legs = firstRoute.optJSONArray("legs");
        if (legs == null || legs.length() == 0) return null;
        JSONObject firstLeg = legs.getJSONObject(0);

        int distanceMeters = 0;
        int durationSeconds = 0;
        if (firstLeg.has("distance")) {
            distanceMeters = firstLeg.optJSONObject("distance").optInt("value", 0);
        }
        if (firstLeg.has("duration")) {
            durationSeconds = firstLeg.optJSONObject("duration").optInt("value", 0);
        }

        // steps -> textual instructions
        List<String> steps = new ArrayList<>();
        JSONArray stepsArray = firstLeg.optJSONArray("steps");
        if (stepsArray != null) {
            IntStream.range(0, stepsArray.length()).forEach(i -> {
                JSONObject step = stepsArray.getJSONObject(i);
                String instr = step.optString("html_instructions", "");
                instr = instr.replaceAll("<[^>]*>", ""); // quick strip tags
                String distText = "";
                if (step.has("distance")) distText = " (" + step.optJSONObject("distance").optString("text", "") + ")";
                steps.add(instr + distText);
            });
        }

        RouteLeg leg = new RouteLeg();
        leg.setMode(mode);
        leg.setFromName(fromName != null ? fromName : origin);
        leg.setToName(toName != null ? toName : destination);
        leg.setPolyline(overviewPolyline);
        leg.setDistanceMeters(distanceMeters);
        leg.setDurationSeconds(durationSeconds);
        leg.setSteps(steps);

        // Fare extraction (if any)
        if (firstRoute.has("fare")) {
            JSONObject fare = firstRoute.optJSONObject("fare");
            if (fare != null) leg.setFareText(fare.optString("text", null));
        } else if (firstLeg.has("fare")) {
            JSONObject fare = firstLeg.optJSONObject("fare");
            if (fare != null) leg.setFareText(fare.optString("text", null));
        }

        // Transit extraction (rich)
        if ("transit".equalsIgnoreCase(mode)) {
            List<TransitSegment> segments = new ArrayList<>();

            if (stepsArray != null) {
                IntStream.range(0, stepsArray.length()).forEach(i -> {
                    JSONObject step = stepsArray.getJSONObject(i);
                    String travelMode = step.optString("travel_mode", "");
                    if ("TRANSIT".equalsIgnoreCase(travelMode)) {
                        JSONObject transit = step.optJSONObject("transit_details");
                        if (transit == null) return;

                        TransitSegment seg = new TransitSegment();

                        // departure / arrival stops and times
                        JSONObject depStop = transit.optJSONObject("departure_stop");
                        JSONObject arrStop = transit.optJSONObject("arrival_stop");
                        if (depStop != null) seg.setDepartureStop(depStop.optString("name", null));
                        if (arrStop != null) seg.setArrivalStop(arrStop.optString("name", null));

                        JSONObject depTime = transit.optJSONObject("departure_time");
                        JSONObject arrTime = transit.optJSONObject("arrival_time");
                        if (depTime != null) seg.setDepartureTimeText(depTime.optString("text", null));
                        if (arrTime != null) seg.setArrivalTimeText(arrTime.optString("text", null));

                        seg.setHeadsign(transit.optString("headsign", null));
                        seg.setNumStops(transit.optInt("num_stops", 0));

                        // line object
                        JSONObject line = transit.optJSONObject("line");
                        if (line != null) {
                            seg.setLineName(line.optString("name", null));
                            seg.setLineShortName(line.optString("short_name", null));

                            // trip short name (if available)
                            seg.setTripShortName(line.optString("trip_short_name", null));

                            // vehicle type
                            JSONObject vehicle = line.optJSONObject("vehicle");
                            if (vehicle != null) seg.setVehicle(vehicle.optString("type", null));

                            // agency
                            JSONArray agencies = line.optJSONArray("agencies");
                            if (agencies != null && agencies.length() > 0) {
                                JSONObject agency = agencies.getJSONObject(0);
                                seg.setAgencyName(agency.optString("name", null));
                            }

                            // sometimes line contains an internal stops[] array
                            JSONArray internalStops = line.optJSONArray("stops");
                            if (internalStops != null) {
                                List<String> stopNames = new ArrayList<>();
                                List<String> stopIds = new ArrayList<>();
                                IntStream.range(0, internalStops.length()).forEach(j -> {
                                    JSONObject s = internalStops.getJSONObject(j);
                                    stopNames.add(s.optString("name", null));
                                    if (s.has("stop_id")) stopIds.add(s.optString("stop_id", null));
                                });
                                seg.setStops(stopNames);
                                seg.setStopIds(stopIds);
                            }
                        }

                        // platform / stop_id often available in departure_stop/arrival_stop objects
                        if (depStop != null && depStop.has("stop_id")) {
                            // some providers put platform/stop id here
                            String stopId = depStop.optString("stop_id", null);
                            if (stopId != null) seg.getStopIds().add(0, stopId);
                        }
                        // step-level polyline (the transit step may have its own polyline)
                        JSONObject stepPolyline = step.optJSONObject("polyline");
                        if (stepPolyline != null) seg.setRawPolyline(stepPolyline.optString("points", null));

                        // Some transit_details contain a 'line' -> 'vehicle' -> 'local_icon' or 'vehicle' metadata,
                        // but platform number is not guaranteed. Some providers include it in text; we attempt to extract:
                        JSONObject departureStopDetail = depStop;
                        if (departureStopDetail != null) {
                            // e.g. "name": "Vaishali", "stop_id": "VASI"
                            // platform sometimes appears in Google UI but not always in API; try to read 'platform' if present
                            String platform = departureStopDetail.optString("platform", null);
                            if (platform == null) {
                                // look for 'platform' in transit object (rare)
                                platform = transit.optString("platform", null);
                            }
                            seg.setPlatform(platform);
                        }

                        segments.add(seg);
                    }
                });
            }

            leg.setTransitSegments(segments);
        }

        return leg;
    }
}
