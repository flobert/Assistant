package com.colaapk.assistant;

import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LLY on 2017/7/5.
 */

public class FileUtils {
    public static String getString(File file) {
        if (file.exists() && file.length() == 0) {
            return "";
        }
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[1024];
            while (-1 != (len = fis.read(buf))) {
                bos.write(buf, 0, len);
            }
            fis.close();
            bos.close();
            return bos.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void saveData(File file, String value) {
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(value.getBytes("UTF-8"));
            fos.close();
            Log.i("FileUtils", "saveData: " + getString(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * 创建刷步文件对象
    * */
    public static File getExternalSDCardPath() {
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String d = sf.format(date);
        Log.i("MainActivity", "date: " + d);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Pedometer" + File.separator + ".cypedometer");
            if (!file.exists()) {
                file.mkdirs();
            }
            String path = file.getAbsolutePath() + File.separator + d;
            Log.i("MainActivity", "getInnerSDCardPath: " + path);
            File file1 = new File(path);
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file1;
        }
        return null;
    }

    /*
   * 删除文件夹及文件
   * */
    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete(); // 删除所有文件
                Log.i("FileUtils", "deleteDirWihtFile: "+file.getName());
            } else if (file.isDirectory()) {
                deleteDirWihtFile(file); // 递规的方式删除文件夹
            }
        }
        dir.delete();// 删除目录本身
    }
}
