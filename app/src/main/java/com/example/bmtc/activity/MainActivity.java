package com.example.bmtc.activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.bmtc.R;
import com.example.bmtc.fragments.MainFragment;
import com.example.bmtc.fragments.PaymentFragment;
import com.example.bmtc.fragments.TicketFragment;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PaymentResultListener {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Toolbar and set it as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get NavHostFragment properly
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        if (navController != null) {
            appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MainFragment())
                    .commit();
        }
        // ✅ Find PaymentFragment and register it as the payment listener
        PaymentFragment paymentFragment = (PaymentFragment) getSupportFragmentManager().findFragmentByTag("PAYMENT_FRAGMENT");
        if (paymentFragment != null) {
            Checkout.preload(getApplicationContext());
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "✅ Payment Successful! ID: " + razorpayPaymentID, Toast.LENGTH_LONG).show();

        // ✅ Save ticket data
        saveTicketData(razorpayPaymentID);

        // ✅ Safely switch to TicketFragment
        runOnUiThread(() -> {
            if (!isFinishing()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TicketFragment())
                        .commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "❌ Payment Failed! " + response, Toast.LENGTH_LONG).show();
    }

    private void saveTicketData(String transactionID) {
        Log.d("MainActivity", "🔹 saveTicketData() called with Transaction ID: " + transactionID);

        SharedPreferences sharedPreferences = getSharedPreferences("TicketData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Retrieve existing details
        String busNumber = sharedPreferences.getString("busNumber", "N/A");
        String vehicleNumber = sharedPreferences.getString("vehicleNumber", "N/A");
        String startStop = sharedPreferences.getString("startStop", "N/A");
        String endStop = sharedPreferences.getString("endStop", "N/A");
        int fare = sharedPreferences.getInt("fare", 0);

        // Add new details (Transaction ID, Date, Time)
        editor.putString("transactionID", transactionID);
        editor.putString("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));

        editor.apply();

        // ✅ Log all saved values for verification
        Log.d("MainActivity", "✅ Ticket Data Saved: ");
        Log.d("MainActivity", "Bus: " + busNumber + ", Vehicle: " + vehicleNumber);
        Log.d("MainActivity", "Route: " + startStop + " → " + endStop);
        Log.d("MainActivity", "Fare: ₹" + fare + ", Transaction ID: " + transactionID);
    }

}
