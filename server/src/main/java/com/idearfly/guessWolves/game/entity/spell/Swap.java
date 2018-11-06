package com.idearfly.guessWolves.game.entity.spell;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 */
public class Swap extends Spell {
    public Swap(Integer caster, Integer... targets) {
        super("交换", caster, targets);
    }

    @Override
    protected void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport) {
        Integer one = targets[0];
        Integer two = targets[1];
        String poker1 = deck.get(one);
        String poker2 = deck.get(two);

        deck.put(one, poker2);
        deck.put(two, poker1);

        poker1 = viewport.remove(one);
        poker2 = viewport.remove(two);
        if (poker2 != null) {
            viewport.put(one, poker2);
        }
        if (poker1 != null) {
            viewport.put(two, poker1);
        }
    }
}
