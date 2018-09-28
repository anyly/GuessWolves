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
public class Insomniac extends Wakeup {

    @Override
    public boolean execute(Player player, JSONObject jsonObject) {
        if ("失眠者".equals(player.poker) || "化身失眠者".equals(player.poker)) {
            LinkedHashMap<Integer, String> deck = jsonObject.getObject("deck", new TypeReference<LinkedHashMap<Integer, String>>() {
            });
            Integer caller = player.seat;

            List<Movement> movements = new LinkedList<>();
            //看牌
            Show show = new Show(caller, caller);
            List<Movement> partMovement = show.cast(deck);
            movements.addAll(partMovement);
            //
            player.setMovements(movements);
            return true;
        }
        return false;
    }
}
