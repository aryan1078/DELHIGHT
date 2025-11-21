package com.delhight.backend.dto;

import java.util.List;

/**
 * Detailed DTO for a single leg of a route.
 * Designed to carry full step-by-step instructions exactly as returned by Google.
 */
public class RouteLegDTO {
    private String mode;              // walking / driving / transit
    private String fromName;
    private String toName;

    private long durationSeconds;
    private long distanceMeters;

    private String polyline;          // overview polyline for this leg (encoded)

    // Full step-by-step instructions from Google (HTML stripped)
    // We keep them as strings like "Head north on X (50 m)" — this matches your Directions service output.
    private List<String> steps;

    // For transit legs: fareText (if available), and raw transit segments
    private String fareText;
    private List<TransitSegmentDTO> transitSegments;

    public RouteLegDTO() {}

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }

    public String getToName() { return toName; }
    public void setToName(String toName) { this.toName = toName; }

    public long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }

    public long getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(long distanceMeters) { this.distanceMeters = distanceMeters; }

    public String getPolyline() { return polyline; }
    public void setPolyline(String polyline) { this.polyline = polyline; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public String getFareText() { return fareText; }
    public void setFareText(String fareText) { this.fareText = fareText; }

    public List<TransitSegmentDTO> getTransitSegments() { return transitSegments; }
    public void setTransitSegments(List<TransitSegmentDTO> transitSegments) { this.transitSegments = transitSegments; }
}
