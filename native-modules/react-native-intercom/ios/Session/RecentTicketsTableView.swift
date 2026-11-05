import UIKit

@objc(_TtC18IntercomSDKPrivate22RecentTicketsTableView)
class RecentTicketsTableView: UITableView, UITableViewDataSource, UITableViewDelegate {
    // The original single-conformer protocol requirement was WMO-deleted.
    weak var recentTicketsViewDelegate: RecentTicketsHomescreenCardViewItem?
    private var tableViewHeightConstraint: NSLayoutConstraint?
    private let dataManager: any ICMDataManagerProtocol
    private lazy var recentTickets: [Ticket] = [Ticket]._unconditionallyBridgeFromObjectiveC(dataManager.getRecentTickets() as NSArray?)
    private var shouldRefresh = false
    init(dataManager: any ICMDataManagerProtocol) {
        self.dataManager = dataManager
        super.init(frame: .zero, style: .plain)
        NotificationCenter.default.addObserver(self, selector: #selector(ticketsDidUpdate), name: .init("intercom-sdk-tickets-updated"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(refreshTicketsIfNeeded), name: .init("IntercomRootViewControllerDidDismissPresentedController"), object: nil)
        setUpTableView()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect, style: UITableView.Style) { fatalError("init(frame:style:) has not been implemented") }
    deinit { NotificationCenter.default.removeObserver(self) }
    private func setUpTableView() {
        register(TicketsListViewCell.self, forCellReuseIdentifier: String(describing: TicketsListViewCell.self))
        dataSource = self
        delegate = self
        separatorColor = .clear
        isScrollEnabled = false
        rowHeight = UITableView.automaticDimension
        estimatedRowHeight = 75
        translatesAutoresizingMaskIntoConstraints = false
        tableViewHeightConstraint = heightAnchor.constraint(equalToConstant: 0)
        tableViewHeightConstraint?.priority = .init(750)
        tableViewHeightConstraint?.isActive = true
    }
    func updateWithLatestTickets() {
        recentTickets = [Ticket]._unconditionallyBridgeFromObjectiveC(dataManager.getRecentTickets() as NSArray?)
        reloadData()
        layoutIfNeeded()
        if let constraint = tableViewHeightConstraint { constraint.constant = contentSize.height }
    }
    override func traitCollectionDidChange(_ previous: UITraitCollection?) {
        super.traitCollectionDidChange(previous)
        reloadData()
        layoutIfNeeded()
        if let constraint = tableViewHeightConstraint { constraint.constant = contentSize.height }
        layoutIfNeeded()
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int { recentTickets.count }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: String(describing: TicketsListViewCell.self), for: indexPath)
        guard let cell = cell as? TicketsListViewCell else { return UITableViewCell() }
        let row = indexPath.row
        let last = tableView.numberOfRows(inSection: indexPath.section) - 1
        let ticket = recentTickets[indexPath.row]
        cell.configure(for: TicketsListViewCellItem(ticket: ticket, showDividerLine: row != last, canShowUnreadDot: true))
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        guard let delegate = recentTicketsViewDelegate else { return }
        let ticket = recentTickets[indexPath.row]
        delegate.didSelectTicket(ticket)
    }
    @objc private func ticketsDidUpdate() {
        if window != nil { updateWithLatestTickets() }
        else { shouldRefresh = true }
    }
    @objc private func refreshTicketsIfNeeded() {
        if shouldRefresh { updateWithLatestTickets(); shouldRefresh = false }
    }
}
