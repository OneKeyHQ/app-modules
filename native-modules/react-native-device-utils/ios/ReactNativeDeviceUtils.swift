import NitroModules

class ReactNativeDeviceUtils: HybridReactNativeDeviceUtilsSpec {
    
    public func hello(params: ReactNativeDeviceUtilsParams) throws -> Promise<ReactNativeDeviceUtilsResult> {
        let result = ReactNativeDeviceUtilsResult(success: true, data: "Hello, \(params.message)!")
        return Promise.resolved(withResult: result)
    }
}
