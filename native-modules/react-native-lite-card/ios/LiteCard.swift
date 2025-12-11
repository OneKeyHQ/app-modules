import Foundation
import NitroModules

class LiteCard: HybridLiteCardSpec {
  
    private let liteManager: OKLiteManager
    
    override init() {
        self.liteManager = OKLiteManager.shared()
        super.init()
    }
    
    public func multiply(a: Double, b: Double) throws -> Double {
        return a * b
    }
    
    public func checkNFCPermission() throws -> Promise<Bool> {
        return Promise.async { resolve in
            self.liteManager.checkNFCPermission { error, result, info in
                if let error = error {
                    resolve(.rejected(error))
                } else if let result = result as? Bool {
                    resolve(.resolved(result))
                } else {
                    resolve(.rejected(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"])))
                }
            }
        }
    }
    
    public func getLiteInfo() throws -> Promise<Variant_NullType_LiteCardInfo> {
        return Promise.async { resolve in
            self.liteManager.getLiteInfo { error, result, info in
                if let error = error {
                    resolve(.rejected(error))
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
                    resolve(.resolved(liteCardInfo))
                } else {
                    resolve(.resolved(nil))
                }
            }
        }
    }
    
    public func setMnemonic(mnemonic: String, pin: String, overwrite: Bool) throws -> Promise<Bool> {
        return Promise.async { resolve in
            self.liteManager.setMnemonic(mnemonic, pin: pin, overwrite: overwrite) { error, result, info in
                if let error = error {
                    resolve(.rejected(error))
                } else if let result = result as? Bool {
                    resolve(.resolved(result))
                } else {
                    resolve(.rejected(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"])))
                }
            }
        }
    }
    
    public func getMnemonicWithPin(pin: String) throws -> Promise<String> {
        return Promise.async { resolve in
            self.liteManager.getMnemonicWithPin(pin) { error, result, info in
                if let error = error {
                    resolve(.rejected(error))
                } else if let result = result as? String {
                    resolve(.resolved(result))
                } else {
                    resolve(.rejected(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"])))
                }
            }
        }
    }
    
    public func changePin(oldPin: String, newPin: String) throws -> Promise<Bool> {
        return Promise.async { resolve in
            self.liteManager.changePin(oldPin, newPwd: newPin) { error, result, info in
                if let error = error {
                    resolve(.rejected(error))
                } else if let result = result as? Bool {
                    resolve(.resolved(result))
                } else {
                    resolve(.rejected(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"])))
                }
            }
        }
    }
    
    public func reset() throws -> Promise<Bool> {
        return Promise.async { resolve in
            self.liteManager.reset { error, result, info in
                if let error = error {
                    resolve(.rejected(error))
                } else if let result = result as? Bool {
                    resolve(.resolved(result))
                } else {
                    resolve(.rejected(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"])))
                }
            }
        }
    }
}
