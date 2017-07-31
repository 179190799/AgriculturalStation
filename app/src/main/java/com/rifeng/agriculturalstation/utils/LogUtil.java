package com.rifeng.agriculturalstation.utils;

import android.util.Log;

/**
 * Logcat统一管理类
 *
 * Created by chw on 2016/8/9.
 */
public class LogUtil {

    private LogUtil()
    {
		/* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "DEBUGING";

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isDebug){
            Log.i(TAG, msg);
        }
    }

    public static void d(String msg)
    {
        if (isDebug){
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg)
    {
        if (isDebug){
            Log.e(TAG, msg);
        }
    }

    public static void v(String msg)
    {
        if (isDebug){
            Log.v(TAG, msg);
        }
    }

    // 下面是传入自定义tag的函数
    /**
     * info输出Log信息
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg)
    {
        if (isDebug){
            Log.i(tag, msg);
        }
    }

    /**
     * debug输出Log信息
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg)
    {
        if (isDebug){
            Log.d(tag, msg);
        }
    }

    /**
     * error输出Log信息
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg)
    {
        if (isDebug){
            Log.e(tag, msg);
        }
    }

    /**
     * verbose输出Log信息
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg)
    {
        if (isDebug){
            Log.v(tag, msg);
        }
    }
}
