// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

protocol TicketCreateFormCellItem {
    var cellType: TicketCreateFormTableViewCell.Type { get }
}
protocol TicketCreateFormCellRefreshable: AnyObject { func refresh() }
protocol TicketCreateFormActionCellItemDelegate: TicketCreateFormCellRefreshable {
    func setActionButtonState(_ enabled: Bool, isLoading: Bool)
}
protocol TicketCreateFormActionCellItemProtocol: TicketCreateFormCellItem, TicketCreateFormCellRefreshable {
    // The requirement spelling follows the concrete descriptor and original inlined callers.
    func setActionButtonState(_ enabled: Bool, isLoading: Bool)
}
protocol TicketCreateFormInputCellItem: TicketCreateFormCellItem {
    var attribute: FormAttribute { get }
    var detail: String? { get }
    var isEnabled: Bool { get }
    var valid: Bool { get set }
    var validityMessage: String? { get set }
    var refreshAction: () -> Void { get }
    // The deleted final getter's name follows both original conformers' exact stored field.
    var onValueChange: ((Any?) -> Void)? { get }
}
