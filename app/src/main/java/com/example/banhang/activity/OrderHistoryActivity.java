package com.example.banhang.activity;

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

    private final int CURRENT_USER_ID = 1; // Fix cứng để test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        initViews();
        setupRecyclerView();
        setupEvents();

        // Load mặc định tất cả đơn hàng khi vừa mở trang
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
            loadOrdersByFilter("Pending"); // Trong DB lưu là Pending
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
        orderList.addAll(orderDAO.getOrderHistory(CURRENT_USER_ID, status));
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