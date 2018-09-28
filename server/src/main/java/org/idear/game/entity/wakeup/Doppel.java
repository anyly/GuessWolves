package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.endpoint.Player;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Replicate;
import org.idear.game.entity.spell.Show;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Doppel extends Wakeup {

    @Override
    public boolean execute(Player player, JSONObject jsonObject) {
        if ("化身幽灵".equals(player.poker)) {
            Integer caller = player.seat;
            LinkedHashMap<Integer, String> deck = jsonObject.getObject("deck", new TypeReference<LinkedHashMap<Integer, String>>(){});
            Integer target = jsonObject.getInteger("target");

            List<Movement> movements = new LinkedList<>();
            //复制身份
            Replicate replicate = new Replicate(caller, target);
            List<Movement> partMovements = replicate.cast(deck);
            movements.addAll(partMovements);
            //查看复制的牌
            Show show = new Show(caller, caller, target);
            //
            player.setMovements(movements);
            return true;
        }
        return false;
    }
}
