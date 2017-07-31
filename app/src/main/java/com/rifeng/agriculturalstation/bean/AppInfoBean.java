package com.rifeng.agriculturalstation.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by chw on 2016/9/1.
 */
public class AppInfoBean {

    /**
     * apk的图标
     */
    private Drawable apkIcon;
    /**
     * apk的包名
     */
    private String packageName;

    /**
     * apk的名字
     */
    private String appName;

    /**
     * apk的绝对路径
     */
    private String filePath;

    /**
     * apk的版本名称
     */
    private String versionName;

    /**
     * apk的版本号
     */
    private int versionCode;

    public Drawable getApkIcon() {
        return apkIcon;
    }

    public void setApkIcon(Drawable apkIcon) {
        this.apkIcon = apkIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}































