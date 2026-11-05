import Foundation

@main enum JWTExpiryTests {
    static func main() throws {
        var passed: [String] = []
        func check(_ condition: Bool, _ name: String) {
            precondition(condition, name)
            passed.append(name)
        }
        func token(_ object: Any) throws -> String {
            let data = try JSONSerialization.data(withJSONObject: object, options: [.fragmentsAllowed])
            let encoded = data.base64EncodedString().replacingOccurrences(of: "+", with: "-").replacingOccurrences(of: "/", with: "_").replacingOccurrences(of: "=", with: "")
            return "header.\(encoded).signature"
        }
        check(JWTExpiry.expiry(of: nil) == nil, "nil expiry")
        check(JWTExpiry.expiry(of: 42) == nil, "nonstring expiry")
        check(JWTExpiry.expiry(of: "") == nil, "empty token")
        check(JWTExpiry.expiry(of: "one.two") == nil, "two components rejected")
        check(JWTExpiry.expiry(of: "one.two.three.four") == nil, "four components rejected")
        check(JWTExpiry.expiry(of: "header.@@.signature") == nil, "invalid base64 rejected")
        check(JWTExpiry.expiry(of: "header.eA.signature") == nil, "invalid JSON rejected")
        check(JWTExpiry.expiry(of: try token([1, 2])) == nil, "array payload rejected")
        check(JWTExpiry.expiry(of: try token(["other": 1])) == nil, "missing expiry rejected")
        check(JWTExpiry.expiry(of: try token(["exp": "1"])) == nil, "string expiry rejected")
        check(JWTExpiry.expiry(of: try token(["exp": NSNull()])) == nil, "null expiry rejected")
        check(JWTExpiry.expiry(of: try token(["exp": true])) == nil, "true expiry rejected")
        check(JWTExpiry.expiry(of: try token(["exp": false])) == nil, "false expiry rejected")
        check(JWTExpiry.expiry(of: try token(["exp": 0]))?.timeIntervalSince1970 == 0, "zero numeric expiry accepted")
        check(JWTExpiry.expiry(of: try token(["exp": -1.25]))?.timeIntervalSince1970 == -1.25, "fractional negative expiry preserved")
        check(JWTExpiry.expiry(of: try token(["exp": 1234567]))?.timeIntervalSince1970 == 1234567, "integer expiry preserved")
        let payload = try token(["exp": 1]).components(separatedBy: ".")[1]
        check(JWTExpiry.expiry(of: ".\(payload).")?.timeIntervalSince1970 == 1, "empty header and signature accepted")
        check(!JWTExpiry.isDefinitivelyExpired(nil), "unknown expiry does not count expired")
        check(JWTExpiry.isDefinitivelyExpired(try token(["exp": Date().timeIntervalSince1970 - 400])), "expired beyond 300 second grace")
        check(!JWTExpiry.isDefinitivelyExpired(try token(["exp": Date().timeIntervalSince1970 - 200])), "expired within grace remains not definitive")
        check(!JWTExpiry.isDefinitivelyExpired(try token(["exp": Date().timeIntervalSince1970 + 1000])), "future expiry not expired")
        let receipt: [String: Any] = ["status": "passed", "testCount": passed.count, "tests": passed, "scope": "JWT expiry helper only; original differential pending"]
        let data = try JSONSerialization.data(withJSONObject: receipt, options: [.prettyPrinted, .sortedKeys])
        print(String(decoding: data, as: UTF8.self))
    }
}
