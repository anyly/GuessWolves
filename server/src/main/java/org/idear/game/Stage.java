package org.idear.game;

import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.handler.Context;
import org.idear.handler.Player;

/**
 * Created by idear on 2018/9/28.
 */
public interface Stage {
    boolean execute(Context context);
}
