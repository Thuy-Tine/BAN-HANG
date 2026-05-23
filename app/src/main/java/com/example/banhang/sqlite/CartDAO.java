package com.example.banhang.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.banhang.model.CartItem;
import com.example.banhang.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private final SQLiteDatabase db;

    public CartDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getWritableDatabase(); // Dùng Writable vì có thao tác Insert/Update
    }

    /**
     * 1. Lấy ID Giỏ hàng của User. Nếu chưa có thì tự động tạo mới.
     */
    @SuppressLint("Range")
    public int getOrCreateCartId(int userId) {
        int cartId = -1;
        Cursor cursor = db.rawQuery("SELECT cart_id FROM Cart WHERE user_id = ?", new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            cartId = cursor.getInt(cursor.getColumnIndex("cart_id"));
            cursor.close();
        } else {
            // Nếu chưa có, tạo giỏ hàng mới cho User
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            cartId = (int) db.insert("Cart", null, values);
            if (cursor != null) cursor.close();
        }
        return cartId;
    }

    /**
     * 2. Lấy toàn bộ Sản phẩm trong Giỏ hàng (INNER JOIN bảng Product)
     */
    @SuppressLint("Range")
    public List<CartItem> getCartItems(int cartId) {
        List<CartItem> list = new ArrayList<>();

        // Join 2 bảng để lấy quantity từ CartItem và thông tin từ Product
        String query = "SELECT ci.cart_item_id, ci.cart_id, ci.product_id, ci.quantity, " +
                "p.product_name, p.price, p.thumbnail, p.category_id " +
                "FROM CartItem ci " +
                "INNER JOIN Product p ON ci.product_id = p.product_id " +
                "WHERE ci.cart_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(cartId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    CartItem item = new CartItem();
                    item.setCartItemId(cursor.getInt(cursor.getColumnIndex("cart_item_id")));
                    item.setCartId(cursor.getInt(cursor.getColumnIndex("cart_id")));
                    item.setProductId(cursor.getInt(cursor.getColumnIndex("product_id")));
                    item.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));

                    // Bọc thông tin Product lại
                    Product product = new Product();
                    product.setProductId(item.getProductId());
                    product.setProductName(cursor.getString(cursor.getColumnIndex("product_name")));
                    product.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                    product.setThumbnail(cursor.getString(cursor.getColumnIndex("thumbnail")));
                    product.setCategoryId(cursor.getInt(cursor.getColumnIndex("category_id")));

                    item.setProduct(product);
                    list.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }

    /**
     * 3. Thêm sản phẩm vào giỏ hàng (Nếu đã có thì +1 số lượng)
     */
    @SuppressLint("Range")
    public void addToCart(int cartId, int productId) {
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        Cursor cursor = db.rawQuery("SELECT cart_item_id, quantity FROM CartItem WHERE cart_id = ? AND product_id = ?",
                new String[]{String.valueOf(cartId), String.valueOf(productId)});

        if (cursor != null && cursor.moveToFirst()) {
            // Đã có -> Tăng quantity
            int cartItemId = cursor.getInt(cursor.getColumnIndex("cart_item_id"));
            int currentQty = cursor.getInt(cursor.getColumnIndex("quantity"));
            updateQuantity(cartItemId, currentQty + 1);
            cursor.close();
        } else {
            // Chưa có -> Thêm mới với số lượng là 1
            ContentValues values = new ContentValues();
            values.put("cart_id", cartId);
            values.put("product_id", productId);
            values.put("quantity", 1);
            db.insert("CartItem", null, values);
            if (cursor != null) cursor.close();
        }
    }

    /**
     * 4. Cập nhật số lượng (Dùng khi ấn nút + hoặc -)
     */

    public void updateQuantity(int cartItemId, int newQuantity) {
        if (newQuantity <= 0) {
            // Nếu số lượng về 0 hoặc nhỏ hơn, xóa luôn sản phẩm khỏi giỏ
            deleteCartItem(cartItemId);
        } else {
            ContentValues values = new ContentValues();
            values.put("quantity", newQuantity);
            db.update("CartItem", values, "cart_item_id = ?", new String[]{String.valueOf(cartItemId)});
        }
    }

    /**
     * 5. Xóa sản phẩm khỏi giỏ
     */
    public void deleteCartItem(int cartItemId) {
        db.delete("CartItem", "cart_item_id = ?", new String[]{String.valueOf(cartItemId)});
    }

    /**
     * 6. Làm sạch giỏ hàng (Sau khi thanh toán xong)
     */
    public void clearCart(int cartId) {
        db.delete("CartItem", "cart_id = ?", new String[]{String.valueOf(cartId)});
    }
}