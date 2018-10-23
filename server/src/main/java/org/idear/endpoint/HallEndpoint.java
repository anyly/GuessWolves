package org.idear.endpoint;

import com.idearfly.timeline.websocket.GameEndpoint;

import javax.websocket.server.ServerEndpoint;

/**
 * Created by idear on 2018/9/21.
 * 大厅，负责创建和销毁房间
 */
@ServerEndpoint("/hall")
public class HallEndpoint extends GameEndpoint {

    public boolean onFindGame(Integer no) {
        if (no == null) {
            return false;
        }
        return gameCenter.game(no) != null;
    }

    public void onEditImg(String img) {
        this.img = img;
    }

}
