// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate23NewConversationResponse)
class NewConversationResponse: NSObject {
    @objc let cta: NewConversationCTAResponse
    @objc let homeCard: NewConversationHomeCardResponse
    @objc let parts: [ICMConversationPart]
    @objc let participants: [Participant]
    @objc let header: ConversationHeader
    @objc let teamIntro: String?
    @objc let specialNotice: String?

    @objc(initWithDictionary:)
    init?(withDictionary dictionary: [String: Any]) {
        guard let ctaDictionary = dictionary["cta"] as? [String: Any],
              let cta = NewConversationCTAResponse(withDictionary: ctaDictionary),
              let homeDictionary = dictionary["home_card"] as? [String: Any],
              let homeCard = NewConversationHomeCardResponse(withDictionary: homeDictionary),
              let headerDictionary = dictionary["header"] as? [String: Any],
              let header = ConversationHeader(withDictionary: headerDictionary) else { return nil }
        self.cta = cta
        self.homeCard = homeCard
        self.header = header
        parts = (dictionary["parts"] as? [[String: Any]] ?? []).map {
            ICMConversationPart.instance(from: $0, displayStyle: 1)!
        }
        participants = (dictionary["participants"] as? [[String: Any]] ?? []).compactMap {
            Participant(withDictionary: $0)
        }
        teamIntro = dictionary["team_intro"] as? String
        specialNotice = dictionary["special_notice"] as? String
        super.init()
    }

    override init() { fatalError("init() has not been implemented") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return cta == other.cta && homeCard == other.homeCard && parts == other.parts &&
            participants == other.participants && header == other.header &&
            teamIntro == other.teamIntro && specialNotice == other.specialNotice
    }
}
