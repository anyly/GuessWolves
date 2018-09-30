package org.idear.game.entity.spell;

import org.idear.game.entity.movement.Copy;

/**
 * Created by idear on 2018/9/27.
 */
public class Replicate extends Spell {

    public Replicate(Integer caster, Integer... targets) {
        super("复制", caster, targets);
    }

    @Override
    public void motions() {
        this.motions.add(new Copy(caster, targets));
    }

}
