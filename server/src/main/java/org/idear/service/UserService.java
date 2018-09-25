package org.idear.service;

import com.alibaba.fastjson.JSONObject;

import javax.websocket.Session;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by idear on 2018/9/21.
 */
public class UserService extends OnlineService {

    public static Map<String, Session> userSession = new LinkedHashMap<>();

    protected String userKey = "user";

    private static UserService __ = new UserService();

    public static UserService instance() {
        return __;
    }

    protected UserService() {
        super();
    }

    //登录
    public JSONObject onLogin(Session session, JSONObject data) {
        String user = data.getString("user");
        userSession.put(user, session);
        //session.getPathParameters().put(userKey, user);
        return null;
    }

    public JSONObject onLogout(Session session, JSONObject data) {
        String user = data.getString("user");
        userSession.remove(user);
        return null;
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
