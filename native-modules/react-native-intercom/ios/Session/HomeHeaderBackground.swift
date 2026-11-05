import Foundation
import UIKit

enum HomeHeaderBackgroundType: String { case solid, colors, image }

@objc(_TtC18IntercomSDKPrivate20HomeHeaderBackground)
class HomeHeaderBackground: NSObject, NSSecureCoding {
    let type: HomeHeaderBackgroundType
    @objc let color: UIColor
    @objc let fadeToWhite: Bool
    @objc let imageUrl: URL?
    @objc let imageDarkUrl: URL?
    @objc let gradient: [UIColor]?
    @objc let enabled: Bool
    let typeDark: HomeHeaderBackgroundType?
    @objc let fadeToDark: Bool
    @objc let colorDark: UIColor?
    @objc let gradientDark: [UIColor]?
    @objc static var supportsSecureCoding: Bool { true }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let typeName = dictionary["type"] as? String,
              let type = HomeHeaderBackgroundType(rawValue: typeName),
              let colorString = dictionary["color"] as? String,
              let color = ICMColor.color(withHexString: colorString) else { return nil }
        self.type = type
        self.color = color
        enabled = dictionary["enabled"] as? Bool ?? false
        imageUrl = (dictionary["image_url"] as? String).flatMap(URL.init(string:))
        imageDarkUrl = (dictionary["image_dark_url"] as? String).flatMap(URL.init(string:))
        let lightGradient = (dictionary["gradient"] as? [String])?.compactMap { ICMColor.color(withHexString: $0) }
        gradient = lightGradient.flatMap { $0.isEmpty ? nil : $0 }
        fadeToWhite = dictionary["fade_to_white"] as? Bool ?? false
        typeDark = (dictionary["type_dark"] as? String).flatMap(HomeHeaderBackgroundType.init(rawValue:))
        fadeToDark = dictionary["fade_to_dark"] as? Bool ?? false
        colorDark = (dictionary["color_dark"] as? String).flatMap { ICMColor.color(withHexString: $0) }
        let darkGradient = (dictionary["gradient_dark"] as? [String])?.compactMap { ICMColor.color(withHexString: $0) }
        gradientDark = darkGradient.flatMap { $0.isEmpty ? nil : $0 }
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let typeName = coder.decodeObject(of: NSString.self, forKey: "type") as String?,
              let colorString = coder.decodeObject(of: NSString.self, forKey: "color") as String?,
              let color = ICMColor.color(withHexString: colorString) else { return nil }
        type = HomeHeaderBackgroundType(rawValue: typeName) ?? .solid
        self.color = color
        fadeToWhite = coder.decodeBool(forKey: "fade_to_white")
        imageUrl = (coder.decodeObject(of: NSString.self, forKey: "image_url") as String?).flatMap(URL.init(string:))
        imageDarkUrl = (coder.decodeObject(of: NSString.self, forKey: "image_dark_url") as String?).flatMap(URL.init(string:))
        gradient = (coder.decodeObject(of: [NSArray.self, NSString.self], forKey: "gradient") as? [String])?.compactMap { ICMColor.color(withHexString: $0) }
        enabled = coder.decodeBool(forKey: "enabled")
        typeDark = (coder.decodeObject(of: NSString.self, forKey: "type_dark") as String?).flatMap(HomeHeaderBackgroundType.init(rawValue:))
        fadeToDark = coder.decodeBool(forKey: "fade_to_dark")
        colorDark = (coder.decodeObject(of: NSString.self, forKey: "color_dark") as String?).flatMap { ICMColor.color(withHexString: $0) }
        gradientDark = (coder.decodeObject(of: [NSArray.self, NSString.self], forKey: "gradient_dark") as? [String])?.compactMap { ICMColor.color(withHexString: $0) }
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(type.rawValue, forKey: "type")
        coder.encode(ICMColor.hexString(from: color), forKey: "color")
        coder.encode(fadeToWhite, forKey: "fade_to_white")
        coder.encode(imageUrl?.absoluteString, forKey: "image_url")
        coder.encode(imageDarkUrl?.absoluteString, forKey: "image_dark_url")
        if let gradient { coder.encode(gradient.map { ICMColor.hexString(from: $0) }, forKey: "gradient") }
        coder.encode(enabled, forKey: "enabled")
        if let typeDark { coder.encode(typeDark.rawValue, forKey: "type_dark") }
        coder.encode(fadeToDark, forKey: "fade_to_dark")
        if let colorDark { coder.encode(ICMColor.hexString(from: colorDark), forKey: "color_dark") }
        if let gradientDark { coder.encode(gradientDark.map { ICMColor.hexString(from: $0) }, forKey: "gradient_dark") }
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return type == other.type && color == other.color && fadeToWhite == other.fadeToWhite && imageUrl == other.imageUrl && imageDarkUrl == other.imageDarkUrl && gradient == other.gradient && enabled == other.enabled && typeDark == other.typeDark && fadeToDark == other.fadeToDark && colorDark == other.colorDark && gradientDark == other.gradientDark
    }

    @objc(fadeFor:)
    func fade(for traits: UITraitCollection) -> Bool {
        traits.userInterfaceStyle == .dark ? fadeToDark : fadeToWhite
    }

    @objc var dynamicBackground: UIColor {
        UIColor { traits in
            if traits.userInterfaceStyle == .dark, let color = self.colorDark { return color }
            return self.color
        }
    }

    @objc(gradientFor:)
    func gradient(for traits: UITraitCollection) -> [UIColor]? {
        if traits.userInterfaceStyle == .dark, let gradientDark { return gradientDark }
        return gradient
    }

    @objc override init() { fatalError("init() has not been implemented") }
}
