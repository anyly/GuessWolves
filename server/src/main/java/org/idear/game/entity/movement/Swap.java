package org.idear.game.entity.movement;

import org.idear.game.entity.poker.Poker;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/26.
 * 交换牌面
 */
public class Swap extends Motion {

    public Swap(Integer own, Integer... targets) {
        super("交换", own, targets);
    }

    @Override
    public void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport) {
        Integer one = targets[0];
        Integer two = targets[1];
        String poker1 = deck.get(one);
        String poker2 = deck.get(two);

        viewport.put(one, poker2);
        viewport.put(two, poker1);
    }
}
