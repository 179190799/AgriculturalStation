package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;

/**
 * Created by chw on 2016/10/28.
 */
public class MyMessageBean implements Serializable {

    /**
     * 记录id
     */
    private int id;
    /**
     * 标题
     */
    private String title;
    /**
     * 时间
     */
    private int dateline;
    /**
     * 内容
     */
    private String content;

    public MyMessageBean() {
    }

    public MyMessageBean(int id, String title, int dateline, String content) {
        this.id = id;
        this.title = title;
        this.dateline = dateline;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDateline() {
        return dateline;
    }

    public void setDateline(int dateline) {
        this.dateline = dateline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
