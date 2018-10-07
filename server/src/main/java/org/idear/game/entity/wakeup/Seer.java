package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Show;
import org.idear.handler.Context;
import org.idear.handler.Player;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Seer extends Wakeup {

    public boolean execute(Context context) {
        if (context == null) {
            return true;
        }
        Player player = context.getPlayer();
        LinkedHashMap<Integer, String> deck = context.getDeck();
        Integer[] targets = /*new Integer[]{3,4};//*/player.getTargets();
        if (targets == null) {
            String stage = this.getClass().getSimpleName();
            player.setStage(stage);
            player.endpoint().emit(stage, null);
            return false;
        }

        Integer caller = player.getSeat();
        List<Movement> movements = player.movements();
        //复制身份
        Show show = new Show(caller, targets);
        if (targets.length == 2) {
            show.setName("看牌"+targets[0]+","+targets[1]);
        } else {
            show.setName("看牌"+targets[0]);
        }
        Movement partMovement = show.cast(context);
        movements.add(partMovement);
        //
        player.setStage(null);
        player.endpoint().emit("syncGame", context.game().export(player));
        System.out.println("####玩家["+player.getUser()+"]["+player.getPoker()+"] 的视角为:"+ JSON.toJSONString(player.movements().get(player.movements().size()-1).getViewport()));
        return true;
    }
}
