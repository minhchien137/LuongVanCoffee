package com.pro.shopfee.event;

import com.pro.shopfee.model.PaymentMethod;

public class PaymentMethodSelectedEvent {

    private PaymentMethod paymentMethod;

    public PaymentMethodSelectedEvent(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
