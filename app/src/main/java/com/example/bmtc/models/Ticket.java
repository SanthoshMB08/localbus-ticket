package com.example.bmtc.models;

public class Ticket {
    private String busNumber;
    private String vehicleNumber;
    private String startStop;
    private String endStop;
    private int fare;
    private String dateTime;

    public Ticket(String busNumber, String vehicleNumber, String startStop, String endStop, int fare, String dateTime) {
        this.busNumber = busNumber;
        this.vehicleNumber = vehicleNumber;
        this.startStop = startStop;
        this.endStop = endStop;
        this.fare = fare;
        this.dateTime = dateTime;
    }

    public String getBusNumber() { return busNumber; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getStartStop() { return startStop; }
    public String getEndStop() { return endStop; }
    public int getFare() { return fare; }
    public String getDateTime() { return dateTime; }
}
