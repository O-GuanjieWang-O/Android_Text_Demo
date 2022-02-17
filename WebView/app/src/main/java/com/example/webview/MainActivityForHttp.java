package com.example.webview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivityForHttp extends AppCompatActivity {
    private Button sendRequest;
    private TextView responseText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_main);
        sendRequest=findViewById(R.id.send_request);
        responseText=findViewById(R.id.response_text);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithHttpRequset();
            }
        });
    }
    private void sendRequestWithHttpRequset(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection =null;
                BufferedReader reader=null;
                try{
                    URL url=new URL("https://www.baidu.com");
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    showResponse(response.toString());
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                        if(connection!=null){
                            connection.disconnect();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void showResponse(final String response){
        Runnable r=new Runnable() {
            @Override
            public void run() {
                responseText.setText(response);
            }
        };
        runOnUiThread(r);
    }
}
