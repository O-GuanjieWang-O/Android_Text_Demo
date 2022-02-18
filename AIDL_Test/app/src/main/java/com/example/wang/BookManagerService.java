package com.example.wang;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {

    private static final String TAG = "BMS";

    public static CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private static RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();
    static boolean mIsServiceDestory = false;


    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.i(TAG, "getBookList");
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.i(TAG, "addBook");
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            Log.i(TAG,"register listener");
            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            Log.i(TAG,"unregister listener");
            mListenerList.unregister(listener);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "android"));
        mBookList.add(new Book(2, "ios"));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestory = true;
        super.onDestroy();
    }

    public static void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        Log.i(TAG, "OnNewBookArrived, book name " + book.getBookName());
        final int N=mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            Log.i(TAG, "OnNewBookArrived, notify listener: " + listener);
            if(listener!=null) {
                try {
                    listener.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();
    }
}
