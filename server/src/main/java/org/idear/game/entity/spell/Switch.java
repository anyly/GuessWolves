package org.idear.game.entity.spell;

import org.idear.game.entity.movement.Swap;

/**
 * Created by idear on 2018/9/27.
 */
public class Switch extends Spell {
    public Switch(Integer caster, Integer... targets) {
        super("交换", caster, targets);
    }

    @Override
    public void motions() {
        this.motions.add(new Swap(caster, targets));
    }
}
