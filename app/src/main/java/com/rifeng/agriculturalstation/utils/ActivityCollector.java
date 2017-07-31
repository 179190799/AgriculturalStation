package com.rifeng.agriculturalstation.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chw on 2017/3/6.
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finshAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
