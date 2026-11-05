import UIKit

@objc(_TtC18IntercomSDKPrivate14TicketInfoView)
class TicketInfoView: UIView {
    private let themeProvider = DefaultThemeProvider.shared
    private lazy var iconView: UIImageView = {
        let image = UIImageView()
        image.image = ICMAssets.ticketIcon()
        image.translatesAutoresizingMaskIntoConstraints = false
        image.heightAnchor.constraint(equalToConstant: 16).isActive = true
        image.widthAnchor.constraint(equalTo: image.heightAnchor).isActive = true
        return image
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        let size = UIFontDescriptor.preferredFontDescriptor(withTextStyle: .footnote).pointSize
        label.font = .systemFont(ofSize: size, weight: .semibold)
        label.lineBreakMode = .byTruncatingTail
        label.setContentCompressionResistancePriority(.init(250), for: .horizontal)
        label.adjustsFontForContentSizeCategory = true
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.isLayoutMarginsRelativeArrangement = true
        stack.spacing = 5
        stack.clipsToBounds = true
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var backgroundView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.clipsToBounds = true
        view.layer.cornerRadius = 10
        return view
    }()
    private var ticketTintColor: UIColor? = DefaultThemeProvider.shared.theme.textDefault
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .clear
        isAccessibilityElement = true
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(backgroundView)
        addSubview(stackView)
        NSLayoutConstraint.activate([
            stackView.leadingAnchor.constraint(equalTo: leadingAnchor),
            stackView.topAnchor.constraint(equalTo: topAnchor),
            stackView.bottomAnchor.constraint(equalTo: bottomAnchor),
            stackView.trailingAnchor.constraint(lessThanOrEqualTo: trailingAnchor),
            backgroundView.leadingAnchor.constraint(equalTo: stackView.leadingAnchor),
            backgroundView.trailingAnchor.constraint(equalTo: stackView.trailingAnchor),
            backgroundView.topAnchor.constraint(equalTo: stackView.topAnchor),
            backgroundView.bottomAnchor.constraint(equalTo: stackView.bottomAnchor)
        ])
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    func configure(with ticket: Ticket) {
        titleLabel.text = ticket.title
        backgroundView.backgroundColor = .clear
        titleLabel.textColor = themeProvider.theme.textMuted
        iconView.tintColor = themeProvider.theme.textMuted
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { configureAccessibilityLayout() }
        else { configureRegularLayout() }
        titleLabel.font = .preferredFont(forTextStyle: .subheadline)
        accessibilityLabel = "Ticket title: " + ticket.title + ", ticket status: " + ticket.currentStatus.title + ". "
    }
    private func configureAccessibilityLayout() {
        let stack = stackView
        stack.axis = .vertical
        stack.alignment = .leading
        stack.spacing = ICMUtils.scale(toAccessibilitySizing: 12)
        stack.layoutMargins = .zero
        backgroundView.isHidden = true
        stack.removeAllArrangedSubviews()
        stack.addArrangedSubview(titleLabel)
    }
    private func configureRegularLayout() {
        let stack = stackView
        stack.axis = .horizontal
        stack.alignment = .center
        stack.spacing = ICMUtils.scale(toAccessibilitySizing: 4)
        stack.layoutMargins = .zero
        backgroundView.isHidden = false
        stack.removeAllArrangedSubviews()
        stack.addArrangedSubview(iconView)
        stack.addArrangedSubview(titleLabel)
    }
}
