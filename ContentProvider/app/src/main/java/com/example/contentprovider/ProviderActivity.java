package com.example.contentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProviderActivity extends AppCompatActivity {
    private final String TAG="wangguanjie";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri bookUri = Uri.parse("content://com.example.provider.book_provider/book");//using the authorities which located at the manifests.xml

        ContentValues values = new ContentValues();
        values.put("_id",6);
        values.put("name","程序设计的艺术");
        Log.i(TAG,"name"+"程序设计的艺术");
        getContentResolver().insert(bookUri,values);
        Cursor bookCursor = getContentResolver().query(bookUri,new String[]
                {"_id","name"},null,null,null);
        while (bookCursor.moveToNext()) {
            Book book = new Book();
            book.bookId = bookCursor.getInt(0);
            book.bookName = bookCursor.getString(1);
            Log.d(TAG,"query book:" + book.toString());
        }
        bookCursor.close();
        Uri userUri = Uri.parse("content://com.example.provider.book_provider/user");
        Cursor userCursor = getContentResolver().query(userUri,new String[]
                {"_id","name","sex"},null,null,null);
        while (userCursor.moveToNext()) {
            User user = new User();
            user.userId = userCursor.getInt(0);
            user.userName = userCursor.getString(1);
            user.isMale = userCursor.getInt(2) == 1;
            Log.d(TAG,"query user:" + user.toString());
        }
        userCursor.close();
    }
}

