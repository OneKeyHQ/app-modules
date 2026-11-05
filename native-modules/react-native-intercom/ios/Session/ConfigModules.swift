// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate13ConfigModules)
class ConfigModules: NSObject, NSSecureCoding {
    @objc let home: HomeConfig
    @objc let customizationConfig: CustomizationConfig
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(homeConfig: HomeConfig, customizationConfig: CustomizationConfig) {
        home = homeConfig
        self.customizationConfig = customizationConfig
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let homeDictionary = dictionary["home"] as? [String: Any],
              let home = HomeConfig(withDictionary: homeDictionary),
              let customization = dictionary["customization"] as? [String: Any],
              let config = CustomizationConfig(withDictionary: customization) else { return nil }
        self.init(homeConfig: home, customizationConfig: config)
    }

    required init?(coder: NSCoder) {
        guard let home = coder.decodeObject(of: HomeConfig.self, forKey: "home"),
              let customization = coder.decodeObject(of: CustomizationConfig.self, forKey: "customization") else { return nil }
        self.home = home
        customizationConfig = customization
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(home, forKey: "home")
        coder.encode(customizationConfig, forKey: "customization")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return home == other.home && customizationConfig == other.customizationConfig
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

@objc(_TtC18IntercomSDKPrivate10HomeConfig)
class HomeConfig: NSObject, NSSecureCoding {
    @objc let header: HomeHeader
    @objc let openConfig: OpenConfig
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(homeHeader: HomeHeader, openConfig: OpenConfig) {
        header = homeHeader
        self.openConfig = openConfig
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let headerDictionary = dictionary["header"] as? [String: Any],
              let header = HomeHeader(withDictionary: headerDictionary),
              let openDictionary = dictionary["open_config"] as? [String: Any],
              let open = OpenConfig(withDictionary: openDictionary) else { return nil }
        self.init(homeHeader: header, openConfig: open)
    }

    required init?(coder: NSCoder) {
        guard let header = coder.decodeObject(of: HomeHeader.self, forKey: "header"),
              let open = coder.decodeObject(of: OpenConfig.self, forKey: "open_config") else { return nil }
        self.header = header
        openConfig = open
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(header, forKey: "header")
        coder.encode(openConfig, forKey: "open_config")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return header == other.header && openConfig == other.openConfig
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

private enum HomeHeaderIdentity: String { case aiAgent = "ai_agent", brand }

@objc(_TtC18IntercomSDKPrivate10HomeHeader)
class HomeHeader: NSObject, NSSecureCoding {
    @objc let isExpandedHeader: Bool
    @objc let background: HomeHeaderBackground
    @objc let content: HomeHeaderContent
    private let identity: HomeHeaderIdentity
    @objc static var supportsSecureCoding: Bool { true }

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let backgroundDictionary = dictionary["background"] as? [String: Any],
              let background = HomeHeaderBackground(withDictionary: backgroundDictionary),
              let contentDictionary = dictionary["content"] as? [String: Any],
              let content = HomeHeaderContent(withDictionary: contentDictionary),
              let identityString = dictionary["identity"] as? String,
              let identity = HomeHeaderIdentity(rawValue: identityString) else { return nil }
        self.background = background
        self.content = content
        self.identity = identity
        isExpandedHeader = dictionary["header_expanded"] as? Bool ?? false
        super.init()
    }

    required init?(coder: NSCoder) {
        guard let background = coder.decodeObject(of: HomeHeaderBackground.self, forKey: "background"),
              let content = coder.decodeObject(of: HomeHeaderContent.self, forKey: "content"),
              let identityString = coder.decodeObject(of: NSString.self, forKey: "identity") as String?,
              let identity = HomeHeaderIdentity(rawValue: identityString) else { return nil }
        isExpandedHeader = coder.decodeBool(forKey: "header_expanded")
        self.background = background
        self.content = content
        self.identity = identity
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(isExpandedHeader, forKey: "header_expanded")
        coder.encode(background, forKey: "background")
        coder.encode(content, forKey: "content")
        coder.encode(identity.rawValue, forKey: "identity")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return isExpandedHeader == other.isExpandedHeader && background == other.background && content == other.content && identity == other.identity
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

@objc enum OpeningViewType: Int { case home, messages, help, tickets, conversation }

private func openingViewType(_ value: String) -> OpeningViewType {
    switch value {
    case "messages": return .messages
    case "help": return .help
    case "tickets": return .tickets
    case "conversation": return .conversation
    default: return .home
    }
}

private func openingViewName(_ type: OpeningViewType) -> String {
    switch type.rawValue {
    case 0: return "home"
    case 1: return "messages"
    case 2: return "help"
    case 3: return "tickets"
    case 4: return "conversation"
    default: preconditionFailure("Invalid OpeningViewType")
    }
}

@objc(_TtC18IntercomSDKPrivate10OpenConfig)
class OpenConfig: NSObject, NSSecureCoding {
    @objc let type: OpeningViewType
    @objc let spaces: [Space]
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(type: OpeningViewType, spaces: [Space]) {
        self.type = type
        self.spaces = spaces
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let openTo = dictionary["open_to"] as? String,
              let spaces = dictionary["spaces"] as? [[String: Any]] else { return nil }
        self.init(type: openingViewType(openTo), spaces: spaces.compactMap { Space(withDictionary: $0) })
    }

    required init?(coder: NSCoder) {
        guard let openTo = coder.decodeObject(of: NSString.self, forKey: "open_to") as String?,
              let spaces = coder.decodeObject(of: [NSArray.self, Space.self], forKey: "spaces") as? [Space] else { return nil }
        type = openingViewType(openTo)
        self.spaces = spaces
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(openingViewName(type), forKey: "open_to")
        coder.encode(spaces, forKey: "spaces")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return type == other.type && spaces == other.spaces
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

private func parsedSpaceType(_ value: String) -> Int {
    switch value {
    case "messages": return 0
    case "help": return 1
    case "tickets": return 2
    default: return 3
    }
}

private func spaceName(_ value: Int) -> String {
    switch value {
    case 0: return "messages"
    case 1: return "help"
    case 2: return "tickets"
    default: return "unknown"
    }
}

@objc(_TtC18IntercomSDKPrivate5Space)
class Space: NSObject, NSSecureCoding {
    @objc let spaceType: Int
    @objc let badge: SpaceBadge?
    @objc let title: String
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(spaceType: Int, badge: SpaceBadge?, title: String) {
        self.spaceType = spaceType
        self.badge = badge
        self.title = title
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let type = dictionary["type"] as? String,
              let label = dictionary["label"] as? String else { return nil }
        let badge = (dictionary["badge"] as? [String: Any]).flatMap { SpaceBadge(withDictionary: $0) }
        self.init(spaceType: parsedSpaceType(type), badge: badge, title: label)
    }

    required init?(coder: NSCoder) {
        guard let type = coder.decodeObject(of: NSString.self, forKey: "type") as String?,
              let label = coder.decodeObject(of: NSString.self, forKey: "label") as String? else { return nil }
        spaceType = parsedSpaceType(type)
        title = label
        badge = coder.decodeObject(of: SpaceBadge.self, forKey: "badge")
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(spaceName(spaceType), forKey: "type")
        coder.encode(badge, forKey: "badge")
        coder.encode(title, forKey: "label")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return spaceType == other.spaceType && badge == other.badge && title == other.title
    }

    @objc override init() { fatalError("init() has not been implemented") }
}

@objc enum SpaceBadgeType: Int { case unread, passive }

@objc(_TtC18IntercomSDKPrivate10SpaceBadge)
class SpaceBadge: NSObject, NSSecureCoding {
    @objc let badgeType: SpaceBadgeType
    @objc let title: String?
    @objc static var supportsSecureCoding: Bool { true }

    @objc init(badgeType: SpaceBadgeType, title: String?) {
        self.badgeType = badgeType
        self.title = title
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let type = dictionary["badge_type"] as? String else { return nil }
        if type == "passive" {
            self.init(badgeType: .passive, title: nil)
        } else {
            guard let label = dictionary["label"] as? String else { return nil }
            self.init(badgeType: .unread, title: label)
        }
    }

    required init?(coder: NSCoder) {
        guard let type = coder.decodeObject(of: NSString.self, forKey: "badge_type") as String? else { return nil }
        if type == "passive" {
            badgeType = .passive
            title = nil
        } else {
            guard let label = coder.decodeObject(of: NSString.self, forKey: "label") as String? else { return nil }
            badgeType = .unread
            title = label
        }
        super.init()
    }

    func encode(with coder: NSCoder) {
        let name: String
        switch badgeType.rawValue {
        case 0: name = "unread"
        case 1: name = "passive"
        default: preconditionFailure("Invalid SpaceBadgeType")
        }
        coder.encode(name, forKey: "badge_type")
        coder.encode(title, forKey: "label")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return badgeType == other.badgeType && title == other.title
    }

    @objc override init() { fatalError("init() has not been implemented") }
}
