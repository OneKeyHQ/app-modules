import UIKit

@objc(_TtC18IntercomSDKPrivate38HelpCenterCollectionListViewController)
class HelpCenterCollectionListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, HelpCenterStartConversationViewDelegate, HelpCenterBrowseTableViewCellDelegate {
    private enum Section: CaseIterable, Hashable { case articles, collections }
    weak var contentDelegate: HelpCenterContainerViewController?
    var showBrowseFullHelpCenterOption = false
    let tableView = UITableView()
    private let config: any ICMConfigProtocol
    private let dataManager: any ICMDataManagerProtocol
    private weak var delegate: (any ICMContentModalViewControllerDelegate)?
    private let sections: [Section] = [.articles, .collections]
    private let collectionCellReuseIdentifier = "collectionTableViewCellReuseIdentifier"
    private let browseAllCellReuseIdentifier = "browseTableViewCellReuseIdentifier"
    private let articleCellReuseIdentifier = "articleTableViewCellReuseIdentifier"
    private let collections: [InternalHelpCenterCollection]
    private let collection: InternalHelpCenterCollectionContent?
    private let metricService: any HelpCenterMetricServiceProtocol
    init(collections: [InternalHelpCenterCollection], metricService: any HelpCenterMetricServiceProtocol, config: any ICMConfigProtocol, dataManager: any ICMDataManagerProtocol, delegate: (any ICMContentModalViewControllerDelegate)?, collection: InternalHelpCenterCollectionContent?) {
        self.config = config
        self.dataManager = dataManager
        self.delegate = delegate
        self.collections = collections
        self.collection = collection
        self.metricService = metricService
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        title = collection?.title ?? config.title(forSpace: 1) ?? TranslationManager.helpCenter.translation(forKey: "help_space_title")
        navigationItem.backButtonDisplayMode = .minimal
        view!.accessibilityIdentifier = "intercom help center collection list view controller"
        setupTableView()
    }
    private func setupTableView() {
        let provider = DefaultThemeProvider.shared
        tableView.backgroundColor = provider.theme.base
        tableView.dataSource = self
        tableView.delegate = self
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 78
        tableView.separatorStyle = .none
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableView.accessibilityIdentifier = "intercom help center collections table View"
        view!.addSubview(tableView)
        pinTableEdges()
        tableView.register(HelpCenterCollectionTableViewCell.self, forCellReuseIdentifier: collectionCellReuseIdentifier)
        tableView.register(HelpCenterBrowseTableViewCell.self, forCellReuseIdentifier: browseAllCellReuseIdentifier)
        tableView.register(HelpCenterArticleTableViewCell.self, forCellReuseIdentifier: articleCellReuseIdentifier)
        if let collection {
            tableView.tableHeaderView = HelpCenterArticleListTableHeaderView(collection: collection, frame: CGRect(x: 0, y: 0, width: 0, height: 140), themeProvider: provider)
        } else {
            tableView.tableHeaderView = HelpCenterCollectionListTableHeaderView(collectionCount: collections.count, frame: CGRect(x: 0, y: 0, width: 0, height: 54), collectionName: nil, themeProvider: provider)
        }
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let flag: Bool
        if #available(iOS 26.0, *) { flag = false } else { flag = animated }
        navigationController?.setNavigationBarHidden(false, animated: flag)
        UIAccessibility.post(notification: .screenChanged, argument: tableView)
        NotificationCenter.default.addObserver(self, selector: #selector(inboxDidUpdate), name: .init("intercom-sdk-inbox-updated"), object: nil)
    }
    override func viewIsAppearing(_ animated: Bool) {
        super.viewIsAppearing(animated)
        updateStartConversationView()
        if let path = tableView.indexPathForSelectedRow { tableView.deselectRow(at: path, animated: true) }
        UIAccessibility.post(notification: .screenChanged, argument: tableView)
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateTableHeaderHeightIfNeeded()
        updateTableFooterHeightIfNeeded()
    }
    @objc private func inboxDidUpdate() { updateStartConversationView() }
    func numberOfSections(in tableView: UITableView) -> Int { sections.count }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch sections[section] {
        case .collections: return collections.count + (showBrowseFullHelpCenterOption ? 1 : 0)
        case .articles: return (collection?.articles ?? []).count
        }
    }
    private func isLastCell(_ path: IndexPath) -> Bool {
        switch sections[path.section] {
        case .collections: return path.row == collections.count - (showBrowseFullHelpCenterOption ? 0 : 1)
        case .articles: return path.row == (collection?.articles ?? []).count - 1
        }
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch sections[indexPath.section] {
        case .collections:
            if isLastCell(indexPath) && showBrowseFullHelpCenterOption {
                guard let cell = self.tableView.dequeueReusableCell(withIdentifier: browseAllCellReuseIdentifier) as? HelpCenterBrowseTableViewCell else { return UITableViewCell() }
                cell.delegate = self
                let text = TranslationManager.helpCenter.translation(forKey: "browse_all_help_topics")
                cell.titleLabel.text = text
                cell.accessibilityLabel = text
                return cell
            }
            guard let cell = self.tableView.dequeueReusableCell(withIdentifier: collectionCellReuseIdentifier) as? HelpCenterCollectionTableViewCell else { return UITableViewCell() }
            let collection = collections[indexPath.row]
            let divider = !isLastCell(indexPath)
            cell.configure(forCollection: collection, showDividerLine: divider)
            return cell
        case .articles:
            guard let cell = self.tableView.dequeueReusableCell(withIdentifier: articleCellReuseIdentifier) as? HelpCenterArticleTableViewCell else { return UITableViewCell() }
            let article = (collection?.articles ?? [])[indexPath.row]
            let title = article.title
            let divider = !isLastCell(indexPath)
            cell.articleView.configure(name: title, showDividerLine: divider, isHomeScreen: false)
            cell.accessibilityIdentifier = title
            return cell
        }
    }
    func tableView(_ tableView: UITableView, willSelectRowAt indexPath: IndexPath) -> IndexPath? {
        if sections[indexPath.section] == .collections && isLastCell(indexPath) && showBrowseFullHelpCenterOption { return nil }
        return indexPath
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch sections[indexPath.section] {
        case .collections: presentCollection(collections[indexPath.row])
        case .articles:
            let article = (collection?.articles ?? [])[indexPath.row]
            let id = article.id
            let metric = metricService
            let manager = ICMDataManager.shared()!
            let provider = DefaultThemeProvider.shared
            let controller = HelpCenterArticleContainerViewController(withArticle: id, metricService: metric, dataManager: manager, themeProvider: provider)
            navigationController?.pushViewController(controller, animated: true)
        }
    }
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? { UIView() }
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        sections[section] == .articles && !(collection?.articles ?? []).isEmpty ? 20 : 0
    }
    private func presentCollection(_ collection: InternalHelpCenterCollection) {
        let id = collection.id
        let metric = metricService
        let controller: HelpCenterContainerViewController
        do { let delegate = delegate; controller = HelpCenterContainerViewController(delegate: delegate) }
        controller.metricService = metric
        controller.isFilteredHelpCenter = true
        controller.contentType = .collection(id)
        controller.isFilteredHelpCenter = false
        navigationController?.pushViewController(controller, animated: true)
        DispatchQueue.main.async {
            guard let parent = self.contentDelegate else { return }
            if parent.searchController.isActive { parent.searchController.isActive = false }
        }
    }
    func didTapBrowseAllHelpTopicsButton() {
        let controller: HelpCenterContainerViewController
        do { let delegate = delegate; controller = HelpCenterContainerViewController(delegate: delegate) }
        navigationController?.pushViewController(controller, animated: true)
    }
    func didSelectStartConversation() {
        let manager = ICMPresentationManager.sharedInstance()
        let conversation = ICMConversation()
        manager?.showConversation(conversation)
        metricService.addNewConversationFromCollectionListEvent(accessToTeammate: config.accessToTeammateEnabled)
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
