package com.example.evchargingmoblineapp.utils;

/*
 * APIService.java
 * Service class for API communication with backend
 * Author: [Your Name]
 * Date: [Date]
 */

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class APIService {
    private static final String BASE_URL = "https://your-api-url.com/api";
    private static final String TAG = "APIService";
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    /**
     * Interface for authentication callbacks
     */
    public interface AuthCallback {
        void onSuccess(String token, String role);
        void onError(String error);
    }

    /**
     * Interface for API response callbacks
     */
    public interface APICallback {
        void onSuccess(String response);
        void onError(String error);
    }

    /**
     * Authenticate user with backend server
     */
    public static void authenticate(String username, String password, String role,
                                    AuthCallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/auth/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject jsonInput = new JSONObject();
                jsonInput.put("username", username);
                jsonInput.put("password", password);
                jsonInput.put("role", role);

                OutputStream os = connection.getOutputStream();
                os.write(jsonInput.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                BufferedReader br;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String token = jsonResponse.getString("token");
                    callback.onSuccess(token, role);
                } else {
                    JSONObject errorResponse = new JSONObject(response.toString());
                    callback.onError(errorResponse.getString("message"));
                }

            } catch (IOException | JSONException e) {
                Log.e(TAG, "Authentication error", e);
                callback.onError("Network error: " + e.getMessage());
            }
        });
    }

    /**
     * Get charging stations from server
     */
    public static void getChargingStations(String token, APICallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/stations");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = connection.getResponseCode();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess(response.toString());
                } else {
                    callback.onError("Failed to fetch stations");
                }

            } catch (IOException e) {
                Log.e(TAG, "Get stations error", e);
                callback.onError("Network error: " + e.getMessage());
            }
        });
    }

    /**
     * Create new booking
     */
    public static void createBooking(String token, JSONObject bookingData,
                                     APICallback callback) {
        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/bookings");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                os.write(bookingData.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                if (responseCode == HttpURLConnection.HTTP_CREATED ||
                        responseCode == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess(response.toString());
                } else {
                    callback.onError("Failed to create booking");
                }

            } catch (IOException e) {
                Log.e(TAG, "Create booking error", e);
                callback.onError("Network error: " + e.getMessage());
            }
        });
    }
}
