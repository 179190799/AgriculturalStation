package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;

/**
 * Created by chw on 2017/1/21.
 */
public class ForumCommentBean implements Serializable {

    /**
     * 记录id
     */
    private int id;
    /**
     * 评论人
     */
    private String username;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论时间
     */
    private String dateline;

    public ForumCommentBean() {
    }

    public ForumCommentBean(int id, String username, String content, String dateline) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.dateline = dateline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }
}
