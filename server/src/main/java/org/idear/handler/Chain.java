package org.idear.handler;

import org.idear.game.Stage;
import org.idear.game.entity.wakeup.Wakeup;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by idear on 2018/9/21.
 */
public class Chain {

    private Map<String, Context> data = new LinkedHashMap<>();

    private LinkedList<Stage> stages = new LinkedList<>();
    private LinkedList<String> keys = new LinkedList<>();

//    public Stage stage() {
//        return stages.getFirst();
//    }
    public String current() {
        return keys.getFirst();
    }

    public Chain setData(String key, Context context) {
        context.setChain(this);
        data.put(key, context);
        return this;
    }

    public Context getData(String key) {
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
     * @param wakeup
     * @return
     */
    public Chain add(Wakeup wakeup) {
        add(wakeup.getClass().getSimpleName()+"Action", wakeup);
        return this;
    }

    /**
     *
     * @param context
     * @return
     */
    public Chain setData(Context context) {
        Player player = context.getPlayer();
        String poker = player.getPoker();
        Wakeup wakeup = GameCenter.pokerAbility.get(poker);
        if (wakeup != null) {
            setData(wakeup.getClass().getSimpleName()+"Action", context);
        }
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
    public String resume(String key) {
        String key1 = keys.getFirst();
        if (key1.equals(key)) {
            poll();
            return start();
        }
        return null;
    }

    /**
     * 开始
     * @return
     */
    public String start() {
        Stage stage = null;
        String key = null;
        Context object = null;
        while ((stage = stages.getFirst()) != null) {
            key = keys.getFirst();
            object = data.get(key);
            if (stage.execute(object)) {
                System.out.println("行动["+key+"]  玩家["+(object!=null?object.getPlayer().getUser():">没有")+"]>[下一步]");
                poll();
            } else {
                System.out.println("行动["+key+"]  玩家["+(object!=null?object.getPlayer().getUser():">没有")+"]=[暂停]");
                return keys.getFirst();
            }
        }
        return null;

    }

    /**
     * 下个阶段
     * @return
     */
    public Chain next() {
        Stage stage = null;
        String key = null;
        Context object = null;
        if ((stage = stages.getFirst()) != null) {
            key = keys.getFirst();
            object = data.get(key);
            if (stage.execute(object)) {
                poll();
            } else {
                return this;
            }
        }
        return this;
    }

    /**
     * 向前一步
     * @return
     */
    public String forward() {
        poll();
        return start();
    }

    private Chain poll() {
        stages.poll();
        keys.poll();
        return this;
    }
}
