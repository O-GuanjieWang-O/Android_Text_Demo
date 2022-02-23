package com.example.sockettest;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TcpActivity extends AppCompatActivity {
    private String TAG = "wangguanjie";
    private EditText e;
    private TextView t;
    private Button b;
    private Socket mClientSocket;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        e = findViewById(R.id.edit);
        t = findViewById(R.id.resp);
        b = findViewById(R.id.submit);
        Intent service = new Intent(this, TCPServerService.class);
        startService(service);


        Log.i(TAG, "remote service starting");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"***********************************************************");
                mClientSocket=null;
                if (e.getText().equals("") || e.getText() == null) {
                    t.setText("Input the text first");
                } else {
                    Runnable r = (new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.i(TAG, "connecting");
                                connection();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    });
                    new Thread(r).start();
                }
            }
        });
    }

    public void connection() throws IOException {
        while (mClientSocket == null) {
            Log.i(TAG, "client socket is null,establish new connection");
            try {
                InetAddress IP = InetAddress.getLocalHost();//get android localhost IP
                mClientSocket = new Socket(IP, 8688);
                Log.i(TAG, "connection success");
            } catch (IOException ioException) {
                SystemClock.sleep(2000);
                Log.i(TAG, "connect tcp server failed, retry...");
                ioException.printStackTrace();
            }
        }
        try {
            Log.i(TAG, "client sending data");
            try {
                DataOutputStream outputStream = null;
                outputStream = new DataOutputStream(mClientSocket.getOutputStream());
                Log.i(TAG, "client send to server: " + e.getText());
                outputStream.writeUTF(String.valueOf(e.getText()));

                DataInputStream inputStream = new DataInputStream(mClientSocket.getInputStream());
                String message = inputStream.readUTF();
                Log.i(TAG, "client got from server:"+ message);
                if (message != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            t.setText(message);
                        }
                    });
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (Exception ioException) {
            ioException.printStackTrace();
        }
        mClientSocket.shutdownInput();
        mClientSocket.shutdownOutput();
        mClientSocket.close();
        Log.i(TAG, "Communication finish");
    }



    @Override
    protected void onDestroy() {
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.shutdownOutput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
