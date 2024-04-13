package com.huarenkeji.china_push.util;


import android.util.Log;

import com.huarenkeji.china_push.PushManager;


public class Logger {

    private static final String TAG = "PushModule";

    public static void d(String message) {
        d(TAG, message);
    }

    public static void d(String tag, String message) {

        if (PushManager.isDebug()) {
            Log.d(tag, message);
        }
    }

    public static void i(String message) {
        i(TAG, message);
    }

    public static void i(String tag, String message) {
        if (PushManager.isDebug()) {
            Log.i(tag, message);
        }
    }

    public static void w(String message) {
        w(TAG, message);
    }

    public static void w(String tag, String message) {
        if (PushManager.isDebug()) {
            Log.w(tag, message);
        }
    }

    public static void e(String message) {
        e(TAG, message);
    }

    public static void e(String tag, String message) {
        if (PushManager.isDebug()) {
            Log.e(tag, message);
        }
    }

    public static void e(String message, Throwable throwable) {
        e(TAG, message, throwable);
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (PushManager.isDebug()) {
            Log.e(tag, message, throwable);
        }
    }

}
