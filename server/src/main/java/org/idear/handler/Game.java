package org.idear.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.idear.CoherentMap;
import org.idear.endpoint.PlayerEndpoint;
import org.idear.game.entity.Camp;
import org.idear.game.entity.Movement;
import org.idear.game.entity.Report;
import org.idear.game.entity.spell.Replicate;
import org.idear.game.entity.spell.Rob;
import org.idear.game.entity.spell.Show;
import org.idear.game.entity.spell.Switch;
import org.idear.util.StringUtil;

import java.util.*;

/**
 * Created by idear on 2018/9/29.
 */
public class Game extends com.idearfly.timeline.websocket.Game<Player> {
    GameCenter gameCenter;

    private int no;// 房间号
    private List<String> setting;// 选牌

    /**
     * 玩家信息，进入房间后记录,包括观战
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
    private LinkedHashMap<Integer, String> initial;// 牌面, <座位, 身份>
    /**
     * 当前牌面
     * seat = poker
     */
    private LinkedHashMap<Integer, String> deck;// 牌面, <座位, 身份>

    /**
     * 发言
     * seat = [String]
     */
    private LinkedHashMap<Integer, List<String>> speekMap;
    private int speekStartIndex;// 第一个发言人
    private int speekCurrentIndex;// 当前发言人
    private int speekRound = 1;// 第几轮发言


    /**
     * 投票情况
     * seat = vote[seat]
     */
    private Map<Integer, Integer> votes;

    /**
     * 猎人时间
     */
    private List<Integer> hunters;

    /**
     * 猎人枪杀
     * seat = kill seat
     */
    private Map<Integer, Integer> hunterKill;

    /**
     * 投票死亡
     */
    private Set<Integer> deadth;

    /**
     * 结果报告
     */
    private Report report;

    /**
     * 流程
     */
    private Story story;

    /**
     * 记录步骤
     */
    private List<Movement> logs;

    public Player getPlayer(String user) {
        return players.get(user);
    }

    /**
     * 加入游戏,通知所有玩家,更新断线状态
     * @param player
     */
    @Override
    public synchronized void join(Player player) {
        super.join(player);
        syncStatus(player);
    }

    /**
     * 离开游戏,通知所有玩家,更新断线状态
     * @param player
     */
    @Override
    public synchronized void leave(Player player) {
        if ("Ready".equals(story.chapter())) {
            super.leave(player);
        }
        syncStatus(player);
    }

    public void removePlayer(Player player) {
        if ("Ready".equals(story.chapter())) {
            players.remove(player.getUser());
            desktop.removeKey(player);
            ready.remove(player);

            leaveGame(player);

            synchronise(player);
        } else {
            
        }
    }


    public int getNo() {
        return no;
    }



    ////////
    @Override
    public com.idearfly.timeline.Story story() {
        return null;
    }

    public List<String> getSetting() {
        return setting;
    }

    /***
     * 判断是否所有步骤完成
     * @return
     */
    private synchronized boolean allComplete() {
        for (Player player:desktop.values()) {
            if (player.getStage() != null) {
                return false;
            }
        }
        return true;
    }

    private Integer[] integers = new Integer[]{};

    /**
     * 找同伙
     * @param indexs
     * @param poker
     */
    private void partnerAction(List<Integer> indexs, String poker) {
        Integer[] indexArray = indexs.toArray(integers);
        String summary = null;
        String description = null;
        if (indexs.size() > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder1 = new StringBuilder();
            for (Integer i : indexs) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                    stringBuilder1.append(",");
                }
                stringBuilder.append(i);
                stringBuilder1.append(i + "号玩家");
            }
            summary = "同伙" + stringBuilder.toString();
            description = poker+"同伙是" + stringBuilder1.toString();
        }  else {
            summary = "没有同伙";
            description = "没有"+poker+"同伙";
        }
        // 每个同伙互相看到
        for (int index:indexs) {
            Player team = desktop.get(index);
            if (team == null) {
                continue;
            }
            Show show = new Show(index, indexArray);
            Movement partMovement = show.cast(deck, team);
            partMovement.setSpell(poker+"行动");
            partMovement.setSummary(summary);
            String string = index+"号玩家"+team.getUser()+description;
            partMovement.setSummary(summary);
            partMovement.setDescription(string);
            team.getMovements().add(partMovement);
            if (team.endpoint() != null) {
                team.endpoint().emit("syncGame", export(team));
            }
            System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(team.getMovements().get(team.getMovements().size()-1).getViewport()));
            team.setTargets(null);
            team.setStage(null);
        }

        Movement movement = new Movement(null);
        movement.setSpell(poker+"行动");
        movement.setSummary(summary);
        movement.setDescription(description);
        movement.setTargets(indexArray);
        addLog(movement);
    }

    public Story waitForAction() {
        return story.addChapter("WaitForAction", () -> {
            if (allComplete()) {
                return true;
            }
            return false;
        });
    }

    /**
     * 用户操作
     * @param stage
     * @param pokers
     */
    private boolean playerAction(String stage, String... pokers) {
        // 找初始身份
        List<Player> players = findInitialByPokers(pokers);
        if (players.size() > 0) {
            for (Player player : players) {
                player.setStage(stage);
                if (player.endpoint() != null) {
                    player.endpoint().emit(stage, null);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 添加日志
     * @param movement
     */
    private void addLog(Movement movement) {
        movement.setViewport(new LinkedHashMap<>(deck));
        logs.add(movement);
        // 广播给观众
        for (Player player:players.values()) {
            if (player.getSeat() == null) {//
                if (player.endpoint() != null) {
                    player.endpoint().emit("God", export(player));
                }
            }
        }
    }

    public void init() {
        initial = new LinkedHashMap<>();
        deck = new LinkedHashMap<>();
        speekMap = new LinkedHashMap<>();
        votes = new LinkedHashMap<>();
        hunters = new LinkedList<>();
        hunterKill = new LinkedHashMap<>();
        deadth = new LinkedHashSet<>();
        report = new Report();
        logs = new LinkedList<>();
        story = new Story()
                .addChapter("Ready")
                .addChapter("Shuffle", ()-> {
                    List<String> pool = new LinkedList<>(setting);

                    List<Player> wolves = findByPokers();
                    Player cobber = null;
                    Player doppel = null;
                    for (int seat=1; pool.size()>0; seat++) {
                        int index = GameCenter.randomInt(pool.size());
                        String poker = pool.remove(index);
                        // 测试
//                            if (seat==1)poker = "化身幽灵";
//                            if (seat==2)poker = "猎人";
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
                            Movement movement = show.cast(deck, player);
                            player.getMovements().add(movement);
                            ///log
                            String summary = "初为"+ StringUtil.simplePokerName(poker);
                            movement.setSummary(summary);
                            movement.setDescription(seat+"号发牌后得到"+poker);
                            movement.setSpell("系统发牌");
                            // 确定执行顺序
                            if (poker.equals("狼人")) {
                                wolves.add(player);
                            } else if (poker.equals("皮匠")) {
                                cobber = player;
                            } else if (poker.equals("化身幽灵")) {
                                doppel = player;
                            }
                        }
                    }
                    ///log
                    Movement movement = new Movement(null);
                    StringBuilder stringBuilder = new StringBuilder();
                    if (wolves.size() > 0) {
                        for (Player player: wolves) {
                            stringBuilder.append(player.getSeat()+"号");
                        }
                        stringBuilder.append("玩家是狼人");
                    } else {
                        stringBuilder.append("没有狼人");
                    }
                    String description = "发牌,本局"+stringBuilder.toString();
                    if (cobber != null) {
                        description += ","+cobber.getSeat()+"玩家是皮匠";
                    }
                    movement.setDescription(description);
                    movement.setSpell("系统发牌");
                    addLog(movement);
                    // 广播所有玩家,游戏开始
                    if (doppel == null) {
                        gameStart(null);
                    } else {
                        if (doppel.endpoint() != null) {
                            doppel.endpoint().emit("GameStart", export(doppel));
                        }
                    }
                    return true;
                })
                // 化身幽灵醒来
                .addChapter("DoppelAction", ()-> {
                    String stage = "Doppel";
                    String poker = "化身幽灵";
                    Player player = findInitialByPoker(poker);
                    if (player != null) {
                        player.setStage(stage);
                        if (player.endpoint() != null) {
                            player.endpoint().emit(stage, null);
                        }
                    }
                    return true;
                });

        waitForAction()
                // 狼人行动
                .addChapter("WolvesAction", ()-> {
                    List<Integer> indexs = findInitialSeatsByPokers("狼人", "化身狼人");
                    if (indexs.size() == 0) {

                    } else if (indexs.size() == 1) {
                        Player player = findBySeat(indexs.get(0));
                        String stage = "Wolves";
                        player.setStage(stage);
                        if (player.endpoint() != null) {
                            player.endpoint().emit(stage, export(player));
                        }
                    } else {
                        partnerAction(indexs, "狼人");
                    }
                    return true;
                })
                // 爪牙行动
                .addChapter("MinionAction", ()-> {
                    List<Player> pls = findInitialByPokers("爪牙", "化身爪牙");
                    if (pls.size() == 0) {
                        return true;
                    }

                    List<Integer> indexs = findSeatsByPokers("狼人", "化身狼人");
                    Integer[] indexArray = indexs.toArray(integers);
                    String summary = null;
                    String description = null;
                    if (indexs.size()>0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        StringBuilder stringBuilder1 = new StringBuilder();
                        for (Integer i : indexs) {
                            if (stringBuilder.length()>0) {
                                stringBuilder.append(",");
                                stringBuilder1.append(",");
                            }
                            stringBuilder.append(i);
                            stringBuilder1.append(i+"号玩家");
                        }
                        summary = "狼人"+stringBuilder.toString();
                        description = "狼人"+stringBuilder1.toString();
                    } else {
                        summary = "没有狼人";
                        description = "没有狼人";
                    }
                    for (Player player: pls) {
                        // 爪牙看到狼
                        Show show = new Show(player.getSeat(), indexArray);
                        Movement partMovement = show.cast(deck, player);
                        partMovement.setSummary(summary);
                        String string = player.getSeat()+"号玩家"+player.getUser()+"发现"+description;
                        partMovement.setDescription(string);
                        partMovement.setSpell("爪牙行动");
                        player.getMovements().add(partMovement);

                        if (player.endpoint() != null) {
                            player.endpoint().emit("syncGame", export(player));
                        }
                        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
                        player.setTargets(null);
                        player.setStage(null);

                    }
                    Movement movement = new Movement(null);
                    movement.setSpell("爪牙行动");
                    movement.setSummary(summary);
                    movement.setDescription(description);
                    movement.setTargets(indexArray);
                    addLog(movement);
                    return  true;
                })

                // 守夜人行动
                .addChapter("MasonAction", () -> {
                    List<Integer> indexs = findInitialSeatsByPokers("守夜人", "化身守夜人");
                    if (indexs.size() > 0) {
                        partnerAction(indexs, "守夜人");
                    }
                    return true;
                })

                // 预言家醒来
                .addChapter("SeerAction", () -> {
                    playerAction("Seer", "预言家", "化身预言家");
                    return true;
                });

        // 拦截, 上面步骤为并行
        waitForAction()
                // 化身强盗醒来
                .addChapter("AsRobberAction", () ->{
                    playerAction("AsRobber", "化身强盗");
                    return true;
                });
        waitForAction()
                // 强盗醒来
                .addChapter("RobberAction", () ->{
                    playerAction("Robber", "强盗");
                    return true;
                });
        waitForAction()
                // 化身捣蛋鬼醒来
                .addChapter("AsTroubleMarkerAction", ()-> {
                    playerAction("AsTroubleMarker", "化身捣蛋鬼");
                    return true;
                });
        waitForAction()
                // 捣蛋鬼醒来
                .addChapter("TroubleMarkerAction", ()-> {
                    playerAction("TroubleMarker", "捣蛋鬼");
                    return true;
                });
        waitForAction()
                // 化身酒鬼醒来
                .addChapter("AsDrunkAction", ()->{
                    playerAction("AsDrunk", "化身酒鬼");
                    return true;
                });

        waitForAction()
                // 酒鬼醒来
                .addChapter("DrunkAction", ()->{
                    playerAction("Drunk", "酒鬼");
                    return true;
                })

                // 失眠者行动
                .addChapter("InsomniacAction", ()-> {
                    String stage = "Insomniac";
                    List<Player> players = findInitialByPokers("失眠者", "化身失眠者");
                    if (players.size() > 0) {
                        StringBuilder allSummary = new StringBuilder();
                        StringBuilder allDescription = new StringBuilder();
                        List<Integer> indexs = new LinkedList<>();
                        for (Player player : players) {
                            Integer seat = player.getSeat();
                            indexs.add(seat);
                            // 失眠者 查看当前自己牌
                            Show show = new Show(seat, seat);
                            Movement partMovement = show.cast(deck, player);
                            player.getMovements().add(partMovement);
                            String poker = deck.get(seat);
                            String summary = null;
                            String description = null;
                            if (poker.equals("失眠者") || poker.equals("化身失眠者")) {
                                summary = "身份未调换";
                                description = "身份未调换";
                            } else {
                                summary = "失变"+ StringUtil.simplePokerName(poker);
                                description = "从失眠者变成"+poker;
                            }
                            String string = seat+"号玩家"+player.getUser() + description;
                            if (allDescription.length()>0) {
                                allDescription.append(",");
                            }
                            allDescription.append(string);
                            partMovement.setDescription(string);
                            if (allSummary.length()>0) {
                                allSummary.append(",");
                            }
                            allSummary.append(summary);
                            partMovement.setSummary(summary);
                            partMovement.setSpell("失眠者行动");
                            if (player.endpoint() != null) {
                                player.endpoint().emit("syncGame", export(player));
                            }
                        }
                        Movement movement = new Movement(null);
                        movement.setSpell("失眠者行动");
                        movement.setSummary(allSummary.toString());
                        movement.setDescription(allDescription.toString());
                        movement.setTargets(indexs.toArray(integers));
                        addLog(movement);
                    }
                    return true;
                });
        // 行动结束
        waitForAction()
                // 发言
                .addChapter("SpeekAction", ()-> {
                    Random random = new Random(System.currentTimeMillis());
                    speekCurrentIndex = random.nextInt(desktop.size())+1;
                    speekStartIndex = speekCurrentIndex;
                    //speek();
                    Movement movement = new Movement(null);
                    movement.setSpell("开始发言");
                    String summary = "随机选出"+speekStartIndex+"号第一个发言";
                    movement.setCaller(speekStartIndex);
                    movement.setSummary(summary);
                    movement.setDescription(summary);
                    movement.setTargets(null);
                    addLog(movement);
                    return true;
                })
                // 发起投票
                .addChapter("StartVote", ()->{
                    LinkedList<Player> list = new LinkedList<>(desktop.values());
                    for (Player player : list) {
                        player.setStage("Vote");
                        PlayerEndpoint playerEndpoint = player.endpoint();
                        if (playerEndpoint != null) {
                            playerEndpoint.emit("Vote", export(player));
                        }
                    }
                    return true;
                })
                // 结束投票
                .addChapter("AfterVote")

                // 死亡情况
                .addChapter("Deadth", ()-> {
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
                })
                // 猎人权力
                .addChapter("HunterAction", ()->{
                    /// 猎人
                    for (Integer seat: deadth) {
                        String poker = deck.get(seat);
                        if (poker.equals("猎人") || poker.equals("化身猎人")) {
                            hunters.add(seat);
                        }
                    }
                    // 有死亡的猎人
                    if (hunters.size() > 0) {
                        String stage = "Hunter";
                        List<Player> players = findInitialByPokers("猎人", "化身猎人");
                        if (players.size() > 0) {
                            for (Player player : players) {
                                player.setStage(stage);
                                if (player.endpoint() != null) {
                                    player.endpoint().emit(stage, export(player));
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                    return true;
                });
        // 猎人执行完毕
        waitForAction()
                // 计算胜负
                .addChapter("Result", ()-> {
                    //狼阵营
                    boolean wolvesWin = false;
                    //村民阵营
                    boolean villagerWin = false;
                    //皮匠阵营
                    boolean cobblerWin = false;
                    // 所有人
                    boolean allWin = false;

                    //皮匠死亡
                    boolean cobberDeadth = false;
                    //狼人死亡
                    boolean wolvesDeadth = false;



                    //皮匠如果死亡, 皮匠一定赢
                    for (Integer seat: deadth) {
                        String poker = deck.get(seat);
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
                        /**
                         * 获胜条件入下:
                         * <条件:皮匠死亡> = 皮匠获胜 => 狼输 , <条件1.有狼局 && 条件2.狼死亡> = 村民获胜, <条件1.无狼局>= 村民输
                         * <条件;皮匠未死亡> = 皮匠输, <条件1.有狼局 && 条件2.狼死亡> = 村民获胜狼输, <条件1.有狼局 && 条件2.狼未死亡> = 村民输狼获胜, <条件1.无狼局 && 条件2.无死亡--平票或弃权票> = 所有人获胜, <条件1.无狼局 && 条件2.有死亡> = 所有人落败
                         */



                        // 皮匠死亡 => 皮匠获胜
                        if (cobberDeadth) {
                            cobblerWin = true;
                            if (deadth.size() == 1) {// 皮匠单独赢
                                villagerWin = false;
                                wolvesWin = false;
                                report.setDescription("皮匠独赢");
                                throw new RuntimeException();
                            }
                        }

                        if (cobblerWin) {
                            wolvesWin = false;
                            if (hasWolves) {
                                if (wolvesDeadth) {
                                    villagerWin = true;
                                    report.setDescription("皮匠狼都死");
                                } else {
                                    villagerWin = false;
                                    report.setDescription("皮匠死狼未死");
                                }
                            } else {
                                villagerWin = false;
                                report.setDescription("皮匠死无狼局");
                            }
                            throw new RuntimeException();
                        } else {
                            if (hasWolves) {
                                if (wolvesDeadth) {
                                    villagerWin = true;
                                    wolvesWin = false;
                                    report.setDescription("狼人被抓住了");
                                } else {
                                    villagerWin = false;
                                    wolvesWin = true;
                                    report.setDescription("有狼未死");
                                }
                                throw new RuntimeException();
                            } else {
                                if (deadth.size() == 0) {
                                    report.setDescription("无狼局弃权票");
                                    allWin = true;
                                } else if (deadth.size() == playerCount()) {
                                    report.setDescription("无狼局平票");
                                    allWin = true;
                                } else {
                                    report.setDescription("无狼局有伤亡");
                                    allWin = false;
                                }
                                // 所有人的获胜
                                for (Map.Entry<Integer, Player> entry : desktop.entrySet()) {
                                    Player player = entry.getValue();
                                    Camp camp = report.getAll();
                                    if (camp == null) {
                                        camp = new Camp("所有人");
                                        camp.setWin(allWin);
                                        report.setAll(camp);
                                    }
                                    String pokerDesc = deck.get(player.getSeat());
                                    if (!player.getPoker().equals(pokerDesc)) {
                                        pokerDesc = player.getPoker() +"->" + pokerDesc;
                                    }
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("user", player.getUser());
                                    jsonObject.put("seat", player.getSeat());
                                    jsonObject.put("pokerDesc", pokerDesc);
                                    camp.getMembers().add(jsonObject);
                                }
                            }
                        }
                    } catch (RuntimeException e) {
                        // 分阵营获胜
                        for (Map.Entry<Integer, Player> entry : desktop.entrySet()) {
                            Player player = entry.getValue();
                            Integer seat = entry.getKey();
                            String poker = deck.get(seat);
                            String pokerDesc = poker;
                            if (!player.getPoker().equals(pokerDesc)) {
                                pokerDesc = player.getPoker() +"->" + pokerDesc;
                            }
                            String campName = GameCenter.camp.get(poker);
                            if ("城镇".equals(campName)) {
                                Camp camp = report.getTown();
                                if (camp == null) {
                                    camp = new Camp("城镇阵营");
                                    camp.setWin(villagerWin);
                                    report.setTown(camp);
                                }

                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("user", player.getUser());
                                jsonObject.put("seat", player.getSeat());
                                jsonObject.put("pokerDesc", pokerDesc);
                                camp.getMembers().add(jsonObject);
                            } else if ("狼人".equals(campName)) {
                                Camp camp = report.getWolves();
                                if (camp == null) {
                                    camp = new Camp("狼人阵营");
                                    camp.setWin(wolvesWin);
                                    report.setWolves(camp);
                                }
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("user", player.getUser());
                                jsonObject.put("seat", player.getSeat());
                                jsonObject.put("pokerDesc", pokerDesc);
                                camp.getMembers().add(jsonObject);
                            } else if ("皮匠".equals(campName)) {
                                Camp camp = report.getCobbler();
                                if (camp == null) {
                                    camp = new Camp("皮匠阵营");
                                    camp.setWin(cobblerWin);
                                    report.setCobbler(camp);
                                }
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("user", player.getUser());
                                jsonObject.put("seat", player.getSeat());
                                jsonObject.put("pokerDesc", pokerDesc);
                                camp.getMembers().add(jsonObject);
                            }
                        }
                    }

                    ///logs
                    Movement movement = new Movement(null);
                    movement.setSpell("游戏结果");
                    movement.setSummary(null);
                    movement.setDescription(null);
                    movement.setTargets(null);
                    addLog(movement);
                    ///日志排序
                    String[] orderPokers = new String[] {
                            "系统发牌",
                            "化身幽灵行动",
                            "狼人行动", "爪牙行动", "守夜人行动", "化身预言家行动", "预言家行动",
                            "化身强盗行动",
                            "强盗行动",
                            "化身捣蛋鬼行动",
                            "捣蛋鬼行动",
                            "化身酒鬼行动",
                            "酒鬼行动",
                            "化身失眠者行动",
                            "失眠者行动",
                            "开始发言",
                            "化身猎人行动",
                            "猎人行动",
                            "游戏结果"
                    };
                    Movement[] orderMovements = new Movement[orderPokers.length];
                    LinkedList<Movement> newLogs = new LinkedList<>();
                    for (Movement m: logs) {
                        for (int i=0;i<orderPokers.length;i++) {
                            String poker = orderPokers[i];
                            if (m.getSpell().equals(poker)) {
                                orderMovements[i] = m;
                            }
                        }
                    }
                    for (Movement m : orderMovements) {
                        if (m != null) {
                            newLogs.add(m);
                        }
                    }
                    logs = newLogs;
                    ///
                    finall();
                    // 清除就位，用于重新开始
                    for (Map.Entry<Integer, Player> entry : desktop.entrySet()) {
                        entry.getValue().setReady(false);
                    }
                    ready.clear();
                    return true;
                })
                .addChapter("Finally")
        ;
    }

    //////////////////////////////////////////////
    /***
     * 通过初始身份牌找玩家
     * @param poker
     * @return
     */
    public Player findInitialByPoker(String poker) {
        for (Player player:desktop.values()) {
            if (player.getPoker().equals(poker)) {
                return player;
            }
        }
        return null;
    }

    /***
     * 通过身份牌找玩家
     * @param poker
     * @return
     */
    public Player findByPoker(String poker) {
        List<Integer> seats = new LinkedList<>();
        for (Map.Entry<Integer, String> entry:deck.entrySet()) {
            if (entry.getValue().equals(poker)) {
                return desktop.get(entry.getKey());
            }
        }
        return null;
    }

    /***
     * 通过初始身份牌找玩家
     * @param pokers
     * @return
     */
    public List<Player> findInitialByPokers(String... pokers) {
        List<Player> players = new LinkedList<>();
        for (Player player:desktop.values()) {
            for (String poker:pokers) {
                if (player.getPoker().equals(poker)) {
                    players.add(player);
                }
            }
        }
        return players;
    }

    /***
     * 通过身份牌找玩家
     * @param pokers
     * @return
     */
    public List<Player> findByPokers(String... pokers) {
        List<Player> players = new LinkedList<>();
        for (Map.Entry<Integer, String> entry:deck.entrySet()) {
            for (String poker:pokers) {
                if (entry.getValue().equals(poker)) {
                    Player player = desktop.get(entry.getKey());
                    if (player != null) {
                        players.add(player);
                    }
                }
            }
        }
        return players;
    }

    /***
     * 通过身份牌找牌号
     * @param pokers
     * @return
     */
    public List<Integer> findDeckByPokers(String... pokers) {
        List<Integer> seats = new LinkedList<>();
        for (Map.Entry<Integer, String> entry:deck.entrySet()) {
            for (String poker:pokers) {
                if (entry.getValue().equals(poker)) {
                    seats.add(entry.getKey());
                }
            }
        }
        return seats;
    }

    /***
     * 通过初始身份牌找座位
     * @param pokers
     * @return
     */
    public List<Integer> findInitialSeatsByPokers(String... pokers) {
        List<Integer> seats = new LinkedList<>();
        for (Player player:desktop.values()) {
            for (String poker:pokers) {
                if (player.getPoker().equals(poker)) {
                    seats.add(player.getSeat());
                }
            }
        }
        return seats;
    }

    /***
     * 通过身份牌找座位
     * @param pokers
     * @return
     */
    public List<Integer> findSeatsByPokers(String... pokers) {
        List<Integer> seats = new LinkedList<>();
        for (Map.Entry<Integer, String> entry:deck.entrySet()) {
            for (String poker:pokers) {
                if (entry.getValue().equals(poker)) {
                    Integer seat = entry.getKey();
                    if (desktop.containsKey(seat)) {
                        seats.add(seat);
                    }
                }
            }
        }
        return seats;
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
     * 广播结果
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
     * 轮流发言
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
     * 离开游戏
     * @param player
     */
    public synchronized void leaveGame(Player player) {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player pl : list) {
            if (pl != player) {
                PlayerEndpoint playerEndpoint = pl.endpoint();
                if (playerEndpoint != null) {
                    JSONObject object = new JSONObject();
                    object.put("seat", player.getSeat());
                    playerEndpoint.emit("LeaveGame", object);
                }
            }
        }
    }

    /**
     * 离开游戏
     */
    public synchronized void gameStart(Player caller) {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player player : list) {
            if (player != caller) {
                PlayerEndpoint playerEndpoint = player.endpoint();
                if (playerEndpoint != null) {
                    playerEndpoint.emit("GameStart", export(player));
                }
            }
        }
    }

    /**
     * 同步客户端
     */
    public synchronized void synchronise() {
        synchronise(null);
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
     * 同步连接状态
     * @param caller
     */
    public synchronized void syncStatus(Player caller) {
        LinkedList<Player> list = new LinkedList<>(players.values());
        for (Player player: list) {
            if (player != caller) {
                PlayerEndpoint playerEndpoint = player.endpoint();
                if (playerEndpoint != null) {
                    playerEndpoint.emit("syncStatus", export(player));
                }
            }
        }
    }

    /**
     * 游戏核心数据,用于输出同步给client
     * @return
     */
    public JSONObject export(Player player) {
        String stage = story.chapter();
        if (stage == null) {
            // 游戏结束
            stage = "Finally";
        } else if (stage.equals("Ready")) {
            // 游戏未开始
            stage = "Ready";
        } else {
            // 游戏进行中
            if (player.getStage() != null) {
                stage = player.getStage();
            } else if (player.getSeat() == null) {
                stage = "God";
            }
        }

        System.out.println("玩家[" + player.getUser() + "][" + player.getPoker() + "] 当前步骤[" + story.chapter() + "] 返回[" + stage + "]");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("setting", setting);
        jsonObject.put("desktop", desktop);
        jsonObject.put("deck", deck);
        jsonObject.put("playerCount", playerCount());
        jsonObject.put("stage", stage);
        jsonObject.put("speekStartIndex", speekStartIndex);
        jsonObject.put("speekCurrentIndex", speekCurrentIndex);
        jsonObject.put("speekRound", speekRound);
        jsonObject.put("votes", votes);
        jsonObject.put("report", report);
        jsonObject.put("deadth", deadth);
        jsonObject.put("hunterKill", hunterKill);
        jsonObject.put("logs", logs);
        //jsonObject.put("movements", movements);
        return jsonObject;
    }

    /**
     * 玩家数量
     * @return
     */
    public int playerCount() {
        return setting.size() - 3;
    }

    ///////////////////////////////////////////////
    /**
     * 化身操作
     * @param player
     * @param target
     */
    public void doppel(Player player, Integer target) {
        Integer caller = player.getSeat();

        List<Movement> movements = player.getMovements();
        //复制身份
        Replicate replicate = new Replicate(caller, target);
        Movement partMovement = replicate.cast(deck, player);
        String targetPoker = deck.get(target);

        movements.add(partMovement);
        // 化身之后, 得到新技能
        String newPoker = deck.get(player.getSeat());
        player.setPoker(newPoker);
        ///log
        String summary = "化"+target+ "变"+StringUtil.simplePokerName(targetPoker);
        partMovement.setSummary(summary);
        Player targetPlayer = desktop.get(target);
        String description = String.format(
                "%d号玩家%s化身为%d号玩家的%s",
                player.getSeat(),
                player.getUser(),
                targetPlayer.getSeat(),
                targetPoker);
        System.out.println("####" + description +"] 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell("化身幽灵行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        // 通知游戏开始
        gameStart(null);

        player.setTargets(null);
        player.setStage(null);


        nextStage();
    }

    /**
     * 孤狼操作
     * @param player
     * @param targets
     */
    public void wolves(Player player, Integer[] targets) {

        // 查看底牌
        Show show = new Show(player.getSeat(), targets);

        Movement partMovement = show.cast(deck, player);
        player.getMovements().add(partMovement);

        ///log
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer target: targets) {
            if (stringBuilder.length()>0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(target.toString());
        }
        String summary = "孤狼底牌" + stringBuilder.toString();
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s翻看%d号底牌%s",
                player.getSeat(),
                player.getUser(),
                targets[0],
                deck.get(targets[0]));
        if (targets.length ==  2) {
            description += String.format("和%d号底牌%s", targets[1], deck.get(targets[1]));
        }
        partMovement.setDescription(description);
        partMovement.setSpell("狼人行动");
        Movement movement = partMovement.clone();
        addLog(movement);
        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        if (player.endpoint() != null) {
            player.endpoint().emit("syncGame", export(player));
        }

        player.setTargets(null);
        player.setStage(null);

        nextStage();
    }

    /**
     * 预言家操作
     * @param player
     * @param targets
     */
    public void seer(Player player, Integer[] targets) {
        Integer caller = player.getSeat();
        List<Movement> movements = player.getMovements();
        //复制身份
        Show show = new Show(caller, targets);

        Movement partMovement = show.cast(deck, player);
        movements.add(partMovement);
        ///log
        String summary = null;
        summary = "看牌"+targets[0];
        if (targets.length == 2) {
            summary += ","+targets[1];
        }
        partMovement.setSummary(summary);
        String description = null;
        if (targets.length == 2) {
            description = String.format(
                    "%d号玩家%s翻看%d号底牌%s和%d底牌%s",
                    player.getSeat(),
                    player.getUser(),
                    targets[0],
                    deck.get(targets[0]),
                    targets[1],
                    deck.get(targets[1]));
        } else {
            description = String.format(
                    "%d号玩家%s翻看%d号玩家%s",
                    player.getSeat(),
                    player.getUser(),
                    targets[0],
                    deck.get(targets[0]));
        }
        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell(player.getPoker()+"行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        if (player.endpoint() != null) {
            player.endpoint().emit("syncGame", export(player));
        }
        player.setTargets(null);
        player.setStage(null);

        nextStage();
    }

    /**
     * 强盗操作
     * @param player
     * @param target
     */
    public void robber(Player player, Integer target) {
        Integer caller = player.getSeat();

        List<Movement> movements = player.getMovements();
        //强牌后查看
        Rob rob = new Rob(caller, target);
        String targetPoker = deck.get(target);
        Movement partMovement = rob.cast(deck, player);

        movements.add(partMovement);
        //
        String summary = "抢走"+target+ "号牌";
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s抢走%d号玩家%s",
                player.getSeat(),
                player.getUser(),
                target,
                targetPoker);
        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell(player.getPoker()+"行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        if (player.endpoint() != null) {
            player.endpoint().emit("syncGame", export(player));
        }
        player.setTargets(null);
        player.setStage(null);


        nextStage();
    }

    /**
     * 捣蛋鬼操作
     * @param player
     * @param targets
     */
    public void troubleMarker(Player player, Integer[] targets) {
        Integer caller = player.getSeat();
        List<Movement> movements = player.getMovements();
        //交换后不查看
        Switch aSwitch = new Switch(caller, targets);

        String targetPoker1 = deck.get(targets[0]);
        String targetPoker2 = deck.get(targets[1]);

        Movement partMovement = aSwitch.cast(deck, player);
        movements.add(partMovement);
        /// log
        String summary = "捣牌"+targets[0]+","+targets[1];
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s交换%d号玩家%s和%d号玩家%s",
                player.getSeat(),
                player.getUser(),
                targets[0],
                targetPoker1,
                targets[1],
                targetPoker2);
        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell(player.getPoker()+"行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        if (player.endpoint() != null) {
            player.endpoint().emit("syncGame", export(player));
        }
        player.setTargets(null);
        player.setStage(null);



        nextStage();
    }

    /**
     * 酒鬼操作
     * @param player
     * @param target
     */
    public void drunk(Player player, Integer target) {
        Integer caller = player.getSeat();
        List<Movement> movements = player.getMovements();
        //交换但不能查看
        Switch aSwitch = new Switch(caller, caller, target);
        String targetPoker = deck.get(target);
        Movement partMovement = aSwitch.cast(deck, player);
        movements.add(partMovement);
        ///log
        String summary = "交换"+target;
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s换走%d号底牌%s",
                player.getSeat(),
                player.getUser(),
                target,
                targetPoker);
        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell(player.getPoker()+"行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        if (player.endpoint() != null) {
            player.endpoint().emit("syncGame", export(player));
        }
        player.setTargets(null);
        player.setStage(null);


        nextStage();
    }

    ///////////////////////////////////////////////
    public void speek(final Player player, final String string) {
        if (speekCurrentIndex != player.getSeat()) {
            return;
        }
        gameCenter.add(()->{
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
                story.focus("Speek");
            } else {
                // 下一个发言
                speek();
            }

        });
    }

    /////////////////////////////////////////////
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
            nextStage("Ready");
            return true;
        }
        return false;
    }

    /**
     * 玩家投票
     * @param player
     * @param vote
     */
    public void vote(Player player, Integer vote) {
        player.setVote(vote);
        player.setStage(null);
        votes.put(player.getSeat(), vote);


        nextStage("AfterVote");
    }

    /**
     * 猎人开枪
     * @param player
     * @param kill
     */
    public void hunter(Player player, Integer kill) {
        hunters.remove(player.getSeat());
        hunterKill.put(player.getSeat(), kill);
        deadth.add(kill);
        player.setStage(null);


        nextStage();
    }

    /**
     * 重新开始
     * @param player
     */
    public synchronized boolean restart(Player player, boolean readyStatus) {
        player.setReady(readyStatus);
        if (readyStatus) {
            ready.add(player);
        } else {
            ready.remove(player);
        }
        if (ready.size() == playerCount()) {
            init();
            //
            clearUserData();
            //开始
            nextStage("Ready");
            return true;
        }
        return false;
    }

    private void clearUserData() {
        for (Player player: desktop.values()) {
            player.setStage(null);
            player.setPoker(null);
            player.setTargets(null);
            player.setSpeaks(new LinkedList<>());
            player.setMovements(new LinkedList<>());
        }
    }

    private void nextStage() {
        nextStage("WaitForAction");
    }

    private void nextStage(final String stage) {
        gameCenter.add( ()-> {
            if (allComplete()) {
                story.focus(stage);
            }
        });
    }
}
