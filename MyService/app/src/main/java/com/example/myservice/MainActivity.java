package com.example.myservice;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private Button start_download;
    private Button pause_download;
    private Button cancel_download;
    ProgressBar tv;
    BroadcastReceiver broadcastReceiver;

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder=(DownloadService.DownloadBinder)service;
            Log.v("wangguanjie","service binded");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        start_download=findViewById(R.id.start_download);
        pause_download=findViewById(R.id.pause_download);
        cancel_download =findViewById(R.id.cancel_download);
        tv=findViewById(R.id.textView);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent startIntent =new Intent(this,DownloadService.class);
        bindService(startIntent,connection,BIND_AUTO_CREATE);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s1 = intent.getStringExtra("Progress Status");
                Log.v("wangguanjie","received "+s1);
                tv.setProgress(Integer.parseInt(s1)+10);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.andy.myapplication");
        registerReceiver(broadcastReceiver, intentFilter);

        start_download.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (downloadBinder == null) {
                    return;
                }
                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                downloadBinder.startDownload(url);
            }
        });

        cancel_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBinder.cancelDownload();
            }
        });

        pause_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBinder.pauseDownload();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("wangguanjie","on destory");
        unbindService(connection);
        unregisterReceiver(broadcastReceiver);
    }

}
