import UIKit

enum HomescreenDestination {
    case article(String)
    case link(URL)
    case conversation(ICMConversation)
    case ticket(Ticket)
    case appSheet(URL, params: [AnyHashable: Any], delegate: ICMMessengerSheetViewControllerDelegate?)
    case createTicket(TicketType)
    case helpCenter
    case helpCenterSearch
    case newConversation
    case messages
    case tickets
}
protocol HomescreenCardViewItem: AnyObject {
    func buildView() -> HomescreenCardView
}
protocol HomescreenCardDelegate: AutoMockable {
    func navigate(to destination: HomescreenDestination)
}
protocol HomescreenCardReloadable {
    func reload()
}
protocol RecentCardMetricsDelegate: AnyObject {
    func addMetricForRecentConversationsClick(with conversation: ICMConversation)
    func addMetricForRecentTicketsClick()
}
