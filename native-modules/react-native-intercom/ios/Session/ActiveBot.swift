import Foundation

@objc(_TtC18IntercomSDKPrivate9ActiveBot)
class ActiveBot: NSObject, NSCopying {
    @objc let participant: Participant
    @objc let hasAI: Bool
    @objc let hasCustomizedIdentity: Bool
    @objc var streamingPart: StreamingPart?

    @objc required init(participant: Participant, hasAI: Bool, hasCustomizedIdentity: Bool, streamingPart: StreamingPart?) {
        self.participant = participant
        self.hasAI = hasAI
        self.hasCustomizedIdentity = hasCustomizedIdentity
        self.streamingPart = streamingPart
        super.init()
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let participantDictionary = dictionary["participant"] as? [String: Any],
              let participant = Participant(withDictionary: participantDictionary) else { return nil }
        let hasAI = dictionary["has_ai"] as? Bool ?? false
        let customized = dictionary["has_customized_identity"] as? Bool ?? true
        let streaming = (dictionary["streaming_part"] as? [String: Any]).flatMap { StreamingPart(withDictionary: $0) }
        self.init(participant: participant, hasAI: hasAI, hasCustomizedIdentity: customized, streamingPart: streaming)
    }

    override init() { fatalError("init() has not been implemented") }

    func copy(with zone: NSZone? = nil) -> Any {
        type(of: self).init(participant: participant, hasAI: hasAI,
                           hasCustomizedIdentity: hasCustomizedIdentity, streamingPart: streamingPart)
    }
}
