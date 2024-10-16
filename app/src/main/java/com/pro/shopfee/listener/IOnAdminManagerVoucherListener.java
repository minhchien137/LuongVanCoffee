package com.pro.shopfee.listener;

import com.pro.shopfee.model.Voucher;

public interface IOnAdminManagerVoucherListener {
    void onClickUpdateVoucher(Voucher voucher);
    void onClickDeleteVoucher(Voucher voucher);
}
