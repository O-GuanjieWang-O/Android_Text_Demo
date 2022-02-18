package com.example.wang;

import static com.example.wang.BookManagerService.onNewBookArrived;

import android.os.RemoteException;
import android.util.Log;

public class ServiceWorker implements Runnable{

    @Override
    public void run() {
        while(!BookManagerService.mIsServiceDestory){
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int bookId=BookManagerService.mBookList.size()+1;
            Log.i("BMW","new book#"+bookId);
            Book newBook=new Book(bookId,"new book#"+bookId);
            try{
                onNewBookArrived(newBook);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
