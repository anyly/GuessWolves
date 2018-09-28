package org.idear.game;

import com.alibaba.fastjson.JSONObject;
import org.idear.endpoint.Player;

/**
 * Created by idear on 2018/9/28.
 */
public class NotifyPlayer implements Stage {
    private Game game;
    private String name;
    private String poker;
    private String next;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPoker() {
        return poker;
    }

    public void setPoker(String poker) {
        this.poker = poker;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public NotifyPlayer(Game game, String name, String poker, String next) {
        this.game = game;
        this.name = name;
        this.poker = poker;
        this.next = next;
    }

    public NotifyPlayer(Game game, String name, String poker) {
        this(game, name, poker, null);
    }

    @Override
    public boolean execute(Player player, JSONObject jsonObject) {
        Player findPlayer = null;
        JSONObject data = null;

        findPlayer = game.findPlayer(poker);
        if (findPlayer != null) {
            if (this.next != null) {
                data = new JSONObject();
                data.put("current", this.name);
                data.put("next", this.next);
            }
            if (findPlayer == player) {
                findPlayer.emit(name, data);
            }
        } else {
            return true;
        }
        return false;
    }
}
