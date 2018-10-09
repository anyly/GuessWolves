package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Spell;
import org.idear.handler.Context;
import org.idear.handler.Player;

import java.util.*;

/**
 * Created by idear on 2018/9/27.
 */
public class Hunter extends Wakeup {

    @Override
    public boolean execute(Context context) {
        if (context == null) {
            return true;
        }
        Player player = context.getPlayer();
        List<Movement> movements = player.movements();
        Set<Integer> deadth = context.getDeadth();
        Integer[] targets = player.getTargets();

        if (deadth.contains(player.getSeat())) {
            // 猎人行动时间
            if (targets == null) {
                player.endpoint().emit(this.getClass().getSimpleName(), null);
                return false;
            }
            // 执行枪杀
            Integer target = targets[0];

            Spell spell = new Spell(
                    "枪杀"+target+"号",
                    player.getSeat(),
                    target) {
                @Override
                public void motions() {

                }

                @Override
                public Movement cast(LinkedHashMap<Integer, String> deck, Movement prev) {
                    deadth.add(targets[0]);
                    return new Movement(this, prev);
                }
            };
            Movement movement = spell.cast(context);
            movements.add(movement);
            System.out.println("####玩家["+player.getUser()+"]["+player.getPoker()+"] 的视角为:"+ JSON.toJSONString(player.movements().get(player.movements().size()-1).getViewport()));
            player.endpoint().emit("syncGame", context.game().export(player));
        }
        player.setTargets(null);
        player.setStage(null);
        return true;
    }
}
