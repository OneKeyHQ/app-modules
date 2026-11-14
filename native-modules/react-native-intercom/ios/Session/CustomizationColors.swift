import Foundation
import UIKit

@objc(_TtC18IntercomSDKPrivate19HeaderCustomization)
class HeaderCustomization: NSObject, NSSecureCoding {
    @objc let backgroundColor: UIColor
    @objc let foregroundColor: UIColor
    @objc let backgroundColorDark: UIColor?
    @objc let foregroundColorDark: UIColor?
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(backgroundColor: UIColor, foregroundColor: UIColor, backgroundColorDark: UIColor?, foregroundColorDark: UIColor?) {
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        self.backgroundColorDark = backgroundColorDark
        self.foregroundColorDark = foregroundColorDark
        super.init()
    }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let backgroundColorString = dictionary["background_color"] as? String,
              let backgroundColor = ICMColor.color(withHexString: backgroundColorString),
              let foregroundColorString = dictionary["foreground_color"] as? String,
              let foregroundColor = ICMColor.color(withHexString: foregroundColorString) else { return nil }
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        backgroundColorDark = (dictionary["background_color_dark"] as? String).flatMap { ICMColor.color(withHexString: $0) }
        foregroundColorDark = (dictionary["foreground_color_dark"] as? String).flatMap { ICMColor.color(withHexString: $0) }
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let backgroundColorString = coder.decodeObject(of: NSString.self, forKey: "background_color") as String?,
              let backgroundColor = ICMColor.color(withHexString: backgroundColorString),
              let foregroundColorString = coder.decodeObject(of: NSString.self, forKey: "foreground_color") as String?,
              let foregroundColor = ICMColor.color(withHexString: foregroundColorString) else { return nil }
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        backgroundColorDark = (coder.decodeObject(of: NSString.self, forKey: "background_color_dark") as String?).flatMap { ICMColor.color(withHexString: $0) }
        foregroundColorDark = (coder.decodeObject(of: NSString.self, forKey: "foreground_color_dark") as String?).flatMap { ICMColor.color(withHexString: $0) }
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(ICMColor.hexString(from: backgroundColor), forKey: "background_color")
        coder.encode(ICMColor.hexString(from: foregroundColor), forKey: "foreground_color")
        if let backgroundColorDark { coder.encode(ICMColor.hexString(from: backgroundColorDark), forKey: "background_color_dark") }
        if let foregroundColorDark { coder.encode(ICMColor.hexString(from: foregroundColorDark), forKey: "foreground_color_dark") }
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return backgroundColor == other.backgroundColor && foregroundColor == other.foregroundColor && backgroundColorDark == other.backgroundColorDark && foregroundColorDark == other.foregroundColorDark
    }

    @objc var dynamicBackgroundColor: UIColor {
        UIColor { traits in
            if traits.userInterfaceStyle == .dark, let color = self.backgroundColorDark { return color }
            return self.backgroundColor
        }
    }

    @objc var dynamicForegroundColor: UIColor {
        UIColor { traits in
            if traits.userInterfaceStyle == .dark, let color = self.foregroundColorDark { return color }
            return self.foregroundColor
        }
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

@objc(_TtC18IntercomSDKPrivate12ActionColors)
class ActionColors: NSObject, NSSecureCoding {
    @objc let backgroundColor: UIColor
    @objc let foregroundColor: UIColor
    @objc let foregroundLowContrastColor: UIColor
    @objc let backgroundColorDark: UIColor?
    @objc let foregroundColorDark: UIColor?
    @objc let foregroundLowContrastColorDark: UIColor?
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(backgroundColor: UIColor, foregroundColor: UIColor, foregroundLowContrastColor: UIColor, backgroundColorDark: UIColor?, foregroundColorDark: UIColor?, foregroundLowContrastColorDark: UIColor?) {
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        self.foregroundLowContrastColor = foregroundLowContrastColor
        self.backgroundColorDark = backgroundColorDark
        self.foregroundColorDark = foregroundColorDark
        self.foregroundLowContrastColorDark = foregroundLowContrastColorDark
        super.init()
    }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let backgroundColorString = dictionary["background_color"] as? String,
              let backgroundColor = ICMColor.color(withHexString: backgroundColorString),
              let foregroundColorString = dictionary["foreground_color"] as? String,
              let foregroundColor = ICMColor.color(withHexString: foregroundColorString),
              let foregroundLowContrastColorString = dictionary["foreground_color_low_contrast"] as? String,
              let foregroundLowContrastColor = ICMColor.color(withHexString: foregroundLowContrastColorString) else { return nil }
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        self.foregroundLowContrastColor = foregroundLowContrastColor
        backgroundColorDark = (dictionary["background_color_dark"] as? String).flatMap { ICMColor.color(withHexString: $0) }
        foregroundColorDark = (dictionary["foreground_color_dark"] as? String).flatMap { ICMColor.color(withHexString: $0) }
        foregroundLowContrastColorDark = (dictionary["foreground_color_low_contrast_dark"] as? String).flatMap { ICMColor.color(withHexString: $0) }
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let backgroundColorString = coder.decodeObject(of: NSString.self, forKey: "background_color") as String?,
              let backgroundColor = ICMColor.color(withHexString: backgroundColorString),
              let foregroundColorString = coder.decodeObject(of: NSString.self, forKey: "foreground_color") as String?,
              let foregroundColor = ICMColor.color(withHexString: foregroundColorString),
              let foregroundLowContrastColorString = coder.decodeObject(of: NSString.self, forKey: "foreground_color_low_contrast") as String?,
              let foregroundLowContrastColor = ICMColor.color(withHexString: foregroundLowContrastColorString) else { return nil }
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        self.foregroundLowContrastColor = foregroundLowContrastColor
        backgroundColorDark = (coder.decodeObject(of: NSString.self, forKey: "background_color_dark") as String?).flatMap { ICMColor.color(withHexString: $0) }
        foregroundColorDark = (coder.decodeObject(of: NSString.self, forKey: "foreground_color_dark") as String?).flatMap { ICMColor.color(withHexString: $0) }
        foregroundLowContrastColorDark = (coder.decodeObject(of: NSString.self, forKey: "foreground_color_low_contrast_dark") as String?).flatMap { ICMColor.color(withHexString: $0) }
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(ICMColor.hexString(from: backgroundColor), forKey: "background_color")
        coder.encode(ICMColor.hexString(from: foregroundColor), forKey: "foreground_color")
        coder.encode(ICMColor.hexString(from: foregroundLowContrastColor), forKey: "foreground_color_low_contrast")
        if let backgroundColorDark { coder.encode(ICMColor.hexString(from: backgroundColorDark), forKey: "background_color_dark") }
        if let foregroundColorDark { coder.encode(ICMColor.hexString(from: foregroundColorDark), forKey: "foreground_color_dark") }
        if let foregroundLowContrastColorDark { coder.encode(ICMColor.hexString(from: foregroundLowContrastColorDark), forKey: "foreground_color_low_contrast_dark") }
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return backgroundColor == other.backgroundColor && foregroundColor == other.foregroundColor && foregroundLowContrastColor == other.foregroundLowContrastColor && backgroundColorDark == other.backgroundColorDark && foregroundColorDark == other.foregroundColorDark && foregroundLowContrastColorDark == other.foregroundLowContrastColorDark
    }

    @objc var primaryBackgroundColor: UIColor {
        UIColor { traits in
            if traits.userInterfaceStyle == .dark, let color = self.backgroundColorDark { return color }
            return self.backgroundColor
        }
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

@objc(_TtC18IntercomSDKPrivate20ActionContrastColors)
class ActionContrastColors: NSObject, NSSecureCoding {
    @objc let backgroundColor: UIColor
    @objc let foregroundColor: UIColor
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(backgroundColor: UIColor, foregroundColor: UIColor) {
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        super.init()
    }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let backgroundColorString = dictionary["background_color"] as? String,
              let backgroundColor = ICMColor.color(withHexString: backgroundColorString),
              let foregroundColorString = dictionary["foreground_color"] as? String,
              let foregroundColor = ICMColor.color(withHexString: foregroundColorString) else { return nil }
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let backgroundColorString = coder.decodeObject(of: NSString.self, forKey: "background_color") as String?,
              let backgroundColor = ICMColor.color(withHexString: backgroundColorString),
              let foregroundColorString = coder.decodeObject(of: NSString.self, forKey: "foreground_color") as String?,
              let foregroundColor = ICMColor.color(withHexString: foregroundColorString) else { return nil }
        self.backgroundColor = backgroundColor
        self.foregroundColor = foregroundColor
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(ICMColor.hexString(from: backgroundColor), forKey: "background_color")
        coder.encode(ICMColor.hexString(from: foregroundColor), forKey: "foreground_color")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return backgroundColor == other.backgroundColor && foregroundColor == other.foregroundColor
    }

    @objc override init() { fatalError("init() has not been implemented") }
}
