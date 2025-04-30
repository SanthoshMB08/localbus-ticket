package com.example.bmtc.models;

import java.util.List;

public class Bus {
    private String bus_id;
    private String vehicle_number;
    private String route;
    private int fare;
    private List<String> stops;

    public Bus(String bus_id, String vehicle_number, String route, int fare) {
        this.bus_id = bus_id;
        this.vehicle_number = vehicle_number;
        this.route = route;
        this.fare = fare;

    }

    public String getBusId() {
        return bus_id;
    }

    public String getVehicleNumber() {
        return vehicle_number;
    }


    public String getRoute() {
        return route;
    }
    public int getFare() {
        return fare;
    }
}

