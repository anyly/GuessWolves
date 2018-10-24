package com.idearfly.guessWolves.game.entity.spell;


import com.idearfly.guessWolves.game.entity.movement.Copy;

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
