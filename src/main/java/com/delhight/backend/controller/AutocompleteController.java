package com.delhight.backend.controller;

import com.delhight.backend.service.AutocompleteService;
import com.delhight.backend.service.AutocompleteService.Prediction;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/autocomplete")
@CrossOrigin(origins = "*")
public class AutocompleteController {

    private final AutocompleteService autocompleteService;

    public AutocompleteController(AutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @GetMapping
    public Map<String, Object> autocomplete(@RequestParam("q") String q) {

        Map<String, Object> response = new HashMap<>();

        if (q == null || q.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Query 'q' is required.");
            return response;
        }

        List<Prediction> predictions = autocompleteService.autocomplete(q);

        response.put("status", "ok");
        response.put("count", predictions.size());
        response.put("predictions", predictions);

        return response;
    }
}
