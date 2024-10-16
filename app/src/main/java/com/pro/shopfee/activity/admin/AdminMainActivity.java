package com.pro.shopfee.activity.admin;

import android.os.Bundle;

import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.BaseActivity;
import com.pro.shopfee.adapter.admin.AdminViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends BaseActivity {

    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        initUi();
        setupActivity();
    }

    private void initUi() {
        viewPager2 = findViewById(R.id.viewpager_2);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupActivity() {
        viewPager2.setUserInputEnabled(false);
        AdminViewPagerAdapter adminViewPagerAdapter = new AdminViewPagerAdapter(this);
        viewPager2.setAdapter(adminViewPagerAdapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigation.getMenu().findItem(R.id.nav_category).setChecked(true);
                        break;

                    case 1:
                        bottomNavigation.getMenu().findItem(R.id.nav_drink).setChecked(true);
                        break;

                    case 2:
                        bottomNavigation.getMenu().findItem(R.id.nav_order).setChecked(true);
                        break;

                    case 3:
                        bottomNavigation.getMenu().findItem(R.id.nav_settings).setChecked(true);
                        break;
                }
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_category) {
                viewPager2.setCurrentItem(0);
            } else if (id == R.id.nav_drink) {
                viewPager2.setCurrentItem(1);
            } else if (id == R.id.nav_order) {
                viewPager2.setCurrentItem(2);
            } else if (id == R.id.nav_settings) {
                viewPager2.setCurrentItem(3);
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finishAffinity())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }
}