package com.delhight.backend.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Backend wrapper for Google Places Autocomplete API.
 * Protects API key and returns a simplified set of predictions.
 */
@Service
public class AutocompleteService {

    private final WebClient webClient;

    @Value("${google.api.key:}")
    private String googleApiKey;

    private static final String AUTOCOMPLETE_URL =
            "https://maps.googleapis.com/maps/api/place/autocomplete/json";

    public AutocompleteService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public List<Prediction> autocomplete(String query) {
        List<Prediction> result = new ArrayList<>();

        if (query == null || query.isBlank()) return result;

        String encoded = query.trim().replace(" ", "+");

        String url = AUTOCOMPLETE_URL
                + "?input=" + encoded
                + "&components=country:in"
                + "&types=geocode"
                + "&key=" + googleApiKey;

        Mono<String> mono =
                webClient.get().uri(url).retrieve().bodyToMono(String.class);

        String body;
        try {
            body = mono.block();
        } catch (Exception e) {
            return result;
        }

        if (body == null || body.isEmpty()) return result;

        JSONObject root = new JSONObject(body);

        if (!"OK".equalsIgnoreCase(root.optString("status"))) {
            return result;
        }

        JSONArray preds = root.optJSONArray("predictions");
        if (preds == null) return result;

        for (int i = 0; i < preds.length(); i++) {
            JSONObject p = preds.getJSONObject(i);

            Prediction prediction = new Prediction();
            prediction.setDescription(p.optString("description", ""));
            prediction.setPlaceId(p.optString("place_id", ""));
            prediction.setMainText(
                    p.optJSONObject("structured_formatting")
                            .optString("main_text", "")
            );
            prediction.setSecondaryText(
                    p.optJSONObject("structured_formatting")
                            .optString("secondary_text", "")
            );

            result.add(prediction);
        }

        return result;
    }

    // ---- inner DTO class ----

    public static class Prediction {
        private String description;
        private String placeId;
        private String mainText;
        private String secondaryText;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getPlaceId() { return placeId; }
        public void setPlaceId(String placeId) { this.placeId = placeId; }

        public String getMainText() { return mainText; }
        public void setMainText(String mainText) { this.mainText = mainText; }

        public String getSecondaryText() { return secondaryText; }
        public void setSecondaryText(String secondaryText) { this.secondaryText = secondaryText; }
    }
}
