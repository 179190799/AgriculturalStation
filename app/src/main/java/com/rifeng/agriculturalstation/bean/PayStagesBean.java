package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;

/**
 * 支付项目款实体
 * Created by Administrator on 2017/9/6.
 */

public class PayStagesBean implements Serializable {
    /**
     * id : 15
     * taskid : 90
     * owneruid : 5
     * farmeruid : 0
     * stages : 1
     * money : 1250.00
     * status : 1
     * dateline : 1504592623
     * updatetime : 1504592623
     */

    private int id;
    private int taskid;
    private String owneruid;
    private String farmeruid;
    private String stages;
    private String money;
    private int status;
    private String dateline;
    private String updatetime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getOwneruid() {
        return owneruid;
    }

    public void setOwneruid(String owneruid) {
        this.owneruid = owneruid;
    }

    public String getFarmeruid() {
        return farmeruid;
    }

    public void setFarmeruid(String farmeruid) {
        this.farmeruid = farmeruid;
    }

    public String getStages() {
        return stages;
    }

    public void setStages(String stages) {
        this.stages = stages;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}
