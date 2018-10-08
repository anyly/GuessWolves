package org.idear.handler;

import com.alibaba.fastjson.JSONObject;
import org.idear.CoherentMap;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.Stage;
import org.idear.game.Utils;
import org.idear.game.entity.Camp;
import org.idear.game.entity.Movement;
import org.idear.game.entity.Report;
import org.idear.game.entity.movement.Flop;
import org.idear.game.entity.spell.Show;
import org.idear.game.entity.wakeup.*;
import org.idear.util.StringUtil;

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
     * 发言
     * seat = [String]
     */
    private LinkedHashMap<Integer, List<String>> speekMap = new LinkedHashMap<>();
    private int speekStartIndex;// 第一个发言人
    private int speekCurrentIndex;// 当前发言人
    private int speekRound = 1;// 第几轮发言


    /**
     * 投票情况
     * seat = vote[seat]
     */
    private Map<Integer, Integer> votes = new LinkedHashMap<>();

    /**
     * 猎人时间
     */
    private List<Integer> hunters = new LinkedList<>();

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
//                            if (seat==1)poker = "猎人";
//                            if (seat==2)poker = "皮匠";
//                            if (seat==3)poker = "狼人";

                            System.out.println("发牌:"+seat+" = "+ poker);
                            initial.put(seat, poker);
                            deck.put(seat, poker);
                            // 回写到玩家身份上
                            Player player = findBySeat(seat);
                            if (player != null) {
                                player.setPoker(poker);
                                // 查看自己的牌
                                Show show = new Show(seat, seat);
                                show.setName("初>"+ StringUtil.simplePokerName(poker));
                                Movement movement = show.cast(deck, player);
                                player.movements().add(movement);
                                //
                                Context context = new Context(player, deck, votes, deadth, desktop, Game.this);
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

                // 化身失眠者行动
                .add(new AsInsomniac())

                // 发言
                .add("Speek", new Stage() {
                    @Override
                    public boolean execute(Context context) {
                        Random random = new Random(System.currentTimeMillis());
                        speekCurrentIndex = random.nextInt(players.size())+1;
                        speekStartIndex = speekCurrentIndex;
                        //Player player = desktop.get(speekCurrentIndex);
                        //player.endpoint().emit("Speek", export(player));
                        speek();
                        return false;
                    }
                })
                // 投票
                .add("Vote", new Stage() {
                    @Override
                    public boolean execute(Context context) {
                        broadcast("Vote", null);
                        return false;
                    }
                })
                // 死亡情况
                .add("Deadth", new Stage() {
                    @Override
                    public boolean execute(Context context) {
                        Integer max = -1;
                        List<Integer> scope = new LinkedList<>();
                        Map<Integer, Integer> map = new LinkedHashMap();
                        for (Map.Entry<Integer, Integer> entry: votes.entrySet()) {
                            Integer vote = entry.getValue();// 投给谁
                            int seat = entry.getKey();// 投票人

                            if (vote == null || vote == 0) {
                                continue;
                            }
                            Integer count = map.get(vote);
                            if (count == null) {
                                count = 1;
                            } else {
                                count = count + 1;
                            }
                            map.put(vote, count);

                            if (count>max) {
                                max = count;
                                scope.clear();
                                scope.add(vote);
                            } else if (count == max){
                                scope.add(vote);
                            } else {

                            }
                        }
                        deadth.addAll(scope);
                        return true;
                    }
                })
                .add("Hunter", new Stage(){
                    @Override
                    public boolean execute(Context context) {
                        for (Integer seat: deadth) {
                            String poker = deck.get(seat);
                            if (poker.equals("猎人") || poker.equals("化身猎人")) {
                                hunters.add(seat);
                            }
                        }
                        if (hunters.size() == 0) {
                            return true;
                        }
                        for (Integer seat: hunters) {
                            Player player = desktop.get(seat);
                            player.setStage("Hunter");
                            player.endpoint().emit("Hunter", export(player));
                        }
                        return false;
                    }
                })
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

                        boolean hasWolves = false;
                        for (Integer seat : desktop.keySet()) {
                            String poker = deck.get(seat);
                            if (poker.equals("狼人") || poker.equals("化身狼人")) {
                                hasWolves = true;
                                break;
                            }
                        }

                        try {
                            // 皮匠死亡 => 皮匠获胜
                            if (cobberDeadth) {
                                cobblerWin = true;
                                if (deadth.size() == 1) {// 皮匠单独赢
                                    villagerWin = false;
                                    wolvesWin = false;
                                    throw new RuntimeException();
                                }
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
                                villagerWin = deadth.size() == 0 || deadth.size()>1;
                            }

                            if (villagerWin) {
                                wolvesWin = false;
                            } else {
                                wolvesWin = true;
                            }
                        } catch (RuntimeException e) {

                        }

                        //
                        for (Map.Entry<Integer, Player> entry : desktop.entrySet()) {
                            Player player = entry.getValue();
                            Integer seat = entry.getKey();
                            String poker = deck.get(seat);
                            String campName = GameCenter.camp.get(poker);
                            if ("城镇".equals(campName)) {
                                Camp camp = report.getTown();
                                if (camp == null) {
                                    camp = new Camp(campName);
                                    camp.setWin(villagerWin);
                                    report.setTown(camp);
                                }
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("user", player.getUser());
                                jsonObject.put("seat", player.getSeat());
                                camp.getMembers().add(jsonObject);
                            } else if ("狼人".equals(campName)) {
                                Camp camp = report.getWolves();
                                if (camp == null) {
                                    camp = new Camp(campName);
                                    camp.setWin(wolvesWin);
                                    report.setWolves(camp);
                                }
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("user", player.getUser());
                                jsonObject.put("seat", player.getSeat());
                                camp.getMembers().add(jsonObject);
                            } else if ("皮匠".equals(campName)) {
                                Camp camp = report.getCobbler();
                                if (camp == null) {
                                    camp = new Camp(campName);
                                    camp.setWin(cobblerWin);
                                    report.setCobbler(camp);
                                }
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("user", player.getUser());
                                jsonObject.put("seat", player.getSeat());
                                camp.getMembers().add(jsonObject);
                            }
                        }

                        finall();
                        return false;
                    }
                })
        ;
    }

    public void speek(Player player, String string) {
        if (speekCurrentIndex != player.getSeat()) {
            return;
        }
        gameCenter.add(new Callback() {
            @Override
            public void execute(Object data) {
                List<String> speaks = player.getSpeaks();
                speaks.add(string);
                speekCurrentIndex++;
                if (speekCurrentIndex > desktop.size()) {
                    speekCurrentIndex = 1;
                    speekRound++;
                }

                if (speekRound > 3) {
                    speekRound = 3;
                    // 结束
                    guide.resume("Speek");
                } else {
                    // 下一个发言
                    speek();
                }
            }
        });
    }

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
     * 尝试当前步骤
     */
    public synchronized void tryStage() {
        gameCenter.add(new Callback() {
            @Override
            public void execute(Object data) {
                guide.start();
            }
        });
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
    public synchronized void finall() {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player player: list) {
            PlayerEndpoint playerEndpoint = player.endpoint();
            if (playerEndpoint != null) {
                playerEndpoint.emit("Finally", export(player));
            }
        }
    }

    /**
     * 同步客户端
     */
    public synchronized void speek() {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player player: list) {
            PlayerEndpoint playerEndpoint = player.endpoint();
            if (playerEndpoint != null) {
                playerEndpoint.emit("Speek", export(player));
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
     * 游戏核心数据,用于输出同步给client
     * @return
     */
    public JSONObject export(Player player) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("desktop", desktop);
        jsonObject.put("deck", deck);
        jsonObject.put("playerCount", playerCount());

        String stage = calcuStage(player);
        System.out.println("玩家["+player.getUser()+"]["+player.getPoker()+"] 当前步骤["+currentStage()+"] 返回["+stage+"]");

        jsonObject.put("stage", stage);
        jsonObject.put("speekStartIndex", speekStartIndex);
        jsonObject.put("speekCurrentIndex", speekCurrentIndex);
        jsonObject.put("speekRound", speekRound);
        jsonObject.put("votes", votes);
        jsonObject.put("report", report);
        jsonObject.put("deadth", deadth);
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

    public void vote(Player player, Integer vote) {
        player.setVote(vote);
        votes.put(player.getSeat(), vote);
        if (votes.size() == players.size()) {
            nextStage("Vote");
        }
    }

    public void hunter(Player player, Integer kill) {
        if (hunters.remove(player.getSeat())) {
            deadth.add(kill);
        }
        if (hunters.size() == 0) {
            nextStage("Hunter");
        }
    }
}
