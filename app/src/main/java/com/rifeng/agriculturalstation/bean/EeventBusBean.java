package com.rifeng.agriculturalstation.bean;

/**
 * EventBus接收发送实体
 * Created by Administrator on 2017/8/25.
 */

public class EeventBusBean{
    private String msg;

    public EeventBusBean(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
