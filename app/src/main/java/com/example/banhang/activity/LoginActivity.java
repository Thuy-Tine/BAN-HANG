package com.example.banhang.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.banhang.R;
import com.example.banhang.sqlite.AccountDAO;

public class LoginActivity extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnLogin;
    CheckBox cbRemember;
    TextView txtForgotPassword, txtRegister;

    AccountDAO accountDAO;

    String PREF_NAME = "LoginData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initViews();

        accountDAO = new AccountDAO(this);

        loadPreferences();

        setEvents();
    }

    // ================= INIT VIEWS =================
    private void initViews() {
        edtPhone = findViewById(R.id.edtSdt);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRemember = findViewById(R.id.ckRemember);
        txtForgotPassword = findViewById(R.id.txtForgot);
        txtRegister = findViewById(R.id.txtRegister);
    }

    // ================= EVENTS =================
    private void setEvents() {
 //login
        btnLogin.setOnClickListener(v -> {

            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,
                        "Please enter all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (accountDAO.CheckLogin(phone, password)) {

                Toast.makeText(this,
                        "Welcome to Tine Cosmetic!",
                        Toast.LENGTH_SHORT).show();

                savePreferences();

                 startActivity(new Intent(this, ProductActivity.class));
                finish();

            } else {
                Toast.makeText(this,
                        "Incorrect phone number or password!",
                        Toast.LENGTH_SHORT).show();
            }
        });
// forgot pasword
        txtForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        // REGISTER
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });






    }

    // ================= SAVE DATA =================
    private void savePreferences() {

        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (cbRemember.isChecked()) {
            editor.putString("phone", edtPhone.getText().toString());
            editor.putString("password", edtPassword.getText().toString());
            editor.putBoolean("remember", true);
        } else {
            editor.remove("phone");
            editor.remove("password");
            editor.putBoolean("remember", false);
        }

        editor.apply();
    }

    // ================= LOAD DATA =================
    private void loadPreferences() {

        SharedPreferences sp = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        boolean isRemembered = sp.getBoolean("remember", false);

        if (isRemembered) {
            edtPhone.setText(sp.getString("phone", ""));
            edtPassword.setText(sp.getString("password", ""));
            cbRemember.setChecked(true);
        } else {
            cbRemember.setChecked(false);
        }
    }

    // ================= AUTO SAVE ON PAUSE =================
    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }
}