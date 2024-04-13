package com.huarenkeji.china_push.receiver;

import android.content.Context;

import com.huarenkeji.china_push.PushManager;
import com.huarenkeji.china_push.core.MiPushInterface;
import com.huarenkeji.china_push.util.Logger;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

public class MiPushMessageReceiver extends PushMessageReceiver {


    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Logger.i("onNotificationMessageClicked: title:" + message.getTitle() + " content : " + message.getDescription() +
                " data : " + message.getContent());
        PushManager.onOpenNotification(message.getContent());

    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Logger.i("onNotificationMessageArrived: title:" + message.getTitle() + " content : " + message.getDescription() +
                " data:" + message.getContent());
    }


    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {

        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        Logger.i("onCommandResult: message:" + message.getCommand() + " resultCode : " + message.getResultCode() + " argsOne : " + cmdArg1 + " argsTwo : " + cmdArg2);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                PushManager.onRegisterSuccess(cmdArg1, MiPushInterface.MANUFACTURER);
            } else {
                PushManager.onRegisterFail("Xiao Mi Init Push Fail resultCode : " + message.getResultCode() + " resultMessage : " + message.getReason());

            }
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        Logger.i("onReceiveRegisterResult: message:" + message.getCommand() + " argsOne : " + cmdArg1 + " argsTwo : " + cmdArg2);

    }
}