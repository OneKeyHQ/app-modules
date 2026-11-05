import UIKit

protocol QuickReplyDelegate: AnyObject {
    func quickReplyTappedFor(replyOption: ReplyOption)
}

@objc(_TtC18IntercomSDKPrivate14QuickReplyView)
class QuickReplyView: UIView {
    var replyOptions: [ReplyOption]
    private let maxWidth: CGFloat
    private let themeProvider: ThemeProvider
    private weak var delegate: (any QuickReplyDelegate)?
    lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .trailing
        stack.spacing = 12
        return stack
    }()
    init(replyOptions: [ReplyOption], delegate: (any QuickReplyDelegate)?, maxWidth: CGFloat, themeProvider: ThemeProvider) {
        self.replyOptions = replyOptions
        self.maxWidth = maxWidth
        self.delegate = delegate
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        addSubview(stackView)
        pinEdges(stackView)
        addButtons()
        NotificationCenter.default.addObserver(self, selector: #selector(preferredContentSizeChanged(_:)), name: UIContentSizeCategory.didChangeNotification, object: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    override var intrinsicContentSize: CGSize { stackView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize) }

    func addButtons() {
        var options = replyOptions
        var row = UIStackView()
        row.spacing = 10
        stackView.addArrangedSubview(row)
        var rowWidth: CGFloat = 0
        let width = maxWidth
        let provider = themeProvider
        while !options.isEmpty {
            let option = options[0]
            options.removeFirst()
            let button = QuickReplyButton(replyOption: option, maxWidth: width, themeProvider: provider)
            let buttonSize = button.calculatedSize
            let advance = buttonSize.width + row.spacing
            button.addTarget(self, action: #selector(quickReplyTapped(_:)), for: .touchUpInside)
            if width < rowWidth + advance {
                let next = UIStackView()
                next.spacing = 10
                stackView.addArrangedSubview(next)
                rowWidth = 0
                row = next
            }
            row.addArrangedSubview(button)
            rowWidth = advance + rowWidth
        }
    }
    @objc private func quickReplyTapped(_ button: QuickReplyButton) {
        if let delegate = delegate { delegate.quickReplyTappedFor(replyOption: button.replyOption) }
    }
    @objc private func preferredContentSizeChanged(_ notification: Notification) {
        stackView.removeAllArrangedSubviews()
        addButtons()
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
