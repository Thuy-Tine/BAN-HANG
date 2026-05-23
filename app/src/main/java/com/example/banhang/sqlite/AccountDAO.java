package com.example.banhang.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.banhang.model.Account;

public class AccountDAO {
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    Account acc;

    public AccountDAO(Context context){
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

    }

    public boolean CheckLogin(String sdt, String password ){
        Cursor curso = db.rawQuery("SELECT * FROM Account WHERE sdt = ? AND password = ?",
                new String[]{sdt, password});

        boolean success = curso.getCount() > 0;
        curso.close();
        return success;

    }

    public long Register(Account acc){
        ContentValues values = new ContentValues();
        values.put("sdt", acc.getSdt());
        values.put("password", acc.getPassword());
        values.put("role_id", 3);
        return db.insert("Account", null, values);
    }

    public boolean updatePassword(String sdt, String newPassword){
        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int result = db.update(
                "Account",
                values,
                "sdt = ?",
                new String[]{sdt}
        );

        return result > 0;
    }

    public boolean checkPhoneExists(String sdt){
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Account WHERE sdt = ?",
                new String[]{sdt}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
