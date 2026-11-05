import UIKit

@objc(_TtC18IntercomSDKPrivate11TicketBlock)
class TicketBlock: ITBBlock {
    @objc var ticketTypeTitle: String
    @objc var title: String
    @objc var ticketType: TicketType

    override init!(dictionary: [AnyHashable: Any]!, appearance: any ITBBlockAppearance) {
        guard let ticketTypeTitle = dictionary?["ticket_type_title"] as? String,
              let title = dictionary?["title"] as? String,
              let typeDictionary = dictionary?["ticket_type"] as? [String: Any],
              let ticketType = TicketType(withDictionary: typeDictionary),
              ITBBlock.validate(dictionary) else { return nil }
        self.ticketTypeTitle = ticketTypeTitle
        self.title = title
        self.ticketType = ticketType
        super.init(dictionary: dictionary, appearance: appearance)
    }

    required init?(coder: NSCoder) {
        guard let ticketTypeTitle = coder.decodeObject(of: NSString.self, forKey: "ticketTypeTitle") as String?,
              let title = coder.decodeObject(of: NSString.self, forKey: "title") as String?,
              let ticketType = coder.decodeObject(of: TicketType.self, forKey: "ticketType") else { return nil }
        self.ticketTypeTitle = ticketTypeTitle
        self.title = title
        self.ticketType = ticketType
        super.init(coder: coder)
    }

    override class var supportsSecureCoding: Bool { true }
    override func type() -> Int { 22 }
    override func viewClass() -> AnyClass! { TicketBlockView.self }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? TicketBlock else { return false }
        guard other.ticketTypeTitle == ticketTypeTitle,
              other.title == title,
              other.ticketType == ticketType else { return false }
        return super.isEqual(object)
    }

    override func copy(with zone: NSZone? = nil) -> Any {
        guard let result = super.copy(with: zone) as? TicketBlock else { return Optional<TicketBlock>.none as Any }
        result.ticketTypeTitle = ticketTypeTitle
        result.title = title
        result.ticketType = ticketType
        return Optional.some(result) as Any
    }

    override func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(ticketTypeTitle, forKey: "ticketTypeTitle")
        coder.encode(title, forKey: "title")
        coder.encode(ticketType, forKey: "ticketType")
    }
}
