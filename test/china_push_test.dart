import 'package:flutter_test/flutter_test.dart';
import 'package:china_push/china_push.dart';
import 'package:china_push/china_push_platform_interface.dart';
import 'package:china_push/china_push_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockChinaPushPlatform
    with MockPlatformInterfaceMixin
    implements ChinaPushPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> getManufacturer() {
    // TODO: implement getManufacturer
    throw UnimplementedError();
  }

  @override
  Future<String?> getRegId() {
    // TODO: implement getRegId
    throw UnimplementedError();
  }

  @override
  Future initPush() {
    // TODO: implement initPush
    throw UnimplementedError();
  }

  @override
  void setNotificationClickListener(Function(dynamic p1) onNotificationClick) {
    // TODO: implement setNotificationClickListener
  }

  @override
  void enableLog(bool enable) {

  }
}

void main() {
  final ChinaPushPlatform initialPlatform = ChinaPushPlatform.instance;

  test('$MethodChannelChinaPush is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelChinaPush>());
  });

  test('getPlatformVersion', () async {
    ChinaPush chinaPushPlugin = ChinaPush();
    MockChinaPushPlatform fakePlatform = MockChinaPushPlatform();
    ChinaPushPlatform.instance = fakePlatform;

    expect(await chinaPushPlugin.getPlatformVersion(), '42');
  });
}
