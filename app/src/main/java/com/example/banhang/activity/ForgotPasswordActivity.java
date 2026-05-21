package com.example.banhang.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.banhang.R;
import com.example.banhang.sqlite.AccountDAO;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edtPhone, edtNewPassword;
    Button btnReset;
    AccountDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtPhone = findViewById(R.id.edtPhone);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        btnReset = findViewById(R.id.btnReset);

        dao = new AccountDAO(this);

        btnReset.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();

            if (phone.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dao.checkPhoneExists(phone)) {
                Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = dao.updatePassword(phone, newPass);

            if (updated) {
                Toast.makeText(this, "Password reset success", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Reset failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}