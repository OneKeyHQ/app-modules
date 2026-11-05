// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate29TicketsListViewEmptyStateView)
class TicketsListViewEmptyStateView: UIView {
    let themeProvider = DefaultThemeProvider.shared
    private lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .center
        stack.spacing = 12
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var iconView: UIImageView = {
        let icon = UIImageView()
        icon.image = ICMAssets.ticketIcon()
        icon.tintColor = themeProvider.theme.textDefault
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.widthAnchor.constraint(equalToConstant: 32).isActive = true
        icon.heightAnchor.constraint(equalTo: icon.widthAnchor).isActive = true
        return icon
    }()
    private lazy var titleLabel = makeLabel(key: "tickets_empty_state_title", style: .headline)
    private lazy var descriptionLabel = makeLabel(key: "tickets_empty_state_text", style: .subheadline)
    override init(frame: CGRect) {
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        stackView.addArrangedSubview(iconView)
        stackView.addArrangedSubview(titleLabel)
        stackView.addArrangedSubview(descriptionLabel)
        addSubview(stackView)
        stackView.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        stackView.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor).isActive = true
        stackView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func makeLabel(key: String, style: UIFont.TextStyle) -> UILabel {
        let label = UILabel()
        label.text = TranslationManager.messenger.translation(forKey: key)
        label.textColor = themeProvider.theme.textDefault
        label.font = .preferredFont(forTextStyle: style)
        label.numberOfLines = 0
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }
}
