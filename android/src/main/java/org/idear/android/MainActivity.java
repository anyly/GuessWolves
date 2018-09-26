package org.idear.android;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.idear.android.jsbridge.WebSocketBridge;

/**
 * Created by idear on 2018/9/23.
 */
public class MainActivity extends Activity {
    private BridgeWebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview = (BridgeWebView) findViewById(R.id.webview);

        settings();

        setupWebsocket();

        //设置打开的页面地址
        webview.loadUrl("http://192.168.1.5:8080/index.html");
        //webview.loadUrl("http://203.110.176.174:8082/GuessWolves/index.html");
    }

    private void settings() {
        //设置WebView属性，能够执行Javascript脚本
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        //开启调试
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        webview.setWebViewClient(new WebViewClient());

        webview.setWebChromeClient(new WebChromeClient());
    }


    private void setupWebsocket() {
        webview.registerBridge(new WebSocketBridge(webview));
    }
}
