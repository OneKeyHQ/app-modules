// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate19ObservableStackView)
class ObservableStackView: UIStackView {
    var sizeDidChange: ((CGSize) -> Void)?
    override init(frame: CGRect) { super.init(frame: frame) }
    required init(coder: NSCoder) { super.init(coder: coder) }
    override func layoutSubviews() {
        super.layoutSubviews()
        guard let callback = sizeDidChange else { return }
        withExtendedLifetime(self) { callback(bounds.size) }
    }
}
