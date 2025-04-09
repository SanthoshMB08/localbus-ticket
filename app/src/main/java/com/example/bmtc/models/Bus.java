package com.example.bmtc.models;

import java.util.List;

public class Bus {
    private String busId;
    private String vehicleNumber;
    private String route;
    private int fare;
    private List<String> stops;

    public Bus(String busId, String vehicleNumber, String route, int fare, List<String> stops) {
        this.busId = busId;
        this.vehicleNumber = vehicleNumber;
        this.route = route;
        this.fare = fare;
        this.stops = stops;
    }

    public String getBusId() {
        return busId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
    public List<String> getStops() {
        return stops;
    }

    public String getRoute() {
        return route;
    }
    public int getFare() {
        return fare;
    }
}

