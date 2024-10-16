package com.pro.shopfee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.R;
import com.pro.shopfee.adapter.PaymentMethodAdapter;
import com.pro.shopfee.event.PaymentMethodSelectedEvent;
import com.pro.shopfee.model.PaymentMethod;
import com.pro.shopfee.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PaymentMethodActivity extends BaseActivity {

    private List<PaymentMethod> listPaymentMethod;
    private PaymentMethodAdapter paymentMethodAdapter;
    private int paymentMethodSelectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        getDataIntent();
        initToolbar();
        initUi();
        getListPaymentMethodFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        paymentMethodSelectedId = bundle.getInt(Constant.PAYMENT_METHOD_ID, 0);
    }

    private void initUi() {
        RecyclerView rcvPaymentMethod = findViewById(R.id.rcv_payment_method);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvPaymentMethod.setLayoutManager(linearLayoutManager);
        listPaymentMethod = new ArrayList<>();
        paymentMethodAdapter = new PaymentMethodAdapter(listPaymentMethod,
                this::handleClickPaymentMethod);
        rcvPaymentMethod.setAdapter(paymentMethodAdapter);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.title_payment_method));
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getListPaymentMethodFromFirebase() {
        resetListPaymentMethod();
        listPaymentMethod.add(new PaymentMethod(1, "Thanh toán tiền mặt", "(Thanh toán khi nhận hàng)"));
        listPaymentMethod.add(new PaymentMethod(2, "Credit or debit card", "(Thẻ Visa hoặc Mastercard)"));
        listPaymentMethod.add(new PaymentMethod(3, "Chuyển khoản ngân hàng", "(Tự động xác nhận)"));
        listPaymentMethod.add(new PaymentMethod(4, "ZaloPay", "(Tự động xác nhận)"));

        if (paymentMethodSelectedId > 0 && listPaymentMethod != null) {
            for (PaymentMethod paymentMethod : listPaymentMethod) {
                if (paymentMethod.getId() == paymentMethodSelectedId) {
                    paymentMethod.setSelected(true);
                    break;
                }
            }
        }
        if (paymentMethodAdapter != null) paymentMethodAdapter.notifyDataSetChanged();
    }

    private void resetListPaymentMethod() {
        if (listPaymentMethod != null) {
            listPaymentMethod.clear();
        } else {
            listPaymentMethod = new ArrayList<>();
        }
    }

    private void handleClickPaymentMethod(PaymentMethod paymentMethod) {
        EventBus.getDefault().post(new PaymentMethodSelectedEvent(paymentMethod));
        finish();
    }
}
