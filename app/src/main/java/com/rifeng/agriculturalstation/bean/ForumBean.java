package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;

/**
 * 帖子
 *
 * Created by chw on 2016/10/31.
 */
public class ForumBean implements Serializable {

    /**
     * 记录id
     */
    public int id;
    /**
     * 帖子类型：1 [置顶]  2 [热帖]  3 [新帖]
     */
    public int type;
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
    /**
     * 发布时间
     */
    public int dateline;
}
