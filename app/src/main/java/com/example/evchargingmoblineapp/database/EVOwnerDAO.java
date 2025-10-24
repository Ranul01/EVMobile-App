package com.example.evchargingmoblineapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.evchargingmoblineapp.models.EVOwner;
import java.util.ArrayList;
import java.util.List;

public class EVOwnerDAO {
    private static final String TAG = "EVOwnerDAO";
    private DatabaseHelper dbHelper;

    public EVOwnerDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        Log.d(TAG, "EVOwnerDAO initialized");
    }

    public long insertEVOwner(EVOwner owner) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_NIC, owner.getNic());
            values.put(DatabaseHelper.COLUMN_FIRST_NAME, owner.getFirstName());
            values.put(DatabaseHelper.COLUMN_LAST_NAME, owner.getLastName());
            values.put(DatabaseHelper.COLUMN_EMAIL, owner.getEmail());
            values.put(DatabaseHelper.COLUMN_PHONE, owner.getPhone());
            values.put(DatabaseHelper.COLUMN_ADDRESS, owner.getAddress());
            values.put(DatabaseHelper.COLUMN_PASSWORD, owner.getPassword());
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, owner.isActive() ? 1 : 0);
            values.put(DatabaseHelper.COLUMN_CREATED_DATE, owner.getCreatedDate());

            long result = db.insert(DatabaseHelper.TABLE_EV_OWNERS, null, values);
            Log.d(TAG, "Inserted EV Owner: " + owner.getNic() + ", result: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting EV Owner: " + e.getMessage(), e);
            return -1;
        } finally {
            if (db != null) db.close();
        }
    }

    public EVOwner getEVOwnerByNIC(String nic) {
        if (nic == null || nic.trim().isEmpty()) {
            Log.w(TAG, "getEVOwnerByNIC called with null or empty NIC");
            return null;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_NIC + " = ?";
            String[] selectionArgs = {nic};

            cursor = db.query(DatabaseHelper.TABLE_EV_OWNERS, null,
                    selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                EVOwner owner = new EVOwner();
                owner.setNic(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NIC)));
                owner.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FIRST_NAME)));
                owner.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LAST_NAME)));
                owner.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
                owner.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)));
                owner.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS)));
                owner.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD)));
                owner.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ACTIVE)) == 1);
                owner.setCreatedDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_DATE)));
                Log.d(TAG, "Found EV Owner: " + owner.getFullName());
                return owner;
            }
            Log.w(TAG, "No EV Owner found for NIC: " + nic);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting EV Owner: " + e.getMessage(), e);
            return null;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public boolean updateEVOwner(EVOwner owner) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.COLUMN_FIRST_NAME, owner.getFirstName());
            values.put(DatabaseHelper.COLUMN_LAST_NAME, owner.getLastName());
            values.put(DatabaseHelper.COLUMN_EMAIL, owner.getEmail());
            values.put(DatabaseHelper.COLUMN_PHONE, owner.getPhone());
            values.put(DatabaseHelper.COLUMN_ADDRESS, owner.getAddress());
            values.put(DatabaseHelper.COLUMN_PASSWORD, owner.getPassword());
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, owner.isActive() ? 1 : 0);

            String whereClause = DatabaseHelper.COLUMN_NIC + " = ?";
            String[] whereArgs = {owner.getNic()};

            int result = db.update(DatabaseHelper.TABLE_EV_OWNERS, values, whereClause, whereArgs);
            Log.d(TAG, "Updated EV Owner: " + owner.getNic() + ", rows affected: " + result);
            return result > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating EV Owner: " + e.getMessage(), e);
            return false;
        } finally {
            if (db != null) db.close();
        }
    }


    public int deleteEVOwner(String nic) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_NIC + " = ?";
            String[] whereArgs = {nic};

            int result = db.delete(DatabaseHelper.TABLE_EV_OWNERS, whereClause, whereArgs);
            Log.d(TAG, "Deleted EV Owner: " + nic + ", rows affected: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting EV Owner: " + e.getMessage(), e);
            return 0;
        } finally {
            if (db != null) db.close();
        }
    }

    public List<EVOwner> getAllEVOwners() {
        List<EVOwner> owners = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(DatabaseHelper.TABLE_EV_OWNERS, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    EVOwner owner = new EVOwner();
                    owner.setNic(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NIC)));
                    owner.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FIRST_NAME)));
                    owner.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LAST_NAME)));
                    owner.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)));
                    owner.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)));
                    owner.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS)));
                    owner.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ACTIVE)) == 1);
                    owner.setCreatedDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_DATE)));
                    owners.add(owner);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Retrieved " + owners.size() + " EV Owners");
        } catch (Exception e) {
            Log.e(TAG, "Error getting all EV Owners: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return owners;
    }
}
