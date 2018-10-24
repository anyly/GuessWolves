package com.idearfly.guessWolves.game.entity.movement;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 */
public class Copy extends Motion {

    public Copy(Integer own, Integer...targets) {
        super("复制", own, targets);
    }

    @Override
    public void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport) {
        Integer target = targets[0];
        String copySource = deck.get(target);
        String newPersona = "化身" + copySource;
        deck.put(this.own, newPersona);

        viewport.put(target, copySource);
        viewport.put(this.own, newPersona);
    }
}
