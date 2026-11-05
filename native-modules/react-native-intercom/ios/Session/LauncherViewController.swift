import UIKit

@objc(ICMLauncherViewController)
class LauncherViewController: UIViewController, ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol {
    private lazy var launcher: LauncherView = {
        let config = config
        let theme = themeProvider
        let provider = imageProvider
        return LauncherView(config: config, themeProvider: theme, imageProvider: provider)
    }()
    private let imageProvider: ImageProvider
    private weak var delegate: ICMInAppsDelegate?
    private var config: ICMConfigProtocol
    private var themeProvider: ThemeProvider
    private let metricsManager: ICMMetricProtocol?
    private var launcherSideConstraint: NSLayoutConstraint?
    private var usingDefaultLauncherAlignment = false
    @objc init(delegate: ICMInAppsDelegate?) {
        self.delegate = delegate
        metricsManager = ICMMetric.sharedInstance()
        themeProvider = DefaultThemeProvider.shared
        config = ICMConfig.shared()!
        imageProvider = DefaultImageLoader(imageLoader: ITBImageLoader.longTerm()!)
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    deinit { NotificationCenter.default.removeObserver(self) }
    override func loadView() {
        super.loadView()
        launcher.translatesAutoresizingMaskIntoConstraints = false
        launcher.alpha = 0
        launcher.transform = CGAffineTransform(scaleX: 0.8, y: 0.8)
        view!.addSubview(launcher)
        configureAlignmentConstraints()
        let launcher = launcher
        let parent = launcher.superview!
        launcher.translatesAutoresizingMaskIntoConstraints = false
        let guide = parent.safeAreaLayoutGuide
        launcher.translatesAutoresizingMaskIntoConstraints = false
        launcher.bottomAnchor.constraint(equalTo: guide.bottomAnchor, constant: -28).isActive = true
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let previous = previousTraitCollection?.userInterfaceStyle
        let current = traitCollection.userInterfaceStyle
        if previous == nil || previous != current { Task { @MainActor in await self.launcher.setLauncherImage() } }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        launcher.onTap = { self.launcherTapped() }
        navigationItem.backButtonDisplayMode = .minimal
    }
    override func viewDidAppear(_ animated: Bool) { super.viewDidAppear(animated); animateIn(); updateUnreadCount() }
    override func viewDidLayoutSubviews() { super.viewDidLayoutSubviews(); updateAlignmentOffset() }
    @objc func conversationMarked(asRead identifier: String) { updateUnreadCount() }
    @objc func launcherTapped() {
        if let delegate, delegate.responds(to: #selector(ICMInAppsDelegate.launcherTapped)) { delegate.launcherTapped?() }
    }
    @objc func configureAlignmentConstraints() {
        let useDefault = config.useDefaultLauncherAlignment()
        if useDefault != usingDefaultLauncherAlignment || launcherSideConstraint == nil {
            usingDefaultLauncherAlignment = useDefault
            launcherSideConstraint?.isActive = false
            let launcher = launcher
            let parent = launcher.superview!
            launcher.translatesAutoresizingMaskIntoConstraints = false
            let guide = parent.safeAreaLayoutGuide
            let direction = launcher.effectiveUserInterfaceLayoutDirection
            launcher.translatesAutoresizingMaskIntoConstraints = false
            let constraint: NSLayoutConstraint
            if useDefault { constraint = launcher.trailingAnchor.constraint(equalTo: guide.trailingAnchor, constant: direction == .rightToLeft ? 0 : -0.0) }
            else { constraint = launcher.leadingAnchor.constraint(equalTo: guide.leadingAnchor, constant: direction == .rightToLeft ? -0.0 : 0) }
            constraint.isActive = true
            launcherSideConstraint = constraint
            updateAlignmentOffset()
        }
        Task { @MainActor in
            let launcher = self.launcher
            await launcher.setLauncherImage()
            UIView.animate(withDuration: 0.2) {
                let provider = launcher.themeProvider
                launcher.backgroundColor = provider.theme.primaryBackground
                let theme = provider.theme
                launcher.tintColor = ColorHelper.suitableDynamicForegroundColor(for: theme.primaryBackground, theme: theme)
            }
        }
    }
    private func updateAlignmentOffset() {
        let left = view!.safeAreaInsets.left
        if left > 0 { launcherSideConstraint?.constant = 0 }
        else { launcherSideConstraint?.constant = config.useDefaultLauncherAlignment() ? -28 : 28 }
    }
    @objc func updateUnreadCount() {
        let count = ICMDataManager.shared()!.unreadManager.unreadConversationIds.count
        let previous = launcher.badgeView.badge
        if previous < count {
            let event = ICMMetricEvent(action: "received", object: "badge", place: "in_app", context: "from_launcher", additionalInfo: ["kICMMetricEventInfoBadgeValue": count])
            metricsManager?.addEvent(event)
        }
        launcher.badgeView.badge = count
    }
    private func animateIn() {
        if launcher.alpha > 0 { return }
        NotificationCenter.default.addObserver(self, selector: #selector(updateUnreadCount), name: Notification.Name("IntercomUnreadConversationCountDidChangeNotification"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(configureAlignmentConstraints), name: Notification.Name("kICMIntercomConfigChangedNotification"), object: nil)
        ICMLauncherAnimationUtils.animate({ self.launcher.alpha = 1; self.launcher.transform = .identity }, completion: nil)
    }
    @objc func animateOut(completion: (() -> Void)?) {
        if launcher.alpha == 0 { completion?(); return }
        NotificationCenter.default.removeObserver(self, name: Notification.Name("IntercomUnreadConversationCountDidChangeNotification"), object: nil)
        NotificationCenter.default.removeObserver(self, name: Notification.Name("kICMIntercomConfigChangedNotification"), object: nil)
        ICMLauncherAnimationUtils.animate({ self.launcher.alpha = 0; self.launcher.transform = CGAffineTransform(scaleX: 0.8, y: 0.8) }, completion: completion)
    }
}
