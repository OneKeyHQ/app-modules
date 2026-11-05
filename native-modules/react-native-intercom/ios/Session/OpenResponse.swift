// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate12OpenResponse)
class OpenResponse: NSObject {
    @objc let composerState: ICMComposerState?
    @objc let composerSuggestions: ICMComposerSuggestions
    @objc let newConversation: NewConversationResponse
    @objc let requestId: String?
    @objc let poweredBy: PoweredBy?
    @objc let privacyPolicyNotice: PrivacyPolicyNotice?
    @objc let recentOpenInboundConversation: ICMConversation?
    @objc let teamPresenceDictionary: [String: Any]?
    @objc let resolutionBotBehaviorVersionId: NSNumber?
    @objc let botIntroId: String?

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let suggestions = dictionary["composer_suggestions"] as? [String: Any],
              let newConversationDictionary = dictionary["new_conversation"] as? [String: Any],
              let newConversation = NewConversationResponse(withDictionary: newConversationDictionary) else {
            return nil
        }
        composerSuggestions = ICMComposerSuggestions.instance(from: suggestions)
        self.newConversation = newConversation
        requestId = dictionary["request_id"] as? String
        if let state = dictionary["composer_state"] as? [String: Any] {
            composerState = ICMComposerState.instance(from: state)
        } else { composerState = nil }
        if let poweredByDictionary = dictionary["powered_by"] as? [String: Any] {
            poweredBy = PoweredBy(instanceFrom: poweredByDictionary)
        } else { poweredBy = nil }
        if let privacy = dictionary["privacy_policy_notice"] as? [String: Any] {
            privacyPolicyNotice = PrivacyPolicyNotice(withDictionary: privacy)
        } else { privacyPolicyNotice = nil }
        if let recent = dictionary["recent_open_inbound_conversation"] as? [String: Any] {
            recentOpenInboundConversation = ICMConversation.instance(from: recent, partDisplayStyle: 1)
        } else { recentOpenInboundConversation = nil }
        teamPresenceDictionary = dictionary["team_presence"] as? [String: Any]
        resolutionBotBehaviorVersionId = dictionary["resolution_bot_behavior_version_id"] as? NSNumber
        botIntroId = dictionary["bot_intro_id"] as? String
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return composerSuggestions == other.composerSuggestions &&
            newConversation == other.newConversation &&
            composerState == other.composerState && requestId == other.requestId &&
            poweredBy == other.poweredBy && privacyPolicyNotice == other.privacyPolicyNotice &&
            teamPresenceDictionary.map { $0 as NSDictionary } == other.teamPresenceDictionary.map { $0 as NSDictionary } &&
            resolutionBotBehaviorVersionId == other.resolutionBotBehaviorVersionId && botIntroId == other.botIntroId
    }
}
