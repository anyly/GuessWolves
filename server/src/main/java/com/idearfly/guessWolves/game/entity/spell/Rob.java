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
        // 实际交换
        Integer one = caster;
        Integer two = targets[0];

        String poker1 = deck.get(one);
        String poker2 =  deck.get(two);

        deck.put(one, poker2);
        deck.put(two, poker1);

        // 强盗视角
        String oldPoker1 = viewport.get(one);
        String oldPoker2 = viewport.get(two);
        if (oldPoker2 == null) {
            oldPoker2 = poker2;
        }

        viewport.put(one, show(oldPoker2));

        viewport.put(two, show(oldPoker1));
    }

    private String show(String poker) {
        if (poker.startsWith("化身")) {
            if (poker.equals("化身幽灵")) {
                // 化身幽灵本身已经是化身之前
            } else if (initPoker.startsWith("化身")) {
                // 初始为化身幽灵，结合自己的操作，相当于看到化身真是身份
            } else {
                // 只能看到化身之前的身份
                poker = "化身幽灵";
            }
        }
        return poker;
    }
}
