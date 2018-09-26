package org.idear.game.entity;

import org.idear.game.entity.movement.Motion;

import java.util.List;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 * 动作帧,具体而完整的行动，包含结果
 */
public class Movement {
    private Motion movement;//行动
    private Map<Integer, String> viewport;//所看到的牌面

}
