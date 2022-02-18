package com.example.wang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BookManagerActivity extends Activity {
    private static final String TAG = "BookManagerActivity";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;
    private IBookManager mRemoteBookManager;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.i(TAG, "recevice new book: " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    private IBinder.DeathRecipient mDeathRecipient=new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.i(TAG,"Binder Dissconnacted");
            if(mDeathRecipient==null){
                return;
            }
            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecipient,0);
            mRemoteBookManager=null;
            Log.i(TAG,"Binder reconnacted");
            Intent intent = new Intent(BookManagerActivity.this, BookManagerService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            mRemoteBookManager=bookManager;
            try {
                mRemoteBookManager.asBinder().linkToDeath(mDeathRecipient,0);
                List<Book> list = bookManager.getBookList();
                Log.i(TAG, "query book list, list type: " + list.getClass().getCanonicalName());
                Log.i(TAG, "query book list: " + list.get(0).getClass());
                Book newBook = new Book(3, "android 开发");
                bookManager.addBook(newBook);
                List<Book> newlist = bookManager.getBookList();
                Log.i(TAG, "query book list: " + newlist.get(2).bookName);
                bookManager.registerListener(mOnNewBookArrivedListener);
                Log.i(TAG, " book list size: " + newlist.size());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            Log.i(TAG, "binder died.");

        }
    };
    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {

        @Override
        public void onNewBookArrived(Book newBooke) throws RemoteException {
            Log.i(TAG, newBooke.getBookName());
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBooke).sendToTarget();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestory");
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                Log.i(TAG, "unergister listener: " + mOnNewBookArrivedListener);
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);

        super.onDestroy();
    }
}
