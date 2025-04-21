package com.example.bmtc.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bmtc.R;
import com.example.bmtc.adapters.TicketAdapter;
import com.example.bmtc.models.Ticket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PastTicketsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private List<Ticket> ticketList;

    public PastTicketsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_past_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewTickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load tickets from SharedPreferences
        ticketList = loadTickets();
        adapter = new TicketAdapter(ticketList, ticket -> showTicketDialog(ticket));

        recyclerView.setAdapter(adapter);
    }

    // Function to load tickets from SharedPreferences
    private List<Ticket> loadTickets() {
        Context context = getContext();
        if (context == null) {
            Log.e("PastTickets", "⚠️ Context is null while loading tickets");
            return new ArrayList<>();
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("TicketPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("past_tickets", null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Ticket>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }
    private void showTicketDialog(Ticket ticket) {
        StringBuilder message = new StringBuilder();

        message.append("Start Stop: ").append(ticket.getStartStop()).append("\n");
        message.append("End Stop: ").append(ticket.getEndStop()).append("\n");
        message.append("Fare: ₹").append(ticket.getFare()).append("\n");
        message.append("Date: ").append(ticket.getDate()).append("\n");

        if (ticket.getBusNumber() != null) {
            message.append("Bus Number: ").append(ticket.getBusNumber()).append("\n");
            message.append("Vehicle Number: ").append(ticket.getVehicleNumber()).append("\n");
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Ticket Details")
                .setMessage(message.toString())
                .setPositiveButton("OK", null)
                .show();
    }
}
