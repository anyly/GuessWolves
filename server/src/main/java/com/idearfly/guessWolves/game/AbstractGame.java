package com.idearfly.guessWolves.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.idearfly.collection.CoherentMap;
import com.idearfly.guessWolves.game.entity.Camp;
import com.idearfly.guessWolves.game.entity.Movement;
import com.idearfly.guessWolves.game.entity.Report;
import com.idearfly.guessWolves.game.entity.spell.*;
import com.idearfly.guessWolves.util.StringUtil;
import com.idearfly.timeline.Event;
import com.idearfly.timeline.Plot;
import com.idearfly.timeline.Story;
import com.idearfly.timeline.websocket.BaseGame;
import com.idearfly.timeline.websocket.Log;

import java.util.*;

/**
 * Created by idear on 2018/9/29.
 */
public abstract class AbstractGame extends BaseGame<Player> {

    //内部类
    private class PlayerEvent extends Event {
        String[] pokers;
        List<Player> players;

        public PlayerEvent(String name, String... pokers) {
            super(name);
            if (pokers == null || pokers.length == 0) {
                throw new NullPointerException("the PlayEvent called \'" + name + "\" unspecified parameters \"pokers\"");
            }
            this.pokers = pokers;
        }

        @Override
        public boolean when() {
            players = findInitialByPokers(pokers);
            return players.size() > 0;
        }

        @Override
        public boolean ending() {
            ListIterator<Player> listIterator =  players.listIterator();
            Player player = null;
            while (listIterator.hasNext()) {
                player = listIterator.next();
                if (player.getMission() != null) {
                    return false;
                }
            }
            after();
            return true;
        }

        @Override
        public void doing() {
            ListIterator<Player> listIterator =  players.listIterator();
            Player player = null;
            while (listIterator.hasNext()) {
                player = listIterator.next();
                player.setMission(getName());
                player.emit(getName(), AbstractGame.this);
            }
        }

        public void after() {
//            ListIterator<Player> listIterator =  players.listIterator();
//            Player player = null;
//            while (listIterator.hasNext()) {
//                player = listIterator.next();
//                player.emit("syncPoker", AbstractGame.this);
//            }
        }
    }
    //[属性]
    /**
     * 防倒霉
     */
    private Map<Integer, String> unlucky;
    /**
     * 选牌
     */
    private List<String> setting;
    private boolean speak = false;
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
     * 上一局的回放
     */
    private JSONObject playback;

    /**
     * 发言
     * seat = [String]
     */
    public class Speak {
        private int seat;
        private String user;
        private String img;
        private String speech;

        public int getSeat() {
            return seat;
        }

        public void setSeat(int seat) {
            this.seat = seat;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getSpeech() {
            return speech;
        }

        public void setSpeech(String speech) {
            this.speech = speech;
        }
    }
    private LinkedList<Speak> speakList;
    private int speakStartIndex;// 第一个发言人
    private int speakCurrentIndex;// 当前发言人
    private int speakRound = 1;// 第几轮发言
    private int speakTotal = 0;// 总共发言


    /**
     * 投票情况
     * seat = vote[seat]
     */
    private Map<Integer, Integer> votes;

    /**
     * 猎人时间
     */
    private List<Player> hunters;

    /**
     * 猎人枪杀
     * seat = kill seat
     */
    private Map<Integer, Integer> hunterKill;

    /**
     * 投票死亡
     */
    private Map<Integer, Integer> deadth;

    /**
     * 结果报告
     */
    private Report report;

    /**
     * 记录步骤
     */
    private List<Movement> logs;

    //[常量区]
    private Integer[] integers = new Integer[]{};

    String[] orderPokers = new String[] {
            "系统发牌",
            "化身幽灵行动",
            // 化身立刻行动
            "化身预言家行动", "化身见习预言家行动", "化身强盗行动", "化身女巫行动","化身捣蛋鬼行动", "化身酒鬼行动",
            // 并行看牌
            "狼人行动", "狼先知行动", "爪牙行动", "守夜人行动", "预言家行动", "见习预言家行动",
            // 串行换牌
            "强盗行动",
            "女巫行动",
            "捣蛋鬼行动",
            "酒鬼行动",
            "失眠者行动",
            "化身失眠者行动",
            "开始发言",
            "投票结果",
            "化身猎人行动",
            "猎人行动",
            "游戏结果"
    };

    //[Getter && Setter]
    public CoherentMap<Integer, Player> getDesktop() {
        return desktop;
    }

    public void setDesktop(CoherentMap<Integer, Player> desktop) {
        this.desktop = desktop;
    }

    public LinkedHashMap<Integer, String> getDeck() {
        return deck;
    }

    public void setDeck(LinkedHashMap<Integer, String> deck) {
        this.deck = deck;
    }

    public int getSpeakStartIndex() {
        return speakStartIndex;
    }

    public void setSpeakStartIndex(int speakStartIndex) {
        this.speakStartIndex = speakStartIndex;
    }

    public int getSpeakCurrentIndex() {
        return speakCurrentIndex;
    }

    public void setSpeakCurrentIndex(int speakCurrentIndex) {
        this.speakCurrentIndex = speakCurrentIndex;
    }

    public int getSpeakRound() {
        return speakRound;
    }

    public void setSpeakRound(int speakRound) {
        this.speakRound = speakRound;
    }

    public Map<Integer, Integer> getVotes() {
        return votes;
    }

    public void setVotes(Map<Integer, Integer> votes) {
        this.votes = votes;
    }

    public Map<Integer, Integer> getHunterKill() {
        return hunterKill;
    }

    public void setHunterKill(Map<Integer, Integer> hunterKill) {
        this.hunterKill = hunterKill;
    }

    public Map<Integer, Integer> getDeadth() {
        return deadth;
    }

    public void setDeadth(Map<Integer, Integer> deadth) {
        this.deadth = deadth;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public List<Movement> getLogs() {
        return logs;
    }

    public void setLogs(List<Movement> logs) {
        this.logs = logs;
    }

    public LinkedList<Speak> getSpeakList() {
        return speakList;
    }

    public void setSpeakList(LinkedList<Speak> speakList) {
        this.speakList = speakList;
    }

    public int getSpeakTotal() {
        return speakTotal;
    }

    public void setSpeakTotal(int speakTotal) {
        this.speakTotal = speakTotal;
    }

    @Override
    public void config(JSONObject config) {
        super.config(config);
        setting = config.getObject("poker", new TypeReference<List<String>>(){});
        speak = config.get("speak") != null;
    }

    public List<String> getSetting() {
        return setting;
    }

    public void setSetting(List<String> setting) {
        this.setting = setting;
    }

    public boolean isSpeak() {
        return speak;
    }

    public void setSpeak(boolean speak) {
        this.speak = speak;
    }

    public JSONObject getPlayback() {
        return playback;
    }

    public void setPlayback(JSONObject playback) {
        this.playback = playback;
    }

    /**
     * 玩家数量
     * @return
     */
    public int getPlayerCount() {
        return setting.size() - 3;
    }
    //[核心帧]
    public Story.Configuration storyConfiguration() {
        return Story
                .configuration()
                .name("一夜终极狼人杀")
                //系统发牌
                .plot(new Event("Ready") {
                    @Override
                    public boolean when() {
                        return true;
                    }

                    @Override
                    public boolean ending() {
                        boolean readyStatus = ready.size() == getPlayerCount();
                        if (readyStatus) {
                            for (Player player : ready) {
                                player.setReady(false);
                            }
                            ready.clear();
                        }
                        return readyStatus;
                    }

                    @Override
                    public void doing() {

                    }
                })
                .plot(new Plot("Shuffle") {
                    @Override
                    public void doing() {
                        List<String> pool = new LinkedList(setting);
                        int playerCount = getPlayerCount();
                        Map<Integer, String> newLucky = new LinkedHashMap<>();

                        List<Player> wolves = findByPokers();
                        Player cobber = null;
                        Player doppel = null;
                        int seat = 0;
                        for (int no=1; pool.size()>0; no++) {
                            int index = GameCenter.randomInt(pool.size());
                            String poker = pool.get(index);

                            if (config.get("unlucky") != null) {
                                //防倒霉
                                if (unlucky == null) {
                                    unlucky = new LinkedHashMap<>();
                                } else {
                                    // 比较上一局,若还是同种坏人
                                    if (poker.equals(unlucky.get(index))) {
                                        // 再来一次
                                        index = GameCenter.randomInt(pool.size());
                                        poker = pool.get(index);
                                    }
                                }
                            }
                            pool.remove(index);

                            if (no > playerCount) {// 修正底牌编号,  -1 -2 -3
                                seat = playerCount - no;
                            } else {
                                seat = no;
                            }

                            // 测试
//                            if (seat==1)poker = "见习预言家";
//                            if (seat==2)poker = "女巫";
//                            if (seat==3)poker = "狼先知";
//                            if (seat==4)poker = "女巫";


                            Log.debug("发牌", seat+" = "+poker);
                            initial.put(seat, poker);
                            deck.put(seat, poker);
                            // 回写到玩家身份上
                            Player player = findBySeat(seat);
                            if (player != null) {
                                player.setPoker(poker);
                                // 查看自己的牌
                                Reveal reveal = new Reveal(seat, seat);
                                Movement movement = reveal.cast(deck, player);
                                player.getMovements().add(movement);
                                ///log
                                String summary = "初始为"+ StringUtil.simplePokerName(poker);
                                movement.setSummary(summary);
                                movement.setDescription(seat+"号发牌后得到"+poker);
                                movement.setSpell("系统发牌");
                                // 确定执行顺序
                                if (poker.equals("狼人")
                                        || poker.equals("狼先知")
                                        || poker.equals("始祖狼")) {
                                    newLucky.put(index, poker);
                                    wolves.add(player);
                                } else if (poker.equals("皮匠")) {
                                    newLucky.put(index, poker);
                                    cobber = player;
                                } else if (poker.equals("化身幽灵")) {
                                    doppel = player;
                                }
                            }
                        }
                        if (config.get("unlucky") != null) {
                            unlucky = newLucky;
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
                        //if (doppel == null) {
                            gameStart(null);
                        //} else {
                        //    doppel.emit("GameStart", AbstractGame.this);
                        //}
                    }
                })
                // 化身幽灵醒来
                .plot(new PlayerEvent("Doppel", "化身幽灵") {
                    @Override
                    public void after() {
                        super.after();
                        //syncExclude("GameStart", players);
                    }
                })
                // 独狼
                .plot(new Event("Wolf") {
                    private Player player;

                    @Override
                    public void doing() {
                        // 孤狼
                        String stage = getName();
                        player.setMission(stage);
                        player.emit(stage, AbstractGame.this);
                    }

                    @Override
                    public boolean when() {
                        List<Integer> indexs = findInitialSeatsByPokers("狼人", "化身狼人", "狼先知", "化身狼先知");
                        if (indexs.size() == 1) {
                            player = findBySeat(indexs.get(0));
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean ending() {
                        // 孤狼
                        if (player.getMission() == null) {
                            player.emit("syncPoker", AbstractGame.this);
                            return true;
                        }
                        return false;
                    }
                })
                // 群狼
                .plot(new Event("Wolves") {
                    private List<Integer> indexs;

                    @Override
                    public boolean when() {
                        indexs = findInitialSeatsByPokers("狼人", "化身狼人", "狼先知", "化身狼先知");
                        if (indexs.size()>1) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean ending() {
                        return true;
                    }

                    @Override
                    public void doing() {
                        // 群狼
                        partnerAction(indexs, "狼人");
                    }
                })
                // 狼先知
                .plot(new PlayerEvent("MysticWolf", "狼先知", "化身狼先知"))

                // 爪牙行动
                .plot(new Plot("Minion") {
                    @Override
                    public void doing() {
                        List<Player> pls = findInitialByPokers("爪牙", "化身爪牙");
                        if (pls.size() == 0) {
                            return;
                        }

                        List<Player> wolvesPlayers = findInitialByPokers("狼人", "化身狼人", "狼先知", "化身狼先知");
                        List<Integer> indexs = new ArrayList<>();
                        String summary = null;
                        String description = null;
                        if (wolvesPlayers.size() > 0) {
                            StringBuilder stringBuilder = new StringBuilder();
                            StringBuilder stringBuilder1 = new StringBuilder();
                            for (Player wolvesPlayer : wolvesPlayers) {
                                int i = wolvesPlayer.getSeat();
                                indexs.add(i);
                                if (stringBuilder.length() > 0) {
                                    stringBuilder.append(",");
                                    stringBuilder1.append(",");
                                }
                                stringBuilder.append(i);
                                stringBuilder1.append(i + "号玩家");
                            }
                            summary = "狼人牌号" + stringBuilder.toString();
                            description = "狼人牌号" + stringBuilder1.toString();
                        } else {
                            summary = "没有狼人";
                            description = "没有狼人";
                        }
                        Integer[] indexArray = indexs.toArray(integers);
                        ListIterator<Player> listIterator =  pls.listIterator();
                        Player player = null;
                        while (listIterator.hasNext()) {
                            player = listIterator.next();
                            // 爪牙看到狼
                            Movement prevMovement = player.getMovements().get(player.getMovements().size()-1);
                            Movement partMovement = new Movement(prevMovement);
                            String poker = "狼人";
                            for (int i : indexs) {
                                String oldPoker = prevMovement.getViewport().get(i);
                                if (oldPoker == null) {
                                    partMovement.getViewport().put(i, poker);
                                }
                            }

                            partMovement.setSummary(summary);
                            String string = player.getSeat() + "号玩家" + player.getUser() + "发现" + description;
                            partMovement.setDescription(string);
                            partMovement.setSpell("爪牙行动");
                            player.getMovements().add(partMovement);

                            player.getTargets().clear();
                            player.setMission(null);

                            player.emit("syncPoker", AbstractGame.this);

                            System.out.println("####" + description + " 视角为:" + JSON.toJSONString(player.getMovements().get(player.getMovements().size() - 1).getViewport()));


                        }
                        Movement movement = new Movement(null);
                        movement.setSpell("爪牙行动");
                        movement.setSummary(summary);
                        movement.setDescription(description);
                        movement.setTargets(indexArray);
                        addLog(movement);
                    }
                })

                // 守夜人行动
                .plot(new Plot("Mason") {
                    @Override
                    public void doing() {
                        List<Integer> indexs = findInitialSeatsByPokers("守夜人", "化身守夜人");
                        if (indexs.size() > 0) {
                            partnerAction(indexs, "守夜人");
                        }
                    }
                })

                // 预言家醒来
                .plot(new PlayerEvent("Seer", "预言家"))
                // 化身预言家
                .plot(new PlayerEvent("AsSeer", "化身预言家"))
                // 见习预言家醒来
                .plot(new PlayerEvent("ApprenticeSeer", "见习预言家"))
                // 化身见习预言家
                .plot(new PlayerEvent("AsApprenticeSeer", "化身见习预言家"))

                // 化身强盗醒来
                .plot(new PlayerEvent("AsRobber", "化身强盗"))
                // 强盗醒来
                .plot(new PlayerEvent("Robber", "强盗"))

                // 化身女巫醒来
                .plot(new PlayerEvent("AsWitch", "化身女巫"))
                // 女巫醒来
                .plot(new PlayerEvent("Witch", "女巫"))

                // 化身捣蛋鬼醒来
                .plot(new PlayerEvent("AsTroubleMarker", "化身捣蛋鬼"))
                // 捣蛋鬼醒来
                .plot(new PlayerEvent("TroubleMarker", "捣蛋鬼"))

                // 化身酒鬼醒来
                .plot(new PlayerEvent("AsDrunk", "化身酒鬼"))
                // 酒鬼醒来
                .plot(new PlayerEvent("Drunk", "酒鬼"))

                // 失眠者行动
                .plot(new Plot("Insomniac") {
                    @Override
                    public void doing() {
                        List<Player> players = findInitialByPokers("失眠者", "化身失眠者");
                        if (players.size() > 0) {
                            StringBuilder allSummary = new StringBuilder();
                            StringBuilder allDescription = new StringBuilder();
                            List<Integer> indexs = new LinkedList<>();
                            ListIterator<Player> listIterator =  players.listIterator();
                            Player player = null;
                            while (listIterator.hasNext()) {
                                player = listIterator.next();
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
                                    summary = "失变" + StringUtil.simplePokerName(poker);
                                    description = "从失眠者变成" + poker;
                                }
                                String string = seat + "号玩家" + player.getUser() + description;
                                if (allDescription.length() > 0) {
                                    allDescription.append(",");
                                }
                                allDescription.append(string);
                                partMovement.setDescription(string);
                                if (allSummary.length() > 0) {
                                    allSummary.append(",");
                                }
                                allSummary.append(summary);
                                partMovement.setSummary(summary);
                                partMovement.setSpell("失眠者行动");

                                player.emit("syncPoker", AbstractGame.this);
                            }
                            Movement movement = new Movement(null);
                            movement.setSpell("失眠者行动");
                            movement.setSummary(allSummary.toString());
                            movement.setDescription(allDescription.toString());
                            movement.setTargets(indexs.toArray(integers));
                            addLog(movement);
                        }
                    }
                })

                // 破晓，天亮之前
                .plot(new Plot("Daybreak") {
                    @Override
                    public void doing() {
                        Random random = new Random(System.currentTimeMillis());
                        speakCurrentIndex = random.nextInt(desktop.size()) + 1;
                        speakStartIndex = speakCurrentIndex;

                        Movement movement = new Movement(null);
                        movement.setSpell("开始发言");
                        String summary = "随机选出" + speakStartIndex + "号第一个发言";
                        movement.setCaller(speakStartIndex);
                        movement.setSummary(summary);
                        movement.setDescription(summary);
                        movement.setTargets(null);
                        addLog(movement);
                    }
                })

                .plot(new Event("Speak") {
                    Player player;

                    @Override
                    public boolean when() {
                        return speak;
                    }

                    @Override
                    public boolean ending() {
                        if (player == null) {
                            return true;
                        }
                        if (player.getMission() == null) {
                            // 下一个发言
                            Integer next = nextSpeak();
                            if (next == null) {
                                return true;
                            } else {
                                player = desktop.get(next);
                                player.setMission("Speak");
                                speak();
                                return false;
                            }
                        }
                        return false;
                    }

                    @Override
                    public void doing() {
                        player = desktop.get(speakCurrentIndex);
                        player.setMission("Speak");
                        speak();
                    }
                })

                // 发起投票
                .plot(new Event("Vote") {
                    LinkedList<Player> players;

                    @Override
                    public boolean when() {
                        players = new LinkedList<>(desktop.values());
                        return players.size() > 0;
                    }

                    @Override
                    public boolean ending() {
                        ListIterator<Player> listIterator =  players.listIterator();
                        Player player = null;
                        while (listIterator.hasNext()) {
                            player = listIterator.next();
                            if (player.getMission() != null) {
                                return false;
                            }
                        }
                        return true;
                    }

                    @Override
                    public void doing() {
                        ListIterator<Player> listIterator =  players.listIterator();
                        Player player = null;
                        while (listIterator.hasNext()) {
                            player = listIterator.next();
                            player.setMission(getName());
                            player.emit(getName(), AbstractGame.this);
                        }
                    }
                })

                // 死亡情况
                .plot(new Plot("Deadth") {
                    @Override
                    public void doing() {
                        Integer max = -1;
                        Map<Integer, Integer> scope = new LinkedHashMap<>();
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
                                scope.put(vote, count);
                            } else if (count == max){
                                scope.put(vote,count);
                            } else {

                            }
                        }

                        String spell = "投票结果";
                        StringBuilder summary = new StringBuilder();
                        StringBuilder description = new StringBuilder();
                        for (Map.Entry<Integer, Integer> entry: scope.entrySet()) {
                            Integer vote = entry.getKey();
                            Integer count = entry.getValue();
                            deadth.put(vote, count);

                            // 添加记录
                            Player player = desktop.get(vote);
                            if (summary.length()>0) {
                                summary.append(",");
                                description.append(",");
                            }
                            summary.append(vote.toString());
                            description.append(vote.toString() + "号玩家" + player.getUser());
                        }
                        String summaryString = null;
                        String descriptionString = null;
                        boolean needSync = false;
                        if (scope.size()==0) {
                            summaryString = "所有人都投了弃权票";
                            descriptionString = "所有人都投了弃权票";
                        } else if (scope.size() == getPlayerCount()) {
                            summaryString = "平票,每个人票数相等";
                            descriptionString = "平票,每个人票数相等";
                            needSync = true;
                        } else {
                            summaryString = summary.toString() + "号玩家被投死了，共" + max + "票";
                            descriptionString = description.toString() + "，被投死了，共" + max + "票";
                            needSync = true;
                        }

                        // 系统记录
                        Movement movement = new Movement(null);
                        for (Player player : desktop.values()) {
                            List<Movement> movements = player.getMovements();
                            Movement last = movements.get(movements.size()-1);
                            movement = new Movement(last);
                            movement.setSpell(spell);
                            movement.setSummary(summaryString);
                            movement.setDescription(descriptionString);
                            movements.add(movement);
                            if (needSync) {
                                player.emit("syncGame", AbstractGame.this);
                            }
                        }
                        movement = movement.clone();
                        addLog(movement);
                    }
                })

                // 猎人权力
                .plot(new Event("Hunter") {
                    @Override
                    public void doing() {
                        String stage = getName();

                        for (Player player : hunters) {
                            player.setMission(stage);
                            player.emit(stage, AbstractGame.this);
                        }
                        //发送给非猎人玩家告知
                        syncExclude("syncHunter", hunters);
                    }

                    @Override
                    public boolean when() {
                        // 排除掉平票
                        if (deadth.size() == getPlayerCount()) {
                            return false;
                        }
                        /// 猎人得票
                        for (Integer seat: deadth.keySet()) {
                            String poker = deck.get(seat);
                            if (poker.equals("猎人") || poker.equals("化身猎人")) {
                                Player player = desktop.get(seat);
                                hunters.add(player);
                            }
                        }
                        // 有死亡的猎人
                        return hunters.size()>0;
                    }

                    @Override
                    public boolean ending() {
                        for (Player player : hunters) {
                            if (player.getMission() != null) {
                                return false;
                            }
                        }
                        // 系统日志
                        StringBuilder summary = new StringBuilder();
                        StringBuilder description = new StringBuilder();
                        for (Map.Entry<Integer, Integer> entry: hunterKill.entrySet()) {
                            Integer hunter = entry.getKey();
                            Integer kill = entry.getValue();
                            if (summary.length()>0) {
                                summary.append(",");
                                description.append(",");
                            }
                            summary.append(kill.toString());
                            description.append(hunter.toString() + "号猎人杀死了" + kill.toString() + "号玩家");
                        }

                        Movement movement = new Movement(null);
                        movement.setSpell("猎人行动");
                        movement.setSummary("猎人杀死了" + summary.toString());
                        movement.setDescription(description.toString());
                        addLog(movement);
                        return true;
                    }

                })

                // 计算胜负
                .plot(new Event("Result")  {
                    @Override
                    public boolean when() {
                        return true;
                    }

                    @Override
                    public boolean ending() {
                        return true;
                    }

                    @Override
                    public void doing() {
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
                        for (Integer seat: deadth.keySet()) {
                            String poker = deck.get(seat);
                            if (poker.equals("皮匠") || poker.equals("化身皮匠")) {
                                cobberDeadth = true;
                            }
                            if (poker.equals("狼人") || poker.equals("化身狼人") || poker.equals("狼先知") || poker.equals("化身狼先知")) {
                                wolvesDeadth = true;
                            }
                        }

                        boolean hasWolves = false;
                        for (Integer seat : desktop.keySet()) {
                            String poker = deck.get(seat);
                            if (poker.equals("狼人") || poker.equals("化身狼人") || poker.equals("狼先知") || poker.equals("化身狼先知")) {
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
                                    } else if (deadth.size() == getPlayerCount() && hunterKill.size() == 0) {
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
                                        player.setWin(allWin);
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("user", player.getUser());
                                        jsonObject.put("seat", player.getSeat());
                                        jsonObject.put("pokerDesc", pokerDesc);
                                        jsonObject.put("win", player.isWin());
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
                                    player.setWin(villagerWin);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("user", player.getUser());
                                    jsonObject.put("seat", player.getSeat());
                                    jsonObject.put("pokerDesc", pokerDesc);
                                    jsonObject.put("win", player.isWin());
                                    camp.getMembers().add(jsonObject);
                                } else if ("狼人".equals(campName)) {
                                    Camp camp = report.getWolves();
                                    if (camp == null) {
                                        camp = new Camp("狼人阵营");
                                        camp.setWin(wolvesWin);
                                        report.setWolves(camp);
                                    }
                                    player.setWin(wolvesWin);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("user", player.getUser());
                                    jsonObject.put("seat", player.getSeat());
                                    jsonObject.put("pokerDesc", pokerDesc);
                                    jsonObject.put("win", player.isWin());
                                    camp.getMembers().add(jsonObject);
                                } else if ("皮匠".equals(campName)) {
                                    Camp camp = report.getCobbler();
                                    if (camp == null) {
                                        camp = new Camp("皮匠阵营");
                                        camp.setWin(cobblerWin);
                                        report.setCobbler(camp);
                                    }
                                    player.setWin(cobblerWin);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("user", player.getUser());
                                    jsonObject.put("seat", player.getSeat());
                                    jsonObject.put("pokerDesc", pokerDesc);
                                    jsonObject.put("win", player.isWin());
                                    camp.getMembers().add(jsonObject);
                                }
                            }
                        }

                        ///logs
                        Movement movement = new Movement(null);
                        movement.setSpell("游戏结果");
                        movement.setSummary(report.getDescription());
                        movement.setDescription(report.getDescription());
                        movement.setTargets(null);
                        addLog(movement);
                        ///日志排序
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
                        // 设置回放
                        JSONObject playback = (JSONObject) JSON.toJSON(AbstractGame.this);
                        setPlayback(playback);
                        // 清除就位，用于重新开始
                        for (Map.Entry<Integer, Player> entry : desktop.entrySet()) {
                            entry.getValue().setReady(false);
                        }
                        reload();
                        //
                        result();
                    }
                })
                ;
    }
    //[被动类方法:供外部调用]
    /**
     * 加入游戏,通知所有玩家,更新断线状态
     * @param player
     */
    @Override
    public synchronized void join(Player player) {
        super.join(player);
        if (player.getSeat() != null) {
            syncStatus(player);
        }
    }

    /**
     * 离开游戏,通知所有玩家,更新断线状态
     * @param player
     */
    @Override
    public synchronized void leave(Player player) {
        // 游戏未开始前,离开房间,清空数据
        if (getStage() == null) {
            super.leave(player);
            desktop.removeKey(player);
            ready.remove(player);
            player.clear();
            emitOthers(player, "syncLeave", this);
        }
        // 游戏开始后,离开只当作断线处理
        //syncStatus(player);
    }

    /**
     * 找同伙
     * @param indexs
     * @param poker
     */
    private List<Player> partnerAction(List<Integer> indexs, String poker) {
        List<Player> players = new LinkedList<>();
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
            players.add(team);

            Movement prevMovement = team.getMovements().get(team.getMovements().size()-1);
            Movement partMovement = new Movement(prevMovement);
            for (int i : indexs) {
                String oldPoker = prevMovement.getViewport().get(i);
                if (oldPoker == null) {
                    partMovement.getViewport().put(i, poker);
                }

            }

            partMovement.setSpell(poker+"行动");
            partMovement.setSummary(summary);
            String string = index+"号玩家"+team.getUser()+description;
            partMovement.setSummary(summary);
            partMovement.setDescription(string);
            team.getMovements().add(partMovement);

            team.emit("syncPoker", AbstractGame.this);

            System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(team.getMovements().get(team.getMovements().size()-1).getViewport()));
        }

        Movement movement = new Movement(null);
        movement.setSpell(poker+"行动");
        movement.setSummary(summary);
        movement.setDescription(description);
        movement.setTargets(indexArray);
        addLog(movement);
        return players;
    }

    /**
     * 添加日志
     * @param movement
     */
    private void addLog(Movement movement) {
        movement.setViewport(new LinkedHashMap<>(deck));
        logs.add(movement);
        // 广播给观众
        for (Player player:allPlayers.values()) {
            if (player.getSeat() == null) {//
                player.emit("God", AbstractGame.this);
            }
        }
    }

    @Override
    public void reload() {
        initial = new LinkedHashMap<>();
        deck = new LinkedHashMap<>();
        speakList = new LinkedList<>();
        speakTotal = 0;
        votes = new LinkedHashMap<>();
        hunters = new LinkedList<>();
        hunterKill = new LinkedHashMap<>();
        deadth = new LinkedHashMap<>();
        report = new Report();
        logs = new LinkedList<>();
        speakRound = 1;
        if (desktop != null){
            for (Player player : desktop.values()) {
                player.clear();
            }
        }
    }

    @Override
    public void replay() {
        super.replay();
    }

    //////////////////////////////////////////////
    private Integer nextSpeak() {
        int next = speakCurrentIndex + 1;
        if (next > desktop.size()) {
            next = 1;
        }
        int round = speakRound;
        if (next == speakStartIndex) {
            round = speakRound + 1;
        }
        if (round > 3) {
            return null;
        }
        speakRound = round;
        speakCurrentIndex = next;
        return speakCurrentIndex;
    }
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
     * 广播结果
     */
    public synchronized void result() {
        syncAll("Result");
    }

    /**
     * 轮流发言
     */
    public synchronized void speak() {
        speak(null);
    }
    public synchronized void speak(Player caller) {
        syncOthers("Speak", caller);
    }

    /**
     * 离开游戏
     */
    public synchronized void gameStart(Player caller) {
        syncOthers("GameStart", caller);
    }

    /**
     * 同步座位
     */
    public synchronized void syncSeat() {
        syncAll("syncSeat");
    }

    /**
     * 同步座位
     */
    public synchronized void syncSeat(Player caller) {
        syncOthers("syncSeat", caller);
    }

    /**
     * 同步客户端
     */
    public synchronized void syncGame() {
        syncAll("syncGame");
    }

    /**
     * 同步客户端
     */
    public synchronized void syncGame(Player caller) {
        syncOthers("syncGame", caller);
    }
    /**
     * 同步牌面
     */
    public synchronized void syncPoker() {
        syncPoker(null);
    }

    /**
     * 同步牌面
     */
    public synchronized void syncPoker(Player caller) {
        syncOthers("syncPoker", caller);
    }

    /**
     * 同步连接状态
     */
    public synchronized void syncStatus() {
        syncAll("syncStatus");
    }

    /**
     * 同步连接状态
     * @param caller
     */
    public synchronized void syncStatus(Player caller) {
        syncOthers("syncStatus", caller);
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
        String summary = "化"+target+ "号变"+ StringUtil.simplePokerName(targetPoker);
        partMovement.setSummary(summary);
        Player targetPlayer = desktop.get(target);
        String description = String.format(
                "%d号玩家%s，化身为%d号玩家的%s",
                player.getSeat(),
                player.getUser(),
                targetPlayer.getSeat(),
                targetPoker);
        System.out.println("####" + description +"] 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell("化身幽灵行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        player.getTargets().clear();
        player.setMission(null);

        
    }

    /**
     * 孤狼操作
     * @param player
     */
    public void wolf(Player player, Integer tar) {
        Integer[] targets = player.getTargets().toArray(integers);

        // 查看底牌
        Show show = new Show(player.getSeat(), targets);

        Movement partMovement = show.cast(deck, player);
        player.getMovements().add(partMovement);

        String poker = deck.get(tar);
        if (poker.equals("狼人") || poker.equals("化身狼人") || poker.equals("狼先知") || poker.equals("化身狼先知")) {
            player.emit("Wolf", AbstractGame.this);
            return;
        }

        ///log
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer target: targets) {
            if (stringBuilder.length()>0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(target.toString());
        }
        String summary = "孤狼底牌" + stringBuilder.toString()+"号";
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s，翻看%d号底牌%s",
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

        player.getTargets().clear();
        player.setMission(null);
        
    }

    /**
     * 狼先知
     * @param player
     */
    public void mysticWolf(Player player) {
        Integer[] targets = player.getTargets().toArray(integers);
        // 查看牌面
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
        String summary = "翻看牌号" + stringBuilder.toString();
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s，翻看%d号身份牌%s",
                player.getSeat(),
                player.getUser(),
                targets[0],
                deck.get(targets[0]));
        partMovement.setDescription(description);
        partMovement.setSpell("狼先知行动");
        Movement movement = partMovement.clone();
        addLog(movement);
        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));

        player.getTargets().clear();
        player.setMission(null);
    }

    /**
     * 预言家操作
     * @param player
     */
    public void seer(Player player) {
        Integer[] targets = player.getTargets().toArray(integers);

        Integer caller = player.getSeat();
        List<Movement> movements = player.getMovements();

        Show show = new Show(caller, targets);

        Movement partMovement = show.cast(deck, player);
        movements.add(partMovement);
        ///log
        String summary = null;
        summary = "翻看牌号"+targets[0];
        if (targets.length == 2) {
            summary += ","+targets[1];
        }
        partMovement.setSummary(summary);
        String description = null;
        if (targets.length == 2) {
            description = String.format(
                    "%d号玩家%s，翻看%d号底牌%s和%d底牌%s",
                    player.getSeat(),
                    player.getUser(),
                    targets[0],
                    deck.get(targets[0]),
                    targets[1],
                    deck.get(targets[1]));
        } else {
            description = String.format(
                    "%d号玩家%s，翻看%d号玩家%s",
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

        player.getTargets().clear();
        player.setMission(null);

        
    }

    /**
     * 见习预言家
     * @param player
     */
    public void apprenticeSeer(Player player) {
        Integer target = player.getTargets().get(0);

        Integer caller = player.getSeat();
        List<Movement> movements = player.getMovements();

        Show show = new Show(caller, target);

        Movement partMovement = show.cast(deck, player);
        movements.add(partMovement);
        ///log
        String summary = null;
        summary = "翻看底牌"+target+"号";

        partMovement.setSummary(summary);
        String description = null;

        description = String.format(
                "%d号玩家%s，翻看%d号玩家%s",
                player.getSeat(),
                player.getUser(),
                target,
                deck.get(target));

        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell(player.getPoker()+"行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        player.getTargets().clear();
        player.setMission(null);
    }

    /**
     * 强盗操作
     * @param player
     */
    public void robber(Player player) {
        Integer target = player.getTargets().get(0);

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
                "%d号玩家%s，抢走%d号玩家%s",
                player.getSeat(),
                player.getUser(),
                target,
                targetPoker);
        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell(player.getPoker()+"行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        player.getTargets().clear();
        player.setMission(null);

        
    }

    /**
     * 女巫
     * @param player
     */
    public void witch(Player player) {
        Integer[] targets = player.getTargets().toArray(integers);

        Integer caller = player.getSeat();
        List<Movement> movements = player.getMovements();

        Movement partMovement = null;
        if (targets.length == 1) {
            // 查看
            Show show = new Show(caller, targets[0]);
            partMovement = show.cast(deck, player);

            movements.add(partMovement);

            player.emit("Witch", AbstractGame.this);
            return;
        }

        // 交换
        String targetPoker1 = deck.get(targets[0]);
        String targetPoker2 = deck.get(targets[1]);

        Swap swap = new Swap(caller, targets);
        partMovement = swap.cast(deck, player);

        movements.add(partMovement);

        String summary = "底牌"+targets[0]+"号换给了"+targets[1]+"号";
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s，查看%d号底牌%s，然后交换给%d号玩家%s",
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

        player.getTargets().clear();
        player.setMission(null);

    }

    /**
     * 捣蛋鬼操作
     * @param player
     */
    public void troubleMarker(Player player) {
        Integer[] targets = player.getTargets().toArray(integers);

        Integer caller = player.getSeat();
        List<Movement> movements = player.getMovements();
        //交换后不查看
        Swap swap = new Swap(caller, targets);

        String targetPoker1 = deck.get(targets[0]);
        String targetPoker2 = deck.get(targets[1]);

        Movement partMovement = swap.cast(deck, player);
        movements.add(partMovement);
        /// log
        String summary = "捣牌"+targets[0]+","+targets[1];
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s，交换%d号玩家%s和%d号玩家%s",
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

        player.getTargets().clear();
        player.setMission(null);

        
    }

    /**
     * 酒鬼操作
     * @param player
     */
    public void drunk(Player player) {
        Integer target = player.getTargets().get(0);

        Integer caller = player.getSeat();
        List<Movement> movements = player.getMovements();
        //交换但不能查看
        Swap swap = new Swap(caller, caller, target);
        String targetPoker = deck.get(target);
        Movement partMovement = swap.cast(deck, player);
        movements.add(partMovement);
        ///log
        String summary = "换"+target+"号底牌";
        partMovement.setSummary(summary);
        String description = String.format(
                "%d号玩家%s，换走%d号底牌%s",
                player.getSeat(),
                player.getUser(),
                target,
                targetPoker);
        System.out.println("####"+description+" 视角为:"+ JSON.toJSONString(player.getMovements().get(player.getMovements().size()-1).getViewport()));
        partMovement.setDescription(description);
        partMovement.setSpell(player.getPoker()+"行动");
        Movement movement = partMovement.clone();
        addLog(movement);

        player.getTargets().clear();
        player.setMission(null);

        
    }

    ///////////////////////////////////////////////
    public void speak(final Player player, final String string) {
        speakTotal++;
        Speak speak = new Speak();
        speak.setSeat(player.getSeat());
        speak.setImg(player.getImg());
        speak.setSpeech(string);
        speak.setUser(player.getUser());
        speakList.add(speak);
        player.getSpeaks().add(string);
        player.setMission(null);
        player.getTargets().clear();
    }

    /////////////////////////////////////////////
    /**
     * 就坐
     * @param seat
     * @param player
     */
    public synchronized boolean sitdown(int seat, Player player) {
        Player old = desktop.get(seat);
        if (old != null) {
            return false;
        }
        desktop.put(seat, player);
        player.setSeat(seat);
        return true;
    }

    /**
     * 设置就绪状态, 当返回true表示游戏开始
     * @param player
     * @param readyStatus
     * @return
     */
    public synchronized void setReadyStatus(Player player, boolean readyStatus) {
        player.setReady(readyStatus);
        if (readyStatus) {
            ready.add(player);
        } else {
            ready.remove(player);
        }
    }

    /**
     * 房主来开始游戏
     */
    public synchronized void startGame() {
        if (getStage() == null && ready.size() == getPlayerCount()) {
            replay();
        }
    }

    /**
     * 玩家投票
     * @param player
     * @param vote
     */
    public void vote(Player player, Integer vote) {
        player.setVote(vote);
        player.setMission(null);
        votes.put(player.getSeat(), vote);
    }

    /**
     * 猎人开枪
     * @param player
     * @param kill
     */
    public void hunter(Player player, Integer kill) {
        hunters.remove(player);
        hunterKill.put(player.getSeat(), kill);
        player.setMission(null);
        deadth.put(kill, 1);

    }

    /**
     * 重新开始
     * @param player
     */
    public synchronized void setRestartStatus(Player player, boolean readyStatus) {
        player.setReady(readyStatus);
        if (readyStatus) {
            ready.add(player);
        } else {
            ready.remove(player);
        }

//        if (getStage() == null && ready.size() == getPlayerCount()) {
//            reload();
//        }
    }

}
