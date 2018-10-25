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
    /**
     * 断线重连, 如果已就坐,同步座位状态
     * @return
     */
    @Override
    protected void resumeGame() {
        if (player.getSeat() != null) {
            // 已就坐,断线重连
            game.syncStatus(player);
        }
    }

    public void onClose(Session session, CloseReason closeReason) {
        if (player != null) {
            player.endpoint(null);
            game.syncStatus(player);
        }
        super.onClose(session, closeReason);
    }

    /////////////////////////
    /**
     * 选定座位
     * @param seat
     * @return
     */
    public void onSitdown(Integer seat){
        game.sitdown(seat, player);
        // 换座位,同步给其他人
        game.syncSeat(player);
    }

    public void onReady(Boolean readyStatus) {
        if (game.setReadyStatus(player, readyStatus)) {
            // 游戏开始了

        }
        game.syncStatus(player);
    }

    public void onRestart(Boolean readyStatus) {
        game.restart(player, readyStatus);
        game.syncStatus(player);
    }

    //////////////////////
    /**
     * 化身幽灵行动
     * @param jsonArray
     * @return
     */
    public Game onDoppel(JSONObject jsonArray) {
        Integer[] targets = jsonArray.toJavaObject(new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.doppel(player, targets[0]);
            return game;
        }
        return null;
    }

    /**
     * 狼人行动
     * @param jsonArray
     * @return
     */
    public Game onWolves(JSONObject jsonArray) {
        Integer[] targets = jsonArray.toJavaObject(new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.wolves(player, targets);
            return game;
        }
        return null;
    }

    /**
     * 预言家行动
     * @param jsonArray
     * @return
     */
    public Game onSeer(JSONObject jsonArray) {
        Integer[] targets = jsonArray.toJavaObject(new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.seer(player, targets);
            return game;
        }
        return null;
    }

    /**
     * 强盗行动
     * @param jsonArray
     * @return
     */
    public Game onRobber(JSONObject jsonArray) {
        Integer[] targets = jsonArray.toJavaObject(new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.robber(player, targets[0]);
            return game;
        }
        return null;
    }

    /**
     * 捣蛋鬼行动
     * @param jsonArray
     * @return
     */
    public Game onTroubleMarker(JSONObject jsonArray) {
        Integer[] targets = jsonArray.toJavaObject(new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.troubleMarker(player, targets);
            return game;
        }
        return null;
    }

    /**
     * 酒鬼行动
     * @param jsonArray
     * @return
     */
    public Game onDrunk(JSONObject jsonArray) {
        Integer[] targets = jsonArray.toJavaObject(new TypeReference<Integer[]>(){});
        this.player.setTargets(targets);
        if (targets.length > 0) {
            // 通知游戏继续
            game.drunk(player, targets[0]);
            return game;
        }
        return null;
    }

    /////////////////////////
    public void onSpeek(String speek) {
        game.speek(player, speek);
    }

    public void onVote(Integer vote){
        game.vote(player, vote);
    }

    public void onHunter(Integer target) {
        game.hunter(player, target);
    }
}
