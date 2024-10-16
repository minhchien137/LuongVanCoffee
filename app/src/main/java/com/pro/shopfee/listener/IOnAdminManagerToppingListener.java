package com.pro.shopfee.listener;

import com.pro.shopfee.model.Topping;

public interface IOnAdminManagerToppingListener {
    void onClickUpdateTopping(Topping topping);
    void onClickDeleteTopping(Topping topping);
}
