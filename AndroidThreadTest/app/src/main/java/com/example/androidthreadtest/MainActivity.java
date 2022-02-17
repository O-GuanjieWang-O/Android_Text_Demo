package com.example.androidthreadtest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView text;
    public static final int UPDATE_TEXT=1;
    static Handler  UIWorkHandler,mainHandler;
    HandlerThread h1;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text=findViewById(R.id.text);
        Button change_text = findViewById(R.id.change_text);
        int i=1,a=0;
        
        change_text.setOnClickListener(this);
        h1=new HandlerThread("UI modify");
        h1.start();
        mainHandler=new Handler();
        UIWorkHandler=new Handler(h1.getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                text.setText("nice to meet you");
                            }
                        });

                        break;
                    default:
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                text.setText("Good Bye");
                            }
                        });
                        break;
                }
            }
        };
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_text:
               Message msg=Message.obtain();
               msg.what=1;
               UIWorkHandler.sendMessage(msg);
               break;
            default:
                break;
        }
    }


}
