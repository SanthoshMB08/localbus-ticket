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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bmtc.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentFragment extends Fragment implements PaymentResultListener {

    private String amount, busNumber, vehicleNumber, startStop, endStop,type;
    private Button razorpayButton;

    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        TextView fareAmountText = view.findViewById(R.id.fareAmountText);
        razorpayButton = view.findViewById(R.id.razorpayButton);

        // Retrieve details from arguments
        if (getArguments() != null) {
            type= getArguments().getString("type", "N/A");
            switch (type){
                case"in_bus":{

            busNumber = getArguments().getString("bus_id", "N/A");
            vehicleNumber = getArguments().getString("vehicle_number", "N/A");
            startStop = getArguments().getString("start_stop", "N/A");
            endStop = getArguments().getString("end_stop", "N/A");

            // Retrieve fare correctly
            int fare = getArguments().getInt("fare", 0);
            amount = String.valueOf(fare * 100); // Razorpay uses paise, so multiply by 100

            fareAmountText.setText("Total Fare: ₹" + fare);
            break;
            }
            case"pre_book":{
                busNumber = getArguments().getString("bus_id", "N/A");
                vehicleNumber = "-None-";
                startStop = getArguments().getString("startStop", "N/A");
                endStop = getArguments().getString("endStop", "N/A");
                int fare = getArguments().getInt("fare", 0);
                amount = String.valueOf(fare * 100); // Razorpay uses paise, so multiply by 100
                fareAmountText.setText("Total Fare: ₹" + fare);
                break;
            }
            }
        }

        // Razorpay Payment Button Click
        razorpayButton.setOnClickListener(v -> {
            saveTicketToLocalStorage(); // ✅ First, save ticket details
            startRazorpayPayment();           // ✅ Then start the payment process
        });


        return view;
    }

    // ✅ Register Razorpay Listener
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Checkout.preload(getActivity().getApplicationContext());

    }

    // ✅ Secure UPI Payment via Razorpay
    private void startRazorpayPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_NxmcoW06IGDQ5C"); // Replace with your Razorpay API Key

        try {
            JSONObject options = new JSONObject();
            options.put("name", "BMTC Bus Service");
            options.put("description", "Bus Ticket Payment");
            options.put("currency", "INR");
            options.put("amount", amount); // Amount in paise (₹10 = 1000)
            options.put("prefill.vpa", "success@razorpay");
            options.put("prefill.email", "test@example.com");
            options.put("prefill.contact", "9999999999");

            // ✅ Force UPI as Default Payment Method
            JSONObject method = new JSONObject();
            method.put("upi", true);
            options.put("method", method);

            // ✅ Open Razorpay Payment Gateway
            checkout.open(getActivity(), options);

        } catch (Exception e) {
            Log.e("Razorpay", "Error in payment: " + e.getMessage());
            Toast.makeText(getContext(), "Payment failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // ✅ Handling Razorpay Payment Success
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        saveTicketToLocalStorage();
        Toast.makeText(getContext(), "✅ Payment Successful! ID: " + razorpayPaymentID, Toast.LENGTH_LONG).show();

        // ✅ Save Ticket Data Locally

        // Navigate back to the Ticket Fragment (or whichever is your previous fragment)
        requireActivity().runOnUiThread(() -> {
            Log.d("Razorpay", "Navigating to TicketFragment...");
            navigateToTicketFragment();
        });
    }



    // ✅ Handling Razorpay Payment Failure
    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(getContext(), "❌ Payment Failed! " + response, Toast.LENGTH_LONG).show();
    }

    // ✅ Save Ticket Data (Without Time & Date)
    public void saveTicketToLocalStorage() {
        Log.d("PaymentFragment", "🔹 saveTicketToLocalStorage() called");

        if (getActivity() == null) {
            Log.e("PaymentFragment", "❌ Activity is null, cannot save ticket");
            return;
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TicketData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (type){
            case "in_bus":{
                editor.putString("type",type);
        editor.putString("busNumber", busNumber);
        editor.putString("vehicleNumber", vehicleNumber);
        editor.putString("startStop", startStop);
        editor.putString("endStop", endStop);
        editor.putString("Status","verfied");
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                editor.putString("Time",dateTime);
        editor.putInt("fare", Integer.parseInt(amount) / 100); // Convert paise to ₹
        editor.apply();

        Log.d("PaymentFragment", "✅ Ticket Data Saved: Bus " + busNumber + ", Fare: ₹" + (Integer.parseInt(amount) / 100));

            break;}
        case "pre_book":{
            editor.putString("type",type);
            editor.putString("busNumber", busNumber);
            editor.putString("vehicleNumber", "-None-");
            editor.putString("startStop", startStop);
            editor.putString("endStop", endStop);
            editor.putString("Status","unverfied");
            editor.putInt("fare", Integer.parseInt(amount) / 100); // Convert paise to ₹
            String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            editor.putString("Time",dateTime);
            editor.apply();
            break;
        }}}
    // ✅ Navigate to Ticket Fragment After Payment
    private void navigateToTicketFragment() {
        Log.d("Razorpay", "Inside navigateToTicketFragment()");

        TicketFragment ticketFragment = new TicketFragment();

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, ticketFragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss(); // ✅ Ensures it commits even if the state is lost

        Log.d("Razorpay", "Fragment transaction committed");
    }
}
