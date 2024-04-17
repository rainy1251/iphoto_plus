package com.iphoto.plus.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import androidx.exifinterface.media.ExifInterface;

import com.iphoto.plus.bean.PicBean;
import com.iphoto.plus.view.AppDB;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class FilesUtil {

    public static String getFileFromBytes(String albumId, String uploadType, byte[] b, int length
            , String fileName, int handle, String px) {

        if (uploadType.equals(Contents.RATING_UPLOAD)) {

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b, 12, length - 12);
            ExifInterface exif = ModifyExif.getExif(byteArrayInputStream);
            String xmp = exif.getAttribute(ExifInterface.TAG_XMP);
            boolean rating = isRating(xmp);
            if (!rating) {
                return "";
            }
        }

        if (px.equals(Contents.Standard)) {

            return saveImageForBmp(albumId, b, length, fileName, handle);
        } else if (px.equals(Contents.Original)) {
            try {
                File file = new File(getPicPath(albumId, fileName));
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(b, 12, length - 12);
                fos.flush();
                fos.close();
                if (file.exists()) {
                    PicBean picBean = new PicBean(fileName, length, file.getAbsolutePath(), String.valueOf(handle), 0, 0);
                    ModifyExif.setOriginExif(file.getAbsolutePath(), String.valueOf(handle));
                    AppDB.getInstance().picDao().insert(picBean);
                    return file.getAbsolutePath();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";


    }

    private final static int FZ_KB = 1024;
    private final static int FZ_MB = 1024 * FZ_KB;
    private final static int FZ_GB = 1024 * FZ_MB;
    private final static int FZ_PB = 1024 * FZ_GB;

    /**
     * 照片大小
     *
     * @param fileLength
     * @return
     */
    public static String formatSize(long fileLength) {
        StringBuilder sb = new StringBuilder();
        if (fileLength < FZ_KB) {
            sb.append(formatDouble(fileLength, 1)).append(" b");
        } else if (fileLength <= FZ_MB) {
            sb.append(formatDouble(fileLength, FZ_KB)).append(" K");
        } else if (fileLength <= FZ_GB) {
            sb.append(formatDouble(fileLength, FZ_MB)).append(" M");
        } else if (fileLength <= FZ_PB) {
            sb.append(formatDouble(fileLength, FZ_GB)).append(" GB");
        } else {
            sb.append(formatDouble(fileLength, FZ_PB)).append(" PB");
        }
        return sb.toString();
    }

    public static String formatDouble(long value, int divider) {
        double result = value * 1.0 / divider;
        return String.format(Locale.getDefault(), "%.2f", result);
    }


    public static String getDirPath(String albumId) {
        String absolutePath = UiUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String basePath = absolutePath + File.separator + "YAOPAI";
        return basePath + File.separator + albumId;
    }
    public static String getBasePath() {
        String absolutePath = UiUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String basePath = absolutePath + File.separator + "YAOPAI";
        return basePath ;
    }

    public static String getPicPath(String albumId, String fileName) {
        return getDirPath(albumId) + File.separator + fileName;
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete(); //删除该文件
        }
    }

    public static void deleteDirectory(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteDirectory(childFiles[i]);
            }
            file.delete();
        }
    }

    /**
     * 从sd卡获取图片ID
     */
    public static void getImageFromSD(String albumId) {

        File fileAll = new File(AppConfig.BASE_PATH, albumId);
        if (!fileAll.exists()) {
            return;
        }
        File[] files = fileAll.listFiles();
        if (files == null) {
            return;
        }

        if (files.length == 0) {
            return;
        }

        for (File file : files) {
            String id = ModifyExif.getExif(file.getAbsolutePath()).getAttribute(ExifInterface.TAG_COPYRIGHT);
            PicBean picBean = new PicBean(file.getName(), (int) file.length(), file.getAbsolutePath(), id, 0, 0);
            AppDB.getInstance().picDao().insert(picBean);
            String xmp = ModifyExif.getExif(file.getAbsolutePath()).getAttribute(ExifInterface.TAG_XMP);
            boolean rating = isRating(xmp);
            //  getRating(xmp);
        }
    }

    private static boolean isRating(String xmp) {

        if (xmp != null) {
            if (xmp.contains("<xmp:Rating>0</xmp:Rating>")) {
                return false;
            } else return xmp.contains("<xmp:Rating>1</xmp:Rating>")
                    || xmp.contains("<xmp:Rating>2</xmp:Rating>")
                    || xmp.contains("<xmp:Rating>3</xmp:Rating>")
                    || xmp.contains("<xmp:Rating>4</xmp:Rating>")
                    || xmp.contains("<xmp:Rating>5</xmp:Rating>");
        } else {
            return false;
        }

    }

    /**
     * 获取SD卡的照片数量
     */
    public static int getImageIdFromSD(String albumId) {
        File fileAll = new File(getBasePath(), albumId);
        if (fileAll.exists()) {
            File[] files = fileAll.listFiles();
            if (files != null) {
                return files.length;
            }
        }
        return 0;
    }

    /**
     * 按文件名排序
     *
     * @param filePath
     */
    public static ArrayList<String> orderByName(String filePath) {
        ArrayList<String> FileNameList = new ArrayList<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        List fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (File file1 : files) {
            if (file1.isDirectory()) {
                FileNameList.add(file1.getName());
            }
        }
        return FileNameList;
    }

    /**
     * 按文件修改时间排序
     *
     * @param filePath
     */
    public static ArrayList<String> orderByDate(String filePath) {
        ArrayList<String> FileNameList = new ArrayList<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
            }

            public boolean equals(Object obj) {
                return true;
            }

        });

        for (File file1 : files) {
            if (file1.isDirectory()) {
                FileNameList.add(file1.getName());
            }
        }
        return FileNameList;
    }


    /**
     * 保存图片
     */
    public static String saveImageForBmp(String albumId, byte[] b, int length, String fileName
            , int handle) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(b, 12, length - 12);
        ExifInterface exif = ModifyExif.getExif(byteArrayInputStream);
        String orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        String originalDate = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 12, length - 12, options);
        File file = new File(getPicPath(albumId, fileName));
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            if (file.exists()) {
                PicBean picBean = new PicBean(fileName, (int) file.length(), file.getAbsolutePath(), String.valueOf(handle), 0, 0);
                ModifyExif.setExif(file.getAbsolutePath(), String.valueOf(handle), originalDate, orientation);
                AppDB.getInstance().picDao().insert(picBean);
                return file.getAbsolutePath();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recycleBitmap(bmp);
        return "";
    }


    //回收Bitmap
    public static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }
}
