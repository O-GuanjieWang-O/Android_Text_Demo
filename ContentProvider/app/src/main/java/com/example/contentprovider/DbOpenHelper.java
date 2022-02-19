package com.example.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final String DB_Name = "book_provider.db";
    public static final String Book_Table_Name = "book";
    public static final String User_Table_Name = "user";
    private static final int DB_version = 1;

    private final String CREATE_BOOK_TABLE = "CREATE TABLE IF NOT EXISTS" + Book_Table_Name + "(_id INTEGER PRIMARY KEY," + "name TEXT)";
    private final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS "
            + User_Table_Name + "(_id INTEGER PRIMARY KEY," + "name TEXT," + "sex INT)";

    public DbOpenHelper(Context context) {
        super(context, DB_Name, null, DB_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_BOOK_TABLE);
            db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
