package com.delhight.backend.dto;

import java.util.List;

/**
 * Full-detail DTO for a route variant.
 * Contains an ordered list of RouteLegDTO (piece1, piece2, piece3).
 */
public class RouteVariantDTO {
    private String id;
    private String summaryText;          // short human summary (Walk → METRO (Blue Line) → Auto)
    private long totalDurationSeconds;
    private double totalDistanceMeters;
    private double totalCostRs;

    // ordered legs (piece1, piece2, piece3). Each leg contains detailed steps.
    private List<RouteLegDTO> legs;

    public RouteVariantDTO() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    public long getTotalDurationSeconds() { return totalDurationSeconds; }
    public void setTotalDurationSeconds(long totalDurationSeconds) { this.totalDurationSeconds = totalDurationSeconds; }

    public double getTotalDistanceMeters() { return totalDistanceMeters; }
    public void setTotalDistanceMeters(double totalDistanceMeters) { this.totalDistanceMeters = totalDistanceMeters; }

    public double getTotalCostRs() { return totalCostRs; }
    public void setTotalCostRs(double totalCostRs) { this.totalCostRs = totalCostRs; }

    public List<RouteLegDTO> getLegs() { return legs; }
    public void setLegs(List<RouteLegDTO> legs) { this.legs = legs; }
}
