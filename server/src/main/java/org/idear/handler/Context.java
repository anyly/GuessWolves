package org.idear.handler;


import org.idear.CoherentMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idear on 2018/9/29.
 */
public class Context {
    private Player player;
    private LinkedHashMap<Integer, String> deck;
    private Map<Integer, Integer> votes;
    private List<Integer> deadth;
    private CoherentMap<Integer, Player> desktop;
    private Chain chain;

    public Context(Player player, LinkedHashMap<Integer, String> deck, Map<Integer, Integer> votes, List<Integer> deadth, CoherentMap<Integer, Player> desktop) {
        this.player = player;
        this.deck = deck;
        this.votes = votes;
        this.deadth = deadth;
        this.desktop = desktop;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public LinkedHashMap<Integer, String> getDeck() {
        return deck;
    }

    public void setDeck(LinkedHashMap<Integer, String> deck) {
        this.deck = deck;
    }

    public Map<Integer, Integer> getVotes() {
        return votes;
    }

    public void setVotes(Map<Integer, Integer> votes) {
        this.votes = votes;
    }

    public List<Integer> getDeadth() {
        return deadth;
    }

    public void setDeadth(List<Integer> deadth) {
        this.deadth = deadth;
    }

    public CoherentMap<Integer, Player> getDesktop() {
        return desktop;
    }

    public void setDesktop(CoherentMap<Integer, Player> desktop) {
        this.desktop = desktop;
    }

    public Chain getChain() {
        return chain;
    }

    public void setChain(Chain chain) {
        this.chain = chain;
    }
}
