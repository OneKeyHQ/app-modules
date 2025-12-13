import NitroModules

class KeychainModule: HybridKeychainModuleSpec {
    
    private let moduleCore = KeychainModuleCore()
    
    public func setItem(params: SetItemParams) throws -> Promise<Bool> {
          let typedParams = params
          do {
            try moduleCore.setItem(params: typedParams)
            return Promise.resolved(withResult: true)
          } catch let error as KeychainModuleError {
            switch error {
            case .encodingFailed:
              return Promise.rejected(withError: NSError(domain: "keychain_set_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to encode value"]))
            case .operationFailed(let status):
              return Promise.rejected(withError: NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: nil))
            default:
              return Promise.rejected(withError: NSError(domain: "keychain_set_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to set keychain item"]))
            }
          } catch {
            return Promise.rejected(withError: NSError(domain: "keychain_set_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to set keychain item", NSUnderlyingErrorKey: error]))
          }
    }
    
  public func getItem(params : GetItemParams) throws -> Promise<Variant_NullType_GetItemResult> {
        let typedParams = params
        do {
            if let result = try moduleCore.getItem(params: typedParams) {
                return Promise.resolved(withResult: Variant_NullType_GetItemResult.second(result))
            } else {
                return Promise.resolved(withResult: Variant_NullType_GetItemResult.first(NullType.null))
            }
        } catch let error as KeychainModuleError {
            switch error {
                case .operationFailed(let status):
                    return Promise.rejected(withError: NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: [NSLocalizedDescriptionKey: "Failed to get keychain item: \(status)"]))
                default:
                    return Promise.rejected(withError: NSError(domain: "keychain_get_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to get keychain item", NSUnderlyingErrorKey: error as NSError]))
            }
        } catch {
            return Promise.rejected(withError: NSError(domain: "keychain_get_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to get keychain item", NSUnderlyingErrorKey: error]))
        }
    }
    
    public func removeItem(params: RemoveItemParams) throws -> Promise<Bool> {
        let typedParams = params
        do {
            try moduleCore.removeItem(params: typedParams)
            return Promise.resolved(withResult: true)
        } catch let error as KeychainModuleError {
        switch error {
            case .operationFailed(let status):
                return Promise.rejected(withError: NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: [NSLocalizedDescriptionKey: "Failed to remove keychain item: \(status)"]))
            default:
                return Promise.rejected(withError: NSError(domain: "keychain_remove_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to remove keychain item", NSUnderlyingErrorKey: error as NSError]))
            }
        } catch {
            return Promise.rejected(withError: NSError(domain: "keychain_remove_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to remove keychain item", NSUnderlyingErrorKey: error]))
        }
    }
    
    public func hasItem(params: HasItemParams) throws -> Promise<Bool> {
        let typedParams = params
         do {
            let exists = try moduleCore.hasItem(params: typedParams)
            return Promise.resolved(withResult: exists)
        } catch {
            return Promise.rejected(withError: NSError(domain: "keychain_has_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to check keychain item", NSUnderlyingErrorKey: error]))
        }
    }
    
    public func isICloudSyncEnabled() throws -> Promise<Bool> {
        do {
            let enabled = try moduleCore.isICloudSyncEnabled()
            return Promise.resolved(withResult: enabled)
        } catch {
            return Promise.rejected(withError: NSError(domain: "keychain_sync_check_error", code: -1000, userInfo: [NSLocalizedDescriptionKey: "Failed to check iCloud sync status", NSUnderlyingErrorKey: error]))
        }
    }
}
