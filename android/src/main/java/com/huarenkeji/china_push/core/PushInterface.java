package com.huarenkeji.china_push.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

public interface PushInterface {

    void init(Context context, Bundle bundle);

    void processIntent(@NonNull Intent intent);

    boolean supportPush(Context context, Bundle bundle);

    /**
     * 获取厂商
     *
     */
    String getManufacturer();


}
