import NitroModules
import UIKit
import ReactNativeNativeLogger

@objcMembers
public class LaunchOptionsStore: NSObject {
    public static let shared = LaunchOptionsStore()

    public var launchOptions: [AnyHashable: Any]?
    public var deviceToken: Data?
    public var startupTime: TimeInterval = 0

    private static let deviceTokenKey = "1k_device_token"

    public func getDeviceTokenString() -> String {
        // Prefer the JS-saved token (persisted across launches)
        if let saved = UserDefaults.standard.string(forKey: LaunchOptionsStore.deviceTokenKey), !saved.isEmpty {
            return saved
        }
        // Fall back to the native APNs token set by AppDelegate
        guard let token = deviceToken else { return "" }
        return token.map { String(format: "%02.2hhx", $0) }.joined()
    }

    public func saveDeviceToken(_ token: String) {
        UserDefaults.standard.set(token, forKey: LaunchOptionsStore.deviceTokenKey)
    }
}

class ReactNativeDeviceUtils: HybridReactNativeDeviceUtilsSpec {

    private static let userInterfaceStyleKey = "1k_user_interface_style"

    override init() {
        super.init()
        restoreUserInterfaceStyle()
    }

    private func restoreUserInterfaceStyle() {
        guard let style = UserDefaults.standard.string(forKey: ReactNativeDeviceUtils.userInterfaceStyleKey) else {
            OneKeyLog.debug("DeviceUtils", "No saved UI style found")
            return
        }
        OneKeyLog.info("DeviceUtils", "Restored UI style: \(style)")
        applyUserInterfaceStyle(style)
    }

    private func applyUserInterfaceStyle(_ style: String) {
        DispatchQueue.main.async {
            var uiStyle: UIUserInterfaceStyle = .unspecified
            if style == "light" {
                uiStyle = .light
            } else if style == "dark" {
                uiStyle = .dark
            }
            if #available(iOS 15.0, *) {
                for scene in UIApplication.shared.connectedScenes {
                    if let windowScene = scene as? UIWindowScene {
                        for window in windowScene.windows {
                            window.overrideUserInterfaceStyle = uiStyle
                        }
                    }
                }
            } else {
                for window in UIApplication.shared.windows {
                    window.overrideUserInterfaceStyle = uiStyle
                }
            }
        }
    }

    public func isDualScreenDevice() throws -> Bool {
        return false
    }

    public func initEventListeners() throws -> Void {
    }
    
    public func isSpanning() throws -> Bool {
        return false
    }
    
    public func getWindowRects() throws -> Promise<[DualScreenInfoRect]> {
        return Promise.resolved(withResult: [])
    }
    
    public func getHingeBounds() throws -> Promise<DualScreenInfoRect> {
        return Promise.resolved(withResult: DualScreenInfoRect(x: 0, y: 0, width: 0, height: 0))
    }

   
    func addSpanningChangedListener(callback: @escaping (Bool) -> Void) throws -> Double {
        return 0
    }

    func removeSpanningChangedListener(id: Double) throws -> Void {
    }
    
    
    public func setUserInterfaceStyle(style: UserInterfaceStyle) throws -> Void {
        OneKeyLog.info("DeviceUtils", "Set UI style: \(style.stringValue)")
        UserDefaults.standard.set(style.stringValue, forKey: ReactNativeDeviceUtils.userInterfaceStyleKey)
        applyUserInterfaceStyle(style.stringValue)
    }

    public func changeBackgroundColor(r: Double, g: Double, b: Double, a: Double) throws -> Void {
        DispatchQueue.main.async {
            // Clamp color values to valid range [0, 255]
            let red = max(0, min(255, r))
            let green = max(0, min(255, g))
            let blue = max(0, min(255, b))
            let alpha = max(0, min(255, a))

            let color = UIColor(red: red/255.0, green: green/255.0, blue: blue/255.0, alpha: alpha/255.0)

            // Fix optional chaining syntax
            if let window = UIApplication.shared.delegate?.window,
               let rootViewController = window?.rootViewController {
                rootViewController.view.backgroundColor = color
            }
        }
    }

    // MARK: - LaunchOptionsManager

    func getLaunchOptions() throws -> Promise<LaunchOptions> {
        return Promise.async {
            let store = LaunchOptionsStore.shared
            guard let opts = store.launchOptions else {
                OneKeyLog.debug("DeviceUtils", "getLaunchOptions: no launch options")
                return LaunchOptions(launchType: "normal", deepLink: nil)
            }

            if opts[UIApplication.LaunchOptionsKey.remoteNotification] != nil {
                return LaunchOptions(launchType: "remoteNotification", deepLink: nil)
            }

            if opts[UIApplication.LaunchOptionsKey.localNotification] != nil {
                return LaunchOptions(launchType: "localNotification", deepLink: nil)
            }

            let deepLink = (opts[UIApplication.LaunchOptionsKey.url] as? URL)?.absoluteString
            return LaunchOptions(launchType: "normal", deepLink: deepLink)
        }
    }

    func clearLaunchOptions() throws -> Promise<Bool> {
        return Promise.async {
            LaunchOptionsStore.shared.launchOptions = nil
            OneKeyLog.info("DeviceUtils", "Cleared launch options")
            return true
        }
    }

    func getDeviceToken() throws -> Promise<String> {
        return Promise.async {
            return LaunchOptionsStore.shared.getDeviceTokenString()
        }
    }

    func saveDeviceToken(token: String) throws -> Promise<Void> {
        return Promise.async {
            LaunchOptionsStore.shared.saveDeviceToken(token)
            OneKeyLog.info("DeviceUtils", "saveDeviceToken: token saved")
        }
    }

    func registerDeviceToken() throws -> Promise<Bool> {
        return Promise.async {
            OneKeyLog.info("DeviceUtils", "registerDeviceToken")
            return true
        }
    }

    func getStartupTime() throws -> Promise<Double> {
        return Promise.async {
            return LaunchOptionsStore.shared.startupTime * 1000.0
        }
    }

    // MARK: - ExitModule

    func exitApp() throws {
        // No-op on iOS: Apple prohibits programmatic app termination (App Store guideline 2.4.5).
    }

    // MARK: - WebView & Play Services

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

    // MARK: - Boot Recovery

    private static let bootFailCountKey = "onekey_consecutive_boot_fail_count"
    private static let recoveryActionKey = "onekey_recovery_action"

    func markBootSuccess() throws -> Void {
        UserDefaults.standard.set(0, forKey: ReactNativeDeviceUtils.bootFailCountKey)
        UserDefaults.standard.synchronize()
    }

    func getConsecutiveBootFailCount() throws -> Promise<Double> {
        return Promise.resolved(withResult:
            Double(UserDefaults.standard.integer(forKey: ReactNativeDeviceUtils.bootFailCountKey))
        )
    }

    func incrementConsecutiveBootFailCount() throws -> Void {
        let current = UserDefaults.standard.integer(forKey: ReactNativeDeviceUtils.bootFailCountKey)
        UserDefaults.standard.set(current + 1, forKey: ReactNativeDeviceUtils.bootFailCountKey)
        UserDefaults.standard.synchronize()
    }

    func setConsecutiveBootFailCount(count: Double) throws -> Void {
        UserDefaults.standard.set(Int(count), forKey: ReactNativeDeviceUtils.bootFailCountKey)
        UserDefaults.standard.synchronize()
    }

    func getAndClearRecoveryAction() throws -> Promise<String> {
        let action = UserDefaults.standard.string(forKey: ReactNativeDeviceUtils.recoveryActionKey) ?? ""
        if !action.isEmpty {
            UserDefaults.standard.removeObject(forKey: ReactNativeDeviceUtils.recoveryActionKey)
            UserDefaults.standard.synchronize()
        }
        return Promise.resolved(withResult: action)
    }

    // MARK: - Android Channel & Installer

    func getAndroidChannel() throws -> AndroidChannel {
        // iOS has no ANDROID_CHANNEL concept; signal with `.unknown` for API parity.
        return .unknown
    }

    func getInstallerPackageName() throws -> InstallerPackageName {
        // Mirror react-native-device-info: distinguish AppStore / TestFlight / Other
        // via receipt path and embedded mobileprovision. Simulator has neither.
        #if targetEnvironment(simulator)
        return .unknown
        #else
        let hasEmbeddedProvision = Bundle.main.path(forResource: "embedded", ofType: "mobileprovision") != nil
        if let receiptUrl = Bundle.main.appStoreReceiptURL {
            if receiptUrl.lastPathComponent == "sandboxReceipt" {
                return hasEmbeddedProvision ? .other : .testflight
            }
            return hasEmbeddedProvision ? .other : .appstore
        }
        return hasEmbeddedProvision ? .other : .unknown
        #endif
    }
}
