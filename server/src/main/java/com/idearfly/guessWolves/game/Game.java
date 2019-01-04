package com.idearfly.guessWolves.game;

import com.idearfly.timeline.Dispatcher;
import com.idearfly.timeline.Story;

/**
 * Created by idear on 2018/9/29.
 */
public class Game extends AbstractGame {

    @Override
    public Story story() {
        return storyConfiguration()
                .timeline()
                //就位- 废弃由房主开始
                .then("Ready")
                //系统发牌
                .then("Shuffle")
                //化身行动
                .then("Doppel")
                //化身立刻使用技能
                .then("AsSeer")
                .then("AsApprenticeSeer")
                .then("AsRobber")
                .then("AsWitch")
                .then("AsTroubleMarker")
                .then("AsDrunk")
                .then("Wolf")
                //刚入夜, 并行：狼人、爪牙、守夜人、预言家
                .meanwhile(new Dispatcher("EarlyNight")
                        .line("Wolves")
                        .line("MysticWolf")
                        .line("Minion")
                        .line("Mason")
                        .line("Seer")
                        .line("ApprenticeSeer")
                )
                //深夜，顺序执行：强盗、女巫、捣蛋鬼、酒鬼、失眠者
                //"MidNight"
                .then("Robber")
                .then("Witch")
                .then("TroubleMarker")
                .then("Drunk")

                .then("Insomniac")
                //破晓，随机选出发言者
                .then("Daybreak")
                //白天，顺序发言，持续3轮
                .then("Speak")
                //开启投票
                .then("Vote")
                //计算伤亡
                .then("Deadth")
                //猎杀时间
                .then("Hunter")
                //游戏结果
                .then("Result")
                .construct();
    }
}
