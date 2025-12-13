import NitroModules

class KeychainModule: HybridKeychainModuleSpec {
    
    private let moduleCore = KeychainModuleCore()
    
    public func setItem(params: SetItemParams) throws -> Promise<Bool> {
        return Promise.async {
            try self.moduleCore.setItem(params: params)
            return true
        }
    }
    
    public func getItem(params: GetItemParams) throws -> Promise<GetItemResult?> {
        return Promise.async {
            return try self.moduleCore.getItem(params: params)
        }
    }
    
    public func removeItem(params: RemoveItemParams) throws -> Promise<Bool> {
        return Promise.async {
            try self.moduleCore.removeItem(params: params)
            return true
        }
    }
    
    public func hasItem(params: HasItemParams) throws -> Promise<Bool> {
        return Promise.async {
            return try self.moduleCore.hasItem(params: params)
        }
    }
    
    public func isICloudSyncEnabled() throws -> Promise<Bool> {
        return Promise.async {
            return try self.moduleCore.isICloudSyncEnabled()
        }
    }
}
