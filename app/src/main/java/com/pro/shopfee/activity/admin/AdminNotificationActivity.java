package com.pro.shopfee.activity.admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.BaseActivity;
import com.pro.shopfee.adapter.NotificationAdapter;
import com.pro.shopfee.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationActivity extends BaseActivity {

    private TextView tvToolbarTitle;
    private RecyclerView rvNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);

        initToolbar();
        rvNotifications = findViewById(R.id.recycler_view_notifications);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList,
                //notification -> updateNotificationAsRead(notification),  // Xử lý sự kiện click để đánh dấu đã đọc
                notification -> deleteNotification(notification) // Xử lý sự kiện long click để xóa thông báo
        );

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(notificationAdapter);

        getNotificationsFromFireBase();

    }

    private void getNotificationsFromFireBase() {
        FirebaseDatabase.getInstance().getReference("notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            // Lấy thông báo từ snapshot
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            if (notification != null) {
                                // Gán lại notificationId khi lấy dữ liệu
                                notification.setNotificationId(dataSnapshot.getKey());

                                // Nếu Firebase lưu "Read" thay vì "isRead", cập nhật đối tượng Notification
                                if (dataSnapshot.hasChild("Read")) {
                                    notification.setRead(dataSnapshot.child("Read").getValue(Boolean.class));
                                }

                                notificationList.add(notification);
                            }
                        }
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Firebase", "Lỗi khi tải dữ liệu");
                    }
                });
    }


    
    private void deleteNotification(Notification notification) {
        FirebaseDatabase.getInstance().getReference("notifications")
                .child(notification.getNotificationId())
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int position = notificationList.indexOf(notification);
                        if (position != -1) {
                            notificationList.remove(position);
                            notificationAdapter.notifyItemRemoved(position);
                            notificationAdapter.notifyItemRangeChanged(position, notificationList.size()); // Cập nhật danh sách còn lại
                        }
                    } else {
                        Toast.makeText(this, "Lỗi khi xóa thông báo!", Toast.LENGTH_SHORT).show();
                        Log.d("Notification", "Xóa không thành công");
                    }
                });
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.label_admin_notification));
    }
}
