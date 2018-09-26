package org.idear.game.entity.movement;

import java.util.List;

/**
 * Created by idear on 2018/9/26.
 * 行动，原子操作，不可拆解的最小概念单元，不包含结果
 */
public abstract class Motion {
    protected Integer own;
    protected String type;
    protected List<Integer> targets;

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

    public List<Integer> getTargets() {
        return targets;
    }

    public void setTargets(List<Integer> targets) {
        this.targets = targets;
    }
}
