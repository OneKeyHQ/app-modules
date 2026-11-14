import UIKit

@objc(_TtC18IntercomSDKPrivate19ErrorViewController)
class ErrorViewController: UIViewController {
    @objc weak var errorDelegate: ICMErrorViewDelegate?
    private var actionButton: ICMActionButton?
    private var errorTitle: String?
    private var errorSubtitle: String?
    private let themeProvider: ThemeProvider
    private lazy var iconView: UIImageView = {
        let icon = UIImageView(image: ICMAssets.homescreenFailedImage())
        icon.contentMode = .scaleAspectFit
        icon.tintColor = themeProvider.theme.textDefault
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.heightAnchor.constraint(equalToConstant: 32).isActive = true
        icon.widthAnchor.constraint(equalToConstant: 32).isActive = true
        return icon
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.accessibilityIdentifier = "error view controller title label"
        label.text = errorTitle
        label.textColor = themeProvider.theme.textDefault
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .title3).pointSize, weight: .semibold)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        return label
    }()
    private lazy var subtitleLabel: UILabel = {
        let label = UILabel()
        label.accessibilityIdentifier = "error view controller subtitle label"
        label.text = errorSubtitle
        label.textColor = themeProvider.theme.textDefault
        label.font = .preferredFont(forTextStyle: .callout)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        label.lineBreakMode = .byWordWrapping
        label.numberOfLines = 0
        return label
    }()
    private var containerStackView: UIStackView

    @objc(initWithTitle:subtitle:showActionButton:actionButtonTitle:themeProvider:icon:)
    init(title: String, subtitle: String?, showActionButton: Bool, actionButtonTitle: String?, themeProvider: ThemeProvider, icon: UIImage?) {
        containerStackView = UIStackView()
        containerStackView.axis = .vertical
        containerStackView.alignment = .center
        containerStackView.distribution = .fill
        containerStackView.spacing = 16
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        self.themeProvider = themeProvider
        super.init(nibName: nil, bundle: nil)
        view.accessibilityLabel = "error"
        view.accessibilityIdentifier = "intercom error view controller"
        view.backgroundColor = themeProvider.theme.base
        errorTitle = title
        errorSubtitle = subtitle
        view.addSubview(containerStackView)
        if let icon { iconView.image = icon }
        containerStackView.addArrangedSubview(iconView)
        containerStackView.addArrangedSubview(titleLabel)
        containerStackView.addArrangedSubview(subtitleLabel)
        if showActionButton, let actionButtonTitle {
            actionButton = ICMActionButton()
            actionButton?.configuration?.title = actionButtonTitle
            setupActionButton()
        }
        containerStackView.leadingAnchor.constraint(greaterThanOrEqualTo: view.leadingAnchor, constant: 16).isActive = true
        containerStackView.trailingAnchor.constraint(lessThanOrEqualTo: view.trailingAnchor, constant: 16).isActive = true
        containerStackView.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        containerStackView.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
    }

    @objc(initWithTitle:subtitle:showActionButton:actionButtonTitle:)
    convenience init(title: String, subtitle: String, showActionButton: Bool, actionButtonTitle: String?) {
        self.init(title: title, subtitle: subtitle, showActionButton: showActionButton, actionButtonTitle: actionButtonTitle, themeProvider: DefaultThemeProvider.shared, icon: nil)
    }

    required init?(coder: NSCoder) {
        containerStackView = UIStackView()
        containerStackView.axis = .vertical
        containerStackView.alignment = .center
        containerStackView.distribution = .fill
        containerStackView.spacing = 16
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        fatalError("init(coder:) has not been implemented")
    }

    @available(*, unavailable)
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }

    override func loadView() { view = UIView() }

    private func setupActionButton() {
        guard let actionButton else { return }
        actionButton.configuration?.contentInsets = NSDirectionalEdgeInsets(top: 12, leading: 16, bottom: 12, trailing: 16)
        let background = themeProvider.theme.primaryBackground
        let foregroundTheme = themeProvider.theme
        let foreground = ColorHelper.suitableDynamicForegroundColor(for: foregroundTheme.primaryBackground, theme: foregroundTheme)
        actionButton.configure(forStyle: 0, backgroundColor: background, foregroundColor: foreground)
        actionButton.accessibilityIdentifier = "error view controller action button"
        actionButton.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(didSelectActionButton)))
        containerStackView.addArrangedSubview(actionButton)
    }

    @objc(configureForBackgroundColorWithBackgroundColor:buttonColor:)
    func configureForBackgroundColor(backgroundColor: UIColor, buttonColor: UIColor) {
        view.backgroundColor = backgroundColor
        let foreground = ColorHelper.suitableForegroundColor(for: backgroundColor, theme: themeProvider.theme)
        titleLabel.textColor = foreground
        subtitleLabel.textColor = foreground
        iconView.tintColor = foreground
        if let actionButton {
            let buttonForeground = ColorHelper.suitableForegroundColor(for: buttonColor, theme: themeProvider.theme)
            actionButton.configure(forStyle: 0, backgroundColor: buttonColor, foregroundColor: buttonForeground)
        }
    }

    @objc private func didSelectActionButton() {
        if let errorDelegate, errorDelegate.responds(to: #selector(ICMErrorViewDelegate.didSelectErrorAction)) {
            errorDelegate.didSelectErrorAction?()
        }
    }
}
