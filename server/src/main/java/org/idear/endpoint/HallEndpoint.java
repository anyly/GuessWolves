package org.idear.endpoint;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.handler.GameCenter;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedList;

/**
 * Created by idear on 2018/9/21.
 * 大厅，负责创建和销毁房间
 */
@ServerEndpoint("/hall")
public class HallEndpoint extends UserEndpoint {
    GameCenter gameCenter;
    public HallEndpoint() {
        gameCenter =  GameCenter.instance();
    }

    public JSONObject onNewGame(JSONObject data) {
        requireLogin();
        LinkedList<String> setting = data.getObject("poker", new TypeReference<LinkedList<String>>(){});
        int no = gameCenter.newGame(setting).getNo();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("no", no);
        return jsonObject;
    }

    public JSONObject onFindGame(JSONObject data) {
        Integer no = data.getInteger("no");
        if (no == null) {
            return null;
        }
        boolean exist = gameCenter.game(no) != null;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("exist", exist);
        return jsonObject;
    }

    @Override
    public JSONObject onLogin(JSONObject data) {
        String user = data.getString("user");
        boolean exist = false;
        if (user.equals(this.user)) {
            exist = true;
        } else {
            exist = userSession.containsKey(user);
        }

        JSONObject jsonObject = null;
//        if (exist) {
//            jsonObject = new JSONObject();
//            jsonObject.put("error", "用户名已存在");
//            return jsonObject;
//        }
        return super.onLogin(data);
    }

    public JSONObject onEditImg(JSONObject data) {
        requireLogin();
        this.img = data.getString("img");

        return null;
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        super.onMessage(message);
    }


    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        super.onOpen(session, config);
    }


    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(closeReason);
    }


    @OnError
    public void onError(Session session, Throwable error) {
        super.onError(session, error);
    }

}
