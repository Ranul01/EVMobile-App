package com.example.evchargingmoblineapp.utils;
/*
 * QRCodeGenerator.java
 * Utility class for generating QR codes
 * Author: [Your Name]
 * Date: [Date]
 */


import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeGenerator {

    /// <summary>
    /// Generate QR code bitmap from booking data
    /// </summary>
    public static Bitmap generateQRCode(String bookingData, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(bookingData, BarcodeFormat.QR_CODE, width, height);

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    /// <summary>
    /// Generate QR code data string from booking information
    /// </summary>
    public static String generateQRData(String bookingId, String ownerNIC, String stationId,
                                        String reservationDate, String startTime) {
        return String.format("BOOKING:%s|OWNER:%s|STATION:%s|DATE:%s|TIME:%s",
                bookingId, ownerNIC, stationId, reservationDate, startTime);
    }
}
