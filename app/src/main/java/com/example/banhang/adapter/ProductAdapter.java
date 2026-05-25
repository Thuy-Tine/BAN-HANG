package com.example.banhang.adapter;

import android.annotation.SuppressLint;
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
import com.example.banhang.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnItemClickListener listener;
    private OnAddToCartListener addToCartListener;
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
    public interface OnAddToCartListener {
        void onAddToCartClick(Product product);
    }
    public ProductAdapter(Context context, OnItemClickListener itemListener, OnAddToCartListener cartListener) {
        this.context = context;
        this.listener = itemListener;
        this.addToCartListener = cartListener;
        this.productList = new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setProducts(List<Product> newList) {
        this.productList.clear();
        this.productList.addAll(newList);
        notifyDataSetChanged();
    }

    public void addMoreProducts(List<Product> additionalList) {
        int startPosition = this.productList.size();
        this.productList.addAll(additionalList);
        notifyItemRangeInserted(startPosition, additionalList.size());
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvProductPrice.setText(String.format("$%.2f", product.getPrice()));


        String categoryName = "";
        if (product.getCategoryId() == 1) categoryName = "Skincare";
        else if (product.getCategoryId() == 2) categoryName = "Makeup";
        else if (product.getCategoryId() == 3) categoryName = "Fragrance";
        holder.tvProductCategory.setText(categoryName);

        // Load hình ảnh bằng Glide từ URL
        Glide.with(context)
                .load(product.getThumbnail())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivProduct);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));

        holder.btnAddToCart.setOnClickListener(v -> {
            if (addToCartListener != null) {
                addToCartListener.onAddToCartClick(product);
            }
        });
    }

    @Override
    public int getItemCount() { return productList.size(); }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductCategory;
        ImageView ivProduct, btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);


            ivProduct = itemView.findViewById(R.id.imgProduct);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}