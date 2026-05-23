package com.example.banhang.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.banhang.R;
import com.example.banhang.model.Order;

import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("#AL-" + order.getOrderId());
        holder.tvOrderDate.setText(order.getOrderDate());
        holder.tvOrderTotal.setText(String.format(Locale.US, "$%.2f", order.getTotalAmount()));
        holder.tvOrderItemCount.setText(order.getItemCount() + " items");

        // Đổi màu Badge tùy theo Status
        String status = order.getStatus();
        holder.tvOrderStatus.setText(status.toUpperCase());
        if (status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("Delivered")) {
            holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#D2E9D0")); // Xanh lá nhạt
            holder.tvOrderStatus.setTextColor(Color.parseColor("#0E1F10"));
        } else if (status.equalsIgnoreCase("Cancelled")) {
            holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#FFDAD6")); // Đỏ nhạt
            holder.tvOrderStatus.setTextColor(Color.parseColor("#93000A"));
        } else {
            holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#E4E2DF")); // Xám (Pending)
            holder.tvOrderStatus.setTextColor(Color.parseColor("#434842"));
        }

        Glide.with(context)
                .load(order.getThumbnail())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgOrderThumb);

        holder.btnViewDetails.setOnClickListener(v -> {
            Toast.makeText(context, "Mở chi tiết đơn #" + order.getOrderId(), Toast.LENGTH_SHORT).show();
            // TODO: Intent sang OrderDetailActivity
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderItemCount, tvOrderTotal;
        ImageView imgOrderThumb;
        View btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderItemCount = itemView.findViewById(R.id.tvOrderItemCount);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            imgOrderThumb = itemView.findViewById(R.id.imgOrderThumb);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}