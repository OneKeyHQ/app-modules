import NitroModules

class KeychainModule: HybridKeychainModuleSpec {
    
    private let moduleCore = KeychainModuleCore()
    
    public func setItem(params: SetItemParams) throws -> Promise<Bool> {
        return Promise.async {
            do {
                try self.moduleCore.setItem(params: params)
                return true
            } catch let error as KeychainModuleError {
                switch error {
                case .encodingFailed:
                    throw NSError(domain: "keychain_set_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to encode value"])
                case .operationFailed(let status):
                    throw NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: [NSLocalizedDescriptionKey: "Failed to set keychain item: \(status)"])
                default:
                    throw NSError(domain: "keychain_set_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to set keychain item"])
                }
            } catch {
                throw NSError(domain: "keychain_set_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to set keychain item", NSUnderlyingErrorKey: error])
            }
        }
    }
    
    public func getItem(params: GetItemParams) throws -> Promise<Variant_NullType_GetItemResult> {
        return Promise.async {
            do {
              let result = try self.moduleCore.getItem(params: params)
              return (result != nil) ? Variant_NullType_GetItemResult.second(result!) : Variant_NullType_GetItemResult.first(NullType.null)
            } catch let error as KeychainModuleError {
                switch error {
                case .operationFailed(let status):
                    throw NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: [NSLocalizedDescriptionKey: "Failed to get keychain item: \(status)"])
                default:
                    throw NSError(domain: "keychain_get_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to get keychain item"])
                }
            } catch {
                throw NSError(domain: "keychain_get_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to get keychain item", NSUnderlyingErrorKey: error])
            }
        }
    }
    
    public func removeItem(params: RemoveItemParams) throws -> Promise<Bool> {
        return Promise.async {
            do {
                try self.moduleCore.removeItem(params: params)
                return true
            } catch let error as KeychainModuleError {
                switch error {
                case .operationFailed(let status):
                    throw NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: [NSLocalizedDescriptionKey: "Failed to remove keychain item: \(status)"])
                default:
                    throw NSError(domain: "keychain_remove_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to remove keychain item"])
                }
            } catch {
                throw NSError(domain: "keychain_remove_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to remove keychain item", NSUnderlyingErrorKey: error])
            }
        }
    }
    
    public func hasItem(params: HasItemParams) throws -> Promise<Bool> {
        return Promise.async {
            do {
                return try self.moduleCore.hasItem(params: params)
            } catch {
                throw NSError(domain: "keychain_has_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to check keychain item", NSUnderlyingErrorKey: error])
            }
        }
    }
    
    public func isICloudSyncEnabled() throws -> Promise<Bool> {
        return Promise.async {
            do {
                return try self.moduleCore.isICloudSyncEnabled()
            } catch {
                throw NSError(domain: "keychain_sync_check_error", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to check iCloud sync status", NSUnderlyingErrorKey: error])
            }
        }
    }
}
