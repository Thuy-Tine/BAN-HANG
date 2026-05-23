package com.example.banhang.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    // DATABASE
    private static final String DATABASE_NAME = "TineCosmetic.db";
    private static final int DATABASE_VERSION = 6;
    private final Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // =========================
        // ROLE
        // =========================

        String CREATE_ROLE_TABLE = "CREATE TABLE Role (" +
                "role_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "role_name TEXT NOT NULL" +
                ")";

        db.execSQL(CREATE_ROLE_TABLE);

        // =========================
        // ACCOUNT
        // =========================

        String CREATE_ACCOUNT_TABLE = "CREATE TABLE Account (" +
                "account_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "sdt TEXT UNIQUE NOT NULL, " +

                "password TEXT NOT NULL, " +

                "role_id INTEGER NOT NULL, " +

                "status INTEGER DEFAULT 1, " +

                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +

                "FOREIGN KEY(role_id) REFERENCES Role(role_id)" +
                ")";

        db.execSQL(CREATE_ACCOUNT_TABLE);

        // =========================
        // USER
        // =========================

        String CREATE_USER_TABLE = "CREATE TABLE User (" +

                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "account_id INTEGER UNIQUE, " +

                "full_name TEXT, " +

                "phone TEXT, " +

                "address TEXT, " +

                "avatar TEXT, " +

                "gender TEXT, " +

                "FOREIGN KEY(account_id) REFERENCES Account(account_id)" +
                ")";

        db.execSQL(CREATE_USER_TABLE);

        // =========================
        // CATEGORY
        // =========================

        String CREATE_CATEGORY_TABLE = "CREATE TABLE Category (" +

                "category_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "category_name TEXT NOT NULL" +

                ")";

        db.execSQL(CREATE_CATEGORY_TABLE);

        // =========================
        // BRAND
        // =========================

        String CREATE_BRAND_TABLE = "CREATE TABLE Brand (" +

                "brand_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "brand_name TEXT NOT NULL, " +

                "logo TEXT, " +

                "description TEXT" +

                ")";

        db.execSQL(CREATE_BRAND_TABLE);

        // =========================
        // PRODUCT
        // =========================

        String CREATE_PRODUCT_TABLE = "CREATE TABLE Product (" +

                "product_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "category_id INTEGER, " +

                "brand_id INTEGER, " +

                "product_name TEXT NOT NULL, " +

                "description TEXT, " +

                "price REAL NOT NULL, " +

                "stock INTEGER DEFAULT 0, " +

                "thumbnail TEXT, " +

                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +

                "FOREIGN KEY(category_id) REFERENCES Category(category_id), " +

                "FOREIGN KEY(brand_id) REFERENCES Brand(brand_id)" +

                ")";

        db.execSQL(CREATE_PRODUCT_TABLE);

        // =========================
        // CART
        // =========================

        String CREATE_CART_TABLE = "CREATE TABLE Cart (" +

                "cart_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "user_id INTEGER UNIQUE, " +

                "FOREIGN KEY(user_id) REFERENCES User(user_id)" +

                ")";

        db.execSQL(CREATE_CART_TABLE);

        // =========================
        // CART ITEM
        // =========================

        String CREATE_CART_ITEM_TABLE = "CREATE TABLE CartItem (" +

                "cart_item_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "cart_id INTEGER, " +

                "product_id INTEGER, " +

                "quantity INTEGER DEFAULT 1, " +

                "FOREIGN KEY(cart_id) REFERENCES Cart(cart_id), " +

                "FOREIGN KEY(product_id) REFERENCES Product(product_id)" +

                ")";

        db.execSQL(CREATE_CART_ITEM_TABLE);

        // =========================
        // ORDER
        // =========================

        String CREATE_ORDER_TABLE = "CREATE TABLE OrderTable (" +

                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "user_id INTEGER, " +

                "total_amount REAL, " +

                "shipping_address TEXT, " +

                "phone TEXT, " +

                "status TEXT DEFAULT 'Pending', " +

                "order_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +

                "FOREIGN KEY(user_id) REFERENCES User(user_id)" +

                ")";

        db.execSQL(CREATE_ORDER_TABLE);

        // =========================
        // ORDER DETAIL
        // =========================

        String CREATE_ORDER_DETAIL_TABLE = "CREATE TABLE OrderDetail (" +

                "order_detail_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "order_id INTEGER, " +

                "product_id INTEGER, " +

                "quantity INTEGER, " +

                "price REAL, " +

                "FOREIGN KEY(order_id) REFERENCES OrderTable(order_id), " +

                "FOREIGN KEY(product_id) REFERENCES Product(product_id)" +

                ")";

        db.execSQL(CREATE_ORDER_DETAIL_TABLE);

        // =========================
        // PAYMENT
        // =========================

        String CREATE_PAYMENT_TABLE = "CREATE TABLE Payment (" +

                "payment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +

                "order_id INTEGER, " +

                "payment_method TEXT, " +

                "payment_status TEXT DEFAULT 'Unpaid', " +

                "payment_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +

                "FOREIGN KEY(order_id) REFERENCES OrderTable(order_id)" +

                ")";

        db.execSQL(CREATE_PAYMENT_TABLE);

        // =========================
        // INSERT ROLE
        // =========================

        db.execSQL("INSERT INTO Role(role_name) VALUES('Admin')");
        db.execSQL("INSERT INTO Role(role_name) VALUES('Staff')");
        db.execSQL("INSERT INTO Role(role_name) VALUES('Customer')");
        db.execSQL("INSERT INTO Account(sdt, password, role_id) VALUES ('0123456789', '123', 1)");
        loadDataFromCSV(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Payment");

        db.execSQL("DROP TABLE IF EXISTS OrderDetail");

        db.execSQL("DROP TABLE IF EXISTS OrderTable");

        db.execSQL("DROP TABLE IF EXISTS CartItem");

        db.execSQL("DROP TABLE IF EXISTS Cart");

        db.execSQL("DROP TABLE IF EXISTS Product");

        db.execSQL("DROP TABLE IF EXISTS Brand");

        db.execSQL("DROP TABLE IF EXISTS Category");

        db.execSQL("DROP TABLE IF EXISTS User");

        db.execSQL("DROP TABLE IF EXISTS Account");

        db.execSQL("DROP TABLE IF EXISTS Role");

        onCreate(db);

    }


    private void loadDataFromCSV(SQLiteDatabase db) {
        try {
            InputStream is = context.getAssets().open("products.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            reader.readLine(); // Bỏ qua dòng tiêu đề

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    ContentValues values = new ContentValues();
                    values.put("category_id", Integer.parseInt(parts[0].trim()));
                    values.put("brand_id", Integer.parseInt(parts[1].trim()));
                    values.put("product_name", parts[2].trim());
                    values.put("description", parts[3].trim());
                    values.put("price", Double.parseDouble(parts[4].trim()));
                    values.put("stock", Integer.parseInt(parts[5].trim()));
                    values.put("thumbnail", parts[6].trim()); // Link URL
                    db.insert("Product", null, values);
                }
            }
            reader.close();
        } catch (Exception e) {
            Log.e("DB_ERROR", "Lỗi đọc file CSV: " + e.getMessage());
        }
    }
}