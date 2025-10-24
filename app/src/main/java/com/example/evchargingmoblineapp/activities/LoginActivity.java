package com.example.evchargingmoblineapp.activities;

/*
 * LoginActivity.java
 * Login screen for EV owners and station operators
 * Author: [Your Name]
 * Date: [Date]
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingmoblineapp.R;
import com.example.evchargingmoblineapp.database.EVOwnerDAO;
import com.example.evchargingmoblineapp.models.EVOwner;
import com.example.evchargingmoblineapp.utils.APIService;

public class LoginActivity extends AppCompatActivity {
    private EditText etNIC, etPassword;
    private Button btnLogin, btnOperatorLogin;
    private TextView tvRegister;
    private EVOwnerDAO evOwnerDAO;

    /**
     * Initialize login activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        evOwnerDAO = new EVOwnerDAO(this);
        setupClickListeners();
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        etNIC = findViewById(R.id.etNIC);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnOperatorLogin = findViewById(R.id.btnOperatorLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    /**
     * Setup click listeners for buttons
     */
    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleEVOwnerLogin());
        btnOperatorLogin.setOnClickListener(v -> handleOperatorLogin());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Handle EV owner login
     */
    private void handleEVOwnerLogin() {
        String nic = etNIC.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (nic.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // First check local database
        EVOwner owner = evOwnerDAO.getEVOwnerByNIC(nic);
        if (owner != null && owner.getPassword().equals(password) && owner.isActive()) {
            saveUserSession(nic, "EVOwner");
            navigateToDashboard();
        } else {
            // Try to authenticate with server
            authenticateWithServer(nic, password, "EVOwner");
        }
    }

    /**
     * Handle station operator login
     */
    private void handleOperatorLogin() {
        String username = etNIC.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        authenticateWithServer(username, password, "StationOperator");
    }

    /**
     * Authenticate user with server
     */
    private void authenticateWithServer(String username, String password, String role) {
        APIService.authenticate(username, password, role, new APIService.AuthCallback() {
            @Override
            public void onSuccess(String token, String userRole) {
                runOnUiThread(() -> {
                    saveUserSession(username, userRole);
                    navigateToDashboard();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Login failed: " + error,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Save user session data
     */
    private void saveUserSession(String userIdentifier, String role) {
        SharedPreferences prefs = getSharedPreferences("EVChargingApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_nic", userIdentifier);
        editor.putString("user_role", role);
        editor.apply();
    }

    /**
     * Navigate to dashboard
     */
    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
