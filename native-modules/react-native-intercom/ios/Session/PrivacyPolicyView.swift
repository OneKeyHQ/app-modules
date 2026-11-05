// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate17PrivacyPolicyView)
class PrivacyPolicyView: UIView {
    let themeProvider: DefaultThemeProvider
    private lazy var closeButton: UIButton = {
        let button = UIButton()
        button.setImage(ICMAssets.closeImage(), for: .normal)
        button.imageView?.tintColor = themeProvider.theme.iconMuted
        button.translatesAutoresizingMaskIntoConstraints = false
        button.widthAnchor.constraint(equalToConstant: 10).isActive = true
        button.heightAnchor.constraint(equalToConstant: 10).isActive = true
        button.addAction(for: .touchUpInside) { [weak capturedDelegate = delegate] _ in
            capturedDelegate?.dismissPrivacyPolicy()
        }
        return button
    }()
    private lazy var descriptionTextView: HyperLinkTextView = {
        let text = HyperLinkTextView()
        text.isEditable = false
        text.isSelectable = false
        text.font = .preferredFont(forTextStyle: .footnote)
        text.translatesAutoresizingMaskIntoConstraints = false
        text.textContainerInset = .zero
        text.contentInset = .zero
        text.textContainer.lineFragmentPadding = 0
        text.isScrollEnabled = true
        text.linkTextAttributes = [.foregroundColor: themeProvider.theme.textMuted]
        return text
    }()
    weak var delegate: ConversationViewModel?

    init(delegate: ConversationViewModel?, themeProvider: DefaultThemeProvider) {
        self.delegate = delegate
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        configure()
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    private func configure() {
        backgroundColor = themeProvider.theme.neutralContainer
        layer.cornerRadius = 12
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(closeButton)
        closeButton.centerYAnchor.constraint(equalTo: closeButton.superview!.centerYAnchor).isActive = true
        closeButton.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16).isActive = true
        addSubview(descriptionTextView)
        descriptionTextView.translatesAutoresizingMaskIntoConstraints = false
        descriptionTextView.topAnchor.constraint(equalTo: descriptionTextView.superview!.topAnchor, constant: 16).isActive = true
        _ = descriptionTextView.effectiveUserInterfaceLayoutDirection
        descriptionTextView.translatesAutoresizingMaskIntoConstraints = false
        descriptionTextView.bottomAnchor.constraint(equalTo: descriptionTextView.superview!.bottomAnchor, constant: -16).isActive = true
        descriptionTextView.translatesAutoresizingMaskIntoConstraints = false
        descriptionTextView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16).isActive = true
        descriptionTextView.translatesAutoresizingMaskIntoConstraints = false
        descriptionTextView.trailingAnchor.constraint(equalTo: closeButton.leadingAnchor, constant: -8).isActive = true
    }

    func setPrivacyPolicyText(_ html: String) {
        let attributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: themeProvider.theme.textMuted,
            .font: UIFont.preferredFont(forTextStyle: .footnote)
        ]
        let text = descriptionTextView
        text.attributedText = ITBHTMLParser.attributedString(fromHTML: html, defaultAttributes: attributes, codeAttributes: [:])
        descriptionTextView.layoutIfNeeded()
        let width = descriptionTextView.frame.size.width
        let height = descriptionTextView.sizeThatFits(CGSize(width: width, height: .greatestFiniteMagnitude)).height
        let capped = height > 100 ? 100 : height
        descriptionTextView.translatesAutoresizingMaskIntoConstraints = false
        let constraint = descriptionTextView.heightAnchor.constraint(equalToConstant: capped)
        constraint.priority = UILayoutPriority(999)
        constraint.isActive = true
    }
}
