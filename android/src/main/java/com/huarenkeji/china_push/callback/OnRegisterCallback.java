package com.huarenkeji.china_push.callback;

public interface OnRegisterCallback {

    void onRegisterSuccess(String token,String manufacturer);
    void onRegisterFail(String errorMsg);


}
