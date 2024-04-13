import Foundation

class FlutterApnsSerialization {
    static func remoteMessageUserInfoToDict(_ userInfo: [AnyHashable: Any]) -> [String: Any] {
        var message: [String: Any] = [:]
        var data: [String: Any] = [:]
        var notification: [String: Any] = [:]
        var notificationIOS: [String: Any] = [:]

        // message.data
        for (key, value) in userInfo {
            if let keyString = key as? String {
                // message.messageId
                if keyString == "gcm.message_id" || keyString == "google.message_id" || keyString == "message_id" {
                    message["messageId"] = value
                    continue
                }

                // message.messageType
                if keyString == "message_type" {
                    message["messageType"] = value
                    continue
                }

                // message.collapseKey
                if keyString == "collapse_key" {
                    message["collapseKey"] = value
                    continue
                }

                // message.from
                if keyString == "from" {
                    message["from"] = value
                    continue
                }

                // message.sentTime
                if keyString == "google.c.a.ts" {
                    message["sentTime"] = value
                    continue
                }

                // message.to
                if keyString == "to" || keyString == "google.to" {
                    message["to"] = value
                    continue
                }

                // build data dict from remaining keys but skip keys that shouldn't be included in data
                if keyString == "aps" || keyString.hasPrefix("gcm.") || keyString.hasPrefix("google.") {
                    continue
                }

                // message.apple.imageUrl
                if keyString == "fcm_options", let options = value as? [String: Any], let image = options["image"] as? String {
                    notificationIOS["imageUrl"] = image
                    continue
                }

                data[keyString] = value
            }
        }
        message["data"] = data

        if let apsDict = userInfo["aps"] as? [String: Any] {
            // message.category
            if let category = apsDict["category"] as? String {
                message["category"] = category
            }

            // message.threadId
            if let threadId = apsDict["thread-id"] as? String {
                message["threadId"] = threadId
            }

            // message.contentAvailable
            if let contentAvailable = apsDict["content-available"] as? Int {
                message["contentAvailable"] = contentAvailable != 0
            }

            // message.mutableContent
            if let mutableContent = apsDict["mutable-content"] as? Int {
                message["mutableContent"] = mutableContent != 0
            }

            // message.notification.*
            if let alert = apsDict["alert"] {
                if let alertDict = alert as? [String: Any] {
                    // message.notification.title
                    if let title = alertDict["title"] as? String {
                        notification["title"] = title
                    }

                    // message.notification.titleLocKey
                    if let titleLocKey = alertDict["title-loc-key"] as? String {
                        notification["titleLocKey"] = titleLocKey
                    }

                    // message.notification.titleLocArgs
                    if let titleLocArgs = alertDict["title-loc-args"] as? [String] {
                        notification["titleLocArgs"] = titleLocArgs
                    }

                    // message.notification.body
                    if let body = alertDict["body"] as? String {
                        notification["body"] = body
                    }

                    // message.notification.bodyLocKey
                    if let bodyLocKey = alertDict["loc-key"] as? String {
                        notification["bodyLocKey"] = bodyLocKey
                    }

                    // message.notification.bodyLocArgs
                    if let bodyLocArgs = alertDict["loc-args"] as? [String] {
                        notification["bodyLocArgs"] = bodyLocArgs
                    }

                    // Apple only
                    // message.notification.apple.subtitle
                    if let subtitle = alertDict["subtitle"] as? String {
                        notificationIOS["subtitle"] = subtitle
                    }

                    // Apple only
                    // message.notification.apple.subtitleLocKey
                    if let subtitleLocKey = alertDict["subtitle-loc-key"] as? String {
                        notificationIOS["subtitleLocKey"] = subtitleLocKey
                    }

                    // Apple only
                    // message.notification.apple.subtitleLocArgs
                    if let subtitleLocArgs = alertDict["subtitle-loc-args"] as? [String] {
                        notificationIOS["subtitleLocArgs"] = subtitleLocArgs
                    }

                    // Apple only
                    // message.notification.apple.badge
                    if let badge = alertDict["badge"] {
                        notificationIOS["badge"] = badge
                    }
                }
                notification["apple"] = notificationIOS
                message["notification"] = notification
            }

            // message.notification.apple.sound
            if let sound = apsDict["sound"] {
                if let soundString = sound as? String {
                    notification["sound"] = [
                        "name": soundString,
                        "critical": false,
                        "volume": 1
                    ]
                } else if let soundDict = sound as? [String: Any] {
                    var notificationIOSSound: [String: Any] = [:]
                    // message.notification.apple.sound.name String
                    if let name = soundDict["name"] as? String {
                        notificationIOSSound["name"] = name
                    }

                    // message.notification.apple.sound.critical Boolean
                    if let critical = soundDict["critical"] as? Bool {
                        notificationIOSSound["critical"] = critical
                    }

                    // message.notification.apple.sound.volume Number
                    if let volume = soundDict["volume"] as? Double {
                        notificationIOSSound["volume"] = volume
                    }

                    notificationIOS["sound"] = notificationIOSSound
                }
                notification["apple"] = notificationIOS
                message["notification"] = notification
            }
        }

        return message
    }
}
