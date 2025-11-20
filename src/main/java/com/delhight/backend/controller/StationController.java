package com.delhight.backend.controller;

import com.delhight.backend.model.Station;
import com.delhight.backend.service.GooglePlacesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stations")
@CrossOrigin(origins = "*")
public class StationController {

    private final GooglePlacesService googlePlacesService;

    public StationController(GooglePlacesService googlePlacesService) {
        this.googlePlacesService = googlePlacesService;
    }

    @GetMapping("/nearest")
    public List<Station> getNearestStations(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "3") int limit
    ) {
        return googlePlacesService.findNearestStations(lat, lng, limit);
    }
}
