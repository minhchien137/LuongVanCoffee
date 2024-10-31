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


}