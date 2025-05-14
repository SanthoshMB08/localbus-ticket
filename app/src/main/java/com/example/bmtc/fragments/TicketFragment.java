package com.example.bmtc.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bmtc.models.CustomScannerActivity;
import com.example.bmtc.network.ApiClient;
import com.example.bmtc.network.ApiService;
import com.example.bmtc.R;
import com.example.bmtc.models.Ticket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketFragment extends Fragment {

    private TextView ticketDetailsText;
    public Button verfiy;
    public int fare;
    private ApiService apiService;
    public String startStop, endStop, type, busNumber, status;

    public TicketFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        ticketDetailsText = view.findViewById(R.id.ticketDetailsText);
        Button clearTicketButton = view.findViewById(R.id.clearTicketButton);
        verfiy = view.findViewById(R.id.verifyButton);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        boolean var= sharedPreferences.getBoolean("pay", false);
        if(var){
            savedata();

        }

        loadTicket();
        saveTicketToPrefsFromTicketData();

        clearTicketButton.setOnClickListener(v -> clearTicket());
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Optional: Clear any data or perform cleanup here
                        //if(status.equals("Verified")){
                            //clearTicket();}
                        // Replace with MainFragment
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, new MainFragment())
                                .commit();
                    }
                }
        );

        return view;
    }
    public void savedata(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        String type = sharedPreferences.getString("type", "N/A");
        String busNumber = sharedPreferences.getString("busNumber", "N/A");
        String vehicleNumber = sharedPreferences.getString("vehicleNumber", "N/A");
       String  startStop = sharedPreferences.getString("startStop", "N/A");
        String endStop = sharedPreferences.getString("endStop", "N/A");
        String timestamp = sharedPreferences.getString("Time", "N/A");
        String status = sharedPreferences.getString("Status", "N/A");
        int fare = sharedPreferences.getInt("fare", 0);

        SharedPreferences sharedPrefer = getActivity().getSharedPreferences("TicketData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefer.edit();
        switch (type){
            case "in_bus":{
                editor.putString("type",type);
                editor.putString("busNumber", busNumber);
                editor.putString("vehicleNumber", vehicleNumber);
                editor.putString("startStop", startStop);
                editor.putString("endStop", endStop);
                editor.putString("Status","verified");
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                editor.putString("Time",dateTime);
                editor.putInt("fare", fare ); // Convert paise to ₹
                editor.apply();

                Log.d("PaymentFragment", "✅ Ticket Data Saved: Bus " + busNumber + ", Fare: ₹" + (fare / 100));

                break;}
            case "pre_book":{
                editor.putString("type",type);
                editor.putString("busNumber", busNumber);
                editor.putString("vehicleNumber", "-None-");
                editor.putString("startStop", startStop);
                editor.putString("endStop", endStop);
                editor.putString("Status","unverified");
                editor.putInt("fare", fare); // Convert paise to ₹
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                editor.putString("Time",dateTime);
                editor.apply();
                break;
            }}

    }

    private void loadTicket() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TicketData", Context.MODE_PRIVATE);
        type = sharedPreferences.getString("type", "N/A");
        busNumber = sharedPreferences.getString("busNumber", "N/A");
        String vehicleNumber = sharedPreferences.getString("vehicleNumber", "N/A");
        startStop = sharedPreferences.getString("startStop", "N/A");
        endStop = sharedPreferences.getString("endStop", "N/A");
        String timestamp = sharedPreferences.getString("Time", "N/A");
         status = sharedPreferences.getString("Status", "N/A");
        fare = sharedPreferences.getInt("fare", 0);

        if (busNumber.equals("N/A")) {
            ticketDetailsText.setText(getString(R.string.no_ticket_available));
        } else {
            String ticketDetails = getString(R.string.ticket_details_format, busNumber, vehicleNumber, startStop, endStop, fare, timestamp, status);
            ticketDetailsText.setText(ticketDetails);

            if ( !status.equals("verified")) {
                verfiy.setVisibility(View.VISIBLE);
                verfiy.setOnClickListener(v -> startQRScanner());
            }
        }
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
                verify(parts[0], parts[1]);
            } else {
                Toast.makeText(getActivity(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Scan Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void verify(String busid, String vehicalnumber) {
        if (busNumber.equals(busid)) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TicketData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("vehicleNumber", vehicalnumber);
            editor.putString("Status", "verified");
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            editor.putString("Time", dateTime);
            editor.apply();
            SharedPreferences ticketPrefs = requireContext().getSharedPreferences("TicketPrefs", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = ticketPrefs.getString("past_tickets", null);
            Type type = new TypeToken<ArrayList<Ticket>>() {}.getType();
            List<Ticket> ticketList = json != null ? gson.fromJson(json, type) : new ArrayList<>();

            String ticketId = "TICKET_" + System.currentTimeMillis();
            editor.putString("lastTicketId", ticketId); // Save latest ID

            Ticket ticket = new Ticket(ticketId, busNumber, vehicalnumber, startStop, endStop, fare, dateTime);
            ticketList.add(ticket);

            SharedPreferences.Editor editor1 = ticketPrefs.edit();
            editor1.putString("past_tickets", gson.toJson(ticketList));
            editor1.apply();
            String ticketDetails = getString(R.string.ticket_details_format, busNumber, vehicalnumber, startStop, endStop, fare,  dateTime,"Verified");
            ticketDetailsText.setText(ticketDetails);
            verfiy.setVisibility(View.INVISIBLE);
            status="verified";

        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Failed")
                    .setMessage("Invalid bus")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void saveTicketToPrefsFromTicketData() {
        if ("pre_book".equals(type)) return;

        SharedPreferences ticketDataPrefs = requireContext().getSharedPreferences("TicketData", Context.MODE_PRIVATE);
        SharedPreferences ticketPrefs = requireContext().getSharedPreferences("TicketPrefs", Context.MODE_PRIVATE);

        String busNumber = ticketDataPrefs.getString("busNumber", null);
        String vehicleNumber = ticketDataPrefs.getString("vehicleNumber", null);
        String startStop = ticketDataPrefs.getString("startStop", null);
        String endStop = ticketDataPrefs.getString("endStop", null);
        int fare = ticketDataPrefs.getInt("fare", 0);
        String dateTime = ticketDataPrefs.getString("Time", null);

        Gson gson = new Gson();
        String json = ticketPrefs.getString("past_tickets", null);
        Type type = new TypeToken<ArrayList<Ticket>>() {}.getType();
        List<Ticket> ticketList = json != null ? gson.fromJson(json, type) : new ArrayList<>();

        String ticketId = "TICKET_" + System.currentTimeMillis()+busNumber;
        ticketDataPrefs.edit().putString("lastTicketId", ticketId).apply();

        Ticket ticket = new Ticket(ticketId, busNumber, vehicleNumber, startStop, endStop, fare, dateTime);
        ticketList.add(ticket);

        SharedPreferences.Editor editor = ticketPrefs.edit();
        editor.putString("past_tickets", gson.toJson(ticketList));
        editor.apply();

        Log.d("TicketSave", "✅ Ticket added to past_tickets with ID: " + ticketId);
    }

    private void clearTicket() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TicketData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        ticketDetailsText.setText(getString(R.string.no_ticket_available));
    }

}
