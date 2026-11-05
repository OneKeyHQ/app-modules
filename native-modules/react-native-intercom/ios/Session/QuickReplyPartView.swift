import UIKit

@objc(_TtC18IntercomSDKPrivate18QuickReplyPartView)
class QuickReplyPartView: UIView, ConversationCellContentView, QuickReplyDelegate {
    let content: ICMConversationPart
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private let maxWidth: CGFloat
    private let themeProvider: ThemeProvider
    private lazy var quickReplyButtonView: QuickReplyView = {
        let options = content.replyOptionsArray! as! [ReplyOption]
        return QuickReplyView(replyOptions: options, delegate: self, maxWidth: maxWidth, themeProvider: themeProvider)
    }()
    init(content: ICMConversationPart, delegate: (any ConversationCellBlockDelegate)?, maxWidth: CGFloat, themeProvider: ThemeProvider) {
        self.content = content
        self.delegate = delegate
        self.maxWidth = maxWidth
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(quickReplyButtonView)
        pinEdges(quickReplyButtonView)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    func quickReplyTappedFor(replyOption: ReplyOption) {
        let part = content
        let partId = part.isInitialMessage ? nil : part.quickReplyPartId
        delegate?.quickReplyTapped(replyOption: replyOption, quickReplyPartId: partId, conversationPart: part)
    }
    private func pinEdges(_ view: UIView) {
        let topParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.topAnchor.constraint(equalTo: topParent.topAnchor).isActive = true
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
        let bottomParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
}
