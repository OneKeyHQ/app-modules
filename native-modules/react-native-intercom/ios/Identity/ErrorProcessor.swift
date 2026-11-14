import Foundation

@objc(_TtC18IntercomSDKPrivate14ErrorProcessor)
final class ErrorProcessor: NSObject {
    @objc static let serverErrorCodeKey = "kIntercomErrorCode"
    @objc static let userJwtExpiredCode = "intercom_user_jwt_expired"

    private static func error(code: Int, action: String, detail: String) -> NSError {
        NSError(domain: "IntercomSDKErrorDomain", code: code,
                userInfo: [NSLocalizedDescriptionKey: ["ERROR", action, detail].joined(separator: " - ")])
    }

    @objc static func generateHTTPError(code: Int, message: String) -> NSError {
        error(code: code, action: "[HTTP \(code)]", detail: message)
    }
    @objc static func generateInvalidUserIdOrEmailError() -> NSError {
        error(code: 2001, action: "Failed to register user", detail: "the 'userId' or 'email' property of 'userAttributes' must be populated as a string")
    }
    @objc static func generateIdentifiedUserExists() -> NSError {
        error(code: 3002, action: "Failed to register user", detail: "We already have a registered user. If you are attempting to register a new user, call logout() before this. If you are attempting to register an identified user call: loginUserWithUserAttributes")
    }
    @objc static func generateInvalidUpdateObjectError() -> NSError {
        error(code: 3001, action: "Failed to update user", detail: "the update object must be not nil and of type `ICMUserAttributes`")
    }
    @objc static func generatePushNotificationsNotEnabledError() -> NSError {
        error(code: 4001, action: "Failed to register a device token", detail: "push notifications are not enabled on the host device")
    }
    @objc static func generatePushNotificationNilDeviceTokenError() -> NSError {
        error(code: 4002, action: "Failed to register a device token", detail: "device token is nil")
    }
    @objc static func generateConfigNotReceivedError() -> NSError {
        error(code: 4003, action: "Failed to register a device token", detail: "the Intercom configuration has not loaded - call setDeviceToken again to retry")
    }
    @objc static func generateInvalidIdentityVerificationError() -> NSError {
        error(code: 4003, action: "Failed to register a device token", detail: "identity verification is not setup correctly")
    }
    @objc static func generateExpiredIdentityVerificationTokenError() -> NSError {
        error(code: 4003, action: "Failed to register a device token", detail: "the identity verification token has expired - call setUserJwt: with a current token")
    }
    @objc static func generateInvalidDeviceTokenError() -> NSError {
        error(code: 4004, action: "Failed to register a device token", detail: "device token is invalid")
    }
    @objc static func generateInvalidAuthTokenError() -> NSError {
        error(code: 2001, action: "Failed to set auth token", detail: "the key/value pairs supplied are empty or invalid")
    }
    @objc static func serverErrorCode(of error: NSError?) -> String? {
        error?.userInfo[serverErrorCodeKey] as? String
    }
    @objc static func isUserJwtExpired(_ error: NSError?) -> Bool {
        guard let error else { return false }
        let status = error.userInfo["kIntercomHTTPStatusCode"] as? Int ?? error.code
        return status == 400 && serverErrorCode(of: error) == userJwtExpiredCode
    }
}
