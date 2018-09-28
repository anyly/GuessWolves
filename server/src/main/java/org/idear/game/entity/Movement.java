package org.idear.game.entity;

import org.idear.game.entity.movement.Motion;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 * 动作帧,具体而完整的行动，包含结果
 */
public class Movement {
    private Motion movement;//行动
    private Map<Integer, String> viewport;//所看到的牌面

    public Movement(Motion movement, Movement prev) {
        this.movement = movement;
        if (prev == null) {
            viewport = new LinkedHashMap<>();
        } else {
            viewport = new LinkedHashMap<>(prev.viewport);
        }
    }

    public Motion getMovement() {
        return movement;
    }

    public void setMovement(Motion movement) {
        this.movement = movement;
    }

    public Map<Integer, String> getViewport() {
        return viewport;
    }

    public void setViewport(Map<Integer, String> viewport) {
        this.viewport = viewport;
    }
}
