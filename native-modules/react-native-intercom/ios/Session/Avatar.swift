// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

@objc enum AvatarShape: Int, Codable {
    case circle = 0, squircle
    init(from decoder: Decoder) throws {
        let value = try decoder.singleValueContainer().decode(String.self)
        self = value == "squircle" ? .squircle : .circle
    }
    func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        try container.encode(rawValue)
    }
}

struct AvatarThumbnail: Codable, Hashable {
    let square128: URL?
}

@objc(_TtC18IntercomSDKPrivate6Avatar)
class Avatar: NSObject, Codable, DictionaryInitializable {
    @objc let shape: AvatarShape
    @objc let label: String?
    @objc let initials: String?
    @objc let imageUrl: URL?
    let thumbnails: AvatarThumbnail?
    @objc let imageDarkUrl: URL?
    let thumbnailsDark: AvatarThumbnail?

    enum CodingKeys: String, CodingKey {
        case shape, label, initials, imageUrl, imageDarkUrl, thumbnails, thumbnailsDark
    }

    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        shape = try container.decodeIfPresent(AvatarShape.self, forKey: .shape) ?? .circle
        label = try container.decodeIfPresent(String.self, forKey: .label)
        initials = try container.decodeIfPresent(String.self, forKey: .initials)
        imageUrl = try container.decodeIfPresent(URL.self, forKey: .imageUrl)
        imageDarkUrl = try container.decodeIfPresent(URL.self, forKey: .imageDarkUrl)
        thumbnails = try container.decodeIfPresent(AvatarThumbnail.self, forKey: .thumbnails)
        thumbnailsDark = try container.decodeIfPresent(AvatarThumbnail.self, forKey: .thumbnailsDark)
        super.init()
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(shape, forKey: .shape)
        try container.encodeIfPresent(label, forKey: .label)
        try container.encodeIfPresent(initials, forKey: .initials)
        try container.encodeIfPresent(imageUrl, forKey: .imageUrl)
        try container.encodeIfPresent(imageDarkUrl, forKey: .imageDarkUrl)
        try container.encodeIfPresent(thumbnails, forKey: .thumbnails)
        try container.encodeIfPresent(thumbnailsDark, forKey: .thumbnailsDark)
    }

    @objc(initWithInstanceFrom:)
    convenience init?(instanceFrom dictionary: [String: Any]) { self.init(withDictionary: dictionary) }

    override init() { fatalError("init() has not been implemented") }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return shape == other.shape && label == other.label && initials == other.initials &&
            imageUrl == other.imageUrl && thumbnails == other.thumbnails &&
            imageDarkUrl == other.imageDarkUrl && thumbnailsDark == other.thumbnailsDark
    }

    override var hash: Int {
        var hasher = Hasher()
        hasher.combine(shape.rawValue)
        hasher.combine(label)
        hasher.combine(initials)
        hasher.combine(imageUrl)
        hasher.combine(thumbnails)
        hasher.combine(imageDarkUrl)
        hasher.combine(thumbnailsDark)
        return hasher.finalize()
    }
}
