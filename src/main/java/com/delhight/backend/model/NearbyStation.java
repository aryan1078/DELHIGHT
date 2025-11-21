package com.delhight.backend.model;

/**
 * Simple DTO for nearby station results.
 */
public class NearbyStation {
    private String name;
    private String placeId;
    private String vicinity;
    private double lat;
    private double lng;
    private int distanceMeters;

    public NearbyStation() {}

    public String getName() {
        return name;
    }

    public NearbyStation setName(String name) {
        this.name = name;
        return this;
    }

    public String getPlaceId() {
        return placeId;
    }

    public NearbyStation setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public String getVicinity() {
        return vicinity;
    }

    public NearbyStation setVicinity(String vicinity) {
        this.vicinity = vicinity;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public NearbyStation setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public NearbyStation setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public int getDistanceMeters() {
        return distanceMeters;
    }

    public NearbyStation setDistanceMeters(int distanceMeters) {
        this.distanceMeters = distanceMeters;
        return this;
    }

    @Override
    public String toString() {
        return "NearbyStation{" +
                "name='" + name + '\'' +
                ", placeId='" + placeId + '\'' +
                ", vicinity='" + vicinity + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", distanceMeters=" + distanceMeters +
                '}';
    }
}
