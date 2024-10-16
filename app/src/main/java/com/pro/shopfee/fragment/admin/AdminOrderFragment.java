package com.pro.shopfee.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.pro.shopfee.R;
import com.pro.shopfee.adapter.OrderPagerAdapter;
import com.pro.shopfee.model.TabOrder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderFragment extends Fragment {

    private View mView;
    private ViewPager2 viewPagerOrder;
    private TabLayout tabOrder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_admin_order, container, false);

        initUi();
        displayTabsOrder();

        return mView;
    }

    private void initUi() {
        viewPagerOrder = mView.findViewById(R.id.view_pager_order);
        viewPagerOrder.setUserInputEnabled(false);
        tabOrder = mView.findViewById(R.id.tab_order);
    }

    private void displayTabsOrder() {
        List<TabOrder> list = new ArrayList<>();
        list.add(new TabOrder(TabOrder.TAB_ORDER_PROCESS, getString(R.string.label_process)));
        list.add(new TabOrder(TabOrder.TAB_ORDER_DONE, getString(R.string.label_done)));
        if (getActivity() == null) return;
        viewPagerOrder.setOffscreenPageLimit(list.size());
        OrderPagerAdapter adapter = new OrderPagerAdapter(getActivity(), list);
        viewPagerOrder.setAdapter(adapter);
        new TabLayoutMediator(tabOrder, viewPagerOrder,
                (tab, position) -> tab.setText(list.get(position).getName().toLowerCase()))
                .attach();
    }
}
