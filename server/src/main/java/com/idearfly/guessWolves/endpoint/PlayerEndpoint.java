package com.idearfly.guessWolves.endpoint;

import com.idearfly.guessWolves.game.Game;
import com.idearfly.guessWolves.game.GameCenter;
import com.idearfly.guessWolves.game.Player;
import com.idearfly.timeline.websocket.GameEndpoint;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.List;

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
    public Game onSitdown(Integer seat){
        if (game.sitdown(seat, player)) {
            // 换座位,同步给其他人
            game.syncGame();
        }
        return game;
    }

    public void onReady(Boolean readyStatus) {
        game.setReadyStatus(player, readyStatus);
        if (!game.startGame()) {
            game.syncGame();
        }
    }

    public void onStartGame() {
        game.startGame();
    }

    public void onRestart(Boolean readyStatus) {
        game.setRestartStatus(player, readyStatus);
        game.syncStatus(player);
    }

    //////////////////////
    /**
     * 化身幽灵行动
     * @param target
     * @return
     */
    public Game onDoppel(Integer target) {
        if (target == null) {
            return null;
        }
        List<Integer> targets = this.player.getTargets();
        targets.add(target);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.doppel(player, target);
            return game;
        }
        return null;
    }

    /**
     * 狼人行动
     * @param target
     * @return
     */
    public Game onWolf(Integer target) {
        if (target == null) {
            return null;
        }
        List<Integer> targets = this.player.getTargets();
        targets.add(target);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.wolf(player, target);
            return game;
        }
        return null;
    }

    /**
     * 狼先知
     * @param target
     * @return
     */
    public Game onMysticWolf(Integer target) {
        if (target == null) {
            return null;
        }
        List<Integer> targets = this.player.getTargets();
        targets.add(target);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.mysticWolf(player);
            return game;
        }
        return null;
    }
    /**
     * 预言家行动
     * @param list
     * @return
     */
    public Game onSeer(List list) {
        List<Integer> targets = (List<Integer>) list;
        this.player.setTargets(targets);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.seer(player);
            return game;
        }
        return null;
    }

    /**
     * 见习预言家
     * @param target
     * @return
     */
    public Game onApprenticeSeer(Integer target) {
        if (target == null) {
            return null;
        }
        List<Integer> targets = this.player.getTargets();
        targets.add(target);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.apprenticeSeer(player);
            return game;
        }
        return null;
    }

    /**
     * 强盗行动
     * @param target
     * @return
     */
    public Game onRobber(Integer target) {
        if (target == null) {
            return null;
        }
        List<Integer> targets = this.player.getTargets();
        targets.add(target);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.robber(player);
            return game;
        }
        return null;
    }

    /**
     * 女巫
     * @param target
     * @return
     */
    public Game onWitch(Integer target) {
        if (target == null) {
            return null;
        }
        List<Integer> targets = this.player.getTargets();
        targets.add(target);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.witch(player);
            return game;
        }
        return null;
    }

    /**
     * 捣蛋鬼行动
     * @param list
     * @return
     */
    public Game onTroubleMarker(List list) {
        List<Integer> targets = (List<Integer>) list;
        this.player.setTargets(targets);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.troubleMarker(player);
            return game;
        }
        return null;
    }

    /**
     * 酒鬼行动
     * @param target
     * @return
     */
    public Game onDrunk(Integer target) {
        if (target == null) {
            return null;
        }
        List<Integer> targets = this.player.getTargets();
        targets.add(target);
        if (targets.size() > 0) {
            // 通知游戏继续
            game.drunk(player);
            return game;
        }
        return null;
    }

    /**
     * 轮流发言
     * @param speek
     */
    public void onSpeak(String speek) {
        game.speak(player, speek);
    }

    public Game onVote(Integer vote){
        game.vote(player, vote);
        return game;
    }

    public Game onHunter(Integer target) {
        game.hunter(player, target);
        return game;
    }
}
