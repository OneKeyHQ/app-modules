import UIKit

@objc(_TtC18IntercomSDKPrivate27StandardOutlineCellItemView)
class StandardOutlineCellItemView: UIView {
    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        label.font = .preferredFont(forTextStyle: .callout)
        label.text = title
        label.numberOfLines = 2
        return label
    }()
    private let title: String?
    private let subtitle: String?
    private let themeProvider: any ThemeProvider
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.distribution = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var subtitleLabel: UILabel? = {
        guard let subtitle else { return nil }
        let label = UILabel()
        label.text = subtitle
        label.font = .preferredFont(forTextStyle: .callout)
        label.textColor = themeProvider.theme.textMuted
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        label.numberOfLines = 0
        return label
    }()
    private lazy var labelStackView: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = .vertical
        stack.spacing = 4
        stack.alignment = .leading
        stack.distribution = .fill
        return stack
    }()
    private var accessoryViews: [UIView] = []
    private var action: (() -> Void)?
    // Observed callers use the surviving zero-frame/fixed-size specialization; alternate inputs remain unrecovered.
    init(frame: CGRect, title: String, subtitle: String?, accessoryViews: [UIView], showSeparator: Bool, constrainAccessoriesToSize: CGSize?, accessibilityLabel: String?, themeProvider: any ThemeProvider, action: (() -> Void)?) {
        self.title = title
        self.subtitle = subtitle
        self.accessoryViews = accessoryViews
        self.action = action
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        setupStackView(showSeparator: showSeparator)
        setupAction()
        if showSeparator { setupSeparator() }
        isAccessibilityElement = true
        self.accessibilityLabel = accessibilityLabel
        accessibilityTraits = .button
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupStackView(showSeparator: Bool) {
        addSubview(containerStackView)
        NSLayoutConstraint.activate([
            containerStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16),
            containerStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16),
            containerStackView.topAnchor.constraint(equalTo: topAnchor, constant: 10),
            containerStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: showSeparator ? -11 : -10)
        ])
        for view in [titleLabel, subtitleLabel].compactMap({ $0 }) { labelStackView.addArrangedSubview(view) }
        containerStackView.addArrangedSubview(labelStackView)
        let accessories = accessoryViews
        for accessory in accessories {
            containerStackView.addArrangedSubview(accessory)
            accessory.translatesAutoresizingMaskIntoConstraints = false
            let parent = accessory.superview!
            accessory.translatesAutoresizingMaskIntoConstraints = false
            accessory.translatesAutoresizingMaskIntoConstraints = false
            accessory.centerYAnchor.constraint(equalTo: parent.centerYAnchor).isActive = true
            if let image = accessory as? UIImageView {
                image.widthAnchor.constraint(equalToConstant: 16).isActive = true
                image.heightAnchor.constraint(equalToConstant: 16).isActive = true
            }
        }
    }
    private func setupAction() {
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        addSubview(button)
        NSLayoutConstraint.activate([
            button.leadingAnchor.constraint(equalTo: leadingAnchor), button.trailingAnchor.constraint(equalTo: trailingAnchor),
            button.topAnchor.constraint(equalTo: topAnchor), button.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        button.addTarget(self, action: #selector(performAction), for: .touchUpInside)
    }
    @objc private func performAction() { let action = action; action?() }
    private func setupSeparator() {
        let separator = UIView()
        separator.backgroundColor = themeProvider.theme.neutralContainer
        separator.translatesAutoresizingMaskIntoConstraints = false
        addSubview(separator)
        NSLayoutConstraint.activate([
            separator.heightAnchor.constraint(equalToConstant: 1), separator.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16),
            separator.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16), separator.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }
    func updateAccessoryView(_ views: [UIView]) {
        let arranged = containerStackView.arrangedSubviews
        if let badge = arranged.first(where: { $0 is BadgeView }) {
            containerStackView.removeArrangedSubview(badge)
            badge.removeFromSuperview()
        } else if let image = containerStackView.arrangedSubviews.first(where: { $0 is UIImageView }) {
            containerStackView.removeArrangedSubview(image)
            image.removeFromSuperview()
        }
        for view in views { containerStackView.addArrangedSubview(view) }
    }
}
