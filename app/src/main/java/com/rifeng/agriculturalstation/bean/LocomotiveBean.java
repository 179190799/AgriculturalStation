package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 农机手
 *
 * Created by chw on 2016/11/2.
 */
public class LocomotiveBean implements Serializable {

    /**
     * 农机id
     */
    public int id;
    /**
     * 农机手
     */
    public String locomaster;
    /**
     * 机车名称
     */
    public String locomotive;
    /**
     * 工作性质
     */
    public int naturework;
    /**
     * 运营时间
     */
    public String operatingtime;
    /**
     * 目前状态
     */
    public int status;
    /**
     * 省份
     */
    public String provinces;
    /**
     * 城市
     */
    public String city;
    /**
     * 主图
     */
    public String locopic;
    /**
     * 所有图片
     */
    public String[] pictures;
    /**
     * 存储图片Bean
     */
    public List<String> picarr;
}
