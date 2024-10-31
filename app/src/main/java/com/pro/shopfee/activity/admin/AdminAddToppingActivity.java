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
import com.pro.shopfee.model.Topping;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AdminAddToppingActivity extends BaseActivity {

    private TextView tvToolbarTitle;
    private EditText edtName, edtPrice;
    private Button btnAddOrEdit;

    private boolean isUpdate;
    private Topping mTopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_topping);

        loadDataIntent();
        initUi();
        initView();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mTopping = (Topping) bundleReceived.get(Constant.KEY_INTENT_TOPPING_OBJECT);
        }
    }

    private void initUi() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        edtName = findViewById(R.id.edt_name);
        edtPrice = findViewById(R.id.edt_price);
        btnAddOrEdit = findViewById(R.id.btn_add_or_edit);

        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        btnAddOrEdit.setOnClickListener(v -> addOrEditTopping());
    }

    private void initView() {
        if (isUpdate) {
            tvToolbarTitle.setText(getString(R.string.label_update_topping));
            btnAddOrEdit.setText(getString(R.string.action_edit));

            edtName.setText(mTopping.getName());
            edtPrice.setText(String.valueOf(mTopping.getPrice()));
        } else {
            tvToolbarTitle.setText(getString(R.string.label_add_topping));
            btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditTopping() {
        String strName = edtName.getText().toString().trim();
        String strPrice = edtPrice.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strPrice)) {
            Toast.makeText(this, getString(R.string.msg_price_require), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update topping
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("price", Integer.parseInt(strPrice));

            MyApplication.get(this).getToppingDatabaseReference()
                    .child(String.valueOf(mTopping.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        Toast.makeText(this,
                                getString(R.string.msg_edit_topping_success), Toast.LENGTH_SHORT).show();
                        GlobalFunction.hideSoftKeyboard(this);
                    });
            return;
        }

        // Add topping
        showProgressDialog(true);
        long toppingId = System.currentTimeMillis();
        Topping topping = new Topping(toppingId, strName, Integer.parseInt(strPrice));
        MyApplication.get(this).getToppingDatabaseReference()
                .child(String.valueOf(toppingId)).setValue(topping, (error, ref) -> {
                    showProgressDialog(false);
                    edtName.setText("");
                    edtPrice.setText("");
                    GlobalFunction.hideSoftKeyboard(this);
                    Toast.makeText(this, getString(R.string.msg_add_topping_success), Toast.LENGTH_SHORT).show();
                });
    }
}