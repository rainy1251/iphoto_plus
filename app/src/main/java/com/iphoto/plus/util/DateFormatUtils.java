package com.iphoto.plus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateFormatUtils {

    public static String formatDate(String dateString) {
        // 设置原始时间的时区为UTC
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // 设置输出时间的格式
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            // 解析原始时间
            Date date = inputFormat.parse(dateString);
            // 格式化输出时间
            String formattedTime = outputFormat.format(date);
            return formattedTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long dateTimeMs(String strTime) {
        long returnMillis = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = null;
        try {
            d = sdf.parse(strTime);
            returnMillis = d.getTime();
        } catch (Exception e) {

        }
        return returnMillis;
    }

    public static String formatMils(Long millSec) {
        Date date = new Date(millSec);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        return sdf.format(date);
    }

    public static String getDate(Date date,String pattern) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(date);
    }

    public static String getCurDate(String pattern) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
        return sDateFormat.format(new Date());
    }
}
