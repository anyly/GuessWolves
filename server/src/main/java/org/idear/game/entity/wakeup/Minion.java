package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.CoherentMap;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Show;
import org.idear.handler.Context;
import org.idear.handler.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Minion extends Wakeup {

    public boolean execute(Context context) {
        if (context == null) {
            return true;
        }

        Player player = context.getPlayer();
        CoherentMap<Integer, Player> desktop = context.getDesktop();
        LinkedHashMap<Integer, String> deck = context.getDeck();

        List<Movement> movements = player.movements();
        // 找狼人
        Integer[] indexs = findWolves(desktop);
        String spellName = null;
        if (indexs.length>0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer i : indexs) {
                if (stringBuilder.length()>0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(i);
            }
            spellName = "狼人"+stringBuilder.toString();
        } else {
            spellName = "没有狼人";
        }
        // 爪牙看到狼
        Show show = new Show(player.getSeat(), indexs);

        show.setName(spellName);
        Movement partMovement = show.cast(context);
        movements.add(partMovement);

        player.endpoint().emit("syncGame", context.game().export(player));
        System.out.println("####玩家["+player.getUser()+"]["+player.getPoker()+"] 的视角为:"+ JSON.toJSONString(player.movements().get(player.movements().size()-1).getViewport()));
        player.setTargets(null);
        player.setStage(null);
        return true;

    }

    private Integer[] findWolves(CoherentMap<Integer, Player> desktop) {
        List<Integer> indexs = new ArrayList<>();
        for (int i=1; i<=desktop.size(); i++) {
            Player player = desktop.get(i);
            String poker = player.getPoker();
            if (poker.equals("狼人") || poker.equals("化身狼人")) {
                indexs.add(i);
            }
        }
        return indexs.toArray(new Integer[]{});
    }
}
