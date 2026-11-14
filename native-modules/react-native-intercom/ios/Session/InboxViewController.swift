import UIKit

@objc(_TtC18IntercomSDKPrivate19InboxViewController)
class InboxViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, IntercomNexusObserverProtocol, RecoveredInboxView {
    private var viewModel: InboxViewModel?
    private let themeProvider: ThemeProvider
    private let nexusClient: IntercomNexusClient
    private var tableView: UITableView?
    private var loadingViewController: LoadingViewController?
    private var infoView: ICMInboxInfoView?
    private var emptyStateView: MessagesEmptyStateView?
    private var createMessageButton: UIBarButtonItem?

    @objc init() {
        themeProvider = DefaultThemeProvider.shared
        nexusClient = IntercomNexusClient.shared()!
        super.init(nibName: nil, bundle: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(reloadInbox), name: Notification.Name("intercom-sdk-inbox-updated"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(conversationMarkedAsRead(_:)), name: Notification.Name("ICMConversationMarkedAsReadNotification"), object: nil)
        setupViewModel(viewModel: nil, dataManager: ICMDataManager.shared(), metricsService: MessagesMetricService(metricInstance: ICMMetric.sharedInstance()), config: ICMConfig.shared())
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    deinit {
        NotificationCenter.default.removeObserver(self)
        nexusClient.removeObserver(self)
    }

    override func loadView() {
        super.loadView()
        view.backgroundColor = themeProvider.theme.base
        view.accessibilityIdentifier = "conversations"
        createLoadingView()
        createTableView()
    }
    private func createLoadingView() {
        loadingViewController = LoadingViewController(image: ICMAssets.messagesLoadingImage(), accessibilityText: TranslationManager.messenger.translation(forKey: "loading"))
        loadingViewController!.view.translatesAutoresizingMaskIntoConstraints = false
        loadingViewController!.view.isHidden = true
        view.addSubview(loadingViewController!.view)
        NSLayoutConstraint.activate([
            loadingViewController!.view.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor),
            loadingViewController!.view.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor),
            loadingViewController!.view.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            loadingViewController!.view.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor)
        ])
    }
    private func createTableView() {
        tableView = UITableView()
        view.addSubview(tableView!)
        tableView!.backgroundColor = themeProvider.theme.base
        tableView!.alwaysBounceVertical = true
        tableView!.separatorStyle = .none
        tableView!.delegate = self
        tableView!.dataSource = self
        tableView!.accessibilityIdentifier = "inbox table view"
        tableView!.translatesAutoresizingMaskIntoConstraints = false
        tableView!.rowHeight = UITableView.automaticDimension
        tableView!.estimatedRowHeight = 90
        tableView!.register(InboxCell.self, forCellReuseIdentifier: "InboxCellIdentifier")
        tableView!.register(TicketsListViewCell.self, forCellReuseIdentifier: "TicketCellIdentifier")
        tableView!.register(ICMLoadingTableViewCell.self, forCellReuseIdentifier: "InboxLoadingCellIdentifier")
        NSLayoutConstraint.activate([
            tableView!.topAnchor.constraint(equalTo: view.topAnchor),
            tableView!.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView!.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            tableView!.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel!.viewDidLoad()
        nexusClient.addObserver(self)
        configureNavigationBar()
    }
    @objc func configureNavigationBar() {
        navigationItem.title = viewModel!.getNavigationTitle()
        navigationItem.backButtonDisplayMode = .minimal
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if !loadingViewController!.view.isHidden { view.addSubview(loadingViewController!.view) }
        viewModel!.dataManager.lastViewedConversation = nil
        viewModel!.reloadInbox()
        if #available(iOS 26.0, *) { navigationController?.setNavigationBarHidden(false, animated: false) }
        else { navigationController?.setNavigationBarHidden(false, animated: animated) }
        configureNavigationBar()
        configureNavigationBarButtons()
    }
    @objc func configureNavigationBarButtons() {
        (navigationController as? ICMNavigationController)?.displayCloseButton()
        removeComposeMessageBarButtonItem()
        if viewModel!.config.helpCenterRequireSearch { return }
        if viewModel!.config.canStartConversation() { addComposeMessageBarButtonItem() }
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        let windows = ICMUtils.fetchApplicationWindows()! as! [UIWindow]
        if let first = windows.first { tableView!.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: first.safeAreaInsets.bottom, right: 0) }
    }
    func updateViewState(_ state: InboxViewState) {
        DispatchQueue.main.async { [weak self, state] in
            guard let self else { return }
            switch state {
            case .content:
                self.loadingViewController!.view.isHidden = true
                self.hideErrorView()
                self.emptyStateView?.removeFromSuperview()
                self.tableView!.isHidden = false
                self.tableView!.reloadData()
            case .empty(let emptyState):
                self.loadingViewController!.view.isHidden = true
                self.hideErrorView()
                self.tableView!.isHidden = true
                self.configureEmptyStateView(emptyState)
            case .error(let message, let retry):
                self.loadingViewController!.view.isHidden = true
                self.emptyStateView?.removeFromSuperview()
                self.showErrorView(title: TranslationManager.messenger.translation(forKey: "spaces_error_title"), message: message, showRetryButton: retry)
            case .loading:
                self.loadingViewController!.view.isHidden = false
                self.tableView!.isHidden = true
                self.hideErrorView()
                self.emptyStateView?.removeFromSuperview()
            }
        }
    }
    @objc func hideErrorView() {
        guard let infoView else { return }
        tableView!.isHidden = false
        infoView.setAlphaAnimated(0) { [self, infoView] finished in
            if finished { infoView.removeFromSuperview(); self.infoView = nil }
        }
    }
    @objc(showErrorViewWithTitle:message:showRetryButton:)
    func showErrorView(title: String, message: String, showRetryButton: Bool) {
        hideErrorView()
        infoView = ICMInboxInfoView(title: title, message: message, showRetryButton: showRetryButton, delegate: viewModel)
        guard let infoView else { return }
        infoView.translatesAutoresizingMaskIntoConstraints = false
        infoView.alpha = 0
        view.addSubview(infoView)
        NSLayoutConstraint.activate([
            infoView.topAnchor.constraint(equalTo: view.topAnchor),
            infoView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            infoView.leftAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leftAnchor),
            infoView.rightAnchor.constraint(equalTo: view.safeAreaLayoutGuide.rightAnchor)
        ])
        infoView.setAlphaAnimated(1, completion: nil)
    }
    private func configureEmptyStateView(_ state: MessagesEmptyState) {
        emptyStateView?.removeFromSuperview()
        let chosen: MessagesEmptyState
        if state.action?.type == .message, let cta = ICMDataManager.shared().conversationCTA {
            let action = MessagesEmptyStateAction(type: .message, label: cta.label, icon: cta.icon.image())
            chosen = MessagesEmptyState(title: state.title, text: state.text, action: action)
        } else { chosen = state }
        emptyStateView = MessagesEmptyStateView(frame: view.bounds, emptyStateConfig: chosen, delegate: viewModel)
        view.addSubview(emptyStateView!)
        emptyStateView!.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            emptyStateView!.topAnchor.constraint(equalTo: view.topAnchor),
            emptyStateView!.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            emptyStateView!.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            emptyStateView!.trailingAnchor.constraint(equalTo: view.trailingAnchor)
        ])
    }
    @objc func reloadTableView() { tableView!.reloadData() }
    @objc func reloadTableViewRow(at indexPath: IndexPath) { tableView!.reloadRows(at: [indexPath], with: .none) }
    @objc func setLoadingViewVisible(_ visible: Bool) { loadingViewController!.view.isHidden = !visible }
    private func removeComposeMessageBarButtonItem() {
        navigationItem.rightBarButtonItems = (navigationItem.rightBarButtonItems ?? []).filter { $0.accessibilityIdentifier != "intercom create message button" }
        createMessageButton = nil
    }
    private func addComposeMessageBarButtonItem() {
        let weight: UIImage.SymbolWeight
        if #available(iOS 26.0, *) { weight = UIImage.SymbolWeight(rawValue: 4)! }
        else { weight = UIImage.SymbolWeight(rawValue: 6)! }
        let configuration = UIImage.SymbolConfiguration(weight: weight)
        let image = UIImage(systemName: "square.and.pencil", withConfiguration: configuration)
        createMessageButton = UIBarButtonItem(image: image, style: .plain, target: self, action: #selector(composeMessageButtonTapped))
        if let button = createMessageButton {
            button.tintColor = themeProvider.theme.textDefault
            var items = navigationItem.rightBarButtonItems ?? []
            items.append(button)
            navigationItem.rightBarButtonItems = items
            button.accessibilityIdentifier = "intercom create message button"
            button.accessibilityLabel = TranslationManager.messenger.translation(forKey: "new_conversation")
        }
    }
    @objc func navigateToConversation(_ conversation: ICMConversation) {
        let selected = conversation.conversationId != nil ? conversation : ICMConversation()
        ICMPresentationManager.sharedInstance().showConversation(selected, from: navigationController as? ICMNavigationController)
    }
    @objc func presentHelpCenter() { ICMPresentationManager.sharedInstance().presentHelpCenter() }
    @objc private func composeMessageButtonTapped() { viewModel!.composeMessageButtonTapped() }
    @objc private func reloadInbox() { viewModel!.reloadInbox() }
    @objc private func conversationMarkedAsRead(_ notification: Notification) {
        guard let id = notification.object as? String else { return }
        viewModel!.conversationMarkedAsRead(id)
    }
    @objc func eventOccurred(_ event: IntercomNexusEvent) {
        guard event.type == 13, let id = event.conversationId,
              let index = viewModel!.indexOfConversation(withId: id) else { return }
        guard let cell = tableView!.cellForRow(at: IndexPath(row: index, section: 0)) as? InboxCell else { return }
        let details: TypingAdminDetails?
        if event.showAvatar, let name = event.adminName, let image = event.adminAvatar {
            let shape = event.avatarShapeString.map { $0 == "squircle" ? AvatarShape.squircle : .circle } ?? (event.isBot ? .squircle : .circle)
            details = TypingAdminDetails(adminName: name, adminImageUrl: image, avatarShape: shape)
        } else { details = nil }
        if event.showAvatar {
            if !ICMUtils.contentSizeCategoryIsAccessibilityCategory() && cell.dotIndicatorView!.isHidden {
                cell.typingIndicatorState = TypingIndicatorState(details: details, timer: nil)
                cell.updateUIForTypingState(showTyping: true, details: details)
                cell.startTypingIndicatorTimer(with: details)
            }
        } else {
            cell.typingIndicatorState?.timer?.invalidate()
            cell.typingIndicatorState?.timer = nil
            cell.typingIndicatorState = nil
            cell.updateUIForTypingState(showTyping: false, details: nil)
        }
    }
    func numberOfSections(in tableView: UITableView) -> Int { viewModel!.numberOfSections() }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { viewModel!.numberOfRows(inSection: section) }
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) { viewModel!.willDisplayCell(at: indexPath) }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        viewModel!.didSelectRow(at: indexPath)
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section != 0 {
            guard let cell = tableView.dequeueReusableCell(withIdentifier: "InboxLoadingCellIdentifier", for: indexPath) as? ICMLoadingTableViewCell else { return UITableViewCell() }
            cell.startSpinning()
            return cell
        }
        guard let conversation = viewModel!.conversation(at: indexPath) else { return UITableViewCell() }
        if let ticket = conversation.ticket {
            guard let cell = tableView.dequeueReusableCell(withIdentifier: "TicketCellIdentifier", for: indexPath) as? TicketsListViewCell else { return UITableViewCell() }
            cell.configure(for: TicketsListViewCellItem(ticket: ticket, showDividerLine: true, canShowUnreadDot: true))
            return cell
        }
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "InboxCellIdentifier", for: indexPath) as? InboxCell else { return UITableViewCell() }
        let currentUserId = ICMIdentityStore.currentIdentity().userIdentity.intercomId
        cell.configure(for: InboxCellViewItem(conversation: conversation, currentUserIntercomId: currentUserId, canShowUnreadDot: true, showDividerLine: true, ticketsSpaceEnabled: viewModel!.config.isTicketsSpaceEnabled()))
        if let label = cell.accessibilityLabel {
            var text = TranslationManager.messenger.translation(forKey: "conversation_number_and_description")
            let parameters = ["conversation_number": String(indexPath.row + 1), "conversation_description": label]
            for (key, value) in parameters { text = text.replacingOccurrences(of: "{" + key + "}", with: value) }
            cell.accessibilityLabel = text
        }
        return cell
    }
    private func setupViewModel(viewModel: InboxViewModel?, dataManager: ICMDataManager, metricsService: MessagesMetricServiceProtocol, config: ICMConfigProtocol) {
        if let viewModel { self.viewModel = viewModel }
        else { self.viewModel = InboxViewModel(view: self, dataManager: dataManager, metricsService: metricsService, config: config, queue: AsyncQueue.main, openCallService: OpenCallService.shared, notificationCenter: NotificationCenter.default) }
    }
}
