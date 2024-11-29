package com.pro.shopfee.fragment.admin;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pro.shopfee.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminReportFragment extends Fragment {

    private View mView;
    private Spinner spinnerYear, spinnerMonth;
    private TextView tvTotalOrders, tvCompletedOrders, tvCancelledOrders, tvProcessingOrders, tvRevenue;
    private ImageView imageSearch;


    // Khai báo biến selectedYear và selectedMonth để lưu giá trị người dùng chọn
    private int selectedYear = 2024;  // Mặc định chọn năm 2024
    private int selectedMonth = 1;    // Mặc định chọn tháng 1


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_admin_report, container, false);
        initUi();
        getReport();
        return mView;
    }

    private void initUi() {
        spinnerYear = mView.findViewById(R.id.spinner_year);
        spinnerMonth = mView.findViewById(R.id.spinner_month);
        tvTotalOrders = mView.findViewById(R.id.tv_total_orders);
        tvCompletedOrders = mView.findViewById(R.id.tv_completed_orders);
        tvCancelledOrders = mView.findViewById(R.id.tv_cancelled_orders);
        tvProcessingOrders = mView.findViewById(R.id.tv_processing_orders);
        tvRevenue = mView.findViewById(R.id.tv_revenue);
        imageSearch = mView.findViewById(R.id.image_search);  // Lấy ImageView của nút tìm kiếm

        // Dữ liệu cho Spinner Year (Năm)
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, getYearsList());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Dữ liệu cho Spinner Month (Tháng)
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, getMonthsList());
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedYear = Integer.parseInt(spinnerYear.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = Integer.parseInt(spinnerMonth.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        // Xử lý sự kiện nhấn nút "Search"
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi hàm để lấy dữ liệu báo cáo
                getReport();
            }
        });
    }

    // Phương thức trả về danh sách các năm
    private List<String> getYearsList() {
        List<String> years = new ArrayList<>();
        years.add("2024");
        years.add("2023");
        years.add("2022");
        return years;
    }

    // Phương thức trả về danh sách các tháng
    private List<String> getMonthsList() {
        List<String> months = new ArrayList<>();
        months.add("1");
        months.add("2");
        months.add("3");
        months.add("4");
        months.add("5");
        months.add("6");
        months.add("7");
        months.add("8");
        months.add("9");
        months.add("10");
        months.add("11");
        months.add("12");
        return months;
    }

    public void getReport() {
        // Thay đổi từ "orders" thành "order"
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("order");

        // Loại bỏ điều kiện năm, truy vấn toàn bộ dữ liệu
        orderRef.orderByChild("dateTime")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("Firebase", "Snapshot exists: " + snapshot.exists());
                        Log.d("Firebase", "Snapshot children count: " + snapshot.getChildrenCount());

                        if (!snapshot.exists()) {
                            Log.d("Firebase", "No data found");
                            updateUI(0, 0, 0, 0, 0); // Xóa các giá trị cũ
                            return;
                        }

                        int totalOrders = 0;
                        int completedOrders = 0;
                        int cancelledOrders = 0;
                        int processingOrders = 0;
                        double totalRevenue = 0; // Chỉ tính doanh thu của các đơn hàng thành công

                        // Duyệt qua các đơn hàng
                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                            Log.d("Firebase", "Processing order with key: " + orderSnapshot.getKey());

                            // Lấy thông tin dateTime từ trực tiếp trong đơn hàng
                            String dateTimeStr = orderSnapshot.child("dateTime").getValue(String.class);

                            // Chuyển đổi dateTime từ String thành Long (timestamp)
                            Long dateTime = null;
                            if (dateTimeStr != null) {
                                try {
                                    dateTime = Long.parseLong(dateTimeStr);
                                } catch (NumberFormatException e) {
                                    Log.e("Firebase", "Error parsing dateTime: " + e.getMessage());
                                }
                            }

                            // Lấy thông tin status từ trực tiếp trong đơn hàng (không phải từ nhánh drinks)
                            Integer status = orderSnapshot.child("status").getValue(Integer.class);

                            // Lấy thông tin total từ trực tiếp trong đơn hàng
                            Double total = orderSnapshot.child("total").getValue(Double.class);
                            if (total == null) total = 0.0;

                            // Log dữ liệu các trường
                            Log.d("Firebase", "Order DateTime: " + dateTime);
                            Log.d("Firebase", "Order Status: " + status);
                            Log.d("Firebase", "Order Total: " + total);

                            // Kiểm tra nếu dateTime hợp lệ
                            if (dateTime != null) {
                                // Chuyển đổi timestamp sang ngày tháng
                                String formattedDate = convertTimeStampToDate(dateTime);
                                String[] dateParts = formattedDate.split(",")[0].split("-");

                                int orderYear = Integer.parseInt(dateParts[2].trim());
                                int orderMonth = Integer.parseInt(dateParts[1].trim());

                                // Kiểm tra nếu năm và tháng trùng với những gì người dùng chọn
                                if (orderYear == selectedYear && orderMonth == selectedMonth) {
                                    totalOrders++;

                                    // Phân loại đơn hàng theo trạng thái
                                    if (status != null) {
                                        switch (status) {
                                            case 5: // Đơn hàng thành công
                                                completedOrders++;
                                                totalRevenue += total; // Cộng doanh thu chỉ cho đơn hàng thành công
                                                break;
                                            case -1: // Đơn hàng bị hủy
                                                cancelledOrders++;
                                                break;
                                            case 1: // Đơn hàng đang xử lý (trạng thái 1)
                                            case 2: // Đơn hàng đang xử lý (trạng thái 2)
                                            case 3: // Đơn hàng đang xử lý (trạng thái 3)
                                            case 4: // Đơn hàng đang xử lý (trạng thái 4)
                                                processingOrders++;
                                                break;
                                        }
                                    }
                                }
                            }
                        }

                        // Log kết quả thống kê
                        Log.d("Firebase", "Total Orders: " + totalOrders);
                        Log.d("Firebase", "Completed Orders: " + completedOrders);
                        Log.d("Firebase", "Cancelled Orders: " + cancelledOrders);
                        Log.d("Firebase", "Processing Orders: " + processingOrders);
                        Log.d("Firebase", "Total Revenue: " + totalRevenue);

                        // Cập nhật UI
                        updateUI(totalOrders, completedOrders, cancelledOrders, processingOrders, totalRevenue);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Error getting data", error.toException());
                    }
                });
    }

    private void updateUI(int totalOrders, int completedOrders, int cancelledOrders, int processingOrders, double totalRevenue) {
        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvCompletedOrders.setText(String.valueOf(completedOrders));
        tvCancelledOrders.setText(String.valueOf(cancelledOrders));
        tvProcessingOrders.setText(String.valueOf(processingOrders));
        tvRevenue.setText(String.format("%.3fđ", totalRevenue));  // Hiển thị tổng doanh thu
    }

    // Hàm chuyển đổi timestamp thành ngày tháng
    private String convertTimeStampToDate(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return android.text.format.DateFormat.format("dd-MM-yyyy", calendar).toString();
    }
}
