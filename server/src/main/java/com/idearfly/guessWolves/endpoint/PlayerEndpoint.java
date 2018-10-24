package com.idearfly.guessWolves.endpoint;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.idearfly.guessWolves.game.Game;
import com.idearfly.guessWolves.game.GameCenter;
import com.idearfly.guessWolves.game.Player;
import com.idearfly.timeline.websocket.GameEndpoint;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by idear on 2018/9/21.
 * 维护player实例
 */
@ServerEndpoint("/game/{no}")
public class PlayerEndpoint extends GameEndpoint<GameCenter, Game, Player> {

    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        if (player != null) {
            player.endpoint(null);
            game.syncStatus(player);
        }
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
        game.syncSeat(player);
        return null;
    }

    public JSONObject onReady(JSONObject data) {
        boolean readyStatus = data.getBoolean("ready");
        if (game.setReadyStatus(player, readyStatus)) {
            // 游戏开始了

        }
        game.syncStatus(player);

        return null;
    }

    public JSONObject onRestart(JSONObject data) {
        boolean readyStatus = data.getBoolean("ready");
        game.restart(player, readyStatus);
        game.syncStatus(player);

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
