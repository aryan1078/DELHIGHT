package com.delhight.backend.controller;

import com.delhight.backend.model.RouteLeg;
import com.delhight.backend.service.DirectionsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
public class DebugDirectionsController {

    private final DirectionsService directionsService;

    public DebugDirectionsController(DirectionsService directionsService) {
        this.directionsService = directionsService;
    }

    @GetMapping("/directions")
    public RouteLeg testDirections(
            @RequestParam double fromLat,
            @RequestParam double fromLng,
            @RequestParam double toLat,
            @RequestParam double toLng,
            @RequestParam(defaultValue = "walking") String mode
    ) {
        // departure_time = 0 means "leave now" (fine for walking/auto tests)
        return directionsService.getRouteLeg(
                fromLat, fromLng,
                toLat, toLng,
                mode,
                0,                 // no departure_time
                "Source",
                "Destination"
        );
    }
}
