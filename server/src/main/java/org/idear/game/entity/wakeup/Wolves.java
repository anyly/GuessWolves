package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.endpoint.Player;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Show;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Wolves extends Wakeup {


    public boolean execute(Player player, JSONObject jsonObject) {
        if ("狼人".equals(player.poker) || "化身狼人".equals(player.poker)) {
            LinkedHashMap<Integer, String> deck = jsonObject.getObject("deck", new TypeReference<LinkedHashMap<Integer, String>>(){});

            List<Movement> movements = new LinkedList<>();
            // 找狼人
            Integer[] indexs = findWolves(deck);
            // 每一只狼互相看到
            for (int i=0; i<indexs.length; i++) {
                Show show = new Show(indexs[i], indexs);
                List<Movement> partMovement = show.cast(deck);
                movements.addAll(partMovement);
            }
            player.setMovements(movements);
            return true;
        }
        return false;
    }

    private Integer[] findWolves(LinkedHashMap<Integer, String> deck) {
        List<Integer> indexs = new ArrayList<>();
        for (int i=0; i<deck.size(); i++) {
            String poker = deck.get(i);
            if (poker.equals("狼人") || poker.equals("化身狼人")) {
                indexs.add(i);
            }
        }
        return indexs.toArray(new Integer[]{});
    }
}
