package com.example.banhang.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.banhang.R;
import com.example.banhang.model.User;
import com.example.banhang.sqlite.UserDAO;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private TextView tvFullName, tvPhone;
    private LinearLayout btnMenuOrderHistory, btnMenuAddress, btnMenuLogout;
    private LinearLayout navHome, navShop, navCart, navProfile;
   TextView btnLogout;
    private UserDAO userDAO;
    private final int CURRENT_USER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserData();
        setupEvents();
    }

    private void initViews() {
        // Ánh xạ thông tin User
        imgAvatar = findViewById(R.id.imgAvatar);
        tvFullName = findViewById(R.id.tvFullName);
        tvPhone = findViewById(R.id.tvPhone);

        // Ánh xạ Menu
        btnMenuOrderHistory = findViewById(R.id.btnMenuOrderHistory);
        btnMenuAddress = findViewById(R.id.btnMenuAddress);
        btnMenuLogout = findViewById(R.id.btnMenuLogout);

        // Ánh xạ Bottom Navigation
        navHome = findViewById(R.id.navHome);
        navShop = findViewById(R.id.navShop);
        navCart = findViewById(R.id.navCart);
        navProfile = findViewById(R.id.navProfile);

        btnLogout = findViewById(R.id.btnlogout);
    }

    private void loadUserData() {
        userDAO = new UserDAO(this);
        User user = userDAO.getUserById(CURRENT_USER_ID);

        if (user != null) {

            tvFullName.setText(user.getFullName() != null ? user.getFullName() : "Trần Thị Thủy Tiên");
            tvPhone.setText(user.getPhone() != null ? user.getPhone() : "No phone number");

            // Load Avatar bằng Glide
            if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
                Glide.with(this)
                        .load(user.getAvatar())
                        .placeholder(android.R.drawable.ic_menu_camera)
                        .into(imgAvatar);
            }
        }
    }

    private void setupEvents() {

        btnMenuOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

        btnMenuAddress.setOnClickListener(v -> {
            Toast.makeText(this, "Shipping Address feature is coming soon", Toast.LENGTH_SHORT).show();
        });

        btnMenuLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            // TODO: Xóa SharedPreferences session tại đây sau này
        });


        navHome.setOnClickListener(v -> finish());

        navShop.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProductActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        navCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Xác nhận đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi ứng dụng không?")
                        .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(ProfileActivity.this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();


                                SharedPreferences preferences = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
                                 preferences.edit().clear().apply();


                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }

        });


        btnMenuAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = userDAO.getUserById(CURRENT_USER_ID);
                String currentAddress = "";
                if (user != null && user.getAddress() != null) {
                    currentAddress = user.getAddress();
                }


                final EditText etAddressInput = new EditText(ProfileActivity.this);
                etAddressInput.setText(currentAddress);
                etAddressInput.setHint("Enter your shipping address");
                etAddressInput.setPadding(50, 40, 50, 40); // Thiết lập khoảng cách viền cho đẹp

                // 3. Khởi tạo AlertDialog hiển thị lên màn hình
                new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Shipping Address")
                        .setMessage("View and update your default delivery address:")
                        .setView(etAddressInput) // Đưa EditText vào hộp thoại
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newAddress = etAddressInput.getText().toString().trim();
                                if (newAddress.isEmpty()) {
                                    Toast.makeText(ProfileActivity.this, "Address cannot be empty!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Thực hiện cập nhật xuống Database qua UserDAO
                                boolean isUpdated = userDAO.updateAddress(CURRENT_USER_ID, newAddress);
                                if (isUpdated) {
                                    Toast.makeText(ProfileActivity.this, "Address updated successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed to update address.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }
}