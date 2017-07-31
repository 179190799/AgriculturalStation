package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;

/**
 * Created by chw on 2017/2/4.
 */
public class PicturesBean implements Serializable {

    /**
     * 记录id
     */
    private int id;
    /**
     * 用户uid
     */
    private int uid;
    /**
     * 农机主id
     */
    private int ownerid;
    /**
     * 农场主id
     */
    private int farmerid;
    /**
     * 任务id
     */
    private int taskid;
    /**
     * 图片路径
     */
    private String picfilepath;
    /**
     * 时间
     */
    private int dateline;

    public PicturesBean() {
    }

    public PicturesBean(int id, int uid, int ownerid, int farmerid, int taskid, String picfilepath, int dateline) {
        this.id = id;
        this.uid = uid;
        this.ownerid = ownerid;
        this.farmerid = farmerid;
        this.taskid = taskid;
        this.picfilepath = picfilepath;
        this.dateline = dateline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }

    public int getFarmerid() {
        return farmerid;
    }

    public void setFarmerid(int farmerid) {
        this.farmerid = farmerid;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public String getPicfilepath() {
        return picfilepath;
    }

    public void setPicfilepath(String picfilepath) {
        this.picfilepath = picfilepath;
    }

    public int getDateline() {
        return dateline;
    }

    public void setDateline(int dateline) {
        this.dateline = dateline;
    }
}






















































