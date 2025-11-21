package com.delhight.backend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A RouteVariant represents ONE entire journey option:
 * - piece1: walk/drive from source → nearest station
 * - piece2: transit route between stations
 * - piece3: walk/drive from final station → destination
 *
 * Also contains summary + totals for sorting & ranking.
 */
public class RouteVariant {

    // ----------------------------
    // Core 3-Piece Structure
    // ----------------------------
    private RouteLeg piece1;   // Origin → SourceStation
    private RouteLeg piece2;   // SourceStation → DestinationStation (typically transit)
    private RouteLeg piece3;   // DestinationStation → Final Destination

    // ----------------------------
    // Additional Metadata
    // ----------------------------
    private String id;                      // unique id
    private long totalDurationSeconds;      // total journey time
    private double totalDistanceMeters;     // total journey distance
    private double totalCostRs;             // (optional) total fare
    private String summaryText;             // human-readable summary

    // Legacy compatibility: store all legs together
    private List<RouteLeg> legs = new ArrayList<>();


    public RouteVariant() {}

    // ----------------------------
    // Piece 1 / 2 / 3 getters-setters
    // ----------------------------

    public RouteLeg getPiece1() {
        return piece1;
    }

    public void setPiece1(RouteLeg piece1) {
        this.piece1 = piece1;
        if (piece1 != null) legs.add(piece1);
    }

    public RouteLeg getPiece2() {
        return piece2;
    }

    public void setPiece2(RouteLeg piece2) {
        this.piece2 = piece2;
        if (piece2 != null) legs.add(piece2);
    }

    public RouteLeg getPiece3() {
        return piece3;
    }

    public void setPiece3(RouteLeg piece3) {
        this.piece3 = piece3;
        if (piece3 != null) legs.add(piece3);
    }


    // ----------------------------
    // Total cost/distance/time
    // ----------------------------

    public long getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public void setTotalDurationSeconds(long totalDurationSeconds) {
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public double getTotalDistanceMeters() {
        return totalDistanceMeters;
    }

    public void setTotalDistanceMeters(double totalDistanceMeters) {
        this.totalDistanceMeters = totalDistanceMeters;
    }

    public double getTotalCostRs() {
        return totalCostRs;
    }

    public void setTotalCostRs(double totalCostRs) {
        this.totalCostRs = totalCostRs;
    }

    public void setTotalCost(double cost) {
        this.totalCostRs = cost;
    }

    public double getTotalCost() {
        return totalCostRs;
    }


    // ----------------------------
    // Other metadata
    // ----------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RouteLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<RouteLeg> legs) {
        this.legs = legs;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }


    // ----------------------------
    // Utility method for logging/debug
    // ----------------------------
    @Override
    public String toString() {
        return "RouteVariant{" +
                "id='" + id + '\'' +
                ", duration=" + totalDurationSeconds +
                ", distance=" + totalDistanceMeters +
                ", cost=" + totalCostRs +
                ", piece1=" + (piece1 != null ? piece1.getMode() : "null") +
                ", piece2=" + (piece2 != null ? piece2.getMode() : "null") +
                ", piece3=" + (piece3 != null ? piece3.getMode() : "null") +
                '}';
    }
}
