package org.idear.game.entity.wakeup;

import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.Player;
import org.idear.game.Stage;

import java.util.LinkedHashMap;

/**
 * Created by idear on 2018/9/26.
 * 职业=由技能组成,提供执行技能的方法
 */
public abstract class Wakeup implements Stage {

    public abstract boolean execute(Player player, JSONObject jsonObject);

}
