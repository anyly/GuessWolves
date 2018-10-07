package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.handler.Context;
import org.idear.handler.Player;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 */
public class Hunter extends Wakeup {

    @Override
    public boolean execute(Context context) {
        if (context == null) {
            return true;
        }
        Player player = context.getPlayer();
        List<Integer> deadth = context.getDeadth();
        Integer[] targets = player.getTargets();

        if (deadth.contains(player.getSeat())) {
            // 猎人行动时间
            if (targets == null) {
                player.endpoint().emit(this.getClass().getSimpleName(), null);
                return false;
            }
            // 执行枪杀
            deadth.add(targets[0]);
        }
        return true;
    }
}