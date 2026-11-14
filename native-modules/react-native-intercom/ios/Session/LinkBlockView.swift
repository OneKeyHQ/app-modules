import UIKit

@objc(_TtC18IntercomSDKPrivate13LinkBlockView)
class LinkBlockView: UIView, ConversationCellContentView {
    let content: ITBLinkBlock
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private lazy var blockTapGesture = UITapGestureRecognizer(target: self, action: #selector(blockTapped))
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.numberOfLines = 0
        return label
    }()

    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?) {
        guard let content = content as? ITBLinkBlock else { return nil }
        self.content = content
        self.delegate = delegate
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        addGestureRecognizer(blockTapGesture)
        configureTitleLabel()
        configureAccessibility()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private func configureTitleLabel() {
        let label = titleLabel
        let original = content.titleAttributedString
        // The original inlined Foundation nil-string bridge constructs an empty mutable string.
        label.attributedText = original?.convertToScalableAttributedString() ?? NSMutableAttributedString(string: String._unconditionallyBridgeFromObjectiveC(nil))
        addSubview(titleLabel)
        NSLayoutConstraint.activate([
            titleLabel.leadingAnchor.constraint(equalTo: leadingAnchor),
            titleLabel.trailingAnchor.constraint(equalTo: trailingAnchor),
            titleLabel.topAnchor.constraint(equalTo: topAnchor),
            titleLabel.bottomAnchor.constraint(equalTo: bottomAnchor),
        ])
    }
    private func configureAccessibility() {
        if let text = titleLabel.attributedText { accessibilityLabel = "Article title: " + text.string }
        else { accessibilityLabel = "" }
    }
    @objc private func blockTapped() {
        guard let id = content.articleId else { return }
        delegate?.openArticle(articleId: id)
    }
}
