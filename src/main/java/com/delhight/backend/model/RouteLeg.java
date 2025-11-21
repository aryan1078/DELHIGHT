package com.delhight.backend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single origin->destination leg returned by a Directions provider.
 * A RouteLeg contains high-level metadata (distance, duration, polyline),
 * a list of textual steps (for list-view), optional fare text and zero-or-more transit segments.
 */
public class RouteLeg {
    private String id;
    private String mode; // walking / driving / transit
    private String fromName;
    private String toName;
    private double fromLat;
    private double fromLng;
    private double toLat;
    private double toLng;
    private int distanceMeters;
    private int durationSeconds;
    private String polyline;
    private List<String> steps = new ArrayList<>();
    private String fareText;
    private List<TransitSegment> transitSegments = new ArrayList<>();

    public RouteLeg() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }

    public String getToName() { return toName; }
    public void setToName(String toName) { this.toName = toName; }

    public double getFromLat() { return fromLat; }
    public void setFromLat(double fromLat) { this.fromLat = fromLat; }

    public double getFromLng() { return fromLng; }
    public void setFromLng(double fromLng) { this.fromLng = fromLng; }

    public double getToLat() { return toLat; }
    public void setToLat(double toLat) { this.toLat = toLat; }

    public double getToLng() { return toLng; }
    public void setToLng(double toLng) { this.toLng = toLng; }

    public int getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(int distanceMeters) { this.distanceMeters = distanceMeters; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getPolyline() { return polyline; }
    public void setPolyline(String polyline) { this.polyline = polyline; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public String getFareText() { return fareText; }
    public void setFareText(String fareText) { this.fareText = fareText; }

    public List<TransitSegment> getTransitSegments() { return transitSegments; }
    public void setTransitSegments(List<TransitSegment> transitSegments) { this.transitSegments = transitSegments; }

    @Override
    public String toString() {
        return "RouteLeg{" +
                "mode='" + mode + '\'' +
                ", fromName='" + fromName + '\'' +
                ", toName='" + toName + '\'' +
                ", distanceMeters=" + distanceMeters +
                ", durationSeconds=" + durationSeconds +
                ", fareText=" + fareText +
                ", transitSegments=" + transitSegments +
                '}';
    }
}
