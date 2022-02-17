package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.os.Handler;



public class MessengerService extends Service {
    private static final String TAG="MeessengerService";
    private static class MessengerHandler extends Handler{
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1:
                    Log.i(TAG, "recevice msg form client: " + msg.getData().getString("msg"));
                    Messenger client =msg.replyTo;
                    Message replyMessage =Message.obtain(null,2);
                    Bundle bundle=new Bundle();
                    bundle.putString("reply","I have received your message");
                    replyMessage.setData(bundle);
                    try{
                        client.send(replyMessage);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.i(TAG, "recevice msg form client:fasdfasdfasdfasfa ");
                    super.handleMessage(msg);
            }
        }

    }
    private final Messenger mMessenger =new Messenger(new MessengerHandler());
    @Override
    public IBinder onBind(Intent intent){
        return mMessenger.getBinder();
    }
}
