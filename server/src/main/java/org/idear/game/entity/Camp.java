package org.idear.game.entity;

import org.idear.handler.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/29.
 * 阵营
 */
public class Camp {
    private String name;
    private boolean win;
    private List<Player> members = new LinkedList<>();

    public Camp(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }
}
