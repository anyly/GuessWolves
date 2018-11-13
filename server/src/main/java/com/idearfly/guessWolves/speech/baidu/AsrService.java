package com.idearfly.guessWolves.speech.baidu;


import com.alibaba.fastjson.JSONObject;
import com.idearfly.guessWolves.speech.Speech;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

public class AsrService implements Speech {

    //  填写网页上申请的appkey 如 $apiKey="g8eBUMSokVB1BHGmgxxxxxx"
    private final String appKey = "4E1BG9lTnlSeIf1NQFlrSq6h";

    // 填写网页上申请的APP SECRET 如 $secretKey="94dc99566550d87f8fa8ece112xxxxx"
    private final String secretKey = "544ca4657ba8002e3dea3ac2f5fdd241";

    // 文件格式
    private final String format = "wav";

    //  1537 表示识别普通话，使用输入法模型。1536表示识别普通话，使用搜索模型。 其它语种参见文档
    private final int dev_pid = 1537;

    private String cuid = "1234567JAVA";
 
 // 采样率固定值
    private final int rate = 16000;
	
    public boolean methodRaw = false; // 默认以json方式上传音频文件

    private final String url = "http://vop.baidu.com/server_api"; // 可以改为https

    public String run(String filename) {
        File file = new File(filename);
        TokenHolder holder = new TokenHolder(appKey, secretKey, TokenHolder.ASR_SCOPE);
        holder.resfresh();
        String token = holder.getToken();
        String result = null;
        if (methodRaw) {
            result = runRawPostMethod(token, file);
        } else {
            result = runJsonPostMethod(token, file);
        }
        return result;
    }

    private String runRawPostMethod(String token, File file) {
        try {
            String url2 = url + "?cuid=" + ConnUtil.urlEncode(cuid) + "&dev_pid=" + dev_pid + "&token=" + token;
            //System.out.println(url2);
            byte[] content = getFileContent(file);
            HttpURLConnection conn = (HttpURLConnection) new URL(url2).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "audio/" + format + "; rate=" + rate);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(content);
            conn.getOutputStream().close();
            String result = ConnUtil.getResponseString(conn);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String runJsonPostMethod(String token, File file) {
        try {
            byte[] content = getFileContent(file);
            String speech = base64Encode(content);

            JSONObject params = new JSONObject();
            params.put("dev_pid", dev_pid);
            params.put("format", format);
            params.put("rate", rate);
            params.put("token", token);
            params.put("cuid", cuid);
            params.put("channel", "1");
            params.put("len", content.length);
            params.put("speech", speech);

            // System.out.println(params.toString());
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setDoOutput(true);
            conn.getOutputStream().write(params.toString().getBytes());
            conn.getOutputStream().close();
            String result = ConnUtil.getResponseString(conn);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private byte[] getFileContent(String filename) {
        File file = new File(filename);
        return getFileContent(file);
    }

    private byte[] getFileContent(File file) {
        if (!file.canRead()) {
            System.err.println("文件不存在或者不可读: " + file.getAbsolutePath());
            throw new RuntimeException("file can not read.");
        }
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            return ConnUtil.getInputStreamContent(is);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String base64Encode(byte[] content) {
         Base64.Encoder encoder = Base64.getEncoder(); // JDK 1.8  推荐方法
         String str = encoder.encodeToString(content);

        return str;
    }

    @Override
    public String recognition(String file) {
        return run(file);
    }
}
