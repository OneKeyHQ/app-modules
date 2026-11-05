import UIKit

@objc(_TtC18IntercomSDKPrivate13TableCardView)
class TableCardView: UIView {
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
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var expandButton: HeaderIconButton = {
        let button = HeaderIconButton(symbolName: "arrow.down.left.and.arrow.up.right", themeProvider: themeProvider)
        button.accessibilityLabel = "Expand table"
        button.accessibilityTraits.formUnion(.button)
        button.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        button.addTarget(self, action: #selector(expandTapped), for: .touchUpInside)
        return button
    }()
    private lazy var headerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.isLayoutMarginsRelativeArrangement = true
        stack.layoutMargins = UIEdgeInsets(top: 16, left: 16, bottom: 12, right: 12)
        [titleLabel, expandButton].forEach { stack.addArrangedSubview($0) }
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var gridContainerView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        [headerStackView, gridContainerView].forEach { stack.addArrangedSubview($0) }
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    init(for table: TableBlock, delegate: (any ConversationCellBlockDelegate)?, themeProvider: ThemeProvider) {
        self.table = table
        self.delegate = delegate
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        setup()
        configure(table)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setup() {
        translatesAutoresizingMaskIntoConstraints = false
        clipsToBounds = true
        backgroundColor = themeProvider.theme.base
        layer.cornerRadius = 20
        layer.borderWidth = 1
        layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        addSubview(containerStackView)
        pinEdges(containerStackView)
    }
    private func configure(_ table: TableBlock) {
        let title = titleLabel
        title.text = Self.resolvedTitle(for: table, translationManager: .messenger)
        var snapshot = delegate
        let view = TableBlockView(content: table, delegate: snapshot, maxHeight: 364, themeProvider: DefaultThemeProvider.shared)!
        snapshot = nil
        gridContainerView.addSubview(view)
        NSLayoutConstraint.activate([
            view.topAnchor.constraint(equalTo: gridContainerView.topAnchor),
            view.leadingAnchor.constraint(equalTo: gridContainerView.leadingAnchor, constant: 16),
            view.trailingAnchor.constraint(equalTo: gridContainerView.trailingAnchor, constant: -16),
            view.bottomAnchor.constraint(equalTo: gridContainerView.bottomAnchor, constant: -8)
        ])
    }
    static func resolvedTitle(for table: TableBlock, translationManager: TranslationManager) -> String {
        if let title = table.title, !title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty { return title }
        let key = "data_table"
        let title = translationManager.translation(forKey: key)
        return title == key ? "Data table" : title
    }
    @objc private func expandTapped() {
        let table = self.table
        var snapshot = delegate
        let controller = TableExpandedViewController(for: table, delegate: snapshot, themeProvider: DefaultThemeProvider.shared)
        snapshot = nil
        controller.modalPresentationStyle = .pageSheet
        let presenter = delegate?.getViewControllerForModalPresentation()
        presenter?.present(controller, animated: true, completion: nil)
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let previous = previousTraitCollection?.userInterfaceStyle ?? .unspecified
        let current = traitCollection.userInterfaceStyle
        if previousTraitCollection == nil || previous != current { layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor }
    }
    private func pinEdges(_ view: UIView) {
        let topParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.topAnchor.constraint(equalTo: topParent.topAnchor).isActive = true
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
        let bottomParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
}
