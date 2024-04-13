import Foundation

class Logger {
    static var isEnabled: Bool = false
    static let TAG:String = "PushModule";
    
    static func log(_ message: String) {
        if isEnabled {
            print("\(TAG): \(message)")
        }
    }
}

