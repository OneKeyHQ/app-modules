import UIKit

@objc(_TtC18IntercomSDKPrivate9CodeBlock)
class CodeBlock: ITBTextBlock {
    @objc var highlightedHtml: String?
    @objc var highlightedAttributedString: NSAttributedString?

    override init!(dictionary: [AnyHashable: Any]!, appearance: any ITBBlockAppearance) {
        if let html = dictionary?["highlighted_html"] as? String, !html.isEmpty {
            highlightedHtml = html
        }
        super.init(dictionary: dictionary, appearance: appearance, font: appearance.codeFont, textColor: appearance.codeTextColor)
        updateWith(appearance: appearance)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override init!(dictionary: [AnyHashable: Any]!, appearance: (any ITBBlockAppearance)?, font: UIFont!, textColor: UIColor!) {
        fatalError("init(dictionary:appearance:font:textColor:) has not been implemented")
    }

    override func type() -> Int { 9 }

    override func update(_ appearance: any ITBBlockAppearance) {
        super.update(appearance, font: appearance.codeFont, textColor: appearance.codeTextColor)
        updateWith(appearance: appearance)
    }

    @objc(updateWithAppearance:)
    func updateWith(appearance: any ITBBlockAppearance) {
        backgroundColor = appearance.codeBackgroundColor
        layoutInsets = appearance.codeInsets
        textInsets = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        if let html = highlightedHtml, let concrete = appearance as? ICMBlockAppearance {
            highlightedAttributedString = CodeSyntaxHighlighter(appearance: concrete).attributedString(fromHighlightedHTML: html)
        }
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? CodeBlock else { return false }
        if self === other { return true }
        guard highlightedHtml == other.highlightedHtml else { return false }
        return super.isEqual(object)
    }

    override func copy(with zone: NSZone? = nil) -> Any {
        guard let result = super.copy(with: zone) as? CodeBlock else { return Optional<CodeBlock>.none as Any }
        result.highlightedHtml = highlightedHtml
        result.highlightedAttributedString = highlightedAttributedString
        return Optional.some(result) as Any
    }
}
