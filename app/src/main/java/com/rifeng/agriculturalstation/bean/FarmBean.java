package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 农场信息
 *
 * Created by chw on 2016/10/31.
 */
public class FarmBean implements Serializable {

    /**
     * 农场id
     */
    public int id;
    /**
     * 农场名称
     */
    public String name;
    /**
     * 农场主
     */
    public String farmer;
    /**
     * 农场面积
     */
    public double floorspace;
    /**
     * 主要农作物
     */
    public String mainproduct;
    /**
     * 农场地址
     */
    public String farmaddress;
    /**
     * 农场主图片
     */
    public String farmpic;
    /**
     * 所在省份
     */
    public String provinces;
    /**
     * 所在城市
     */
    public String city;
    /**
     * 所有的图片
     */
    public String[] pictures;
    /**
     * 存储图片Bean
     */
    public List<String> picarr;
}