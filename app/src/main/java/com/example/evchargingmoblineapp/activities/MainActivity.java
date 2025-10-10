package com.example.evchargingmoblineapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.evchargingmoblineapp.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int SPLASH_DELAY = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);
            Log.d(TAG, "MainActivity started");

            // Check if user is already logged in
            SharedPreferences prefs = getSharedPreferences("EVChargingApp", MODE_PRIVATE);
            String userNIC = prefs.getString("user_nic", "");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    Intent intent;
                    if (userNIC != null && !userNIC.isEmpty()) {
                        Log.d(TAG, "User already logged in, navigating to Dashboard");
                        intent = new Intent(MainActivity.this, DashboardActivity.class);
                    } else {
                        Log.d(TAG, "No logged in user, navigating to Login");
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating from splash: " + e.getMessage(), e);
                }
            }, SPLASH_DELAY);
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            // Fallback to login activity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
