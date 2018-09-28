package org.idear.game;

import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.Player;
import org.idear.game.entity.wakeup.Wakeup;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by idear on 2018/9/21.
 */
public class Chain {

    private Map<String, JSONObject> data = new LinkedHashMap<>();

    private LinkedList<Stage> stages = new LinkedList<>();
    private LinkedList<String> keys = new LinkedList<>();

    public Stage stage() {
        return stages.getFirst();
    }
    public String key() {
        return keys.getFirst();
    }

    public void setData(String key, JSONObject jsonObject) {
        data.put(key, jsonObject);
    }

    public JSONObject getData(String key) {
        return data.get(key);
    }

    /**
     *  添加阶段
     * @param key
     * @param stage
     * @return
     */
    public Chain add(String key, Stage stage) {
        stages.add(stage);
        keys.add(key);
        return this;
    }

    /**
     * 添加唤醒阶段
     * @return
     */
    public Chain addPlayerInput(Game game, String poker, Wakeup wakeup) {
        String next = wakeup.getClass().getSimpleName();
        String name = next + "Wakeup";
        add(new NotifyPlayer(game, name, poker, next));
        add(wakeup);
        return this;
    }

    /**
     * 添加唤醒阶段
     * @param notifyPlayer
     * @return
     */
    public Chain add(NotifyPlayer notifyPlayer) {
        add(notifyPlayer.getName(), notifyPlayer);
        return this;
    }

    /**
     * 添加唤醒阶段
     * @param wakeup
     * @return
     */
    public Chain add(Wakeup wakeup) {
        add(wakeup.getClass().getSimpleName(), wakeup);
        return this;
    }

    /**
     * 添加中断
     * @param key
     * @return
     */
    public Chain interrupt(String key) {
        add(key, null);
        return this;
    }

    /**
     * 恢复指定阶段, 若当前不是对应阶段不工作
     * @param key
     * @return
     */
    public String resume(Player player, String key) {
        String key1 = keys.getFirst();
        if (key1.equals(key)) {
            return start(player);
        }
        return null;
    }

    /**
     * 开始
     * @return
     */
    public String start(Player player) {
        Stage stage = null;
        String key = null;
        JSONObject jsonObject = null;
        while ((stage = stages.getFirst()) != null) {
            key = keys.getFirst();
            jsonObject = data.get(key);
            if (stage.execute(player,jsonObject)) {
                stages.poll();
                keys.poll();
            } else {
                return keys.getFirst();
            }
        }
        return null;

    }

    /**
     * 下个阶段
     * @return
     */
    public Chain next(Player player) {
        Stage stage = null;
        String key = null;
        JSONObject jsonObject = null;
        if ((stage = stages.getFirst()) != null) {
            key = keys.getFirst();
            jsonObject = data.get(key);
            if (stage.execute(player, jsonObject)) {
                stages.poll();
                keys.poll();
            } else {
                return this;
            }
        }
        return this;
    }
}
