import 'package:china_push/china_push.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  Permission.notification.request();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  // {"type":"anyone","id":123}
  static List<String> logs = [];

  @override
  void initState() {
    super.initState();
    ChinaPush.enableLog(true);
    ChinaPush.initPush().then((value) {
      setState(() {
        logs.add("init success : $value");
      });
      print("init success : $value");
    }, onError: (e) {
      print("${e.error}");
      setState(() {
        logs.add("init fail : ${e.error}");
      });
    });

    // 建议在路由初始化后的首个界面设置回调
    ChinaPush.setOnClickNotification((dynamic value) {
      print("onClickNotification : value : $value");
      setState(() {
        logs.add("onClickNotification : value : $value");
      });
    });

    ChinaPush.getRegId().then((value) {
      print("regId:$value");
    });

    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await ChinaPush().getPlatformVersion() ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
          actions: [
            CupertinoButton(
                child: const Text("getReg"),
                onPressed: () {
                  ChinaPush.getRegId().then((value) {
                    setState(() {
                      logs.add("getRegId : value : $value");
                    });
                  });
                }),
            CupertinoButton(
                child: const Text("getMu"),
                onPressed: () {
                  ChinaPush.getManufacturer().then((value) {
                    setState(() {
                      logs.add("getManufacturer : value : $value");
                    });
                  });
                })
          ],
        ),
        body: Column(
          children: List.generate(logs.length, (index) {
            return Text(logs[index]);
          }),
        ),
      ),
    );
  }
}
