import UIKit

@objc(_TtC18IntercomSDKPrivate33HelpCenterContainerViewController)
class HelpCenterContainerViewController: UIViewController, UISearchControllerDelegate, ICMErrorViewDelegate {
    enum ContentType { case someCollections([String]), collection(String), allCollections }
    private enum State {
        case collectionList([InternalHelpCenterCollection]), collection(InternalHelpCenterCollectionContent)
        case articleList(InternalHelpCenterCollectionContent), error(HelpCenterError), loading
    }
    var isFilteredHelpCenter = false
    var contentType = ContentType.allCollections
    private var state: State? {
        didSet { DispatchQueue.main.async { self.applyState(previous: oldValue) } }
    }
    private var helpCenterService: HelpCenterService
    var metricService: any HelpCenterMetricServiceProtocol
    private let translationManager = TranslationManager.helpCenter
    private let config: any ICMConfigProtocol
    private let themeProvider: any ThemeProvider
    private let openCallService: any OpenCallServiceProtocol
    private let notificationCenter: any NotificationCenterInterface
    private weak var delegate: (any ICMContentModalViewControllerDelegate)?
    private var containerView = UIView()
    var currentChildViewController: UIViewController?
    private lazy var searchViewController = HelpCenterSearchViewController(helpCenterService: helpCenterService, metricService: metricService, config: config, dataManager: ICMDataManager.shared(), themeProvider: DefaultThemeProvider.shared)
    lazy var searchController = UISearchController(searchResultsController: searchViewController)
    var fromSearchBrowse = false
    private var showTitle = true

    @objc init(delegate: (any ICMContentModalViewControllerDelegate)?) {
        metricService = HelpCenterMetricService()
        helpCenterService = HelpCenterService(apiBridge: SwiftAPIBridge())
        config = ICMConfig.shared()
        themeProvider = DefaultThemeProvider.shared
        openCallService = OpenCallService.shared
        notificationCenter = NotificationCenter.default
        self.delegate = delegate
        super.init(nibName: nil, bundle: nil)
    }
    @objc(initWithCollections:delegate:)
    convenience init(collections: [String], delegate: (any ICMContentModalViewControllerDelegate)?) {
        self.init(delegate: delegate)
        isFilteredHelpCenter = true
        contentType = .someCollections(collections)
    }
    @objc(initWithCollection:delegate:)
    convenience init(collection: String, delegate: (any ICMContentModalViewControllerDelegate)?) {
        let service = HelpCenterMetricService()
        self.init(delegate: delegate)
        metricService = service
        isFilteredHelpCenter = true
        contentType = .collection(collection)
    }
    @objc(initFromSearchBrowse:delegate:)
    convenience init(fromSearchBrowse: Bool, delegate: (any ICMContentModalViewControllerDelegate)?) {
        self.init(delegate: delegate)
        self.fromSearchBrowse = fromSearchBrowse
        metricService.contextIsSearchBrowse = fromSearchBrowse
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    deinit { notificationCenter.removeObserver(self) }
    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = themeProvider.theme.base
        view.accessibilityIdentifier = "intercom help center container view controller"
        setupContainerView()
        setupSearchController()
        setupSearchBar()
        displayCloseButton()
        navigationItem.backButtonTitle = ""
        navigationItem.backButtonDisplayMode = .minimal
        customiseNavigationTitle()
        notificationCenter.addObserver(self, selector: #selector(openCallStateDidChange(_:)), name: .init("IntercomOpenCallStateDidChangeNotification"), object: nil)
    }
    private func setupContainerView() {
        containerView.backgroundColor = .clear
        containerView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(containerView)
        let constraints = [containerView.leadingAnchor.constraint(equalTo: view.leadingAnchor), containerView.trailingAnchor.constraint(equalTo: view.trailingAnchor), containerView.topAnchor.constraint(equalTo: view.topAnchor), containerView.bottomAnchor.constraint(equalTo: view.bottomAnchor)]
        for constraint in constraints { constraint.isActive = true }
    }
    private func setupSearchController() {
        searchViewController.containedViewDelegate = self
        searchController.searchResultsUpdater = searchViewController
        searchController.obscuresBackgroundDuringPresentation = false
        searchController.delegate = self
        definesPresentationContext = true
    }
    private func setupSearchBar() {
        let bar = searchController.searchBar
        bar.placeholder = translationManager.translation(forKey: "search_for_help")
        bar.searchTextField.accessibilityIdentifier = "intercom help center search field"
        navigationItem.searchController = searchController
        navigationItem.hidesSearchBarWhenScrolling = false
    }
    private func displayCloseButton() {
        if navigationController is ICMNavigationController { intercomNavigationController()?.displayCloseButton() }
        else if let controller = navigationController { _ = String(describing: type(of: controller)) }
    }
    private func customiseNavigationTitle() {
        let title = config.title(forSpace: 1) ?? translationManager.translation(forKey: "help_space_title")
        navigationItem.title = showTitle ? title : ""
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let navigationAnimated: Bool
        if #available(iOS 26.0, *) { navigationAnimated = false } else { navigationAnimated = animated }
        navigationController?.setNavigationBarHidden(false, animated: navigationAnimated)
        switch openCallService.state {
        case .loading: state = .loading
        case .failure: state = .error(.openCallFailed)
        case .success: routeCurrentContent(); fireHelpViewedMetricIfNeeded()
        }
    }
    private func fireHelpViewedMetricIfNeeded() {
        if let controllers = intercomNavigationController()?.viewControllers, controllers.count >= 2,
           controllers[controllers.count - 2].isKind(of: type(of: self)) { return }
        // Original 0x31a03c is a dead-self specialization using this same once metric.
        let event = ICMMetricEvent(action: "viewed", object: "help", place: "messenger", context: "no_context", additionalInfo: nil)
        event?.eventType = 0
        helpCenterSharedMetric?.addEvent(event)
    }
    override func viewIsAppearing(_ animated: Bool) {
        super.viewIsAppearing(animated)
        searchViewController.updateStartConversationView()
    }
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        if fromSearchBrowse {
            DispatchQueue.main.async { self.searchController.searchBar.becomeFirstResponder() }
            fromSearchBrowse = false
        }
        navigationController?.interactivePopGestureRecognizer?.isEnabled = !searchController.isActive
    }
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        navigationController?.interactivePopGestureRecognizer?.isEnabled = true
    }
    func willPresentSearchController(_ searchController: UISearchController) {
        navigationController?.interactivePopGestureRecognizer?.isEnabled = false
        delegate?.dismissible?(bySwipe: false, didAttemptDismiss: nil)
    }
    func didDismissSearchController(_ searchController: UISearchController) {
        navigationController?.interactivePopGestureRecognizer?.isEnabled = true
        delegate?.dismissible?(bySwipe: true, didAttemptDismiss: nil)
    }
    @objc private func openCallStateDidChange(_ notification: Notification) {
        switch openCallService.state {
        case .loading: return
        case .failure: state = .error(.openCallFailed)
        case .success: routeCurrentContent(); fireHelpViewedMetricIfNeeded()
        }
    }
    @objc func didSelectErrorAction() {
        if openCallService.state == .failure { state = .loading; openCallService.retry() }
        else { routeCurrentContent() }
    }
    private func routeCurrentContent() {
        switch contentType {
        case .someCollections(let identifiers): presentCollectionsByIds(identifiers)
        case .collection(let identifier): presentCollection(identifier)
        case .allCollections: presentAllCollections()
        }
    }
    private func applyState(previous: State?) {
        guard let state else { return }
        switch state {
        case .collectionList(let collections):
            if case .collectionList? = previous { return }
            configureForCollectionList(collections, collection: nil)
        case .collection(let collection):
            if case .collection? = previous { return }
            configureForCollectionList(collection.collections, collection: collection)
        case .articleList(let collection):
            if case .articleList? = previous { return }
            configureForArticleList(collection)
        case .error(let error):
            if case .error? = previous { return }
            configureForError(error)
        case .loading:
            if case .loading? = previous { return }
            configureForLoading()
        }
    }
    private func configureForCollectionList(_ collections: [InternalHelpCenterCollection], collection: InternalHelpCenterCollectionContent?) {
        let service = metricService
        let config = config
        let delegate = delegate
        let manager = ICMDataManager.shared()!
        let controller = HelpCenterCollectionListViewController(collections: collections, metricService: service, config: config, dataManager: manager, delegate: delegate, collection: collection)
        controller.showBrowseFullHelpCenterOption = isFilteredHelpCenter
        controller.contentDelegate = self
        configure(controller, showTitle: true, showSearch: true)
        metricService.addCollectionListViewedEvent(filteredHelpCenter: isFilteredHelpCenter)
    }
    private func configureForArticleList(_ collection: InternalHelpCenterCollectionContent) {
        let service = metricService
        let config = config
        let manager = ICMDataManager.shared()!
        let controller = HelpCenterArticleListViewController(collection: collection, metricService: service, config: config, dataManager: manager)
        controller.showBrowseFullHelpCenterOption = isFilteredHelpCenter
        configure(controller, showTitle: true, showSearch: true)
        metricService.addArticleListViewedEvent(filteredHelpCenter: isFilteredHelpCenter)
    }
    private func configureForError(_ error: HelpCenterError) {
        let reload = translationManager.translation(forKey: "reload")
        let subtitle = error == .contentNotAvailable ? nil : translationManager.translation(forKey: "spaces_error_description")
        let message = error.message
        let icon = ICMAssets.helpIcon()
        let provider = DefaultThemeProvider.shared
        let controller = ErrorViewController(title: message, subtitle: subtitle, showActionButton: error == .serverError || error == .openCallFailed, actionButtonTitle: reload, themeProvider: provider, icon: icon)
        controller.errorDelegate = self
        configure(controller, showTitle: true, showSearch: false)
    }
    private func configureForLoading() {
        let controller = UIViewController()
        controller.view!.backgroundColor = themeProvider.theme.base
        let spinner = LoadingSpinnerView()
        controller.view!.addSubview(spinner)
        spinner.translatesAutoresizingMaskIntoConstraints = false
        spinner.translatesAutoresizingMaskIntoConstraints = false
        let x = spinner.centerXAnchor.constraint(equalTo: controller.view!.centerXAnchor)
        x.priority = .required
        x.isActive = true
        spinner.translatesAutoresizingMaskIntoConstraints = false
        spinner.translatesAutoresizingMaskIntoConstraints = false
        let y = spinner.centerYAnchor.constraint(equalTo: controller.view!.centerYAnchor)
        y.priority = .required
        y.isActive = true
        spinner.startSpinning()
        controller.view!.accessibilityLabel = translationManager.translation(forKey: "loading")
        configure(controller, showTitle: true, showSearch: true)
    }
    private func configure(_ controller: UIViewController, showTitle: Bool, showSearch: Bool) {
        if let old = currentChildViewController {
            old.willMove(toParent: nil)
            old.view!.removeFromSuperview()
            old.removeFromParent()
            currentChildViewController = nil
        }
        self.showTitle = showTitle
        customiseNavigationTitle()
        navigationItem.searchController = showSearch ? searchController : nil
        if showSearch { searchController.searchResultsUpdater = searchViewController }
        else { searchController.searchResultsUpdater = nil }
        let container = containerView
        addChild(controller)
        container.addSubview(controller.view!)
        controller.view!.frame = container.bounds
        controller.view!.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        controller.didMove(toParent: self)
        currentChildViewController = controller
    }
    private func presentAllCollections() {
        state = .loading
        let service = helpCenterService
        service.fetchCollections { [weak self] result in
            switch result {
            case .success(let collections): self?.presentCollections(collections)
            case .failure(let error):
                self?.state = .error(error.errorType)
                let metric = self?.metricService
                metric?.addCollectionListFailedEvent(errorCode: error.statusCode)
            }
        }
    }
    private func presentCollections(_ collections: [InternalHelpCenterCollection]) {
        switch collections.count {
        case 0: state = .error(.contentNotAvailable)
        case 1: presentCollection(collections[0].id)
        default: state = .collectionList(collections)
        }
    }
    private func presentCollectionsByIds(_ identifiers: [String]) {
        state = .loading
        let service = helpCenterService
        service.fetchCollections(identifiers) { [weak self] result in
            switch result {
            case .failure(let error):
                print(error)
                self?.state = .error(error.errorType)
                let metric = self?.metricService
                metric?.addCollectionListFailedEvent(errorCode: error.statusCode)
            case .success(let content):
                switch content {
                case .fullHelpCenter(let collections): self?.isFilteredHelpCenter = false; self?.presentCollections(collections)
                case .filteredHelpCenter(let collections): self?.isFilteredHelpCenter = true; self?.presentCollections(collections)
                }
            }
        }
    }
    private func presentCollection(_ identifier: String) {
        state = .loading
        let service = helpCenterService
        service.fetchCollection(identifier) { [weak self] result in
            switch result {
            case .failure(let error):
                self?.state = .error(error.errorType)
                let metric = self?.metricService
                metric?.addArticleListFailedEvent(errorCode: error.statusCode)
            case .success(let collection):
                if collection.collections.count > 0 { self?.state = .collection(collection) }
                else { self?.state = .articleList(collection) }
            }
        }
    }
}
