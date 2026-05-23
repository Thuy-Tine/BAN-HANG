package com.example.banhang.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.banhang.R;
import com.example.banhang.adapter.CheckoutAdapter;
import com.example.banhang.model.CartItem;
import com.example.banhang.model.User;
import com.example.banhang.sqlite.CartDAO;
import com.example.banhang.sqlite.OrderDAO;
import com.example.banhang.sqlite.UserDAO;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.net.Uri;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvCheckoutItems;
    private TextView tvCheckoutTotal;
    private RadioGroup rgPayment;
    private RadioButton rbQR;
    private LinearLayout layoutQR;
    private MaterialButton btnCompleteOrder;

    private CartDAO cartDAO;
    private OrderDAO orderDAO;
    private List<CartItem> checkoutList;
    private CheckoutAdapter checkoutAdapter;

    private int currentCartId;
    private double totalAmount = 0;
    private final int CURRENT_USER_ID = 1;
    private TextView tvCheckoutNamePhone, tvCheckoutAddress;
    private String userShippingAddress = "";
    private String userPhoneContact = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        loadData();
        setupEvents();
    }

    private void initViews() {
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);
        rgPayment = findViewById(R.id.rgPayment);
        rbQR = findViewById(R.id.rbQR);
        layoutQR = findViewById(R.id.layoutQR);
        btnCompleteOrder = findViewById(R.id.btnCompleteOrder);
        tvCheckoutNamePhone = findViewById(R.id.tvCheckoutNamePhone);
        tvCheckoutAddress = findViewById(R.id.tvCheckoutAddress);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadData() {
        cartDAO = new CartDAO(this);
        orderDAO = new OrderDAO(this);
        checkoutList = new ArrayList<>();

        currentCartId = cartDAO.getOrCreateCartId(CURRENT_USER_ID);
        checkoutList.addAll(cartDAO.getCartItems(currentCartId));

        checkoutAdapter = new CheckoutAdapter(this, checkoutList);
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(checkoutAdapter);
        rvCheckoutItems.setNestedScrollingEnabled(false);

        for (CartItem item : checkoutList) {
            if (item.getProduct() != null) {
                totalAmount += (item.getProduct().getPrice() * item.getQuantity());
            }
        }
        tvCheckoutTotal.setText(String.format(Locale.US, "$%.2f", totalAmount));
    }
    private void loadUserInfo() {
        UserDAO userDAO = new UserDAO(this);
        User user = userDAO.getUserById(CURRENT_USER_ID);

        if (user != null) {
            String name = user.getFullName() != null ? user.getFullName() : "Customer";
            userPhoneContact = user.getPhone() != null ? user.getPhone() : "No Phone";

            tvCheckoutNamePhone.setText(name + " - " + userPhoneContact);

            if (user.getAddress() != null && !user.getAddress().trim().isEmpty()) {
                userShippingAddress = user.getAddress();
                tvCheckoutAddress.setText(userShippingAddress);
                tvCheckoutAddress.setTextColor(android.graphics.Color.parseColor("#434842")); // Màu chữ xám đen bình thường
            } else {
                tvCheckoutAddress.setText("Vui lòng cập nhật địa chỉ trong Profile!");
                tvCheckoutAddress.setTextColor(android.graphics.Color.parseColor("#BA1A1A")); // Báo đỏ nếu chưa có địa chỉ
            }
        }
    }

    private void setupEvents() {
        rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbQR) {
                layoutQR.setVisibility(View.VISIBLE);

                // Gọi hàm load ảnh QR khi layout hiển thị lên
                loadVietQR();

            } else {
                layoutQR.setVisibility(View.GONE);
            }
        });

        btnCompleteOrder.setOnClickListener(v -> {
            if (checkoutList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // CHECK: Phải có địa chỉ mới cho thanh toán
            if (userShippingAddress.isEmpty()) {
                Toast.makeText(this, "Please set your shipping address in Profile first!", Toast.LENGTH_LONG).show();
                return;
            }

            String selectedMethod = rbQR.isChecked() ? "QR" : "COD";


            boolean isSuccess = orderDAO.processCheckout(
                    CURRENT_USER_ID,
                    currentCartId,
                    totalAmount,
                    userShippingAddress,
                    userPhoneContact,
                    selectedMethod,
                    checkoutList
            );

            if (isSuccess) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();


                Intent intent = new Intent(CheckoutActivity.this, ProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Transaction failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });



        btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkoutList.isEmpty()) {
                    Toast.makeText(CheckoutActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }


                UserDAO userDAO = new UserDAO(CheckoutActivity.this);
                User user = userDAO.getUserById(CURRENT_USER_ID);

                String shippingAddress = "";
                String phoneContact = "0987654321";

                if (user != null && user.getAddress() != null && !user.getAddress().trim().isEmpty()) {
                    shippingAddress = user.getAddress();
                } else {

                    Toast.makeText(CheckoutActivity.this, "Please set your shipping address in Profile first!", Toast.LENGTH_LONG).show();
                    return;
                }


                String selectedMethod = rbQR.isChecked() ? "QR" : "COD";


                boolean isSuccess = orderDAO.processCheckout(
                        CURRENT_USER_ID,
                        currentCartId,
                        totalAmount,
                        shippingAddress,
                        phoneContact,
                        selectedMethod,
                        checkoutList
                );

                if (isSuccess) {
                    Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CheckoutActivity.this, ProductActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CheckoutActivity.this, "Transaction failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadVietQR() {

        String bankId = "Agribank";
        String accountNo = "6706272170988";


        String accountName = Uri.encode("TINE COSMETIC");
        String addInfo = Uri.encode("Thanh toan don hang");


        int amountVND = (int) (totalAmount * 25000);


        String qrUrl = String.format(Locale.US,
                "https://img.vietqr.io/image/%s-%s-print.png?amount=%d&addInfo=%s&accountName=%s",
                bankId, accountNo, amountVND, addInfo, accountName);


        ImageView imgQRCode = findViewById(R.id.imgQRCode);
        Glide.with(this)
                .load(qrUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(imgQRCode);
    }

}