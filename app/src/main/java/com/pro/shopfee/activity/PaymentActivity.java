package com.pro.shopfee.activity;

import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.database.DrinkDatabase;
import com.pro.shopfee.event.DisplayCartEvent;
import com.pro.shopfee.event.OrderSuccessEvent;
import com.pro.shopfee.model.Notification;
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

                    // Tạo thông báo cho đơn hàng mới
                    createOrderNotification(mOrderBooking);

                    Bundle bundle = new Bundle();
                    bundle.putLong(Constant.ORDER_ID, mOrderBooking.getId());
                    GlobalFunction.startActivity(PaymentActivity.this,
                            ReceiptOrderActivity.class, bundle);

                    finish();
                });
    }

    private void createOrderNotification(Order order) {
        // Lấy thông tin đơn hàng
        String orderId = String.valueOf(order.getId());  // Mã đơn hàng
        String userName = order.getUserEmail();            // Tên khách hàng

        // Tạo thông báo
        String notificationMessage = "Người dùng " + userName + " đã đặt đơn hàng mã " + orderId;

        // Tạo đối tượng Notification với notificationId là ID khóa chính
        String notificationId = FirebaseDatabase.getInstance().getReference("notifications").push().getKey(); // Tạo ID tự động cho notification

        Notification notification = new Notification(notificationId, orderId, notificationMessage, false); // false: thông báo chưa được xem

        // Lưu thông báo vào Firebase, sử dụng notificationId làm khóa chính
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
        notificationsRef.child(notificationId).setValue(notification); // Dùng notificationId làm key chính
    }






}
