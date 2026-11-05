import UIKit

@objc(_TtP18IntercomSDKPrivate13ThemeProvider_)
protocol ThemeProvider {
    var theme: Theme { get }
}

@objc(_TtC18IntercomSDKPrivate20DefaultThemeProvider)
class DefaultThemeProvider: NSObject, ThemeProvider {
    @objc var theme: Theme
    private let notificationCenter: NotificationCenter
    private let config: ICMConfigProtocol

    @objc static let shared = DefaultThemeProvider(notificationCenter: .default, config: ICMConfig.shared())

    @objc init(notificationCenter: NotificationCenter, config: ICMConfigProtocol) {
        self.notificationCenter = notificationCenter
        self.config = config
        theme = Theme(primaryForeground: UIColor.systemBlue, primaryBackground: UIColor.systemBlue)
        super.init()
        refreshThemeForCurrentConfig()
        notificationCenter.addObserver(self, selector: #selector(refreshThemeForCurrentConfig),
                                       name: NSNotification.Name("kICMIntercomConfigChangedNotification"), object: nil)
    }

    @objc private func refreshThemeForCurrentConfig() {
        // The Clang alias and real Swift class describe the same runtime object.
        guard let rawModules = config.modules else { return }
        let modules = (rawModules as AnyObject) as! ConfigModules
        let foreground = modules.customizationConfig.primaryForegroundColor
        let background = modules.customizationConfig.actionColors.primaryBackgroundColor
        theme = Theme(primaryForeground: foreground, primaryBackground: background)
    }

    @objc override init() { fatalError("init() has not been implemented") }
}
