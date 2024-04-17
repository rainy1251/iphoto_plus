package com.iphoto.plus.util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class TimeFormUtil {

    public static String formTimeT(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            String format = dateFormat.format(date);
            return format;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;

    }

    public static String formTimeT1(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = dateFormat.format(date);
            return format;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;

    }

    public static String formTimeT2(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String format = dateFormat.format(date);
            return format;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;

    }

    public static String formTimeT3(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = dateFormat.format(date);
            return format;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;

    }

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String time)  {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
    public static long dateToLong(String time)  {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(long timeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillis);
        return simpleDateFormat.format(date);
    }

    public static String localFormart(String location) {
        String local = "";
        if (location == null) {
            return local;
        }
        if (location.contains("/")&&location.length()>1) {

            String[] split = location.split("/");
            local = split[0];

        } else {
            return location;
        }
        return local;
    }

    /**
     * 获取时间
     * @param date
     * @return
     */
    public static String getTime(Date date) {//可根据需要自行截取数据显示

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 获取时间
     * @param date
     * @return
     */
    public static String getTime1(Date date) {//可根据需要自行截取数据显示

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }
}