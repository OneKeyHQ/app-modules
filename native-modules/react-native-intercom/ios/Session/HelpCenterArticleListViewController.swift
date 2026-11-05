import UIKit

@objc(_TtC18IntercomSDKPrivate35HelpCenterArticleListViewController)
class HelpCenterArticleListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, HelpCenterStartConversationViewDelegate, HelpCenterBrowseTableViewCellDelegate {
    // Concrete default transport preserves the observed weak owner; private generic injection ABI remains unrecovered.
    weak var contentDelegate: HelpCenterContainerViewController?
    var showBrowseFullHelpCenterOption = false
    let tableView = UITableView()
    private let config: any ICMConfigProtocol
    private let dataManager: any ICMDataManagerProtocol
    private let articleCellReuseIdentifier = "articleTableViewCellReuseIdentifier"
    private let browseAllCellReuseIdentifier = "browseTableViewCellReuseIdentifier"
    private let content: InternalHelpCenterCollectionContent
    private let metricService: any HelpCenterMetricServiceProtocol
    init(collection: InternalHelpCenterCollectionContent, metricService: any HelpCenterMetricServiceProtocol, config: any ICMConfigProtocol, dataManager: any ICMDataManagerProtocol) {
        self.content = collection
        self.metricService = metricService
        self.config = config
        self.dataManager = dataManager
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        title = content.title
        navigationItem.backButtonDisplayMode = .minimal
        view!.accessibilityIdentifier = "intercom help center article list view controller"
        setupTableView()
        UIAccessibility.post(notification: .screenChanged, argument: tableView)
        NotificationCenter.default.addObserver(self, selector: #selector(inboxDidUpdate), name: .init("intercom-sdk-inbox-updated"), object: nil)
    }
    private func setupTableView() {
        tableView.dataSource = self
        tableView.delegate = self
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 78
        tableView.sectionHeaderTopPadding = 0
        tableView.separatorStyle = .none
        tableView.translatesAutoresizingMaskIntoConstraints = false
        let provider = DefaultThemeProvider.shared
        tableView.backgroundColor = provider.theme.base
        view!.addSubview(tableView)
        pinTableEdges()
        tableView.register(HelpCenterArticleTableViewCell.self, forCellReuseIdentifier: articleCellReuseIdentifier)
        tableView.register(HelpCenterBrowseTableViewCell.self, forCellReuseIdentifier: browseAllCellReuseIdentifier)
        tableView.tableHeaderView = HelpCenterArticleListTableHeaderView(collection: content, frame: CGRect(x: 0, y: 0, width: 0, height: 140), themeProvider: provider)
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let flag: Bool
        if #available(iOS 26.0, *) { flag = false } else { flag = animated }
        navigationController?.setNavigationBarHidden(false, animated: flag)
    }
    override func viewIsAppearing(_ animated: Bool) {
        super.viewIsAppearing(animated)
        updateStartConversationView()
        if let path = tableView.indexPathForSelectedRow { tableView.deselectRow(at: path, animated: true) }
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateTableHeaderHeightIfNeeded()
        updateTableFooterHeightIfNeeded()
    }
    @objc private func inboxDidUpdate() { updateStartConversationView() }
    func numberOfSections(in tableView: UITableView) -> Int { showBrowseFullHelpCenterOption ? 2 : 1 }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        showBrowseFullHelpCenterOption && section == 1 ? 1 : content.articles.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if showBrowseFullHelpCenterOption && indexPath.section == 1 {
            guard let cell = self.tableView.dequeueReusableCell(withIdentifier: browseAllCellReuseIdentifier) as? HelpCenterBrowseTableViewCell else { return UITableViewCell() }
            cell.delegate = self
            let text = TranslationManager.helpCenter.translation(forKey: "browse_all_help_topics")
            cell.titleLabel.text = text
            cell.accessibilityLabel = text
            return cell
        }
        guard let cell = self.tableView.dequeueReusableCell(withIdentifier: articleCellReuseIdentifier) as? HelpCenterArticleTableViewCell else { return UITableViewCell() }
        let article = content.articles[indexPath.row]
        let title = article.title
        let divider = indexPath.row != content.articles.count - 1
        cell.articleView.configure(name: title, showDividerLine: divider, isHomeScreen: false)
        cell.accessibilityIdentifier = title
        return cell
    }
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? { UIView() }
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? { UIView() }
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat { showBrowseFullHelpCenterOption && section == 1 ? 0 : 8 }
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if section == (showBrowseFullHelpCenterOption ? 1 : 0), dataManager.helpCenterArticleViewed, config.canStartConversation() { return 0 }
        return 15
    }
    func tableView(_ tableView: UITableView, willSelectRowAt indexPath: IndexPath) -> IndexPath? { showBrowseFullHelpCenterOption && indexPath.section == 1 ? nil : indexPath }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let article = content.articles[indexPath.row]
        let id = article.id
        let metric = metricService
        let manager = ICMDataManager.shared()!
        let provider = DefaultThemeProvider.shared
        let controller = HelpCenterArticleContainerViewController(withArticle: id, metricService: metric, dataManager: manager, themeProvider: provider)
        navigationController?.pushViewController(controller, animated: true)
    }
    func didTapBrowseAllHelpTopicsButton() {
        let controller = HelpCenterContainerViewController(delegate: nil)
        navigationController?.pushViewController(controller, animated: true)
    }
    func didSelectStartConversation() {
        let manager = ICMPresentationManager.sharedInstance()
        let conversation = ICMConversation()
        manager?.showConversation(conversation)
        metricService.addNewConversationFromArticleListEvent(accessToTeammate: config.accessToTeammateEnabled)
    }
    private func updateStartConversationView() {
        let manager = dataManager
        if let cta = manager.conversationCTA, tableView.tableFooterView == nil {
            if manager.helpCenterArticleViewed && config.canStartConversation() {
                tableView.tableFooterView = HelpCenterStartConversationView(frame: .zero, useFilledButtonStyle: false, delegate: self, conversationCTA: cta, themeProvider: DefaultThemeProvider.shared)
                tableView.reloadData()
                return
            }
        }
        if !(manager.helpCenterArticleViewed && config.canStartConversation()) { tableView.tableFooterView = nil }
    }
    private func updateTableHeaderHeightIfNeeded() {
        let table = tableView
        guard let header = table.tableHeaderView else { return }
        let previous = header.frame.height
        let height = header.systemLayoutSizeFitting(CGSize(width: table.bounds.width, height: 0)).height
        if previous == height { return }
        var frame = header.frame
        frame.size.height = height
        header.frame = frame
        table.tableHeaderView = header
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
    private func pinTableEdges() {
        let table = tableView
        let parent = table.superview!
        table.translatesAutoresizingMaskIntoConstraints = false
        table.translatesAutoresizingMaskIntoConstraints = false
        let top = table.topAnchor.constraint(equalTo: parent.topAnchor); top.priority = .required; top.isActive = true
        let rtl = table.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            table.translatesAutoresizingMaskIntoConstraints = false; table.translatesAutoresizingMaskIntoConstraints = false
            let edge = table.leftAnchor.constraint(equalTo: parent.leftAnchor); edge.priority = .required; edge.isActive = true
        }
        func right() {
            table.translatesAutoresizingMaskIntoConstraints = false; table.translatesAutoresizingMaskIntoConstraints = false
            let edge = table.rightAnchor.constraint(equalTo: parent.rightAnchor); edge.priority = .required; edge.isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
        table.translatesAutoresizingMaskIntoConstraints = false; table.translatesAutoresizingMaskIntoConstraints = false
        let bottom = table.bottomAnchor.constraint(equalTo: parent.bottomAnchor); bottom.priority = .required; bottom.isActive = true
    }
}
