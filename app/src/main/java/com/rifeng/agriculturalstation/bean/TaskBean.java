package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 任务
 * <p>
 * Created by chw on 2016/11/1.
 */
public class TaskBean implements Serializable {

    /**
     * 记录id
     */
    public int id;
    /**
     * 标题
     */

    public String name;
    /**
     * 农作物
     */
    public String content;


    //需要支付的投标保证金
    public float joinmoney;

    //审核状态，0：未审核，1：已审核
    public int pass;

    //项目保证金的支付状态 ， 1：已支付。0：未支付
    public int status;

    //需要支付的项目保证金
    public int taskmoney;

    //发布改任务的农场主id
    public int uid;

    /**
     * 所需的星级
     */
    public int needstar;
    /**
     * 作业面积
     */
    public double operatingarea;
    /**
     * 竞标最高限价
     */
    public double limitedprice;
    /**
     * 总价、项目款
     */
    public double totalprice;
    /**
     * 预计工期
     */
    public int timelimit;
    /**
     * 开工时间
     */
    public int starttime;
    /**
     * 承接类型、可接用户
     */
    public String meetuser;
    /**
     * 截止时间
     */
    public int enddate;
    /**
     * 当前状态，1 竞标中  2 作业中  3 已结束
     */
    public int curstatu;
    /**
     * 参与人数
     */
    public int participationnum;
    /**
     * 所在省份
     */
    public String provinces;
    /**
     * 所在城市
     */
    public String city;
    /**
     * 完成进度
     */
    public double curProgress;
    /**
     * 具体地址
     */
    public String detailaddress;
    /**
     * 主图
     */
    public String picfilepath;
    /**
     * 任务的评论
     */
    public String comment;
    /**
     * 评论等级 1好评 0 中评 -1差评
     */
    public int rating;
    /**
     * 所有图片
     */
    public String[] pictures;
    /**
     * 存储图片Bean
     */
    public List<String> picarr;
}
