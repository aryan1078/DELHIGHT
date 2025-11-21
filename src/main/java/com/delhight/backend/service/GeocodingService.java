package com.delhight.backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Simple Geocoding service using Google Geocoding API.
 * Returns the first result's lat/lng for a given address string.
 */
@Service
public class GeocodingService {

    private final WebClient webClient;

    @Value("${google.api.key:}")
    private String googleApiKey;

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public GeocodingService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * Geocode an address. Returns a double array [lat, lng] or null if not found.
     */
    public double[] geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) return null;

        String encoded = address.trim().replace(" ", "+");
        String url = GEOCODE_URL + "?address=" + encoded + "&key=" + googleApiKey;

        Mono<String> mono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);

        String body;
        try {
            body = mono.block();
        } catch (Exception e) {
            return null;
        }

        if (body == null || body.isEmpty()) return null;

        JSONObject root = new JSONObject(body);
        String status = root.optString("status", "");
        if (!"OK".equalsIgnoreCase(status)) return null;

        JSONArray results = root.optJSONArray("results");
        if (results == null || results.length() == 0) return null;

        JSONObject first = results.getJSONObject(0);
        JSONObject geom = first.optJSONObject("geometry");
        if (geom == null) return null;
        JSONObject loc = geom.optJSONObject("location");
        if (loc == null) return null;

        double lat = loc.optDouble("lat", Double.NaN);
        double lng = loc.optDouble("lng", Double.NaN);

        if (Double.isNaN(lat) || Double.isNaN(lng)) return null;
        return new double[]{lat, lng};
    }
}
