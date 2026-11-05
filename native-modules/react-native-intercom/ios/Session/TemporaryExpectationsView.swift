import UIKit

@objc(_TtC18IntercomSDKPrivate25TemporaryExpectationsView)
class TemporaryExpectationsView: UIView {
    private let themeProvider: ThemeProvider
    private lazy var infoIconImageView: UIImageView = {
        let icon = UIImageView(image: ICMAssets.informationIcon())
        icon.tintColor = themeProvider.theme.iconDefault
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.widthAnchor.constraint(equalToConstant: 16).isActive = true
        icon.heightAnchor.constraint(equalToConstant: 16).isActive = true
        return icon
    }()
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
        layer.cornerRadius = 20
        layer.borderWidth = 1
        layer.borderColor = themeProvider.theme.neutralBorder.cgColor
        backgroundColor = DefaultThemeProvider.shared.theme.base
        let attributes: [NSAttributedString.Key: Any] = [.font: UIFont.preferredFont(forTextStyle: .footnote), .foregroundColor: themeProvider.theme.textMuted]
        let view = textView
        view.attributedText = ITBHTMLParser.attributedString(fromHTML: text, defaultAttributes: attributes, codeAttributes: [:])
        if !ICMUtils.contentSizeCategoryIsAccessibilityCategory() {
            addSubview(infoIconImageView)
            infoIconImageView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16).isActive = true
            infoIconImageView.topAnchor.constraint(equalTo: topAnchor, constant: 12).isActive = true
        }
        addSubview(textView)
        let top = textView.topAnchor.constraint(equalTo: topAnchor, constant: 12)
        let bottom = textView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -12)
        let target = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? leadingAnchor : infoIconImageView.trailingAnchor
        let constant: CGFloat = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? 16 : 8
        let leading = textView.leadingAnchor.constraint(equalTo: target, constant: constant)
        let trailing = textView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -16)
        NSLayoutConstraint.activate([top, bottom, leading, trailing])
    }
}
