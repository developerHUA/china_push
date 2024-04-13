package com.huarenkeji.china_push.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.huarenkeji.china_push.PushManager;
import com.huarenkeji.china_push.util.Logger;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.common.ApiException;

import java.util.HashMap;
import java.util.Map;

public class HmsPushInterface implements PushInterface {
    private static final String BUNDLE_APP_ID = "HMS_APP_ID";
    private static final String MANUFACTURER = "HMS";

    @Override
    public void init(Context context, Bundle bundle) {
        String appId = bundle.getString(BUNDLE_APP_ID);
        if (TextUtils.isEmpty(appId)) {
            String errorMsg = "HMS 初始化失败，未找到appID或appSecret 请在build.gradle中配置HMS_APP_ID";
            Logger.e(errorMsg);
            PushManager.onRegisterFail(errorMsg);
            return;
        }
        new Thread(() -> {
            if (HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)
                    != com.huawei.hms.api.ConnectionResult.SUCCESS) {
                Logger.e("init Hms Push fail: 不支持的设备");
                PushManager.onRegisterFail("init Hms Push fail: No Support Device");
                return;
            }
            try {
                String regId = HmsInstanceId.getInstance(context).getToken(appId, "HCM");
                PushManager.onRegisterSuccess(regId, MANUFACTURER);
                Logger.i("init Hms Push Success token : " + regId);
            } catch (ApiException e) {
                PushManager.onRegisterFail("init Hms Push fail getToken: " + e.getMessage());
                Logger.e("init Hms Push fail getToken: " + e.getMessage());
            }
        }).start();

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
            Logger.e("HmsPushInterface processIntent fail bundle is Null");
        }

    }

    @Override
    public boolean supportPush(Context context, Bundle bundle) {
        String appId = bundle.getString(BUNDLE_APP_ID);
        return !TextUtils.isEmpty(appId) && HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(context)
                == com.huawei.hms.api.ConnectionResult.SUCCESS;
    }

    @Override
    public String getManufacturer() {
        return MANUFACTURER;
    }


}
