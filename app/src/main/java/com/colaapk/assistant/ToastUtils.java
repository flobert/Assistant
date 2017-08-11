package com.colaapk.assistant;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by LLY on 2017/8/11.
 */

public class ToastUtils {
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
