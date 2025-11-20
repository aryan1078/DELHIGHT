package com.delhight.backend.service;

import com.delhight.backend.model.Station;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GooglePlacesService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Uses Google Nearby Search to find the nearest metro stations.
     * Google sorts these automatically by distance.
     */
    public List<Station> findNearestStations(double lat, double lng, int limit) {

        String url =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                        + "?location=" + lat + "," + lng
                        + "&radius=5000"
                        + "&type=subway_station"
                        + "&key=" + apiKey;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("results")) {
            return List.of();
        }

        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        List<Station> stations = new ArrayList<>();

        for (int i = 0; i < Math.min(results.size(), limit); i++) {

            Map<String, Object> result = results.get(i);

            String name = (String) result.get("name");

            Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
            Map<String, Object> location = (Map<String, Object>) geometry.get("location");

            double slat = ((Number) location.get("lat")).doubleValue();
            double slng = ((Number) location.get("lng")).doubleValue();

            stations.add(new Station(name, slat, slng));
        }

        return stations;
    }
}
