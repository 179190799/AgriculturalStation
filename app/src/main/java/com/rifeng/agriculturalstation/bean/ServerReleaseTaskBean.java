package com.rifeng.agriculturalstation.bean;

/**
 * Created by Administrator on 2017/8/8.
 */

/**
 * 用来接收发布任务后返回的数据
 */
public class ServerReleaseTaskBean {
    public int code;
    public String msg;
    public int taskid; //发布的任务的id
    public float taskmoney; //发布项目后，需要支付的项目保证金金额

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    public float getTaskmoney() {
        return taskmoney;
    }

    public void setTaskmoney(float taskmoney) {
        this.taskmoney = taskmoney;
    }
}
