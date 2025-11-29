package com.example.asm2;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "ASM2.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean insertUser(String user, String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user);
        cv.put("password", pass);
        long result = db.insert("users", null, cv);
        return result != -1;
    }

    public boolean checkLogin(String user, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE username=? AND password=?",
                new String[]{user, pass});
        return c.getCount() > 0;
    }
}
