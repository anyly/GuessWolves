package org.idear.endpoint;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.handler.Game;
import org.idear.handler.GameCenter;
import org.idear.handler.Player;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by idear on 2018/9/21.
 * 维护player实例
 */
@ServerEndpoint("/game/{room}")
public class PlayerEndpoint extends UserEndpoint {
    private GameCenter gameCenter;

    public PlayerEndpoint() {
        gameCenter = GameCenter.instance();
    }

    private Game game;
    private Player player;

    @OnMessage
    public void onMessage(String message, Session session) {
        super.onMessage(message);
    }

    @OnOpen
    public void onOpen(@PathParam("room") int room, Session session, EndpointConfig config) {
        super.onOpen(session, config);
        this.game = gameCenter.game(room);
    }

    @Override
    public synchronized JSONObject onLogin(JSONObject data) {
        JSONObject jsonObject = null;
        if ((jsonObject = loadGame()) != null) {
            // 加载游戏
        } else if ((jsonObject = loadUser(data)) != null) {
            // 加载用户
        } else if ((jsonObject = loadPlayer()) != null) {
            // 加载玩家
        } else {
            // 登录成功后, 同步给其他玩家
            if (player.getSeat() != null) {
                game.syncStatus(player);
            }

            jsonObject = game.export(player);
        }

        return jsonObject;
    }

    /**
     * 同步桌面
     */
//    private void syncGame() {
//        if (this.player.getSeat() != null) {
//            game.broadcast(player, "syncGame", game.export());
//        }
//    }

    private JSONObject loadGame() {
        if (game == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("stage", "notExistGame");
            return jsonObject;
        }
        return null;
    }

    private JSONObject loadUser(JSONObject data) {
        return super.onLogin(data);
    }

    private JSONObject loadPlayer() {
        player = game.getPlayer(user);
        if (player == null) {
            player = new Player(user, img, this);
            game.addPlayer(player);
        } else {
            //if (player.endpoint() == null) {
                player.endpoint(this);
                player.setImg(img);
           // } else {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("stage", "existUser");
//                return jsonObject;
            //}
        }
        return null;
    }

    @Override
    public synchronized JSONObject onLogout(JSONObject data) {
        if (player != null) {
            game.removePlayer(player);
        }
        return super.onLogout(data);
    }



    @OnClose
    public synchronized void onClose(Session session, CloseReason closeReason) {
        if (player != null) {
            player.endpoint(null);
            // 断开连接,同步状态
            //if (player.getSeat() != null) {
                game.syncStatus(player);
            //}
        }

        super.onClose(closeReason);
    }


    @OnError
    public void onError(Session session, Throwable error) {
        super.onError(session, error);
    }

    /////////////////////////
    /**
     * 选定座位
     * @param data
     * @return
     */
    public JSONObject onSitdown(JSONObject data){
        Integer seat = data.getInteger("seat");
        game.sitdown(seat, player);
        // 换座位,同步给其他人
        game.synchronise(player);
        return null;
    }

    public JSONObject onReady(JSONObject data) {
        boolean readyStatus = data.getBoolean("ready");
        if (game.setReadyStatus(player, readyStatus)) {
            // 游戏开始了

        }
        game.synchronise(player);

        return null;
    }

    public JSONObject onRestart(JSONObject data) {
        boolean readyStatus = data.getBoolean("ready");
        game.restart(player, readyStatus);
        game.synchronise(player);

        return null;
    }

    //////////////////////
    /**
     * 化身幽灵行动
     * @param data
     * @return
     */
    public JSONObject onDoppel(JSONObject data) {
        Integer[] targets = data.getObject("targets", new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.doppel(player, targets[0]);
            return game.export(player);
        }
        return null;
    }

    /**
     * 狼人行动
     * @param data
     * @return
     */
    public JSONObject onWolves(JSONObject data) {
        Integer[] targets = data.getObject("targets", new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.wolves(player, targets);
            return game.export(player);
        }
        return null;
    }

    /**
     * 预言家行动
     * @param data
     * @return
     */
    public JSONObject onSeer(JSONObject data) {
        Integer[] targets = data.getObject("targets", new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.seer(player, targets);
            return game.export(player);
        }
        return null;
    }

    /**
     * 强盗行动
     * @param data
     * @return
     */
    public JSONObject onRobber(JSONObject data) {
        Integer[] targets = data.getObject("targets", new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.robber(player, targets[0]);
            return game.export(player);
        }
        return null;
    }

    /**
     * 捣蛋鬼行动
     * @param data
     * @return
     */
    public JSONObject onTroubleMarker(JSONObject data) {
        Integer[] targets = data.getObject("targets", new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.troubleMarker(player, targets);
            return game.export(player);
        }
        return null;
    }

    /**
     * 酒鬼行动
     * @param data
     * @return
     */
    public JSONObject onDrunk(JSONObject data) {
        Integer[] targets = data.getObject("targets", new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.drunk(player, targets[0]);
            return game.export(player);
        }
        return null;
    }

    /////////////////////////
    public JSONObject onSpeek(JSONObject data) {
        String speek = data.getString("speek");
        game.speek(player, speek);
        return null;
    }

    public JSONObject onVote(JSONObject data){
        Integer vote = data.getInteger("vote");
        game.vote(player, vote);
        return null;
    }

    public JSONObject onHunter(JSONObject data) {
        Integer target = data.getInteger("target");
        game.hunter(player, target);
        return null;
    }
}
