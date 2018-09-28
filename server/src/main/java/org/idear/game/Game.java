package org.idear.game;

import com.alibaba.fastjson.JSONObject;
import org.idear.CoherentMap;
import org.idear.endpoint.Player;
import org.idear.game.entity.wakeup.*;

import java.util.*;

/**
 * Created by idear on 2018/9/21.
 * 牌局, 座位有关,与用户无关
 */
public class Game {
    public List<String> setting;// 选牌
    public Map<String, Player> players = new LinkedHashMap<>();// 玩家信息，进入房间后记录
    public CoherentMap<String, Integer> sitdown = new CoherentMap<>();// 就坐情况，用于游戏就绪
    public Set<String> ready = new HashSet<>();// 就绪完毕后游戏开始
    public Set<String> disconnect = new HashSet<>();//断线用户

    public LinkedHashMap<Integer, String> base = new LinkedHashMap<>();// 牌面, <座位, 身份>
    public LinkedHashMap<Integer, String> deck = new LinkedHashMap<>();// 牌面, <座位, 身份>

    //游戏进度存档,用于断线重连
    private Chain guide;

    public Game(List<String> setting) {
        this.setting = setting;
        //
        init();
    }

    private void init() {
        guide = new Chain()
                // 选座位就绪
                //.interrupt("Sitdown")
                // 选座位就绪
                .add("Ready", new Stage() {
                    @Override
                    public boolean execute(Player player, JSONObject jsonObject) {
                        String user = player.user;
                        if (sitdown.containsKey(user) && !ready.contains(user)) {
                            player.emit("Ready", null);
                        }
                        return false;
                    }
                })
                // 洗牌发牌
                .add("Shuffle", new Stage() {
                    @Override
                    public boolean execute(Player player, JSONObject jsonObject) {
                        List<String> pool = new LinkedList<>(setting);
                        Random random = new Random(System.currentTimeMillis());
                        for (int i=0; pool.size()>0; i++) {
                            int index = random.nextInt(pool.size());
                            String poker = pool.remove(index);
                            base.put(i, poker);
                            deck.put(i, poker);
                            // 回写到玩家身份上
                            String user = sitdown.key(i);
                            Player player1 = players.get(user);
                            player1.poker = poker;
                        }
                        return true;
                    }
                })
                // 化身幽灵醒来
                .addPlayerInput(this, "化身幽灵", new Doppel())
                // 狼人行动
                .add(new Wolves())

                // 爪牙行动
                .add(new Minion())
                // 化身爪牙行动
                .add(new AsMinion())

                // 守夜人行动
                .add(new Mason())

                // 预言家醒来
                .addPlayerInput(this, "预言家", new Seer())
                // 化身预言家醒来
                .addPlayerInput(this, "化身预言家", new AsSeer())

                // 强盗醒来
                .addPlayerInput(this, "强盗", new Robber())
                // 化身强盗醒来
                .addPlayerInput(this, "化身强盗", new AsRobber())

                // 捣蛋鬼醒来
                .addPlayerInput(this, "捣蛋鬼", new TroubleMarker())
                // 化身捣蛋鬼醒来
                .addPlayerInput(this, "化身捣蛋鬼", new AsTroubleMarker())

                // 酒鬼醒来
                .addPlayerInput(this, "酒鬼", new Drunk())
                // 化身酒鬼醒来
                .addPlayerInput(this, "化身酒鬼", new AsDrunk())

                // 失眠者行动
                .add(new Insomniac())
        ;
    }

    public JSONObject desktop() {
        JSONObject desktop = new JSONObject();
        if (sitdown.size() == 0) {
            return null;
        }
        for (Map.Entry<String, Integer> entry : sitdown.entrySet()) {
            Integer seat = entry.getValue();
            String user = entry.getKey();
            Player player = players.get(user);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("seat", seat);
            jsonObject.put("user", user);
            jsonObject.put("img", player.img);
            jsonObject.put("isReady", ready.contains(user));
            jsonObject.put("isDisconnect", disconnect.contains(user));

            desktop.put(seat.toString(), jsonObject);
        }
        return desktop;
    }

    public Integer getSeat(String user) {
        return sitdown.get(user);
    }

    public String getPoker(String user) {
        int seat = sitdown.get(user);
        return base.get(seat);
    }

    public String play(Player player) {
        return guide.start(player);
    }

    public List<Integer> findSeats(String poker) {
        List<Integer> indexs = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : deck.entrySet()) {
            if (entry.getValue().equals(poker)) {
                indexs.add(entry.getKey());
            }
        }
        return indexs;
    }

    public List<Player> findPayers(String poker) {
        List<Player> indexs = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : deck.entrySet()) {
            if (entry.getValue().equals(poker)) {
                int index = entry.getKey();
                String user = sitdown.key(index);
                if (user == null) {
                    continue;//表示在底牌
                } else {
                    indexs.add(players.get(user));
                }

            }
        }
        return indexs;
    }

    public Integer findSeat(String poker) {
        for (Map.Entry<Integer, String> entry : deck.entrySet()) {
            if (entry.getValue().equals(poker)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Player findPlayer(String poker) {
        for (Map.Entry<Integer, String> entry : deck.entrySet()) {
            if (entry.getValue().equals(poker)) {
                int index = entry.getKey();
                String user = sitdown.key(index);
                if (user == null) {
                    return null;//表示在底牌
                } else {
                    return players.get(user);
                }
            }
        }
        return null;
    }
}
