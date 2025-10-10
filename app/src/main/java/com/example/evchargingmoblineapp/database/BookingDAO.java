package com.example.evchargingmoblineapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.evchargingmoblineapp.models.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private static final String TAG = "BookingDAO";
    private DatabaseHelper dbHelper;

    public BookingDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        Log.d(TAG, "BookingDAO initialized");
    }

    public long insertBooking(Booking booking) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_BOOKING_ID, booking.getBookingId());
            values.put(DatabaseHelper.COLUMN_EV_OWNER_NIC, booking.getEvOwnerNIC());
            values.put(DatabaseHelper.COLUMN_STATION_ID, booking.getStationId());
            values.put(DatabaseHelper.COLUMN_STATION_NAME, booking.getStationName());
            values.put(DatabaseHelper.COLUMN_BOOKING_DATE, booking.getBookingDate());
            values.put(DatabaseHelper.COLUMN_RESERVATION_DATE, booking.getReservationDate());
            values.put(DatabaseHelper.COLUMN_START_TIME, booking.getStartTime());
            values.put(DatabaseHelper.COLUMN_END_TIME, booking.getEndTime());
            values.put(DatabaseHelper.COLUMN_STATUS, booking.getStatus());
            values.put(DatabaseHelper.COLUMN_QR_CODE, booking.getQrCode());
            values.put(DatabaseHelper.COLUMN_TOTAL_AMOUNT, booking.getTotalAmount());
            values.put(DatabaseHelper.COLUMN_CREATED_DATE, booking.getCreatedDate());

            long result = db.insert(DatabaseHelper.TABLE_BOOKINGS, null, values);
            Log.d(TAG, "Inserted Booking: " + booking.getBookingId() + ", result: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting Booking: " + e.getMessage(), e);
            return -1;
        } finally {
            if (db != null) db.close();
        }
    }

    public List<Booking> getBookingsByOwner(String ownerNIC) {
        List<Booking> bookings = new ArrayList<>();
        if (ownerNIC == null || ownerNIC.trim().isEmpty()) {
            Log.w(TAG, "getBookingsByOwner called with null or empty NIC");
            return bookings;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_EV_OWNER_NIC + " = ?";
            String[] selectionArgs = {ownerNIC};
            String orderBy = DatabaseHelper.COLUMN_CREATED_DATE + " DESC";

            cursor = db.query(DatabaseHelper.TABLE_BOOKINGS, null,
                    selection, selectionArgs, null, null, orderBy);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Booking booking = new Booking();
                    booking.setBookingId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_ID)));
                    booking.setEvOwnerNIC(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EV_OWNER_NIC)));
                    booking.setStationId(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATION_ID)));
                    booking.setStationName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATION_NAME)));
                    booking.setBookingDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOOKING_DATE)));
                    booking.setReservationDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESERVATION_DATE)));
                    booking.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_TIME)));
                    booking.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_END_TIME)));
                    booking.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)));
                    booking.setQrCode(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_QR_CODE)));
                    booking.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TOTAL_AMOUNT)));
                    booking.setCreatedDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_DATE)));
                    bookings.add(booking);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Retrieved " + bookings.size() + " bookings for owner: " + ownerNIC);
        } catch (Exception e) {
            Log.e(TAG, "Error getting bookings: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return bookings;
    }

    public int getPendingBookingsCount(String ownerNIC) {
        if (ownerNIC == null || ownerNIC.trim().isEmpty()) {
            Log.w(TAG, "getPendingBookingsCount called with null or empty NIC");
            return 0;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_BOOKINGS +
                    " WHERE " + DatabaseHelper.COLUMN_EV_OWNER_NIC + " = ? AND " +
                    DatabaseHelper.COLUMN_STATUS + " = ?";

            cursor = db.rawQuery(query, new String[]{ownerNIC, "Pending"});
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Pending bookings count for " + ownerNIC + ": " + count);
                return count;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting pending bookings count: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    public int getApprovedBookingsCount(String ownerNIC) {
        if (ownerNIC == null || ownerNIC.trim().isEmpty()) {
            Log.w(TAG, "getApprovedBookingsCount called with null or empty NIC");
            return 0;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_BOOKINGS +
                    " WHERE " + DatabaseHelper.COLUMN_EV_OWNER_NIC + " = ? AND " +
                    DatabaseHelper.COLUMN_STATUS + " = ?";

            cursor = db.rawQuery(query, new String[]{ownerNIC, "Approved"});
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Approved bookings count for " + ownerNIC + ": " + count);
                return count;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting approved bookings count: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    public int getTotalBookingsCount(String ownerNIC) {
        if (ownerNIC == null || ownerNIC.trim().isEmpty()) {
            Log.w(TAG, "getTotalBookingsCount called with null or empty NIC");
            return 0;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_BOOKINGS +
                    " WHERE " + DatabaseHelper.COLUMN_EV_OWNER_NIC + " = ?";

            cursor = db.rawQuery(query, new String[]{ownerNIC});
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d(TAG, "Total bookings count for " + ownerNIC + ": " + count);
                return count;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting total bookings count: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return 0;
    }

    public int updateBookingStatus(String bookingId, String status) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_STATUS, status);

            String whereClause = DatabaseHelper.COLUMN_BOOKING_ID + " = ?";
            String[] whereArgs = {bookingId};

            int result = db.update(DatabaseHelper.TABLE_BOOKINGS, values, whereClause, whereArgs);
            Log.d(TAG, "Updated booking status: " + bookingId + ", rows affected: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error updating booking status: " + e.getMessage(), e);
            return 0;
        } finally {
            if (db != null) db.close();
        }
    }

    public int updateBookingQR(String bookingId, String qrCode) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_QR_CODE, qrCode);

            String whereClause = DatabaseHelper.COLUMN_BOOKING_ID + " = ?";
            String[] whereArgs = {bookingId};

            int result = db.update(DatabaseHelper.TABLE_BOOKINGS, values, whereClause, whereArgs);
            Log.d(TAG, "Updated booking QR: " + bookingId + ", rows affected: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error updating booking QR: " + e.getMessage(), e);
            return 0;
        } finally {
            if (db != null) db.close();
        }
    }

    public int deleteBooking(String bookingId) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_BOOKING_ID + " = ?";
            String[] whereArgs = {bookingId};

            int result = db.delete(DatabaseHelper.TABLE_BOOKINGS, whereClause, whereArgs);
            Log.d(TAG, "Deleted booking: " + bookingId + ", rows affected: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting booking: " + e.getMessage(), e);
            return 0;
        } finally {
            if (db != null) db.close();
        }
    }
}
