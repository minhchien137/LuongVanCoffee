package com.pro.shopfee.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.BaseActivity;
import com.pro.shopfee.adapter.admin.AdminSelectAdapter;
import com.pro.shopfee.model.Category;
import com.pro.shopfee.model.Drink;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddDrinkActivity extends BaseActivity {

    private TextView tvToolbarTitle;
    private EditText edtName, edtDescription, edtPrice, edtPromotion, edtImage, edtImageBanner;
    private CheckBox chbFeatured;
    private Spinner spnCategory;
    private Button btnAddOrEdit;

    private boolean isUpdate;
    private Drink mDrink;
    private Category mCategorySelected;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_drink);

        loadDataIntent();
        initUi();
        initData();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mDrink = (Drink) bundleReceived.get(Constant.KEY_INTENT_DRINK_OBJECT);
        }
    }

    private void initUi() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        edtName = findViewById(R.id.edt_name);
        edtDescription = findViewById(R.id.edt_description);
        edtPrice = findViewById(R.id.edt_price);
        edtPromotion = findViewById(R.id.edt_promotion);
        edtImage = findViewById(R.id.edt_image);
        edtImageBanner = findViewById(R.id.edt_image_banner);
        chbFeatured = findViewById(R.id.chb_featured);
        btnAddOrEdit = findViewById(R.id.btn_add_or_edit);
        spnCategory = findViewById(R.id.spn_category);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        btnAddOrEdit.setOnClickListener(v -> addOrEditDrink());
    }

    private void initData() {
        if (isUpdate) {
            tvToolbarTitle.setText(getString(R.string.label_update_drink));
            btnAddOrEdit.setText(getString(R.string.action_edit));

            edtName.setText(mDrink.getName());
            edtDescription.setText(mDrink.getDescription());
            edtPrice.setText(String.valueOf(mDrink.getPrice()));
            edtPromotion.setText(String.valueOf(mDrink.getSale()));
            edtImage.setText(mDrink.getImage());
            edtImageBanner.setText(mDrink.getBanner());
            chbFeatured.setChecked(mDrink.isFeatured());
        } else {
            tvToolbarTitle.setText(getString(R.string.label_add_drink));
            btnAddOrEdit.setText(getString(R.string.action_add));
        }
        loadListCategory();
    }

    private void loadListCategory() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category == null) return;
                    list.add(0, category);
                }
                AdminSelectAdapter adapter = new AdminSelectAdapter(AdminAddDrinkActivity.this,
                        R.layout.item_choose_option, list);
                spnCategory.setAdapter(adapter);
                spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mCategorySelected = adapter.getItem(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                if (mDrink != null && mDrink.getCategory_id() > 0) {
                    spnCategory.setSelection(getPositionSelected(list, mDrink.getCategory_id()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        MyApplication.get(this).getCategoryDatabaseReference()
                .addValueEventListener(mValueEventListener);
    }

    private int getPositionSelected(List<Category> list, long id) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getId()) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void addOrEditDrink() {
        String strName = edtName.getText().toString().trim();
        String strDescription = edtDescription.getText().toString().trim();
        String strPrice = edtPrice.getText().toString().trim();
        String strPromotion = edtPromotion.getText().toString().trim();
        String strImage = edtImage.getText().toString().trim();
        String strImageBanner = edtImageBanner.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strDescription)) {
            Toast.makeText(this, getString(R.string.msg_description_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strPrice)) {
            Toast.makeText(this, getString(R.string.msg_price_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImageBanner)) {
            Toast.makeText(this, getString(R.string.msg_image_banner_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strPromotion)) {
            strPromotion = "0";
        }

        // Update drink
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("description", strDescription);
            map.put("price", Integer.parseInt(strPrice));
            map.put("sale", Integer.parseInt(strPromotion));
            map.put("image", strImage);
            map.put("banner", strImageBanner);
            map.put("featured", chbFeatured.isChecked());
            map.put("category_id", mCategorySelected.getId());
            map.put("category_name", mCategorySelected.getName());

            MyApplication.get(this).getDrinkDatabaseReference()
                    .child(String.valueOf(mDrink.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        Toast.makeText(this,
                                getString(R.string.msg_edit_drink_success), Toast.LENGTH_SHORT).show();
                        GlobalFunction.hideSoftKeyboard(this);
                    });
            return;
        }

        // Add drink
        showProgressDialog(true);
        long drinkId = System.currentTimeMillis();
        Drink drink = new Drink();
        drink.setId(drinkId);
        drink.setName(strName);
        drink.setDescription(strDescription);
        drink.setPrice(Integer.parseInt(strPrice));
        drink.setSale(Integer.parseInt(strPromotion));
        drink.setImage(strImage);
        drink.setBanner(strImageBanner);
        drink.setFeatured(chbFeatured.isChecked());

        drink.setCategory_id(mCategorySelected.getId());
        drink.setCategory_name(mCategorySelected.getName());

        MyApplication.get(this).getDrinkDatabaseReference()
                .child(String.valueOf(drinkId)).setValue(drink, (error, ref) -> {
                    showProgressDialog(false);
                    edtName.setText("");
                    edtDescription.setText("");
                    edtPrice.setText("");
                    edtPromotion.setText("0");
                    edtImage.setText("");
                    edtImageBanner.setText("");
                    chbFeatured.setChecked(false);
                    spnCategory.setSelection(0);
                    GlobalFunction.hideSoftKeyboard(this);
                    Toast.makeText(this, getString(R.string.msg_add_drink_success), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).getCategoryDatabaseReference()
                    .removeEventListener(mValueEventListener);
        }
    }
}