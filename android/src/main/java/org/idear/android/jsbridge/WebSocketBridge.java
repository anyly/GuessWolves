package org.idear.android.jsbridge;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.idear.android.BridgeWebView;
import org.idear.android.CoherentMap;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/23.
 */
public class WebSocketBridge extends JavaScriptBridge {
    private Handler handler = new Handler();

    private CoherentMap<String, WebSocket> clientMap = new CoherentMap<>();
    private LinkedHashMap<String, LinkedHashMap<String, String>> clientCallback = new LinkedHashMap<>();

    private static String seed = "0";

    public WebSocketBridge(BridgeWebView webView) {
        super(webView);
    }

    @Override
    public String getName() {
        return "WebSocketBridge";
    }

    private String next() {
        int i = Integer.valueOf(seed.substring(0, 1), 16);
        seed = Integer.toOctalString(++i) + seed.substring(1);
        return seed;
    }

    @JavascriptInterface
    public String create() {
        return next();
    }

    @JavascriptInterface
    public void connect(String id, String url) {
        URI uri = URI.create(url);
        WebSocketClient webSocketClient = new WebSocketClient(uri);
        webSocketClient.connect();
        clientMap.put(id, webSocketClient);
    }

    @JavascriptInterface
    public String disconnect(String id, int code, String message) {
        clientMap.get(id).close(code, message);
        return id;
    }

    private void addEvent(String id, String event, String callBackFunction) {
        LinkedHashMap<String, String> eventsCallback = clientCallback.get(id);
        if (eventsCallback == null) {
            eventsCallback = new LinkedHashMap<>();
            clientCallback.put(id, eventsCallback);
        }
        eventsCallback.put(event, callBackFunction.toString());
    }

    @JavascriptInterface
    public void onopen(String id, String callBackFunction) {
        String event = "open";
        addEvent(id, event, callBackFunction);
    }

    @JavascriptInterface
    public void onmessage(String id, String callBackFunction) {
        String event = "message";
        addEvent(id, event, callBackFunction);
    }

    @JavascriptInterface
    public void onerror(String id, String callBackFunction) {
        String event = "error";
        addEvent(id, event, callBackFunction);
    }

    @JavascriptInterface
    public void onclose(String id, String callBackFunction) {
        String event = "close";
        addEvent(id, event, callBackFunction);
    }

    @JavascriptInterface
    public void send(String id, String message) {
        clientMap.get(id).send(message);
    }

    private String getEventScript(String id, String event) {
        LinkedHashMap<String, String> eventsCallback = clientCallback.get(id);
        if (eventsCallback != null) {
            return eventsCallback.get(event);
        }
        return null;
    }

    class WebSocketClient extends org.java_websocket.client.WebSocketClient {

        public WebSocketClient(URI serverUri) {
            super(serverUri);
        }

        public WebSocketClient(URI serverUri, Draft protocolDraft) {
            super(serverUri, protocolDraft);
        }

        public WebSocketClient(URI serverUri, Map<String, String> httpHeaders) {
            super(serverUri, httpHeaders);
        }

        public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
            super(serverUri, protocolDraft, httpHeaders);
        }

        public WebSocketClient(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
            super(serverUri, protocolDraft, httpHeaders, connectTimeout);
        }

        @Override
        public void onOpen(final ServerHandshake handshakedata) {
            Log.e(this.getClass().getSimpleName(), "onOpen=>"+JSON.toJSONString(handshakedata));
            String id = clientMap.key(this);
            String event = "open";
            final String function = getEventScript(id, event);
            if (function == null || function.length() == 0 ||
                    "undefined".equals(function) || "null".equals(function)) {
                return;
            }
            //{function(Event)}
            handler.post(new Runnable() {
                @Override
                public void run() {
                    webView.evaluateFunction(function, null, handshakedata);
                }
            });

        }

        @Override
        public void onMessage(final String message) {
            Log.e(this.getClass().getSimpleName(), "onMessage=>"+message);
            String id = clientMap.key(this);
            String event = "message";
            final String function = getEventScript(id, event);
            //{function({data: (String|Blob|ArrayBuffer)})}
            if (function == null || function.length() == 0 ||
                    "undefined".equals(function) || "null".equals(function)) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("data", message);
                    webView.evaluateFunction(function, null, jsonObject);
                }
            });
        }

        @Override
        public void onClose(final int code, final String reason, boolean remote) {
            Log.e(this.getClass().getSimpleName(), "onClose=> code:"+code+", reason:"+reason+", remote:"+remote);
            clientMap.remove(this);
            //
            String id = clientMap.key(this);
            String event = "close";
            final String function = getEventScript(id, event);
            // code: Number, reason: String, wasClean: Boolean
            if (function == null || function.length() == 0 ||
                    "undefined".equals(function) || "null".equals(function)) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    webView.evaluateFunction(function, null, code, reason, reason);
                }
            });
        }

        @Override
        public void onError(final Exception ex) {
            Log.e(this.getClass().getSimpleName(), ex.toString());
            String id = clientMap.key(this);
            if (id == null) {
                return;
            }
            String event = "error";
            final String function = getEventScript(id, event);

            //{function({data: (String|Blob|ArrayBuffer)})}
            if (function == null || function.length() == 0 ||
                    "undefined".equals(function) || "null".equals(function)) {
                return;
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", ex.getMessage());
                    webView.evaluateFunction(function, null, jsonObject);
                }
            });
        }
    }

}
