import UIKit

// Raw values and roles are recovered; original private case spellings were optimized away.
@objc enum BadgeViewContext: Int {
    case launcher = 0, spaceCardNumberless, spaceCardNumbered, navigationItemNumbered
}

@objc(_TtC18IntercomSDKPrivate9BadgeView)
class BadgeView: UIView {
    @objc var badge = 0 {
        didSet {
            if badge < 1 { alpha = 0 }
            else if context == .launcher {
                ICMLauncherAnimationUtils.animate({ self.alpha = 1; self.transform = .identity }, completion: nil)
            } else if context != .spaceCardNumberless { alpha = 1 }
            let accessibility = ICMUtils.contentSizeCategoryIsAccessibilityCategory()
            if context == .spaceCardNumberless { return }
            if context == .navigationItemNumbered && accessibility { return }
            let label = badgeLabel
            let count = badge
            label.text = count >= 10 ? "9+" : String(count)
        }
    }
    private lazy var badgeLabel: UILabel = {
        let label = UILabel()
        let provider = themeProvider
        label.textColor = provider.theme.textInversed
        label.backgroundColor = provider.theme.statusNotificationFill
        switch context {
        case .launcher: label.font = ICMUtils.font(ofSize: 14, weight: 2)
        case .spaceCardNumberless: break
        case .spaceCardNumbered:
            let font = UIFont.preferredFont(forTextStyle: .caption2)
            if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) {
                label.font = UIFont(descriptor: descriptor, size: 0)
            } else { label.font = font }
            label.adjustsFontForContentSizeCategory = true
        case .navigationItemNumbered:
            label.font = ICMUtils.font(ofSize: 11, weight: 1)
            label.adjustsFontForContentSizeCategory = false
        }
        label.textAlignment = .center
        label.accessibilityIdentifier = "unread badge label"
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private let context: BadgeViewContext
    private let themeProvider: ThemeProvider

    @objc init(context: BadgeViewContext, themeProvider: ThemeProvider) {
        self.context = context
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        backgroundColor = themeProvider.theme.statusNotificationFill
        clipsToBounds = true
        switch context {
        case .launcher: accessibilityIdentifier = "intercom launcher unread badge"
        case .spaceCardNumberless: accessibilityIdentifier = "intercom space card numberless badge"
        case .spaceCardNumbered: accessibilityIdentifier = "intercom space card numbered badge"
        case .navigationItemNumbered: accessibilityIdentifier = "intercom navigation item numbered badge"
        }
        translatesAutoresizingMaskIntoConstraints = false
        addSubviews()
        if context == .launcher { transform = CGAffineTransform(scaleX: 0.8, y: 0.8) }
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    override func layoutSubviews() {
        super.layoutSubviews()
        let layer = layer
        let width = bounds.width
        let height = bounds.height
        layer.cornerRadius = (height < width ? height : width) * 0.5
    }

    private func addSubviews() {
        let context = context
        if context != .spaceCardNumberless {
            addSubview(badgeLabel)
            NSLayoutConstraint.activate([
                badgeLabel.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 3),
                badgeLabel.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -3),
                badgeLabel.topAnchor.constraint(equalTo: topAnchor),
                badgeLabel.bottomAnchor.constraint(equalTo: bottomAnchor)
            ])
        }
        switch context {
        case .launcher:
            NSLayoutConstraint.activate([widthAnchor.constraint(greaterThanOrEqualToConstant: 22), heightAnchor.constraint(greaterThanOrEqualToConstant: 22)])
        case .spaceCardNumberless:
            NSLayoutConstraint.activate([widthAnchor.constraint(equalToConstant: 8), heightAnchor.constraint(equalToConstant: 8)])
        case .spaceCardNumbered, .navigationItemNumbered:
            NSLayoutConstraint.activate([widthAnchor.constraint(greaterThanOrEqualToConstant: 16), heightAnchor.constraint(greaterThanOrEqualToConstant: 16)])
        }
        widthAnchor.constraint(greaterThanOrEqualTo: heightAnchor).isActive = true
    }
}
