import UIKit

@objc(_TtC18IntercomSDKPrivate16HeaderIconButton)
class HeaderIconButton: UIButton {
    private let themeProvider: any ThemeProvider
    private let hitAreaInset: CGFloat
    private var isPointerHovering = false
    private(set) var iconView: UIImageView?
    private var iconSizeConstraints: [NSLayoutConstraint] = []
    private var buttonSizeConstraints: [NSLayoutConstraint] = []

    init(image: UIImage?, themeProvider: any ThemeProvider) {
        self.themeProvider = themeProvider
        hitAreaInset = 6
        super.init(frame: .zero)
        setupChrome()
        setupImage(image)
    }

    init(symbolName: String, themeProvider: any ThemeProvider) {
        self.themeProvider = themeProvider
        hitAreaInset = 4
        super.init(frame: .zero)
        setupChrome()
        setupSymbol(symbolName)
    }

    private func setupSymbol(_ name: String) {
        let symbol = UIImage.SymbolConfiguration(pointSize: 16, weight: .regular)
        var configuration = self.configuration
        configuration?.image = UIImage(systemName: name, withConfiguration: symbol)
        self.configuration = configuration
        configuration = self.configuration
        configuration?.baseForegroundColor = themeProvider.theme.iconMuted
        self.configuration = configuration
        translatesAutoresizingMaskIntoConstraints = false
        translatesAutoresizingMaskIntoConstraints = false
        let width = widthAnchor.constraint(equalToConstant: 36)
        width.priority = .required
        width.isActive = true
        translatesAutoresizingMaskIntoConstraints = false
        let height = heightAnchor.constraint(equalToConstant: 36)
        height.priority = .required
        height.isActive = true
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func setupChrome() {
        var configuration = UIButton.Configuration.plain()
        configuration.background.cornerRadius = 8
        configuration.background.backgroundColor = .clear
        configuration.contentInsets = .zero
        self.configuration = configuration
        isPointerInteractionEnabled = true
        configurationUpdateHandler = { [weak self] button in
            guard let self = self else { return }
            let active = button.isHighlighted || self.isPointerHovering
            var configuration = button.configuration
            configuration?.background.backgroundColor = active ? self.themeProvider.theme.neutralContainer : .clear
            button.configuration = configuration
        }
        addGestureRecognizer(UIHoverGestureRecognizer(target: self, action: #selector(handleHover(_:))))
    }

    @objc private func handleHover(_ gesture: UIHoverGestureRecognizer) {
        isPointerHovering = gesture.state == .began || gesture.state == .changed
        setNeedsUpdateConfiguration()
    }

    private func setupImage(_ image: UIImage?) {
        let view = UIImageView()
        view.contentMode = .scaleAspectFit
        view.tintColor = themeProvider.theme.iconMuted
        view.isUserInteractionEnabled = false
        view.translatesAutoresizingMaskIntoConstraints = false
        iconView = view
        view.image = image?.withRenderingMode(.alwaysTemplate)
        addSubview(view)
        iconSizeConstraints = [view.widthAnchor.constraint(equalToConstant: 16), view.heightAnchor.constraint(equalToConstant: 16)]
        NSLayoutConstraint.activate(iconSizeConstraints + [view.centerXAnchor.constraint(equalTo: centerXAnchor), view.centerYAnchor.constraint(equalTo: centerYAnchor)])
        translatesAutoresizingMaskIntoConstraints = false
        buttonSizeConstraints = [widthAnchor.constraint(equalToConstant: 32), heightAnchor.constraint(equalToConstant: 32)]
        NSLayoutConstraint.activate(buttonSizeConstraints)
        applyScaledSizes()
    }

    private func applyScaledSizes() {
        let metrics = UIFontMetrics.default
        for constraint in iconSizeConstraints { constraint.constant = metrics.scaledValue(for: 16, compatibleWith: traitCollection) }
        for constraint in buttonSizeConstraints { constraint.constant = metrics.scaledValue(for: 32, compatibleWith: traitCollection) }
    }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        if iconSizeConstraints.isEmpty { return }
        if previousTraitCollection == nil || previousTraitCollection!.preferredContentSizeCategory != traitCollection.preferredContentSizeCategory { applyScaledSizes() }
    }

    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        bounds.insetBy(dx: -hitAreaInset, dy: -hitAreaInset).contains(point)
    }
}
