package com.example.bmtc.models;
import java.util.List;
public class StopResponse {
    private List<String> reachable;

    public List<String> getReachable() {
        return reachable;
    }

    public void setReachable(List<String> reachable) {
        this.reachable = reachable;
    }
}