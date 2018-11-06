package com.idearfly.guessWolves.game.entity.spell;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 */
public class Reveal extends Spell {
    public Reveal(Integer caster, Integer... targets) {
        super("揭示", caster, targets);
    }

    @Override
    protected void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport) {
        for (Integer target: targets) {
            String poker = deck.get(target);
            if (poker.startsWith("化身") && !poker.equals("化身幽灵")) {
                // 只能看到化身之后的身份
                poker = poker.substring("化身".length());
            }
            viewport.put(target, poker);
        }
    }
}
