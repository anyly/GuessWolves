package org.idear.game.entity.spell;

import org.idear.game.entity.movement.Flop;

/**
 * Created by idear on 2018/9/27.
 */
public class Show extends Spell {
    public Show(Integer caster, Integer... targets) {
        super("查看", caster, targets);
    }

    @Override
    public void movements() {
        this.motions.add(new Flop(caster, targets));
    }
}
