import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate37TicketCreateFormStandardInputCellItem)
class TicketCreateFormStandardInputCellItem: TicketCreateFormInputCellItem, TicketCreateFormCellRefreshable {
    let cellType: TicketCreateFormTableViewCell.Type
    let attribute: FormAttribute
    let detail: String?
    let isEnabled: Bool
    let userIsIdentified: Bool
    let refreshAction: () -> Void
    let onValueChange: ((Any?) -> Void)?
    var valid = false
    var validityMessage: String?
    weak var cellDelegate: (any TicketCreateFormCellRefreshable)?
    init(cellType: TicketCreateFormTableViewCell.Type, attribute: FormAttribute, detail: String?, isEnabled: Bool, refreshAction: @escaping () -> Void, onValueChange: ((Any?) -> Void)?) {
        self.cellType = cellType
        self.attribute = attribute
        self.detail = detail
        self.isEnabled = isEnabled
        userIsIdentified = ICMIdentityStore.currentIdentity().userIdentity.isIdentified()
        self.refreshAction = refreshAction
        self.onValueChange = onValueChange
    }
    func refresh() { cellDelegate?.refresh() }
}
