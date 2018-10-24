package com.idearfly.guessWolves.game.entity;

import com.alibaba.fastjson.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by idear on 2018/9/29.
 * 阵营
 */
public class Camp {
    private String name;
    private boolean win;
    private List<JSONObject> members = new LinkedList<>();

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

    public List<JSONObject> getMembers() {
        return members;
    }

    public void setMembers(List<JSONObject> members) {
        this.members = members;
    }
}
