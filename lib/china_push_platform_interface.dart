import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'china_push_method_channel.dart';

abstract class ChinaPushPlatform extends PlatformInterface {
  /// Constructs a ChinaPushPlatform.
  ChinaPushPlatform() : super(token: _token);

  static final Object _token = Object();

  static ChinaPushPlatform _instance = MethodChannelChinaPush();

  /// The default instance of [ChinaPushPlatform] to use.
  ///
  /// Defaults to [MethodChannelChinaPush].
  static ChinaPushPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ChinaPushPlatform] when
  /// they register themselves.
  static set instance(ChinaPushPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<dynamic> initPush() {
    throw UnimplementedError('initPush() has not been implemented.');
  }

  Future<String?> getRegId() {
    throw UnimplementedError('getRegId() has not been implemented.');
  }

  void setNotificationClickListener(Function(dynamic) onNotificationClick) {
    throw UnimplementedError(
        'addNotificationClickListener() has not been implemented.');
  }

  Future<String?> getManufacturer() {
    throw UnimplementedError(
        'addNotificationClickListener() has not been implemented.');
  }

  void enableLog(bool enable) {
    throw UnimplementedError(
        'addNotificationClickListener() has not been implemented.');
  }
}
