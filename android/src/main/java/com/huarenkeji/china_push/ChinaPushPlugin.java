package com.huarenkeji.china_push;

import android.content.Intent;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/** ChinaPushPlugin */
public class ChinaPushPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware , PluginRegistry.NewIntentListener{
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private ChinaPushHandler chinaPushHandler;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    chinaPushHandler = ChinaPushHandler.init(flutterPluginBinding.getApplicationContext(),
            flutterPluginBinding.getBinaryMessenger(), "china_push");
  }



  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    chinaPushHandler.dispose();
  }

  public ChinaPushHandler getPushHandler() {
    return chinaPushHandler;
  }


  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    Intent intent = binding.getActivity().getIntent();
    binding.addOnNewIntentListener(this);
    PushManager.processIntent(intent);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
  }

  @Override
  public void onDetachedFromActivity() {
    chinaPushHandler.dispose();
  }

  @Override
  public boolean onNewIntent(@NonNull Intent intent) {
    PushManager.processIntent(intent);
    return true;
  }


  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }


}
