import UIKit

@objc(_TtC18IntercomSDKPrivate20UnreadMessageDivider)
class UnreadMessageDivider: UIView {
    private lazy var textLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = TranslationManager.messenger.translation(forKey: "new")
        label.textColor = themeProvider.theme.statusNotificationFill
        label.font = .preferredFont(forTextStyle: .caption1)
        return label
    }()
    private lazy var dividerLineLeading: UIView = {
        let line = UIView()
        line.translatesAutoresizingMaskIntoConstraints = false
        line.backgroundColor = themeProvider.theme.statusNotificationFill
        return line
    }()
    private lazy var dividerLineTrailing: UIView = {
        let line = UIView()
        line.translatesAutoresizingMaskIntoConstraints = false
        line.backgroundColor = themeProvider.theme.statusNotificationFill
        return line
    }()
    private let themeProvider: ThemeProvider

    override init(frame: CGRect) {
        themeProvider = DefaultThemeProvider.shared
        super.init(frame: frame)
        setupView()
    }
    required init?(coder: NSCoder) {
        themeProvider = DefaultThemeProvider.shared
        fatalError("init(coder:) has not been implemented")
    }
    private func setupView() {
        addSubview(dividerLineLeading)
        addSubview(textLabel)
        addSubview(dividerLineTrailing)
        let leading: CGFloat = dividerLineLeading.effectiveUserInterfaceLayoutDirection == .rightToLeft ? -0.0 : 0.0
        dividerLineLeading.leadingAnchor.constraint(equalTo: dividerLineLeading.superview!.leadingAnchor, constant: leading).isActive = true
        dividerLineLeading.centerYAnchor.constraint(equalTo: textLabel.centerYAnchor).isActive = true
        dividerLineLeading.heightAnchor.constraint(equalToConstant: 1).isActive = true
        dividerLineLeading.widthAnchor.constraint(equalToConstant: 32).isActive = true
        textLabel.leadingAnchor.constraint(equalTo: dividerLineLeading.trailingAnchor, constant: 16).isActive = true
        textLabel.topAnchor.constraint(equalTo: textLabel.superview!.topAnchor).isActive = true
        textLabel.bottomAnchor.constraint(equalTo: textLabel.superview!.bottomAnchor, constant: -0.0).isActive = true
        textLabel.trailingAnchor.constraint(equalTo: dividerLineTrailing.leadingAnchor, constant: -16).isActive = true
        dividerLineTrailing.centerYAnchor.constraint(equalTo: textLabel.centerYAnchor).isActive = true
        dividerLineTrailing.heightAnchor.constraint(equalToConstant: 1).isActive = true
        let trailing: CGFloat = dividerLineTrailing.effectiveUserInterfaceLayoutDirection == .rightToLeft ? 0.0 : -0.0
        dividerLineTrailing.trailingAnchor.constraint(equalTo: dividerLineTrailing.superview!.trailingAnchor, constant: trailing).isActive = true
    }
}
