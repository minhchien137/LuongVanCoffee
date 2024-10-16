package com.pro.shopfee.activity.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.BaseActivity;
import com.pro.shopfee.adapter.admin.AdminDrinkAdapter;
import com.pro.shopfee.listener.IOnAdminManagerDrinkListener;
import com.pro.shopfee.model.Category;
import com.pro.shopfee.model.Drink;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;

import java.util.ArrayList;
import java.util.List;

public class AdminDrinkByCategoryActivity extends BaseActivity {

    private List<Drink> mListDrink;
    private AdminDrinkAdapter mAdminDrinkAdapter;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_drink_by_category);

        loadDataIntent();
        initView();
        loadListDrink();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            mCategory = (Category) bundleReceived.get(Constant.KEY_INTENT_CATEGORY_OBJECT);
        }
    }

    private void initView() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setText(mCategory.getName());

        RecyclerView rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        mListDrink = new ArrayList<>();
        mAdminDrinkAdapter = new AdminDrinkAdapter(mListDrink, new IOnAdminManagerDrinkListener() {
            @Override
            public void onClickUpdateDrink(Drink drink) {
                onClickEditDrink(drink);
            }

            @Override
            public void onClickDeleteDrink(Drink drink) {
                deleteDrinkItem(drink);
            }
        });
        rcvData.setAdapter(mAdminDrinkAdapter);
    }

    private void onClickEditDrink(Drink drink) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_DRINK_OBJECT, drink);
        GlobalFunction.startActivity(this, AdminAddDrinkActivity.class, bundle);
    }

    private void deleteDrinkItem(Drink drink) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> MyApplication.get(this).getDrinkDatabaseReference()
                        .child(String.valueOf(drink.getId())).removeValue((error, ref) ->
                                Toast.makeText(this,
                                        getString(R.string.msg_delete_drink_successfully),
                                        Toast.LENGTH_SHORT).show()))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void resetListDrink() {
        if (mListDrink != null) {
            mListDrink.clear();
        } else {
            mListDrink = new ArrayList<>();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadListDrink() {
        MyApplication.get(this).getDrinkDatabaseReference()
                .orderByChild("category_id").equalTo(mCategory.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        resetListDrink();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Drink drink = dataSnapshot.getValue(Drink.class);
                            if (drink == null) return;
                            mListDrink.add(0, drink);
                        }
                        if (mAdminDrinkAdapter != null) mAdminDrinkAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}