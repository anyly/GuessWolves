package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.endpoint.Player;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Rob;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Robber extends Wakeup {

    @Override
    public boolean execute(Player player, JSONObject jsonObject) {
        if ("强盗".equals(player.poker) || "化身强盗".equals(player.poker)) {
            LinkedHashMap<Integer, String> deck = jsonObject.getObject("deck", new TypeReference<LinkedHashMap<Integer, String>>() {
            });
            Integer caller = player.seat;
            Integer target = jsonObject.getInteger("target");

            List<Movement> movements = new LinkedList<>();
            //强牌后查看
            Rob rob = new Rob(caller, target);
            List<Movement> partMovement = rob.cast(deck);
            movements.addAll(partMovement);
            //
            player.setMovements(movements);
            return true;
        }
        return false;
    }
}
