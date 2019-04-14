package com.caldremch.ffmpegcore.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * @author Caldremch
 * @date 2019-04-11 15:31
 * @email caldremch@163.com
 * @describe
 **/
public class FileUtils {

    public static String TMP_DIR = "/storage/emulated/0/Android/FFmpegCore";
    /**
     * 删除文件
     * @param fileName
     */
    public static boolean deleteFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        return deleteFile(new File(fileName));
    }

    /**
     * 删除文件
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        boolean result = true;
        if (null != file) {
            result = file.delete();
        }
        return result;
    }


    /**
     * 创建文件路径
     * @param dir
     * @param suffix
     * @return
     */
    public static String createPath(String dir, String suffix) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);
        year = year - 2000;
        String name = dir;
        File d = new File(name);

        // 如果目录不中存在，创建这个目录
        if (!d.exists())
            d.mkdir();
        name += "/";


        name += String.valueOf(year);
        name += String.valueOf(month);
        name += String.valueOf(day);
        name += String.valueOf(hour);
        name += String.valueOf(minute);
        name += String.valueOf(second);
        name += String.valueOf(millisecond);
        if (!suffix.startsWith(".")) {
            name += ".";
        }
        name += suffix;
        return name;
    }

    /**
     * 创建文件路径
     * @param suffix
     * @return
     */
    public static String createPathInBox(String suffix) {
        return createPath(TMP_DIR, suffix);
    }

    /**
     * 创建指定后缀的文件
     * @param suffix
     * @return
     */
    public static String createFileInBox(String suffix) {
        return createFile(TMP_DIR, suffix);
    }


    /**
     * 在指定目录创建指定后缀文件
     * @param dir
     * @param suffix
     * @return
     */
    public static String createFile(String dir, String suffix) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int second = c.get(Calendar.SECOND);
        int millisecond = c.get(Calendar.MILLISECOND);
        year = year - 2000;
        String name = dir;
        File d = new File(name);

        // 如果目录不中存在，创建这个目录
        if (!d.exists())
            d.mkdir();
        name += "/";


        name += String.valueOf(year);
        name += String.valueOf(month);
        name += String.valueOf(day);
        name += String.valueOf(hour);
        name += String.valueOf(minute);
        name += String.valueOf(second);
        name += String.valueOf(millisecond);
        if (suffix.startsWith(".") == false) {
            name += ".";
        }
        name += suffix;

        try {
            Thread.sleep(1);  //保持文件名的唯一性.
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File file = new File(name);
        if (file.exists() == false) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return name;
    }

}
