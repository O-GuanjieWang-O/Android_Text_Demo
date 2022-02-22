package com.example.sockettest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {
    private String TAG = "wangguanjie";
    private Boolean isServiceDestory = false;
    private String[] mResponseString = {"你好啊,哈哈",
            "请问你叫什么名字呀?",
            "今天北京天气不错啊,shy",
            "你知道吗?我可是可以和多个人同时聊天的哦",
            "给你讲个笑话吧:据说爱笑的人运气不会太差,不知道真假。"};

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service start");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ServerSocket serverSocket = null;
                try {
                    serverSocket = new ServerSocket(45888);
                    Log.i(TAG, "Service start port 45888");
                } catch (IOException e) {
                    Log.i(TAG, "connection can not be established on port 8888");
                    e.printStackTrace();
                    return;
                }
                Log.i(TAG, "service wait");

                while (!isServiceDestory) {
                    Log.i(TAG,"qa9q");
                    try {
                        Log.i(TAG,"qaq6");
                        Socket client = serverSocket.accept();
                        Log.i(TAG,"qaq7");
                        Log.i(TAG, "accept socket ");
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    responseClient(client);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    } catch (IOException e) {
                        Log.i(TAG,"connect tcp server failed,retry...");
                        SystemClock.sleep(1000);

                        Log.i(TAG,"qaq8");
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(r).start();
    }
    private void  responseClient(Socket client) throws IOException {
        BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out=new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())));
        Log.i(TAG,"Welcome come to chat room");
        while(!isServiceDestory){
            String str=in.readLine();
            Log.i(TAG,"meesage form client: "+str);
            if(str==null) break;
            int i= new Random().nextInt(mResponseString.length);
            String msg=mResponseString[i];
        }
        out.close();
        in.close();
        client.close();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        isServiceDestory = true;
        super.onDestroy();
    }
}
