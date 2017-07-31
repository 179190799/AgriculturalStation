package com.rifeng.agriculturalstation.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间转换工具类
 *
 * Created by chw on 2016/8/9.
 */
public class DateUtil {

    /**
     * 调用此方法输入所要转换的时间和类型，返回时间戳(字符串)
     * 例如:
     *  输入时间、类型（"2016年02月18日09时43分" 、 "yyyy年MM月dd日HH时mm分ss秒"）
     *            （"2016-02-18 09:43" 、 "yyyy-MM-dd HH:mm"）
     * @param time 所要转换的时间
     * @param type 所要转换的类型
     * @return 返回时间戳(字符串)
     */
    public static String getTimeStamp(String time, String type){
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdf.parse(time);
            //返回一个long型的毫秒数
            long l = date.getTime();
            String string = String.valueOf(l);
            times = string.substring(0, 10);
            //System.out.println("  ------  " + times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 调用此方法输入所要转换的时间戳和类型，返回格式化时间（字符串）
     * @param time 所要转换的时间戳
     * @param type 所要转换的类型
     * @return 格式化时间（字符串）
     */
    public static String getTime(String time, String type){
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        long lcc = Long.parseLong(time);
        String times = sdf.format(new Date(lcc * 1000L));
        return times;
    }

    /**
     * 计算两个时间戳之间相差的天数
     *
     * @param startTime 较小的时间
     * @param endTime 较大的时间
     * @return 相差的天数
     */
    public static int differentDaysByMillisecond(long startTime, long endTime){
        return (int) ((endTime - startTime) / (3600 * 24));
    }

    /**
     * 输入所要转换的时间戳（毫秒），返回该时间戳对应的星期
     * @param timeStamp 时间戳（毫秒）
     * @return 对应的星期
     */
    public static String getWeek(String timeStamp){
        long lcc = Long.parseLong(timeStamp);
        int myDate = 0;
        String week = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(lcc * 1000L));
        myDate = calendar.get(Calendar.DAY_OF_WEEK);
        //获取指定日期转换成星期几
        switch (myDate) {
            case 1:
                week = "星期日";
                break;

            case 2:
                week = "星期一";
                break;

            case 3:
                week = "星期二";
                break;

            case 4:
                week = "星期三";
                break;

            case 5:
                week = "星期四";
                break;

            case 6:
                week = "星期五";
                break;

            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

    /**
     * 输入日期如（2016年02月18日13时32分20秒），返回对应的星期数
     * @param time 日期   如（2016年02月18日13时32分20秒）
     * @param type 对应的格式化类型   如（yyyy年MM月dd日HH时mm分ss秒）
     * @return 对应的星期数
     */
    public static String dayToWeek(String time, String type){
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        Date date = null;
        int myDate = 0;
        String week = null;
        try {
            date = sdf.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            myDate = calendar.get(Calendar.DAY_OF_WEEK);
            //获取指定日期转换成星期几
            switch (myDate) {
                case 1:
                    week = "星期日";
                    break;

                case 2:
                    week = "星期一";
                    break;

                case 3:
                    week = "星期二";
                    break;

                case 4:
                    week = "星期三";
                    break;

                case 5:
                    week = "星期四";
                    break;

                case 6:
                    week = "星期五";
                    break;

                case 7:
                    week = "星期六";
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week;
    }

    /**
     * 获取当前时间，并且格式化时间
     * @param type 要格式化的类型
     * @return 格式化后的当前时间
     */
    public static String getCurrentTime(String type){
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
//    public static long getTimeStamp(){
//        // 方法一
//        return System.currentTimeMillis();
//        // 方法二
////        Calendar.getInstance().getTimeInMillis();
//        // 方法三
////        new Date().getTime();
//    }
}













