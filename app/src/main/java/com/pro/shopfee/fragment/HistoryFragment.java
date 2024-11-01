package com.pro.shopfee.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.pro.shopfee.R;
import com.pro.shopfee.activity.MainActivity;
import com.pro.shopfee.adapter.OrderPagerAdapter;
import com.pro.shopfee.model.TabOrder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private View mView;
    private ViewPager2 viewPagerOrder;
    private TabLayout tabOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_history, container, false);

        initToolbar();
        initUi();
        displayTabsOrder();

        return mView;
    }

}
