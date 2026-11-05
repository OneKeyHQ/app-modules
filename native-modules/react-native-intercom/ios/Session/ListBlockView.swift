import UIKit

@objc(_TtC18IntercomSDKPrivate13ListBlockView)
class ListBlockView: UIView, ConversationCellContentView {
    let content: ITBListBlock
    private lazy var textStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 6
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private weak var citationDelegate: (any CitationTapDelegate)?
    private weak var blockDelegate: (any ConversationCellBlockDelegate)?

    init?(content: ITBBlock, citationDelegate: (any CitationTapDelegate)?, blockDelegate: (any ConversationCellBlockDelegate)?) {
        guard let content = content as? ITBListBlock else { return nil }
        self.content = content
        self.citationDelegate = citationDelegate
        self.blockDelegate = blockDelegate
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        if let blocks = content.textBlocks {
            let views = blocks.compactMap { block in
                TextBlockView(content: block, citationDelegate: self.citationDelegate, blockDelegate: self.blockDelegate)
            }
            addSubview(textStackView)
            // Original edge helpers perform both outer and anchor-level translation writes.
            textStackView.translatesAutoresizingMaskIntoConstraints = false
            textStackView.translatesAutoresizingMaskIntoConstraints = false
            let top = textStackView.topAnchor.constraint(equalTo: textStackView.superview!.topAnchor, constant: 0)
            top.priority = .required
            top.isActive = true
            let isRTL = textStackView.effectiveUserInterfaceLayoutDirection.rawValue != 0
            if isRTL {
                textStackView.translatesAutoresizingMaskIntoConstraints = false
                textStackView.translatesAutoresizingMaskIntoConstraints = false
                let right = textStackView.rightAnchor.constraint(equalTo: textStackView.superview!.rightAnchor, constant: -CGFloat.zero)
                right.priority = .required
                right.isActive = true
                textStackView.translatesAutoresizingMaskIntoConstraints = false
                textStackView.translatesAutoresizingMaskIntoConstraints = false
                let left = textStackView.leftAnchor.constraint(equalTo: textStackView.superview!.leftAnchor, constant: 0)
                left.priority = .required
                left.isActive = true
            } else {
                textStackView.translatesAutoresizingMaskIntoConstraints = false
                textStackView.translatesAutoresizingMaskIntoConstraints = false
                let left = textStackView.leftAnchor.constraint(equalTo: textStackView.superview!.leftAnchor, constant: 0)
                left.priority = .required
                left.isActive = true
                textStackView.translatesAutoresizingMaskIntoConstraints = false
                textStackView.translatesAutoresizingMaskIntoConstraints = false
                let right = textStackView.rightAnchor.constraint(equalTo: textStackView.superview!.rightAnchor, constant: -CGFloat.zero)
                right.priority = .required
                right.isActive = true
            }
            textStackView.translatesAutoresizingMaskIntoConstraints = false
            textStackView.translatesAutoresizingMaskIntoConstraints = false
            let bottom = textStackView.bottomAnchor.constraint(equalTo: textStackView.superview!.bottomAnchor, constant: -CGFloat.zero)
            bottom.priority = .required
            bottom.isActive = true
            textStackView.addArrangedSubviews(views)
        }
        isAccessibilityElement = true
        accessibilityLabel = content.plainTextIncludingListItems()
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
}
