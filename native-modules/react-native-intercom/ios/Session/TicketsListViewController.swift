import UIKit

@objc(_TtC18IntercomSDKPrivate25TicketsListViewController)
class TicketsListViewController: IntercomTableViewController, UITableViewDataSource, UITableViewDelegate, ICMErrorViewDelegate {
    var viewModel: TicketsListViewModel?
    let themeProvider: any ThemeProvider = DefaultThemeProvider.shared
    private lazy var tableView: UITableView = {
        let table = UITableView()
        table.dataSource = self
        table.delegate = self
        table.rowHeight = UITableView.automaticDimension
        table.estimatedRowHeight = 75
        table.separatorStyle = .none
        table.register(TicketsListViewCell.self, forCellReuseIdentifier: String(describing: TicketsListViewCell.self))
        table.register(ICMLoadingTableViewCell.self, forCellReuseIdentifier: String(describing: ICMLoadingTableViewCell.self))
        table.translatesAutoresizingMaskIntoConstraints = false
        return table
    }()
    private lazy var loadingView: UIView = {
        let image = ICMAssets.messagesLoadingImage()
        var controller: LoadingViewController? = LoadingViewController(image: image, accessibilityText: "loading")
        let loadedView = controller!.view
        controller = nil
        if let view = loadedView { view.translatesAutoresizingMaskIntoConstraints = false; return view }
        return UIView()
    }()
    private lazy var emptyStateView = TicketsListViewEmptyStateView(frame: .zero)
    private lazy var errorView: ICMInboxInfoView = {
        let message: String
        if let name = config.appName {
            message = TranslationManager.messenger.translation(forKey: "failed_to_load_tickets_with_app").replacingOccurrences(of: "{app_name}", with: name)
        } else { message = TranslationManager.messenger.translation(forKey: "failed_to_load_tickets") }
        let title = TranslationManager.messenger.translation(forKey: "spaces_error_title")
        let view = ICMInboxInfoView(title: title, message: message, showRetryButton: true, delegate: self)!
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    let config: any ICMConfigProtocol
    @objc(initWithConfig:)
    init(config: any ICMConfigProtocol) {
        self.config = config
        super.init(nibName: nil, bundle: nil)
    }
    @objc convenience init() {
        self.init(config: ICMConfig.shared())
        let interactor = TicketsListInteractor(dataManager: ICMDataManager.shared())
        let manager = ICMDataManager.shared()!
        let notifications = NotificationCenter.default
        let metric = MessagesMetricService(metricInstance: ICMMetric.sharedInstance())
        let config = ICMConfig.shared()!
        let service = OpenCallService.shared
        viewModel = TicketsListViewModel(view: self, interactor: interactor, dataManager: manager, notificationCenter: notifications, metricService: metric, config: config, openCallService: service)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    @objc var isCurrentlyShown: Bool { navigationController?.topViewController == self }
    override func viewDidLoad() {
        super.viewDidLoad()
        view!.backgroundColor = themeProvider.theme.base
        addSubviews()
        viewModel!.setViewTitle()
        viewModel!.handleOpenCallState()
        intercomNavigationController()?.displayCloseButton()
        navigationItem.backButtonDisplayMode = .minimal
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let navigationAnimation: Bool
        if #available(iOS 26.0, *) { navigationAnimation = false } else { navigationAnimation = animated }
        navigationController?.setNavigationBarHidden(false, animated: navigationAnimation)
    }
    override func viewIsAppearing(_ animated: Bool) {
        super.viewIsAppearing(animated)
        let model = viewModel!
        model.view?.render(model.currentState)
    }
    private func addSubviews() {
        view!.addSubview(tableView)
        view!.addSubview(loadingView)
        view!.addSubview(emptyStateView)
        view!.addSubview(errorView)
        pinEdges(tableView, safe: false)
        pinEdges(loadingView, safe: true)
        pinEdges(emptyStateView, safe: true)
        pinEdges(errorView, safe: true)
    }
    private func pinEdges(_ child: UIView, safe: Bool) {
        child.translatesAutoresizingMaskIntoConstraints = false
        if safe { child.topAnchor.constraint(equalTo: child.superview!.safeAreaLayoutGuide.topAnchor).isActive = true }
        else { child.topAnchor.constraint(equalTo: child.superview!.topAnchor).isActive = true }
        child.translatesAutoresizingMaskIntoConstraints = false
        if child.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            child.rightAnchor.constraint(equalTo: safe ? child.superview!.safeAreaLayoutGuide.rightAnchor : child.superview!.rightAnchor).isActive = true
            child.leftAnchor.constraint(equalTo: safe ? child.superview!.safeAreaLayoutGuide.leftAnchor : child.superview!.leftAnchor).isActive = true
        } else {
            child.leftAnchor.constraint(equalTo: safe ? child.superview!.safeAreaLayoutGuide.leftAnchor : child.superview!.leftAnchor).isActive = true
            child.rightAnchor.constraint(equalTo: safe ? child.superview!.safeAreaLayoutGuide.rightAnchor : child.superview!.rightAnchor).isActive = true
        }
        child.translatesAutoresizingMaskIntoConstraints = false
        child.bottomAnchor.constraint(equalTo: safe ? child.superview!.safeAreaLayoutGuide.bottomAnchor : child.superview!.bottomAnchor).isActive = true
    }
    @objc(setViewTitle:) func setViewTitle(_ title: String) { self.title = title }
    @objc(showTicket:) func showTicket(_ ticket: Ticket) {
        if let navigation = navigationController {
            ICMPresentationManager.sharedInstance().pushTicket(ticket, from: navigation, didCreateTicket: false, triggerSource: 1)
        }
    }
    @objc func didSelectErrorAction() { viewModel!.errorViewReloadButtonTapped() }
    func render(_ state: TicketsListViewState) {
        switch state {
        case .loading: hideAll(except: loadingView)
        case .success:
            hideAll(except: tableView)
            tableView.reloadData()
        case .error:
            hideAll(except: errorView)
            errorView.alpha = 0
            errorView.setAlphaAnimated(1, completion: nil)
        case .empty: hideAll(except: emptyStateView)
        }
    }
    private func hideAll(except exception: UIView) {
        let views: [UIView] = [tableView, loadingView, emptyStateView, errorView]
        for view in views { view.isHidden = !(view == exception) }
    }
    func numberOfSections(in tableView: UITableView) -> Int { viewModel!.dataManager.nextTicketsPageToFetch != nil ? 2 : 1 }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 { return viewModel!.tickets.count }
        if section == 1 { return viewModel!.tickets.isEmpty ? 0 : 1 }
        return 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if indexPath.section == 0 {
            let ticket = viewModel!.tickets[indexPath.row]
            let item = TicketsListViewCellItem(ticket: ticket, showDividerLine: true, canShowUnreadDot: true)
            let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: TicketsListViewCell.self), for: indexPath) as? TicketsListViewCell ?? TicketsListViewCell()
            cell.configure(for: item)
            return cell
        }
        if indexPath.section == 1 {
            let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: ICMLoadingTableViewCell.self), for: indexPath) as? ICMLoadingTableViewCell ?? ICMLoadingTableViewCell()
            cell.startSpinning()
            return cell
        }
        return UITableViewCell()
    }
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.section == 1, !viewModel!.fetchRequestInProgress,
           let page = viewModel!.dataManager.nextTicketsPageToFetch { viewModel!.fetchTickets(fromPage: page.intValue) }
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        viewModel!.didSelectRow(at: indexPath.row)
        tableView.deselectRow(at: indexPath, animated: true)
    }
}
