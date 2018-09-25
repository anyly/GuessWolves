package org.idear.android.jsbridge;

import android.webkit.WebView;
import org.idear.android.BridgeWebView;

/**
 * Created by idear on 2018/9/24.
 */
public abstract class JavaScriptBridge {
    protected BridgeWebView webView;

    public JavaScriptBridge(BridgeWebView webView) {
        this.webView = webView;
    }

    public abstract String getName();
}
