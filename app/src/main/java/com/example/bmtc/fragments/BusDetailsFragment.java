package com.example.bmtc.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bmtc.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import com.example.bmtc.fragments.PaymentFragment; // Adjust the package path if necessary

public class BusDetailsFragment extends Fragment {

    private Spinner startStopSpinner, endStopSpinner, ticketCountSpinner;
    private TextView routeDisplay;
    private String busId, vehicleNumber;
    private List<String> busStops = new ArrayList<>();
    private int baseFare = 6;

    public BusDetailsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startStopSpinner = view.findViewById(R.id.startStopSpinner);
        endStopSpinner = view.findViewById(R.id.endStopSpinner);
        ticketCountSpinner = view.findViewById(R.id.ticketCountSpinner);
        routeDisplay = view.findViewById(R.id.routeDisplay);
        Button calculateFareButton = view.findViewById(R.id.calculateFareButton);

        // Retrieve bus ID and vehicle number from previous fragment
        if (getArguments() != null) {
            busId = getArguments().getString("bus_id", "");
            vehicleNumber = getArguments().getString("vehicle_number", "");
            fetchBusStops(busId);
        }

        // Populate ticket count options (1-10)
        List<String> ticketCounts = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ticketCounts.add(String.valueOf(i));
        }
        ArrayAdapter<String> ticketAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ticketCounts);
        ticketAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketCountSpinner.setAdapter(ticketAdapter);

        // Calculate fare and navigate to payment fragment
        calculateFareButton.setOnClickListener(v -> {
            String startStop = startStopSpinner.getSelectedItem().toString();
            String endStop = endStopSpinner.getSelectedItem().toString();
            int ticketCount = Integer.parseInt(ticketCountSpinner.getSelectedItem().toString());

            if (startStop.equals(endStop)) {
                Toast.makeText(getContext(), "Start and End stops cannot be the same!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate fare
            int fare = calculateFare(busStops.indexOf(startStop), busStops.indexOf(endStop), ticketCount);
// Create a new instance of PaymentFragment
            Bundle bundle = new Bundle();
            bundle.putString("bus_id", busId);
            bundle.putString("vehicle_number", vehicleNumber);
            bundle.putString("start_stop", startStop);
            bundle.putString("end_stop", endStop);
            bundle.putInt("fare", fare);  // Ensure correct data type

            PaymentFragment paymentFragment = new PaymentFragment();
            paymentFragment.setArguments(bundle);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, paymentFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });
    }

        private void fetchBusStops(String busId) {
        String url = "http://192.168.146.231:5000/get_bus_stops?bus_id=" + busId;// Replace with your Flask server IP

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        @SuppressLint("SetTextI18n") JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray stopsArray = response.getJSONArray("stops");
                        busStops.clear();
                        // Add a placeholder as the first item
                        busStops.add("Select Start Stop");

                        for (int i = 0; i < stopsArray.length(); i++) {
                            busStops.add(stopsArray.getString(i));
                        }

                        // Update UI with stops
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, busStops);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        startStopSpinner.setAdapter(adapter);
                        endStopSpinner.setAdapter(adapter);

                        // Display the route in a simple text format
                        routeDisplay.setText("Route: " + String.join(" → ", busStops));

                    } catch (JSONException e) {
                        Log.e("JSONError", "JSON Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.toString());
                    Toast.makeText(getContext(), "Failed to fetch stops!", Toast.LENGTH_SHORT).show();
                });
        queue.add(request);
    }

    private int calculateFare(int startIndex, int endIndex, int ticketCount) {
        // Ensure the difference is always positive
        int stopDifference = Math.abs(endIndex - startIndex);

        int fare;
        if (stopDifference <= 2) {
            fare = 6;  // First 2 stops cost ₹6
        } else {
            fare = 6 + ((stopDifference - 2) * 6);  // Every extra stop beyond 2 adds ₹6
        }

        return fare * ticketCount; // Multiply by the number of tickets
    }
}

