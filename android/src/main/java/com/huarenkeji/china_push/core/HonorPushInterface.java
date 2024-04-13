package com.huarenkeji.china_push.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hihonor.push.sdk.HonorPushClient;
import com.huarenkeji.china_push.PushManager;
import com.huarenkeji.china_push.util.Logger;

import java.util.HashMap;
import java.util.Map;

public class HonorPushInterface implements PushInterface {

    public static final String MANUFACTURER = "HONOR";
    public static final String BUNDLE_APP_ID = "HMS_APP_ID";

    @Override
    public void init(Context context, Bundle bundle) {
        boolean isSupport = HonorPushClient.getInstance().checkSupportHonorPush(context);
        if (isSupport) {
            HonorPushClient.getInstance().init(context, true);
            Logger.i("init Honor Push Called.");
        } else {
            String errorMsg = "init Honor Push fail not support";
            Logger.e(errorMsg);
            PushManager.onRegisterFail(errorMsg);
        }
    }

    @Override
    public void processIntent(@NonNull Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Map<String, Object> pushContetMap = new HashMap<>();
            for (String key : bundle.keySet()) {
                String content = bundle.getString(key);
                pushContetMap.put(key, content);
                Logger.i("receive data from push, key = " + key + ", content = " + content);
            }
            PushManager.onOpenNotification(pushContetMap);
        } else {
            Logger.e("HonorPushInterface processIntent fail bundle is Null");
        }
    }

    @Override
    public boolean supportPush(Context context, Bundle bundle) {
        String appId = bundle.getString(BUNDLE_APP_ID);
        return !TextUtils.isEmpty(appId) && HonorPushClient.getInstance().checkSupportHonorPush(context);
    }

    @Override
    public String getManufacturer() {
        return MANUFACTURER;
    }


}
