// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
import UIKit

@objc enum ThemeMode: Int { case light, dark, system }

private func parsedThemeMode(_ value: String?) -> ThemeMode {
    switch value {
    case "dark": return .dark
    case "system": return .system
    default: return .light
    }
}

@objc(_TtC18IntercomSDKPrivate19CustomizationConfig)
class CustomizationConfig: NSObject, NSSecureCoding {
    @objc let headerCustomization: HeaderCustomization
    @objc let actionColors: ActionColors
    @objc let actionContrastLightColors: ActionContrastColors?
    @objc let actionContrastDarkColors: ActionContrastColors?
    @objc let themeMode: ThemeMode
    @objc let messengerLogoDarkURL: URL?
    @objc let launcherLogoURL: URL?
    @objc let launcherLogoDarkURL: URL?
    private var themeOverride: ThemeMode?
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(headerCustomization: HeaderCustomization, actionColors: ActionColors, actionContrastLightColors: ActionContrastColors?, actionContrastDarkColors: ActionContrastColors?, themeMode: ThemeMode, messengerLogoDarkURL: URL?, launcherLogoURL: URL?, launcherLogoDarkURL: URL?) {
        self.headerCustomization = headerCustomization
        self.actionColors = actionColors
        self.actionContrastLightColors = actionContrastLightColors
        self.actionContrastDarkColors = actionContrastDarkColors
        self.themeMode = themeMode
        self.messengerLogoDarkURL = messengerLogoDarkURL
        self.launcherLogoURL = launcherLogoURL
        self.launcherLogoDarkURL = launcherLogoDarkURL
        themeOverride = nil
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let headerDictionary = dictionary["header"] as? [String: Any],
              let header = HeaderCustomization(withDictionary: headerDictionary),
              let actionDictionary = dictionary["action"] as? [String: Any],
              let action = ActionColors(withDictionary: actionDictionary) else { return nil }
        let lightContrast = (dictionary["action_contrast_white"] as? [String: Any]).flatMap(ActionContrastColors.init(withDictionary:))
        let darkContrast = (dictionary["action_contrast_dark"] as? [String: Any]).flatMap(ActionContrastColors.init(withDictionary:))
        self.init(headerCustomization: header, actionColors: action, actionContrastLightColors: lightContrast, actionContrastDarkColors: darkContrast, themeMode: parsedThemeMode(dictionary["theme_mode"] as? String), messengerLogoDarkURL: (dictionary["messenger_logo_dark_url"] as? String).flatMap(URL.init(string:)), launcherLogoURL: (dictionary["launcher_logo_url"] as? String).flatMap(URL.init(string:)), launcherLogoDarkURL: (dictionary["launcher_logo_dark_url"] as? String).flatMap(URL.init(string:)))
    }

    required init?(coder: NSCoder) {
        guard let header = coder.decodeObject(of: HeaderCustomization.self, forKey: "header"),
              let action = coder.decodeObject(of: ActionColors.self, forKey: "action") else { return nil }
        headerCustomization = header
        actionColors = action
        actionContrastLightColors = coder.decodeObject(of: ActionContrastColors.self, forKey: "action_contrast_white")
        actionContrastDarkColors = coder.decodeObject(of: ActionContrastColors.self, forKey: "action_contrast_dark")
        themeMode = parsedThemeMode(coder.decodeObject(of: NSString.self, forKey: "theme_mode") as String?)
        messengerLogoDarkURL = (coder.decodeObject(of: NSString.self, forKey: "messenger_logo_dark_url") as String?).flatMap(URL.init(string:))
        launcherLogoURL = (coder.decodeObject(of: NSString.self, forKey: "launcher_logo_url") as String?).flatMap(URL.init(string:))
        launcherLogoDarkURL = (coder.decodeObject(of: NSString.self, forKey: "launcher_logo_dark_url") as String?).flatMap(URL.init(string:))
        themeOverride = nil
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(headerCustomization, forKey: "header")
        coder.encode(actionColors, forKey: "action")
        coder.encode(actionContrastLightColors, forKey: "action_contrast_white")
        coder.encode(actionContrastDarkColors, forKey: "action_contrast_dark")
        let name: String
        switch themeMode.rawValue {
        case 0: name = "light"
        case 1: name = "dark"
        case 2: name = "system"
        default: preconditionFailure("Invalid ThemeMode")
        }
        coder.encode(name, forKey: "theme_mode")
        coder.encode(messengerLogoDarkURL?.absoluteString, forKey: "messenger_logo_dark_url")
        coder.encode(launcherLogoURL?.absoluteString, forKey: "launcher_logo_url")
        coder.encode(launcherLogoDarkURL?.absoluteString, forKey: "launcher_logo_dark_url")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return headerCustomization == other.headerCustomization && actionColors == other.actionColors && actionContrastLightColors == other.actionContrastLightColors && actionContrastDarkColors == other.actionContrastDarkColors && themeMode == other.themeMode && messengerLogoDarkURL == other.messengerLogoDarkURL && launcherLogoURL == other.launcherLogoURL && launcherLogoDarkURL == other.launcherLogoDarkURL
    }

    @objc var primaryForegroundColor: UIColor {
        let light = actionContrastLightColors?.backgroundColor ?? actionColors.backgroundColor
        let dark = actionContrastDarkColors?.backgroundColor ?? actionColors.backgroundColorDark ?? actionColors.backgroundColor
        return UIColor { traits in traits.userInterfaceStyle == .dark ? dark : light }
    }

    @objc var effectiveThemeMode: ThemeMode { themeOverride ?? themeMode }

    @objc(setThemeOverrideWithThemeMode:)
    func setThemeOverride(themeMode: ThemeMode) { themeOverride = themeMode }

    @objc func clearThemeOverride() { themeOverride = nil }

    @objc override init() { fatalError("init() has not been implemented") }
}
