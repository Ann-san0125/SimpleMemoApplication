package com.example.simplememoapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "memolist.db";
    private static final int DATABASE_VERSION = 1;

    //コンストラクタ
    public MemoDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = "CREATE TABLE memolist (_id INTEGER PRIMARY KEY, title TEXT, note TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
