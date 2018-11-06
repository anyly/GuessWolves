package com.idearfly.guessWolves.game.entity.spell;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 */
public class Rob extends Spell {
    public Rob(Integer caster, Integer... targets) {
        super("抢劫", caster, targets);
    }

    @Override
    protected void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport) {
        // 交换并查看
        Integer one = caster;
        Integer two = targets[0];
        String poker1 = deck.get(one);
        String poker2 = deck.get(two);

        deck.put(one, poker2);
        deck.put(two, poker1);

        viewport.put(one, show(poker2));

        viewport.put(two, show(poker1));
    }

    private String show(String poker) {
        if (poker.startsWith("化身") && !poker.equals("化身幽灵")) {
            // 只能看到化身之前的身份
            return "化身幽灵";
        }
        return poker;
    }
}
