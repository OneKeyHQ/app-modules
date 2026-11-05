// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import Foundation

struct TicketFormRequestAttribute {
    let descriptorID: String
    let value: Any?
}

@objc(_TtC18IntercomSDKPrivate17TicketFormRequest)
class TicketFormRequest: NSObject {
    @objc let ticketTypeID: String
    let attributes: [TicketFormRequestAttribute]
    @objc var attributesDictionary: [[String: Any]] {
        var dictionaries: [[String: Any]] = []
        for attribute in attributes {
            if let value = attribute.value { dictionaries.append(["descriptor_id": attribute.descriptorID, "value": value]) }
        }
        return dictionaries
    }
    init(ticketTypeID: String, attributes: [TicketFormRequestAttribute]) {
        self.ticketTypeID = ticketTypeID
        self.attributes = attributes
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
}
