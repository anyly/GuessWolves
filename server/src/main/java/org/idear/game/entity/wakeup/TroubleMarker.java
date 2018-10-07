package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.CoherentMap;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Switch;
import org.idear.handler.Context;
import org.idear.handler.Player;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class TroubleMarker extends Wakeup {

    @Override
    public boolean execute(Context context) {
        if (context == null) {
            return true;
        }
        Player player = context.getPlayer();
        LinkedHashMap<Integer, String> deck = context.getDeck();
        Integer caller = player.getSeat();
        Integer[] targets = /*new Integer[]{2,3};*/player.getTargets();
        if (targets == null) {
            String stage = this.getClass().getSimpleName();
            player.setStage(stage);
            player.endpoint().emit(stage, null);
            return false;
        }

        List<Movement> movements = player.movements();
        //交换后不查看
        Switch aSwitch = new Switch(caller, targets);
        aSwitch.setName("换牌"+targets[0]+","+targets[1]);
        Movement partMovement = aSwitch.cast(context);
        movements.add(partMovement);
        //
        player.setStage(null);
        System.out.println("####玩家["+player.getUser()+"]["+player.getPoker()+"] 的视角为:"+ JSON.toJSONString(player.movements().get(player.movements().size()-1).getViewport()));
        player.endpoint().emit("syncGame", context.game().export(player));
        return true;

    }
}