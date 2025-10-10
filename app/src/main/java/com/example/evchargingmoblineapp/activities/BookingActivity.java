package com.example.evchargingmoblineapp.activities;

/*
 * BookingActivity.java
 * Activity for creating new bookings
 * Author: [Your Name]
 * Date: [Date]
 */

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingmoblineapp.R;
import com.example.evchargingmoblineapp.database.BookingDAO;
import com.example.evchargingmoblineapp.models.Booking;
import com.example.evchargingmoblineapp.utils.APIService;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private Spinner spinnerStations;
    private EditText etReservationDate, etStartTime, etEndTime;
    private TextView tvTotalAmount;
    private Button btnCreateBooking;
    private BookingDAO bookingDAO;
    private String userNIC;
    private Calendar calendar;
    private SimpleDateFormat dateFormat, timeFormat;

    /// <summary>
    /// Initialize booking activity
    /// </summary>
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initializeViews();
        setupDateTimeFormatters();
        getUserSession();
        bookingDAO = new BookingDAO(this);
        setupClickListeners();
        loadStations();
    }

    /// <summary>
    /// Initialize UI components
    /// </summary>
    private void initializeViews() {
        spinnerStations = findViewById(R.id.spinnerStations);
        etReservationDate = findViewById(R.id.etReservationDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnCreateBooking = findViewById(R.id.btnCreateBooking);
        calendar = Calendar.getInstance();
    }

    /// <summary>
    /// Setup date and time formatters
    /// </summary>
    private void setupDateTimeFormatters() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    /// <summary>
    /// Get user session data
    /// </summary>
    private void getUserSession() {
        SharedPreferences prefs = getSharedPreferences("EVChargingApp", MODE_PRIVATE);
        userNIC = prefs.getString("user_nic", "");
    }

    /// <summary>
    /// Setup click listeners for UI components
    /// </summary>
    private void setupClickListeners() {
        etReservationDate.setOnClickListener(v -> showDatePicker());
        etStartTime.setOnClickListener(v -> showTimePicker(true));
        etEndTime.setOnClickListener(v -> showTimePicker(false));
        btnCreateBooking.setOnClickListener(v -> createBooking());
    }

    /// <summary>
    /// Show date picker dialog
    /// </summary>
    private void showDatePicker() {
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 7); // 7 days from now

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    etReservationDate.setText(dateFormat.format(calendar.getTime()));
                    calculateTotalAmount();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    /// <summary>
    /// Show time picker dialog
    /// </summary>
    private void showTimePicker(boolean isStartTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    String timeString = timeFormat.format(calendar.getTime());

                    if (isStartTime) {
                        etStartTime.setText(timeString);
                    } else {
                        etEndTime.setText(timeString);
                    }
                    calculateTotalAmount();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        timePickerDialog.show();
    }

    /// <summary>
    /// Load available charging stations
    /// </summary>
    private void loadStations() {
        // For demo purposes, using static data
        // In real implementation, load from API
        String[] stations = {"Station A - Downtown", "Station B - Mall", "Station C - Airport"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, stations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStations.setAdapter(adapter);
    }

    /// <summary>
    /// Calculate total amount based on time duration
    /// </summary>
    private void calculateTotalAmount() {
        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();

        if (!startTime.isEmpty() && !endTime.isEmpty()) {
            try {
                Date start = timeFormat.parse(startTime);
                Date end = timeFormat.parse(endTime);

                if (start != null && end != null && end.after(start)) {
                    long durationMillis = end.getTime() - start.getTime();
                    long durationHours = durationMillis / (1000 * 60 * 60);
                    if (durationMillis % (1000 * 60 * 60) > 0) {
                        durationHours++; // Round up partial hours
                    }

                    double pricePerHour = 15.0; // Default price
                    double totalAmount = durationHours * pricePerHour;
                    tvTotalAmount.setText(String.format("Total: $%.2f", totalAmount));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /// <summary>
    /// Create new booking
    /// </summary>
    private void createBooking() {
        if (!validateInputs()) {
            return;
        }

        String selectedStation = spinnerStations.getSelectedItem().toString();
        String stationId = "STATION_" + (spinnerStations.getSelectedItemPosition() + 1);
        String reservationDate = etReservationDate.getText().toString();
        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();

        // Calculate total amount
        double totalAmount = calculateFinalAmount();

        // Create booking object
        Booking booking = new Booking(userNIC, stationId, reservationDate, startTime, endTime, totalAmount);

        // Save to local database
        long result = bookingDAO.insertBooking(booking);

        if (result > 0) {
            // Also send to server
            sendBookingToServer(booking);
            Toast.makeText(this, "Booking created successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to create booking", Toast.LENGTH_SHORT).show();
        }
    }

    /// <summary>
    /// Validate user inputs
    /// </summary>
    private boolean validateInputs() {
        if (etReservationDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select reservation date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etStartTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etEndTime.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select end time", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate that end time is after start time
        try {
            Date start = timeFormat.parse(etStartTime.getText().toString());
            Date end = timeFormat.parse(etEndTime.getText().toString());

            if (start != null && end != null && !end.after(start)) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /// <summary>
    /// Calculate final amount for booking
    /// </summary>
    private double calculateFinalAmount() {
        try {
            Date start = timeFormat.parse(etStartTime.getText().toString());
            Date end = timeFormat.parse(etEndTime.getText().toString());

            if (start != null && end != null) {
                long durationMillis = end.getTime() - start.getTime();
                long durationHours = durationMillis / (1000 * 60 * 60);
                if (durationMillis % (1000 * 60 * 60) > 0) {
                    durationHours++;
                }
                return durationHours * 15.0; // $15 per hour
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /// <summary>
    /// Send booking data to server
    /// </summary>
    private void sendBookingToServer(Booking booking) {
        try {
            JSONObject bookingData = new JSONObject();
            bookingData.put("evOwnerNIC", booking.getEvOwnerNIC());
            bookingData.put("stationId", booking.getStationId());
            bookingData.put("reservationDate", booking.getReservationDate());
            bookingData.put("startTime", booking.getStartTime());
            bookingData.put("endTime", booking.getEndTime());
            bookingData.put("totalAmount", booking.getTotalAmount());

            SharedPreferences prefs = getSharedPreferences("EVChargingApp", MODE_PRIVATE);
            String token = prefs.getString("auth_token", "");

            APIService.createBooking(token, bookingData, new APIService.APICallback() {
                @Override
                public void onSuccess(String response) {
                    // Handle successful server response
                    runOnUiThread(() -> {
                        try {
                            JSONObject responseObj = new JSONObject(response);
                            String qrCode = responseObj.optString("qrCode", "");
                            if (!qrCode.isEmpty()) {
                                bookingDAO.updateBookingQR(booking.getBookingId(), qrCode);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    // Handle error - booking is still saved locally
                    runOnUiThread(() -> {
                        Toast.makeText(BookingActivity.this,
                                "Booking saved locally. Will sync when online.",
                                Toast.LENGTH_SHORT).show();
                    });
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
