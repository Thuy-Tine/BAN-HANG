package com.example.banhang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.banhang.R;
import com.example.banhang.model.CartItem;

import java.util.List;
import java.util.Locale;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private final Context context;
    private final List<CartItem> checkoutList;

    public CheckoutAdapter(Context context, List<CartItem> checkoutList) {
        this.context = context;
        this.checkoutList = checkoutList;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_checkout_item, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        CartItem item = checkoutList.get(position);

        if (item.getProduct() != null) {
            holder.tvCheckoutProductName.setText(item.getProduct().getProductName());
            holder.tvCheckoutProductPrice.setText(String.format(Locale.US, "$%.2f", item.getProduct().getPrice()));

            // Xử lý Category tĩnh (Hoặc lấy từ DB nếu Product model có chứa Category Name)
            String categoryName = "Cosmetic";
            if (item.getProduct().getCategoryId() == 1) categoryName = "Skincare";
            else if (item.getProduct().getCategoryId() == 2) categoryName = "Makeup";
            holder.tvCheckoutProductCategory.setText(categoryName);

            Glide.with(context)
                    .load(item.getProduct().getThumbnail())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgCheckoutProduct);
        }

        holder.tvCheckoutQuantity.setText("Qty: " + item.getQuantity());
    }

    @Override
    public int getItemCount() {
        return checkoutList != null ? checkoutList.size() : 0;
    }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCheckoutProduct;
        TextView tvCheckoutProductName, tvCheckoutProductCategory, tvCheckoutProductPrice, tvCheckoutQuantity;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCheckoutProduct = itemView.findViewById(R.id.imgCheckoutProduct);
            tvCheckoutProductName = itemView.findViewById(R.id.tvCheckoutProductName);
            tvCheckoutProductCategory = itemView.findViewById(R.id.tvCheckoutProductCategory);
            tvCheckoutProductPrice = itemView.findViewById(R.id.tvCheckoutProductPrice);
            tvCheckoutQuantity = itemView.findViewById(R.id.tvCheckoutQuantity);
        }
    }
}