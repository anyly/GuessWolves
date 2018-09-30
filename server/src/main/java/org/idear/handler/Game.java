package org.idear.handler;

import com.alibaba.fastjson.JSONObject;
import org.idear.CoherentMap;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.Stage;
import org.idear.game.entity.Camp;
import org.idear.game.entity.Movement;
import org.idear.game.entity.Report;
import org.idear.game.entity.movement.Flop;
import org.idear.game.entity.wakeup.*;

import javax.print.attribute.standard.PrinterLocation;
import javax.websocket.Session;
import java.util.*;

/**
 * Created by idear on 2018/9/29.
 */
public class Game {
    GameCenter gameCenter;

    private int no;// 房间号
    private List<String> setting;// 选牌

    /**
     * 玩家信息，进入房间后记录
     * user = Player
     */
    private Map<String, Player> players = new LinkedHashMap<>();

    /**
     * 就坐情况，用于游戏就绪
     * seat = Player
     */
    private CoherentMap<Integer, Player> desktop = new CoherentMap<>();

    /**
     * 全部就绪,游戏开始
     */
    private Set<Player> ready = new LinkedHashSet<>();

    /**
     * 用于发牌
     * seat = poker
     */
    private LinkedHashMap<Integer, String> initial = new LinkedHashMap<>();// 牌面, <座位, 身份>
    /**
     * 当前牌面
     * seat = poker
     */
    private LinkedHashMap<Integer, String> deck = new LinkedHashMap<>();// 牌面, <座位, 身份>

    /**
     * 投票情况
     * seat = vote
     */
    private Map<Integer, Integer> votes = new LinkedHashMap<>();

    /**
     * 投票死亡
     */
    private List<Integer> deadth = new LinkedList<>();

    /**
     * 结果报告
     */
    private Report report = new Report();

    // 流程
    public Chain guide;

    public Player getPlayer(String user) {
        return players.get(user);
    }

    /**
     * 加入游戏
     * @param player
     */
    public void addPlayer(Player player) {
        players.put(player.getUser(), player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }


    public Game(int no, List<String> setting) {
        this.no = no;
        this.setting = setting;
        gameCenter = GameCenter.instance();

        init();
    }

    public int getNo() {
        return no;
    }

    public List<String> getSetting() {
        return setting;
    }

    public void init() {
        guide = new Chain()
                // 选座位就绪
                //.interrupt("Sitdown")
                // 选座位就绪
                .interrupt("Ready")
                // 洗牌发牌
                .add("Shuffle", new Stage() {
                    @Override
                    public boolean execute(Context object) {
                        List<String> pool = new LinkedList<>(setting);
                        Random random = new Random(System.currentTimeMillis());
                        for (int seat=1; pool.size()>0; seat++) {
                            int index = random.nextInt(pool.size());
                            String poker = pool.remove(index);
                            // 测试
                            //if (seat==1)poker = "强盗";
                            //if (seat==2)poker = "失眠者";
                            //if (seat==3)poker = "狼人";

                            System.out.println("发牌:"+seat+" = "+ poker);
                            initial.put(seat, poker);
                            deck.put(seat, poker);
                            // 回写到玩家身份上
                            Player player = findBySeat(seat);
                            if (player != null) {
                                player.setPoker(poker);
                                // 查看自己的牌
                                Flop flop = new Flop(seat, seat);
                                Movement movement = new Movement(flop, null);
                                flop.doing(deck, movement.getViewport());
                                player.movements().add(movement);
                                //
                                Context context = new Context(player, deck, votes, deadth, desktop);
                                guide.setData(context);
                            }
                        }
                        // 广播所有玩家,游戏开始
                        synchronise();
                        return true;
                    }
                })
                // 化身幽灵醒来
                .add(new Doppel())
                // 狼人行动
                .add(new Wolves())

                // 爪牙行动
                .add(new Minion())
                // 化身爪牙行动
                .add(new AsMinion())

                // 守夜人行动
                .add(new Mason())

                // 预言家醒来
                .add(new Seer())
                // 化身预言家醒来
                .add(new AsSeer())

                // 强盗醒来
                .add(new Robber())
                // 化身强盗醒来
                .add(new AsRobber())

                // 捣蛋鬼醒来
                .add(new TroubleMarker())
                // 化身捣蛋鬼醒来
                .add(new AsTroubleMarker())

                // 酒鬼醒来
                .add(new Drunk())
                // 化身酒鬼醒来
                .add(new AsDrunk())

                // 失眠者行动
                .add(new Insomniac())

                // 发言
                .interrupt("Speek")
                // 投票
                .add("vote", new Stage() {
                    @Override
                    public boolean execute(Context context) {
                        broadcast("vote", null);
                        return false;
                    }
                })
                // 死亡情况
                .add("Deadth", new Stage() {
                    @Override
                    public boolean execute(Context context) {
                        Map<Integer,Integer> votes = context.getVotes();
                        LinkedHashMap<Integer, List<Integer>> voteAndSeat = new LinkedHashMap<>();
                        Integer max = -1;
                        List<Integer> list = null;
                        for (Map.Entry<Integer, Integer> entry: votes.entrySet()) {
                            int vote = entry.getValue();
                            int seat = entry.getKey();
                            if (vote>max) {
                                max = vote;
                                list = new LinkedList<>();
                                list.add(seat);
                                voteAndSeat.put(max, list);
                            } else if (vote == max){
                                list = voteAndSeat.get(max);
                                list.add(seat);
                            } else {

                            }
                        }
                        list = voteAndSeat.get(max);
                        deadth.addAll(list);
                        return true;
                    }
                })
                .add(new Hunter())
                .add("Finally", new Stage() {
                    @Override
                    public boolean execute(Context context) {
                        //狼阵营
                        boolean wolvesWin = false;
                        //村民阵营
                        boolean villagerWin = false;
                        //皮匠阵营
                        boolean cobblerWin = false;

                        //皮匠死亡
                        boolean cobberDeadth = false;
                        //狼人死亡
                        boolean wolvesDeadth = false;

                        //皮匠如果死亡, 皮匠一定赢
                        for (Integer seat: deadth) {
                            Player player = findBySeat(seat);
                            String poker = player.getPoker();
                            if (poker.equals("皮匠") || poker.equals("化身皮匠")) {
                                cobberDeadth = true;
                            }
                            if (poker.equals("狼人") || poker.equals("化身狼人")) {
                                wolvesDeadth = true;
                            }
                        }

                        boolean hasWolves = deck.containsValue("狼人") || deck.containsValue("化身狼人");

                        // 皮匠死亡 => 皮匠获胜
                        if (cobberDeadth) {
                            cobblerWin = true;
                        }

                        // 有狼局，投出狼才能获胜
                        if (hasWolves) {
                            if (wolvesDeadth) {
                                villagerWin = true;
                            } else {
                                villagerWin = false;
                            }
                        } else {
                            // 无狼局，平局，无人出局
                            villagerWin = deadth.size()>1 || votes.size()>0;
                        }

                        if (villagerWin) {
                            wolvesWin = false;
                        } else {
                            wolvesWin = true;
                        }
                        //
                        for (Player player : players.values()) {
                            String campName = GameCenter.camp.get(player.getPoker());
                            if ("城镇".equals(campName)) {
                                Camp camp = report.getTown();
                                if (camp == null) {
                                    camp = new Camp(campName);
                                    report.setTown(camp);
                                }
                                camp.getMembers().add(player);
                            } else if ("狼人".equals(campName)) {
                                Camp camp = report.getTown();
                                if (camp == null) {
                                    camp = new Camp(campName);
                                    report.setWolves(camp);
                                }
                                camp.getMembers().add(player);
                            } else if ("皮匠".equals(campName)) {
                                Camp camp = report.getTown();
                                if (camp == null) {
                                    camp = new Camp(campName);
                                    report.setCobbler(camp);
                                }
                                camp.getMembers().add(player);
                            }
                        }

                        return false;
                    }
                })
        ;
    }

    /**
     * 通过身份牌找玩家, 如果return为null, 表示没有玩家持有此身份, 可能是在牌堆里
     * @param poker
     * @return
     */
//    public Player findByPoker(String poker) {
//        Integer seat = deck.key(poker);
//        if (seat == null) {
//            return null;
//        }
//        return findBySeat(seat);
//    }

    /**
     * 通过座位找玩家, 如果是null表示座位上个没有玩家
     * @param seat
     * @return
     */
    public Player findBySeat(int seat) {
        return desktop.get(seat);
    }

    /**
     * 当前游戏所进行到哪个阶段
     * @return
     */
    public String currentStage() {
        return guide.current();
    }

    /**
     * 完成上一个阶段, 进入下一个阶段
     * @param current
     */
    public synchronized void nextStage(String current) {
        gameCenter.add(new Callback() {
            @Override
            public void execute(Object data) {
                guide.resume(current);
            }
        });

    }

    /**
     * 广播所有玩家
     * @param action
     * @param jsonObject
     */
    public synchronized void broadcast(String action, JSONObject jsonObject) {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player player: list) {
            PlayerEndpoint playerEndpoint = player.endpoint();
            if (playerEndpoint != null) {
                playerEndpoint.emit(action, jsonObject);
            }
        }
    }


    /**
     * 广播,除了调用者本身的玩家
     * @param caller
     * @param action
     * @param jsonObject
     */
    public synchronized void broadcast(Player caller, String action, JSONObject jsonObject) {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player player: list) {
            if (player != caller) {
                PlayerEndpoint playerEndpoint = player.endpoint();
                if (playerEndpoint != null) {
                    playerEndpoint.emit(action, jsonObject);
                }
            }
        }
    }

    /**
     * 同步客户端
     */
    public synchronized void synchronise() {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player player: list) {
            PlayerEndpoint playerEndpoint = player.endpoint();
            if (playerEndpoint != null) {
                playerEndpoint.emit("syncGame", export(player));
            }
        }
    }

    /**
     * 同步客户端
     */
    public synchronized void synchronise(Player caller) {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player player: list) {
            if (player != caller) {
                PlayerEndpoint playerEndpoint = player.endpoint();
                if (playerEndpoint != null) {
                    playerEndpoint.emit("syncGame", export(player));
                }
            }

        }
    }

    /**
     * 桌面
     * @return
     */
    public JSONObject getDesktop() {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<Integer, Player> entry: desktop.entrySet()) {
            jsonObject.put(entry.getKey().toString(), entry.getValue());
        }
        return jsonObject;
    }

    /**
     * 游戏核心数据,用于输出同步给client
     * @return
     */
    public JSONObject export(Player player) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("desktop", getDesktop());
        jsonObject.put("playerCount", playerCount());

        String stage = calcuStage(player);
        System.out.println("玩家["+player.getUser()+"]["+player.getPoker()+"] 当前步骤["+currentStage()+"] 返回["+stage+"]");

        jsonObject.put("stage", stage);
        //jsonObject.put("movements", movements);
        return jsonObject;
    }

    private String calcuStage(Player player) {
        if (player.getStage() != null) {
            return player.getStage();
        }
        String stage = currentStage();
        if (stage == null){
            return "GameOver";
        }
        return stage;
    }

    /**
     * 玩家数量
     * @return
     */
    public int playerCount() {
        return setting.size() - 3;
    }

    /**
     * 就坐
     * @param seat
     * @param player
     */
    public synchronized void sitdown(int seat, Player player) {
        desktop.put(seat, player);
        player.setSeat(seat);
    }

    /**
     * 设置就绪状态, 当返回true表示游戏开始
     * @param player
     * @param readyStatus
     * @return
     */
    public synchronized boolean setReadyStatus(Player player, boolean readyStatus) {
        player.setReady(readyStatus);
        if (readyStatus) {
            ready.add(player);
        } else {
            ready.remove(player);
        }
        if (ready.size() == playerCount()) {
            //开始
            String stage = currentStage();
            //if ("Ready".equals(stage)) {
                nextStage(stage);
                return true;
            //}
        }
        return false;
    }
}
