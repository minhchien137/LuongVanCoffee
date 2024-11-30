package com.pro.shopfee.model;

public class Notification {
    private String notificationId;
    private String orderId;
    private String message;
    private boolean Read;


    public Notification(String notificationId,String orderId, String message, boolean Read) {
        this.notificationId = notificationId;
        this.orderId = orderId;
        this.message = message;
        this.Read = Read;
    }
    // Constructor không tham số
    public Notification() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getRead() {
        return Read;
    }

    public void setRead(boolean read) {
        Read = read;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
