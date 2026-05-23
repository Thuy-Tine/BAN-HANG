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

    // Interface bắt sự kiện để Activity xử lý Database và Tổng tiền
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
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CartItem cartItem = cartList.get(position);

        if (cartItem.getProduct() != null) {
            // Hiển thị tên và giá sản phẩm
            holder.tvCartProductName.setText(cartItem.getProduct().getProductName());
            holder.tvCartProductPrice.setText(String.format(Locale.US, "$%.2f", cartItem.getProduct().getPrice()));

            // Load hình ảnh bằng Glide
            Glide.with(context)
                    .load(cartItem.getProduct().getThumbnail())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imgCartProduct);
        }

        // Hiển thị số lượng
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        // --- CÁC SỰ KIỆN NÚT BẤM ---

        // Nút Tăng Số Lượng
        holder.btnIncreaseQty.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncreaseQuantity(cartItem, position);
            }
        });

        // Nút Giảm Số Lượng
        holder.btnDecreaseQty.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecreaseQuantity(cartItem, position);
            }
        });

        // Nút Xóa Khỏi Giỏ Hàng
        holder.btnRemoveItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(cartItem, position);
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
        ImageButton btnDecreaseQty, btnIncreaseQty, btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ 100% khớp với id trong item_cart.xml
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