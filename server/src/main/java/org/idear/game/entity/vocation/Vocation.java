package org.idear.game.entity.vocation;

import org.idear.game.Context;
import org.idear.game.entity.Deck;
import org.idear.game.entity.Movement;

import java.util.List;

/**
 * Created by idear on 2018/9/26.
 * 职业=由技能组成,提供执行技能的方法
 */
public abstract class Vocation {
    protected String name;

    public abstract List<Movement> execute(Context context);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
