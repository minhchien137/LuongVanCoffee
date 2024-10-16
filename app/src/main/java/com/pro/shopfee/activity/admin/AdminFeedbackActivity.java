package com.pro.shopfee.activity.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.BaseActivity;
import com.pro.shopfee.adapter.admin.AdminFeedbackAdapter;
import com.pro.shopfee.model.Feedback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminFeedbackActivity extends BaseActivity {

    private List<Feedback> listFeedback;
    private AdminFeedbackAdapter adminFeedbackAdapter;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_feedback);

        initToolbar();
        initUi();
        loadListFeedbackFromFirebase();
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.feedback));
    }

    private void initUi() {
        RecyclerView rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        listFeedback = new ArrayList<>();
        adminFeedbackAdapter = new AdminFeedbackAdapter(listFeedback);
        rcvData.setAdapter(adminFeedbackAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListFeedbackFromFirebase() {
        showProgressDialog(true);
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                resetListFeedback();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback == null) return;
                    listFeedback.add(0, feedback);
                }
                if (adminFeedbackAdapter != null) adminFeedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).getFeedbackDatabaseReference()
                .addValueEventListener(mValueEventListener);
    }

    private void resetListFeedback() {
        if (listFeedback != null) {
            listFeedback.clear();
        } else {
            listFeedback = new ArrayList<>();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).getFeedbackDatabaseReference()
                    .removeEventListener(mValueEventListener);
        }
    }
}
