package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.endpoint.Player;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Show;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Seer extends Wakeup {

    public boolean execute(Player player, JSONObject jsonObject) {
        if ("预言家".equals(player.poker) || "化身预言家".equals(player.poker)) {
            LinkedHashMap<Integer, String> deck = jsonObject.getObject("deck", new TypeReference<LinkedHashMap<Integer, String>>() {
            });
            Integer[] targets = jsonObject.getObject("targets", new TypeReference<Integer[]>() {
            });

            Integer caller = player.seat;
            List<Movement> movements = new LinkedList<>();
            //复制身份
            Show show = new Show(caller, targets);
            List<Movement> partMovement = show.cast(deck);
            movements.addAll(partMovement);
            //
            player.setMovements(movements);
            return true;
        }
        return false;
    }
}
