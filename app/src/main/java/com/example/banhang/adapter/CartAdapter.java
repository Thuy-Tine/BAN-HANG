package com.example.banhang.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.banhang.R;
import com.example.banhang.model.CartItem;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItem> cartList;
    private final CartActionListener listener;

    public interface CartActionListener {
        void onIncreaseQuantity(CartItem item, int position);
        void onDecreaseQuantity(CartItem item, int position);
        void onRemoveItem(CartItem item, int position);
    }

    public CartAdapter(Context context, List<CartItem> cartList, CartActionListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartList.get(position);

        // 1. Gán dữ liệu sản phẩm
        if (cartItem.getProduct() != null) {
            holder.tvCartProductName.setText(cartItem.getProduct().getProductName());
            holder.tvCartProductPrice.setText(String.format(Locale.US, "$%.2f", cartItem.getProduct().getPrice()));

            Glide.with(context)
                    .load(cartItem.getProduct().getThumbnail())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgCartProduct);
        } else {
            holder.tvCartProductName.setText("Sản phẩm lỗi");
            holder.tvCartProductPrice.setText("$0.00");
            holder.imgCartProduct.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // 2. Gán số lượng
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // 3. Xử lý sự kiện nút bấm
        holder.btnIncreaseQty.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncreaseQuantity(cartItem, holder.getAdapterPosition());
            }
        });

        holder.btnDecreaseQty.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecreaseQuantity(cartItem, holder.getAdapterPosition());
            }
        });

        holder.btnRemoveItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(cartItem, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCartProduct;
        TextView tvCartProductName, tvCartProductDesc, tvCartProductPrice, tvQuantity;
        TextView btnDecreaseQty, btnIncreaseQty;
        ImageButton btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCartProduct = itemView.findViewById(R.id.imgCartProduct);
            tvCartProductName = itemView.findViewById(R.id.tvCartProductName);
            tvCartProductDesc = itemView.findViewById(R.id.tvCartProductDesc);
            tvCartProductPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);

            btnDecreaseQty = itemView.findViewById(R.id.btnDecreaseQty);
            btnIncreaseQty = itemView.findViewById(R.id.btnIncreaseQty);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
        }
    }
}