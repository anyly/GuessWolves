package org.idear.android;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.ValueCallback;
import org.xwalk.core.*;

/**
 * Created by idear on 2018/9/23.
 */
public class CrosswalkMainActivity extends Activity {
    private XWalkView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crosswalk);

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //webview = new XWalkView(getApplicationContext());
        //webview.setLayoutParams(params);
        //setContentView(webview);
        webview = (XWalkView) findViewById(R.id.webview);

        defaultSettings(webview);

        setWebViewClient(webview);

        setWebChromeClient(webview);

        //webview.loadUrl("http://192.168.1.5:8080/index.html");
        webview.loadUrl("http://192.168.1.5:8080/flash/sample.html");
    }

    private void setWebChromeClient(XWalkView webview) {
        webview.setUIClient(new XWalkUIClient(webview) {
            @Override
            public void onPageLoadStarted(XWalkView view, String url) {
                super.onPageLoadStarted(view, url);
            }

            @Override
            public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onScaleChanged(XWalkView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                super.onPageLoadStopped(view, url, status);
            }
        });
    }

    private void setWebViewClient(XWalkView webview) {
        webview.setResourceClient(
                new XWalkResourceClient(webview) {
                    //=========HTML5定位==========================================================
                    //需要先加入权限
                    //<uses-permission android:name="android.permission.INTERNET"/>
                    //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
                    //<uses-permission android:name=
                    // "android.permission.ACCESS_COARSE_LOCATION"/>

                    @Override
                    public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
                        super.onReceivedSslError(view, callback, error);
                    }

                    @Override
                    public void onLoadFinished(XWalkView view, String url) {
                        super.onLoadFinished(view, url);
                    }

                    @Override
                    public void onLoadStarted(XWalkView view, String url) {
                        super.onLoadStarted(view, url);
                    }

                    @Override
                    public void onProgressChanged(XWalkView view, int progressInPercent) {
                        super.onProgressChanged(view, progressInPercent);
                    }

                    @Override
                    public void onReceivedClientCertRequest(XWalkView view, ClientCertRequest handler) {
                        super.onReceivedClientCertRequest(view, handler);
                    }

                    @Override
                    public void onDocumentLoadedInFrame(XWalkView view, long frameId) {
                        super.onDocumentLoadedInFrame(view, frameId);
                    }

                    @Override
                    public void onReceivedHttpAuthRequest(XWalkView view, XWalkHttpAuthHandler handler, String host, String realm) {
                        super.onReceivedHttpAuthRequest(view, handler, host, realm);
                    }

                    @Override
                    public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedLoadError(view, errorCode, description, failingUrl);
                    }

                    @Override
                    public void onReceivedResponseHeaders(XWalkView view, XWalkWebResourceRequest request, XWalkWebResourceResponse response) {
                        super.onReceivedResponseHeaders(view, request, response);
                    }
                }
        );
    }

    private void defaultSettings(XWalkView webview) {
        XWalkSettings mWebSettings = webview.getSettings();
        mWebSettings.setSupportZoom(true);//支持缩放
        mWebSettings.setBuiltInZoomControls(true);//可以任意缩放
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);////将图片调整到适合webview的大小
//        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);
//        mWebSettings.setMixedContentMode()
        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        mWebSettings.setJavaScriptEnabled(true);//支持JS
    }
}
