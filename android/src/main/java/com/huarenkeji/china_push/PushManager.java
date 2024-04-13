package com.huarenkeji.china_push;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.huarenkeji.china_push.callback.OnOpenNotification;
import com.huarenkeji.china_push.callback.OnRegisterCallback;
import com.huarenkeji.china_push.core.HmsPushInterface;
import com.huarenkeji.china_push.core.HonorPushInterface;
import com.huarenkeji.china_push.core.MiPushInterface;
import com.huarenkeji.china_push.core.OppoPushInterface;
import com.huarenkeji.china_push.core.PushInterface;
import com.huarenkeji.china_push.core.VivoPushInterface;
import com.huarenkeji.china_push.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class PushManager {
    private static final PushManager instance = new PushManager();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private PushInterface pushInterface;

    private String regId;

    private boolean debug = true;

    private final List<OnRegisterCallback> registerCallbackList;
    private final List<OnOpenNotification> openNotificationList;

    private PushManager() {
        registerCallbackList = new ArrayList<>();
        openNotificationList = new ArrayList<>();
        initPushInterface();
    }


    private void initPushInterface() {
        String manufacturer = getPhoneManufacturer();
        switch (manufacturer.toLowerCase()) {
            case "huawei":
                pushInterface = new HmsPushInterface();
                break;
            case "oppo":
            case "oneplus":
                pushInterface = new OppoPushInterface();
                break;
            case "vivo":
                pushInterface = new VivoPushInterface();
                break;
            case "honor":
                pushInterface = new HonorPushInterface();
                break;
            default:
                pushInterface = new MiPushInterface();
                break;
        }
    }


    public static PushManager getInstance() {
        return instance;
    }


    public static void init(Context context) {
        Bundle bundle = getInstance().getMetaDataBundle(context.getApplicationContext());
        if (bundle == null) {
            Logger.i("初始化推送失败 bundle is null 请检查初始化时机");
            return;
        }
        if (!getInstance().pushInterface.supportPush(context,bundle)) {
            Logger.i(getInstance().pushInterface.getClass().getName() + " 不支持推送，改为小米推送");
            getInstance().pushInterface = new MiPushInterface();
        }

        Logger.i("开始初始化 className：" + getInstance().pushInterface.getClass().getName());
        getInstance().pushInterface.init(context.getApplicationContext(), bundle);
    }

    public static void enableLog(boolean debug) {
        getInstance().debug = debug;
    }

    public static boolean isDebug() {
        return getInstance().debug;
    }

    private Bundle getMetaDataBundle(Context context) {
        // 获取 ApplicationInfo 对象
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            // 从 ApplicationInfo 中获取 meta-data
            return appInfo.metaData;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logger.e("getMetaDataBundle fail : " + e.getMessage());
        }
        return null;
    }

    public static void processIntent(Intent intent) {
        if (getInstance().pushInterface == null) {
            Logger.e("processIntent fail manager not init");
            return;
        }
        getInstance().pushInterface.processIntent(intent);
    }


    public static void onRegisterSuccess(String token, String manufacturer) {
        getInstance().regId = token;
        Logger.i("PushManager onRegisterSuccess PushInterface:" + getInstance().pushInterface.getClass().getName() + " token :" + token);
        if (TextUtils.isEmpty(token)) {
            return;
        }
        getInstance().mHandler.post(() -> {
            for (int i = getInstance().registerCallbackList.size() - 1; i >= 0; i--) {
                getInstance().registerCallbackList.get(i).onRegisterSuccess(token, manufacturer);
            }
        });
    }

    public static void onRegisterFail(String errorMsg) {
        Logger.e("PushManager onRegisterFail PushInterface:" + getInstance().pushInterface.getClass().getName() + " errorMsg :" + errorMsg);
        getInstance().mHandler.post(() -> {
            for (int i = getInstance().registerCallbackList.size() - 1; i >= 0; i--) {
                getInstance().registerCallbackList.get(i).onRegisterFail(errorMsg);
            }
        });
    }


    public static void addRegisterListener(OnRegisterCallback listener) {
        getInstance().registerCallbackList.add(listener);
    }

    public static void removeRegisterListener(OnRegisterCallback listener) {
        getInstance().registerCallbackList.remove(listener);
    }

    public static void onOpenNotification(Object data) {
        Logger.i("PushManager onOpenNotification PushInterface:" + getInstance().pushInterface.getClass().getName() + " data :" + data);
        for (int i = getInstance().registerCallbackList.size() - 1; i >= 0; i--) {
            getInstance().openNotificationList.get(i).onOpenNotification(data);
        }
    }

    public static void addOpenNotificationListener(OnOpenNotification listener) {
        getInstance().openNotificationList.add(listener);
    }

    public static void removeNotificationListener(OnOpenNotification listener) {
        getInstance().openNotificationList.remove(listener);
    }


    public static String getRegId() {
        return getInstance().regId;
    }

    public static String getManufacturer() {
        return getInstance().pushInterface.getManufacturer();
    }

    public static String getPhoneManufacturer() {
        String manufacturer = "unknown";
        try {
            manufacturer = Build.MANUFACTURER;
        } catch (Exception e) {
            Logger.e("获取手机厂商失败：" + e.getMessage());
        }
        return manufacturer;
    }

}
