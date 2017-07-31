package com.rifeng.agriculturalstation.bean;

import java.io.Serializable;

/**
 * Created by chw on 2016/11/29.
 */
public class UserBean implements Serializable {

    /**
     * 用户id
     */
    private int uid;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 用户类型
     */
    private int usertype;
    /**
     * 真实名字
     */
    private String realname;
    /**
     * 用户名
     */
    private String username;
    /**
     * 联系号码
     */
    private String phone;
    /**
     * 身份证号
     */
    private String idcard;
    /**
     * 省份
     */
    private String resideprovince;
    /**
     * 城市
     */
    private String residecity;
    /**
     * 地址
     */
    private String resideaddress;
    /**
     * 证件一
     */
    private String certificate;
    /**
     * 证件二
     */
    private String certificate1;

    public UserBean() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getResideprovince() {
        return resideprovince;
    }

    public void setResideprovince(String resideprovince) {
        this.resideprovince = resideprovince;
    }

    public String getResidecity() {
        return residecity;
    }

    public void setResidecity(String residecity) {
        this.residecity = residecity;
    }

    public String getResideaddress() {
        return resideaddress;
    }

    public void setResideaddress(String resideaddress) {
        this.resideaddress = resideaddress;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getCertificate1() {
        return certificate1;
    }

    public void setCertificate1(String certificate1) {
        this.certificate1 = certificate1;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "uid=" + uid +
                ", avatar='" + avatar + '\'' +
                ", usertype=" + usertype +
                ", realname='" + realname + '\'' +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", idcard='" + idcard + '\'' +
                ", resideprovince='" + resideprovince + '\'' +
                ", residecity='" + residecity + '\'' +
                ", resideaddress='" + resideaddress + '\'' +
                ", certificate='" + certificate + '\'' +
                ", certificate1='" + certificate1 + '\'' +
                '}';
    }
}
























































