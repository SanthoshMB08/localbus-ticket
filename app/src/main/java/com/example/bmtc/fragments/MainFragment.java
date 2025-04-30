package com.example.bmtc.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bmtc.R;
import com.example.bmtc.models.CustomScannerActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainFragment extends Fragment {

    private ImageButton scanQRButton, inBusButton, preBookButton ,ticketButton;
    private Button  lastticket ;
    private LinearLayout searchButton;

    public MainFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scanQRButton = view.findViewById(R.id.scanQRButton);
        inBusButton = view.findViewById(R.id.in_bus_button);
        preBookButton = view.findViewById(R.id.preBookButton);
        lastticket = view.findViewById(R.id.LastTicket);
        searchButton = view.findViewById(R.id.searchButton);
        ticketButton = view.findViewById(R.id.ticket_button);

        // Open QR Scanner
        scanQRButton.setOnClickListener(v -> startQRScanner());

        // Open InBus Fragment (Pop-up)
        inBusButton.setOnClickListener(v -> {
            InBusDialogFragment  inBusFragment = new InBusDialogFragment ();
            inBusFragment.show(getParentFragmentManager(), "InBusFragment");
        });

        // Navigate to Bus Selection (Pre-Book, Get Fare, Search Bar)
        View.OnClickListener busSelectionListener = v -> navigateToBusSelection();
        preBookButton.setOnClickListener(busSelectionListener);

        searchButton.setOnClickListener(busSelectionListener);
        lastticket.setOnClickListener(v -> navigateToTicketFragment());
        // Navigate to Past Tickets
        ticketButton.setOnClickListener(v -> navigateToPastTickets());
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan the QR Code on the bus");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false); // Allow auto-rotate
        integrator.setCaptureActivity(CustomScannerActivity.class); // Use custom UI
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String[] parts = result.getContents().split(",");
            if (parts.length == 2) {
                navigateToBusDetails(parts[0], parts[1]); // Auto-navigate after scanning
            } else {
                Toast.makeText(getActivity(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Scan Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToBusDetails(String busId, String vehicleNumber) {
        if (!busId.isEmpty() && !vehicleNumber.isEmpty()) {
            Bundle bundle = new Bundle();
            bundle.putString("bus_id", busId);
            bundle.putString("vehicle_number", vehicleNumber);

            BusDetailsFragment busDetailsFragment = new BusDetailsFragment();
            busDetailsFragment.setArguments(bundle);

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, busDetailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Toast.makeText(getActivity(), "Please enter valid Bus ID and Vehicle Number", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToBusSelection() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new BusSelectionFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void navigateToTicketFragment() {

        TicketFragment ticketFragment = new TicketFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, ticketFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss(); // ✅ Ensures it commits even if the state is lost

    }
    private void navigateToPastTickets() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new PastTicketsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
