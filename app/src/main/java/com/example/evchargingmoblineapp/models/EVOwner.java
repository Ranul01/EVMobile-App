package com.example.evchargingmoblineapp.models;


public class EVOwner {
    private String nic;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String password;
    private boolean isActive;
    private String createdDate;

    public EVOwner() {}

    public EVOwner(String nic, String firstName, String lastName, String email,
                   String phone, String address, String password) {
        this.nic = nic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.isActive = true;
        this.createdDate = new java.util.Date().toString();
    }

    // Getters and Setters
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
