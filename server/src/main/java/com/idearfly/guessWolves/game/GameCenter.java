package com.idearfly.guessWolves.game;

import com.idearfly.timeline.websocket.BaseGameCenter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by idear on 2018/9/29.
 *
 */
public class GameCenter extends BaseGameCenter<Game> {

    /**
     * 随机数
     */
    private static Random random = new Random(System.currentTimeMillis());

    public static int randomInt(int number) {
        return random.nextInt(number);
    }

    /**
     * 阵营分类
     */
    public static Map<String, String> camp = new LinkedHashMap<>();
    static {

        camp.put("狼人", "狼人");
        camp.put("化身狼人", "狼人");
        camp.put("狼先知", "狼人");
        camp.put("化身狼先知", "狼人");
        camp.put("爪牙", "狼人");
        camp.put("化身爪牙", "狼人");
        camp.put("化身幽灵", "城镇");
        camp.put("守夜人", "城镇");
        camp.put("化身守夜人", "城镇");
        camp.put("预言家", "城镇");
        camp.put("化身预言家", "城镇");
        camp.put("见习预言家", "城镇");
        camp.put("化身见习预言家", "城镇");
        camp.put("强盗", "城镇");
        camp.put("化身强盗", "城镇");
        camp.put("女巫", "城镇");
        camp.put("化身女巫", "城镇");
        camp.put("捣蛋鬼", "城镇");
        camp.put("化身捣蛋鬼", "城镇");
        camp.put("酒鬼", "城镇");
        camp.put("化身酒鬼", "城镇");
        camp.put("失眠者", "城镇");
        camp.put("化身失眠者", "城镇");
        camp.put("村民", "城镇");
        camp.put("化身村民", "城镇");
        camp.put("猎人", "城镇");
        camp.put("化身猎人", "城镇");
        camp.put("皮匠", "皮匠");
        camp.put("化身皮匠", "皮匠");
    }

}
