import NitroModules
import UIKit

class ReactNativeDeviceUtils: HybridReactNativeDeviceUtilsSpec {
    
    private var spanningCallback: ((Bool) -> Void)?
    
    // MARK: - Dual Screen Detection
    
    public func isDualScreenDevice() throws -> Bool {
        // iOS doesn't have dual screen devices like Surface Duo
        // But we can check for external displays
        return UIScreen.screens.count > 1
    }
    
    public func isSpanning() throws -> Bool {
        // For iOS, we consider spanning when there are multiple screens
        // or when the app is running on an external display
        let screens = UIScreen.screens
        if screens.count > 1 {
            // Check if we have windows on multiple screens
            guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene else {
                return false
            }
            return windowScene.windows.count > 1
        }
        return false
    }
    
    // MARK: - Window Information
    
    public func getWindowRects() throws -> Promise<[DualScreenInfoRect]> {
        return Promise { resolve, reject in
            DispatchQueue.main.async {
                var rects: [DualScreenInfoRect] = []
                
                // Get all windows from all scenes
                for scene in UIApplication.shared.connectedScenes {
                    if let windowScene = scene as? UIWindowScene {
                        for window in windowScene.windows {
                            let frame = window.frame
                            let rect = DualScreenInfoRect(
                                x: Double(frame.origin.x),
                                y: Double(frame.origin.y),
                                width: Double(frame.size.width),
                                height: Double(frame.size.height)
                            )
                            rects.append(rect)
                        }
                    }
                }
                
                // If no windows found, use main screen bounds
                if rects.isEmpty {
                    let mainScreen = UIScreen.main.bounds
                    let rect = DualScreenInfoRect(
                        x: Double(mainScreen.origin.x),
                        y: Double(mainScreen.origin.y),
                        width: Double(mainScreen.size.width),
                        height: Double(mainScreen.size.height)
                    )
                    rects.append(rect)
                }
                
                resolve(rects)
            }
        }
    }
    
    public func getHingeBounds() throws -> Promise<DualScreenInfoRect> {
        return Promise { resolve, reject in
            DispatchQueue.main.async {
                // iOS doesn't have physical hinges, but we can simulate
                // the area between screens if multiple displays exist
                let screens = UIScreen.screens
                if screens.count > 1 {
                    let mainBounds = UIScreen.main.bounds
                    // Simulate hinge as a small area between screens
                    let hingeRect = DualScreenInfoRect(
                        x: Double(mainBounds.width),
                        y: 0,
                        width: 20, // Simulated hinge width
                        height: Double(mainBounds.height)
                    )
                    resolve(hingeRect)
                } else {
                    // No hinge on single screen
                    let emptyRect = DualScreenInfoRect(x: 0, y: 0, width: 0, height: 0)
                    resolve(emptyRect)
                }
            }
        }
    }
    
    // MARK: - Spanning Change Callback
    
    public func onSpanningChanged(callback: @escaping (Bool) -> Void) throws -> Void {
        self.spanningCallback = callback
        
        // Listen for screen connection/disconnection events
        NotificationCenter.default.addObserver(
            forName: UIScreen.didConnectNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.checkSpanningState()
        }
        
        NotificationCenter.default.addObserver(
            forName: UIScreen.didDisconnectNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.checkSpanningState()
        }
        
        // Also listen for window scene changes
        NotificationCenter.default.addObserver(
            forName: UIScene.didActivateNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.checkSpanningState()
        }
    }
    
    private func checkSpanningState() {
        do {
            let isCurrentlySpanning = try isSpanning()
            spanningCallback?(isCurrentlySpanning)
        } catch {
            // Handle error silently or log it
            print("Error checking spanning state: \(error)")
        }
    }
    
    // MARK: - Background Color
    
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
    
    private func parseColor(_ colorString: String) -> UIColor {
        let trimmedColor = colorString.trimmingCharacters(in: .whitespacesAndNewlines)
        
        // Handle hex colors
        if trimmedColor.hasPrefix("#") {
            let hex = String(trimmedColor.dropFirst())
            var rgbValue: UInt64 = 0
            Scanner(string: hex).scanHexInt64(&rgbValue)
            
            let red = CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0
            let green = CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0
            let blue = CGFloat(rgbValue & 0x0000FF) / 255.0
            
            return UIColor(red: red, green: green, blue: blue, alpha: 1.0)
        }
        
        // Handle named colors
        switch trimmedColor.lowercased() {
        case "red": return .red
        case "green": return .green
        case "blue": return .blue
        case "yellow": return .yellow
        case "orange": return .orange
        case "purple": return .purple
        case "black": return .black
        case "white": return .white
        case "gray", "grey": return .gray
        case "clear": return .clear
        default: return .systemBackground
        }
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}
