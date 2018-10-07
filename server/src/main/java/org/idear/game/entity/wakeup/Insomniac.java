package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.Movement;
import org.idear.game.entity.spell.Show;
import org.idear.handler.Context;
import org.idear.handler.Player;
import org.idear.util.StringUtil;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/27.
 */
public class Insomniac extends Wakeup {

    @Override
    public boolean execute(Context context) {
        if (context == null) {
            return true;
        }
        Player player = context.getPlayer();
        LinkedHashMap<Integer, String> deck = context.getDeck();
        Integer caller = player.getSeat();

        List<Movement> movements = player.movements();
        //看牌
        Show show = new Show(caller, caller);
        Movement partMovement = show.cast(context);
        show.setName("失>"+ StringUtil.simplePokerName(deck.get(player.getSeat())));
        movements.add(partMovement);
        //
        System.out.println("####玩家["+player.getUser()+"]["+player.getPoker()+"] 的视角为:"+ JSON.toJSONString(player.movements().get(player.movements().size()-1).getViewport()));
        return true;
    }
}
