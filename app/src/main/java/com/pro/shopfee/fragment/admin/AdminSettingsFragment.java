package com.pro.shopfee.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pro.shopfee.R;
import com.pro.shopfee.activity.LoginActivity;
import com.pro.shopfee.activity.admin.AdminFeedbackActivity;
import com.pro.shopfee.activity.admin.AdminToppingActivity;
import com.pro.shopfee.activity.admin.AdminVoucherActivity;
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

        mView.findViewById(R.id.tv_manage_topping).setOnClickListener(view -> onClickManageProductColor());
        mView.findViewById(R.id.tv_manage_voucher).setOnClickListener(view -> onClickManageVoucher());
        mView.findViewById(R.id.tv_manage_feedback).setOnClickListener(view -> onClickManageFeedback());
        mView.findViewById(R.id.tv_sign_out).setOnClickListener(view -> onClickSignOut());
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
