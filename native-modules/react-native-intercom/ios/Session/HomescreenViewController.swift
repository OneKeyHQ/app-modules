import UIKit

@objc(_TtC18IntercomSDKPrivate24HomescreenViewController)
class HomescreenViewController: UIViewController, UIScrollViewDelegate, HomescreenHeaderViewDelegate {
    private enum ScreenFocusState { case notPosted, awaitingHeader, done }
    private weak var contentModalDelegate: (any ICMContentModalViewControllerDelegate)?
    @objc private var viewModel: HomescreenViewModel?
    private var headerViewController: HomescreenHeaderViewController?
    private var currentChildViewController: UIViewController?
    private var headerBackgroundHeightConstraint: NSLayoutConstraint?
    private var headerContainerViewHeightConstraint: NSLayoutConstraint?
    private var currentState: HomescreenHeaderViewState?
    private let homeMetricService: any HomeMetricServiceProtocol
    private let themeProvider: any ThemeProvider
    private var content: HomeHeaderContent?
    private var screenFocusState = ScreenFocusState.notPosted
    private let postAccessibilityNotification: (UIAccessibility.Notification, Any?) -> Void
    private var headerBackgroundView: HomescreenHeaderBackgroundView
    // Original backing storage survives, but its unused private lazy accessor was WMO-deleted.
    private var closeButtonBackgroundView: UIVisualEffectView?
    private lazy var closeButton: UIButton = {
        let button = UIButton(type: .system)
        if #available(iOS 26.0, *) { button.configuration = .glass() }
        else {
            var configuration = UIButton.Configuration.filled()
            configuration.cornerStyle = .capsule
            configuration.baseBackgroundColor = themeProvider.theme.base.withAlphaComponent(0.8)
            configuration.baseForegroundColor = themeProvider.theme.iconDefault
            button.configuration = configuration
        }
        button.setImage(UIImage(systemName: "xmark"), for: .normal)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.widthAnchor.constraint(equalToConstant: 42).isActive = true
        button.heightAnchor.constraint(equalToConstant: 42).isActive = true
        button.tintColor = themeProvider.theme.iconDefault
        return button
    }()
    private lazy var mainScrollView: UIScrollView = {
        let view = UIScrollView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = .vertical
        stack.alignment = .leading
        stack.spacing = 10
        stack.distribution = .fill
        return stack
    }()
    private lazy var headerContainerView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var errorContainerView: UIView = {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.base
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var intercomLinkView: IntercomLinkView = {
        let view = IntercomLinkView(themeProvider: DefaultThemeProvider.shared)
        view.onTap = { [weak self] in
            guard let self else { return }
            self.homeMetricService.addWeRunOnIntercomClickedMetric()
        }
        return view
    }()
    @objc init(delegate: (any ICMContentModalViewControllerDelegate)?) {
        let loader = ImageLoader(imageLoader: ITBImageLoader.longTerm()!)
        let background = HomescreenHeaderBackgroundView(imageLoader: loader)
        background.translatesAutoresizingMaskIntoConstraints = false
        background.layer.masksToBounds = true
        headerBackgroundView = background
        homeMetricService = HomeMetricService(metricInstance: ICMMetric.sharedInstance())
        let provider = DefaultThemeProvider.shared
        themeProvider = provider
        postAccessibilityNotification = { UIAccessibility.post(notification: $0, argument: $1) }
        contentModalDelegate = delegate
        super.init(nibName: nil, bundle: nil)
        let interactor = HomescreenInteractor(apiBridge: SwiftAPIBridge(), dataManager: ICMDataManager.shared(), config: ICMConfig.shared())
        let openCall = OpenCallService.shared
        let center = NotificationCenter.default
        let config = ICMConfig.shared()!
        let service = HomeMetricService(metricInstance: ICMMetric.sharedInstance())
        let queue = AsyncQueue.main
        let opener = LinkOpener.shared
        viewModel = HomescreenViewModel(view: self, interactor: interactor, linkOpener: opener, openCallService: openCall, notificationCenter: center, config: config, homeMetricService: service, queue: queue)
        let headerLoader = ImageLoader(imageLoader: ITBImageLoader.longTerm()!)
        headerViewController = HomescreenHeaderViewController(delegate: self, viewModelInitializer: nil, imageLoader: headerLoader, themeProvider: provider)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        view!.backgroundColor = themeProvider.theme.base
        setupHeaderBackgroundView()
        setupScrollView()
        setupMainStackView()
        setupHeaderContainerView()
        setupHeaderViewController()
        view!.addSubview(errorContainerView)
        pinZeroEdges(errorContainerView, to: view!)
        errorContainerView.isHidden = true
        displayCloseButton()
        viewModel!.viewDidLoad()
        title = TranslationManager.messenger.translation(forKey: "home_space_title")
        navigationItem.titleView = UIView()
        navigationItem.backButtonDisplayMode = .minimal
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: animated)
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        viewModel!.viewDidAppear()
        if screenFocusState == .notPosted {
            let target = headerViewController!.accessibilityFocusTarget
            screenFocusState = target != nil ? .done : .awaitingHeader
            postAccessibilityNotification(.screenChanged, target ?? mainScrollView)
        }
        navigationController?.sheetPresentationController?.detents = [.large()]
    }
    override func viewWillTransition(to size: CGSize, with coordinator: any UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        coordinator.animate(alongsideTransition: { _ in self.viewModel!.didFinishSizeChange() }, completion: nil)
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        let scroll = mainScrollView
        let frame = mainStackView.frame
        let converted = scroll.convert(frame, to: view)
        let rtl = UIView.userInterfaceLayoutDirection(for: view!.semanticContentAttribute) != .leftToRight
        let overlap: CGFloat
        if rtl { overlap = closeButton.frame.minX - converted.origin.x + 17 }
        else { overlap = converted.maxX - closeButton.frame.origin.x - 17 }
        headerViewController!.closeButtonOverlap = overlap
    }
    override func preferredContentSizeDidChange(forChildContentContainer container: any UIContentContainer) {
        super.preferredContentSizeDidChange(forChildContentContainer: container)
        if let constraint = headerContainerViewHeightConstraint { constraint.constant = container.preferredContentSize.height }
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offset = scrollView.contentOffset.y
        let height = headerContainerView.frame.size.height
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { return }
        headerContainerView.alpha = height <= offset ? 0 : 1 - offset / height
    }
    override func traitCollectionDidChange(_ previous: UITraitCollection?) {
        super.traitCollectionDidChange(previous)
        if previous?.userInterfaceStyle != traitCollection.userInterfaceStyle {
            updateCloseButtonTintColor()
            DispatchQueue.main.async { [weak self] in
                guard let self else { return }
                self.viewModel!.didChangeInterfaceStyle()
            }
        }
    }
    private func setupHeaderBackgroundView() {
        view!.addSubview(headerBackgroundView)
        NSLayoutConstraint.activate([
            headerBackgroundView.topAnchor.constraint(equalTo: view!.topAnchor),
            headerBackgroundView.leadingAnchor.constraint(equalTo: view!.leadingAnchor),
            headerBackgroundView.trailingAnchor.constraint(equalTo: view!.trailingAnchor)
        ])
    }
    private func setupScrollView() {
        mainScrollView.delegate = self
        view!.addSubview(mainScrollView)
        NSLayoutConstraint.activate([
            mainScrollView.topAnchor.constraint(equalTo: view!.topAnchor),
            mainScrollView.leadingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.leadingAnchor),
            mainScrollView.trailingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.trailingAnchor),
            mainScrollView.bottomAnchor.constraint(equalTo: view!.bottomAnchor)
        ])
    }
    private func setupMainStackView() {
        mainScrollView.addSubview(mainStackView)
        let maximum = mainStackView.widthAnchor.constraint(lessThanOrEqualToConstant: 400)
        let leading = mainStackView.leadingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.leadingAnchor, constant: 16)
        leading.priority = .init(999)
        let width = mainStackView.widthAnchor.constraint(equalToConstant: 400)
        width.priority = .init(998)
        let bottom = mainStackView.bottomAnchor.constraint(equalTo: mainScrollView.bottomAnchor, constant: -10)
        let center = mainStackView.centerXAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.centerXAnchor)
        NSLayoutConstraint.activate([maximum, leading, width, bottom, center])
    }
    private func setupHeaderContainerView() {
        mainScrollView.addSubview(headerContainerView)
        headerContainerViewHeightConstraint = headerContainerView.heightAnchor.constraint(equalToConstant: 0)
        headerContainerViewHeightConstraint?.isActive = true
        headerBackgroundHeightConstraint = headerBackgroundView.heightAnchor.constraint(equalTo: headerContainerView.heightAnchor)
        headerBackgroundHeightConstraint?.isActive = true
        NSLayoutConstraint.activate([
            headerContainerView.topAnchor.constraint(equalTo: mainScrollView.topAnchor),
            headerContainerView.leadingAnchor.constraint(equalTo: mainStackView.leadingAnchor, constant: 17),
            headerContainerView.trailingAnchor.constraint(equalTo: mainStackView.trailingAnchor, constant: -17),
            headerContainerView.bottomAnchor.constraint(equalTo: mainStackView.topAnchor)
        ])
    }
    private func setupHeaderViewController() {
        addChild(headerViewController!)
        headerContainerView.addSubview(headerViewController!.view!)
        headerViewController!.view!.frame = headerContainerView.bounds
        headerViewController!.view!.heightAnchor.constraint(equalTo: headerContainerView.heightAnchor).isActive = true
        headerViewController!.view!.widthAnchor.constraint(equalTo: headerContainerView.widthAnchor).isActive = true
        headerViewController!.didMove(toParent: self)
    }
    private func displayCloseButton() {
        view!.addSubview(closeButton)
        closeButton.addTarget(self, action: #selector(closeButtonTapped), for: .touchUpInside)
        closeButton.translatesAutoresizingMaskIntoConstraints = false
        closeButton.topAnchor.constraint(equalTo: view!.topAnchor, constant: 32).isActive = true
        closeButton.translatesAutoresizingMaskIntoConstraints = false
        closeButton.trailingAnchor.constraint(equalTo: view!.layoutMarginsGuide.trailingAnchor).isActive = true
        updateCloseButtonTintColor()
    }
    private func updateCloseButtonTintColor() {
        if #available(iOS 26.0, *) { return }
        guard var configuration = closeButton.configuration else { return }
        let style = traitCollection.userInterfaceStyle
        let content = content
        let type: TextColorType = style == .dark ? (content?.textColorTypeDark ?? .light) : (content?.textColorType ?? .dark)
        switch type {
        case .light: closeButton.overrideUserInterfaceStyle = .dark
        case .dark: closeButton.overrideUserInterfaceStyle = .light
        }
        configuration.baseBackgroundColor = themeProvider.theme.neutralContainerAlt.withAlphaComponent(0.8)
        switch type {
        case .light: configuration.baseForegroundColor = themeProvider.theme.textAlwaysDark
        case .dark: configuration.baseForegroundColor = themeProvider.theme.textAlwaysLight
        }
        closeButton.configuration = configuration
    }
    @objc private func closeButtonTapped() { intercomNavigationController()?.closeIntercomModal() }
    private func setupNavigation(hidden: Bool, showClose: Bool) {
        guard navigationController?.topViewController === self else { return }
        intercomNavigationController()?.setNavigationBarHidden(hidden, animated: false)
        closeButton.isHidden = !showClose
    }
    func clearNavigationStack() {
        guard let controllers = navigationController?.viewControllers, !controllers.isEmpty else { return }
        let last = controllers[controllers.count - 1]
        navigationController?.viewControllers = [last]
    }
    private func removeCurrentChild() {
        guard let child = currentChildViewController else { return }
        child.willMove(toParent: nil)
        child.view!.removeFromSuperview()
        child.removeFromParent()
        currentChildViewController = nil
    }
    private func showError(_ message: String, retry: Bool) {
        removeCurrentChild()
        let description = TranslationManager.messenger.translation(forKey: "spaces_error_description")
        let reload = TranslationManager.messenger.translation(forKey: "reload")
        let error = ErrorViewController(title: message, subtitle: description, showActionButton: retry, actionButtonTitle: reload, themeProvider: themeProvider, icon: nil)
        error.errorDelegate = viewModel
        let container = errorContainerView
        addChild(error)
        container.addSubview(error.view!)
        error.view!.frame = container.bounds
        error.view!.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        error.didMove(toParent: self)
        currentChildViewController = error
        errorContainerView.isHidden = false
        view!.bringSubviewToFront(closeButton)
    }
    func updateViewState(_ state: HomescreenViewState) {
        setupNavigation(hidden: true, showClose: true)
        switch state {
        case .error(let message, let retry): showError(message, retry: retry)
        case .loaded(let items):
            removeCurrentChild()
            mainStackView.removeAllArrangedSubviews()
            errorContainerView.isHidden = true
            render(items)
            let header = headerViewController!
            if content?.showAvatars == true { UIView.animate(withDuration: 0.3) { header.avatarsView.alpha = 1 } }
            setupIntercomLink()
        case .loading:
            errorContainerView.isHidden = true
            mainStackView.removeAllArrangedSubviews()
            for height: CGFloat in [96, 166, 189, 79] {
                let card = buildLoadingCard()
                card.heightAnchor.constraint(equalToConstant: height).isActive = true
                mainStackView.addArrangedSubview(card)
                card.widthAnchor.constraint(equalTo: mainStackView.widthAnchor).isActive = true
            }
        }
    }
    private func render(_ items: [any HomescreenCardViewItem]) {
        mainStackView.alpha = 0
        for (index, item) in items.enumerated() {
            let view = item.buildView()
            view.translatesAutoresizingMaskIntoConstraints = false
            mainStackView.insertArrangedSubview(view, at: index)
            NSLayoutConstraint.activate([
                view.leadingAnchor.constraint(equalTo: mainStackView.leadingAnchor),
                view.trailingAnchor.constraint(equalTo: mainStackView.trailingAnchor)
            ])
            mainStackView.layoutIfNeeded()
        }
        viewModel!.didFinishSizeChange()
        UIView.animate(withDuration: 0.3) { self.mainStackView.alpha = 1 }
    }
    func showCardViewItem(_ show: Bool, forCardAt index: Int) {
        let views = mainStackView.arrangedSubviews
        if index < 0 || index >= views.count { return }
        if let card = views[index] as? HomescreenCardView { card.isHidden = !show }
    }
    private func buildLoadingCard() -> UIView {
        let plain = UIView()
        plain.translatesAutoresizingMaskIntoConstraints = false
        plain.backgroundColor = themeProvider.theme.neutralContainer
        let shimmer = ShimmeringView(frame: .zero)
        shimmer.translatesAutoresizingMaskIntoConstraints = false
        shimmer.contentView = plain
        shimmer.isShimmering = true
        shimmer.alpha = 0.6
        let effect = UIVisualEffectView(effect: nil)
        effect.translatesAutoresizingMaskIntoConstraints = false
        view!.addSubview(effect)
        effect.contentView.addSubview(shimmer)
        effect.effect = UIBlurEffect(style: .prominent)
        effect.layer.cornerRadius = 12
        effect.layer.masksToBounds = true
        NSLayoutConstraint.activate([
            shimmer.leadingAnchor.constraint(equalTo: effect.leadingAnchor), shimmer.trailingAnchor.constraint(equalTo: effect.trailingAnchor),
            shimmer.topAnchor.constraint(equalTo: effect.topAnchor), shimmer.bottomAnchor.constraint(equalTo: effect.bottomAnchor)
        ])
        return effect
    }
    private func setupIntercomLink() {
        guard let poweredBy = viewModel!.poweredByForConversation else { return }
        intercomLinkView.updateDetails(with: poweredBy)
        intercomLinkView.alpha = 0
        view!.addSubview(intercomLinkView)
        let windows = [UIWindow]._unconditionallyBridgeFromObjectiveC(ICMUtils.fetchApplicationWindows() as NSArray?)
        let bottom = windows.first?.safeAreaInsets.bottom ?? 0
        let spacing: CGFloat = bottom == 0 ? 24 : bottom
        NSLayoutConstraint.activate([
            intercomLinkView.centerXAnchor.constraint(equalTo: view!.centerXAnchor),
            intercomLinkView.bottomAnchor.constraint(equalTo: view!.bottomAnchor, constant: -spacing)
        ])
        let link = intercomLinkView
        let material = link.subviews.compactMap { $0 as? UIVisualEffectView }.first
        material?.removeFromSuperview()
        let effect = UIVisualEffectView(effect: UIBlurEffect(style: .systemUltraThinMaterial))
        link.insertSubview(effect, at: 0)
        pinZeroEdges(effect, to: link)
        UIView.animate(withDuration: 0.3) { self.intercomLinkView.alpha = 1 }
        let height = intercomLinkView.frame.size.height
        mainScrollView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: bottom == 0 ? spacing + height : height, right: 0)
    }
    func headerDidUpdate(_ state: HomescreenHeaderViewState) {
        var fade = false
        if case .noHeader? = currentState {
            if case .noHeader = state {} else { fade = true }
        }
        let height: CGFloat
        switch state {
        case .expandedHeader(let header):
            let type = header.background.type
            let fades = header.background.fadeToWhite
            height = (type == .solid || type == .colors) && fades ? 174 : 54
            closeButton.tintColor = header.content.closeButtonDetails.foregroundColor
            content = header.content
            let controller = headerViewController!
            if header.content.showAvatars { UIView.animate(withDuration: 0.3) { controller.avatarsView.alpha = 1 } }
        case .waitingForImage(let header, _, let foreground):
            let type = header.background.type
            let fades = header.background.fadeToWhite
            height = (type == .solid || type == .colors) && fades ? 174 : 54
            closeButton.tintColor = foreground
            content = header.content
            let controller = headerViewController!
            if header.content.showAvatars { UIView.animate(withDuration: 0.3) { controller.avatarsView.alpha = 1 } }
        case .miniHeader(_, _, let foreground): closeButton.tintColor = foreground; height = -25
        case .noHeader:
            closeButton.tintColor = themeProvider.theme.iconDefault
            headerContainerViewHeightConstraint?.constant = 239
            height = 239
        }
        headerBackgroundHeightConstraint?.constant = height
        headerBackgroundView.configure(for: state)
        if fade {
            headerContainerView.alpha = 0
            headerBackgroundView.alpha = 0
            UIView.animate(withDuration: 0.3) { self.headerContainerView.alpha = 1; self.headerBackgroundView.alpha = 1 }
        }
        currentState = state
        if screenFocusState == .awaitingHeader, let target = headerViewController!.accessibilityFocusTarget {
            screenFocusState = .done
            postAccessibilityNotification(.layoutChanged, target)
        }
    }
    func showIntercomLink(_ show: Bool) { UIView.animate(withDuration: 0.3) { self.intercomLinkView.alpha = show ? 1 : 0 } }
    func presentAlertController(_ controller: UIAlertController) { present(controller, animated: true, completion: nil) }
    func dismissMessengerSheet() { dismiss(animated: true, completion: nil) }
    private func pushSheetToFullscreen() -> Bool {
        let sheet = navigationController?.sheetPresentationController
        if sheet?.selectedDetentIdentifier != .medium || traitCollection.verticalSizeClass != .regular { return true }
        sheet?.animateChanges { sheet?.selectedDetentIdentifier = .large }
        return false
    }
    private func pushToViewController(_ controller: UIViewController) {
        let animated = pushSheetToFullscreen()
        navigationController?.pushViewController(controller, animated: animated)
        navigationController?.sheetPresentationController?.detents = [.large()]
    }
    func navigateToArticle(_ id: String) { ICMPresentationManager.sharedInstance().presentArticle(id, allowReactions: true, anchorLink: nil) }
    func navigateToConversation(_ conversation: ICMConversation) { pushToViewController(ConversationViewController(conversation: conversation)) }
    func navigateToMessages() { pushToViewController(InboxViewController()) }
    func navigateToTickets() { pushToViewController(TicketsListViewController()) }
    func navigateToTicket(_ ticket: Ticket) {
        guard let navigation = navigationController else { return }
        ICMPresentationManager.sharedInstance().pushTicket(ticket, from: navigation, didCreateTicket: false, triggerSource: 2)
    }
    func navigateToNewConversation() {
        let manager = ICMPresentationManager.sharedInstance()
        let conversation = ICMConversation()
        manager?.showConversation(conversation)
    }
    func navigateToAppSheet(_ url: URL, params: [AnyHashable: Any], delegate: (any ICMMessengerSheetViewControllerDelegate)?) {
        guard let sheet = ICMMessengerSheetViewController(sheetURL: url, params: params, delegate: delegate) else { return }
        ICMPresentationManager.sharedInstance().presentSheet(sheet)
    }
    func navigateToCreateTicket(_ type: TicketType) { ICMPresentationManager.sharedInstance().presentCreateTicketForm(forTicketType: type, conversation: nil) }
    func navigateToHelpCenter() {
        let delegate = contentModalDelegate
        let controller = HelpCenterContainerViewController(delegate: delegate)
        pushToViewController(controller)
    }
    func navigateToHelpCenterSearch() {
        let delegate = contentModalDelegate
        let controller = HelpCenterContainerViewController(delegate: delegate)
        controller.fromSearchBrowse = true
        controller.metricService.contextIsSearchBrowse = true
        pushToViewController(controller)
    }
    private func pinZeroEdges(_ child: UIView, to parent: UIView) {
        child.translatesAutoresizingMaskIntoConstraints = false
        child.translatesAutoresizingMaskIntoConstraints = false
        child.topAnchor.constraint(equalTo: parent.topAnchor).isActive = true
        let rtl = child.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            child.translatesAutoresizingMaskIntoConstraints = false
            child.translatesAutoresizingMaskIntoConstraints = false
            child.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            child.translatesAutoresizingMaskIntoConstraints = false
            child.translatesAutoresizingMaskIntoConstraints = false
            child.rightAnchor.constraint(equalTo: parent.rightAnchor).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
        child.translatesAutoresizingMaskIntoConstraints = false
        child.translatesAutoresizingMaskIntoConstraints = false
        child.bottomAnchor.constraint(equalTo: parent.bottomAnchor).isActive = true
    }
}
