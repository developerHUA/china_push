package com.huarenkeji.china_push.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.huarenkeji.china_push.PushManager;
import com.huarenkeji.china_push.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class OppoPushInterface implements PushInterface, ICallBackResultService {

    private static final String BUNDLE_APP_KEY = "OPPO_APP_KEY";

    private static final String BUNDLE_APP_SECRET = "OPPO_APP_SECRET";
    private static final String MANUFACTURER = "OPPO";

    @Override
    public void init(Context context, Bundle bundle) {
        HeytapPushManager.requestNotificationPermission();
        String appKey = bundle.getString(BUNDLE_APP_KEY);
        String appSecret = bundle.getString(BUNDLE_APP_SECRET);
        if (TextUtils.isEmpty(appKey) || TextUtils.isEmpty(appSecret)) {
            String errorMsg = "OPPO 初始化失败，未找到appKey或appSecret 请在build.gradle中配置OPPO_APP_KEY和OPPO_APP_SECRET";
            Logger.e(errorMsg);
            PushManager.onRegisterFail(errorMsg);
            return;
        }
        HeytapPushManager.init(context, PushManager.isDebug());
        HeytapPushManager.register(context.getApplicationContext(), bundle.getString(BUNDLE_APP_KEY),
                bundle.getString(BUNDLE_APP_SECRET), this);
    }

    @Override
    public void processIntent(@NonNull Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Map<String, Object> pushContetMap = new HashMap<>();
            for (String key : bundle.keySet()) {
                String content = bundle.getString(key);
                pushContetMap.put(key, content);
                Logger.i("OppoPushInterface processIntent data from push, key = " + key + ", content = " + content);
            }
            PushManager.onOpenNotification(pushContetMap);
        } else {
            Logger.e("OppoPushInterface processIntent fail bundle is Null");
        }
    }

    @Override
    public boolean supportPush(Context context, Bundle bundle) {
        String appKey = bundle.getString(BUNDLE_APP_KEY);
        String appSecret = bundle.getString(BUNDLE_APP_SECRET);
        return !TextUtils.isEmpty(appKey) || !TextUtils.isEmpty(appSecret) && HeytapPushManager.isSupportPush(context);
    }

    @Override
    public String getManufacturer() {
        return MANUFACTURER;
    }

    @Override
    public void onRegister(int responseCode, String registerID, String packageName, String miniPackageName) {
        if (responseCode == 0 && !TextUtils.isEmpty(registerID)) {
            PushManager.onRegisterSuccess(registerID, MANUFACTURER);
        } else {
            PushManager.onRegisterFail("Oppo Push RegisterFail responseCode = -2");
        }
        Logger.i("onRegister responseCode:" + responseCode + " registerID :" + registerID + " packageName :" + packageName + " miniPackageName :" + miniPackageName);
    }

    @Override
    public void onUnRegister(int responseCode, String packageName, String miniProgramPkg) {
        Logger.i("onUnRegister responseCode:" + responseCode + " packageName :" + packageName + " miniProgramPkg :" + miniProgramPkg);
    }

    @Override
    public void onSetPushTime(int errorCode, String message) {
        Logger.i("onSetPushTime errorCode:" + errorCode + " message :" + message);
    }

    @Override
    public void onGetPushStatus(int responseCode, int status) {
        Logger.i("onSetPushTime responseCode:" + responseCode + " status :" + status);
    }

    @Override
    public void onGetNotificationStatus(int responseCode, int status) {
        Logger.i("onGetNotificationStatus errorCode:" + responseCode + " status :" + status);
    }

    @Override
    public void onError(int errorCode, String message, String packageName, String miniProgramPkg) {
        Logger.i("onError errorCode:" + errorCode + " message :" + message + " packageName : " + packageName + " miniProgramPkg : " + miniProgramPkg);
    }
}
