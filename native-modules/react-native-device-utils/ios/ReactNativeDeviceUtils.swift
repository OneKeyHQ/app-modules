import NitroModules
import UIKit

class ReactNativeDeviceUtils: HybridReactNativeDeviceUtilsSpec {

    private static let userInterfaceStyleKey = "1k_user_interface_style"

    override init() {
        super.init()
        restoreUserInterfaceStyle()
    }

    private func restoreUserInterfaceStyle() {
        guard let style = UserDefaults.standard.string(forKey: ReactNativeDeviceUtils.userInterfaceStyleKey) else {
            return
        }
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
    
    
    public func setUserInterfaceStyle(style: String) throws -> Void {
        UserDefaults.standard.set(style, forKey: ReactNativeDeviceUtils.userInterfaceStyleKey)
        applyUserInterfaceStyle(style)
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
}
