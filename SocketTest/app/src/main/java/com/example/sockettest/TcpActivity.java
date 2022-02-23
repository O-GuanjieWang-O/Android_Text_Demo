package com.example.sockettest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public class TcpActivity extends AppCompatActivity {
    private String TAG = "wangguanjie";
    private EditText e;
    private TextView t;
    private Button b;
    private PrintWriter mPrintWriter;
    private Socket mClientSocket;
    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;
    private Handler mainHandler;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG: {
                    t.setText(new StringBuilder().append(t.getText()).append((String) msg.obj).toString());
                    break;
                }
                case MESSAGE_SOCKET_CONNECTED: {
                    Log.i(TAG,"1Q");
                    b.setEnabled(true);
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        e = findViewById(R.id.edit);
        t = findViewById(R.id.resp);
        b = findViewById(R.id.submit);
        Intent service = new Intent(this, TCPServerService.class);
        startService(service);
        Runnable r= (new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG,"dfaasdfasdf");
                    connection();

                } catch (IOException ioException) {

                    ioException.printStackTrace();
                }
            }
        });
        new Thread(r).start();

        Log.i(TAG,"dfaasdsssssssfasdf");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String msg = e.getText().toString();
                if (!msg.equals("") && mPrintWriter != null) {

                    mPrintWriter.println(msg);
                    e.setText("");
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showedMsg = "self " + time + ":" + msg + "\n";
                    t.setText(t.getText() + showedMsg);

                }
            }
        });


    }

    @SuppressLint("SimpleDateFormat")
    private String formatDateTime(long time) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(time));
    }

    public void connection() throws IOException {
        Socket socket =null;;
        String port="";
        Log.i(TAG,"qq");
        while (socket == null) {
            Log.i(TAG,"qaq");
            try {
                Log.i(TAG, "qa4q");
                InetAddress IP=InetAddress.getLocalHost();
                Log.i(TAG,"local host ip address is: "+IP);
                socket=new Socket(IP, 8688);
                port=String.valueOf(socket.getPort());
                mClientSocket = socket;

                Log.i(TAG, "qa1q");
                mPrintWriter = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
                Log.i(TAG, "qa2q");
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                Log.i(TAG, "connection success");
            } catch (IOException ioException) {
                SystemClock.sleep(2000);
                Log.i(TAG,"Client port: "+port);
                Log.i(TAG,"connect tcp server failed, retry...");
                ioException.printStackTrace();
            }
        }
        try{
            BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!this.isFinishing()) {
                String msg= br.readLine();
                Log.i(TAG,"client get the msg: "+msg);
                if(msg!=null){
                    String time = formatDateTime(System.currentTimeMillis());
                    final String showedMsg = "server " + time + ":" + msg
                            + "\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG,showedMsg)
                            .sendToTarget();

                }
            }
            Log.i(TAG,"discount");
            br.close();
            mPrintWriter.close();
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}