package com.pro.shopfee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.adapter.AddressAdapter;
import com.pro.shopfee.event.AddressSelectedEvent;
import com.pro.shopfee.model.Address;
import com.pro.shopfee.prefs.DataStoreManager;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.StringUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends BaseActivity {

    private List<Address> listAddress;
    private AddressAdapter addressAdapter;
    private long addressSelectedId;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        loadDataIntent();
        initToolbar();
        initUi();
        loadListAddressFromFirebase();
    }

    private void loadDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        addressSelectedId = bundle.getLong(Constant.ADDRESS_ID, 0);
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.address_title));
    }

    private void initUi() {
        RecyclerView rcvAddress = findViewById(R.id.rcv_address);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvAddress.setLayoutManager(linearLayoutManager);
        listAddress = new ArrayList<>();
        addressAdapter = new AddressAdapter(listAddress, this::handleClickAddress);
        rcvAddress.setAdapter(addressAdapter);

        Button btnAddAddress = findViewById(R.id.btn_add_address);
        btnAddAddress.setOnClickListener(view -> onClickAddAddress());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadListAddressFromFirebase() {
        showProgressDialog(true);
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showProgressDialog(false);
                resetListAddress();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Address address = dataSnapshot.getValue(Address.class);
                    if (address != null) {
                        listAddress.add(0, address);
                    }
                }

                if (addressSelectedId > 0 && listAddress != null && !listAddress.isEmpty()) {
                    for (Address address : listAddress) {
                        if (address.getId() == addressSelectedId) {
                            address.setSelected(true);
                            break;
                        }
                    }
                }

                if (addressAdapter != null) addressAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showProgressDialog(false);
                showToastMessage(getString(R.string.msg_get_date_error));
            }
        };
        MyApplication.get(this).getAddressDatabaseReference()
                .orderByChild("userEmail").equalTo(DataStoreManager.getUser().getEmail())
                .addValueEventListener(mValueEventListener);
    }

    private void resetListAddress() {
        if (listAddress != null) {
            listAddress.clear();
        } else {
            listAddress = new ArrayList<>();
        }
    }

    private void handleClickAddress(Address address) {
        EventBus.getDefault().post(new AddressSelectedEvent(address));
        finish();
    }

    @SuppressLint("InflateParams, MissingInflatedId")
    public void onClickAddAddress() {
        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_add_address, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // init ui
        TextView edtName = viewDialog.findViewById(R.id.edt_name);
        TextView edtPhone = viewDialog.findViewById(R.id.edt_phone);
        TextView edtAddress = viewDialog.findViewById(R.id.edt_address);
        TextView tvCancel = viewDialog.findViewById(R.id.tv_cancel);
        TextView tvAdd = viewDialog.findViewById(R.id.tv_add);

        // Set listener
        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        tvAdd.setOnClickListener(v -> {
            String strName = edtName.getText().toString().trim();
            String strPhone = edtPhone.getText().toString().trim();
            String strAddress = edtAddress.getText().toString().trim();

            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strPhone) || StringUtil.isEmpty(strAddress)) {
                GlobalFunction.showToastMessage(this, getString(R.string.message_enter_infor));
            } else {
                long id = System.currentTimeMillis();
                Address address = new Address(id, strName, strPhone, strAddress, DataStoreManager.getUser().getEmail());
                MyApplication.get(this).getAddressDatabaseReference()
                        .child(String.valueOf(id))
                        .setValue(address, (error1, ref1) -> {
                            GlobalFunction.showToastMessage(this,
                                    getString(R.string.msg_add_address_success));
                            GlobalFunction.hideSoftKeyboard(this);
                            bottomSheetDialog.dismiss();
                        });
            }
        });

        bottomSheetDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mValueEventListener != null) {
            MyApplication.get(this).getAddressDatabaseReference()
                    .removeEventListener(mValueEventListener);
        }
    }
}
