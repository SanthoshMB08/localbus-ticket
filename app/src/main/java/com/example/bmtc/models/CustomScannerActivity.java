package com.example.bmtc.models;
import com.journeyapps.barcodescanner.CaptureActivity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class CustomScannerActivity extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lock to portrait or set to sensor-based
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }
}