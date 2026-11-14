import UIKit

@objc(_TtC18IntercomSDKPrivate27TableExpandedViewController)
class TableExpandedViewController: UIViewController {
    private let table: TableBlock
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private let themeProvider: ThemeProvider
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFontMetrics(forTextStyle: .subheadline).scaledFont(for: .systemFont(ofSize: 14, weight: .semibold))
        label.adjustsFontForContentSizeCategory = true
        label.textColor = themeProvider.theme.textDefault
        label.numberOfLines = 1
        label.lineBreakMode = .byTruncatingTail
        label.accessibilityTraits = .header
        label.setContentHuggingPriority(.defaultLow, for: .horizontal)
        return label
    }()
    private lazy var closeButton: HeaderIconButton = {
        let button = HeaderIconButton(symbolName: "xmark", themeProvider: themeProvider)
        button.accessibilityLabel = TranslationManager.messenger.translation(forKey: "close")
        button.accessibilityIdentifier = "table expanded close button"
        button.accessibilityTraits.formUnion(.button)
        button.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        button.addTarget(self, action: #selector(closeTapped), for: .touchUpInside)
        return button
    }()
    private lazy var headerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.isLayoutMarginsRelativeArrangement = true
        stack.layoutMargins = UIEdgeInsets(top: 2, left: 16, bottom: 2, right: 8)
        [titleLabel, closeButton].forEach { stack.addArrangedSubview($0) }
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var scrollView: UIScrollView = {
        let view = UIScrollView()
        view.showsVerticalScrollIndicator = true
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    init(for table: TableBlock, delegate: (any ConversationCellBlockDelegate)?, themeProvider: ThemeProvider) {
        self.table = table
        self.delegate = delegate
        self.themeProvider = themeProvider
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        view!.backgroundColor = themeProvider.theme.base
        let title = titleLabel
        title.text = TableCardView.resolvedTitle(for: table, translationManager: .messenger)
        setupViews()
    }
    private func setupViews() {
        view!.addSubview(headerStackView)
        NSLayoutConstraint.activate([
            headerStackView.topAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.topAnchor),
            headerStackView.leadingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.leadingAnchor),
            headerStackView.trailingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.trailingAnchor)
        ])
        setupTable()
    }
    private func setupTable() {
        let table = self.table
        var snapshot = delegate
        let child = TableBlockView(content: table, delegate: snapshot, maxHeight: .greatestFiniteMagnitude, themeProvider: DefaultThemeProvider.shared)!
        snapshot = nil
        view!.addSubview(scrollView)
        scrollView.addSubview(child)
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: headerStackView.bottomAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.bottomAnchor),
            child.topAnchor.constraint(equalTo: scrollView.contentLayoutGuide.topAnchor, constant: 16),
            child.bottomAnchor.constraint(equalTo: scrollView.contentLayoutGuide.bottomAnchor, constant: -16),
            child.leadingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.leadingAnchor, constant: 16),
            child.trailingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.trailingAnchor, constant: -16),
            child.widthAnchor.constraint(equalTo: scrollView.frameLayoutGuide.widthAnchor, constant: -32)
        ])
    }
    @objc private func closeTapped() { dismiss(animated: true, completion: nil) }
}
