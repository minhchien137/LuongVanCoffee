package com.pro.shopfee.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.ChangePasswordActivity;
import com.pro.shopfee.activity.ContactActivity;
import com.pro.shopfee.activity.FeedbackActivity;
import com.pro.shopfee.activity.LoginActivity;
import com.pro.shopfee.activity.MainActivity;
import com.pro.shopfee.activity.NotificationActivity;
import com.pro.shopfee.fragment.admin.AdminSettingsFragment;
import com.pro.shopfee.model.Notification;
import com.pro.shopfee.prefs.DataStoreManager;
import com.pro.shopfee.utils.GlobalFunction;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private View mView;
    private LinearLayout layoutFeedback;
    private LinearLayout layoutContact;
    private LinearLayout layoutChangePassword;
    private LinearLayout layoutSignOut;
    private LinearLayout layoutNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account, container, false);

        initToolbar();
        initUi();
        initListener();
        setupNotification();

        return mView;
    }

    private void setupNotification() {
        // Lấy số lượng thông báo mới
        getNewNotificationCount(new AdminSettingsFragment.NotificationCountCallback() {
            @Override
            public void onCountReceived(int count) {
                updateNotificationCount(count);
            }
        });
    }

    // Hàm lấy số lượng thông báo mới từ Firebase
    private void getNewNotificationCount(AdminSettingsFragment.NotificationCountCallback callback) {
        FirebaseDatabase.getInstance().getReference("notifications")
                .addValueEventListener(new ValueEventListener() { // Sử dụng addValueEventListener để theo dõi sự thay đổi trong Firebase
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            if (notification != null && !notification.getRead()) { // Kiểm tra thông báo chưa đọc
                                if (notification.getMessage().contains("đã được giao thành công, vui lòng nhận đơn hàng")) {// Kiểm tra thông báo chưa đọc
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


    private void initToolbar() {
        ImageView imgToolbarBack = mView.findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = mView.findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> backToHomeScreen());
        tvToolbarTitle.setText(getString(R.string.nav_account));
    }

    private void backToHomeScreen() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) return;
        mainActivity.getViewPager2().setCurrentItem(0);
    }

    private void initUi() {
        TextView tvUsername = mView.findViewById(R.id.tv_username);
        tvUsername.setText(DataStoreManager.getUser().getEmail());
        layoutFeedback = mView.findViewById(R.id.layout_feedback);
        layoutContact = mView.findViewById(R.id.layout_contact);
        layoutChangePassword = mView.findViewById(R.id.layout_change_password);
        layoutSignOut = mView.findViewById(R.id.layout_sign_out);
        layoutNotification = mView.findViewById(R.id.layout_notification);
    }

    private void initListener() {
        layoutNotification.setOnClickListener(view ->
                GlobalFunction.startActivity(getActivity(), NotificationActivity.class));
        layoutFeedback.setOnClickListener(view ->
                GlobalFunction.startActivity(getActivity(), FeedbackActivity.class));
        layoutContact.setOnClickListener(view ->
                GlobalFunction.startActivity(getActivity(), ContactActivity.class));
        layoutChangePassword.setOnClickListener(view ->
                GlobalFunction.startActivity(getActivity(), ChangePasswordActivity.class));
        layoutSignOut.setOnClickListener(view -> onClickSignOut());
    }

    private void onClickSignOut() {
        if (getActivity() == null) return;

        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(getActivity(), LoginActivity.class);
        getActivity().finishAffinity();
    }


}
