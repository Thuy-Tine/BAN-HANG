package com.example.banhang.sqlite;

// Bổ sung 2 thư viện còn thiếu
import android.annotation.SuppressLint;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.banhang.model.CartItem;
import com.example.banhang.model.Order;

import java.util.List;

public class OrderDAO {
    private final DatabaseHelper dbHelper;

    public OrderDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public boolean processCheckout(int userId, int cartId, double totalAmount, String address, String phone, String paymentMethod, List<CartItem> items) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            // 1. Insert OrderTable
            ContentValues orderValues = new ContentValues();
            orderValues.put("user_id", userId);
            orderValues.put("total_amount", totalAmount);
            orderValues.put("shipping_address", address);
            orderValues.put("phone", phone);
            orderValues.put("status", "Pending");
            long orderId = db.insert("OrderTable", null, orderValues);

            if (orderId == -1) return false;

            // 2. Insert Payment
            ContentValues paymentValues = new ContentValues();
            paymentValues.put("order_id", orderId);
            paymentValues.put("payment_method", paymentMethod);
            paymentValues.put("payment_status", paymentMethod.equals("QR") ? "Paid" : "Unpaid");
            db.insert("Payment", null, paymentValues);

            // 3. Insert OrderDetail & Trừ Kho Product
            for (CartItem item : items) {
                ContentValues detailValues = new ContentValues();
                detailValues.put("order_id", orderId);
                detailValues.put("product_id", item.getProductId());
                detailValues.put("quantity", item.getQuantity());
                detailValues.put("price", item.getProduct().getPrice());
                db.insert("OrderDetail", null, detailValues);

                db.execSQL("UPDATE Product SET stock = stock - ? WHERE product_id = ?",
                        new Object[]{item.getQuantity(), item.getProductId()});
            }

            // 4. Clear CartItem
            db.delete("CartItem", "cart_id = ?", new String[]{String.valueOf(cartId)});

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }

    @SuppressLint("Range")
    public List<Order> getOrderHistory(int userId, String statusFilter) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Xây dựng điều kiện lọc trạng thái
        String statusCondition = "";
        if (!statusFilter.equals("All")) {
            statusCondition = " AND o.status = '" + statusFilter + "' ";
        }

        // Truy vấn gom nhóm để lấy tổng tiền, tổng số lượng SP và 1 hình ảnh đại diện
        String query = "SELECT o.order_id, o.total_amount, o.status, o.order_date, " +
                "SUM(od.quantity) as total_items, MIN(p.thumbnail) as thumb " +
                "FROM OrderTable o " +
                "INNER JOIN OrderDetail od ON o.order_id = od.order_id " +
                "INNER JOIN Product p ON od.product_id = p.product_id " +
                "WHERE o.user_id = ? " + statusCondition +
                "GROUP BY o.order_id " +
                "ORDER BY o.order_date DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Order order = new Order();
                    order.setOrderId(cursor.getInt(cursor.getColumnIndex("order_id")));
                    order.setTotalAmount(cursor.getDouble(cursor.getColumnIndex("total_amount")));
                    order.setStatus(cursor.getString(cursor.getColumnIndex("status")));
                    order.setOrderDate(cursor.getString(cursor.getColumnIndex("order_date")));
                    order.setItemCount(cursor.getInt(cursor.getColumnIndex("total_items")));
                    order.setThumbnail(cursor.getString(cursor.getColumnIndex("thumb")));
                    list.add(order);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
}