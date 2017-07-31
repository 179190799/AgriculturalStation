package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;

/**
 * 交易记录
 *
 * Created by chw on 2017/1/19.
 */
public class TradingRecordBean implements Serializable {

    /**
     * 记录id
     */
    public int id;
    /**
     * 用户id
     */
    public int uid;
    /**
     * 标题
     */
    public String designation;
    /**
     * 操作
     */
    public String operation;
    /**
     * 时间
     */
    public String dateline;
    /**
     * 金额
     */
    public double money;
    /**
     * 订单id
     */
    public String orderid;
    /**
     * 接收人
     */
    public String recipient;
    /**
     * 交易状态
     */
    public int state;
}
