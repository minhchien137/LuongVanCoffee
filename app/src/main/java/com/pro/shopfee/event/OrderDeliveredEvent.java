package com.pro.shopfee.event;

public class OrderDeliveredEvent {
    private String orderId;

    public OrderDeliveredEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
