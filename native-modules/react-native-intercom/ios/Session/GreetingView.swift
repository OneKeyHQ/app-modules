import UIKit

@objc(_TtC18IntercomSDKPrivate12GreetingView)
class GreetingView: UIView {
    private let themeProvider: ThemeProvider
    private lazy var textView: HyperLinkTextView = {
        let view = HyperLinkTextView()
        view.isEditable = false
        view.isSelectable = false
        view.font = .preferredFont(forTextStyle: .footnote)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.textContainerInset = .zero
        view.contentInset = .zero
        view.textContainer.lineFragmentPadding = 0
        view.linkTextAttributes = [.foregroundColor: themeProvider.theme.textMuted]
        return view
    }()
    init(themeProvider: ThemeProvider) {
        self.themeProvider = themeProvider
        super.init(frame: .zero)
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    func configure(with text: String) {
        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        let attributes: [NSAttributedString.Key: Any] = [.font: UIFont.preferredFont(forTextStyle: .footnote), .foregroundColor: themeProvider.theme.textMuted, .paragraphStyle: paragraph]
        let view = textView
        view.attributedText = ITBHTMLParser.attributedString(fromHTML: text, defaultAttributes: attributes, codeAttributes: [:])
        addSubview(textView)
        NSLayoutConstraint.activate([
            textView.topAnchor.constraint(equalTo: topAnchor),
            textView.leadingAnchor.constraint(equalTo: leadingAnchor),
            textView.bottomAnchor.constraint(equalTo: bottomAnchor),
            textView.trailingAnchor.constraint(equalTo: trailingAnchor)
        ])
    }
}
