package com.pro.shopfee.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.adapter.DrinkOrderAdapter;
import com.pro.shopfee.model.Order;
import com.pro.shopfee.model.RatingReview;
import com.pro.shopfee.prefs.DataStoreManager;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TrackingOrderActivity extends BaseActivity {

    private RecyclerView rcvDrinks;
    private LinearLayout layoutReceiptOrder;
    private View dividerStep1, dividerStep2;
    private ImageView imgStep1, imgStep2, imgStep3;
    private TextView tvTakeOrder, tvTakeOrderMessage;

    private long orderId;
    private Order mOrder;
    private boolean isOrderArrived;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);

        getDataIntent();
        initToolbar();
        initUi();
        initListener();
        getOrderDetailFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        orderId = bundle.getLong(Constant.ORDER_ID);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(getString(R.string.label_tracking_order));
    }

    private void initUi() {
        rcvDrinks = findViewById(R.id.rcv_drinks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvDrinks.setLayoutManager(linearLayoutManager);
        layoutReceiptOrder = findViewById(R.id.layout_receipt_order);
        dividerStep1 = findViewById(R.id.divider_step_1);
        dividerStep2 = findViewById(R.id.divider_step_2);
        imgStep1 = findViewById(R.id.img_step_1);
        imgStep2 = findViewById(R.id.img_step_2);
        imgStep3 = findViewById(R.id.img_step_3);
        tvTakeOrder = findViewById(R.id.tv_take_order);
        tvTakeOrderMessage = findViewById(R.id.tv_take_order_message);
        LinearLayout layoutBottom = findViewById(R.id.layout_bottom);
        if (DataStoreManager.getUser().isAdmin()) {
            layoutBottom.setVisibility(View.GONE);
        } else {
            layoutBottom.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {
        layoutReceiptOrder.setOnClickListener(view -> {
            if (mOrder == null) return;
            Bundle bundle = new Bundle();
            bundle.putLong(Constant.ORDER_ID, mOrder.getId());
            GlobalFunction.startActivity(TrackingOrderActivity.this,
                    ReceiptOrderActivity.class, bundle);
            finish();
        });

        if (DataStoreManager.getUser().isAdmin()) {
            imgStep1.setOnClickListener(view -> updateStatusOrder(Order.STATUS_NEW));
            imgStep2.setOnClickListener(view -> updateStatusOrder(Order.STATUS_DOING));
            imgStep3.setOnClickListener(view -> updateStatusOrder(Order.STATUS_ARRIVED));
        } else {
            imgStep1.setOnClickListener(null);
            imgStep2.setOnClickListener(null);
            imgStep3.setOnClickListener(null);
        }
        tvTakeOrder.setOnClickListener(view -> {
            if (isOrderArrived) {
                updateStatusOrder(Order.STATUS_COMPLETE);
            }
        });
    }

    private void getOrderDetailFromFirebase() {
        showProgressDialog(true);
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                mOrder = snapshot.getValue(Order.class);
                if (mOrder == null) return;

                initData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).getOrderDetailDatabaseReference(orderId)
                .addValueEventListener(mValueEventListener);
    }

    private void initData() {
        DrinkOrderAdapter adapter = new DrinkOrderAdapter(mOrder.getDrinks());
        rcvDrinks.setAdapter(adapter);

        switch (mOrder.getStatus()) {
            case Order.STATUS_NEW:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_disable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                imgStep3.setImageResource(R.drawable.ic_step_disable);

                isOrderArrived = false;
                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                tvTakeOrderMessage.setVisibility(View.GONE);
                break;

            case Order.STATUS_DOING:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_enable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep3.setImageResource(R.drawable.ic_step_disable);

                isOrderArrived = false;
                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                tvTakeOrderMessage.setVisibility(View.GONE);
                break;

            case Order.STATUS_ARRIVED:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_enable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep3.setImageResource(R.drawable.ic_step_enable);

                isOrderArrived = true;
                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                tvTakeOrderMessage.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateStatusOrder(int status) {
        if (mOrder == null) return;
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);

        MyApplication.get(this).getOrderDatabaseReference()
                .child(String.valueOf(mOrder.getId()))
                .updateChildren(map, (error, ref) -> {
                    if (Order.STATUS_COMPLETE == status) {
                        Bundle bundle = new Bundle();
                        RatingReview ratingReview = new RatingReview(RatingReview.TYPE_RATING_REVIEW_ORDER,
                                String.valueOf(mOrder.getId()));
                        bundle.putSerializable(Constant.RATING_REVIEW_OBJECT, ratingReview);
                        GlobalFunction.startActivity(TrackingOrderActivity.this,
                                RatingReviewActivity.class, bundle);
                        finish();
                    }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).getOrderDetailDatabaseReference(orderId)
                    .removeEventListener(mValueEventListener);
        }
    }
}
