import Foundation

@objc enum PoweredByIcon: Int, Codable {
    case intercom = 0, fin
    init(from decoder: Decoder) throws {
        let value = try decoder.singleValueContainer().decode(String.self)
        self = value == "fin" ? .fin : .intercom
    }
    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(rawValue)
    }
}

@objc(_TtC18IntercomSDKPrivate9PoweredBy)
class PoweredBy: NSObject, Codable, DictionaryInitializable {
    @objc let icon: PoweredByIcon
    @objc let text: String
    @objc let linkURL: URL

    enum CodingKeys: String, CodingKey { case icon, text, linkURL = "linkUrl" }

    @objc init(icon: PoweredByIcon, text: String, linkURL: URL) {
        self.icon = icon
        self.text = text
        self.linkURL = linkURL
        super.init()
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        icon = try container.decode(PoweredByIcon.self, forKey: .icon)
        text = try container.decode(String.self, forKey: .text)
        linkURL = try container.decode(URL.self, forKey: .linkURL)
        super.init()
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(icon, forKey: .icon)
        try container.encode(text, forKey: .text)
        try container.encode(linkURL, forKey: .linkURL)
    }

    @objc(initWithInstanceFrom:)
    convenience init?(instanceFrom dictionary: [String: Any]) { self.init(withDictionary: dictionary) }

    override init() { fatalError("init() has not been implemented") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return icon == other.icon && text == other.text && linkURL == other.linkURL
    }
}
