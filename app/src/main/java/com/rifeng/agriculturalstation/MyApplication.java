package com.rifeng.agriculturalstation;

import android.app.Application;
import android.view.KeyEvent;

import com.rifeng.agriculturalstation.utils.ToastUtil;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/3/31.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
        //初始化sdk
        JPushInterface.setDebugMode(true);//正式版的时候设置false，关闭调试


        JPushInterface.init(this);

        //建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
        Set<String> set = new HashSet<>();
        set.add("andfixdemo");//名字任意，可多添加几个
        JPushInterface.setTags(this, set, null);//设置标签

    }

    //    记录第一次按下back键的时间，用于和第二次按下的时间相比较
    private long firstTime = 0;

}


