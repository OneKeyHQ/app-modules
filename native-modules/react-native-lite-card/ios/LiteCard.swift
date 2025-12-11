import Foundation

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
        return Promise.async { resolve, reject in
            self.liteManager.checkNFCPermission { error, result, info in
                if let error = error {
                    reject(error)
                } else if let result = result as? Bool {
                    resolve(result)
                } else {
                    reject(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                }
            }
        }
    }
    
    public func getLiteInfo() throws -> Promise<[String: Any]> {
        return Promise.async { resolve, reject in
            self.liteManager.getLiteInfo { error, result, info in
                if let error = error {
                    reject(error)
                } else if let result = result as? [String: Any] {
                    resolve(result)
                } else {
                    reject(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                }
            }
        }
    }
    
    public func setMnemonic(mnemonic: String, pin: String, overwrite: Bool) throws -> Promise<Bool> {
        return Promise.async { resolve, reject in
            self.liteManager.setMnemonic(mnemonic, pin: pin, overwrite: overwrite) { error, result, info in
                if let error = error {
                    reject(error)
                } else if let result = result as? Bool {
                    resolve(result)
                } else {
                    reject(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                }
            }
        }
    }
    
    public func getMnemonicWithPin(pin: String) throws -> Promise<String> {
        return Promise.async { resolve, reject in
            self.liteManager.getMnemonicWithPin(pin) { error, result, info in
                if let error = error {
                    reject(error)
                } else if let result = result as? String {
                    resolve(result)
                } else {
                    reject(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                }
            }
        }
    }
    
    public func changePin(oldPin: String, newPin: String) throws -> Promise<Bool> {
        return Promise.async { resolve, reject in
            self.liteManager.changePin(oldPin, newPwd: newPin) { error, result, info in
                if let error = error {
                    reject(error)
                } else if let result = result as? Bool {
                    resolve(result)
                } else {
                    reject(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                }
            }
        }
    }
    
    public func reset() throws -> Promise<Bool> {
        return Promise.async { resolve, reject in
            self.liteManager.reset { error, result, info in
                if let error = error {
                    reject(error)
                } else if let result = result as? Bool {
                    resolve(result)
                } else {
                    reject(NSError(domain: "LiteCard", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid result type"]))
                }
            }
        }
    }
}
