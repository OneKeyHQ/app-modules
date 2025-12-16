import NitroModules

class ReactNativeGetRandomValues: HybridReactNativeGetRandomValuesSpec {
    
    public func hello(params: ReactNativeGetRandomValuesParams) throws -> Promise<ReactNativeGetRandomValuesResult> {
        let result = ReactNativeGetRandomValuesResult(success: true, data: "Hello, \(params.message)!")
        return Promise.resolved(withResult: result)
    }
}
