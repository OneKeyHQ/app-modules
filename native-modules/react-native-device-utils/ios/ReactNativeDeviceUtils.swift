import NitroModules
import UIKit

class ReactNativeDeviceUtils: HybridReactNativeDeviceUtilsSpec {
    
    private var spanningCallback: ((Bool) -> Void)?
    
    public func isDualScreenDevice() throws -> Bool {
        return false
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
    
    public func onSpanningChanged(callback: @escaping (Bool) -> Void) throws -> Void {
    }
    
    public func changeBackgroundColor(color: String) throws -> Void {
        DispatchQueue.main.async {
            guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                  let window = windowScene.windows.first else {
                return
            }
            
            // Parse color string (supports hex colors like "#FF0000" or named colors)
            let uiColor = self.parseColor(color)
            window.backgroundColor = uiColor
            
            // Also set the root view controller's view background if available
            if let rootViewController = window.rootViewController {
                rootViewController.view.backgroundColor = uiColor
            }
        }
    }
}
