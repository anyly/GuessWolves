package org.idear.game.entity.spell;

import org.idear.game.entity.movement.Flop;
import org.idear.game.entity.movement.Swap;

/**
 * Created by idear on 2018/9/27.
 */
public class Rob extends Spell {
    public Rob(Integer caster, Integer... targets) {
        super("抢劫", caster, targets);
    }

    @Override
    public void motions() {
        // 交换之后查看
        Integer[] newTarget = new Integer[] {caster, targets[0]};
        this.motions.add(new Swap(caster, newTarget));
        this.motions.add(new Flop(caster, newTarget));
    }
}
