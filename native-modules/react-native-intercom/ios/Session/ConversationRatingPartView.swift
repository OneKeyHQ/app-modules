import UIKit

@objc(_TtC18IntercomSDKPrivate26ConversationRatingPartView)
class ConversationRatingPartView: BlockPartView {
    override init(content: ICMConversationPart, maxWidth: CGFloat, delegate: (any ConversationCellBlockDelegate)?, queue: any Dispatcher) {
        super.init(content: content, maxWidth: maxWidth, delegate: delegate, queue: queue)
    }
    // The original has only the inherited Objective-C coder trap.
    @nonobjc required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    override func layoutSubviews() {
        super.layoutSubviews()
        let stack = blockStackView
        stack.layer.cornerRadius = 20
        stack.layer.borderWidth = 1
        stack.layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        stack.backgroundColor = DefaultThemeProvider.shared.theme.base
        stack.clipsToBounds = true
    }
}
