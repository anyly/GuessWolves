package org.idear.endpoint;

import com.alibaba.fastjson.JSONObject;
import org.idear.CoherentMap;
import org.idear.game.Game;
import org.idear.game.entity.Movement;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idear on 2018/9/21.
 *
 */
@ServerEndpoint("/game/{room}")
public class Player extends Endpoint<Player> {
    public static Map<Integer, Game> gameMap = new LinkedHashMap<>();

    public List<Movement> movements;
    public Game game;
    public Integer room;
    public Integer seat;
    public String poker;

    public List<Movement> getMovements() {
        return movements;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }

    @Override
    public Map<String, Player> allUserSession() {
        return this.game.players;
    }

    @Override
    public void fromOld(Player from) {
        this.movements = from.movements;
        this.seat = from.seat;
        this.poker = from.poker;
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        super.onMessage(message);
    }

    @OnOpen
    public void onOpen(@PathParam("room") int room, Session session, EndpointConfig config) {
        super.onOpen(session, config);
        this.room = room;
        this.game = gameMap.get(room);
        if (game == null) {
            emit("notExistGame", null);
        }
    }

    @Override
    public JSONObject onLogin(JSONObject data) {
        JSONObject jsonObject = super.onLogin(data);
        game.disconnect.remove(user);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        String stage = loadGame();

        JSONObject desktop = game.desktop();

        if (this.seat != null) {// 如果有座位, 恢复连接, 需要广播
            updateSeat(desktop);
        }
        jsonObject.put("desktop", desktop);
        jsonObject.put("movements", movements);
        jsonObject.put("stage", stage);
        return jsonObject;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        game.disconnect.add(user);
        if (this.seat != null) {// 如果有座位，断开连接，需要广播
            updateSeat();
        }
        super.onClose(closeReason);
    }


    @OnError
    public void onError(Session session, Throwable error) {
        super.onError(session, error);
    }

    String loadGame() {
        return game.play(this);
    }
    public Integer getSeat() {
        return game.getSeat(user);
    }

    public String getPoker() {
        return game.getPoker(user);
    }

    public JSONObject onSitdown(JSONObject data){
        Integer seat = data.getInteger("seat");
        this.seat = seat;
        game.sitdown.put(user, seat);
        // 换座位广播给所有人
        updateSeat();
        return null;
    }

    private void updateSeat() {
        updateSeat(null);
    }

    private void updateSeat(JSONObject jsonObject) {
        if (jsonObject == null) {
            jsonObject = game.desktop();
        }
        this.broadcastExcludeMe("updateSeat", jsonObject);
    }

    public JSONObject onReady() {
        game.ready.add(user);
        if (game.ready.size() == game.sitdown.size()) {
            //开始
        }
        return null;
    }
}
