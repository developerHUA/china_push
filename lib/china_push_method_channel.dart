import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'china_push_platform_interface.dart';
import 'exception/register_exception.dart';

/// An implementation of [ChinaPushPlatform] that uses method channels.
class MethodChannelChinaPush extends ChinaPushPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('china_push');

  MethodChannelChinaPush() {
    methodChannel.setMethodCallHandler(flutterMethod);
  }

  Function(dynamic)? onNotificationClick;

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<dynamic> initPush() async {
    try {
      return await methodChannel.invokeMethod('initPush');
    } on PlatformException catch (error) {
      throw RegisterException(
        error: "${error.message}",
        errorCode: "10086",
      );
    }
  }

  @override
  Future<String?> getRegId() async {
    return await methodChannel.invokeMethod('getRegId');
  }

  @override
  void enableLog(bool enable) {
    methodChannel.invokeMethod('enableLog',enable);
  }

  Future<dynamic> flutterMethod(MethodCall methodCall) async {
    switch (methodCall.method) {
      case 'onNotificationClick':
        notifyClickListener(methodCall.arguments);
        break;
      default:
        break;
    }
  }

  void notifyClickListener(dynamic data) {
    print("onNotificationClick:args : $data");
    if (data != null && data is String && data.isNotEmpty) {
      data = jsonDecode(data);
    }

    onNotificationClick?.call(data);
  }

  @override
  void setNotificationClickListener(Function(dynamic) onNotificationClick) {
    this.onNotificationClick = onNotificationClick;
  }

  @override
  Future<String?> getManufacturer() async {
    return await methodChannel.invokeMethod('getManufacturer');
  }
}
