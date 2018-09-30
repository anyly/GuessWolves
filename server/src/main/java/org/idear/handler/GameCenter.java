package org.idear.handler;

import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.wakeup.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by idear on 2018/9/29.
 *
 */
public class GameCenter extends Handler {
    private static GameCenter _singleton = new GameCenter();

    private GameCenter () {
        init();
    }

    public static GameCenter instance() {
        return _singleton;
    }

    public static void loadFor() {

    }

    /**
     * 房间号 = 游戏
     */
    private static Map<Integer, Game> games = new LinkedHashMap<>();

    private static int noSeed = 1000;

    /**
     * 身份牌和处理函数
     */
    public static Map<String, Wakeup> pokerAbility = new LinkedHashMap<>();
    /**
     * 阵营分类
     */
    public static Map<String, String> camp = new LinkedHashMap<>();
    static {
        pokerAbility.put("化身幽灵", new Doppel());
        pokerAbility.put("狼人", new Wolves());
        pokerAbility.put("化身狼人", new Wolves());
        pokerAbility.put("爪牙", new Minion());
        pokerAbility.put("化身爪牙", new AsMinion());
        pokerAbility.put("守夜人", new Mason());
        pokerAbility.put("化身守夜人", new Mason());
        pokerAbility.put("预言家", new Seer());
        pokerAbility.put("化身预言家", new AsSeer());
        pokerAbility.put("强盗", new Robber());
        pokerAbility.put("化身强盗", new AsRobber());
        pokerAbility.put("捣蛋鬼", new TroubleMarker());
        pokerAbility.put("化身捣蛋鬼", new AsTroubleMarker());
        pokerAbility.put("酒鬼", new Drunk());
        pokerAbility.put("化身酒鬼", new AsDrunk());
        pokerAbility.put("失眠者", new Insomniac());
        pokerAbility.put("化身失眠者", new Insomniac());

        camp.put("狼人", "狼人");
        camp.put("化身狼人", "狼人");
        camp.put("爪牙", "狼人");
        camp.put("化身爪牙", "狼人");
        camp.put("守夜人", "城镇");
        camp.put("化身守夜人", "城镇");
        camp.put("预言家", "城镇");
        camp.put("化身预言家", "城镇");
        camp.put("强盗", "城镇");
        camp.put("化身强盗", "城镇");
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
    void init() {

    }

    private int nextNo() {
        return ++noSeed;
    }

    public Game newGame(List<String> setting) {
        Integer no = nextNo();
        Game game = new Game(no, setting);
        games.put(no, game);
        return game;
    }

    public Game game(int no) {
        return games.get(no);
    }

}
