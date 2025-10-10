package com.example.evchargingmoblineapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.evchargingmoblineapp.R;
import com.example.evchargingmoblineapp.database.BookingDAO;
import com.example.evchargingmoblineapp.database.EVOwnerDAO;
import com.example.evchargingmoblineapp.models.EVOwner;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    private TextView tvWelcome, tvPendingCount, tvApprovedCount, tvTotalBookings;
    private CardView cardNewBooking, cardViewBookings, cardProfile, cardNearbyStations;
    private String userNIC, userRole;
    private BookingDAO bookingDAO;
    private EVOwnerDAO evOwnerDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_dashboard);
            Log.d(TAG, "Dashboard activity started");

            initializeViews();
            getUserSession();
            setupDAOs();
            setupClickListeners();
            loadDashboardData();
        } catch (Exception e) {
            Log.e(TAG, "Critical error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading dashboard. Please try again.", Toast.LENGTH_LONG).show();
            // Redirect to login on critical error
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeViews() {
        try {
            tvWelcome = findViewById(R.id.tvWelcome);
            tvPendingCount = findViewById(R.id.tvPendingCount);
            tvApprovedCount = findViewById(R.id.tvApprovedCount);
            tvTotalBookings = findViewById(R.id.tvTotalBookings);

            cardNewBooking = findViewById(R.id.cardNewBooking);
            cardViewBookings = findViewById(R.id.cardViewBookings);
            cardProfile = findViewById(R.id.cardProfile);
            cardNearbyStations = findViewById(R.id.cardNearbyStations);

            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize views", e);
        }
    }

    private void getUserSession() {
        try {
            SharedPreferences prefs = getSharedPreferences("EVChargingApp", MODE_PRIVATE);
            userNIC = prefs.getString("user_nic", "");
            userRole = prefs.getString("user_role", "EVOwner");
            Log.d(TAG, "User session - NIC: " + userNIC + ", Role: " + userRole);

            if (userNIC == null || userNIC.isEmpty()) {
                Log.w(TAG, "No user session found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting user session: " + e.getMessage(), e);
            throw new RuntimeException("Failed to get user session", e);
        }
    }

    private void setupDAOs() {
        try {
            bookingDAO = new BookingDAO(this);
            evOwnerDAO = new EVOwnerDAO(this);
            Log.d(TAG, "DAOs initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up DAOs: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize DAOs", e);
        }
    }

    private void setupClickListeners() {
        try {
            cardNewBooking.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(DashboardActivity.this, BookingActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating to BookingActivity: " + e.getMessage(), e);
                    Toast.makeText(this, "Feature not available", Toast.LENGTH_SHORT).show();
                }
            });

            cardViewBookings.setOnClickListener(v -> {
                Toast.makeText(this, "View Bookings - Coming soon", Toast.LENGTH_SHORT).show();
            });

            cardProfile.setOnClickListener(v -> {
                Toast.makeText(this, "Profile - Coming soon", Toast.LENGTH_SHORT).show();
            });

            cardNearbyStations.setOnClickListener(v -> {
                Toast.makeText(this, "Nearby Stations - Coming soon", Toast.LENGTH_SHORT).show();
            });

            Log.d(TAG, "Click listeners configured successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void loadDashboardData() {
        try {
            // Set welcome message
            if (userNIC == null || userNIC.isEmpty()) {
                tvWelcome.setText("Welcome, Guest");
                Log.w(TAG, "No user NIC in session");
            } else if ("EVOwner".equals(userRole)) {
                EVOwner owner = evOwnerDAO.getEVOwnerByNIC(userNIC);
                if (owner != null) {
                    tvWelcome.setText("Welcome, " + owner.getFullName());
                    Log.d(TAG, "Welcome message set for: " + owner.getFullName());
                } else {
                    tvWelcome.setText("Welcome, EV Owner");
                    Log.w(TAG, "Owner not found in database for NIC: " + userNIC);
                }
            } else {
                tvWelcome.setText("Welcome, Station Operator");
            }

            // Load booking statistics
            int pendingCount = 0;
            int approvedCount = 0;
            int totalCount = 0;

            if (userNIC != null && !userNIC.isEmpty()) {
                pendingCount = bookingDAO.getPendingBookingsCount(userNIC);
                approvedCount = bookingDAO.getApprovedBookingsCount(userNIC);
                totalCount = bookingDAO.getTotalBookingsCount(userNIC);
            }

            tvPendingCount.setText(String.valueOf(pendingCount));
            tvApprovedCount.setText(String.valueOf(approvedCount));
            tvTotalBookings.setText(String.valueOf(totalCount));

            Log.d(TAG, String.format("Dashboard stats loaded - Pending: %d, Approved: %d, Total: %d",
                    pendingCount, approvedCount, totalCount));
        } catch (Exception e) {
            Log.e(TAG, "Error loading dashboard data: " + e.getMessage(), e);

            // Set default values on error
            tvWelcome.setText("Welcome");
            tvPendingCount.setText("0");
            tvApprovedCount.setText("0");
            tvTotalBookings.setText("0");

            Toast.makeText(this, "Error loading some data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "Dashboard activity resumed, refreshing data");
            loadDashboardData();
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
        }
    }
}
