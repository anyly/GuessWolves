package org.idear.handler;

import java.util.*;

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
        camp.put("爪牙", "狼人");
        camp.put("化身爪牙", "狼人");
        camp.put("化身幽灵", "城镇");
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
