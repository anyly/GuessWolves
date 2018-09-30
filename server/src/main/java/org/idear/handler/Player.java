package org.idear.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.Movement;

import javax.websocket.Session;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/29.
 */
public class Player {
    private String user;// 用户名,唯一标志
    private String img;// 头像
    private PlayerEndpoint endpoint;// 连接端,null为断开连接

    private Integer seat;// 座位号
    private String poker;// 身份牌
    private boolean ready;// 是否就绪, 就绪好了就开始游戏

    private String stage;//  玩家所处阶段, 无指定则与游戏总进度一直

    private Integer[] targets;// 行动时选择技能目标

    private List<String> speaks;//发言阶段

    private int vote;// 投票

    private boolean win;// 胜利或失败

    private List<Movement> movements= new LinkedList<>();// 行动结果, viewport为最后所看到的牌面

    public Player() {
    }

    public Player(String user, String img, PlayerEndpoint endpoint) {
        this.user = user;
        this.img = img;
        this.endpoint = endpoint;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public PlayerEndpoint endpoint() {
        return endpoint;
    }

    public void endpoint(PlayerEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public boolean getDisconnect() {
        return endpoint == null;
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

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public List<Movement> movements() {
        return movements;
    }

    public void movements(List<Movement> movements) {
        this.movements = movements;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
