package com.huarenkeji.china_push.core;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.huarenkeji.china_push.PushManager;
import com.huarenkeji.china_push.util.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;

import java.io.Serializable;
import java.util.List;


public class MiPushInterface implements PushInterface {

    private static final String BUNDLE_APP_ID = "MI_APP_ID";
    private static final String BUNDLE_APP_KEY = "MI_APP_KEY";

    public static final String MANUFACTURER = "MI";
    @Override
    public void init(Context context, Bundle bundle) {
        if (!shouldInit(context)) {
            Logger.i("Xiao Mi initPush return no mainProcess.");
            return;
        }
        String appKey = bundle.getString(BUNDLE_APP_KEY);
        String appId = bundle.getString(BUNDLE_APP_ID);
        if (TextUtils.isEmpty(appKey)) {
            String errorMsg = "Xiao Mi 推送初始化失败，未找到appKey 请在build.gradle中配置MI_APP_KEY";
            Logger.e(errorMsg);
            PushManager.onRegisterFail(errorMsg);
            return;
        }
        if (TextUtils.isEmpty(appId)) {
            String errorMsg = "Xiao Mi推送 初始化失败，未找到appId 请在build.gradle中配置MI_APP_ID";
            Logger.e(errorMsg);
            PushManager.onRegisterFail(errorMsg);
            return;
        }

        MiPushClient.registerPush(context, appId, appKey);
        Logger.i("Xiao Mi initPush is Called.");
    }

    @Override
    public void processIntent(@NonNull Intent intent) {
        //服务端调用Message.Builder类的extra(String key, String value)方法，
        // 将key设置为Constants.EXTRA_PARAM_NOTIFY_EFFECT，value设置为Constants.NOTIFY_LAUNCHER_ACTIVITY。
        // 具体请参见《服务端Java SDK文档》。
        // 封装消息的MiPushMessage对象通过Intent传到客户端，
        // 客户端在相应的Activity中可以调用Intent的getSerializableExtra（PushMessageHelper.KEY_MESSAGE）方法得到MiPushMessage对象。
        Serializable serializable = intent.getSerializableExtra(PushMessageHelper.KEY_MESSAGE);
        if (serializable instanceof MiPushMessage) {
            String extraData = ((MiPushMessage) (serializable)).getContent();
            Logger.i("MiPushInterface Open Notification data :" + extraData);
            PushManager.onOpenNotification(extraData);
        }

    }

    @Override
    public boolean supportPush(Context context, Bundle bundle) {
        // 小米为默认推送
        return true;
    }

    @Override
    public String getManufacturer() {
        return MANUFACTURER;
    }


    private boolean shouldInit(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getApplicationInfo().processName;
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

}
