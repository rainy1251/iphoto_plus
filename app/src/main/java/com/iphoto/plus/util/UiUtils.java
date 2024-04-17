package com.iphoto.plus.util;

import android.content.Context;
import android.os.Environment;

import com.iphoto.plus.BaseApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class UiUtils {

    public static Context getContext() {
        return BaseApplication.getInstance();
    }
    public static List<String> getImagePathFromSD(String activity_title) {
        List<String> imagePathList = new ArrayList<>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        String filePath = Environment.getExternalStorageDirectory() + "/YAOPAI" + File.separator
                + activity_title;
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        if (files != null) {

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                imagePathList.add(file.getPath());
            }
        }
        // 返回得到的图片列表
        return imagePathList;
    }
}
