import UIKit

@objc(_TtC18IntercomSDKPrivate13CodeBlockView)
class CodeBlockView: UIView, ConversationCellContentView {
    enum Mode {
        case inline((() -> Void)?)
        case expanded(() -> Void)
    }
    private enum CopyState { case idle, copied }

    let content: CodeBlock
    private lazy var trailingHeaderButton: HeaderIconButton? = makeTrailingHeaderButton()
    private var copyState: CopyState = .idle {
        didSet { copyButton.iconView?.image = (copyState == .copied ? ICMAssets.copiedIcon() : ICMAssets.copyIcon())?.withRenderingMode(.alwaysTemplate) }
    }
    private weak var blockDelegate: (any ConversationCellBlockDelegate)?
    private let maxWidth: CGFloat?
    private let themeProvider: any ThemeProvider
    private let copier: CodeBlockCopier
    private let scheduler: DefaultScheduler
    private let mode: Mode
    private lazy var languageLabel: UILabel = {
        let label = UILabel()
        label.font = UIFontMetrics(forTextStyle: .subheadline).scaledFont(for: .systemFont(ofSize: 14, weight: .regular))
        label.adjustsFontForContentSizeCategory = true
        label.textColor = themeProvider.theme.textMuted
        label.numberOfLines = 1
        label.lineBreakMode = .byTruncatingTail
        label.setContentHuggingPriority(.defaultLow, for: .horizontal)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var copyButton: HeaderIconButton = {
        let button = HeaderIconButton(image: ICMAssets.copyIcon(), themeProvider: themeProvider)
        let key = "copy_to_clipboard"
        let label = TranslationManager.messenger.translation(forKey: key)
        button.accessibilityLabel = label == key ? "Copy this code block to your clipboard." : label
        button.accessibilityTraits.insert(.button)
        button.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        button.addTarget(self, action: #selector(copyTapped), for: .touchUpInside)
        return button
    }()
    private lazy var headerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.isLayoutMarginsRelativeArrangement = true
        stack.directionalLayoutMargins = NSDirectionalEdgeInsets(top: 8, leading: 16, bottom: 8, trailing: 12)
        stack.addArrangedSubviews([languageLabel, copyButton])
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private let scrollView: UIScrollView
    private let codeScrollView: UIScrollView
    private lazy var gutterTextView: UnwrappedTextView = {
        let view = UnwrappedTextView(frame: .zero, textContainer: nil)
        view.accessibilityIdentifier = "code block gutter"
        view.isSelectable = false
        view.backgroundColor = .clear
        view.isAccessibilityElement = false
        view.accessibilityElementsHidden = true
        return view
    }()
    private lazy var codeTextView: UnwrappedTextView = {
        let view = UnwrappedTextView(frame: .zero, textContainer: nil)
        view.accessibilityIdentifier = "code block code"
        view.onLinkTap = { [weak self] url in
            if let delegate = self?.blockDelegate { delegate.open(url: url) }
            else { LinkOpener.shared.openLink(url) }
        }
        return view
    }()

    init?(content: ITBBlock, blockDelegate: (any ConversationCellBlockDelegate)?, maxWidth: CGFloat?, themeProvider: any ThemeProvider, copier: CodeBlockCopier = CodeBlockCopier(), scheduler: DefaultScheduler = DefaultScheduler(), mode: Mode) {
        guard let content = content as? CodeBlock else { return nil }
        let scroll = UIScrollView()
        scroll.showsVerticalScrollIndicator = false
        scroll.showsHorizontalScrollIndicator = false
        scroll.isDirectionalLockEnabled = true
        scroll.semanticContentAttribute = .forceLeftToRight
        scroll.translatesAutoresizingMaskIntoConstraints = false
        scrollView = scroll
        let horizontal = UIScrollView()
        horizontal.accessibilityIdentifier = "code block horizontal scroller"
        horizontal.showsVerticalScrollIndicator = false
        horizontal.showsHorizontalScrollIndicator = false
        horizontal.isDirectionalLockEnabled = true
        horizontal.semanticContentAttribute = .forceLeftToRight
        horizontal.translatesAutoresizingMaskIntoConstraints = false
        codeScrollView = horizontal
        self.content = content
        self.blockDelegate = blockDelegate
        self.maxWidth = maxWidth
        self.themeProvider = themeProvider
        self.copier = copier
        self.scheduler = scheduler
        self.mode = mode
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configure()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    deinit { scheduler.invalidate() }

    private func configure() {
        layer.cornerRadius = 20
        layer.borderWidth = 1
        layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        if case .expanded = mode { layer.cornerRadius = 12 }
        clipsToBounds = true
        let theme = themeProvider.theme
        backgroundColor = UIColor { $0.userInterfaceStyle == .dark ? Theme.grey900 : theme.alwaysWhite }
        if let button = trailingHeaderButton { headerStackView.addArrangedSubview(button) }
        addSubview(headerStackView)
        addSubview(scrollView)
        scrollView.addSubview(gutterTextView)
        scrollView.addSubview(codeScrollView)
        codeScrollView.addSubview(codeTextView)
        configureLayout()
        codeTextView.isSelectable = content.isTextSelectable
        codeTextView.textContainerInset = UIEdgeInsets(top: 12, left: 0, bottom: 12, right: 16)
        codeTextView.attributedText = styledCodeText()
        gutterTextView.textContainerInset = UIEdgeInsets(top: 12, left: 12, bottom: 12, right: 20)
        gutterTextView.attributedText = gutterText()
        languageLabel.text = TranslationManager.messenger.translation(forKey: "code_block_label").localizedLowercase
        configureAccessibility()
    }

    private func configureLayout() {
        let height = scrollView.heightAnchor.constraint(equalTo: codeTextView.heightAnchor)
        height.priority = UILayoutPriority(rawValue: 749)
        var constraints = [
            headerStackView.leadingAnchor.constraint(equalTo: leadingAnchor),
            headerStackView.trailingAnchor.constraint(equalTo: trailingAnchor),
            headerStackView.topAnchor.constraint(equalTo: topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: trailingAnchor),
            scrollView.topAnchor.constraint(equalTo: headerStackView.bottomAnchor),
            scrollView.bottomAnchor.constraint(equalTo: bottomAnchor),
            scrollView.contentLayoutGuide.widthAnchor.constraint(equalTo: scrollView.frameLayoutGuide.widthAnchor),
            gutterTextView.leadingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.leadingAnchor),
            gutterTextView.topAnchor.constraint(equalTo: scrollView.contentLayoutGuide.topAnchor),
            gutterTextView.bottomAnchor.constraint(lessThanOrEqualTo: scrollView.contentLayoutGuide.bottomAnchor),
            codeScrollView.leadingAnchor.constraint(equalTo: gutterTextView.trailingAnchor),
            codeScrollView.trailingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.trailingAnchor),
            codeScrollView.topAnchor.constraint(equalTo: scrollView.contentLayoutGuide.topAnchor),
            codeScrollView.bottomAnchor.constraint(equalTo: scrollView.contentLayoutGuide.bottomAnchor),
            codeScrollView.frameLayoutGuide.heightAnchor.constraint(equalTo: codeTextView.heightAnchor),
            codeTextView.leadingAnchor.constraint(equalTo: codeScrollView.contentLayoutGuide.leadingAnchor),
            codeTextView.trailingAnchor.constraint(equalTo: codeScrollView.contentLayoutGuide.trailingAnchor),
            codeTextView.topAnchor.constraint(equalTo: codeScrollView.contentLayoutGuide.topAnchor),
            codeTextView.bottomAnchor.constraint(equalTo: codeScrollView.contentLayoutGuide.bottomAnchor),
        ]
        if case .inline = mode {
            constraints.append(scrollView.heightAnchor.constraint(lessThanOrEqualToConstant: 416))
            constraints.append(height)
        }
        if let maxWidth = maxWidth {
            let width = widthAnchor.constraint(equalToConstant: maxWidth)
            width.priority = UILayoutPriority(rawValue: 999)
            constraints.append(width)
        }
        NSLayoutConstraint.activate(constraints)
    }

    override var intrinsicContentSize: CGSize {
        let header = headerStackView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize).width
        let gutter = ceil(gutterTextView.sizeThatFits(CGSize(width: CGFloat.greatestFiniteMagnitude, height: CGFloat.greatestFiniteMagnitude)).width)
        let code = ceil(codeTextView.sizeThatFits(CGSize(width: CGFloat.greatestFiniteMagnitude, height: CGFloat.greatestFiniteMagnitude)).width)
        let sum = gutter + code
        return CGSize(width: sum <= header ? header : sum, height: UIView.noIntrinsicMetric)
    }

    private func makeTrailingHeaderButton() -> HeaderIconButton? {
        let button: HeaderIconButton
        switch mode {
        case .expanded:
            button = HeaderIconButton(image: ICMAssets.closeIcon(), themeProvider: themeProvider)
            button.accessibilityLabel = TranslationManager.messenger.translation(forKey: "close")
        case .inline(let action):
            guard action != nil else { return nil }
            button = HeaderIconButton(image: ICMAssets.expandIcon(), themeProvider: themeProvider)
            button.accessibilityLabel = "Expand code"
        }
        button.accessibilityTraits.insert(.button)
        button.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        button.addTarget(self, action: #selector(trailingButtonTapped), for: .touchUpInside)
        return button
    }

    @objc private func trailingButtonTapped() {
        switch mode {
        case .expanded(let action): action()
        case .inline(let action): action?()
        }
    }

    private var trimmedCodeText: String {
        var text = (content.highlightedAttributedString ?? content.attributedString!).string
        while let last = text.last, last.isWhitespace { text.removeLast() }
        return text
    }

    @objc private func copyTapped() {
        copier.copyCode(trimmedCodeText)
        ICMFeedbackGenerator.feedback(withType: 2)
        UIAccessibility.post(notification: .announcement, argument: "Code copied")
        copyState = .copied
        scheduler.invalidate()
        scheduler.schedule(timeInterval: 1.5, repeats: false) { [weak self] in self?.copyState = .idle }
    }

    private func textAttributes() -> [NSAttributedString.Key: Any] {
        let font = UIFontMetrics(forTextStyle: .body).scaledFont(for: .monospacedSystemFont(ofSize: 16, weight: .regular))
        let style = NSMutableParagraphStyle()
        let height = UIFontMetrics(forTextStyle: .body).scaledValue(for: 20)
        style.minimumLineHeight = height
        style.maximumLineHeight = height
        style.alignment = .left
        style.baseWritingDirection = .leftToRight
        return [.font: font, .paragraphStyle: style]
    }

    private func styledCodeText() -> NSAttributedString {
        let result = NSMutableAttributedString(attributedString: content.highlightedAttributedString ?? content.attributedString!)
        let range = NSRange(location: 0, length: result.length)
        result.addAttributes(textAttributes(), range: range)
        if content.highlightedAttributedString != nil {
            result.enumerateAttribute(.foregroundColor, in: range, options: []) { value, range, _ in
                if value == nil { result.addAttribute(.foregroundColor, value: self.themeProvider.theme.textDefault, range: range) }
            }
        } else { result.addAttribute(.foregroundColor, value: themeProvider.theme.textDefault, range: range) }
        return result
    }

    private func gutterText() -> NSAttributedString {
        let count = trimmedCodeText.split(maxSplits: Int.max, omittingEmptySubsequences: false, whereSeparator: { $0.isNewline }).count
        let width = String(count).count
        let text = (1...count).map { String(repeating: " ", count: width - String($0).count) + String($0) }.joined(separator: "\n")
        var attributes = textAttributes()
        attributes[.foregroundColor] = themeProvider.theme.textMuted
        return NSAttributedString(string: text, attributes: attributes)
    }

    private func configureAccessibility() {
        isAccessibilityElement = false
        scrollView.isAccessibilityElement = true
        scrollView.accessibilityTraits = .staticText
        scrollView.accessibilityLabel = codeTextView.attributedText!.string
        scrollView.accessibilityCustomActions = codeTextView.accessibilityCustomActions
        accessibilityElements = [languageLabel, copyButton, trailingHeaderButton, scrollView].compactMap { $0 }
    }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        if previousTraitCollection == nil || previousTraitCollection!.userInterfaceStyle != traitCollection.userInterfaceStyle {
            layer.borderColor = themeProvider.theme.neutralBorder.cgColor
        }
    }

    @objc(_TtCC18IntercomSDKPrivate13CodeBlockViewP33_EBF7782925B8A3F2749C9F27C3854CA117UnwrappedTextView)
    private class UnwrappedTextView: HyperLinkTextView {
        override init(frame: CGRect, textContainer: NSTextContainer?) {
            super.init(frame: frame, textContainer: textContainer)
            self.textContainer.widthTracksTextView = false
            self.textContainer.heightTracksTextView = false
            self.textContainer.size = CGSize(width: CGFloat.greatestFiniteMagnitude, height: CGFloat.greatestFiniteMagnitude)
            self.textContainer.lineFragmentPadding = 0
            semanticContentAttribute = .forceLeftToRight
        }
        required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
        override var attributedText: NSAttributedString! {
            get { super.attributedText }
            set { super.attributedText = newValue; invalidateIntrinsicContentSize() }
        }
        override var intrinsicContentSize: CGSize {
            let size = sizeThatFits(CGSize(width: CGFloat.greatestFiniteMagnitude, height: CGFloat.greatestFiniteMagnitude))
            return CGSize(width: ceil(size.width), height: ceil(size.height))
        }
    }
}
