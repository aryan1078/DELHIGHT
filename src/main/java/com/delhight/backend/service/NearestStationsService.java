package com.delhight.backend.service;

import com.delhight.backend.model.NearbyStation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Simple Places-based nearest-station finder.
 *
 * - Calls Google Places Nearby Search with type=subway_station.
 * - Parses geometry.location from results and computes haversine distance.
 * - Returns top N (default 3) closest stations by straight-line distance.
 *
 * Note: uses straight-line distance (haversine) as requested (Option A).
 */
@Service
public class NearestStationsService {

    private final WebClient webClient;

    @Value("${google.api.key:}")
    private String googleApiKey;

    private static final String PLACES_NEARBY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    public NearestStationsService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Finds nearest subway stations to the given lat/lng.
     *
     * @param lat center latitude
     * @param lng center longitude
     * @param top how many results to return (e.g., 3)
     */
    public List<NearbyStation> findNearestStations(double lat, double lng, int top) {
        // radius large enough to cover whole city (we'll rely on sorting); adjust if needed
        int radiusMeters = 15000;

        String location = lat + "," + lng;
        String url = PLACES_NEARBY_URL
                + "?location=" + location
                + "&radius=" + radiusMeters
                + "&type=subway_station"
                + "&key=" + googleApiKey;

        Mono<String> mono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);

        String body;
        try {
            body = mono.block();
        } catch (Exception ex) {
            return new ArrayList<>();
        }

        if (body == null || body.isEmpty()) return new ArrayList<>();

        JSONObject root = new JSONObject(body);
        String status = root.optString("status", "");
        if (!"OK".equalsIgnoreCase(status) && !"ZERO_RESULTS".equalsIgnoreCase(status)) {
            // in case of OVER_QUERY_LIMIT or other statuses, return empty
            return new ArrayList<>();
        }

        JSONArray results = root.optJSONArray("results");
        if (results == null || results.length() == 0) return new ArrayList<>();

        List<NearbyStation> stations = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject r = results.getJSONObject(i);
            String name = r.optString("name", null);
            String placeId = r.optString("place_id", null);
            String vicinity = r.optString("vicinity", null);
            JSONObject geom = r.optJSONObject("geometry");
            if (geom == null) continue;
            JSONObject loc = geom.optJSONObject("location");
            if (loc == null) continue;
            double slat = loc.optDouble("lat", Double.NaN);
            double slng = loc.optDouble("lng", Double.NaN);
            if (Double.isNaN(slat) || Double.isNaN(slng)) continue;

            double dist = haversineMeters(lat, lng, slat, slng);

            NearbyStation s = new NearbyStation();
            s.setName(name);
            s.setPlaceId(placeId);
            s.setVicinity(vicinity);
            s.setLat(slat);
            s.setLng(slng);
            s.setDistanceMeters((int)Math.round(dist));
            stations.add(s);
        }

        // sort by straight-line distance
        stations.sort(Comparator.comparingInt(NearbyStation::getDistanceMeters));

        if (stations.size() <= top) return stations;
        return stations.subList(0, top);
    }

    // Haversine formula -> meters
    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
