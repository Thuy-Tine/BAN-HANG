package com.example.banhang.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.banhang.R;
import com.example.banhang.adapter.OrderHistoryAdapter;
import com.example.banhang.model.Order;
import com.example.banhang.sqlite.OrderDAO;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private TextView chipAll, chipProcessing, chipCompleted, chipCancelled;

    private OrderDAO orderDAO;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;


    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);


        SharedPreferences sessionPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE);
        currentUserId = sessionPref.getInt("current_user_id", -1);

        // Kiểm tra an toàn, nếu chưa đăng nhập hoặc mất session thì quay về Login
        if (currentUserId == -1) {
            Intent intent = new Intent(OrderHistoryActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }


        initViews();
        setupRecyclerView();
        setupEvents();


        loadOrdersByFilter("All");
    }

    private void initViews() {
        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        chipAll = findViewById(R.id.chipAll);
        chipProcessing = findViewById(R.id.chipProcessing);
        chipCompleted = findViewById(R.id.chipCompleted);
        chipCancelled = findViewById(R.id.chipCancelled);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        orderDAO = new OrderDAO(this);
        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(this, orderList);
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        rvOrderHistory.setAdapter(adapter);
    }

    private void setupEvents() {
        chipAll.setOnClickListener(v -> {
            updateChipUI(chipAll);
            loadOrdersByFilter("All");
        });

        chipProcessing.setOnClickListener(v -> {
            updateChipUI(chipProcessing);
            loadOrdersByFilter("Pending");
        });

        chipCompleted.setOnClickListener(v -> {
            updateChipUI(chipCompleted);
            loadOrdersByFilter("Completed");
        });

        chipCancelled.setOnClickListener(v -> {
            updateChipUI(chipCancelled);
            loadOrdersByFilter("Cancelled");
        });
    }

    private void loadOrdersByFilter(String status) {
        orderList.clear();
        // Truyền currentUserId động vào truy vấn
        orderList.addAll(orderDAO.getOrderHistory(currentUserId, status));
        adapter.notifyDataSetChanged();
    }

    private void updateChipUI(TextView selectedChip) {
        TextView[] chips = {chipAll, chipProcessing, chipCompleted, chipCancelled};
        for (TextView chip : chips) {
            if (chip == selectedChip) {
                // Style Active (Nền đậm, chữ trắng)
                chip.setBackgroundResource(R.drawable.bg_quantity_outline);
                chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#5A6E5A")));
                chip.setTextColor(Color.WHITE);
            } else {
                // Style Inactive (Nền trong suốt viền nhạt, chữ xám)
                chip.setBackgroundResource(R.drawable.bg_quantity_outline);
                chip.setBackgroundTintList(null); // Xóa màu nền
                chip.setTextColor(Color.parseColor("#434842"));
            }
        }
    }
}