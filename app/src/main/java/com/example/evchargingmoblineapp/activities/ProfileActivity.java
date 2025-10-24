package com.example.evchargingmoblineapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingmoblineapp.R;
import com.example.evchargingmoblineapp.database.EVOwnerDAO;
import com.example.evchargingmoblineapp.models.EVOwner;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private TextView tvUserName, tvNIC, tvEmail, tvPhone, tvAddress;
    private Button btnEditProfile, btnChangePassword, btnViewBookings, btnLogout;

    private EVOwnerDAO evOwnerDAO;
    private String userNIC;
    private EVOwner currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        evOwnerDAO = new EVOwnerDAO(this);
        loadUserData();
        setupClickListeners();
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUserName = findViewById(R.id.tvUserName);
        tvNIC = findViewById(R.id.tvNIC);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnLogout = findViewById(R.id.btnLogout);
    }

    /**
     * Load user data from database
     */
    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("EVChargingApp", MODE_PRIVATE);
        userNIC = prefs.getString("user_nic", "");

        if (userNIC.isEmpty()) {
            Toast.makeText(this, "User session not found", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        currentUser = evOwnerDAO.getEVOwnerByNIC(userNIC);

        if (currentUser != null) {
            displayUserInfo();
        } else {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        }
    }

    /**
     * Display user information
     */
    private void displayUserInfo() {
        String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
        tvUserName.setText(fullName);
        tvNIC.setText(currentUser.getNic());
        tvEmail.setText(currentUser.getEmail());
        tvPhone.setText(currentUser.getPhone());
        tvAddress.setText(currentUser.getAddress());
    }

    /**
     * Setup click listeners for buttons
     */
    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> openEditProfile());
        btnChangePassword.setOnClickListener(v -> openChangePassword());
        btnViewBookings.setOnClickListener(v -> openBookings());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    /**
     * Open edit profile activity
     */
    private void openEditProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("USER_NIC", userNIC);
        startActivity(intent);
    }

    /**
     * Open change password activity
     */
    private void openChangePassword() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("USER_NIC", userNIC);
        startActivity(intent);
    }

    /**
     * Open bookings activity
     */
    private void openBookings() {
        Intent intent = new Intent(this, BookingActivity.class);
        startActivity(intent);
    }

    /**
     * Show logout confirmation dialog
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Handle user logout
     */
    private void logout() {
        SharedPreferences prefs = getSharedPreferences("EVChargingApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        navigateToLogin();
    }

    /**
     * Navigate to login activity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data when returning from edit profile
        loadUserData();
    }
}
