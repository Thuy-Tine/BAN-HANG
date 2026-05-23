package com.example.banhang.sqlite;



import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.banhang.model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final SQLiteDatabase db;

    public ProductDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.db = dbHelper.getReadableDatabase();
    }

    @SuppressLint("Range")
    public List<Product> getAllProducts(int categoryId) {
        List<Product> list = new ArrayList<>();
        String query = (categoryId == -1) ?
                "SELECT * FROM Product ORDER BY created_at DESC" :
                "SELECT * FROM Product WHERE category_id = " + categoryId + " ORDER BY created_at DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Product p = new Product();
                    p.setProductId(cursor.getInt(cursor.getColumnIndex("product_id")));
                    p.setCategoryId(cursor.getInt(cursor.getColumnIndex("category_id")));
                    p.setBrandId(cursor.getInt(cursor.getColumnIndex("brand_id")));
                    p.setProductName(cursor.getString(cursor.getColumnIndex("product_name")));
                    p.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                    p.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                    p.setStock(cursor.getInt(cursor.getColumnIndex("stock")));
                    p.setThumbnail(cursor.getString(cursor.getColumnIndex("thumbnail")));
                    p.setCreatedAt(cursor.getString(cursor.getColumnIndex("created_at")));
                    list.add(p);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
}