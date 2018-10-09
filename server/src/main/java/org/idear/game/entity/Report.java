package org.idear.game.entity;

/**
 * Created by idear on 2018/9/29.
 */
public class Report {
    private Camp town;// 村民阵营
    private Camp wolves;// 狼人阵营
    private Camp cobbler;// 皮匠阵营
    private Camp all;// 所有人
    private String description;// 描述

    public Camp getTown() {
        return town;
    }

    public void setTown(Camp town) {
        this.town = town;
    }

    public Camp getWolves() {
        return wolves;
    }

    public void setWolves(Camp wolves) {
        this.wolves = wolves;
    }

    public Camp getCobbler() {
        return cobbler;
    }

    public void setCobbler(Camp cobbler) {
        this.cobbler = cobbler;
    }

    public Camp getAll() {
        return all;
    }

    public void setAll(Camp all) {
        this.all = all;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
