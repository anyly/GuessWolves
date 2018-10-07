package org.idear.game.entity;

import org.idear.game.entity.movement.Motion;
import org.idear.game.entity.spell.Spell;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 * 动作帧,具体而完整的行动，包含结果
 */
public class Movement {
    private Spell spell;//行动说明
    private Map<Integer, String> viewport;//所看到的牌面

    public Movement(Spell spell, Movement prev) {
        this.spell = spell;
        if (prev == null) {
            viewport = new LinkedHashMap<>();
        } else {
            viewport = new LinkedHashMap<>(prev.viewport);
        }
    }

    public Spell getSpell() {
        return spell;
    }

    public void setSpell(Spell spell) {
        this.spell = spell;
    }

    public Map<Integer, String> getViewport() {
        return viewport;
    }

    public void setViewport(Map<Integer, String> viewport) {
        this.viewport = viewport;
    }
}
