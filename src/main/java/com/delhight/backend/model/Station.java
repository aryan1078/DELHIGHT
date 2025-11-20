package com.delhight.backend.model;

public class Station {
    private final String name;
    private final double lat;
    private final double lng;
    private double distanceMeters;  // computed dynamically

    public Station(String name, double lat, double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    // --- Getters ---
    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    // Used only after computing Haversine
    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
}
