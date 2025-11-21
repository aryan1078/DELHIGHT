package com.delhight.backend.controller;

import com.delhight.backend.dto.RouteVariantDTO;
import com.delhight.backend.mapper.RouteMapper;
import com.delhight.backend.model.RouteVariant;
import com.delhight.backend.service.RouteComputationService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main endpoint:
 *
 *   GET /routes?from=Connaught+Place&to=Indirapuram
 *
 * Returns:
 *   - cleaned DTOs only (frontend-safe)
 *   - detailed steps for walking/auto/metro
 *   - summary + cost + duration
 */
@RestController
@RequestMapping("/routes")
@CrossOrigin(origins = "*")
public class RoutesController {

    private final RouteComputationService routeComputationService;
    private final RouteMapper routeMapper;

    public RoutesController(RouteComputationService routeComputationService,
                            RouteMapper routeMapper) {
        this.routeComputationService = routeComputationService;
        this.routeMapper = routeMapper;
    }

    /**
     * Example:
     *   /routes?from=Connaught+Place&to=Shakti+Khand+4+Indirapuram
     *
     * Steps:
     *   1. geocode both addresses
     *   2. locate top 3 metro stations for each
     *   3. compute 9 permutations
     *   4. generate walk/auto variants
     *   5. sort & return top variants
     */
    @GetMapping
    public Map<String, Object> getRoutes(
            @RequestParam("from") String from,
            @RequestParam("to") String to
    ) {

        Map<String, Object> response = new HashMap<>();

        if (from == null || from.isBlank() ||
                to == null || to.isBlank()) {

            response.put("status", "error");
            response.put("message", "Both 'from' and 'to' parameters are required.");
            return response;
        }

        // Compute internal variants (with full RouteLeg models)
        List<RouteVariant> internalVariants =
                routeComputationService.computeRoutes(from, to);

        // Convert to DTOs for frontend (removes internal fields)
        List<RouteVariantDTO> dtoVariants =
                routeMapper.toDTOList(internalVariants);

        response.put("status", "ok");
        response.put("from", from);
        response.put("to", to);
        response.put("totalVariants", dtoVariants.size());
        response.put("routes", dtoVariants);

        return response;
    }
}
