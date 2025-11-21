package com.delhight.backend.controller;

import com.delhight.backend.model.NearbyStation;
import com.delhight.backend.service.GeocodingService;
import com.delhight.backend.service.NearestStationsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Debug / lightweight controller to expose nearest stations and geocoding endpoints.
 *
 * Endpoints:
 *  - GET /api/geocode?address=...
 *  - GET /api/nearest-stations?lat=...&lng=...&top=3
 *  - GET /api/nearest-stations-by-text?address=...&top=3
 */
@RestController
public class NearestStationsController {

    private final GeocodingService geocodingService;
    private final NearestStationsService nearestStationsService;

    public NearestStationsController(GeocodingService geocodingService,
                                     NearestStationsService nearestStationsService) {
        this.geocodingService = geocodingService;
        this.nearestStationsService = nearestStationsService;
    }

    @GetMapping("/api/geocode")
    public double[] geocode(@RequestParam String address) {
        return geocodingService.geocodeAddress(address);
    }

    @GetMapping("/api/nearest-stations")
    public List<NearbyStation> nearestByLatLng(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false, defaultValue = "3") int top) {
        return nearestStationsService.findNearestStations(lat, lng, top);
    }

    @GetMapping("/api/nearest-stations-by-text")
    public List<NearbyStation> nearestByText(
            @RequestParam String address,
            @RequestParam(required = false, defaultValue = "3") int top) {

        double[] coords = geocodingService.geocodeAddress(address);
        if (coords == null) return List.of();
        return nearestStationsService.findNearestStations(coords[0], coords[1], top);
    }
}
