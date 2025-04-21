package com.example.bmtc.network;

import com.example.bmtc.models.AllStopsResponse;
import com.example.bmtc.models.Bus;
import com.example.bmtc.models.BusRequest;
import com.example.bmtc.models.StopResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;

public interface ApiService {
    @POST("/get_bus")
    Call<Bus> getBusDetails(@Body BusRequest busRequest);
    @GET("/get_buses") // For fetching buses based on origin & destination
    Call<List<Bus>> getBuses(@Query("origin") String origin, @Query("destination") String destination);

    @GET("/get_bus_stops")
    Call<List<String>> getBusStops(@Query("bus_id") String busId);
    @GET("/get_all_stops")
    Call<AllStopsResponse> getAllStops();
    @GET("/get_reachable_stops")
    Call<StopResponse> getReachableStops(@Query("origin") String origin);

}

