package com.pro.shopfee.fragment.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.admin.AdminAddDrinkActivity;
import com.pro.shopfee.adapter.admin.AdminDrinkAdapter;
import com.pro.shopfee.listener.IOnAdminManagerDrinkListener;
import com.pro.shopfee.model.Drink;
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

public class AdminDrinkFragment extends Fragment {

    private View mView;
    private List<Drink> mListDrink;
    private AdminDrinkAdapter mAdminDrinkAdapter;
    private ChildEventListener mChildEventListener;
    private EditText edtSearchName;
    private ImageView imgSearch;
    private FloatingActionButton btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_admin_drink, container, false);

        initUi();
        initView();
        initListener();
        loadListDrink("");

        return mView;
    }

    private void initUi() {
        edtSearchName = mView.findViewById(R.id.edt_search_name);
        imgSearch = mView.findViewById(R.id.img_search);
        btnAdd = mView.findViewById(R.id.btn_add);
    }

    private void initView() {
        RecyclerView rcvData = mView.findViewById(R.id.rcv_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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
        btnAdd.setOnClickListener(v -> onClickAddDrink());

        imgSearch.setOnClickListener(v -> searchDrink());

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDrink();
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
                    searchDrink();
                }
            }
        });
    }

    private void onClickAddDrink() {
        GlobalFunction.startActivity(getActivity(), AdminAddDrinkActivity.class);
    }

    private void onClickEditDrink(Drink drink) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_DRINK_OBJECT, drink);
        GlobalFunction.startActivity(getActivity(), AdminAddDrinkActivity.class, bundle);
    }

    private void deleteDrinkItem(Drink drink) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).getDrinkDatabaseReference()
                            .child(String.valueOf(drink.getId())).removeValue((error, ref) ->
                                    Toast.makeText(getActivity(),
                                            getString(R.string.msg_delete_drink_successfully),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchDrink() {
        String strKey = edtSearchName.getText().toString().trim();
        resetListDrink();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).getDrinkDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
        loadListDrink(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void resetListDrink() {
        if (mListDrink != null) {
            mListDrink.clear();
        } else {
            mListDrink = new ArrayList<>();
        }
    }

    public void loadListDrink(String keyword) {
        if (getActivity() == null) return;
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Drink drink = dataSnapshot.getValue(Drink.class);
                if (drink == null || mListDrink == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListDrink.add(0, drink);
                } else {
                    if (Utils.getTextSearch(drink.getName()).toLowerCase().trim()
                            .contains(Utils.getTextSearch(keyword).toLowerCase().trim())) {
                        mListDrink.add(0, drink);
                    }
                }
                if (mAdminDrinkAdapter != null) mAdminDrinkAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Drink drink = dataSnapshot.getValue(Drink.class);
                if (drink == null || mListDrink == null || mListDrink.isEmpty()) return;
                for (int i = 0; i < mListDrink.size(); i++) {
                    if (drink.getId() == mListDrink.get(i).getId()) {
                        mListDrink.set(i, drink);
                        break;
                    }
                }
                if (mAdminDrinkAdapter != null) mAdminDrinkAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Drink drink = dataSnapshot.getValue(Drink.class);
                if (drink == null || mListDrink == null || mListDrink.isEmpty()) return;
                for (Drink drinkObject : mListDrink) {
                    if (drink.getId() == drinkObject.getId()) {
                        mListDrink.remove(drinkObject);
                        break;
                    }
                }
                if (mAdminDrinkAdapter != null) mAdminDrinkAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(getActivity()).getDrinkDatabaseReference().addChildEventListener(mChildEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).getDrinkDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
    }
}
