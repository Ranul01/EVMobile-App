package com.example.evchargingmoblineapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "EVChargingDB";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // EV Owners table
    public static final String TABLE_EV_OWNERS = "ev_owners";
    public static final String COLUMN_NIC = "nic";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_CREATED_DATE = "created_date";

    // Bookings table
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String COLUMN_BOOKING_ID = "booking_id";
    public static final String COLUMN_EV_OWNER_NIC = "ev_owner_nic";
    public static final String COLUMN_STATION_ID = "station_id";
    public static final String COLUMN_STATION_NAME = "station_name";
    public static final String COLUMN_BOOKING_DATE = "booking_date";
    public static final String COLUMN_RESERVATION_DATE = "reservation_date";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_QR_CODE = "qr_code";
    public static final String COLUMN_TOTAL_AMOUNT = "total_amount";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "DatabaseHelper initialized");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database tables");

        // Create EV Owners table
        String createEVOwnersTable = "CREATE TABLE " + TABLE_EV_OWNERS + "(" +
                COLUMN_NIC + " TEXT PRIMARY KEY," +
                COLUMN_FIRST_NAME + " TEXT NOT NULL," +
                COLUMN_LAST_NAME + " TEXT NOT NULL," +
                COLUMN_EMAIL + " TEXT NOT NULL," +
                COLUMN_PHONE + " TEXT NOT NULL," +
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_PASSWORD + " TEXT NOT NULL," +
                COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1," +
                COLUMN_CREATED_DATE + " TEXT" +
                ")";

        // Create Bookings table
        String createBookingsTable = "CREATE TABLE " + TABLE_BOOKINGS + "(" +
                COLUMN_BOOKING_ID + " TEXT PRIMARY KEY," +
                COLUMN_EV_OWNER_NIC + " TEXT," +
                COLUMN_STATION_ID + " TEXT," +
                COLUMN_STATION_NAME + " TEXT," +
                COLUMN_BOOKING_DATE + " TEXT," +
                COLUMN_RESERVATION_DATE + " TEXT," +
                COLUMN_START_TIME + " TEXT," +
                COLUMN_END_TIME + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_QR_CODE + " TEXT," +
                COLUMN_TOTAL_AMOUNT + " REAL," +
                COLUMN_CREATED_DATE + " TEXT," +
                "FOREIGN KEY(" + COLUMN_EV_OWNER_NIC + ") REFERENCES " +
                TABLE_EV_OWNERS + "(" + COLUMN_NIC + ")" +
                ")";

        try {
            db.execSQL(createEVOwnersTable);
            db.execSQL(createBookingsTable);
            Log.d(TAG, "Database tables created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database tables: " + e.getMessage(), e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EV_OWNERS);
        onCreate(db);
    }
}
