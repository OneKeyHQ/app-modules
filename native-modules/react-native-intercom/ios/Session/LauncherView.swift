import UIKit

@objc(ICMLauncherView)
class LauncherView: UIView, UIPointerInteractionDelegate {
    @objc var onTap: (() -> Void)?
    @objc lazy var imageView: UIImageView = {
        let view = UIImageView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    @objc var badgeView: BadgeView
    private let imageProvider: ImageProvider
    private let config: ICMConfigProtocol
    let themeProvider: ThemeProvider
    private let gestureRecognizer: UITapGestureRecognizer
    init(config: ICMConfigProtocol, themeProvider: ThemeProvider, imageProvider: ImageProvider) {
        gestureRecognizer = UITapGestureRecognizer()
        self.config = config
        self.themeProvider = themeProvider
        self.imageProvider = imageProvider
        badgeView = BadgeView(context: .launcher, themeProvider: themeProvider)
        super.init(frame: .zero)
        setupView()
    }
    required init?(coder: NSCoder) {
        gestureRecognizer = UITapGestureRecognizer()
        config = ICMConfig.shared()!
        themeProvider = DefaultThemeProvider.shared
        imageProvider = DefaultImageLoader(imageLoader: ITBImageLoader.longTerm()!)
        badgeView = BadgeView(context: .launcher, themeProvider: themeProvider)
        super.init(coder: coder)
        setupView()
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupView() {
        translatesAutoresizingMaskIntoConstraints = false
        translatesAutoresizingMaskIntoConstraints = false
        widthAnchor.constraint(equalToConstant: 58).isActive = true
        translatesAutoresizingMaskIntoConstraints = false
        heightAnchor.constraint(equalToConstant: 58).isActive = true
        applyElevationOneShadow(cornerRadius: 0)
        setupButtonConfiguration()
        addSubview(badgeView)
        addInteraction(UIPointerInteraction(delegate: self))
        addSubview(imageView)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint(item: imageView, attribute: .centerX, relatedBy: .equal, toItem: imageView.superview!, attribute: .centerX, multiplier: 1, constant: 0).isActive = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint(item: imageView, attribute: .centerY, relatedBy: .equal, toItem: imageView.superview!, attribute: .centerY, multiplier: 1, constant: 0).isActive = true
        imageView.contentMode = .scaleAspectFit
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.widthAnchor.constraint(equalToConstant: 36).isActive = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.heightAnchor.constraint(equalToConstant: 36).isActive = true
    }
    private func setupButtonConfiguration() {
        let provider = themeProvider
        backgroundColor = provider.theme.primaryBackground
        layer.cornerRadius = 29
        let theme = provider.theme
        tintColor = ColorHelper.suitableDynamicForegroundColor(for: theme.primaryBackground, theme: theme)
        Task { @MainActor in await self.setLauncherImage() }
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        let radius = bounds.width * 0.5
        let x = bounds.midX + radius * 0.7071067811865476
        let y = bounds.midY + radius * -0.7071067811865475
        badgeView.center = CGPoint(x: x, y: y)
        bringSubviewToFront(badgeView)
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let current = traitCollection.userInterfaceStyle
        let previous = previousTraitCollection?.userInterfaceStyle
        if previous == nil || previous != current { Task { @MainActor in await self.setLauncherImage() } }
    }
    @MainActor @objc func setLauncherImage() async {
        imageView.image = ICMAssets.launcherImage()
        guard let url = customImageURL else { return }
        do { let image = try await imageProvider.image(for: url); imageView.image = image }
        catch {}
    }
    private var customImageURL: URL? {
        guard let modules = config.modules else { return nil }
        let customization = ((modules as AnyObject) as! ConfigModules).customizationConfig
        let light = customization.launcherLogoURL
        let dark = customization.launcherLogoDarkURL
        if traitCollection.userInterfaceStyle == .dark { return dark ?? light }
        return light ?? dark
    }
    @MainActor @objc func configUpdated() async {
        await setLauncherImage()
        UIView.animate(withDuration: 0.2) {
            let provider = self.themeProvider
            self.backgroundColor = provider.theme.primaryBackground
            let theme = provider.theme
            self.tintColor = ColorHelper.suitableDynamicForegroundColor(for: theme.primaryBackground, theme: theme)
        }
    }
    @objc func buttonTapped() { UIView.animate(withDuration: 0.08) { self.transform = CGAffineTransform(scaleX: 0.9, y: 0.9) } }
    @objc func buttonReleased() {
        UIView.animate(withDuration: 0.08) { self.transform = CGAffineTransform(scaleX: 1, y: 1) }
        let callback = onTap
        callback?()
    }
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) { super.touchesBegan(touches, with: event); buttonTapped() }
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) { super.touchesEnded(touches, with: event); buttonReleased() }
    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) { super.touchesCancelled(touches, with: event); buttonReleased() }
    func pointerInteraction(_ interaction: UIPointerInteraction, styleFor region: UIPointerRegion) -> UIPointerStyle? {
        let preview = UITargetedPreview(view: self)
        return UIPointerStyle(effect: .lift(preview), shape: nil)
    }
}
