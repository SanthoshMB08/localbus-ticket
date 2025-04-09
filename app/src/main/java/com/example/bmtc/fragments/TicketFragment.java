package com.example.bmtc.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bmtc.R;

public class TicketFragment extends Fragment {

    private TextView ticketDetailsText;

    public TicketFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        ticketDetailsText = view.findViewById(R.id.ticketDetailsText);
        Button clearTicketButton = view.findViewById(R.id.clearTicketButton);


        // Load and display last saved ticket
        loadTicket();
        // Clear ticket when the button is clicked
        clearTicketButton.setOnClickListener(v -> clearTicket());

        return view;
    }

    // Load the last ticket from SharedPreferences
    private void loadTicket() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TicketData", Context.MODE_PRIVATE);

        String busNumber = sharedPreferences.getString("busNumber", "N/A");
        String vehicleNumber = sharedPreferences.getString("vehicleNumber", "N/A");
        String startStop = sharedPreferences.getString("startStop", "N/A");
        String endStop = sharedPreferences.getString("endStop", "N/A");
        String timestamp = sharedPreferences.getString("timestamp", "N/A");
        int fare = sharedPreferences.getInt("fare", 0); // ✅ Fetch as Integer
        String fareString = String.valueOf(fare); // ✅ Convert to String if needed
        if (busNumber.equals("N/A")) {
            ticketDetailsText.setText(getString(R.string.no_ticket_available));
        } else {
            String ticketDetails = getString(R.string.ticket_details_format, busNumber, vehicleNumber, startStop, endStop, fare, timestamp);
            ticketDetailsText.setText(ticketDetails);
        }
    }

    // Clear the saved ticket
    private void clearTicket() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TicketData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();  // Clears all stored ticket data
        editor.apply();

        ticketDetailsText.setText(getString(R.string.no_ticket_available));
    }
}
