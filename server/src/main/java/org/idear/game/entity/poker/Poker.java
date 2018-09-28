package org.idear.game.entity.poker;

/**
 * Created by idear on 2018/9/27.
 */
public abstract class Poker {
    protected String type;// 身份牌/牌堆
    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
