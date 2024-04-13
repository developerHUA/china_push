package com.huarenkeji.china_push.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.huarenkeji.china_push.PushManager;
import com.huarenkeji.china_push.util.Logger;
import com.vivo.push.PushClient;
import com.vivo.push.PushConfig;
import com.vivo.push.listener.IPushQueryActionListener;
import com.vivo.push.util.VivoPushException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VivoPushInterface implements PushInterface, IPushQueryActionListener {

    private static final String MANUFACTURER = "VIVO";
    private static final String BUNDLE_APP_ID = "api_key";
    private static final String BUNDLE_APP_KEY = "app_id";

    @Override
    public void init(Context context, Bundle bundle) {
        try {
            PushClient.getInstance(context).initialize(new PushConfig.Builder()
                    .agreePrivacyStatement(true)
                    .build());
            PushClient.getInstance(context).turnOnPush(state -> {
                if (Objects.equals(0, state)) {
                    Logger.i("Open VIVO Push Success stats : " + state);
                    PushClient.getInstance(context).getRegId(this);
                } else {
                    Logger.e("Open VIVO Push Fail stats : " + state);
                    PushManager.onRegisterFail("ViVo push register fail Open VIVO Push Fail stats :" + state);
                }
            });
        } catch (VivoPushException e) {
            Logger.e("Init VIVO Push fail message : " + e.getMessage());
            PushManager.onRegisterFail("ViVo push register fail Init VIVO Push fail message : " + e.getMessage());
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
            if (pushContetMap.containsKey("intent_userId") ||
                    pushContetMap.containsKey("vivo_push_messageId") ||
                    pushContetMap.containsKey("data")) {
                PushManager.onOpenNotification(pushContetMap.get("data"));
            }
        } else {
            Logger.e("VivoPushInterface processIntent fail bundle is Null");
        }
    }

    @Override
    public boolean supportPush(Context context, Bundle bundle) {
        String appKey = bundle.getString(BUNDLE_APP_KEY);
        String appId = bundle.getString(BUNDLE_APP_ID);
        return !TextUtils.isEmpty(appKey) || !TextUtils.isEmpty(appId) && PushClient.getInstance(context).isSupport();
    }

    @Override
    public String getManufacturer() {
        return MANUFACTURER;
    }

    @Override
    public void onSuccess(String s) {
        if (TextUtils.isEmpty(s)) {
            PushManager.onRegisterFail("ViVo push register fail regid is empty");
        } else {
            PushManager.onRegisterSuccess(s, MANUFACTURER);
        }
        Logger.i("VivoPushInterface register Success onSuccess -> token : " + s);
    }

    @Override
    public void onFail(Integer errerCode) {
        Logger.e("VivoPushInterface register Fail onFail -> errerCode : " + errerCode);
        PushManager.onRegisterFail("ViVo Push  register Fail errerCode :" + errerCode);
    }
}
