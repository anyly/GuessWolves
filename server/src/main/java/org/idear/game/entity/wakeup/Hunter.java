package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.Player;

/**
 * Created by idear on 2018/9/27.
 */
public class Hunter extends Wakeup {

    @Override
    public boolean execute(Player player, JSONObject jsonObject) {
        return true;
    }
}
