import UIKit

@objc(_TtC18IntercomSDKPrivate21MessengerAppBlockView)
class MessengerAppBlockView: UIView, ConversationCellContentView {
    let content: ITBMessengerCardFallbackBlock
    private lazy var blockViewContainer: UIView = {
        let view = UIView()
        view.clipsToBounds = true
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        return view
    }()
    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?, singleBlockView: Bool) {
        guard let content = content as? ITBMessengerCardFallbackBlock else { return nil }
        self.content = content
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configure(with: delegate, singleBlockView: singleBlockView)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func configure(with delegate: (any ConversationCellBlockDelegate)?, singleBlockView: Bool) {
        guard let inner = MessengerAppConversationView(content: content, delegate: delegate, dismissAppSheet: { [weak delegate] in delegate?.dismissAppSheet() }) else { return }
        let styled = delegate?.shouldApplyCardStyleToMessengerApps() ?? false
        if styled {
            blockViewContainer.layer.cornerRadius = 20
            blockViewContainer.layer.borderWidth = 1
            blockViewContainer.layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        }
        blockViewContainer.addSubview(inner)
        addSubview(blockViewContainer)
        blockViewContainer.translatesAutoresizingMaskIntoConstraints = false
        blockViewContainer.topAnchor.constraint(equalTo: topAnchor).isActive = true
        let rtl = blockViewContainer.effectiveUserInterfaceLayoutDirection == .rightToLeft
        if rtl { blockViewContainer.rightAnchor.constraint(equalTo: rightAnchor, constant: -CGFloat.zero).isActive = true }
        else { blockViewContainer.leftAnchor.constraint(equalTo: leftAnchor).isActive = true }
        if !styled {
            if rtl { blockViewContainer.leftAnchor.constraint(equalTo: leftAnchor).isActive = true }
            else { blockViewContainer.rightAnchor.constraint(equalTo: rightAnchor, constant: -CGFloat.zero).isActive = true }
        }
        blockViewContainer.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -CGFloat.zero).isActive = true
        if styled {
            blockViewContainer.widthAnchor.constraint(lessThanOrEqualToConstant: 280).isActive = true
            let trailing = blockViewContainer.trailingAnchor.constraint(equalTo: trailingAnchor)
            trailing.priority = UILayoutPriority(rawValue: 999)
            trailing.isActive = true
        }
        NSLayoutConstraint.activate([
            inner.leadingAnchor.constraint(equalTo: blockViewContainer.leadingAnchor, constant: singleBlockView ? 0 : 8),
            inner.trailingAnchor.constraint(equalTo: blockViewContainer.trailingAnchor, constant: singleBlockView ? 0 : -8),
            inner.topAnchor.constraint(equalTo: blockViewContainer.topAnchor),
            inner.bottomAnchor.constraint(equalTo: blockViewContainer.bottomAnchor, constant: singleBlockView ? 0 : -16),
        ])
    }
}
