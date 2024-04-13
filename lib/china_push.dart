
import 'china_push_platform_interface.dart';

class ChinaPush {
  Future<String?> getPlatformVersion() {
    return ChinaPushPlatform.instance.getPlatformVersion();
  }

  static Future<dynamic> initPush() async {
    return await ChinaPushPlatform.instance.initPush();
  }

  static Future<String?> getRegId() async {
    return await ChinaPushPlatform.instance.getRegId();
  }

  static Future<String?> getManufacturer() async {
    return await ChinaPushPlatform.instance.getManufacturer();
  }

  static void enableLog(bool enable)  {
      ChinaPushPlatform.instance.enableLog(enable);
  }

  static void setOnClickNotification(Function(dynamic) onClickNotification) {
    ChinaPushPlatform.instance
        .setNotificationClickListener(onClickNotification);
  }

}
