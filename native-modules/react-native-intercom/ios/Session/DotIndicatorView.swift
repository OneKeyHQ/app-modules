import UIKit

struct TypingAdminDetails: Hashable {
    let adminName: String
    let adminImageUrl: String?
    let avatarShape: AvatarShape
}

@objc(_TtC18IntercomSDKPrivate16DotIndicatorView)
class DotIndicatorView: UIView {
    enum Context { case inbox, conversation }
    private enum Constants {
        static let dotSize = UIFontMetrics.default.scaledValue(for: 8)
        static let dotSpacing = UIFontMetrics.default.scaledValue(for: 4)
        static let backgroundPadding = UIFontMetrics.default.scaledValue(for: 16)
        static let cornerRadius = UIFontMetrics.default.scaledValue(for: 10)
        static let stackSpacing = UIFontMetrics.default.scaledValue(for: 12)
        static let delays = [0.0, 0.19999999999999998, 0.39999999999999997]
    }
    private let context: Context
    private let horizontalStackView: UIStackView
    lazy var avatar: ICMAvatarView = ICMAvatarView(noTeammatePresenceWithAvatarDiameter: context == .conversation ? 36 : 32)
    private let dataManager: ICMDataManagerProtocol
    private let dots: [UIView]
    private let backgroundView: UIView
    private let themeProvider: DefaultThemeProvider

    init(frame: CGRect, context: Context, dataManager: ICMDataManagerProtocol) {
        horizontalStackView = UIStackView()
        horizontalStackView.axis = .horizontal
        horizontalStackView.spacing = Constants.stackSpacing
        horizontalStackView.alignment = .center
        dots = [UIView(), UIView(), UIView()]
        backgroundView = UIView()
        themeProvider = .shared
        self.context = context
        self.dataManager = dataManager
        super.init(frame: frame)
        setupView()
        startAnimating()
        NotificationCenter.default.addObserver(self, selector: #selector(dynamicTypeDidChange), name: UIContentSizeCategory.didChangeNotification, object: nil)
    }

    required init?(coder: NSCoder) {
        horizontalStackView = UIStackView()
        horizontalStackView.axis = .horizontal
        horizontalStackView.spacing = Constants.stackSpacing
        horizontalStackView.alignment = .center
        dots = [UIView(), UIView(), UIView()]
        backgroundView = UIView()
        themeProvider = .shared
        fatalError("init(coder:) has not been implemented")
    }

    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    deinit { NotificationCenter.default.removeObserver(self) }

    override var intrinsicContentSize: CGSize {
        switch context {
        case .inbox:
            return CGSize(width: 32 + Constants.stackSpacing + 3 * Constants.dotSize + 2 * Constants.dotSpacing, height: fmax(Constants.dotSize, 32))
        case .conversation:
            let height = Constants.dotSize + 2 * Constants.backgroundPadding
            return CGSize(width: 36 + Constants.stackSpacing + 3 * Constants.dotSize + 2 * Constants.dotSpacing + 2 * Constants.backgroundPadding, height: 36 <= height ? height : 36)
        }
    }

    func configure(with details: TypingAdminDetails) {
        horizontalStackView.insertArrangedSubview(avatar, at: 0)
        let diameter: CGFloat = context == .conversation ? 36 : 32
        avatar.translatesAutoresizingMaskIntoConstraints = false
        avatar.widthAnchor.constraint(equalToConstant: diameter).isActive = true
        avatar.translatesAutoresizingMaskIntoConstraints = false
        avatar.heightAnchor.constraint(equalToConstant: diameter).isActive = true
        let url = details.adminImageUrl.flatMap(URL.init(string:))
        avatar.loadAvatar(with: url, userName: details.adminName, avatarShape: details.avatarShape.rawValue)
    }

    private func setupView() {
        addSubview(horizontalStackView)
        horizontalStackView.translatesAutoresizingMaskIntoConstraints = false
        let leading: CGFloat = effectiveUserInterfaceLayoutDirection == .rightToLeft ? -0.0 : 0.0
        horizontalStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: leading).isActive = true
        NSLayoutConstraint(item: horizontalStackView, attribute: .centerY, relatedBy: .equal, toItem: self, attribute: .centerY, multiplier: 1, constant: 0).isActive = true
        for dot in dots {
            dot.backgroundColor = themeProvider.theme.iconMuted
            dot.layer.cornerRadius = Constants.dotSize * 0.5
            dot.translatesAutoresizingMaskIntoConstraints = false
            dot.widthAnchor.constraint(equalToConstant: Constants.dotSize).isActive = true
            dot.heightAnchor.constraint(equalToConstant: Constants.dotSize).isActive = true
        }
        let stack = UIStackView(arrangedSubviews: dots)
        stack.axis = .horizontal
        stack.spacing = Constants.dotSpacing
        if context == .conversation {
            horizontalStackView.addArrangedSubview(backgroundView)
            backgroundView.backgroundColor = themeProvider.theme.neutralContainer
            backgroundView.addSubview(stack)
            stack.translatesAutoresizingMaskIntoConstraints = false
            stack.topAnchor.constraint(equalTo: backgroundView.topAnchor, constant: Constants.backgroundPadding).isActive = true
            stack.leadingAnchor.constraint(equalTo: backgroundView.leadingAnchor, constant: Constants.backgroundPadding).isActive = true
            stack.bottomAnchor.constraint(equalTo: backgroundView.bottomAnchor, constant: -Constants.backgroundPadding).isActive = true
            stack.trailingAnchor.constraint(equalTo: backgroundView.trailingAnchor, constant: -Constants.backgroundPadding).isActive = true
        } else { horizontalStackView.addArrangedSubview(stack) }
    }

    private func startAnimating() {
        for (index, dot) in dots.enumerated() {
            let animation = CABasicAnimation(keyPath: "opacity")
            animation.fromValue = NSNumber(value: 1.0)
            animation.toValue = NSNumber(value: 0.1)
            animation.duration = 0.6
            animation.autoreverses = true
            animation.repeatCount = .infinity
            animation.beginTime = CACurrentMediaTime() + Constants.delays[index]
            dot.layer.add(animation, forKey: "typingIndicator")
        }
    }

    override func didMoveToSuperview() {
        super.didMoveToSuperview()
        if superview != nil && !isHidden {
            for dot in dots { dot.layer.removeAllAnimations() }
            startAnimating()
        }
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        layoutIfNeeded()
        backgroundView.applyCornerRadiusForGrouping(3, bubbleType: .admin, bubbleRadius: BubbleRadius(large: Constants.cornerRadius, small: Constants.cornerRadius), supportsCornerConfiguration: UIView.supportsCornerConfiguration)
    }

    @objc private func dynamicTypeDidChange() {
        for child in subviews { child.removeFromSuperview() }
        setupView()
        invalidateIntrinsicContentSize()
    }
}
