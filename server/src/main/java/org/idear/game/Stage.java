package org.idear.game;

import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.Player;

/**
 * Created by idear on 2018/9/28.
 */
public interface Stage {
    boolean execute(Player player, JSONObject jsonObject);
}
