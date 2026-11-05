import Foundation

@main enum ErrorProcessorTests {
    static func main() throws {
        var passed: [String] = []
        func check(_ condition: Bool, _ name: String) {
            precondition(condition, name)
            passed.append(name)
        }
        let errors: [(String, NSError, Int, String)] = [
            ("HTTP", ErrorProcessor.generateHTTPError(code: 503, message: "synthetic unavailable"), 503, "ERROR - [HTTP 503] - synthetic unavailable"),
            ("invalid identifiers", ErrorProcessor.generateInvalidUserIdOrEmailError(), 2001, "ERROR - Failed to register user - the 'userId' or 'email' property of 'userAttributes' must be populated as a string"),
            ("identified exists", ErrorProcessor.generateIdentifiedUserExists(), 3002, "ERROR - Failed to register user - We already have a registered user. If you are attempting to register a new user, call logout() before this. If you are attempting to register an identified user call: loginUserWithUserAttributes"),
            ("invalid update", ErrorProcessor.generateInvalidUpdateObjectError(), 3001, "ERROR - Failed to update user - the update object must be not nil and of type `ICMUserAttributes`"),
            ("push disabled", ErrorProcessor.generatePushNotificationsNotEnabledError(), 4001, "ERROR - Failed to register a device token - push notifications are not enabled on the host device"),
            ("nil token", ErrorProcessor.generatePushNotificationNilDeviceTokenError(), 4002, "ERROR - Failed to register a device token - device token is nil"),
            ("config missing", ErrorProcessor.generateConfigNotReceivedError(), 4003, "ERROR - Failed to register a device token - the Intercom configuration has not loaded - call setDeviceToken again to retry"),
            ("invalid verification", ErrorProcessor.generateInvalidIdentityVerificationError(), 4003, "ERROR - Failed to register a device token - identity verification is not setup correctly"),
            ("expired verification", ErrorProcessor.generateExpiredIdentityVerificationTokenError(), 4003, "ERROR - Failed to register a device token - the identity verification token has expired - call setUserJwt: with a current token"),
            ("invalid token", ErrorProcessor.generateInvalidDeviceTokenError(), 4004, "ERROR - Failed to register a device token - device token is invalid"),
            ("invalid auth", ErrorProcessor.generateInvalidAuthTokenError(), 2001, "ERROR - Failed to set auth token - the key/value pairs supplied are empty or invalid")
        ]
        for (name, error, code, message) in errors {
            check(error.domain == "IntercomSDKErrorDomain" && error.code == code && error.userInfo.count == 1 && error.localizedDescription == message, "error mapping \(name)")
        }
        check(ErrorProcessor.serverErrorCodeKey == "kIntercomErrorCode" && ErrorProcessor.userJwtExpiredCode == "intercom_user_jwt_expired", "server error constants")
        check(ErrorProcessor.serverErrorCode(of: nil) == nil && !ErrorProcessor.isUserJwtExpired(nil), "nil error")
        let codeKey = ErrorProcessor.serverErrorCodeKey
        let expiredCode = ErrorProcessor.userJwtExpiredCode
        func make(_ code: Int, _ info: [String: Any]) -> NSError { NSError(domain: "synthetic-other-domain", code: code, userInfo: info) }
        check(ErrorProcessor.isUserJwtExpired(make(400, [codeKey: expiredCode])), "expired detection preserves no domain restriction")
        check(!ErrorProcessor.isUserJwtExpired(make(401, [codeKey: expiredCode])), "non400 status is not JWT expiry")
        check(ErrorProcessor.isUserJwtExpired(make(500, [codeKey: expiredCode, "kIntercomHTTPStatusCode": 400])), "integer status overrides NSError code")
        check(!ErrorProcessor.isUserJwtExpired(make(400, [codeKey: expiredCode, "kIntercomHTTPStatusCode": 500])), "non400 integer override wins")
        check(ErrorProcessor.isUserJwtExpired(make(400, [codeKey: expiredCode, "kIntercomHTTPStatusCode": "500"])), "nonnumeric status falls back to NSError code")
        check(!ErrorProcessor.isUserJwtExpired(make(400, [codeKey: "other"])), "other server code rejected")
        check(!ErrorProcessor.isUserJwtExpired(make(400, [codeKey: 42])) && ErrorProcessor.serverErrorCode(of: make(400, [codeKey: 42])) == nil, "numeric server error code rejected")
        check(!ErrorProcessor.isUserJwtExpired(make(400, [:])), "missing server error code rejected")
        let receipt: [String: Any] = ["status": "passed", "testCount": passed.count, "tests": passed,
                                    "errors": errors.map { ["name": $0.0, "domain": $0.1.domain, "code": $0.1.code, "description": $0.1.localizedDescription] },
                                    "scope": "ErrorProcessor helper only; original differential pending"]
        let data = try JSONSerialization.data(withJSONObject: receipt, options: [.prettyPrinted, .sortedKeys])
        print(String(decoding: data, as: UTF8.self))
    }
}
