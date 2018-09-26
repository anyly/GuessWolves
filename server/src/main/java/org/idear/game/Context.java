package org.idear.game;

import org.idear.CoherentMap;
import org.idear.game.entity.Deck;
import org.idear.game.entity.Player;

import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 */
public class Context {
    private Map<String, Player> playerMap;// 玩家信息，进入房间后记录
    private CoherentMap<String, Integer> sitdownMap;// 就坐情况，用于游戏就绪
    private Deck deck;//牌面,开始游戏后发牌
    private int step = 0;//0进入房间/1游戏就绪/2开始游戏/3天黑闭眼/4天亮行动/5发言结束/6投票时间/7猎人权力

    public Map<String, Player> getPlayerMap() {
        return playerMap;
    }

    public void setPlayerMap(Map<String, Player> playerMap) {
        this.playerMap = playerMap;
    }

    public CoherentMap<String, Integer> getSitdownMap() {
        return sitdownMap;
    }

    public void setSitdownMap(CoherentMap<String, Integer> sitdownMap) {
        this.sitdownMap = sitdownMap;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
