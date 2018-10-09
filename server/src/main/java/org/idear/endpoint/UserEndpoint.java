package org.idear.endpoint;

import com.alibaba.fastjson.JSONObject;
import org.idear.CoherentMap;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by idear on 2018/9/21.
 */
public abstract class UserEndpoint extends OnlineEndpoint {

    public static CoherentMap<String, Session> userSession = new CoherentMap<>();
    public String user;
    public String img;

    //登录
    public JSONObject onLogin(JSONObject data) {
        this.user = data.getString("user");
        this.img = data.getString("img");
        /*
        if (userSession.containsKey(this.user)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("stage", "existUser");
            return jsonObject;
        } else {
            userSession.put(this.user, session);
        }
        */
        // 做挤下线
        Session session = userSession.put(this.user, this.session);
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 要求必须登录
     * @return
     */
    public boolean requireLogin() {
        if (user == null) {
            emit("login", null);
            return false;
        }
        return true;
    }

    public JSONObject onLogout(JSONObject data) {
        if (user != null) {
            userSession.remove(user);
            this.user = null;
            this.img = null;
        }
        return null;
    }

    @Override
    public void onClose(CloseReason closeReason) {
        if (user != null) {
            userSession.remove(user);
        }
        super.onClose(closeReason);
    }

    final public void emitAllUser(String action, JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);

        LinkedList<Session> list = new LinkedList<>(userSession.values());
        for (Session session: list) {
            try {
                session.getBasicRemote().sendText(jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //this.session.getAsyncRemote().sendText(message);
        }
    }
}
