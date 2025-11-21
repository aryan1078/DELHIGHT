package com.delhight.backend.utils;

import com.delhight.backend.model.TransitSegment;

public class TransitInstructionsFormatter {

    /**
     * Create a human-readable description of a transit segment:
     * - Line name & short name
     * - Vehicle type
     * - Platform (if available)
     * - Departure & arrival stops
     * - Head-sign (direction)
     * - Number of stops
     * - Timing info
     */
    public static String format(TransitSegment seg) {
        if (seg == null) return "No transit information available.";

        StringBuilder sb = new StringBuilder();

        // Line info
        sb.append("Take ")
                .append(formatLine(seg))
                .append(" from ")
                .append(formatStop(seg.getDepartureStop()));

        // Platform (optional)
        if (seg.getPlatform() != null && !seg.getPlatform().isEmpty()) {
            sb.append(" (Platform ").append(seg.getPlatform()).append(")");
        }

        sb.append(". ");

        // Head-sign (direction)
        if (seg.getHeadsign() != null) {
            sb.append("Direction: ").append(seg.getHeadsign()).append(". ");
        }

        // Timing
        if (seg.getDepartureTimeText() != null) {
            sb.append("Departs at ").append(seg.getDepartureTimeText()).append(". ");
        }

        // Duration / stops
        sb.append("Ride for ").append(seg.getNumStops()).append(" stops");

        if (seg.getArrivalTimeText() != null) {
            sb.append(" (arrives at ").append(seg.getArrivalTimeText()).append(")");
        }

        sb.append(". ");

        // Arrival stop
        sb.append("Get down at ").append(formatStop(seg.getArrivalStop())).append(".");

        return sb.toString().trim();
    }

    /** Helper for formatting line names */
    private static String formatLine(TransitSegment seg) {
        String shortName = seg.getLineShortName();
        String fullName = seg.getLineName();

        if (shortName != null && fullName != null) {
            return fullName + " (" + shortName + ")";
        }
        if (fullName != null) return fullName;
        if (shortName != null) return "Line " + shortName;

        return "the metro line";
    }

    /** Ensures stop names look nice */
    private static String formatStop(String stop) {
        return stop != null ? stop : "the station";
    }
}
