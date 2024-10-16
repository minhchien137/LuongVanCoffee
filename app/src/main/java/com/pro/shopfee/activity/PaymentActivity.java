package com.pro.shopfee.activity;

import android.os.Bundle;
import android.os.Handler;

import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.database.DrinkDatabase;
import com.pro.shopfee.event.DisplayCartEvent;
import com.pro.shopfee.event.OrderSuccessEvent;
import com.pro.shopfee.model.Order;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;

import org.greenrobot.eventbus.EventBus;

public class PaymentActivity extends BaseActivity {

    private Order mOrderBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getDataIntent();

        Handler handler = new Handler();
        handler.postDelayed(this::createOrderFirebase, 2000);
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mOrderBooking = (Order) bundle.get(Constant.ORDER_OBJECT);
    }

    private void createOrderFirebase() {
        MyApplication.get(this).getOrderDatabaseReference()
                .child(String.valueOf(mOrderBooking.getId()))
                .setValue(mOrderBooking, (error1, ref1) -> {

                    DrinkDatabase.getInstance(this).drinkDAO().deleteAllDrink();
                    EventBus.getDefault().post(new DisplayCartEvent());
                    EventBus.getDefault().post(new OrderSuccessEvent());

                    Bundle bundle = new Bundle();
                    bundle.putLong(Constant.ORDER_ID, mOrderBooking.getId());
                    GlobalFunction.startActivity(PaymentActivity.this,
                            ReceiptOrderActivity.class, bundle);

                    finish();
                });
    }
}
