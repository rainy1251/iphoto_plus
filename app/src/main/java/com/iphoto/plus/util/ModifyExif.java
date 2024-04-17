package com.iphoto.plus.util;


import android.annotation.SuppressLint;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

public class ModifyExif {

    private static ExifInterface exif = null;

    @SuppressLint("SimpleDateFormat")
    public static void setExif(String filepath, String picId, String date, String orientation) {
        String photographerId = SPUtils.getString(Contents.Id);

        try {
//            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
//            Date parseDate = dateFormat.parse(date);
//            String curDate;
//            if (parseDate != null) {
//                curDate = DateFormatUtils.getDate(parseDate, "yyyy-MM-dd HH:mm:ss");
//            } else {
//                curDate = DateFormatUtils.getCurDate( "yyyy-MM-dd HH:mm:ss");
//            }
            exif = new ExifInterface(filepath);
            exif.setAttribute(ExifInterface.TAG_ARTIST, photographerId);
            exif.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, date);
            exif.setAttribute(ExifInterface.TAG_COPYRIGHT, picId);
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation);
            exif.saveAttributes();
        } catch (Exception e) {
            Log.e("rain", "cannot save exif", e);
        }
    }

    public static void setOriginExif(String filepath, String picId) {
        String photographerId = SPUtils.getString(Contents.Id);
        try {
            exif = new ExifInterface(filepath);
            exif.setAttribute(ExifInterface.TAG_ARTIST, photographerId);
            exif.setAttribute(ExifInterface.TAG_COPYRIGHT, picId);
            exif.saveAttributes();
        } catch (IOException e) {
            Log.e("rain", "cannot save exif", e);
        }
    }

    public static ExifInterface getExif(String filepath) {
        try {
            exif = new ExifInterface(filepath);    //想要获取相应的值：exif.getAttribute("对应的key")；比如获取时间：exif.getAttribute(ExifInterface.TAG_DATETIME);  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exif;
    }

    public static ExifInterface getExif(InputStream inputStream) {
        try {
            exif = new ExifInterface(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exif;
    }
}