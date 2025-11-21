package com.delhight.backend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single transit (metro/train) segment extracted from Google Directions transit_details.
 * This holds rich metadata we extract from the response so frontend can display platform, stops, etc.
 */
public class TransitSegment {
    private String lineName;
    private String lineShortName;
    private String vehicle;          // e.g. SUBWAY
    private String agencyName;       // e.g. DMRC
    private String tripShortName;    // GTFS trip short name if available
    private String departureStop;    // name
    private String arrivalStop;      // name
    private String departureTimeText;
    private String arrivalTimeText;
    private String headsign;
    private int numStops;
    private List<String> stops = new ArrayList<>();     // intermediate stops (names)
    private List<String> stopIds = new ArrayList<>();   // corresponding stop ids if provided
    private String platform;         // platform info if Google exposes it (sometimes in step details)
    private String rawPolyline;      // segment polyline (if available)

    public TransitSegment() {}

    public String getLineName() { return lineName; }
    public void setLineName(String lineName) { this.lineName = lineName; }

    public String getLineShortName() { return lineShortName; }
    public void setLineShortName(String lineShortName) { this.lineShortName = lineShortName; }

    public String getVehicle() { return vehicle; }
    public void setVehicle(String vehicle) { this.vehicle = vehicle; }

    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

    public String getTripShortName() { return tripShortName; }
    public void setTripShortName(String tripShortName) { this.tripShortName = tripShortName; }

    public String getDepartureStop() { return departureStop; }
    public void setDepartureStop(String departureStop) { this.departureStop = departureStop; }

    public String getArrivalStop() { return arrivalStop; }
    public void setArrivalStop(String arrivalStop) { this.arrivalStop = arrivalStop; }

    public String getDepartureTimeText() { return departureTimeText; }
    public void setDepartureTimeText(String departureTimeText) { this.departureTimeText = departureTimeText; }

    public String getArrivalTimeText() { return arrivalTimeText; }
    public void setArrivalTimeText(String arrivalTimeText) { this.arrivalTimeText = arrivalTimeText; }

    public String getHeadsign() { return headsign; }
    public void setHeadsign(String headsign) { this.headsign = headsign; }

    public int getNumStops() { return numStops; }
    public void setNumStops(int numStops) { this.numStops = numStops; }

    public List<String> getStops() { return stops; }
    public void setStops(List<String> stops) { this.stops = stops; }

    public List<String> getStopIds() { return stopIds; }
    public void setStopIds(List<String> stopIds) { this.stopIds = stopIds; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getRawPolyline() { return rawPolyline; }
    public void setRawPolyline(String rawPolyline) { this.rawPolyline = rawPolyline; }

    @Override
    public String toString() {
        return "TransitSegment{" +
                "lineName='" + lineName + '\'' +
                ", lineShortName='" + lineShortName + '\'' +
                ", vehicle='" + vehicle + '\'' +
                ", agencyName='" + agencyName + '\'' +
                ", tripShortName='" + tripShortName + '\'' +
                ", departureStop='" + departureStop + '\'' +
                ", arrivalStop='" + arrivalStop + '\'' +
                ", departureTimeText='" + departureTimeText + '\'' +
                ", arrivalTimeText='" + arrivalTimeText + '\'' +
                ", headsign='" + headsign + '\'' +
                ", numStops=" + numStops +
                ", stops=" + stops +
                ", stopIds=" + stopIds +
                ", platform='" + platform + '\'' +
                '}';
    }
}
