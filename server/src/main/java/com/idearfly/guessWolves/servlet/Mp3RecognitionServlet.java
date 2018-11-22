package com.idearfly.guessWolves.servlet;

import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.speech.AipSpeech;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

@WebServlet(urlPatterns = "/mp3recognition")
public class Mp3RecognitionServlet extends HttpServlet {
    private  final String appId = "14913810";
    private final String appKey = "jL3AGkxldu0MZSj5kDDdCOC8";
    private final String secretKey = "RG5rBjuG1xzGHCKzdQkuCvP6w1oXjv2Y";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // 得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
        String savePath = request.getServletContext().getRealPath("/pcm/");

        File file = new File(savePath);
        // 判断上传文件的保存目录是否存在
        if (!file.exists() && !file.isDirectory()) {
            System.out.println(savePath + "目录不存在，需要创建");
            // 创建目录
            file.mkdir();
        }

        try {
            // 使用Apache文件上传组件处理文件上传步骤：
            // 1、创建一个DiskFileItemFactory工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();
            // 2、创建一个文件上传解析器
            ServletFileUpload upload = new ServletFileUpload(factory);
            // 解决上传文件名的中文乱码
            upload.setHeaderEncoding("UTF-8");
            // 3、判断提交上来的数据是否是上传表单的数据
            if (!ServletFileUpload.isMultipartContent(request)) {
                // 按照传统方式获取数据
                return;
            }
            // 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
            List<FileItem> list = upload.parseRequest(request);

            int i = 0;
            for (FileItem item : list) {
                // 如果fileitem中封装的是普通输入项的数据
                if (item.isFormField()) {
                    // System.out.println("歌曲名"+item.getString("musicName")+"类别"+item.getString("musicType"));
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                } else {// 如果fileitem中封装的是上传文件
                    // 得到上传的文件名称，
                    InputStream in = null;
                    FileOutputStream out = null;
                    String path = null;
                    try {
                        String filename = item.getFieldName();
                        System.out.println(filename);
                        // 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：
                        // c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
                        // 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
                        //filename = filename.substring(filename.lastIndexOf(File.separator) + 1);

                        // 创建一个文件输出流
                        if (filename.lastIndexOf('.') == -1) {
                            filename += ".wav";
                        }
                        path = savePath + File.separator + filename;
                        File uploadFile = new File(path);
                        File dir = uploadFile.getParentFile();
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        if (uploadFile.exists()) {
                            uploadFile.delete();
                        }
                        // 获取item中的上传文件的输入流
                        in = item.getInputStream();
                        out = new FileOutputStream(uploadFile);
                        // 创建一个缓冲区
                        byte buffer[] = new byte[1024];
                        // 判断输入流中的数据是否已经读完的标识
                        int len = 0;
                        // 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
                        while ((len = in.read(buffer)) > 0) {
                            // 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\"
                            // + filename)当中
                            out.write(buffer, 0, len);
                        }
                    } finally {
                        // 关闭输入流
                        if (in != null) {
                            in.close();
                        }
                        // 关闭输出流
                        if (out != null) {
                            out.close();
                        }
                        // 删除处理文件上传时生成的临时文件
                        item.delete();
                    }
                    // 识别
                    if (path != null) {
                        FileInputStream fileInputStream = new FileInputStream(path);
                        byte[] pcmBytes = mp3Convertpcm(fileInputStream);
                        JSONObject resultJson = speechBdApi(pcmBytes);
                        System.out.println(resultJson.toString());
                        if (null != resultJson && resultJson.getIntValue("err_no") == 0) {
                            String result = resultJson.getJSONArray("result").get(0).toString();
                            response.setContentType("text/html;charset = utf-8");
                            response.getWriter().print(result);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description 调用百度语音识别API
     * @param pcmBytes
     * @return
     * @author liuyang
     * @blog http://www.pqsky.me
     * @date 2018年1月30日
     */
    public JSONObject speechBdApi(byte[] pcmBytes) {
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(appId, appKey, secretKey);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 调用接口
        org.json.JSONObject res = client.asr(pcmBytes, "pcm", 16000, null);
        return JSONObject.parseObject(res.toString());
    }

    /**
     * @Description MP3转换pcm
     * @param mp3Stream
     *            原始文件流
     * @return 转换后的二进制
     * @throws Exception
     * @author liuyang
     * @blog http://www.pqsky.me
     * @date 2018年1月30日
     */
    public byte[] mp3Convertpcm(InputStream mp3Stream) throws Exception {
        // 原MP3文件转AudioInputStream
        AudioInputStream mp3audioStream = AudioSystem.getAudioInputStream(mp3Stream);
        // 将AudioInputStream MP3文件 转换为PCM AudioInputStream
        AudioInputStream pcmaudioStream = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
                mp3audioStream);
        byte[] pcmBytes = IOUtils.toByteArray(pcmaudioStream);
        pcmaudioStream.close();
        mp3audioStream.close();
        return pcmBytes;
    }
}
