package com.pro.shopfee.activity.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.BaseActivity;
import com.pro.shopfee.adapter.admin.AdminToppingAdapter;
import com.pro.shopfee.listener.IOnAdminManagerToppingListener;
import com.pro.shopfee.model.Topping;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.StringUtil;
import com.pro.shopfee.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminToppingActivity extends BaseActivity {

    private List<Topping> mListTopping;
    private AdminToppingAdapter mAdminToppingAdapter;
    private ChildEventListener mChildEventListener;
    private EditText edtSearchName;
    private ImageView imgSearch;
    private FloatingActionButton btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_topping);

        initToolbar();
        initUi();
        initView();
        initListener();
        loadListTopping("");
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> onBackPressed());
        tvToolbarTitle.setText(getString(R.string.manage_topping));
    }

    private void initUi() {
        edtSearchName = findViewById(R.id.edt_search_name);
        imgSearch = findViewById(R.id.img_search);
        btnAdd = findViewById(R.id.btn_add);
    }

    private void initView() {
        RecyclerView rcvData = findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvData.setLayoutManager(linearLayoutManager);
        mListTopping = new ArrayList<>();
        mAdminToppingAdapter = new AdminToppingAdapter(mListTopping, new IOnAdminManagerToppingListener() {
            @Override
            public void onClickUpdateTopping(Topping topping) {
                onClickEditTopping(topping);
            }

            @Override
            public void onClickDeleteTopping(Topping topping) {
                deleteToppingItem(topping);
            }
        });
        rcvData.setAdapter(mAdminToppingAdapter);
        rcvData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    btnAdd.hide();
                } else {
                    btnAdd.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        btnAdd.setOnClickListener(v -> onClickAddTopping());

        imgSearch.setOnClickListener(view1 -> searchTopping());

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTopping();
                return true;
            }
            return false;
        });

        edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    searchTopping();
                }
            }
        });
    }

    private void onClickAddTopping() {
        GlobalFunction.startActivity(this, AdminAddToppingActivity.class);
    }

    private void onClickEditTopping(Topping topping) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_TOPPING_OBJECT, topping);
        GlobalFunction.startActivity(this, AdminAddToppingActivity.class, bundle);
    }

    private void deleteToppingItem(Topping topping) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) ->
                        MyApplication.get(this).getToppingDatabaseReference()
                        .child(String.valueOf(topping.getId())).removeValue((error, ref) ->
                                Toast.makeText(this,
                                        getString(R.string.msg_delete_topping_successfully),
                                        Toast.LENGTH_SHORT).show()))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchTopping() {
        String strKey = edtSearchName.getText().toString().trim();
        resetListTopping();
        if (mChildEventListener != null) {
            MyApplication.get(this).getToppingDatabaseReference().removeEventListener(mChildEventListener);
        }
        loadListTopping(strKey);
        GlobalFunction.hideSoftKeyboard(this);
    }

    private void resetListTopping() {
        if (mListTopping != null) {
            mListTopping.clear();
        } else {
            mListTopping = new ArrayList<>();
        }
    }

    public void loadListTopping(String keyword) {
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Topping topping = dataSnapshot.getValue(Topping.class);
                if (topping == null || mListTopping == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListTopping.add(0, topping);
                } else {
                    if (Utils.getTextSearch(topping.getName()).toLowerCase().trim()
                            .contains(Utils.getTextSearch(keyword).toLowerCase().trim())) {
                        mListTopping.add(0, topping);
                    }
                }
                if (mAdminToppingAdapter != null) mAdminToppingAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Topping topping = dataSnapshot.getValue(Topping.class);
                if (topping == null || mListTopping == null || mListTopping.isEmpty()) return;
                for (int i = 0; i < mListTopping.size(); i++) {
                    if (topping.getId() == mListTopping.get(i).getId()) {
                        mListTopping.set(i, topping);
                        break;
                    }
                }
                if (mAdminToppingAdapter != null) mAdminToppingAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Topping topping = dataSnapshot.getValue(Topping.class);
                if (topping == null || mListTopping == null || mListTopping.isEmpty()) return;
                for (Topping toppingObject : mListTopping) {
                    if (topping.getId() == toppingObject.getId()) {
                        mListTopping.remove(toppingObject);
                        break;
                    }
                }
                if (mAdminToppingAdapter != null) mAdminToppingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(this).getToppingDatabaseReference().addChildEventListener(mChildEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChildEventListener != null) {
            MyApplication.get(this).getToppingDatabaseReference().removeEventListener(mChildEventListener);
        }
    }
}
