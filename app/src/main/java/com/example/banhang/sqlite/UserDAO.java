package com.example.banhang.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import com.example.banhang.model.User;

public class UserDAO {
    private final DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    @SuppressLint("Range")
    public User getUserById(int userId) {
        User user = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE user_id = ?", new String[]{String.valueOf(userId)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                user = new User();
                user.setUserId(cursor.getInt(cursor.getColumnIndex("user_id")));
                user.setAccountId(cursor.getInt(cursor.getColumnIndex("account_id")));
                user.setFullName(cursor.getString(cursor.getColumnIndex("full_name")));
                user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                user.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                user.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
                user.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            }
            cursor.close();
        }
        return user;
    }
    public boolean updateAddress(int userId, String newAddress) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // 1. Kiểm tra xem user_id này đã tồn tại trong bảng User chưa
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE user_id = ?", new String[]{String.valueOf(userId)});
        boolean isExists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();

        ContentValues values = new ContentValues();
        values.put("address", newAddress);

        if (isExists) {
            // 2. Nếu đã tồn tại dòng user_id = 1, tiến hành UPDATE dữ liệu mới
            int rows = db.update("User", values, "user_id = ?", new String[]{String.valueOf(userId)});
            return rows > 0;
        } else {

            // Gán các thông tin mặc định ban đầu để tránh bảng bị thiếu dữ liệu
            values.put("user_id", userId);
            values.put("account_id", 1); // Liên kết với tài khoản mặc định
            values.put("full_name", "Trần Thị Thủy Tiên");
            values.put("phone", "0123456789");

            long resultId = db.insert("User", null, values);
            return resultId != -1;
        }
    }


    public boolean insertInitialUser(long accountId, String fullName, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("account_id", (int) accountId);
        values.put("full_name", fullName);
        values.put("phone", phone);


        long result = db.insert("User", null, values);
        return result != -1;
    }
    public int getUserIdByPhone(String phone) {
        android.database.sqlite.SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT u.user_id FROM User u " +
                "INNER JOIN Account a ON u.account_id = a.account_id " +
                "WHERE a.sdt = ?";
        android.database.Cursor cursor = db.rawQuery(query, new String[]{phone});
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }
}