package com.idearfly.guessWolves.game.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idear on 2018/9/27.
 * 动作帧,具体而完整的行动，包含结果
 */
public class Movement {
    /**
     * 详细描述
     */
    private String description;
    /**
     * 摘要信息
     */
    private String summary;

    /**
     * 使用技能
     */
    private String spell;

    /**
     * 发起人
     */
    private Integer caller;

    /**
     * 目标
     */
    private Integer[] targets;

    /**
     * 视野
     */
    private Map<Integer, String> viewport;

    @Override
    public Movement clone() {
        Movement movement = new Movement(this);
        movement.spell = this.spell;
        movement.description = this.description;
        movement.summary = this.summary;
        movement.caller = this.caller;
        movement.targets = this.targets;
        return movement;
    }

    public Movement(Movement prev) {
        if (prev == null) {
            viewport = new LinkedHashMap<>();
        } else {
            viewport = new LinkedHashMap<>(prev.viewport);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public Integer getCaller() {
        return caller;
    }

    public void setCaller(Integer caller) {
        this.caller = caller;
    }

    public Integer[] getTargets() {
        return targets;
    }

    public void setTargets(Integer[] targets) {
        this.targets = targets;
    }

    public Map<Integer, String> getViewport() {
        return viewport;
    }

    public void setViewport(Map<Integer, String> viewport) {
        this.viewport = viewport;
    }
}
