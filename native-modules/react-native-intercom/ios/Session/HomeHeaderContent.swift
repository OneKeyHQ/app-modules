// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
import UIKit

enum TextColorType: String { case light, dark }

private func loadHeaderLogo(_ string: String) {
    _ = ITBImageLoader.longTerm()!.loadImage(withURLString: string) { _ in
        NotificationCenter.default.post(name: NSNotification.Name("kICMHomeHeaderLogoLoaded"), object: nil)
    }
}

@objc(_TtC18IntercomSDKPrivate17HomeHeaderContent)
class HomeHeaderContent: NSObject, NSSecureCoding {
    let logoUrl: URL?
    let logoUrlDark: URL?
    let showAvatars: Bool
    let greeting: HomeHeaderLabelDetails?
    let introduction: HomeHeaderLabelDetails?
    let closeButtonDetails: HomeHeaderCloseButtonDetails
    let textColorType: TextColorType
    let textColorTypeDark: TextColorType?
    @objc static var supportsSecureCoding: Bool { true }

    init?(withDictionary dictionary: [String: Any]) {
        showAvatars = dictionary["show_avatars"] as? Bool ?? true
        if let string = dictionary["logo_url"] as? String, let url = URL(string: string) {
            logoUrl = url
            loadHeaderLogo(string)
        } else { logoUrl = nil }
        if let string = dictionary["logo_dark_url"] as? String, let url = URL(string: string) {
            logoUrlDark = url
            loadHeaderLogo(string)
        } else { logoUrlDark = nil }
        greeting = (dictionary["greeting"] as? [String: Any]).flatMap(HomeHeaderLabelDetails.init(withDictionary:))
        introduction = (dictionary["introduction"] as? [String: Any]).flatMap(HomeHeaderLabelDetails.init(withDictionary:))
        closeButtonDetails = (dictionary["close_button"] as? [String: Any]).flatMap(HomeHeaderCloseButtonDetails.init(withDictionary:)) ?? HomeHeaderCloseButtonDetails(foregroundColor: .black, backgroundColor: .systemGray, backgroundOpacity: 0.7)
        textColorType = (dictionary["text_color_type"] as? String).flatMap(TextColorType.init(rawValue:)) ?? .light
        textColorTypeDark = (dictionary["text_color_type_dark"] as? String).flatMap(TextColorType.init(rawValue:))
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let closeButton = coder.decodeObject(of: HomeHeaderCloseButtonDetails.self, forKey: "close_button") else { return nil }
        greeting = coder.decodeObject(of: HomeHeaderLabelDetails.self, forKey: "greeting")
        introduction = coder.decodeObject(of: HomeHeaderLabelDetails.self, forKey: "introduction")
        closeButtonDetails = closeButton
        if let string = coder.decodeObject(of: NSString.self, forKey: "logo_url") as String? {
            logoUrl = URL(string: string)
            loadHeaderLogo(string)
        } else { logoUrl = nil }
        if let string = coder.decodeObject(of: NSString.self, forKey: "logo_dark_url") as String? {
            logoUrlDark = URL(string: string)
            loadHeaderLogo(string)
        } else { logoUrlDark = nil }
        showAvatars = coder.decodeBool(forKey: "show_avatars")
        textColorType = (coder.decodeObject(of: NSString.self, forKey: "text_color_type") as String?).flatMap(TextColorType.init(rawValue:)) ?? .light
        textColorTypeDark = (coder.decodeObject(of: NSString.self, forKey: "text_color_type_dark") as String?).flatMap(TextColorType.init(rawValue:))
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(logoUrl?.absoluteString, forKey: "logo_url")
        coder.encode(logoUrlDark?.absoluteString, forKey: "logo_dark_url")
        coder.encode(showAvatars, forKey: "show_avatars")
        coder.encode(greeting, forKey: "greeting")
        coder.encode(introduction, forKey: "introduction")
        coder.encode(closeButtonDetails, forKey: "close_button")
        coder.encode(textColorType.rawValue, forKey: "text_color_type")
        if let textColorTypeDark { coder.encode(textColorTypeDark.rawValue, forKey: "text_color_type_dark") }
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return logoUrl == other.logoUrl && logoUrlDark == other.logoUrlDark && showAvatars == other.showAvatars && greeting == other.greeting && introduction == other.introduction && closeButtonDetails == other.closeButtonDetails && textColorType == other.textColorType && textColorTypeDark == other.textColorTypeDark
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

@objc(_TtC18IntercomSDKPrivate28HomeHeaderCloseButtonDetails)
class HomeHeaderCloseButtonDetails: NSObject, NSSecureCoding {
    let foregroundColor: UIColor
    let backgroundColor: UIColor
    let backgroundOpacity: Double
    @objc static var supportsSecureCoding: Bool { true }

    init(foregroundColor: UIColor, backgroundColor: UIColor, backgroundOpacity: Double) {
        self.foregroundColor = foregroundColor
        self.backgroundColor = backgroundColor
        self.backgroundOpacity = backgroundOpacity
        super.init()
    }

    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let foreground = dictionary["foreground_color"] as? String,
              let foregroundColor = ICMColor.color(withHexString: foreground),
              let background = dictionary["background_color"] as? String,
              let backgroundColor = ICMColor.color(withHexString: background),
              let opacity = dictionary["background_opacity"] as? Double else { return nil }
        self.init(foregroundColor: foregroundColor, backgroundColor: backgroundColor, backgroundOpacity: opacity)
    }

    required init?(coder: NSCoder) {
        guard let foreground = coder.decodeObject(of: NSString.self, forKey: "foreground_color") as String?,
              let foregroundColor = ICMColor.color(withHexString: foreground),
              let background = coder.decodeObject(of: NSString.self, forKey: "background_color") as String?,
              let backgroundColor = ICMColor.color(withHexString: background),
              let opacity = coder.decodeObject(of: NSNumber.self, forKey: "background_opacity") as? Double else { return nil }
        self.foregroundColor = foregroundColor
        self.backgroundColor = backgroundColor
        backgroundOpacity = opacity
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(ICMColor.hexString(from: foregroundColor), forKey: "foreground_color")
        coder.encode(ICMColor.hexString(from: backgroundColor), forKey: "background_color")
        coder.encode(NSNumber(value: backgroundOpacity), forKey: "background_opacity")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return foregroundColor == other.foregroundColor && backgroundColor == other.backgroundColor && backgroundOpacity == other.backgroundOpacity
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

@objc(_TtC18IntercomSDKPrivate22HomeHeaderLabelDetails)
class HomeHeaderLabelDetails: NSObject, NSSecureCoding {
    let content: String
    let textColor: UIColor
    let opacity: Double
    @objc static var supportsSecureCoding: Bool { true }

    init?(withDictionary dictionary: [String: Any]) {
        guard let content = dictionary["content"] as? String,
              let textColor = dictionary["text_color"] as? String,
              let color = ICMColor.color(withHexString: textColor),
              let opacity = dictionary["opacity"] as? Double else { return nil }
        self.content = content
        self.textColor = color
        self.opacity = opacity
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let content = coder.decodeObject(of: NSString.self, forKey: "content") as String?,
              let textColor = coder.decodeObject(of: NSString.self, forKey: "text_color") as String?,
              let color = ICMColor.color(withHexString: textColor),
              let opacity = coder.decodeObject(of: NSNumber.self, forKey: "opacity") as? Double else { return nil }
        self.content = content
        self.textColor = color
        self.opacity = opacity
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(content, forKey: "content")
        coder.encode(ICMColor.hexString(from: textColor), forKey: "text_color")
        coder.encode(NSNumber(value: opacity), forKey: "opacity")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return content == other.content && textColor == other.textColor && opacity == other.opacity
    }

    @objc override init() { fatalError("init() has not been implemented") }
}
