package com.pro.shopfee.activity.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.BaseActivity;
import com.pro.shopfee.model.Voucher;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AdminAddVoucherActivity extends BaseActivity {

    private TextView tvToolbarTitle;
    private EditText edtDiscount, edtMinimum;
    private Button btnAddOrEdit;

    private boolean isUpdate;
    private Voucher mVoucher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_voucher);

        loadDataIntent();
        initUi();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mVoucher = (Voucher) bundleReceived.get(Constant.KEY_INTENT_VOUCHER_OBJECT);
        }
    }

    private void initUi() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        edtDiscount = findViewById(R.id.edt_discount);
        edtMinimum = findViewById(R.id.edt_minimum);
        btnAddOrEdit = findViewById(R.id.btn_add_or_edit);

        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        btnAddOrEdit.setOnClickListener(v -> addOrEditVoucher());
    }


}