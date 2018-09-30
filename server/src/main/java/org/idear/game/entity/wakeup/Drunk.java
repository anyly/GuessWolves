package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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
public class Drunk extends Wakeup {

    @Override
    public boolean execute(Context context) {
        if (context == null) {
            return true;
        }
        Player player = context.getPlayer();
        LinkedHashMap<Integer, String> deck = context.getDeck();
        Integer caller = player.getSeat();
        Integer[] targets = new Integer[]{3};//player.getTargets();
        if (targets == null) {
            String stage = this.getClass().getSimpleName();
            player.setStage(stage);
            player.endpoint().emit(stage, null);
            return false;
        }
        Integer target = targets[0];

        List<Movement> movements = new LinkedList<>();
        //交换但不能查看
        Switch aSwitch = new Switch(caller, caller, target);
        List<Movement> partMovement = aSwitch.cast(context);
        movements.addAll(partMovement);
        //
        player.movements(movements);
        player.setStage(null);
        System.out.println("####玩家["+player.getUser()+"]["+player.getPoker()+"] 的视角为:"+ JSON.toJSONString(player.movements().get(player.movements().size()-1).getViewport()));
        return true;

    }
}
