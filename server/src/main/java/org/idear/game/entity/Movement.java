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
    private Motion motion;//行动
    private Map<Integer, String> viewport;//所看到的牌面

    public Movement(Motion motion, Movement prev) {
        this.motion = motion;
        if (prev == null) {
            viewport = new LinkedHashMap<>();
        } else {
            viewport = new LinkedHashMap<>(prev.viewport);
        }
    }

    public Motion getMotion() {
        return motion;
    }

    public void setMotion(Motion motion) {
        this.motion = motion;
    }

    public Map<Integer, String> getViewport() {
        return viewport;
    }

    public void setViewport(Map<Integer, String> viewport) {
        this.viewport = viewport;
    }
}
