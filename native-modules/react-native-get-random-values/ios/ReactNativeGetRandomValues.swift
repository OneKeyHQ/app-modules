import NitroModules
import ReactNativeNativeLogger

class ReactNativeGetRandomValues: HybridReactNativeGetRandomValuesSpec {
    func getRandomBase64(byteLength: Double) throws -> String {
        let length = Int(byteLength)
        guard length > 0, let data = NSMutableData(length: length) else {
            OneKeyLog.error("RandomValues", "Failed to allocate buffer of size \(Int(byteLength))")
            throw NSError(domain: "ReactNativeGetRandomValues", code: -1, userInfo: nil)
        }
        let result = SecRandomCopyBytes(kSecRandomDefault, length, data.mutableBytes)
        if result != errSecSuccess {
            OneKeyLog.error("RandomValues", "SecRandomCopyBytes failed with status: \(result)")
            throw NSError(domain: "ReactNativeGetRandomValues", code: Int(result), userInfo: nil)
        }
        return data.base64EncodedString(options: [])
    }
}
