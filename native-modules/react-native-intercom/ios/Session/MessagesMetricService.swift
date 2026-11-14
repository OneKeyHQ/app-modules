import Foundation

// Functional requirements recovered from concrete methods; original private witness slots were deleted.
protocol MessagesMetricServiceProtocol {
    func addMessagesViewedMetric()
    func addNewConversationFromMessagesMetric()
    func addConversationClickedFromHomeMetric(ticket: Ticket?)
    func addConversationClickedFromMessagesMetric(ticket: Ticket?)
    func addWeRunOnIntercomClickedMetric()
    func addTicketClickedFromHomeMetric()
    func addCreateTicketFormPresentedEvent(conversationId: String, ticketTypeId: Int)
    func addNewConversationFromHomeMetric()
    func addTicketsViewedMetric()
    func addTicketDetailsViewedEvent(triggerSource: Int, ticketTypeId: Int, conversationId: String, ticketStatus: String)
    func addCreateTicketFormIsSubmitted(conversationId: String?, ticketTypeId: Int)
}

@objc(_TtC18IntercomSDKPrivate21MessagesMetricService)
class MessagesMetricService: NSObject, MessagesMetricServiceProtocol {
    private let metricInstance: ICMMetricProtocol?

    init(metricInstance: ICMMetricProtocol?) {
        self.metricInstance = metricInstance
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    private enum PrivateMetaData {
        case articleId(String), ticketTypeId(Int), conversationId(String), ticketStatus(String)
        var pair: (String, Any) {
            switch self {
            case .articleId(let value): return ("article_id", value)
            case .ticketTypeId(let value): return ("ticket_type_id", value)
            case .conversationId(let value): return ("conversation_id", value)
            case .ticketStatus(let value): return ("ticket_status", value)
            }
        }
    }
    private func add(action: String, object: String, context: String, metadata: [PrivateMetaData]? = nil) {
        let info = metadata.map { entries -> [AnyHashable: Any] in
            var result: [AnyHashable: Any] = [:]
            for entry in entries { let (key, value) = entry.pair; result[key] = value }
            return result
        }
        let event = ICMMetricEvent(action: action, object: object, place: "messenger", context: context, additionalInfo: info)
        event?.eventType = 0
        metricInstance?.addEvent(event)
    }
    func addMessagesViewedMetric() { add(action: "viewed", object: "messages", context: "no_context") }
    func addNewConversationFromMessagesMetric() { add(action: "clicked", object: "new_conversation", context: "messages") }
    func addConversationClickedFromHomeMetric(ticket: Ticket?) {
        let metadata: [PrivateMetaData]? = ticket.map { [.ticketTypeId($0.typeId), .ticketStatus($0.currentStatus.stage.rawValue)] }
        add(action: "clicked", object: "conversation", context: "home", metadata: metadata)
    }
    func addConversationClickedFromMessagesMetric(ticket: Ticket?) {
        let metadata: [PrivateMetaData]? = ticket.map { [.ticketTypeId($0.typeId), .ticketStatus($0.currentStatus.stage.rawValue)] }
        add(action: "clicked", object: "conversation", context: "messages", metadata: metadata)
    }
    func addWeRunOnIntercomClickedMetric() { add(action: "clicked", object: "we_run_on_intercom", context: "messages") }
    func addTicketClickedFromHomeMetric() { add(action: "clicked", object: "recent_tickets", context: "home") }
    func addCreateTicketFormPresentedEvent(conversationId: String, ticketTypeId: Int) {
        add(action: "viewed", object: "create_ticket_form", context: "conversation", metadata: [.conversationId(conversationId), .ticketTypeId(ticketTypeId)])
    }
    func addNewConversationFromHomeMetric() { add(action: "clicked", object: "new_conversation", context: "home") }
    func addTicketsViewedMetric() { add(action: "viewed", object: "tickets", context: "no_context") }
    func addTicketDetailsViewedEvent(triggerSource: Int, ticketTypeId: Int, conversationId: String, ticketStatus: String) {
        let context: String
        switch triggerSource {
        case 1: context = "from_tickets_space"
        case 2: context = "home_screen"
        default: context = "conversation"
        }
        add(action: "viewed", object: "ticket_details", context: context, metadata: [.ticketTypeId(ticketTypeId), .conversationId(conversationId), .ticketStatus(ticketStatus)])
    }
    func addCreateTicketFormIsSubmitted(conversationId: String?, ticketTypeId: Int) {
        var metadata: [PrivateMetaData] = [.ticketTypeId(ticketTypeId)]
        if let conversationId { metadata.append(.conversationId(conversationId)) }
        add(action: "submitted", object: "create_ticket_form", context: "conversation", metadata: metadata)
    }

    @objc static func addMessagesViewedMetric() { MessagesMetricService(metricInstance: ICMMetric.sharedInstance()).addMessagesViewedMetric() }
    @objc static func addNewConversationFromMessagesMetric() { MessagesMetricService(metricInstance: ICMMetric.sharedInstance()).addNewConversationFromMessagesMetric() }
    @objc(addConversationClickedFromHomeMetricWithTicket:)
    static func addConversationClickedFromHomeMetric(ticket: Ticket?) { MessagesMetricService(metricInstance: ICMMetric.sharedInstance()).addConversationClickedFromHomeMetric(ticket: ticket) }
    @objc(addConversationClickedFromMessagesMetricWithTicket:)
    static func addConversationClickedFromMessagesMetric(ticket: Ticket?) { MessagesMetricService(metricInstance: ICMMetric.sharedInstance()).addConversationClickedFromMessagesMetric(ticket: ticket) }
    @objc static func addWeRunOnIntercomClickedMetric() { MessagesMetricService(metricInstance: ICMMetric.sharedInstance()).addWeRunOnIntercomClickedMetric() }
    @objc static func addTicketClickedFromHomeMetric() { MessagesMetricService(metricInstance: ICMMetric.sharedInstance()).addTicketClickedFromHomeMetric() }
    @objc(addCreateTicketFormPresentedEventWithConversationId:ticketTypeId:)
    static func addCreateTicketFormPresentedEvent(conversationId: String, ticketTypeId: Int) { MessagesMetricService(metricInstance: ICMMetric.sharedInstance()).addCreateTicketFormPresentedEvent(conversationId: conversationId, ticketTypeId: ticketTypeId) }
}
