package com.pro.shopfee.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.pro.shopfee.R;
import com.pro.shopfee.activity.admin.AdminMainActivity;
import com.pro.shopfee.model.User;
import com.pro.shopfee.prefs.DataStoreManager;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends BaseActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnRegister;
    private LinearLayout layoutLogin;
    private RadioButton rdbAdmin, rdbUser;
    private boolean isEnableButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUi();
        initListener();
    }

    private void initUi() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnRegister = findViewById(R.id.btn_register);
        layoutLogin = findViewById(R.id.layout_login);
        rdbAdmin = findViewById(R.id.rdb_admin);
        rdbUser = findViewById(R.id.rdb_user);
    }

    private void initListener() {
        rdbUser.setChecked(true);
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    edtEmail.setBackgroundResource(R.drawable.bg_white_corner_16_border_main);
                } else {
                    edtEmail.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray);
                }

                String strPassword = edtPassword.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strPassword)) {
                    isEnableButtonRegister = true;
                    btnRegister.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                } else {
                    isEnableButtonRegister = false;
                    btnRegister.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                }
            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    edtPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_main);
                } else {
                    edtPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray);
                }

                String strEmail = edtEmail.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strEmail)) {
                    isEnableButtonRegister = true;
                    btnRegister.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                } else {
                    isEnableButtonRegister = false;
                    btnRegister.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                }
            }
        });

        layoutLogin.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> onClickValidateRegister());
    }

    private void onClickValidateRegister() {
        if (!isEnableButtonRegister) return;

        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        if (StringUtil.isEmpty(strEmail)) {
            showToastMessage(getString(R.string.msg_email_require));
        } else if (StringUtil.isEmpty(strPassword)) {
            showToastMessage(getString(R.string.msg_password_require));
        } else if (!StringUtil.isValidEmail(strEmail)) {
            showToastMessage(getString(R.string.msg_email_invalid));
        } else {
            if (rdbAdmin.isChecked()) {
                if (!strEmail.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                    Toast.makeText(this, getString(R.string.msg_email_invalid_admin), Toast.LENGTH_SHORT).show();
                } else {
                    registerUserFirebase(strEmail, strPassword);
                }
            } else {
                if (strEmail.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                    Toast.makeText(this, getString(R.string.msg_email_invalid_user), Toast.LENGTH_SHORT).show();
                } else {
                    registerUserFirebase(strEmail, strPassword);
                }
            }
        }
    }

    private void registerUserFirebase(String email, String password) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            User userObject = new User(user.getEmail(), password);
                            if (user.getEmail() != null && user.getEmail().contains(Constant.ADMIN_EMAIL_FORMAT)) {
                                userObject.setAdmin(true);
                            }
                            DataStoreManager.setUser(userObject);
                            goToMainActivity();
                        }
                    } else {
                        showToastMessage(getString(R.string.msg_register_error));
                    }
                });
    }

    private void goToMainActivity() {
        if (DataStoreManager.getUser().isAdmin()) {
            GlobalFunction.startActivity(this, AdminMainActivity.class);
        } else {
            GlobalFunction.startActivity(this, MainActivity.class);
        }
        finishAffinity();
    }
}