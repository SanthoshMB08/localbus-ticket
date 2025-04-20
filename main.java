package com.example.busticketapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button scanButton;
    private TextView resultText;
    private RequestQueue requestQueue; // Volley request queue

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanButton = findViewById(R.id.scanButton);
        resultText = findViewById(R.id.resultText);
        requestQueue = Volley.newRequestQueue(this);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQrCode();
            }
        });
    }

    private void scanQrCode() {
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this);
        scanner.startScan()
            .addOnSuccessListener(new OnSuccessListener<com.google.mlkit.vision.barcode.common.Barcode>() {
                @Override
                public void onSuccess(com.google.mlkit.vision.barcode.common.Barcode barcode) {
                    String qrData = barcode.getRawValue(); // Assuming QR contains bus_id
                    fetchBusDetails(qrData);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    resultText.setText("Failed to scan QR Code");
                }
            });
    }

    private void fetchBusDetails(String busId) {
        String url = "http://192.168.1.100:5000/get_bus"; // Replace with your server's IP

        JSONObject params = new JSONObject();
        try {
            params.put("bus_id", busId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String route = response.getString("route");
                        JSONArray stops = response.getJSONArray("stops");
                        resultText.setText("Route: " + route + "\nStops: " + stops.join(", "));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    resultText.setText("Error: " + error.getMessage());
                }
            });

        requestQueue.add(request);
    }
}
