package com.huarenkeji.china_push;

import android.content.Context;

import androidx.annotation.NonNull;

import com.huarenkeji.china_push.callback.OnOpenNotification;
import com.huarenkeji.china_push.callback.OnRegisterCallback;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;

public class ChinaPushHandler implements MethodChannel.MethodCallHandler, OnRegisterCallback, OnOpenNotification {

    private final Context context;
    private final MethodChannel channel;

    private MethodChannel.Result initPushResult;

    public ChinaPushHandler(Context context, MethodChannel channel) {
        this.context = context;
        this.channel = channel;
    }


    public static ChinaPushHandler init(Context context, BinaryMessenger messenger, String channel) {
        MethodChannel methodChannel = new MethodChannel(messenger, channel);
        ChinaPushHandler pushHandler = new ChinaPushHandler(context, methodChannel);
        methodChannel.setMethodCallHandler(pushHandler);
        PushManager.addRegisterListener(pushHandler);
        PushManager.addOpenNotificationListener(pushHandler);
        return pushHandler;
    }


    private void initPush(MethodChannel.Result result) {
        if (context == null) {
            result.error("3004", "Context is Null", StandardMessageCodec.INSTANCE);
            return;
        }
        this.initPushResult = result;
        PushManager.init(context);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "getPlatformVersion":
                result.success("Android " + android.os.Build.VERSION.RELEASE);
                break;
            case "initPush":
                initPush(result);
                break;
            case "getRegId":
                result.success(PushManager.getRegId());
                break;
            case "getManufacturer":
                result.success(PushManager.getManufacturer());
                break;
            case "enableLog":
                PushManager.enableLog(Boolean.TRUE.equals(call.argument("enable")));
                break;
            default:
                result.notImplemented();
                break;
        }

    }


    public void dispose() {
        channel.setMethodCallHandler(null);
        PushManager.removeRegisterListener(this);
        PushManager.removeNotificationListener(this);
    }


    @Override
    public void onRegisterSuccess(String token, String manufacturer) {
        if (initPushResult != null) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("regId", token);
            resultMap.put("manufacturer", manufacturer);
            initPushResult.success(resultMap);


        }
        initPushResult = null;
    }

    @Override
    public void onRegisterFail(String errorMsg) {
        if (initPushResult != null) {
            initPushResult.error("10086", errorMsg, null);
        }
        initPushResult = null;
    }

    @Override
    public void onOpenNotification(Object data) {
        channel.invokeMethod("onNotificationClick", data);
    }
}
