import Foundation

@_objcRuntimeName(_TtC18IntercomSDKPrivate21FooterNoticeViewModel)
class FooterNoticeViewModel: Hashable {
    let title: String
    let body: String?
    let avatarDetails: AvatarDetails?

    init(title: String, body: String?, avatarDetails: AvatarDetails?) {
        self.title = title
        self.body = body
        self.avatarDetails = avatarDetails
    }
    static func == (lhs: FooterNoticeViewModel, rhs: FooterNoticeViewModel) -> Bool {
        lhs.title == rhs.title && lhs.body == rhs.body && lhs.avatarDetails == rhs.avatarDetails
    }
    func hash(into hasher: inout Hasher) {
        hasher.combine(title)
        hasher.combine(body)
        hasher.combine(avatarDetails)
    }
}
