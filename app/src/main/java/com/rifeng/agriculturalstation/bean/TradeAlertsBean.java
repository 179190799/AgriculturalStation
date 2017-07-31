package com.rifeng.agriculturalstation.bean;

import android.support.annotation.NonNull;


import java.io.Serializable;

/**
 * 行业快讯
 * <p>
 * Created by chw on 2016/11/1.
 */
public class TradeAlertsBean implements Serializable {

    /**
     * 标题
     */
    public String name;
    /**
     * 内容
     */
    public String content;
    /**
     * 浏览量
     */
    public int viewnum;

    public int getDateline() {
        return dateline;
    }

    public void setDateline(int dateline) {
        this.dateline = dateline;
    }

    /**
     * 发布时间
     */
    public int dateline;



    /**
     * 发布者
     */
//    public String author;


}