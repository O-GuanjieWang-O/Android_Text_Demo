package com.example.myservice;
//回调接口
public interface DownloadListenser {
    void onProgress(int progress);//下载进度

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceld();
}
