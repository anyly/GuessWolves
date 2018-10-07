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
public class Wolves extends Wakeup {


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
        String string = "";
        if (indexs.length == 1) {
            Integer[] targets = player.getTargets();
            if (targets == null) {
                //
                String stage = this.getClass().getSimpleName();
                player.setStage(stage);
                player.endpoint().emit(stage, context.game().export(player));
                return false;
            }
            ////
            for (Integer target: targets) {
                if (string.length()>0) {
                    string += ",";
                }
                string += target.toString();
            }
            string = "孤狼>" + string;
            // 查看底牌
            Show show = new Show(player.getSeat(), targets);
            show.setName(string);
            Movement partMovement = show.cast(context);
            movements.add(partMovement);
            player.endpoint().emit("syncGame", context.game().export(player));
            System.out.println("####玩家["+player.getUser()+"]["+player.getPoker()+"] 的视角为:"+ JSON.toJSONString(player.movements().get(player.movements().size()-1).getViewport()));
            return true;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Integer ii : indexs) {
            if (stringBuilder.length()>0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(ii);
        }
        string = "同伴"+stringBuilder.toString();


        // 每一只狼互相看到
        for (int i=0; i<indexs.length; i++) {
            Player team = desktop.get(indexs[i]);
            if (team == null) {
                continue;
            }
            Show show = new Show(indexs[i], indexs);

            show.setName(string);
            Movement partMovement = show.cast(context);
            team.movements().add(partMovement);
            team.endpoint().emit("syncGame", context.game().export(player));
            System.out.println("####玩家["+team.getUser()+"]["+team.getPoker()+"] 的视角为:"+ JSON.toJSONString(team.movements().get(team.movements().size()-1).getViewport()));
        }
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
