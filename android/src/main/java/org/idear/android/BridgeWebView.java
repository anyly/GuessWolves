package org.idear.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.idear.android.jsbridge.JavaScriptBridge;

import java.util.List;

/**
 * Created by idear on 2018/9/24.
 */
public class BridgeWebView extends WebView {

    public BridgeWebView(Context context) {
        super(context);
    }

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }


    @SuppressLint("JavascriptInterface")
    public void registerBridge(JavaScriptBridge bridge) {
        this.addJavascriptInterface(bridge, bridge.getName());
    }


    @SuppressLint("NewApi")
    public <T> void evaluateFunction(String function, ValueCallback<String> valueCallback, Object... parameters) {
        if (function.startsWith("function")) {
            Log.d(this.getClass().getSimpleName(), "evaluateFunction() can not execute anonymous function!");
            return;
        }
        if (function == null || function.length() == 0 ||
                "undefined".equals(function) || "null".equals(function)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (function.startsWith("function ")) {
            stringBuilder.append("(").append(function).append(")").append("(");
        } else {
            stringBuilder.append(function).append("(");
        }
        if (parameters != null && parameters.length>0) {
            boolean split = false;
            for (Object parameter : parameters) {
                if (split) {
                    stringBuilder.append(",");
                } else {
                    split = true;
                }
                if (parameter instanceof String) {
                    stringBuilder.append("'").append(parameter).append("'");
                } else if (parameter instanceof Integer ||
                        parameter instanceof Double ||
                        parameter instanceof Float ||
                        parameter instanceof Short ||
                        parameter instanceof Boolean) {
                    stringBuilder.append(parameter);
                } else {
                    stringBuilder.append(JSONObject.toJSONString(parameter));
                }
            }
        }
        stringBuilder.append(")");
        this.evaluateJavascript(stringBuilder.toString(), valueCallback);
    }
}
