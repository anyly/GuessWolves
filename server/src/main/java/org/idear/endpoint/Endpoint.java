package org.idear.endpoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.idear.CoherentMap;

import javax.websocket.Session;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by idear on 2018/9/28.
 */
public abstract class Endpoint<T extends Endpoint> extends UserEndpoint {
    public abstract Map<String, T> allUserSession();
    public abstract void fromOld(T from);

    @Override
    public JSONObject onLogin(JSONObject data) {
        JSONObject jsonObject = super.onLogin(data);
        T to = (T)this;
        T from = allUserSession().put(user, to);
        if (from != null) {
            fromOld(from);
        }
        return jsonObject;

    }

    @Override
    public boolean requireLogin() {
        if (super.requireLogin()) {
            T endpoint = allUserSession().get(user);
            if (endpoint != null && endpoint == this) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JSONObject onLogout(JSONObject data) {
        allUserSession().remove(user);
        return super.onLogout(data);
    }

    public void broadcastExcludeMe(String action, JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);

        LinkedList<T> list = new LinkedList(allUserSession().values());
        for (T endpoint: list) {
            try {
                if (endpoint != this) {
                    endpoint.session.getBasicRemote().sendText(jsonObject.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //this.session.getAsyncRemote().sendText(message);
        }
    }

    public void broadcast(String action, JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", action);
        jsonObject.put("data", data);

        LinkedList<T> list = new LinkedList(allUserSession().values());
        for (T endpoint: list) {
            try {
                endpoint.session.getBasicRemote().sendText(jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //this.session.getAsyncRemote().sendText(message);
        }
    }
}
