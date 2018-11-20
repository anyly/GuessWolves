package com.idearfly.guessWolves.servlet;

import com.alibaba.fastjson.JSONObject;
import com.idearfly.guessWolves.util.HttpRequestUtils;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

@WebServlet("/minprogram/authorize")
public class MinprogramServlet extends HttpServlet {

    private static String appid = "wx19f0b5166b913d11";
    private static String appscret = "5f27b7721f2c9ceaaa47d2014bf0cb91";

    private String access_token;
    private long access_token_timeover;

    private String jsapi_ticket;
    private long jsapi_ticket_timeover;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String url = req.getParameter("url");
        if (url == null) {
            return;
        }

        String accessToken = getAccessToken();
        String jsapiTicket = getJsapiTicket(accessToken);

        JSONObject jsonObject = getSignature(url, jsapiTicket);
        try {
            resp.getWriter().print(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private JSONObject getSignature(String url, String jsapiTicket) {
        String noncestr =  getNonceStr();
        String timestamp =  getTimeStamp();
        String sign = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + noncestr + "&timestamp=" + timestamp + "&url=" + url;
        sign = DigestUtils.shaHex(sign);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", appid);
        jsonObject.put("nonceStr", noncestr);
        jsonObject.put("timestamp", timestamp);
        jsonObject.put("signature", sign);
        return jsonObject;
    }
    private String getNonceStr() {
        Random random = new Random();
        return DigestUtils.md5Hex(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);

    }

    private String getJsapiTicket(String accessToken) {
        long now = System.currentTimeMillis();
        if (now <= jsapi_ticket_timeover) {
            return jsapi_ticket;
        }
        JSONObject result = HttpRequestUtils.httpGet(
                "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi"
        );
        if (result != null) {
            int expires_in = result.getIntValue("expires_in");
            jsapi_ticket_timeover = System.currentTimeMillis() + expires_in;
            jsapi_ticket = result.getString("ticket");
            return jsapi_ticket;
        }
        return null;
    }

    private String getAccessToken() {
        long now = System.currentTimeMillis();
        if (now <= access_token_timeover) {
            return access_token;
        }
        JSONObject result = HttpRequestUtils.httpGet(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+appscret
        );
        if (result != null) {
            int expires_in = result.getIntValue("expires_in");
            access_token_timeover = System.currentTimeMillis() + expires_in;
            access_token = result.getString("access_token");
            return access_token;
        }
        return null;
    }
}
