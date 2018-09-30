package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Replicate;
import org.idear.game.entity.spell.Show;
import org.idear.handler.Context;
import org.idear.handler.Player;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Doppel extends Wakeup {

    @Override
    public boolean execute(Context context) {
        if (context == null) {
            return true;
        }

        Player player = context.getPlayer();
        LinkedHashMap<Integer, String> deck = context.getDeck();
        Integer[] targets = new Integer[]{2};//player.getTargets();
        if (targets == null) {
            //
            String stage = this.getClass().getSimpleName();
            player.setStage(stage);
            player.endpoint().emit(stage, null);
            return false;
        }
        Integer target = targets[0];

        Integer caller = player.getSeat();

        List<Movement> movements = player.movements();
        //复制身份
        Replicate replicate = new Replicate(caller, target);
        List<Movement> partMovements = replicate.cast(context);
        movements.addAll(partMovements);
        //查看复制的牌
        Show show = new Show(caller, caller, target);
        // 化身之后, 得到新技能
        player.setPoker(deck.get(player.getSeat()));
        context.getChain().setData(context);
        //
        System.out.println("####玩家["+player.getUser()+"]["+player.getPoker()+"] 的视角为:"+ JSON.toJSONString(player.movements().get(player.movements().size()-1).getViewport()));


        player.setTargets(null);
        player.setStage(null);
        return true;
    }

}
