package com.rifeng.agriculturalstation.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 获取设备唯一标识
 *
 * 用到的两个权限
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 *
 * Created by chw on 2016/9/29.
 */
public class DeviceId {

    public static String getDeviceId(Context context){
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");

        // wifi mac地址
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String wifiMac = info.getMacAddress();
        if(wifiMac != null){
            deviceId.append("wifi");
            deviceId.append(wifiMac);
            Log.i("DeviceId", deviceId.toString());
            return deviceId.toString();
        }

        // IMEI(imei)
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if(imei != null){
            deviceId.append("imei");
            deviceId.append(imei);
            Log.i("DeviceId", deviceId.toString());
            return deviceId.toString();
        }

        // 序列号（sn）
        String sn = tm.getSimSerialNumber();
        if(sn != null){
            deviceId.append("sn");
            deviceId.append(sn);
            Log.i("DeviceId", deviceId.toString());
            return deviceId.toString();
        }
        
        //如果上面都没有，则生成一个id：随机码
//        String uuid = getUUID(context);
        return deviceId.toString();
    }

    /**
     * 得到全局唯一UUID
     *
     * @param context
     * @return
     */
    private static String getUUID(Context context) {
//        SharedPreferences mShare = getSysShare(context, "sysCacheMap");
        return null;
    }
}
