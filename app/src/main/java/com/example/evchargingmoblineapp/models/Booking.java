package com.example.evchargingmoblineapp.models;

public class Booking {
    private String bookingId;
    private String evOwnerNIC;
    private String stationId;
    private String stationName;
    private String bookingDate;
    private String reservationDate;
    private String startTime;
    private String endTime;
    private String status;
    private String qrCode;
    private double totalAmount;
    private String createdDate;

    public Booking() {}

    public Booking(String evOwnerNIC, String stationId, String reservationDate,
                   String startTime, String endTime, double totalAmount) {
        this.bookingId = java.util.UUID.randomUUID().toString();
        this.evOwnerNIC = evOwnerNIC;
        this.stationId = stationId;
        this.bookingDate = new java.util.Date().toString();
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "Pending";
        this.totalAmount = totalAmount;
        this.createdDate = new java.util.Date().toString();
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getEvOwnerNIC() { return evOwnerNIC; }
    public void setEvOwnerNIC(String evOwnerNIC) { this.evOwnerNIC = evOwnerNIC; }

    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getReservationDate() { return reservationDate; }
    public void setReservationDate(String reservationDate) { this.reservationDate = reservationDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
