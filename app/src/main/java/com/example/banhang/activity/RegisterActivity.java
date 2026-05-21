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
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvSignIn;
    private ImageView imgBack;

    private AccountDAO accountDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        accountDAO = new AccountDAO(this);
        setEvents();
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvSignIn = findViewById(R.id.tvSignIn);
        imgBack = findViewById(R.id.imgBack);
    }

    private void setEvents() {
        // Back button event
        imgBack.setOnClickListener(v -> finish());

        // Navigate to Sign In screen event
        tvSignIn.setOnClickListener(v -> finish());

        // Handle account registration event
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String fullName = etFullName.getText().toString().trim();
        String emailOrPhone = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Check for empty input fields
        if (fullName.isEmpty() || emailOrPhone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if confirm password matches
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if phone number/email already exists in the database
        if (accountDAO.checkPhoneExists(emailOrPhone)) {
            Toast.makeText(this, "Account already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Account object and assign values
        Account account = new Account();
        account.setSdt(emailOrPhone);
        account.setPassword(password);

        // Call the registration function to save in SQLite
        boolean isSuccess = accountDAO.Register(account);

        if (isSuccess) {
            Toast.makeText(this, "Account registered successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}