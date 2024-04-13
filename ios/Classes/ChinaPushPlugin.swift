import Flutter
import UIKit

import UserNotifications

public class ChinaPushPlugin: NSObject,  FlutterPlugin,UNUserNotificationCenterDelegate {
   var channel: FlutterMethodChannel?
      var result:FlutterResult?
      var resumingFromBackground = false
      var regId:String?

      let manufacturer = "APPLE"

      init(channel: FlutterMethodChannel) {
          self.channel = channel
          super.init()
      }

      public static func register(with registrar: FlutterPluginRegistrar) {
          let channel = FlutterMethodChannel(name: "china_push", binaryMessenger: registrar.messenger())
          let instance = ChinaPushPlugin(channel: channel)
          registrar.addApplicationDelegate(instance)
          registrar.addMethodCallDelegate(instance, channel: channel)
      }

      public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
          switch call.method {
          case "getPlatformVersion":
              result("iOS " + UIDevice.current.systemVersion)
          case "initPush":
              initPush(result:result)
          case "getRegId":
              result(regId)
          case "getManufacturer":
              result(manufacturer)
          default:
              result(FlutterMethodNotImplemented)
          }
      }

      private func initPush(result: @escaping FlutterResult) {
          self.result = result;

          guard UNUserNotificationCenter.current().delegate != nil else {
              print("Init Fail delegate is nil")
              result(FlutterError(code: "10086", message: "Init Fail delegate is nil", details: nil))
              self.result = nil;
              return
          }

          // 注册远程通知
          UIApplication.shared.registerForRemoteNotifications()

      }

      public func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [AnyHashable : Any] = [:]) -> Bool {

          UNUserNotificationCenter.current().delegate = self
          return true
      }

      public func applicationDidEnterBackground(_ application: UIApplication) {
          resumingFromBackground = true
      }

      public func applicationDidBecomeActive(_ application: UIApplication) {
          resumingFromBackground = false
          UIApplication.shared.applicationIconBadgeNumber = -1;
      }

      public func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {

          let deviceTokenString = deviceToken.map { String(format: "%02.2hhx", $0) }.joined()
          self.regId = deviceTokenString
          result?(["regId":regId,"manufacturer":"APPLE"])
      }


      public func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) -> Bool {
          let userInfo = FlutterApnsSerialization.remoteMessageUserInfoToDict(userInfo)

          onMessage(userInfo: userInfo)
          completionHandler(.noData)
          return true
      }

      public func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
          let userInfo = notification.request.content.userInfo

          guard userInfo["aps"] != nil else {
              return
          }

          _ = FlutterApnsSerialization.remoteMessageUserInfoToDict(userInfo)
          completionHandler([.alert, .sound])
      }

      public func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
          var userInfo = response.notification.request.content.userInfo
          guard userInfo["aps"] != nil else {
              return
          }

          userInfo["actionIdentifier"] = response.actionIdentifier
          _ = FlutterApnsSerialization.remoteMessageUserInfoToDict(userInfo)

          onMessage(userInfo: userInfo)
          completionHandler()
      }


      private func onMessage(userInfo: [AnyHashable: Any]) {
          channel?.invokeMethod("onNotificationClick", arguments: ((userInfo["aps"] as? [String: Any])?["attributes"] as? [String: Any])?["data"] as? String ?? nil)
      }

  }
  extension UNNotificationCategoryOptions {
      static let stringToValue: [String: UNNotificationCategoryOptions] = {
          var r: [String: UNNotificationCategoryOptions] = [:]
          r["UNNotificationCategoryOptions.customDismissAction"] = .customDismissAction
          r["UNNotificationCategoryOptions.allowInCarPlay"] = .allowInCarPlay
          if #available(iOS 11.0, *) {
              r["UNNotificationCategoryOptions.hiddenPreviewsShowTitle"] = .hiddenPreviewsShowTitle
          }
          if #available(iOS 11.0, *) {
              r["UNNotificationCategoryOptions.hiddenPreviewsShowSubtitle"] = .hiddenPreviewsShowSubtitle
          }
          if #available(iOS 13.0, *) {
              r["UNNotificationCategoryOptions.allowAnnouncement"] = .allowAnnouncement
          }
          return r
      }()
}
