package com.example.myservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.security.Provider;

public class DownloadService extends Service {
    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification(String title, int progress){
        Intent intent =new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);

        String NOTIFICATION_CHANNEL_ID = "com.example.myservice";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(DownloadService.this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.img))
                .setContentIntent(pi)
                .setContentTitle(title);
        if(progress>0){
            notificationBuilder.setContentText(progress+"%");
            notificationBuilder.setProgress(100,progress,false);
        }
        return notificationBuilder.build();

    }
    private LocalBroadcastManager localBroadcastManager;
    private DownloadListenser listenser=new DownloadListenser() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onProgress(int progress) {

            Log.v("wangguanjie","on Progress");
            Intent  intent=new Intent();
            intent.setAction("com.example.andy.myapplication");
            intent.putExtra("Progress Status",String.valueOf(progress));
            Log.v("wangguanjie","progress value: "+progress);
            sendBroadcast(intent);
            getNotificationManager().notify(1,getNotification("Downloading...",progress));
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onSuccess() {
            Log.v("wangguanjie","on Success");
            downloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Downloading>>>",-1));
            Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onFailed() {
            downloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Downloading Failed",-1));
            Toast.makeText(DownloadService.this,"Download Failed",Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onPaused() {
            downloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Downloading Paused",-1));
            Toast.makeText(DownloadService.this,"Download Paused",Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCanceld() {
            downloadTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Downloading Canceled",-1));
            Toast.makeText(DownloadService.this,"Download Canceled",Toast.LENGTH_SHORT).show();
        }
    };
    private DownloadTask downloadTask;
    private String downloadUrl;
    private DownloadBinder mBinder=new DownloadBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    class DownloadBinder extends Binder {

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void startDownload(String url) {
            if (url != null) {
                downloadUrl = url;
                downloadTask = new DownloadTask(listenser);
                downloadTask.execute(downloadUrl);
                Log.v("wangguanjie", "ready to download");
                startForeground(1, getNotification("Downloading...", 0));

            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                downloadTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                }
            }

        }
    }
    @Override
    public void onDestroy() {
        downloadTask =null;
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("wangguanjie","on unbind");
        return super.onUnbind(intent);
    }
}
