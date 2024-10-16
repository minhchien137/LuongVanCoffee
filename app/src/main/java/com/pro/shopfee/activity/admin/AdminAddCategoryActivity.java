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
import com.pro.shopfee.model.Category;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AdminAddCategoryActivity extends BaseActivity {

    private TextView tvToolbarTitle;
    private EditText edtName;
    private Button btnAddOrEdit;

    private boolean isUpdate;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_category);

        loadDataIntent();
        initUi();
        initView();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mCategory = (Category) bundleReceived.get(Constant.KEY_INTENT_CATEGORY_OBJECT);
        }
    }

    private void initUi() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        edtName = findViewById(R.id.edt_name);
        btnAddOrEdit = findViewById(R.id.btn_add_or_edit);

        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        btnAddOrEdit.setOnClickListener(v -> addOrEditCategory());
    }

    private void initView() {
        if (isUpdate) {
            tvToolbarTitle.setText(getString(R.string.label_update_category));
            btnAddOrEdit.setText(getString(R.string.action_edit));

            edtName.setText(mCategory.getName());
        } else {
            tvToolbarTitle.setText(getString(R.string.label_add_category));
            btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditCategory() {
        String strName = edtName.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update category
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);

            MyApplication.get(this).getCategoryDatabaseReference()
                    .child(String.valueOf(mCategory.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        Toast.makeText(this,
                                getString(R.string.msg_edit_category_success), Toast.LENGTH_SHORT).show();
                        GlobalFunction.hideSoftKeyboard(this);
                    });
            return;
        }

        // Add category
        showProgressDialog(true);
        long categoryId = System.currentTimeMillis();
        Category category = new Category(categoryId, strName);
        MyApplication.get(this).getCategoryDatabaseReference()
                .child(String.valueOf(categoryId)).setValue(category, (error, ref) -> {
                    showProgressDialog(false);
                    edtName.setText("");
                    GlobalFunction.hideSoftKeyboard(this);
                    Toast.makeText(this, getString(R.string.msg_add_category_success), Toast.LENGTH_SHORT).show();
                });
    }
}