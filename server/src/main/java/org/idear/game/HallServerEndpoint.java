package org.idear.game;

import com.alibaba.fastjson.JSONObject;
import org.idear.game.entity.GameSetting;
import org.idear.service.UserService;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/21.
 * 大厅，负责创建和销毁房间
 */
@ServerEndpoint("/hall")
public class HallServerEndpoint extends UserService {
    public static Map<Integer, GameSetting> settingMap = new LinkedHashMap<>();
    //private UserService userService = UserService.instance();

    private static int noSeed = 1000;

    private int nextNo() {
        return ++noSeed;
    }

    public void onNewGame(Session session, JSONObject data) {
        GameSetting gameSetting = data.toJavaObject(GameSetting.class);
        settingMap.put(nextNo(), gameSetting);
    }

    public void onDeleteGame(Session session, JSONObject data) {
        int no = data.getInteger("no");
        if (no != 0) {
            settingMap.remove(no);
        }
    }


    
    @OnMessage
    public void onMessage(String message, Session session) {
        super.onMessage(message, session);
    }

   
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        super.onOpen(session, config);
    }

    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
    }

    
    @OnError
    public void onError(Session session, Throwable error) {
        super.onError(session, error);
    }

}
