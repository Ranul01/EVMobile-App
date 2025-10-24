package com.example.evchargingmoblineapp.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingmoblineapp.R;
import com.example.evchargingmoblineapp.database.EVOwnerDAO;
import com.example.evchargingmoblineapp.models.EVOwner;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etEmail, etPhone, etAddress;
    private Button btnSave, btnCancel;

    private EVOwnerDAO evOwnerDAO;
    private String userNIC;
    private EVOwner currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userNIC = getIntent().getStringExtra("USER_NIC");

        initializeViews();
        evOwnerDAO = new EVOwnerDAO(this);
        loadUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

        private void loadUserData() {
        if (userNIC == null) {
            Toast.makeText(this, "User information not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUser = evOwnerDAO.getEVOwnerByNIC(userNIC);
        if (currentUser != null) {
            etFirstName.setText(currentUser.getFirstName());
            etLastName.setText(currentUser.getLastName());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhone());
            etAddress.setText(currentUser.getAddress());
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (!validateInputs(firstName, lastName, email, phone)) {
            return;
        }

        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        if (evOwnerDAO.updateEVOwner(currentUser)) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(String firstName, String lastName, String email, String phone) {
        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name is required");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone is required");
            return false;
        }

        if (phone.length() != 10) {
            etPhone.setError("Phone must be 10 digits");
            return false;
        }

        return true;
    }
}
