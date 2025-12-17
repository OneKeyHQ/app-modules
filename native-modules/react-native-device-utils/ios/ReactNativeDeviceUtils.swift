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
    
    public func changeBackgroundColor(r: Double, g: Double, b: Double, a: Double) throws -> Void {
        DispatchQueue.main.async {
            let color = UIColor(red: r/255, green: g/255, blue: b/255, alpha: a/255)
            let rootViewController = UIApplication.shared.delegate?.window??.rootViewController
            rootViewController?.view.backgroundColor = color
        }
    }
}
