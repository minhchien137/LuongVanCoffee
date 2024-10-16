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
        initView();
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

    private void initView() {
        if (isUpdate) {
            tvToolbarTitle.setText(getString(R.string.label_update_voucher));
            btnAddOrEdit.setText(getString(R.string.action_edit));

            edtDiscount.setText(String.valueOf(mVoucher.getDiscount()));
            edtMinimum.setText(String.valueOf(mVoucher.getMinimum()));
        } else {
            tvToolbarTitle.setText(getString(R.string.label_add_voucher));
            btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditVoucher() {
        String strDiscount = edtDiscount.getText().toString().trim();
        String strMinimum = edtMinimum.getText().toString().trim();

        if (StringUtil.isEmpty(strDiscount) || Integer.parseInt(strDiscount) <= 0) {
            Toast.makeText(this, getString(R.string.msg_discount_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strMinimum)) {
            strMinimum = "0";
        }
        // Update voucher
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("discount", Integer.parseInt(strDiscount));
            map.put("minimum", Integer.parseInt(strMinimum));

            MyApplication.get(this).getVoucherDatabaseReference()
                    .child(String.valueOf(mVoucher.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        Toast.makeText(this,
                                getString(R.string.msg_edit_voucher_success), Toast.LENGTH_SHORT).show();
                        GlobalFunction.hideSoftKeyboard(this);
                    });
            return;
        }

        // Add voucher
        showProgressDialog(true);
        long voucherId = System.currentTimeMillis();
        Voucher voucher = new Voucher(voucherId, Integer.parseInt(strDiscount), Integer.parseInt(strMinimum));
        MyApplication.get(this).getVoucherDatabaseReference()
                .child(String.valueOf(voucherId)).setValue(voucher, (error, ref) -> {
                    showProgressDialog(false);
                    edtDiscount.setText("");
                    edtMinimum.setText("");
                    GlobalFunction.hideSoftKeyboard(this);
                    Toast.makeText(this, getString(R.string.msg_add_voucher_success), Toast.LENGTH_SHORT).show();
                });
    }
}