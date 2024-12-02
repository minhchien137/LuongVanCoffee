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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.adapter.DrinkOrderAdapter;
import com.pro.shopfee.model.Notification;
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
    private View dividerStep1, dividerStep2, dividerStep3;
    private ImageView imgStep1, imgStep2, imgStep3, imgStep4;
    private TextView tvTakeOrder, tvTakeOrderMessage,tvCancelOrder;

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
        dividerStep3 = findViewById(R.id.divider_step_3);
        imgStep1 = findViewById(R.id.img_step_1);
        imgStep2 = findViewById(R.id.img_step_2);
        imgStep3 = findViewById(R.id.img_step_3);
        imgStep4 = findViewById(R.id.img_step_4);
        tvTakeOrder = findViewById(R.id.tv_take_order);
        tvTakeOrderMessage = findViewById(R.id.tv_take_order_message);
        tvCancelOrder = findViewById(R.id.tv_cancel_order);
        LinearLayout layoutBottom = findViewById(R.id.layout_bottom);
        if (DataStoreManager.getUser().isAdmin()) {
            layoutBottom.setVisibility(View.VISIBLE);
            tvTakeOrder.setVisibility(View.GONE);
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
            imgStep2.setOnClickListener(view -> updateStatusOrder(Order.STATUS_PREPARE));
            imgStep3.setOnClickListener(view -> updateStatusOrder(Order.STATUS_DOING));
            imgStep4.setOnClickListener(view -> updateStatusOrder(Order.STATUS_DOINGCOMPLETE));
        } else {
            imgStep1.setOnClickListener(null);
            imgStep2.setOnClickListener(null);
            imgStep3.setOnClickListener(null);
            imgStep4.setOnClickListener(null);
        }
        tvTakeOrder.setOnClickListener(view -> {
            if (isOrderArrived) {
                updateStatusOrder(Order.STATUS_COMPLETE);
            }
        });

        tvCancelOrder.setOnClickListener(view -> {
            if (mOrder == null) return;

            // Nếu người dùng là quản trị viên, cho phép hủy đơn hàng ở mọi trạng thái
            if (DataStoreManager.getUser().isAdmin()) {
                cancelOrder();
            } else {
                // Nếu không phải quản trị viên, chỉ hủy đơn hàng khi trạng thái là mới
                if (mOrder.getStatus() == Order.STATUS_NEW) {
                    cancelOrder();
                } else {
                    showToastMessage(getString(R.string.msg_cannot_cancel_order));
                }
            }
        });
    }

    private void cancelOrder() {
        if (mOrder == null) return;

        Map<String, Object> map = new HashMap<>();
        map.put("status", Order.STATUS_CANCELLED);

        MyApplication.get(this).getOrderDatabaseReference()
                .child(String.valueOf(mOrder.getId()))
                .updateChildren(map, (error, ref) -> {
                    if (error == null) {
                        showToastMessage(getString(R.string.msg_order_cancelled));
                        finish(); // Quay về màn hình trước đó
                    } else {
                        showToastMessage(getString(R.string.msg_cancel_order_failed));
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
                dividerStep3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                imgStep4.setImageResource(R.drawable.ic_step_disable);

                // Không cho phép xác nhận "Đã nhận đơn hàng"
                isOrderArrived = false;
                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                tvTakeOrderMessage.setVisibility(View.GONE);

                // Hiển thị nút hủy đơn hàng
                tvCancelOrder.setVisibility(View.VISIBLE); // Bật nút hủy
                tvCancelOrder.setBackgroundResource(R.drawable.bg_button_enable_corner_16); // Kích hoạt giao diện nút hủy
                tvCancelOrder.setEnabled(true); // Kích hoạt chức năng nút hủy


                break;

            case Order.STATUS_PREPARE:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_enable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep3.setImageResource(R.drawable.ic_step_disable);
                dividerStep3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                imgStep4.setImageResource(R.drawable.ic_step_disable);

                isOrderArrived = false;
                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                tvTakeOrderMessage.setVisibility(View.GONE);
                tvCancelOrder.setVisibility(View.GONE);
                break;

            case Order.STATUS_DOING:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_enable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep3.setImageResource(R.drawable.ic_step_enable);
                dividerStep3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                imgStep4.setImageResource(R.drawable.ic_step_disable);

                isOrderArrived = false;
                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                tvTakeOrderMessage.setVisibility(View.GONE);
                tvCancelOrder.setVisibility(View.GONE);
                break;

            case Order.STATUS_DOINGCOMPLETE:
                imgStep1.setImageResource(R.drawable.ic_step_enable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep2.setImageResource(R.drawable.ic_step_enable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                imgStep3.setImageResource(R.drawable.ic_step_enable);
                dividerStep3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                imgStep4.setImageResource(R.drawable.ic_step_enable);

                isOrderArrived = true;
                tvTakeOrder.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                //tvTakeOrderMessage.setVisibility(View.VISIBLE);
                tvCancelOrder.setVisibility(View.GONE);
                // Tạo thông báo khi đơn hàng đã hoàn thành
                createOrderNotification(mOrder);
                break;

            case Order.STATUS_CANCELLED:
                imgStep1.setImageResource(R.drawable.ic_step_disable);
                dividerStep1.setBackgroundColor(ContextCompat.getColor(this, R.color.bgFilter));
                imgStep2.setImageResource(R.drawable.ic_step_disable);
                dividerStep2.setBackgroundColor(ContextCompat.getColor(this, R.color.bgFilter));
                imgStep3.setImageResource(R.drawable.ic_step_disable);
                dividerStep3.setBackgroundColor(ContextCompat.getColor(this, R.color.bgFilter));
                imgStep4.setImageResource(R.drawable.ic_step_disable);

                tvTakeOrder.setVisibility(View.GONE);
                tvCancelOrder.setVisibility(View.GONE);
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

    private void createOrderNotification(Order order) {
        // Lấy thông tin đơn hàng
        String orderId = String.valueOf(order.getId());  // Mã đơn hàng
        // Tạo thông báo
        String notificationMessage = "Đơn hàng mã " + orderId + " đã được giao thành công, vui lòng nhận đơn hàng";

        // Tạo đối tượng Notification với notificationId là ID khóa chính
        String notificationId = FirebaseDatabase.getInstance().getReference("notifications").push().getKey(); // Tạo ID tự động cho notification

        Notification notification = new Notification(notificationId, orderId, notificationMessage, false, null); // false: thông báo chưa được xem

        // Lưu thông báo vào Firebase, sử dụng notificationId làm khóa chính
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
        notificationsRef.child(notificationId).setValue(notification); // Dùng notificationId làm key chính


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
