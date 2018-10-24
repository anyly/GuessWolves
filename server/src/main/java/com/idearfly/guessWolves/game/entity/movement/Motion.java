package com.idearfly.guessWolves.game.entity.movement;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/26.
 * 行动，原子操作，不可拆解的最小概念单元，不包含结果
 */
public abstract class Motion {
    protected Integer own;
    protected String type;
    protected Integer[] targets;

    public Motion(String type, Integer own, Integer...targets) {
        this.own = own;
        this.type = type;
        this.targets = targets;
    }

    public abstract void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport);

    @Override
    public String toString() {
        return type.toString();
    }

    public Integer getOwn() {
        return own;
    }

    public void setOwn(Integer own) {
        this.own = own;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer[] getTargets() {
        return targets;
    }

    public void setTargets(Integer[] targets) {
        this.targets = targets;
    }
}
