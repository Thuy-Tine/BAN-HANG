package com.example.banhang.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banhang.R;
import com.example.banhang.adapter.ProductAdapter;
import com.example.banhang.sqlite.CartDAO;
import com.example.banhang.sqlite.ProductDAO;
import com.example.banhang.model.Product;

import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private TextView tvChipAll, tvChipSkincare, tvChipMakeup;
    private RecyclerView rvProducts;
    private LinearLayout navHome, navCart, navProfile;
    private ProductDAO productDAO;
    private ProductAdapter productAdapter;
    private int currentCategoryId = -1; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        initViews();
        setupRecyclerView();
        setupEvents();

        productDAO = new ProductDAO(this);
        loadData();
    }

    private void initViews() {
        tvChipAll = findViewById(R.id.tvChipAll);
        tvChipSkincare = findViewById(R.id.tvChipSkincare);
        tvChipMakeup = findViewById(R.id.tvChipMakeup);
        rvProducts = findViewById(R.id.rvProducts);

        navHome = findViewById(R.id.navHome);

        navCart = findViewById(R.id.navCart);
        navProfile = findViewById(R.id.navProfile);
    }

    // Trong ProductActivity.java
    private void setupRecyclerView() {

        productAdapter = new ProductAdapter(this,
                product -> { /* Click xem chi tiết */ },
                product -> {
                    // LOGIC THÊM VÀO GIỎ HÀNG
                    CartDAO cartDAO = new CartDAO(this);
                    int userId = 1; // Lấy từ Session/Login
                    int cartId = cartDAO.getOrCreateCartId(userId);

                    cartDAO.addToCart(cartId, product.getProductId());
                    Toast.makeText(this, "Added: " + product.getProductName(), Toast.LENGTH_SHORT).show();
                }
        );
        rvProducts.setAdapter(productAdapter);
    }

    private void setupEvents() {
        tvChipAll.setOnClickListener(v -> {
            updateChipUI(tvChipAll);
            currentCategoryId = -1;
            loadData();
        });

        tvChipSkincare.setOnClickListener(v -> {
            updateChipUI(tvChipSkincare);
            currentCategoryId = 1;
            loadData();
        });

        tvChipMakeup.setOnClickListener(v -> {
            updateChipUI(tvChipMakeup);
            currentCategoryId = 2;
            loadData();
        });

        navHome.setOnClickListener(v -> finish());
        navCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });




    }

    private void loadData() {
        List<Product> products = productDAO.getAllProducts(currentCategoryId);
        productAdapter.setProducts(products);
    }

    private void updateChipUI(TextView selectedChip) {
        TextView[] chips = {tvChipAll, tvChipSkincare, tvChipMakeup};
        for (TextView chip : chips) {
            if (chip == selectedChip) {
                chip.setBackgroundResource(R.drawable.bg_chip_active);
                chip.setTextColor(Color.parseColor("#D9F0D7"));
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_inactive);

                chip.setTextColor(Color.parseColor("#2C423F"));
            }
        }
    }
}