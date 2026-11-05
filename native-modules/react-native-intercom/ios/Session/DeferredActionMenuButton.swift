// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate24DeferredActionMenuButton)
class DeferredActionMenuButton: UIButton {
    var pendingAction: (() -> Void)?
    var iconSizeConstraints: [NSLayoutConstraint] = []
    override init(frame: CGRect) { super.init(frame: frame) }
    required init?(coder: NSCoder) { super.init(coder: coder) }

    override func contextMenuInteraction(_ interaction: UIContextMenuInteraction,
                                         willEndFor configuration: UIContextMenuConfiguration,
                                         animator: (any UIContextMenuInteractionAnimating)?) {
        super.contextMenuInteraction(interaction, willEndFor: configuration, animator: animator)
        let action = pendingAction
        pendingAction = nil
        if let animator { animator.addCompletion { action?() } }
        else { action?() }
    }
}
