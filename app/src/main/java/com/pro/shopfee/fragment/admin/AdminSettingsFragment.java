package com.pro.shopfee.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.LoginActivity;
import com.pro.shopfee.activity.admin.AdminFeedbackActivity;
import com.pro.shopfee.activity.admin.AdminNotificationActivity;
import com.pro.shopfee.activity.admin.AdminToppingActivity;
import com.pro.shopfee.activity.admin.AdminVoucherActivity;
import com.pro.shopfee.model.Notification;
import com.pro.shopfee.prefs.DataStoreManager;
import com.pro.shopfee.utils.GlobalFunction;
import com.google.firebase.auth.FirebaseAuth;

public class AdminSettingsFragment extends Fragment {

    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_admin_settings, container, false);

        setupScreen();

        return mView;
    }

    private void setupScreen() {
        TextView tvEmail = mView.findViewById(R.id.tv_email);
        tvEmail.setText(DataStoreManager.getUser().getEmail());

        // Lấy số lượng thông báo mới
        getNewNotificationCount(new NotificationCountCallback() {
            @Override
            public void onCountReceived(int count) {
                updateNotificationCount(count);
            }
        });

        mView.findViewById(R.id.tv_manage_notification).setOnClickListener(view -> onClickManageNotification());
        mView.findViewById(R.id.tv_manage_topping).setOnClickListener(view -> onClickManageProductColor());
        mView.findViewById(R.id.tv_manage_voucher).setOnClickListener(view -> onClickManageVoucher());
        mView.findViewById(R.id.tv_manage_feedback).setOnClickListener(view -> onClickManageFeedback());
        mView.findViewById(R.id.tv_sign_out).setOnClickListener(view -> onClickSignOut());
    }

    // Hàm lấy số lượng thông báo mới từ Firebase
    private void getNewNotificationCount(NotificationCountCallback callback) {
        FirebaseDatabase.getInstance().getReference("notifications")
                .addValueEventListener(new ValueEventListener() { // Sử dụng addValueEventListener để theo dõi sự thay đổi trong Firebase
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            if (notification != null && !notification.getRead()) {
                                if (notification.getMessage().contains("đã đặt đơn hàng mã")) {// Kiểm tra thông báo chưa đọc
                                    count++; // Tăng số lượng thông báo chưa đọc
                                }
                            }
                        }
                        // Trả số lượng thông báo chưa đọc qua callback
                        callback.onCountReceived(count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu có
                    }
                });
    }


    // Cập nhật giao diện khi có thông báo mới
    private void updateNotificationCount(int count) {
        TextView tvNotificationCount = mView.findViewById(R.id.tv_notification_count);
        if (tvNotificationCount != null) {
            if (count > 0) {
                tvNotificationCount.setVisibility(View.VISIBLE);
                tvNotificationCount.setText(String.valueOf(count));
            } else {
                tvNotificationCount.setVisibility(View.GONE);
            }
        }
    }

    // Giao diện callback để trả về số lượng thông báo
    public interface NotificationCountCallback {
        void onCountReceived(int count);
    }


    private void onClickManageNotification() {
        GlobalFunction.startActivity(getActivity(), AdminNotificationActivity.class);
    }


    private void onClickManageProductColor() {
        GlobalFunction.startActivity(getActivity(), AdminToppingActivity.class);
    }

    private void onClickManageVoucher() {
        GlobalFunction.startActivity(getActivity(), AdminVoucherActivity.class);
    }

    private void onClickManageFeedback() {
        GlobalFunction.startActivity(getActivity(), AdminFeedbackActivity.class);
    }

    private void onClickSignOut() {
        if (getActivity() == null) return;
        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(getActivity(), LoginActivity.class);
        getActivity().finishAffinity();
    }


}