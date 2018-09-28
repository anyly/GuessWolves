package org.idear.endpoint;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.game.Game;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedList;

/**
 * Created by idear on 2018/9/21.
 * 大厅，负责创建和销毁房间
 */
@ServerEndpoint("/hall")
public class HallEndpoint extends UserEndpoint {

    private static int noSeed = 1000;

    private int nextNo() {
        return ++noSeed;
    }

    public JSONObject onNewGame(JSONObject data) {
        requireLogin();
        LinkedList<String> setting = data.getObject("poker", new TypeReference<LinkedList<String>>(){});
        Game game = new Game(setting);
        Integer no = nextNo();
        Player.gameMap.put(no, game);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("no", no);
        return jsonObject;
    }

    public JSONObject onFindGame(JSONObject data) {
        Integer no = data.getInteger("no");
        if (no == null) {
            return null;
        }
        boolean exist = Player.gameMap.containsKey(no);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("exist", exist);
        return jsonObject;
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
