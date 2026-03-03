import NitroModules
import ReactNativeNativeLogger

class ReactNativeAppUpdate: HybridReactNativeAppUpdateSpec {

    private var nextListenerId: Int64 = 1

    func downloadAPK(params: AppUpdateDownloadParams) throws -> Promise<Void> {
        OneKeyLog.debug("AppUpdate", "downloadAPK not available on iOS")
        return Promise.resolved(withResult: ())
    }

    func downloadASC(params: AppUpdateFileParams) throws -> Promise<Void> {
        OneKeyLog.debug("AppUpdate", "downloadASC not available on iOS")
        return Promise.resolved(withResult: ())
    }

    func verifyASC(params: AppUpdateFileParams) throws -> Promise<Void> {
        OneKeyLog.debug("AppUpdate", "verifyASC not available on iOS")
        return Promise.resolved(withResult: ())
    }

    func verifyAPK(params: AppUpdateFileParams) throws -> Promise<Void> {
        OneKeyLog.debug("AppUpdate", "verifyAPK not available on iOS")
        return Promise.resolved(withResult: ())
    }

    func installAPK(params: AppUpdateFileParams) throws -> Promise<Void> {
        OneKeyLog.debug("AppUpdate", "installAPK not available on iOS")
        return Promise.resolved(withResult: ())
    }

    func clearCache() throws -> Promise<Void> {
        return Promise.resolved(withResult: ())
    }

    func addDownloadListener(callback: @escaping (DownloadEvent) -> Void) throws -> Double {
        let id = nextListenerId
        nextListenerId += 1
        return Double(id)
    }

    func removeDownloadListener(id: Double) throws {
    }
}
