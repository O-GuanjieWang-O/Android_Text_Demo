package com.example.wang;

import java.util.List;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

public class BookManagerActivity extends Activity {
    private static final String TAG="BookManagerActivity";
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            IBookManager bookManager=IBookManager.Stub.asInterface(service);
            try{
                List<Book>list=bookManager.getBookList();
                Log.i(TAG,"query book list, list type: "+list.getClass().getCanonicalName());
                Log.i(TAG,"query book list: "+list.get(0).getClass());
                Book newBook=new Book(3,"android 开发");
                bookManager.addBook(newBook);
                List<Book>newlist=bookManager.getBookList();
                Log.i(TAG,"query book list: "+newlist.get(2).bookName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(this,BookManagerService.class);
        bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
