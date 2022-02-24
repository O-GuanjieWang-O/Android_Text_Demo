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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class TcpActivity extends AppCompatActivity {
    private String TAG = "wangguanjie";
    private EditText e;
    private TextView t;
    private Button b;
    private Socket mClientSocket;
    DataOutputStream outputStream = null;
    BufferedReader inputStream = null;

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
                Log.i(TAG, "***********************************************************");
                mClientSocket = null;
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

//                BufferedWriter mPrintWriter =new BufferedWriter(
//                        new OutputStreamWriter(mClientSocket.getOutputStream()));
//                mPrintWriter.write(String.valueOf(e.getText()));


            outputStream = new DataOutputStream(mClientSocket.getOutputStream());
            Log.i(TAG, "client send to server: " + e.getText());
            outputStream.writeUTF(String.valueOf(e.getText()));
//                mPrintWriter.newLine();
//                mPrintWriter.flush();
//                outputStream.close();
            inputStream = new BufferedReader(new InputStreamReader(
                    mClientSocket.getInputStream()));
//                DataInputStream inputStream = new DataInputStream(mClientSocket.getInputStream());
            String message = inputStream.readLine();

            Log.i(TAG, "client got from server:" + message);
            if (message != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        t.setText(message);
                    }

                });
            }
        } catch (Exception ioException) {
            ioException.printStackTrace();
        } finally {
            inputStream.close();
            outputStream.close();
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
