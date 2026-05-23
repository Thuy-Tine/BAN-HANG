package com.example.banhang.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    // Các cột thực tế trong bảng CartItem của Database
    private int cartItemId;
    private int cartId;
    private int productId;
    private int quantity; // Mặc định là 1 theo SQL của em

    // Đối tượng mở rộng để hứng dữ liệu khi JOIN với bảng Product
    // Giúp em dễ dàng gọi: cartItem.getProduct().getProductName() trên giao diện
    private Product product;

    public CartItem() {}

    // --- GETTER & SETTER cho bảng CartItem ---
    public int getCartItemId() { return cartItemId; }
    public void setCartItemId(int cartItemId) { this.cartItemId = cartItemId; }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // --- GETTER & SETTER cho đối tượng Product đi kèm ---
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}