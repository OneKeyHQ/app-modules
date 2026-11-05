import UIKit

@objc(_TtC18IntercomSDKPrivate15ButtonBlockView)
class ButtonBlockView: UIView, ConversationCellContentView {
    let content: ITBButtonBlock
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private lazy var button: UIButton = {
        let button = UIButton(type: .custom)
        var configuration = UIButton.Configuration.plain()
        configuration.contentInsets = NSDirectionalEdgeInsets(top: 5, leading: 5, bottom: 5, trailing: 5)
        var attributes = AttributeContainer()
        attributes.foregroundColor = content.textColor
        attributes.font = UIFont.preferredFont(forTextStyle: .subheadline)
        configuration.attributedTitle = AttributedString(content.text ?? "", attributes: attributes)
        configuration.titleAlignment = .center
        var background = configuration.background
        background.backgroundColor = content.backgroundColor
        background.cornerRadius = 4
        configuration.background = background
        configuration.cornerStyle = .fixed
        configuration.contentInsets = NSDirectionalEdgeInsets(top: 0, leading: 8, bottom: 0, trailing: 8)
        button.configuration = configuration
        button.addTarget(self, action: #selector(buttonTapped), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()

    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?) {
        guard let content = content as? ITBButtonBlock else { return nil }
        self.content = content
        self.delegate = delegate
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configure()
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func configure() {
        addSubview(button)
        NSLayoutConstraint.activate([
            button.leadingAnchor.constraint(equalTo: leadingAnchor),
            button.trailingAnchor.constraint(equalTo: trailingAnchor),
            button.bottomAnchor.constraint(equalTo: bottomAnchor),
            button.topAnchor.constraint(equalTo: topAnchor),
            button.heightAnchor.constraint(greaterThanOrEqualToConstant: 44),
        ])
    }

    @objc private func buttonTapped() {
        guard let string = content.url, let url = URL(string: string) else { return }
        if let delegate { delegate.open(url: url) }
        else { LinkOpener.shared.openLink(url) }
    }
}
