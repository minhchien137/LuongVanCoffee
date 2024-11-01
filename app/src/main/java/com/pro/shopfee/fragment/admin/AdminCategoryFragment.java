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
import com.pro.shopfee.activity.admin.AdminAddCategoryActivity;
import com.pro.shopfee.activity.admin.AdminDrinkByCategoryActivity;
import com.pro.shopfee.adapter.admin.AdminCategoryAdapter;
import com.pro.shopfee.listener.IOnAdminManagerCategoryListener;
import com.pro.shopfee.model.Category;
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

public class AdminCategoryFragment extends Fragment {

    private View mView;
    private List<Category> mListCategory;
    private AdminCategoryAdapter mAdminCategoryAdapter;
    private ChildEventListener mChildEventListener;
    private EditText edtSearchName;
    private ImageView imgSearch;
    private FloatingActionButton btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_admin_category, container, false);

        initUi();
        initView();
        initListener();
        loadListCategory("");

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
        mListCategory = new ArrayList<>();
        mAdminCategoryAdapter = new AdminCategoryAdapter(mListCategory, new IOnAdminManagerCategoryListener() {
            @Override
            public void onClickUpdateCategory(Category category) {
                onClickEditCategory(category);
            }

            @Override
            public void onClickDeleteCategory(Category category) {
                deleteCategoryItem(category);
            }

            @Override
            public void onClickItemCategory(Category category) {
                goToDrinkOfCategory(category);
            }
        });
        rcvData.setAdapter(mAdminCategoryAdapter);
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
        btnAdd.setOnClickListener(v -> onClickAddCategory());

        imgSearch.setOnClickListener(view1 -> searchCategory());

        edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCategory();
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
                    searchCategory();
                }
            }
        });
    }

    private void onClickAddCategory() {
        GlobalFunction.startActivity(getActivity(), AdminAddCategoryActivity.class);
    }

    private void goToDrinkOfCategory(Category category) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_CATEGORY_OBJECT, category);
        GlobalFunction.startActivity(getActivity(), AdminDrinkByCategoryActivity.class, bundle);
    }

    private void onClickEditCategory(Category category) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_CATEGORY_OBJECT, category);
        GlobalFunction.startActivity(getActivity(), AdminAddCategoryActivity.class, bundle);
    }

    private void deleteCategoryItem(Category category) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).getCategoryDatabaseReference()
                            .child(String.valueOf(category.getId())).removeValue((error, ref) ->
                                    Toast.makeText(getActivity(),
                                            getString(R.string.msg_delete_category_successfully),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchCategory() {
        String strKey = edtSearchName.getText().toString().trim();
        resetListCategory();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).getCategoryDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
        loadListCategory(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void resetListCategory() {
        if (mListCategory != null) {
            mListCategory.clear();
        } else {
            mListCategory = new ArrayList<>();
        }
    }

    public void loadListCategory(String keyword) {
        if (getActivity() == null) return;
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Category category = dataSnapshot.getValue(Category.class);
                if (category == null || mListCategory == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListCategory.add(0, category);
                } else {
                    if (Utils.getTextSearch(category.getName()).toLowerCase().trim()
                            .contains(Utils.getTextSearch(keyword).toLowerCase().trim())) {
                        mListCategory.add(0, category);
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Category category = dataSnapshot.getValue(Category.class);
                if (category == null || mListCategory == null || mListCategory.isEmpty()) return;
                for (int i = 0; i < mListCategory.size(); i++) {
                    if (category.getId() == mListCategory.get(i).getId()) {
                        mListCategory.set(i, category);
                        break;
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Category category = dataSnapshot.getValue(Category.class);
                if (category == null || mListCategory == null || mListCategory.isEmpty()) return;
                for (Category categoryObject : mListCategory) {
                    if (category.getId() == categoryObject.getId()) {
                        mListCategory.remove(categoryObject);
                        break;
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(getActivity()).getCategoryDatabaseReference().addChildEventListener(mChildEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).getCategoryDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
    }
}
