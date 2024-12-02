package com.pro.shopfee.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.TrackingOrderActivity;
import com.pro.shopfee.model.Notification;
import com.pro.shopfee.utils.Constant;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    private OnNotificationLongClickListener onNotificationLongClickListener;
   // private OnNotificationClickListener onClick;


    public NotificationAdapter(Context context, List<Notification> notificationList,
                               OnNotificationLongClickListener onNotificationLongClickListener
                               ) {
        this.context = context;
        this.notificationList = notificationList;
        this.onNotificationLongClickListener = onNotificationLongClickListener;

    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        if (notification == null) return;

        // Lọc thông báo cho Admin
        if (notification.getMessage().contains("đã đặt đơn hàng mã")) {
            holder.tvMessage.setText("Người dùng " + notification.getUserName() + " đã đặt đơn hàng mã " + notification.getOrderId());
        } else {
            // Lọc thông báo cho khách hàng
            holder.tvMessage.setText("Đơn hàng mã " + notification.getOrderId() + " đã được giao thành công, vui lòng nhận đơn hàng");
        }
        holder.tvMessage.setText(notification.getMessage());

        // Kiểm tra trạng thái "đã đọc" và cập nhật UI
        if (notification.getRead()) {
            holder.tvUnreadIndicator.setVisibility(View.GONE);  // Ẩn biểu tượng "1" nếu thông báo đã đọc
        } else {
            holder.tvUnreadIndicator.setVisibility(View.VISIBLE);  // Hiển thị biểu tượng "1" nếu thông báo chưa đọc
        }

        // Set up long click to delete notification
        holder.itemView.setOnLongClickListener(view -> {
            // Gọi callback để xử lý long click
            onNotificationLongClickListener.onLongClick(notification);
            return true;
        });


        // Set up regular click to open order tracking
        holder.itemView.setOnClickListener(view -> {
            // Đánh dấu thông báo là đã đọc
            if (!notification.getRead()) {
                notification.setRead(true);
                notifyItemChanged(position);  // Cập nhật lại UI
                // Cập nhật trạng thái trong Firebase hoặc nơi bạn lưu trữ thông báo
                updateNotificationAsRead(notification.getNotificationId());
            }
            Intent intent = new Intent(context, TrackingOrderActivity.class);
            intent.putExtra(Constant.ORDER_ID, Long.parseLong(notification.getOrderId())); // orderId được truyền từ Notification
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
    // Hàm cập nhật trạng thái "đã đọc" cho thông báo trong Firebase
    private void updateNotificationAsRead(String notificationId) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(notificationId);
        notificationRef.child("read").setValue(true);
    }


    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        public TextView tvMessage;
        public TextView tvUnreadIndicator; // Biểu tượng số 1

        public NotificationViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvUnreadIndicator = itemView.findViewById(R.id.tv_unread_indicator); // Biểu tượng số 1
        }
    }

    // Interface để truyền callback khi click vào thông báo
    public interface OnNotificationClickListener {
        void onClick(Notification notification);
    }

    // Interface để truyền callback khi long click vào thông báo (xóa)
    public interface OnNotificationLongClickListener {
        void onLongClick(Notification notification);
    }
}
