import UIKit

struct PreviewAction {
    enum Mode { case edit, view }
    enum ButtonType {
        case text(title: String)
        case image(title: String?, image: UIImage)
        case system(item: UIBarButtonItem.SystemItem)
    }
    let mode: Mode
    let buttonType: ButtonType
    let handler: (([URL]) -> Void)?
}
