import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate26SpaceHomecreenCardViewItem)
class SpaceHomecreenCardViewItem: HomescreenCardViewItem {
    private weak var cardDelegate: (any HomescreenCardDelegate)?
    private let themeProvider: any ThemeProvider
    private var card: SpaceCard
    private var standardOutlineCellItemViews: [StandardOutlineCellItemView] = []
    private let homeMetricService: any HomeMetricServiceProtocol

    init(card: SpaceCard, cardDelegate: (any HomescreenCardDelegate)?, homeMetricService: any HomeMetricServiceProtocol, themeProvider: any ThemeProvider) {
        self.cardDelegate = cardDelegate
        self.themeProvider = themeProvider
        self.card = card
        self.homeMetricService = homeMetricService
    }
    func buildView() -> HomescreenCardView {
        let rows = buildSpaceViews()
        let title = card.title
        return HomescreenCardView(title: title, subviews: rows, themeProvider: DefaultThemeProvider.shared)
    }
    private func buildSpaceViews() -> [StandardOutlineCellItemView] {
        let spaces = card.space
        let rows = spaces.enumerated().map { index, space in
            let accessories = getAccessoryViews(space)
            let title = space.title
            let extra = getAccessoryViewAccessibilityLabel(space)
            var accessibilityLabel = title
            if let extra { accessibilityLabel += "; " + extra }
            let separator = card.space.count >= 2 && index < card.space.count - 1
            let action = { [weak self, space] in
                if self == nil { return }
                let type = space.spaceType
                let destination: HomescreenDestination
                switch type {
                case 0: destination = .messages
                case 1: destination = .helpCenter
                case 2: destination = .tickets
                default: return
                }
                do {
                    let service = self?.homeMetricService
                    if type == 0 { service?.addMessagesSpaceClickedMetric() }
                    else if type == 1 { service?.addHelpSpaceClickedMetric() }
                }
                let delegate = self?.cardDelegate
                delegate?.navigate(to: destination)
            }
            let row = StandardOutlineCellItemView(frame: .zero, title: title, subtitle: nil, accessoryViews: accessories, showSeparator: separator, constrainAccessoriesToSize: CGSize(width: 16, height: 16), accessibilityLabel: accessibilityLabel, themeProvider: DefaultThemeProvider.shared, action: action)
            let font = row.titleLabel.font!
            if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) {
                row.titleLabel.font = UIFont(descriptor: descriptor, size: 0)
            } else { row.titleLabel.font = font }
            return row
        }
        standardOutlineCellItemViews = rows
        return rows
    }
    private func getAccessoryViews(_ space: Space) -> [UIView] {
        switch space.spaceType {
        case 0:
            if let title = space.badge?.title, let count = Int(title) {
                let badge = BadgeView(context: .spaceCardNumbered, themeProvider: themeProvider)
                badge.badge = count
                return [badge]
            }
            return imageAccessory(ICMAssets.messageSpaceIcon()!, withNumberlessBadge: false)
        case 1: return imageAccessory(ICMAssets.helpIcon()!, withNumberlessBadge: false)
        case 2: return imageAccessory(ICMAssets.ticketIcon()!, withNumberlessBadge: space.badge?.badgeType.rawValue == 1)
        default: return []
        }
    }
    private func getAccessoryViewAccessibilityLabel(_ space: Space) -> String? {
        switch space.spaceType {
        case 0:
            guard let title = space.badge?.title else { return nil }
            return TranslationManager.messenger.translation(forKey: "unread_message_count", params: ["unread_count": title])
        case 2: return TranslationManager.messenger.translation(forKey: "tickets_unread")
        default: return nil
        }
    }
    private func imageAccessory(_ image: UIImage, withNumberlessBadge: Bool) -> [UIView] {
        let image = image.imageFlippedForRightToLeftLayoutDirection()
        let view = UIImageView(image: image)
        view.contentMode = .scaleAspectFit
        view.translatesAutoresizingMaskIntoConstraints = false
        view.tintColor = themeProvider.theme.primaryForeground
        if withNumberlessBadge { addNumberlessUnreadBadge(to: view) }
        return [view]
    }
    private func addNumberlessUnreadBadge(to accessory: UIView) {
        let provider = themeProvider
        let badge = BadgeView(context: .spaceCardNumberless, themeProvider: provider)
        accessory.addSubview(badge)
        NSLayoutConstraint.activate([
            badge.centerXAnchor.constraint(equalTo: accessory.trailingAnchor, constant: -3),
            badge.centerYAnchor.constraint(equalTo: accessory.topAnchor, constant: 3)
        ])
        let shape = CAShapeLayer()
        let x: CGFloat = UIView.userInterfaceLayoutDirection(for: accessory.semanticContentAttribute) == .leftToRight ? accessory.bounds.width - 3 : 3
        let path = UIBezierPath(arcCenter: CGPoint(x: x, y: 3), radius: 4, startAngle: 0, endAngle: 2 * .pi, clockwise: false)
        shape.path = path.cgPath
        shape.fillColor = nil
        shape.strokeColor = provider.theme.textDefault.cgColor
        shape.lineWidth = 2
        let mask = CALayer()
        mask.frame = accessory.bounds
        mask.backgroundColor = provider.theme.textDefault.cgColor
        mask.masksToBounds = true
        mask.addSublayer(shape)
        shape.compositingFilter = "xor"
        accessory.layer.mask = mask
    }
    func update(with card: any HomeCard) {
        guard let card = card as? SpaceCard else { return }
        self.card = card
        for (index, space) in card.space.enumerated() {
            if index < standardOutlineCellItemViews.count {
                let accessories = getAccessoryViews(space)
                let row = standardOutlineCellItemViews[index]
                row.updateAccessoryView(accessories)
            }
        }
    }
}
