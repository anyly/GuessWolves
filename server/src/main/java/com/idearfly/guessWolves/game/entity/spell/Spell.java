package com.idearfly.guessWolves.game.entity.spell;


import com.idearfly.guessWolves.game.Player;
import com.idearfly.guessWolves.game.entity.Movement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idear on 2018/9/26.
 * 法术等于若干个天赋顺序执行
 */
public abstract class Spell {
    protected Integer caster;
    protected Integer[] targets;
    protected String name;

    public Spell(String name, Integer caster, Integer...targets) {
        this.name = name;
        this.caster = caster;
        this.targets = targets;
    }

    public Movement cast(LinkedHashMap<Integer, String> deck, Player player) {
        List<Movement> movements = player.getMovements();
        Movement movement = null;
        if (movements.size()>0) {
            movement = movements.get(movements.size()-1);
        }
        return cast(deck, movement);
    }

    public Movement cast(LinkedHashMap<Integer, String> deck, Movement prev) {
        Movement movement = new Movement(prev);
        movement.setCaller(caster);
        movement.setTargets(targets);
        doing(deck, movement.getViewport());
        return movement;
    }

    protected abstract void doing(LinkedHashMap<Integer, String> deck, Map<Integer, String> viewport);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
