import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate30TicketCreateFormActionCellItem)
class TicketCreateFormActionCellItem: TicketCreateFormActionCellItemProtocol {
    let cellType: TicketCreateFormTableViewCell.Type
    let buttonTitle: String
    let buttonImage: UIImage?
    let buttonAction: () -> Void
    weak var cellDelegate: (any TicketCreateFormActionCellItemDelegate)?
    var isLoading = false
    init(cellType: TicketCreateFormTableViewCell.Type, buttonTitle: String, buttonImage: UIImage?, buttonAction: @escaping () -> Void) {
        self.cellType = cellType
        self.buttonTitle = buttonTitle
        self.buttonImage = buttonImage
        self.buttonAction = buttonAction
    }
    func refresh() { cellDelegate?.refresh() }
    func setActionButtonState(_ enabled: Bool, isLoading: Bool) { cellDelegate?.setActionButtonState(enabled, isLoading: isLoading) }
}
