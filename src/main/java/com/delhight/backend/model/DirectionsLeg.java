package com.delhight.backend.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DirectionsLeg
 *
 * POJO holding the extracted result of a single directions request (walking/driving/transit).
 * No Lombok used so it compiles without extra dependencies.
 */
public class DirectionsLeg implements Serializable {

    private String mode;                 // walking | driving | transit
    private String polyline;             // overview polyline
    private int distanceMeters;
    private int durationSeconds;
    private List<String> steps = new ArrayList<>();
    private List<TransitSegment> transitSegments = new ArrayList<>();
    private String error;                // optional error message

    public DirectionsLeg() { }

    // Getters & Setters
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(int distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public List<TransitSegment> getTransitSegments() {
        return transitSegments;
    }

    public void setTransitSegments(List<TransitSegment> transitSegments) {
        this.transitSegments = transitSegments;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // convenience helpers
    public boolean hasError() {
        return this.error != null && !this.error.isEmpty();
    }

    @Override
    public String toString() {
        return "DirectionsLeg{" +
                "mode='" + mode + '\'' +
                ", polyline='" + (polyline != null ? polyline.substring(0, Math.min(40, polyline.length())) + "..." : null) + '\'' +
                ", distanceMeters=" + distanceMeters +
                ", durationSeconds=" + durationSeconds +
                ", stepsCount=" + (steps != null ? steps.size() : 0) +
                ", transitSegments=" + (transitSegments != null ? transitSegments.size() : 0) +
                ", error='" + error + '\'' +
                '}';
    }
}
