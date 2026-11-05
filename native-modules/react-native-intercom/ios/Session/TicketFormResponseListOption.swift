// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate28TicketFormResponseListOption)
class TicketFormResponseListOption: NSObject, Codable {
    @objc let label: String
    @objc let archived: Bool
    @objc let id: String
    enum CodingKeys: String, CodingKey { case label, archived, id }
    @objc init(label: String, archived: Bool, id: String) {
        self.label = label
        self.archived = archived
        self.id = id
        super.init()
    }
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        label = try container.decode(String.self, forKey: .label)
        archived = try container.decode(Bool.self, forKey: .archived)
        id = try container.decode(String.self, forKey: .id)
        super.init()
    }
    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(label, forKey: .label)
        try container.encode(archived, forKey: .archived)
        try container.encode(id, forKey: .id)
    }
    override init() { fatalError("init() has not been implemented") }
}
