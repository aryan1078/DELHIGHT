package com.delhight.backend.mapper;

import com.delhight.backend.dto.RouteLegDTO;
import com.delhight.backend.dto.RouteVariantDTO;
import com.delhight.backend.dto.TransitSegmentDTO;
import com.delhight.backend.model.RouteLeg;
import com.delhight.backend.model.RouteVariant;
import com.delhight.backend.model.TransitSegment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps internal models (RouteVariant, RouteLeg, TransitSegment) to detailed DTOs
 * for frontend integration. This mapper preserves full step-level instructions and
 * transit segment details.
 */
@Component
public class RouteMapper {

    public List<RouteVariantDTO> toDTOList(List<RouteVariant> variants) {
        List<RouteVariantDTO> out = new ArrayList<>();
        if (variants == null) return out;
        for (RouteVariant v : variants) out.add(toDTO(v));
        return out;
    }

    public RouteVariantDTO toDTO(RouteVariant v) {
        RouteVariantDTO dto = new RouteVariantDTO();
        dto.setId(v.getId());
        dto.setSummaryText(v.getSummaryText());
        dto.setTotalDurationSeconds(v.getTotalDurationSeconds());
        dto.setTotalDistanceMeters(v.getTotalDistanceMeters());
        dto.setTotalCostRs(v.getTotalCostRs());

        List<RouteLegDTO> legs = new ArrayList<>();
        if (v.getLegs() != null) {
            for (RouteLeg leg : v.getLegs()) {
                legs.add(toLegDTO(leg));
            }
        }
        dto.setLegs(legs);
        return dto;
    }

    private RouteLegDTO toLegDTO(RouteLeg leg) {
        RouteLegDTO dto = new RouteLegDTO();
        if (leg == null) return dto;

        dto.setMode(leg.getMode());
        dto.setFromName(leg.getFromName());
        dto.setToName(leg.getToName());
        dto.setDurationSeconds(leg.getDurationSeconds());
        dto.setDistanceMeters(leg.getDistanceMeters());
        dto.setPolyline(leg.getPolyline());

        // Steps: we expect your DirectionsService already produced cleaned strings
        dto.setSteps(leg.getSteps());

        // Fare text
        dto.setFareText(leg.getFareText());

        // Transit segments -> TransitSegmentDTO
        if (leg.getTransitSegments() != null) {
            List<TransitSegmentDTO> segs = new ArrayList<>();
            for (TransitSegment s : leg.getTransitSegments()) {
                TransitSegmentDTO sd = new TransitSegmentDTO();
                sd.setLineName(s.getLineName());
                // your TransitSegment model used getVehicle() earlier; map to vehicle field
                sd.setVehicle(s.getVehicle());
                sd.setDepartureStop(s.getDepartureStop());
                sd.setArrivalStop(s.getArrivalStop());
                sd.setHeadsign(s.getHeadsign());
                sd.setNumStops(s.getNumStops());
                segs.add(sd);
            }
            dto.setTransitSegments(segs);
        }

        return dto;
    }
}
