import UIKit

@objc(_TtC18IntercomSDKPrivate30HelpCenterSearchViewController)
class HelpCenterSearchViewController: UIViewController, UISearchResultsUpdating, UITableViewDataSource, UITableViewDelegate, HelpCenterStartConversationViewDelegate {
    private enum State { case searchedList([InternalHelpCenterArticleSearchResult]), error(String), emptyView }
    weak var containedViewDelegate: HelpCenterContainerViewController?
    private let tableView = UITableView()
    private let config: any ICMConfigProtocol
    private let dataManager: any ICMDataManagerProtocol
    private var state: State? {
        didSet {
            DispatchQueue.main.async {
                if case .emptyView? = self.state { self.view.alpha = 0 } else { self.view.alpha = 1 }
                self.tableView.reloadData()
                self.updateStartConversationView()
                self.updateTableFooterHeightIfNeeded()
            }
        }
    }
    private let themeProvider: DefaultThemeProvider
    private let translationManager = TranslationManager.helpCenter
    private let cellReuseIdentifier = "searchResultTableViewCellReuseIdentifier"
    private let tableViewCellEstimatedHeight: CGFloat = 78
    private let errorDividerMargins: CGFloat = 160
    private let errorLabelInsets = UIEdgeInsets(top: 20, left: 20, bottom: 20, right: 20)
    private let helpCenterService: HelpCenterService
    private let metricService: any HelpCenterMetricServiceProtocol
    private var debounceWorkItem: DispatchWorkItem?
    private var searchMetricFired = false
    private var lastSearchQuery: String?
    init(helpCenterService: HelpCenterService, metricService: any HelpCenterMetricServiceProtocol, config: any ICMConfigProtocol, dataManager: any ICMDataManagerProtocol, themeProvider: DefaultThemeProvider) {
        self.config = config
        self.dataManager = dataManager
        state = .emptyView
        self.themeProvider = themeProvider
        self.helpCenterService = helpCenterService
        self.metricService = metricService
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    deinit { NotificationCenter.default.removeObserver(self) }
    override func viewDidLoad() {
        super.viewDidLoad()
        title = TranslationManager.helpCenter.translation(forKey: "search_for_help")
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        view.accessibilityIdentifier = "intercom help center search list view controller"
        navigationItem.backButtonDisplayMode = .minimal
        setupTableView()
    }
    private func setupTableView() {
        tableView.dataSource = self
        tableView.delegate = self
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = tableViewCellEstimatedHeight
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.separatorStyle = .none
        view.addSubview(tableView)
        tableView.leadingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leadingAnchor).isActive = true
        tableView.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor).isActive = true
        tableView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor).isActive = true
        tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        tableView.register(HelpCenterSearchResultTableViewCell.self, forCellReuseIdentifier: cellReuseIdentifier)
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let flag: Bool
        if #available(iOS 26.0, *) { flag = false } else { flag = animated }
        navigationController?.setNavigationBarHidden(false, animated: flag)
        NotificationCenter.default.addObserver(self, selector: #selector(adjustForKeyboard(_:)), name: UIResponder.keyboardDidHideNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(adjustForKeyboard(_:)), name: UIResponder.keyboardDidChangeFrameNotification, object: nil)
        searchMetricFired = false
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateTableFooterHeightIfNeeded()
    }
    func updateSearchResults(for searchController: UISearchController) {
        searchController.searchResultsController?.view.isHidden = false
        let bar = searchController.searchBar
        debounceWorkItem?.cancel()
        guard let text = bar.text else { state = .emptyView; return }
        let query = text.trimmingCharacters(in: .whitespaces)
        if query.count < 1 { state = .emptyView; return }
        if !searchMetricFired { metricService.addSearchEvent(); searchMetricFired = true }
        let work = DispatchWorkItem { [weak self] in self?.resultsForSearchText(query) }
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.4, execute: work)
        debounceWorkItem = work
    }
    private func resultsForSearchText(_ query: String) {
        let source = metricService.contextIsSearchBrowse ? "search_browse" : nil
        lastSearchQuery = query
        helpCenterService.fetchSearchResults(for: query, source: source) { [weak self] result in
            switch result {
            case .success(let results):
                if !results.isEmpty { self?.state = .searchedList(results) }
                else if let owner = self {
                    let translator = self?.translationManager
                    let message = translator?.translation(forKey: "search_browse_empty_state_text") ?? ""
                    owner.state = .error(message)
                }
            case .failure(let error):
                if let owner = self {
                    let translator = self?.translationManager
                    let message = translator?.translation(forKey: "something_went_wrong_try_again") ?? ""
                    owner.state = .error(message)
                }
                let metric = self?.metricService
                metric?.addSearchResultsFailedEvent(errorCode: error.statusCode)
            }
        }
    }
    @objc(adjustForKeyboardWithNotification:)
    private func adjustForKeyboard(_ notification: Notification) {
        guard let frame = notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue else { return }
        let converted = view.convert(frame.cgRectValue, from: view.window)
        let tables: [UITableView]
        let parent = containedViewDelegate
        let child = parent?.currentChildViewController
        if let controller = child as? HelpCenterCollectionListViewController {
            tables = [tableView, controller.tableView]
        } else if let controller = child as? HelpCenterArticleListViewController {
            tables = [tableView, controller.tableView]
        } else { tables = [tableView] }
        for table in tables {
            if notification.name == UIResponder.keyboardDidHideNotification { table.contentInset = .zero }
            else { table.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: converted.height - view.safeAreaInsets.bottom + 16, right: 0) }
        }
        for table in tables { table.scrollIndicatorInsets = table.contentInset }
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch state {
        case .searchedList(let results)?: return results.count
        case .error?: return 1
        default: return 0
        }
    }
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        if case .searchedList? = state { return UITableView.automaticDimension }
        return 0
    }
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        guard case .searchedList? = state else { return nil }
        let header = HelpCenterArticleListSectionHeaderView()
        let title = translationManager.translation(forKey: "article_search_search_results")
        header.sectionTitleLabel.text = title
        header.accessibilityLabel = title
        return header
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch state {
        case .searchedList(let results)?:
            guard let cell = self.tableView.dequeueReusableCell(withIdentifier: cellReuseIdentifier) as? HelpCenterSearchResultTableViewCell else { return UITableViewCell() }
            let result = results[indexPath.row]
            let title = result.highlight?.title ?? result.title
            let summary = result.highlight?.summary ?? result.summary
            var hide = false
            if indexPath.row == results.count - 1 {
                let show: Bool
                if case .error? = state { show = config.canStartConversation() }
                else { show = dataManager.helpCenterArticleViewed && config.canStartConversation() }
                hide = !show
            }
            cell.configure(title: title, summary: summary, hideDivider: hide)
            cell.accessibilityIdentifier = "intercom help center search list content cell"
            return cell
        case .error(let message)?: return createErrorLabelCell(message)
        default: return UITableViewCell()
        }
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard case .searchedList(let results)? = state else { return }
        let row = indexPath.row
        if row >= results.count { return }
        let id = results[row].id
        if let query = lastSearchQuery { metricService.addClickSearchEvent(searchQuery: query) }
        tableView.deselectRow(at: indexPath, animated: true)
        let controller = HelpCenterArticleContainerViewController(withArticle: id, metricService: metricService, dataManager: ICMDataManager.shared(), themeProvider: DefaultThemeProvider.shared)
        presentingViewController?.navigationController?.pushViewController(controller, animated: true)
    }
    func didSelectStartConversation() {
        let manager = ICMPresentationManager.sharedInstance()
        let conversation = ICMConversation()
        manager?.showConversation(conversation)
        metricService.addNewConversationFromSearchEvent(accessToTeammate: config.accessToTeammateEnabled)
    }
    private func createErrorLabelCell(_ message: String) -> UITableViewCell {
        let cell = UITableViewCell()
        cell.selectionStyle = .none
        let label = UILabel()
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.font = .preferredFont(forTextStyle: .body)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.textAlignment = .center
        let provider = themeProvider
        label.textColor = provider.theme.textMuted
        label.text = message
        cell.contentView.addSubview(label)
        NSLayoutConstraint.activate([
            label.leadingAnchor.constraint(equalTo: cell.contentView.leadingAnchor, constant: errorLabelInsets.left),
            label.topAnchor.constraint(equalTo: cell.contentView.topAnchor, constant: errorLabelInsets.top),
            label.trailingAnchor.constraint(equalTo: cell.contentView.trailingAnchor, constant: -errorLabelInsets.right),
            label.bottomAnchor.constraint(equalTo: cell.contentView.bottomAnchor, constant: -errorLabelInsets.bottom)
        ])
        let show: Bool
        if case .error? = state { show = config.canStartConversation() }
        else { show = dataManager.helpCenterArticleViewed && config.canStartConversation() }
        if show {
            let divider = UIView()
            divider.translatesAutoresizingMaskIntoConstraints = false
            divider.backgroundColor = provider.theme.neutralContainerEmphasis
            cell.contentView.addSubview(divider)
            NSLayoutConstraint.activate([
                divider.heightAnchor.constraint(equalToConstant: 1), divider.bottomAnchor.constraint(equalTo: cell.contentView.bottomAnchor),
                divider.centerXAnchor.constraint(equalTo: cell.contentView.centerXAnchor), divider.widthAnchor.constraint(equalTo: cell.contentView.widthAnchor, constant: -errorDividerMargins * 2)
            ])
        }
        cell.accessibilityIdentifier = "intercom help center search list error cell"
        return cell
    }
    func updateStartConversationView() {
        let manager = dataManager
        if let cta = manager.conversationCTA, tableView.tableFooterView == nil {
            let show: Bool
            if case .error? = state { show = config.canStartConversation() }
            else { show = manager.helpCenterArticleViewed && config.canStartConversation() }
            if show {
                tableView.tableFooterView = HelpCenterStartConversationView(frame: .zero, useFilledButtonStyle: false, delegate: self, conversationCTA: cta, themeProvider: DefaultThemeProvider.shared)
                tableView.reloadData()
                return
            }
        }
        let show: Bool
        if case .error? = state { show = config.canStartConversation() }
        else { show = manager.helpCenterArticleViewed && config.canStartConversation() }
        if !show { tableView.tableFooterView = nil }
    }
    private func updateTableFooterHeightIfNeeded() {
        let table = tableView
        guard let footer = table.tableFooterView else { return }
        let previous = footer.frame.height
        let height = footer.systemLayoutSizeFitting(CGSize(width: table.bounds.width, height: 0)).height
        if previous == height { return }
        var frame = footer.frame
        frame.size.height = height
        footer.frame = frame
        table.tableFooterView = footer
    }
}
