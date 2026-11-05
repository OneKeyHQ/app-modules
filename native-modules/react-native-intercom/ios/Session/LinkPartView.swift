import UIKit

@objc(_TtC18IntercomSDKPrivate12LinkPartView)
class LinkPartView: UIView, ConversationCellContentView {
    let content: ICMConversationPart
    private let themeProvider: DefaultThemeProvider
    private lazy var dayLabel: UILabel = {
        let label = UILabel()
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .footnote)
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var askedAboutLabel: UILabel = {
        let label = UILabel()
        label.text = TranslationManager.messenger.translation(forKey: "asked_about")
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .footnote)
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var blockViewContainer: UIView = {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.base
        view.clipsToBounds = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.layer.cornerRadius = 20
        view.layer.borderWidth = 1
        view.layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        return view
    }()
    init(content: ICMConversationPart, themeProvider: DefaultThemeProvider, delegate: (any ConversationCellBlockDelegate)?) {
        self.content = content
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configure(delegate: delegate)
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    private func configure(delegate: (any ConversationCellBlockDelegate)?) {
        let part = content
        if part.isArticleContextCard() {
            addSubview(askedAboutLabel)
            let label = askedAboutLabel
            let parent = label.superview!
            label.translatesAutoresizingMaskIntoConstraints = false
            label.translatesAutoresizingMaskIntoConstraints = false
            label.topAnchor.constraint(equalTo: parent.topAnchor).isActive = true
            pinHorizontal(label)
        }
        addSubview(blockViewContainer)
        let top = blockViewContainer.topAnchor
        let anchor = part.isArticleContextCard() ? askedAboutLabel.bottomAnchor : topAnchor
        let spacing: CGFloat = part.isArticleContextCard() ? 11 : 0
        NSLayoutConstraint.activate([
            top.constraint(equalTo: anchor, constant: spacing),
            blockViewContainer.leadingAnchor.constraint(equalTo: leadingAnchor),
            blockViewContainer.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        blockViewContainer.widthAnchor.constraint(lessThanOrEqualToConstant: 366).isActive = true
        let trailing = blockViewContainer.trailingAnchor.constraint(equalTo: trailingAnchor)
        trailing.priority = UILayoutPriority(999)
        trailing.isActive = true
        isAccessibilityElement = false
        accessibilityElements = [dayLabel, askedAboutLabel]
        let blocks: [ITBBlock] = part.blocks!
        guard let first = blocks.first, let link = first as? ITBLinkBlock,
              let view = LinkBlockView(content: link, delegate: delegate) else { return }
        blockViewContainer.addSubview(view)
        NSLayoutConstraint.activate([
            view.leadingAnchor.constraint(equalTo: blockViewContainer.leadingAnchor, constant: 18),
            view.trailingAnchor.constraint(equalTo: blockViewContainer.trailingAnchor, constant: -18),
            view.topAnchor.constraint(equalTo: blockViewContainer.topAnchor, constant: 18),
            view.bottomAnchor.constraint(equalTo: blockViewContainer.bottomAnchor, constant: -18)
        ])
        blockViewContainer.isAccessibilityElement = true
        blockViewContainer.accessibilityLabel = view.accessibilityLabel
        blockViewContainer.accessibilityTraits = .link
        if var elements = accessibilityElements {
            elements.append(blockViewContainer)
            accessibilityElements = elements
        } else { accessibilityElements = nil }
    }
    private func pinHorizontal(_ view: UIView) {
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor, constant: 0).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() }
        else { left(); right() }
    }
}
