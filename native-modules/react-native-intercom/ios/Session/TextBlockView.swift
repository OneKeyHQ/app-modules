import UIKit

@objc(_TtC18IntercomSDKPrivate13TextBlockView)
class TextBlockView: UIView, ConversationCellContentView, UITextViewDelegate, HyperLinkTextViewDelegate {
    let content: ITBTextBlock
    private let themeProvider: any ThemeProvider = DefaultThemeProvider.shared
    private let drawsInlineCodeChips: Bool
    private weak var citationDelegate: (any CitationTapDelegate)?
    private weak var blockDelegate: (any ConversationCellBlockDelegate)?
    lazy var textView: HyperLinkTextView = {
        let view = drawsInlineCodeChips ? HyperLinkTextView.withInlineCodeChips() : HyperLinkTextView()
        view.delegate = self
        view.textViewDelegate = self
        view.onLinkTap = { [weak self] url in
            if let delegate = self?.blockDelegate { delegate.open(url: url) }
            else { LinkOpener.shared.openLink(url) }
        }
        return view
    }()

    init?(content: ITBBlock, citationDelegate: (any CitationTapDelegate)?, blockDelegate: (any ConversationCellBlockDelegate)?) {
        guard let content = content as? ITBTextBlock else { return nil }
        self.content = content
        self.citationDelegate = citationDelegate
        self.blockDelegate = blockDelegate
        drawsInlineCodeChips = Self.containsInlineCode(content)
        super.init(frame: .zero)
        textView.isPartOfAIAnswerCard = content.isPartOfAIAnswerCard
        translatesAutoresizingMaskIntoConstraints = false
        configure()
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }

    private static func containsInlineCode(_ block: ITBTextBlock) -> Bool {
        guard let attributed = block.attributedString else { return false }
        var found = false
        attributed.enumerateAttribute(.backgroundColor, in: NSRange(location: 0, length: attributed.length), options: []) { value, _, stop in
            if value != nil { found = true; stop.pointee = true }
        }
        return found
    }

    func didTapNonLinkInTextView() {
        guard let citations = content.inlineCitations else { return }
        if !citations.isEmpty { citationDelegate?.didTapCitations(citations) }
    }

    private func configure() {
        addSubview(textView)
        NSLayoutConstraint.activate([
            textView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: content.layoutInsets.left),
            textView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -content.layoutInsets.right),
            textView.topAnchor.constraint(equalTo: topAnchor, constant: content.layoutInsets.top),
            textView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -content.layoutInsets.bottom),
        ])
        textView.isSelectable = content.isTextSelectable
        textView.layoutMargins = content.layoutInsets
        textView.textContainerInset = content.textInsets
        if let color = content.linkTextColor {
            textView.linkTextAttributes = [.foregroundColor: color, .underlineColor: color]
        }
        let updated = updateAttributes(in: content)
        let mutable = NSMutableAttributedString(attributedString: updated)
        let attributed = appendInlineCitations(to: mutable)
        textView.attributedText = attributed
        textView.backgroundColor = content.backgroundColor
        if content.textInsets.left == 0 && content.textInsets.right == 0 {
            textView.textContainer.lineFragmentPadding = 0
        }
        isAccessibilityElement = true
        accessibilityLabel = attributed.string
        accessibilityCustomActions = textView.accessibilityCustomActions
    }

    private func updateAttributes(in block: ITBTextBlock) -> NSAttributedString {
        let result = NSMutableAttributedString(string: block.attributedString!.string)
        let original = block.attributedString!
        let length = block.attributedString!.length
        original.enumerateAttributes(in: NSRange(location: 0, length: length), options: []) { attributes, range, _ in
            for (key, value) in attributes {
                if key == .paragraphStyle && (block.type() == 4 || block.type() == 3) {
                    let font = UIFontMetrics(forTextStyle: .body).scaledFont(for: block.font!)
                    let style = NSMutableParagraphStyle()
                    style.headIndent = font.pointSize * 1.5
                    style.firstLineHeadIndent = 0
                    style.tabStops = [NSTextTab(textAlignment: .natural, location: font.pointSize * 1.5, options: [:])]
                    result.addAttribute(.paragraphStyle, value: style, range: range)
                } else if key == .font, let font = value as? UIFont {
                    let scaled = UIFontMetrics(forTextStyle: .body).scaledFont(for: font)
                    result.addAttribute(.font, value: scaled, range: range)
                } else if key == .backgroundColor && self.drawsInlineCodeChips {
                    result.addAttribute(.inlineCodeBackground, value: value, range: range)
                } else {
                    result.addAttribute(key, value: value, range: range)
                }
            }
        }
        if drawsInlineCodeChips { reserveChipLineHeight(in: result) }
        return result
    }

    private func reserveChipLineHeight(in string: NSMutableAttributedString) {
        let length = string.length
        var chipFont: UIFont?
        string.enumerateAttribute(.font, in: NSRange(location: 0, length: length), options: []) { value, range, _ in
            if let font = value as? UIFont,
               string.attribute(.inlineCodeBackground, at: range.location, effectiveRange: nil) != nil {
                chipFont = font
            }
        }
        guard let font = chipFont else { return }
        let height = ((font.ascender - font.descender) + 5) + 1
        string.enumerateAttribute(.paragraphStyle, in: NSRange(location: 0, length: length), options: []) { value, range, _ in
            guard let style = value as? NSParagraphStyle,
                  let copy = style.mutableCopy() as? NSMutableParagraphStyle else { return }
            let previous = copy.minimumLineHeight
            copy.minimumLineHeight = previous <= height ? height : previous
            string.addAttribute(.paragraphStyle, value: copy, range: range)
        }
    }

    private func appendInlineCitations(to attributed: NSAttributedString) -> NSAttributedString {
        guard let citations = content.inlineCitations else { return attributed }
        let result = NSMutableAttributedString(attributedString: attributed)
        for citation in citations {
            let font = UIFont.preferredFont(forTextStyle: .body)
            let size = font.pointSize
            let label = buildCitationLabel(for: citation, with: size)
            let container = buildCitationView(with: size)
            label.center = CGPoint(x: container.bounds.midX, y: container.bounds.midY)
            container.addSubview(label)
            let spacer = NSTextAttachment()
            spacer.bounds = CGRect(x: 0, y: 0, width: 3, height: 0)
            result.append(NSAttributedString(attachment: spacer))
            let image = container.asTextAttachmentImage()
            let attachment = NSTextAttachment(image: image)
            let value = NSMutableAttributedString(attachment: attachment)
            value.addAttribute(.baselineOffset, value: size * -0.25, range: NSRange(location: 0, length: value.length))
            result.append(value)
        }
        return result
    }

    private func buildCitationLabel(for citation: ITBInlineCitation, with size: CGFloat) -> UILabel {
        let label = UILabel()
        label.text = citation.sourceIndex
        label.textColor = themeProvider.theme.textDefault
        let fontSize = size * 0.75
        label.font = UIFont.systemFont(ofSize: fontSize, weight: .regular)
        label.textAlignment = .center
        var bounds = label.bounds
        bounds.size = CGSize(width: fontSize, height: fontSize)
        label.bounds = bounds
        return label
    }

    private func buildCitationView(with size: CGFloat) -> UIView {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.neutralContainerEmphasis
        var bounds = view.bounds
        bounds.size = CGSize(width: size, height: size)
        view.bounds = bounds
        view.layer.cornerRadius = view.bounds.width * 0.5
        view.layer.masksToBounds = true
        return view
    }
}
