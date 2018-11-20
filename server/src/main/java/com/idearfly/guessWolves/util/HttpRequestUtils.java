package com.idearfly.guessWolves.util;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HttpRequestUtils {
    public static JSONObject httpGet(String url) {
        try {
            URL _url = new URL(url);
            URLConnection connection = _url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection)connection;

            httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            InputStream inputStream = null;


            if (httpURLConnection.getResponseCode() >= 300) {
                throw new RuntimeException("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            try {
                inputStream = httpURLConnection.getInputStream();
                return JSONObject.parseObject(inputStream, JSONObject.class);
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
