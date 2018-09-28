package org.idear.game.entity.movement;

import org.idear.game.entity.poker.Poker;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/26.
 * 翻看牌面
 */
public class Flop extends Motion {

    public Flop(Integer own, Integer...targets) {
        super("翻看", own, targets);
    }

    @Override
    public void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport) {
        Integer target = targets[0];
        String poker = deck.get(target);
        viewport.put(target, poker);
    }
}
