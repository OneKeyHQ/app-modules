import NitroModules
import UIKit
import ReactNativeNativeLogger

class ReactNativeWebviewChecker: HybridReactNativeWebviewCheckerSpec {

    func getCurrentWebViewPackageInfo() throws -> Promise<WebViewPackageInfo> {
        return Promise.resolved(withResult: WebViewPackageInfo(
            packageName: "com.apple.WebKit",
            versionName: UIDevice.current.systemVersion,
            versionCode: 0
        ))
    }

    func isGooglePlayServicesAvailable() throws -> Promise<GooglePlayServicesStatus> {
        return Promise.resolved(withResult: GooglePlayServicesStatus(
            status: -1,
            isAvailable: false
        ))
    }
}
