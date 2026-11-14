import UIKit

protocol HyperLinkTextViewDelegate: AnyObject {
    func didTapNonLinkInTextView()
}

@objc(_TtC18IntercomSDKPrivate17HyperLinkTextView)
class HyperLinkTextView: UITextView {
    var isPartOfAIAnswerCard = false
    weak var textViewDelegate: HyperLinkTextViewDelegate?
    var onLinkTap: ((URL) -> Void)?
    private lazy var linkTapGesture: UITapGestureRecognizer = {
        let gesture = UITapGestureRecognizer(target: self, action: #selector(handleLinkTapGestureRecognizer(_:)))
        gesture.cancelsTouchesInView = false
        gesture.delaysTouchesBegan = false
        gesture.delaysTouchesEnded = false
        return gesture
    }()

    override init(frame: CGRect, textContainer: NSTextContainer?) {
        super.init(frame: frame, textContainer: textContainer)
        setup()
    }
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setup()
    }
    private func setup() {
        adjustsFontForContentSizeCategory = true
        backgroundColor = .clear
        scrollsToTop = false
        isEditable = false
        isScrollEnabled = false
        translatesAutoresizingMaskIntoConstraints = false
        addGestureRecognizer(linkTapGesture)
    }
    static func withInlineCodeChips() -> HyperLinkTextView {
        let storage = NSTextStorage()
        let manager = InlineCodeChipLayoutManager()
        storage.addLayoutManager(manager)
        let container = NSTextContainer()
        container.widthTracksTextView = true
        manager.addTextContainer(container)
        return HyperLinkTextView(frame: .zero, textContainer: container)
    }
    override var attributedText: NSAttributedString! {
        get { super.attributedText }
        set {
            super.attributedText = newValue
            updateLinkAccessibility()
        }
    }
    private func updateLinkAccessibility() {
        var actions: [UIAccessibilityCustomAction] = []
        let text = attributedText!
        let length = attributedText!.string.utf16.count
        text.enumerateAttribute(.link, in: NSRange(location: 0, length: length), options: []) { value, range, _ in
            guard let string = value as? String, let url = URL(string: string) else { return }
            let name = "Go to link titled: " + self.attributedText!.attributedSubstring(from: range).string
            actions.append(UIAccessibilityCustomAction(name: name, actionHandler: { [weak self] _ in
                guard let self else { return false }
                if let onLinkTap = self.onLinkTap { onLinkTap(url) }
                else { LinkOpener.shared.openLink(url) }
                return true
            }))
        }
        accessibilityCustomActions = actions
    }
    @objc private func handleLinkTapGestureRecognizer(_ recognizer: UITapGestureRecognizer) {
        guard let position = closestPosition(to: recognizer.location(in: self)) else { return }
        guard let range = tokenizer.rangeEnclosingPosition(position, with: .word, inDirection: UITextDirection(rawValue: 1)) else {
            textViewDelegate?.didTapNonLinkInTextView()
            return
        }
        let start = offset(from: beginningOfDocument, to: range.start)
        let length = offset(from: range.start, to: range.end)
        let substring = attributedText!.attributedSubstring(from: NSRange(location: start, length: length))
        guard let value = substring.attribute(.link, at: 0, effectiveRange: nil) else {
            textViewDelegate?.didTapNonLinkInTextView()
            return
        }
        let url: URL?
        if let direct = value as? URL { url = direct }
        else if let string = value as? String { url = URL(string: string) }
        else { url = nil }
        guard let url else { return }
        if let onLinkTap { onLinkTap(url) }
        else { LinkOpener.shared.openLink(url) }
    }
}
