import Foundation

enum MessengerAppConstants {
    static func isSecureSheetURL(_ url: URL) -> Bool {
        guard let scheme = url.scheme, scheme.lowercased() == "https" else { return false }
        return url.host != nil
    }
}
