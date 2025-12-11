import Foundation
import NitroModules

class LiteCard: HybridLiteCardSpec {
    private let liteManager: OKLiteManager
    
    public var hybridContext = margelo.nitro.HybridContext()
    
    public var memorySize: Int {
        return getSizeOf(self)
    }
    
    override init() {
        self.liteManager = OKLiteManager.shared()
        super.init()
    }
    
    public func multiply(a: Double, b: Double) throws -> Double {
        return a * b
    }
    
    public func checkNFCPermission() throws -> Promise<Bool> {
        return Promise.async {
            try await withCheckedThrowingContinuation { continuation in
                self.liteManager.checkNFCPermission { error, result, info in
                    if let error = error {
                        continuation.resume(throwing: error)
                    } else if let result = result as? Bool {
                        continuation.resume(returning: result)
                    } else {
                        continuation.resume(throwing: NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                    }
                }
            }
        }
    }
    
    public func getLiteInfo() throws -> Promise<LiteCardInfo?> {
        return Promise.async {
            try await withCheckedThrowingContinuation { continuation in
                self.liteManager.getLiteInfo { error, result, info in
                    if let error = error {
                        continuation.resume(throwing: error)
                    } else if let result = result as? [String: Any] {
                        // Parse the result dictionary into LiteCardInfo
                        let hasBackup = result["hasBackup"] as? Bool ?? false
                        let pinRetryCount = result["pinRetryCount"] as? Int ?? 0
                        let isNewCard = result["isNewCard"] as? Bool ?? true
                        let serialNum = result["serialNum"] as? String ?? ""
                        
                        let liteCardInfo = LiteCardInfo(
                            hasBackup: hasBackup,
                            pinRetryCount: pinRetryCount,
                            isNewCard: isNewCard,
                            serialNum: serialNum
                        )
                        continuation.resume(returning: liteCardInfo)
                    } else {
                        continuation.resume(returning: nil)
                    }
                }
            }
        }
    }
    
    public func setMnemonic(mnemonic: String, pin: String, overwrite: Bool) throws -> Promise<Bool> {
        return Promise.async {
            try await withCheckedThrowingContinuation { continuation in
                self.liteManager.setMnemonic(mnemonic, pin: pin, overwrite: overwrite) { error, result, info in
                    if let error = error {
                        continuation.resume(throwing: error)
                    } else if let result = result as? Bool {
                        continuation.resume(returning: result)
                    } else {
                        continuation.resume(throwing: NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                    }
                }
            }
        }
    }
    
    public func getMnemonicWithPin(pin: String) throws -> Promise<String> {
        return Promise.async {
            try await withCheckedThrowingContinuation { continuation in
                self.liteManager.getMnemonicWithPin(pin) { error, result, info in
                    if let error = error {
                        continuation.resume(throwing: error)
                    } else if let result = result as? String {
                        continuation.resume(returning: result)
                    } else {
                        continuation.resume(throwing: NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                    }
                }
            }
        }
    }
    
    public func changePin(oldPin: String, newPin: String) throws -> Promise<Bool> {
        return Promise.async {
            try await withCheckedThrowingContinuation { continuation in
                self.liteManager.changePin(oldPin, newPwd: newPin) { error, result, info in
                    if let error = error {
                        continuation.resume(throwing: error)
                    } else if let result = result as? Bool {
                        continuation.resume(returning: result)
                    } else {
                        continuation.resume(throwing: NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                    }
                }
            }
        }
    }
    
    public func reset() throws -> Promise<Bool> {
        return Promise.async {
            try await withCheckedThrowingContinuation { continuation in
                self.liteManager.reset { error, result, info in
                    if let error = error {
                        continuation.resume(throwing: error)
                    } else if let result = result as? Bool {
                        continuation.resume(returning: result)
                    } else {
                        continuation.resume(throwing: NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                    }
                }
            }
        }
    }
}
