package com.pro.shopfee.model;

import com.pro.shopfee.utils.StringUtil;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    // nhan don hang
    public static final int STATUS_NEW = 1;
    // chuan bi don hang
    public static final int STATUS_PREPARE = 2;
    // giao don hang
    public static final int STATUS_DOING = 3;
    // giao thanh cong
    public static final int STATUS_DOINGCOMPLETE  = 4;
    // nhan don hang
    public static final int STATUS_COMPLETE = 5;
    // huy don hang
    public static final int STATUS_CANCELLED = -1;



    private long id;
    private String userEmail;
    private String dateTime;
    private List<DrinkOrder> drinks;
    private int price;
    private int voucher;
    private int total;
    private String paymentMethod;
    private int status;
    private double rate;
    private String review;
    private Address address;
    private String tableNumber;

    public Order() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public List<DrinkOrder> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<DrinkOrder> drinks) {
        this.drinks = drinks;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getVoucher() {
        return voucher;
    }

    public void setVoucher(int voucher) {
        this.voucher = voucher;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getListDrinksName() {
        if (drinks == null || drinks.isEmpty()) return "";
        String result = "";
        for (DrinkOrder drinkOrder : drinks) {
            if (StringUtil.isEmpty(result)) {
                result += drinkOrder.getName();
            } else {
                result += ", " + drinkOrder.getName();
            }
        }
        return result;
    }
}
