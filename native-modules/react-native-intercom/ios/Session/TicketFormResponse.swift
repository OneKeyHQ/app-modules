// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import Foundation

struct TicketFormResponseOptions: Codable {
    let multiline: Bool?
    let accept: String?
    let listOptions: [TicketFormResponseListOption]?
}
struct TicketFormResponseFormField: Codable {
    let id: String
    let name: String
    let type: String
    let required: Bool
    let options: TicketFormResponseOptions?
    let identifier: String?
    let isControllingAttribute: Bool
}

@objc(_TtC18IntercomSDKPrivate18TicketFormResponse)
class TicketFormResponse: NSObject, Codable, DictionaryInitializable {
    @objc let ticketTypeId: Int
    @objc let name: String
    let formFields: [TicketFormResponseFormField]
    enum CodingKeys: String, CodingKey { case ticketTypeId, name, formFields }
    required init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        ticketTypeId = try values.decode(Int.self, forKey: .ticketTypeId)
        name = try values.decode(String.self, forKey: .name)
        formFields = try values.decode([TicketFormResponseFormField].self, forKey: .formFields)
        super.init()
    }
    func encode(to encoder: Encoder) throws {
        var values = encoder.container(keyedBy: CodingKeys.self)
        try values.encode(ticketTypeId, forKey: .ticketTypeId)
        try values.encode(name, forKey: .name)
        try values.encode(formFields, forKey: .formFields)
    }
    // Preserve inherited ObjC init while retaining the Swift unimplemented initializer path.
    @nonobjc override init() { fatalError("init() has not been implemented") }
    @objc(initWith:)
    convenience init?(with dictionary: [String: Any]) { self.init(withDictionary: dictionary) }
}
