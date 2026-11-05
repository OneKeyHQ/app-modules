// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
import CoreFoundation

@objc(_TtC18IntercomSDKPrivate9JWTExpiry)
final class JWTExpiry: NSObject {
    @objc static func isDefinitivelyExpired(_ value: Any?) -> Bool {
        let now = Date()
        guard let expiry = expiry(of: value) else { return false }
        return expiry.addingTimeInterval(300) < now
    }

    static func expiry(of value: Any?) -> Date? {
        guard let jwt = value as? String else { return nil }
        let components = jwt.components(separatedBy: ".")
        guard components.count == 3 else { return nil }
        var payload = components[1].replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")
        let remainder = payload.count % 4
        if remainder > 0 { payload += String(repeating: "=", count: 4 - remainder) }
        guard let data = Data(base64Encoded: payload),
              let object = try? JSONSerialization.jsonObject(with: data),
              let claims = object as? [String: Any],
              let expiration = claims["exp"] as? NSNumber,
              CFGetTypeID(expiration) != CFBooleanGetTypeID() else { return nil }
        return Date(timeIntervalSince1970: expiration.doubleValue)
    }
}
