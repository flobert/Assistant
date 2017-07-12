package com.colaapk.assistant;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
            Log.i("FileUtils", "saveData: "+getString(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
