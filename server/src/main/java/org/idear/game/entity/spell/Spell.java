package org.idear.game.entity.spell;

import org.idear.game.entity.Movement;
import org.idear.game.entity.movement.Motion;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/26.
 * 法术等于若干个天赋顺序执行
 */
public abstract class Spell {
    protected Integer caster;
    protected Integer[] targets;
    protected String name;
    protected List<Motion> motions = new LinkedList<>();

    public Spell(String name, Integer caster, Integer...targets) {
        this.name = name;
        this.caster = caster;
        this.targets = targets;
    }

    public abstract void movements();

    public List<Movement> cast(LinkedHashMap<Integer, String> deck) {
        List<Movement> movements = new LinkedList<>();
        Movement movement = null;
        for (Motion motion: this.motions) {
            movement = new Movement(motion, movement);
            motion.doing(deck, movement.getViewport());
        }

        return movements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Motion> getMotions() {
        return motions;
    }

    public void setMotions(List<Motion> motions) {
        this.motions = motions;
    }
}
