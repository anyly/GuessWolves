package org.idear.game;

import com.alibaba.fastjson.JSONObject;
import org.idear.service.UserService;

import javax.websocket.Session;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/21.
 */
public class Room/* extends UserService*/ {
    public static Map<Integer, Room> roomMap = new LinkedHashMap<>();

    Map<String, Session> roomSessions = new LinkedHashMap<>();

    //@Override
    protected JSONObject httpResponse(Session session, String action, JSONObject data) {
        return null;
    }

    //@Override
    protected void wsReceive(Session session, String action, JSONObject data) {

    }
}
