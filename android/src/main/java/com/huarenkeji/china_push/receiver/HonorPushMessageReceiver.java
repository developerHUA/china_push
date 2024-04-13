package com.huarenkeji.china_push.receiver;

import com.hihonor.push.sdk.HonorMessageService;
import com.hihonor.push.sdk.HonorPushDataMsg;
import com.huarenkeji.china_push.PushManager;
import com.huarenkeji.china_push.core.HonorPushInterface;
import com.huarenkeji.china_push.util.Logger;

public class HonorPushMessageReceiver extends HonorMessageService {
    //Token发生变化时，会以onNewToken方法返回
    @Override
    public void onNewToken(String pushToken) {
        Logger.i("onNewToken pushToken:" + pushToken);
        PushManager.onRegisterSuccess(pushToken, HonorPushInterface.MANUFACTURER);
    }

    @Override
    public void onMessageReceived(HonorPushDataMsg msg) {
        // TODO: 处理收到的透传消息。
        Logger.i("onMessageReceived msg:" + msg.getData());
    }



}
