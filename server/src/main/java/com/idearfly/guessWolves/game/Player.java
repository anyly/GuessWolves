package com.idearfly.guessWolves.game;


import com.idearfly.guessWolves.game.entity.Movement;
import com.idearfly.timeline.websocket.BasePlayer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/29.
 */
public class Player extends BasePlayer {

    private Integer seat;// 座位号
    private String poker;// 身份牌
    private boolean ready;// 是否就绪, 就绪好了就开始游戏

    private Integer[] targets;// 行动时选择技能目标

    private List<String> speaks = new LinkedList<>();//发言阶段

    private Integer vote;// 投票

    private boolean win;// 胜利或失败

    private List<Movement> movements= new LinkedList<>();// 行动结果, viewport为最后所看到的牌面

    public Player() {
    }

    public Integer getSeat() {
        return seat;
    }

    public void setSeat(Integer seat) {
        this.seat = seat;
    }

    public String getPoker() {
        return poker;
    }

    public void setPoker(String poker) {
        this.poker = poker;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean getDisconnect() {
        return endpoint() == null;
    }

    public Integer[] getTargets() {
        return targets;
    }

    public void setTargets(Integer[] targets) {
        this.targets = targets;
    }

    public List<String> getSpeaks() {
        return speaks;
    }

    public void setSpeaks(List<String> speaks) {
        this.speaks = speaks;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public List<Movement> getMovements() {
        return movements;
    }

    public void setMovements(List<Movement> movements) {
        this.movements = movements;
    }
/**
     * 当前视线
     * @return
     */
//    public Map<Integer, String> getViewport() {
//        if (movements.size()>0) {
//
//            return movements.get(movements.size() - 1);
//        }
//        return null;
//    }
}
