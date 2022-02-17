package com.example.myservice;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * string :传入参数
 *Integer: 使用整形数据作为进度显示单位
 * Integer: 使用整形数据返回执行的结果
 */
public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    public static final int TYPE_SUCCESS=0;
    public static final int TYPE_FAILED=1;
    public static final int TYPE_PAUSED=2;
    public static final int TYPE_CANCELED=3;
    private DownloadListenser listenser;

    private boolean isCanceled=false;
    private boolean isPaused=false;
    private int lastProgress;

    public DownloadTask(DownloadListenser l){
        this.listenser=l;
    }
    @Override  
    protected Integer doInBackground(String... params) {
        InputStream is=null;
        RandomAccessFile savedFile=null;
        File file=null;
        Log.v("wangguanjie","do in backgroud");
        try{
            long downloadLength =0;

            String downloadUrl=params[0];
            Log.v("wangguanjie","Download URL "+String.valueOf(params[0]));
            String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            Log.v("wangguanjie","File position "+directory+" "+fileName);
            file=new File(directory+fileName);
            if(file.exists()){
                downloadLength= file.length();
            }
            long contentLength=getContentLength(downloadUrl);
            Log.v("wangguanjie", "content length"+String.valueOf(contentLength));

            if(contentLength==0){
                return TYPE_FAILED;
            }
            else if(contentLength==downloadLength){
                Log.v("wangguanjie","success");
                return TYPE_SUCCESS;
            }
            Log.v("wangguanjie","wwwwwwww");
            OkHttpClient client=new OkHttpClient();
            Log.v("wangguanjie","wgh");
            Request request=new Request.Builder()
                    .url(downloadUrl)
                    .build();
            Log.v("wangguanjie","request " +String.valueOf(request));
            Response response=client.newCall(request).execute();
            Log.v("wangguanjie","Response " +String.valueOf(response));
            if(response!=null){
                assert response.body() != null;
                Log.v("wangguanjie","Response " +String.valueOf(response.body()));
                is=response.body().byteStream();//获取输入流
                savedFile=new RandomAccessFile(file,"rw");
                savedFile.seek(downloadLength);
                byte[]b=new byte[1024*8];
                int total=0;
                int len;
                while((len=is.read(b))!=-1){
                    if(isCanceled){
                        return TYPE_CANCELED;
                    }
                    else if(isPaused){
                        return  TYPE_PAUSED;
                    }
                    else{
                        total+=len;
                    }
                    savedFile.write(b,0,len);
                    int progress= (int) (((total+downloadLength)*100)/contentLength);
                    publishProgress(progress);
                }
                response.body().close();
                return TYPE_SUCCESS;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            try{
                if(is!=null){
                    is.close();
                }
                if(savedFile!=null){
                    savedFile.close();
                }
                if(isCanceled&&file!=null){
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        int progress=values[0];
        Log.v("wangguanjie","on progress update"+String.valueOf(progress));
        if(progress>lastProgress){
            listenser.onProgress(progress);
            lastProgress=progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        Log.v("wangguanjie","on post execute");
        switch(integer){
            case TYPE_SUCCESS:
                listenser.onSuccess();
                break;
            case TYPE_CANCELED:
                listenser.onCanceld();
                break;
            case TYPE_FAILED:
                listenser.onFailed();
                break;
            case TYPE_PAUSED:
                listenser.onPaused();
                break;
            default:
                break;
        }
    }

    public void pauseDownload(){
        isPaused=true;
    }
    public void cancelDownload(){
        isCanceled=true;
    }


    private long getContentLength(String downloadUrl) throws IOException{
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(downloadUrl).build();
        Response response =client.newCall(request).execute();
        if(response.isSuccessful()){
            long contentLength=response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }
}
