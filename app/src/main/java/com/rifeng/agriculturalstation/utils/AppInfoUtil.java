package com.rifeng.agriculturalstation.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.rifeng.agriculturalstation.bean.AppInfoBean;

import java.io.File;

/**
 * App相关的辅助类
 *
 * Created by chw on 2016/8/10.
 */
public class AppInfoUtil {

    private AppInfoUtil()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取应用程序名称
     * @param context 上下文
     * @return 应用程序名称
     */
    public static String getAppName(Context context){
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序包名
     * @param context 上下文
     * @return 应用程序包名
     */
    public static String getPackageName(Context context){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序版本名称信息
     * @param context 上下文
     * @return 应用程序版本名称
     */
    public static String getVersionName(Context context){
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序版本号信息
     * @param context 上下文
     * @return 应用程序版本号
     */
    public static Integer getVersionCode(Context context){
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取apk安装包信息的方法，如图标、应用包名、应用名、版本名称、版本号等
     * @param context 上下文
     * @param file 安装包的完整路径
     */
    public static AppInfoBean getApkInfo(Context context, File file){
        AppInfoBean appInfoBean = null;
        if(file.isFile()){
            String fileName = file.getName();
            String apk_path = null;
            if(fileName.toLowerCase().endsWith(".apk")){
                apk_path = file.getAbsolutePath(); // apk文件的绝对路径
                LogUtil.i("getApkInfo" + apk_path + " ---- " + fileName);
                PackageManager pm = context.getPackageManager();
                PackageInfo packageInfo = pm.getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
                if(packageInfo != null){
                    appInfoBean = new AppInfoBean();
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    // 获取apk的图标
                    appInfo.sourceDir = apk_path;
                    appInfo.publicSourceDir = apk_path;
                    Drawable apk_icon = appInfo.loadIcon(pm);
                    appInfoBean.setApkIcon(apk_icon);
                    // apk文件的绝对路径
                    appInfoBean.setFilePath(apk_path);

                    // 得到应用包名
                    String packageName = packageInfo.packageName;
                    appInfoBean.setPackageName(packageName);

                    // 得到应用名
                    String appName = pm.getApplicationLabel(appInfo).toString();
                    appInfoBean.setAppName(appName);

                    // apk的版本名称
                    String versionName = packageInfo.versionName;
                    appInfoBean.setVersionName(versionName);

                    // apk的版本号
                    int versionCode = packageInfo.versionCode;
                    appInfoBean.setVersionCode(versionCode);
                    String pkgInfoStr = String.format("PackageName:%s, AppName:%s, VersionName:%s, VersionCode:%s", packageName, appName, versionName, versionCode);
                    LogUtil.i("getApkInfo", String.format("pkgInfo:%s", pkgInfoStr));
                }
            }
        }
        return appInfoBean;
    }
}































