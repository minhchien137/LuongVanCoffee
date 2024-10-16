package com.pro.shopfee.adapter.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pro.shopfee.fragment.admin.AdminCategoryFragment;
import com.pro.shopfee.fragment.admin.AdminDrinkFragment;
import com.pro.shopfee.fragment.admin.AdminOrderFragment;
import com.pro.shopfee.fragment.admin.AdminSettingsFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new AdminDrinkFragment();

            case 2:
                return new AdminOrderFragment();

            case 3:
                return new AdminSettingsFragment();

            default:
                return new AdminCategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
