import NitroModules
import ReactNativeNativeLogger

class ReactNativeGetRandomValues: HybridReactNativeGetRandomValuesSpec {
    func getRandomBase64(byteLength: Double) throws -> String {
        let data = NSMutableData(length: Int(byteLength))!
        let result = SecRandomCopyBytes(kSecRandomDefault, Int(byteLength), data.mutableBytes)
        if result != errSecSuccess {
            OneKeyLog.error("RandomValues", "SecRandomCopyBytes failed with status: \(result)")
            throw NSError(domain: "ReactNativeGetRandomValues", code: Int(result), userInfo: nil)
        }
        return data.base64EncodedString(options: .lineLength64Characters)
    }
}
