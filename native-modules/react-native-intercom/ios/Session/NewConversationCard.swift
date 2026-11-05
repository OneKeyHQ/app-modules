import UIKit

@objc(_TtC18IntercomSDKPrivate19NewConversationCard)
class NewConversationCard: NSObject, HomeCard {
    let cardType = HomeCardType.newConversation
    var title: String?
    let subtitle: String?
    let icon: IntercomIcon?
    let label: String?
    let avatarDetails: AvatarDetails?
    required init?(withDictionary dictionary: [String: Any]) {
        title = nil
        guard let raw = dictionary["type"] as? String, let type = HomeCardType(rawValue: raw), type == .newConversation else { return nil }
        subtitle = nil
        icon = nil
        label = nil
        avatarDetails = nil
        super.init()
    }
    @objc(initWithNewConversationHomeCard:)
    init?(withNewConversationHomeCard response: NewConversationHomeCardResponse) {
        title = nil
        label = response.label
        subtitle = response.subtitle
        icon = .some(response.icon)
        avatarDetails = response.avatarDetails
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    var iconViews: [UIView] {
        let avatar: UIView? = avatarDetails.map { AvatarDetailsView(avatarDetails: $0, avatarHeight: 32, overlap: 2.56, activeIndicatorSize: 0) }
        let imageView: UIImageView?
        if let icon, let image = icon.image() { imageView = UIImageView(image: image) }
        else { imageView = nil }
        let spacer: UIView?
        if avatar != nil && imageView != nil {
            let view = UIView()
            view.translatesAutoresizingMaskIntoConstraints = false
            view.widthAnchor.constraint(equalToConstant: 8).isActive = true
            spacer = view
        } else { spacer = nil }
        return [avatar, spacer, imageView].compactMap { $0 }
    }
}
