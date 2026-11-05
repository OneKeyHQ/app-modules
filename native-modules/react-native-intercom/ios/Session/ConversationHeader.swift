// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate18ConversationHeader)
class ConversationHeader: NSObject {
    @objc let useBotHeader: Bool
    @objc let backgroundColor: UIColor
    @objc let foregroundPrimaryColor: UIColor
    @objc let foregroundSecondaryColor: UIColor
    @objc let displayActiveIndicator: Bool
    @objc var expanded: ExpandedConversationHeader?
    @objc let collapsed: CollapsedConversationHeader
    @objc let hideGreeting: Bool

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let useBotHeader = dictionary["use_bot_header"] as? Bool,
              let background = dictionary["background_color"] as? String,
              let primary = dictionary["foreground_primary_color"] as? String,
              let secondary = dictionary["foreground_secondary_color"] as? String,
              let backgroundColor = ICMColor.color(withHexString: background),
              let foregroundPrimaryColor = ICMColor.color(withHexString: primary),
              let foregroundSecondaryColor = ICMColor.color(withHexString: secondary),
              let displayActiveIndicator = dictionary["display_active_indicator"] as? Bool,
              let collapsedDictionary = dictionary["collapsed"] as? [String: Any],
              let collapsed = CollapsedConversationHeader(withDictionary: collapsedDictionary),
              let hideGreeting = dictionary["hide_greeting"] as? Bool else { return nil }
        if let expandedDictionary = dictionary["expanded"] as? [String: Any] {
            expanded = ExpandedConversationHeader(withDictionary: expandedDictionary)
        } else { expanded = nil }
        self.useBotHeader = useBotHeader
        self.backgroundColor = backgroundColor
        self.foregroundPrimaryColor = foregroundPrimaryColor
        self.foregroundSecondaryColor = foregroundSecondaryColor
        self.displayActiveIndicator = displayActiveIndicator
        self.collapsed = collapsed
        self.hideGreeting = hideGreeting
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }

    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(useBotHeader)
        hasher.combine(displayActiveIndicator)
        hasher.combine(expanded)
        hasher.combine(collapsed)
        hasher.combine(hideGreeting)
        return hasher.finalize()
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return useBotHeader == other.useBotHeader && displayActiveIndicator == other.displayActiveIndicator &&
            expanded == other.expanded && collapsed == other.collapsed && hideGreeting == other.hideGreeting
    }
}
