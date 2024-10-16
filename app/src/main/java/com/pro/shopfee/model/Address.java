package com.pro.shopfee.model;

import java.io.Serializable;

public class Address implements Serializable {

    private long id;
    private String name;
    private String phone;
    private String address;

    private String userEmail;
    private boolean isSelected;

    public Address() {
    }

    public Address(long id, String name, String phone, String address, String userEmail) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.userEmail = userEmail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}