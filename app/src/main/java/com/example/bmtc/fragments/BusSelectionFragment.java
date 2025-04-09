package com.example.bmtc.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bmtc.R;
import com.example.bmtc.adapters.BusListAdapter;
import com.example.bmtc.models.Bus;
import com.example.bmtc.network.ApiClient;
import com.example.bmtc.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusSelectionFragment extends Fragment {

    private EditText originInput, destinationInput;
    private Button buyButton;
    private RecyclerView busListRecyclerView;
    private BusListAdapter busListAdapter;
    private ApiService apiService;
    private List<Bus> availableBuses;

    public BusSelectionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bus_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        originInput = view.findViewById(R.id.originInput);
        destinationInput = view.findViewById(R.id.destinationInput);
        buyButton = view.findViewById(R.id.buyButton);
        busListRecyclerView = view.findViewById(R.id.busListRecyclerView);
        ImageButton backButton = view.findViewById(R.id.backButton);

        apiService = ApiClient.getClient().create(ApiService.class);

        // Set RecyclerView
        busListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Fetch buses when user enters origin & destination
        buyButton.setOnClickListener(v -> fetchBuses());

        // Back Button Click
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void fetchBuses() {
        String origin = originInput.getText().toString().trim();
        String destination = destinationInput.getText().toString().trim();

        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter both stops", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<Bus>> call = apiService.getBuses(origin, destination);
        call.enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    availableBuses = response.body();
                    busListAdapter = new BusListAdapter(availableBuses, BusSelectionFragment.this::goToPayment);
                    busListRecyclerView.setAdapter(busListAdapter);
                } else {
                    Toast.makeText(getActivity(), "No buses found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error fetching buses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToPayment(Bus selectedBus) {
        PaymentFragment paymentFragment = new PaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("bus_id", selectedBus.getBusId());
        bundle.putString("fare", String.valueOf(selectedBus.getFare()));
        paymentFragment.setArguments(bundle);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, paymentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

