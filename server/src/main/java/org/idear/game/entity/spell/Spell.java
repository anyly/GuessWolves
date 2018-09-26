package org.idear.game.entity.spell;

import org.idear.game.Context;
import org.idear.game.entity.Movement;
import org.idear.game.entity.movement.Motion;

import java.util.List;

/**
 * Created by idear on 2018/9/26.
 * 法术等于若干个天赋顺序执行
 */
public abstract class Spell {
    private String name;
    private List<Motion> movements;

    public abstract List<Movement> cast(Context context);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Motion> getMovements() {
        return movements;
    }

    public void setMovements(List<Motion> movements) {
        this.movements = movements;
    }
}
