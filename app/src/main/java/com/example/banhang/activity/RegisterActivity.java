package com.example.banhang.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.banhang.R;
import com.example.banhang.model.Account;
import com.example.banhang.sqlite.AccountDAO;
import com.example.banhang.sqlite.UserDAO;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvSignIn;
    private ImageView imgBack;

    private AccountDAO accountDAO;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setEvents();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignIn);
        imgBack = findViewById(R.id.imgBack);

        accountDAO = new AccountDAO(this);
        userDAO = new UserDAO(this);
    }

    private void setEvents() {

        imgBack.setOnClickListener(v -> finish());


        tvSignIn.setOnClickListener(v -> finish());


        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim(); // Phone là tài khoản chính
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Kiểm tra các trường bắt buộc không được để trống
        if (fullName.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu xác nhận
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số điện thoại đã tồn tại trong database chưa
        if (accountDAO.checkPhoneExists(phone)) {
            Toast.makeText(this, "Phone number already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Account mới
        Account account = new Account();
        account.setSdt(phone);
        account.setPassword(password);

        long accountId = accountDAO.Register(account);

        if (accountId != -1) {

            userDAO.insertInitialUser(accountId, fullName, phone);

            Toast.makeText(this, "Account registered successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại trang Login
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}