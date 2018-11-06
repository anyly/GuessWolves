package com.idearfly.guessWolves.game.entity.spell;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 */
public class Replicate extends Spell {

    public Replicate(Integer caster, Integer... targets) {
        super("复制", caster, targets);
    }

    @Override
    public void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport) {
        Integer target = targets[0];
        String copySource = deck.get(target);
        String newPersona = "化身" + copySource;
        deck.put(this.caster, newPersona);

        viewport.put(target, copySource);
        viewport.put(this.caster, newPersona);
    }
}
