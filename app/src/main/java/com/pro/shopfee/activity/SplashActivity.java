package com.pro.shopfee.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

import com.pro.shopfee.R;
import com.pro.shopfee.activity.admin.AdminMainActivity;
import com.pro.shopfee.prefs.DataStoreManager;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.StringUtil;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(this::goToActivity, 2000);
    }

    private void goToActivity() {
        if (DataStoreManager.getUser() != null
                && !StringUtil.isEmpty(DataStoreManager.getUser().getEmail())) {
            if (DataStoreManager.getUser().isAdmin()) {
                GlobalFunction.startActivity(this, AdminMainActivity.class);
            } else {
                GlobalFunction.startActivity(this, MainActivity.class);
            }
        } else {
            GlobalFunction.startActivity(this, LoginActivity.class);
        }
        finish();
    }
}
