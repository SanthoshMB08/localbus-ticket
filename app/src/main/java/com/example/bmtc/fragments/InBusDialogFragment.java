package com.example.bmtc.fragments;

import android.content.DialogInterface;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bmtc.R;
import com.example.bmtc.models.CustomScannerActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class InBusDialogFragment extends DialogFragment {

    private Button  proceedButton;
    private EditText busIdInput, vehicleNumberInput;
    private ImageButton backButton,scanQRButton;

    public InBusDialogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_in_bus, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scanQRButton = view.findViewById(R.id.scanQRButton);
        proceedButton = view.findViewById(R.id.proceedButton);
        busIdInput = view.findViewById(R.id.busIdInput);
        vehicleNumberInput = view.findViewById(R.id.vehicleNumberInput);
         backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> dismiss());

        // QR Scanner
        scanQRButton.setOnClickListener(v -> startQRScanner());

        // Proceed Button: Pass data to BusDetailsFragment
        proceedButton.setOnClickListener(v -> {
            String busId = busIdInput.getText().toString().trim().toUpperCase();
            String vehicleNumber = vehicleNumberInput.getText().toString().trim().toUpperCase();

            if (!busId.isEmpty() && !vehicleNumber.isEmpty()) {
                // Navigate to BusDetailsFragment with scanned details
                BusDetailsFragment busDetailsFragment = new BusDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("bus_id", busId);
                bundle.putString("vehicle_number", vehicleNumber);
                busDetailsFragment.setArguments(bundle);

                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, busDetailsFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                dismiss(); // Close the pop-up
            } else {
                Toast.makeText(getActivity(), "Please enter Bus ID and Vehicle Number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Start QR Code Scanner
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

    // Handle QR Scan Result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String scannedData = result.getContents();
            String[] parts = scannedData.split(",");
            if (parts.length == 2) {
                busIdInput.setText(parts[0]);
                vehicleNumberInput.setText(parts[1]);
            } else {
                Toast.makeText(getActivity(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Scan Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
