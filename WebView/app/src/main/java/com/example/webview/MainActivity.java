package com.example.webview;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private WebView wb;
    private EditText et;
    private String address = null;
    private Button submit;
    private ProgressBar pb;
    private WebViewClient webViewClient;
    private WebChromeClient wc;
    private String TAG = "wangguanjie";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            int a2dpstate=mBluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
            Log.v(TAG,"Bluetooth a2dpstate: "+a2dpstate);

        }
        if (mBluetoothAdapter.isEnabled()) {
            int a2dpstate=mBluetoothAdapter.getProfileConnectionState(29);
            Log.v(TAG,"Bluetooth LE state: "+a2dpstate);

        }
        wb = findViewById(R.id.web_view);
        et = findViewById(R.id.edit_text);
        pb = findViewById(R.id.progress);
        submit = findViewById(R.id.submited);

        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        wb.getSettings().setAllowContentAccess(true);
        wb.getSettings().setDomStorageEnabled(true);

        wb.getSettings().setDisplayZoomControls(true);
        wb.getSettings().setSupportMultipleWindows(true);
        wb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        wb.getSettings().setSupportZoom(true);
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setDisplayZoomControls(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setLoadWithOverviewMode(true);

        wb.getSettings().setLoadsImagesAutomatically(true);

    }

    //solving the back button can not back to the last page on the webview
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (wb.canGoBack()) {
                wb.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        webViewClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pb.setVisibility(View.GONE);
            }

//            @Override
//            public void onLoadResource(WebView view, String url) {
//                Toast.makeText(getApplicationContext(),"Load Resource", Toast.LENGTH_SHORT).show();
//            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString() == null) {
                    return false;
                }
                String url = request.getUrl().toString();
                if (url.startsWith("weixin://") //微信
                        || url.startsWith("alipays://") //支付宝
                        || url.startsWith("mailto://") //邮件
                        || url.startsWith("tel://")//电话
                        || url.startsWith("dianping://")
                        || url.startsWith("baidu")) {
                    Log.v("wang", url);
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                } else {
                    view.loadUrl(url);
                    return true;
                }

            }
        };
        wc = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pb.setProgress(newProgress);
                Log.v(TAG, "progress :" + newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.v(TAG, "tile :" + title);
            }
        };
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wb.clearCache(true);
                wb.clearHistory();
                wb.clearFormData();
                address = String.valueOf(et.getText());
                wb.setWebViewClient(webViewClient);
                wb.setWebChromeClient(wc);
                wb.loadUrl(address);
            }
        });
    }
    @Override
    protected void onPause() {
        wb.pauseTimers();
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        wb.resumeTimers();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        wb=null;
        wb.destroy();

    }


}
