package com.example.bmtc.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

class PastTicketsFragment extends Fragment {

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
        adapter = new TicketAdapter(ticketList);
        recyclerView.setAdapter(adapter);
    }

    // Function to load tickets from SharedPreferences
    private List<Ticket> loadTickets() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("TicketPrefs", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("past_tickets", null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Ticket>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }
}
