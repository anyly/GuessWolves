package com.idearfly.guessWolves.game;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.idearfly.collection.CoherentMap;
import com.idearfly.guessWolves.game.entity.Camp;
import com.idearfly.guessWolves.game.entity.Movement;
import com.idearfly.guessWolves.game.entity.Report;
import com.idearfly.guessWolves.game.entity.spell.Replicate;
import com.idearfly.guessWolves.game.entity.spell.Rob;
import com.idearfly.guessWolves.game.entity.spell.Show;
import com.idearfly.guessWolves.game.entity.spell.Switch;
import com.idearfly.timeline.Dispatcher;
import com.idearfly.timeline.Event;
import com.idearfly.timeline.Plot;
import com.idearfly.timeline.Story;
import com.idearfly.timeline.websocket.BaseGame;
import com.idearfly.timeline.websocket.Log;
import com.idearfly.util.StringUtil;

import java.util.*;

/**
 * Created by idear on 2018/9/29.
 */
public class Game extends AbstractGame {

    @Override
    public Story story() {
        return storyConfiguration()
                .timeline()
                //就位
                .then("Ready")
                //系统发牌
                .then("Shuffle")
                //化身行动
                .then("Doppel")
                //刚入夜, 并行：狼人、爪牙、守夜人、预言家
                .meanwhile(new Dispatcher("EarlyNight")
                        .line("Wolves")
                        .line("Minion")
                        .line("Mason")
                        .line("Seer")
                )
                //深夜，顺序执行：强盗、捣蛋鬼、酒鬼、失眠者
                //"MidNight"
                .then("AsRobber")
                .then("Robber")

                .then("AsTroubleMarker")
                .then("TroubleMarker")

                .then("AsDrunk")
                .then("Drunk")

                .then("Insomniac")
                //白天，顺序发言，持续3轮
                .then("Speek")
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
