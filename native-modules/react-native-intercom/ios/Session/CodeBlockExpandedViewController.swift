import UIKit

@objc(_TtC18IntercomSDKPrivate31CodeBlockExpandedViewController)
class CodeBlockExpandedViewController: UIViewController, UIGestureRecognizerDelegate {
    private var codeView: CodeBlockView?
    private let content: CodeBlock
    private weak var blockDelegate: (any ConversationCellBlockDelegate)?
    private let themeProvider: any ThemeProvider

    init(content: CodeBlock, blockDelegate: (any ConversationCellBlockDelegate)?, themeProvider: any ThemeProvider) {
        self.content = content
        self.blockDelegate = blockDelegate
        self.themeProvider = themeProvider
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.black.withAlphaComponent(0.5)
        view.accessibilityViewIsModal = true
        setupCard()
        let gesture = UITapGestureRecognizer(target: self, action: #selector(scrimTapped))
        gesture.delegate = self
        view.addGestureRecognizer(gesture)
    }

    private func setupCard() {
        let card = CodeBlockView(content: content, blockDelegate: blockDelegate, maxWidth: nil, themeProvider: themeProvider, mode: .expanded({ [weak self] in self?.dismiss(animated: true, completion: nil) }))!
        codeView = card
        view.addSubview(card)
        let guide = view.safeAreaLayoutGuide
        NSLayoutConstraint.activate([
            card.leadingAnchor.constraint(equalTo: guide.leadingAnchor, constant: 16),
            card.trailingAnchor.constraint(equalTo: guide.trailingAnchor, constant: -16),
            card.topAnchor.constraint(equalTo: guide.topAnchor, constant: 16),
            card.bottomAnchor.constraint(equalTo: guide.bottomAnchor, constant: -16),
        ])
    }
    override func accessibilityPerformEscape() -> Bool { dismiss(animated: true, completion: nil); return true }
    @objc private func scrimTapped() { dismiss(animated: true, completion: nil) }
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive touch: UITouch) -> Bool {
        let point = touch.location(in: view)
        if let codeView = codeView { return !codeView.frame.contains(point) }
        return true
    }
}
