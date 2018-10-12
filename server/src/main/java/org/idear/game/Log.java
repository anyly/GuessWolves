package org.idear.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 操作日志
 */
public class Log {
    /**
     * 详细描述
     */
    private String description;
    /**
     * 摘要信息
     */
    private String summary;
    /**
     * 视角
     */
    private List<Map<Integer, String>> viewports = new LinkedList<>();

    /**
     * 发起人
     */
    private Integer caller;

    /**
     * 目标
     */
    private Integer[] targets;

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

    public List<Map<Integer, String>> getViewports() {
        return viewports;
    }

    public void setViewports(List<Map<Integer, String>> viewports) {
        this.viewports = viewports;
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
}
