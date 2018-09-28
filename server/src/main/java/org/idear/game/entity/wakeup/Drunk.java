package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.endpoint.Player;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Switch;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Drunk extends Wakeup {

    @Override
    public boolean execute(Player player, JSONObject jsonObject) {
        if ("酒鬼".equals(player.poker) || "化身酒鬼".equals(player.poker)) {
            LinkedHashMap<Integer, String> deck = jsonObject.getObject("deck", new TypeReference<LinkedHashMap<Integer, String>>() {
            });
            Integer caller = player.seat;
            Integer target = jsonObject.getInteger("target");

            List<Movement> movements = new LinkedList<>();
            //交换但不能查看
            Switch aSwitch = new Switch(caller, caller, target);
            List<Movement> partMovement = aSwitch.cast(deck);
            movements.addAll(partMovement);
            //
            player.setMovements(movements);
            return true;
        }
        return false;
    }
}
