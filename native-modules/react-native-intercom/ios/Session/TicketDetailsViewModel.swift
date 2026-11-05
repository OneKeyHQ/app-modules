import UIKit

@objc(_TtC18IntercomSDKPrivate22TicketDetailsViewModel)
class TicketDetailsViewModel: NSObject, ICMErrorViewDelegate {
    weak var view: TicketDetailsViewController?
    var interactor: TicketDetailsInteractor
    let identityStore: ICMIdentityStore
    var ticket: Ticket
    var conversationId: String
    let showSubmittedView: Bool
    let triggerSource: UInt
    let metricService: any MessagesMetricServiceProtocol
    init(view: TicketDetailsViewController, interactor: TicketDetailsInteractor, identityStore: ICMIdentityStore, ticket: Ticket, conversationId: String, showSubmittedView: Bool, triggerSource: UInt, metricService: any MessagesMetricServiceProtocol) {
        self.view = view
        self.interactor = interactor
        self.identityStore = identityStore
        self.ticket = ticket
        self.conversationId = conversationId
        self.showSubmittedView = showSubmittedView
        self.triggerSource = triggerSource
        self.metricService = metricService
        super.init()
    }
    @objc(initWithViewController:ticket:showSubmittedView:triggerSource:)
    convenience init(viewController: TicketDetailsViewController, ticket: Ticket, showSubmittedView: Bool, triggerSource: UInt) {
        let ticketId = ticket.id
        let conversationId = ticket.conversationId
        let nexus = IntercomNexusClient.shared()!
        let manager = ICMDataManager.shared()!
        let notifications = NotificationCenter.default
        let interactor = TicketDetailsInteractor(nexusClient: nexus, dataManager: manager, ticketId: ticketId, conversationId: conversationId, notificationCenter: notifications)
        let identity = ICMIdentityStore.currentIdentity()
        let metric = MessagesMetricService(metricInstance: ICMMetric.sharedInstance())
        self.init(view: viewController, interactor: interactor, identityStore: identity, ticket: ticket, conversationId: ticket.conversationId, showSubmittedView: showSubmittedView, triggerSource: triggerSource, metricService: metric)
    }
    override init() { fatalError("init() has not been implemented") }
    @objc var errorDelegate: any ICMErrorViewDelegate { self }
    @objc func viewDidLoad() {
        guard let view else { return }
        metricService.addTicketDetailsViewedEvent(triggerSource: Int(truncatingIfNeeded: triggerSource), ticketTypeId: ticket.typeId, conversationId: conversationId, ticketStatus: ticket.currentStatus.stage.rawValue)
        interactor.ticketUpdatingDelegate = self
        view.titleLabel.text = ticket.title
        if triggerSource >= 1 && triggerSource <= 3 { fetchAndRenderTicket() }
        else { renderTicket() }
        withExtendedLifetime(view) {}
    }
    @objc func viewWillAppear() { interactor.nexusClient.addObserver(interactor) }
    @objc func viewWillDisappear() { interactor.nexusClient.removeObserver(interactor) }
    func renderTicket() {
        guard let view else { return }
        view.titleLabel.text = ticket.title
        view.ticketDetailsHeaderView.update(with: ticket)
        view.ticketDetailsHeaderView.alpha = showSubmittedView ? 0 : 1
        view.updateNotificationDetails(notificationDetails())
        view.updateAttributes(ticket.attributes)
        if showSubmittedView { view.displaySubmittedView() }
        if triggerSource != 0, let info = ticket.conversationButtonInfo {
            let image: UIImage?
            switch info.iconType {
            case .send: image = ICMAssets.startConversationImage()
            case .conversation: image = ICMAssets.messageSpaceIcon()
            case .unknown: image = ICMAssets.startConversationImage()
            }
            view.showConversationButton(title: info.title, icon: image!, target: self, action: #selector(didTapConversationButton))
        }
    }
    func ticketDidUpdate(_ ticket: Ticket) {
        guard let view else { return }
        if ticket == self.ticket { return }
        self.ticket = ticket
        view.ticketDetailsHeaderView.update(with: ticket)
        view.ticketDetailsHeaderView.alpha = 1
        view.updateNotificationDetails(notificationDetails())
        view.updateAttributes(ticket.attributes)
    }
    @objc func didTapConversationButton() {
        let conversation = ICMConversation()
        conversation.conversationId = ticket.conversationId
        conversation.ticket = ticket
        guard let view else { return }
        ICMPresentationManager.sharedInstance().showConversation(conversation, from: view.intercomNavigationController())
    }
    func fetchAndRenderTicket() {
        do {
            if let view {
                view.loadingView.startSpinning()
                view.loadingViewContainer.isHidden = false
                withExtendedLifetime(view) {}
            }
        }
        interactor.fetchTicket { [weak self] result in
            guard let self, let view = self.view else { return }
            UIView.transition(with: view.view!, duration: 0.25, options: UIView.AnimationOptions(rawValue: 0x500000), animations: {
                view.loadingView.spinnerImageView.layer.removeAnimation(forKey: "spin")
                view.loadingView.isHidden = true
            }, completion: { _ in view.loadingViewContainer.isHidden = true })
            switch result {
            case .failure: view.errorView?.isHidden = false
            case .success(let ticket):
                self.ticket = ticket
                self.renderTicket()
            }
        }
    }
    @objc func didSelectErrorAction() {
        do { if let view { view.errorView?.isHidden = true } }
        fetchAndRenderTicket()
    }
    private func notificationDetails() -> NSAttributedString? {
        guard let email = identityStore.userIdentity.email else { return nil }
        let paragraph = NSMutableParagraphStyle()
        paragraph.lineSpacing = UIFontMetrics(forTextStyle: .subheadline).scaledValue(for: 4)
        let regular: [NSAttributedString.Key: Any] = [.font: UIFont.preferredFont(forTextStyle: .subheadline), .paragraphStyle: paragraph]
        let bold: [NSAttributedString.Key: Any] = [.font: UIFont.systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .semibold), .paragraphStyle: paragraph]
        let text = TranslationManager.messenger.translation(forKey: "youll_be_notified_here_and_by_email") + "\n"
        let result = NSMutableAttributedString(string: text, attributes: bold)
        result.append(NSAttributedString(string: email, attributes: regular))
        return result
    }
}
