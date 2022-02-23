package com.example.sockettest;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {
    public static boolean flog = false;
    private String TAG = "wangguanjie";
    private Boolean isConnectionDestory = false;
    private String[] mResponseString = {"你好啊,哈哈",
            "请问你叫什么名字呀?",
            "今天北京天气不错啊,shy",
            "你知道吗?我可是可以和多个人同时聊天的哦",
            "给你讲个笑话吧:据说爱笑的人运气不会太差,不知道真假。"};
    private IntentFilter intentFilter;
    private MyBroadcastReceiver myBroadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();


        Log.i(TAG, "Service start");

        Runnable r = new Runnable() {
            @Override
            public void run() {

                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(8688);
                    Log.i(TAG, "Service start port 8688");
                } catch (IOException e) {
                    Log.i(TAG, "connection can not be established on port 8688");
                    e.printStackTrace();
                    return;
                }
                while (!flog) {
                    try {
                        final Socket client = serverSocket.accept();
                        Log.i(TAG, "qaq7");
                        Log.i(TAG, "accept socket ");
                        ServerSocket finalServerSocket = serverSocket;
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    responseClient(client);

                                    Log.i(TAG, "whether lost the connection: " + client.isClosed());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } catch (IOException e) {
                        Log.i(TAG, "service wait");
                        SystemClock.sleep(1000);
                        Log.i(TAG, "qaq8");
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(r).start();
    }


    private void responseClient(Socket client) throws IOException {
        DataInputStream inputStream = null;
        DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
        Log.i(TAG, "Welcome come to chat room");

        inputStream = new DataInputStream(client.getInputStream());
        String message = null;
        Log.i(TAG, "wait client");
        try {
            message = inputStream.readUTF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "meesage form client: " + message);
        if (message == null) return;
        int i = new Random().nextInt(mResponseString.length);
        String msg = mResponseString[i];
        outputStream.writeUTF(msg);

        outputStream.close();
        inputStream.close();
        client.close();
        flog = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {

        Log.i(TAG, "service destory");
        super.onDestroy();
    }


}
