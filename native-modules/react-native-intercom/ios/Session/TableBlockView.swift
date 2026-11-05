import UIKit

@objc(_TtC18IntercomSDKPrivate14TableBlockView)
class TableBlockView: UIView, ConversationCellContentView, UIScrollViewDelegate {
    let content: TableBlock
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private let maxHeight: CGFloat
    private let themeProvider: ThemeProvider
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var gridScrollView: UIScrollView = {
        let scroll = UIScrollView()
        scroll.showsHorizontalScrollIndicator = true
        scroll.showsVerticalScrollIndicator = false
        scroll.alwaysBounceHorizontal = false
        scroll.alwaysBounceVertical = false
        scroll.translatesAutoresizingMaskIntoConstraints = false
        return scroll
    }()
    private lazy var bodyRowsStackView = makeRowsStackView()
    private lazy var headerRowsStackView = makeRowsStackView()
    private lazy var headerScrollView: UIScrollView = {
        let scroll = UIScrollView()
        scroll.isUserInteractionEnabled = false
        scroll.showsHorizontalScrollIndicator = false
        scroll.showsVerticalScrollIndicator = false
        scroll.translatesAutoresizingMaskIntoConstraints = false
        return scroll
    }()
    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?, maxHeight: CGFloat, themeProvider: ThemeProvider) {
        guard let table = content as? TableBlock else { return nil }
        self.content = table
        self.delegate = delegate
        self.maxHeight = maxHeight
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(containerStackView)
        pinEdges(containerStackView)
        let rows = buildRowViews()
        configureCard(rowViews: rows, maxHeight: self.maxHeight)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func makeRowsStackView() -> UIStackView {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 0
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }
    private func configureCard(rowViews: [UIView], maxHeight: CGFloat) {
        guard let first = rowViews.first else {
            containerStackView.addArrangedSubview(gridScrollView)
            return
        }
        headerRowsStackView.addArrangedSubview(first)
        headerScrollView.addSubview(headerRowsStackView)
        NSLayoutConstraint.activate([
            headerRowsStackView.topAnchor.constraint(equalTo: headerScrollView.contentLayoutGuide.topAnchor),
            headerRowsStackView.leadingAnchor.constraint(equalTo: headerScrollView.contentLayoutGuide.leadingAnchor),
            headerRowsStackView.trailingAnchor.constraint(equalTo: headerScrollView.contentLayoutGuide.trailingAnchor),
            headerRowsStackView.bottomAnchor.constraint(equalTo: headerScrollView.contentLayoutGuide.bottomAnchor),
            headerScrollView.heightAnchor.constraint(equalTo: headerRowsStackView.heightAnchor)
        ])
        containerStackView.addArrangedSubview(headerScrollView)
        for row in rowViews.dropFirst() { bodyRowsStackView.addArrangedSubview(row) }
        gridScrollView.showsVerticalScrollIndicator = true
        gridScrollView.delegate = self
        gridScrollView.addSubview(bodyRowsStackView)
        let equality = gridScrollView.heightAnchor.constraint(equalTo: bodyRowsStackView.heightAnchor)
        equality.priority = UILayoutPriority(rawValue: 999)
        NSLayoutConstraint.activate([
            bodyRowsStackView.topAnchor.constraint(equalTo: gridScrollView.contentLayoutGuide.topAnchor),
            bodyRowsStackView.leadingAnchor.constraint(equalTo: gridScrollView.contentLayoutGuide.leadingAnchor),
            bodyRowsStackView.trailingAnchor.constraint(equalTo: gridScrollView.contentLayoutGuide.trailingAnchor),
            bodyRowsStackView.bottomAnchor.constraint(equalTo: gridScrollView.contentLayoutGuide.bottomAnchor),
            equality,
            heightAnchor.constraint(lessThanOrEqualToConstant: maxHeight)
        ])
        containerStackView.addArrangedSubview(gridScrollView)
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let header = headerScrollView
        let offset = scrollView.contentOffset
        header.contentOffset = CGPoint(x: offset.x, y: 0)
    }
    private func buildRowViews() -> [UIView] {
        guard let columnCount = content.rows.map({ $0.cells.count }).max(), columnCount > 0 else { return [] }
        let rows = content.rows
        var widths: [[CGFloat]] = []
        var stacks: [[UIStackView?]] = []
        for (rowIndex, row) in rows.enumerated() {
            var rowWidths: [CGFloat] = []
            var rowStacks: [UIStackView?] = []
            for column in 0..<columnCount {
                if column < row.cells.count {
                    let stack = makeContentStackView(for: row.cells[column], isHeader: rowIndex == 0)
                    let size = stack.systemLayoutSizeFitting(CGSize(width: 10000, height: UIView.layoutFittingCompressedSize.height), withHorizontalFittingPriority: .fittingSizeLevel, verticalFittingPriority: .fittingSizeLevel)
                    rowWidths.append(size.width)
                    rowStacks.append(stack)
                } else {
                    rowWidths.append(0)
                    rowStacks.append(nil)
                }
            }
            widths.append(rowWidths)
            stacks.append(rowStacks)
        }
        let columnWidths = TableColumnWidthCalculator.columnWidths(contentWidths: widths, minColumnWidth: 48, maxColumnWidth: 400, horizontalPadding: 0)
        let provider = themeProvider
        return stacks.enumerated().map { rowIndex, rowStacks in
            let height = rowHeight(for: rowStacks, columnWidths: columnWidths)
            let wrappers = rowStacks.map { stack -> UIView in
                let view = UIView()
                view.backgroundColor = provider.theme.base
                if let stack {
                    view.addSubview(stack)
                    let bottom = stack.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -8)
                    bottom.priority = .defaultLow
                    NSLayoutConstraint.activate([
                        stack.topAnchor.constraint(equalTo: view.topAnchor, constant: 8),
                        stack.leadingAnchor.constraint(equalTo: view.leadingAnchor),
                        stack.trailingAnchor.constraint(equalTo: view.trailingAnchor),
                        stack.bottomAnchor.constraint(lessThanOrEqualTo: view.bottomAnchor, constant: -8),
                        bottom
                    ])
                }
                return view
            }
            return makeRowView(wrappers, columnWidths: columnWidths, rowHeight: height, showsHairline: rowIndex != stacks.count - 1)
        }
    }
    private func rowHeight(for stacks: [UIStackView?], columnWidths: [CGFloat]) -> CGFloat {
        var maximumHeight: CGFloat = 0
        for (index, stack) in stacks.enumerated() {
            guard let stack, index < columnWidths.count else { continue }
            let size = stack.systemLayoutSizeFitting(CGSize(width: columnWidths[index], height: UIView.layoutFittingCompressedSize.height), withHorizontalFittingPriority: .required, verticalFittingPriority: .fittingSizeLevel)
            if maximumHeight <= size.height { maximumHeight = size.height }
        }
        return maximumHeight + 16
    }
    private func makeRowView(_ views: [UIView], columnWidths: [CGFloat], rowHeight: CGFloat, showsHairline: Bool) -> UIView {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.spacing = 16
        stack.distribution = .fill
        stack.alignment = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false
        for (index, view) in views.enumerated() {
            stack.addArrangedSubview(view)
            if index < columnWidths.count { view.widthAnchor.constraint(equalToConstant: columnWidths[index]).isActive = true }
        }
        let row = UIView()
        row.addSubview(stack)
        NSLayoutConstraint.activate([
            row.heightAnchor.constraint(equalToConstant: rowHeight),
            stack.topAnchor.constraint(equalTo: row.topAnchor),
            stack.leadingAnchor.constraint(equalTo: row.leadingAnchor),
            stack.trailingAnchor.constraint(equalTo: row.trailingAnchor),
            stack.bottomAnchor.constraint(equalTo: row.bottomAnchor)
        ])
        if showsHairline {
            let line = UIView()
            line.backgroundColor = themeProvider.theme.neutralBorder
            line.translatesAutoresizingMaskIntoConstraints = false
            row.addSubview(line)
            NSLayoutConstraint.activate([
                line.leadingAnchor.constraint(equalTo: row.leadingAnchor),
                line.trailingAnchor.constraint(equalTo: row.trailingAnchor),
                line.bottomAnchor.constraint(equalTo: row.bottomAnchor),
                line.heightAnchor.constraint(equalToConstant: 1)
            ])
        }
        return row
    }
    private func makeContentStackView(for cell: TableCell, isHeader: Bool) -> UIStackView {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 4
        stack.translatesAutoresizingMaskIntoConstraints = false
        let blocks = cell.content
        var views: [UIView] = []
        for block in blocks {
            var capturedDelegate = delegate
            var part: ICMConversationPart? = ICMConversationPart()
            part!.blocks = [block]
            let result = BlockViewFactory.getViewsForBlocks(in: part!, delegate: capturedDelegate, citationDelegate: nil, maxWidth: nil, imageDisplayContext: .context0)
            capturedDelegate = nil
            part = nil
            if let first = result.first { views.append(first) }
        }
        let theme = themeProvider.theme
        let color = isHeader ? theme.textMuted : theme.textDefault
        for view in views {
            if let text = view as? TextBlockView {
                let metrics = UIFontMetrics(forTextStyle: .footnote)
                text.textView.font = metrics.scaledFont(for: .systemFont(ofSize: 13))
                text.textView.textColor = color
            }
        }
        views.forEach { stack.addArrangedSubview($0) }
        return stack
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
