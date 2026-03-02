import NitroModules
import ReactNativeNativeLogger

class ReactNativeGetRandomValues: HybridReactNativeGetRandomValuesSpec {
    private static let maxByteLength = 65536  // 64 KB upper bound

    func getRandomBase64(byteLength: Double) throws -> String {
        let length = Int(byteLength)
        guard length > 0, length <= ReactNativeGetRandomValues.maxByteLength,
              let data = NSMutableData(length: length) else {
            throw NSError(domain: "ReactNativeGetRandomValues", code: -1,
                          userInfo: [NSLocalizedDescriptionKey: "Invalid byteLength: must be 1...\(ReactNativeGetRandomValues.maxByteLength)"])
        }
        let result = SecRandomCopyBytes(kSecRandomDefault, length, data.mutableBytes)
        if result != errSecSuccess {
            OneKeyLog.error("RandomValues", "SecRandomCopyBytes failed with status: \(result)")
            throw NSError(domain: "ReactNativeGetRandomValues", code: Int(result), userInfo: nil)
        }
        return data.base64EncodedString(options: [])
    }
}
