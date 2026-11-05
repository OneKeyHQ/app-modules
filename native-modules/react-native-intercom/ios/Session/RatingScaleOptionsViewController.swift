// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
import UIKit

protocol RatingScaleInputView {
    var ratingScaleOptionsViewController: RatingScaleOptionsViewController? { get }
}

struct RowDataGenerator<Item> {
    let itemSize: CGFloat
    let availableWidth: CGFloat
    let itemSpacing: CGFloat
    let items: [Item]

    private var maxItemsPerLine: Int {
        let stride = itemSize + itemSpacing
        let count = Int(availableWidth / stride)
        let incremented = count + 1
        let result = CGFloat(incremented) * stride - itemSpacing <= availableWidth ? incremented : count
        return max(result, 1)
    }

    func rowData() -> [[Item]] {
        if items.isEmpty { return [] }
        let rows = Int(ceil(CGFloat(items.count) / CGFloat(maxItemsPerLine)))
        let base = items.count / rows
        let remainder = items.count % rows
        var result: [[Item]] = []
        var itemIndex = 0
        for rowIndex in 0..<rows {
            var row: [Item] = []
            for _ in 0..<base {
                row.append(items[itemIndex])
                itemIndex += 1
            }
            if rowIndex < remainder {
                row.append(items[itemIndex])
                itemIndex += 1
            }
            result.append(row)
        }
        return result
    }
}

@objc(_TtC18IntercomSDKPrivate32RatingScaleOptionsViewController)
class RatingScaleOptionsViewController: UIViewController {
    private let themeProvider: any ThemeProvider = DefaultThemeProvider.shared
    private let options: [UIButton]
    private let stackView = UIStackView()
    private let lowerLabel: String?
    private let upperLabel: String?
    private let itemSize: CGFloat = 44
    private let itemSpacing: CGFloat = 8
    private let labelTopPadding: CGFloat = 12

    init(with options: [UIButton], lowerLabel: String?, upperLabel: String?) {
        self.options = options
        self.lowerLabel = lowerLabel
        self.upperLabel = upperLabel
        super.init(nibName: nil, bundle: nil)
        setupStackView()
        setupLabels()
        setupAccessibility()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }

    private func setupStackView() {
        view!.addSubview(stackView)
        stackView.axis = .vertical
        stackView.spacing = itemSpacing
        stackView.alignment = .center
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.centerXAnchor.constraint(equalTo: view!.centerXAnchor).isActive = true
        stackView.topAnchor.constraint(equalTo: view!.topAnchor).isActive = true
        if lowerLabel == nil && upperLabel == nil {
            stackView.bottomAnchor.constraint(equalTo: view!.bottomAnchor).isActive = true
        }
    }
    private func setupLabels() {
        if let lowerLabel {
            let label = UILabel()
            label.font = .preferredFont(forTextStyle: .caption2)
            label.textColor = themeProvider.theme.textMuted
            label.translatesAutoresizingMaskIntoConstraints = false
            label.text = lowerLabel
            view!.addSubview(label)
            label.topAnchor.constraint(equalTo: stackView.bottomAnchor, constant: labelTopPadding).isActive = true
            label.bottomAnchor.constraint(equalTo: view!.bottomAnchor).isActive = true
            label.leadingAnchor.constraint(equalTo: view!.leadingAnchor).isActive = true
        }
        if let upperLabel {
            let label = UILabel()
            label.font = .preferredFont(forTextStyle: .caption2)
            label.textColor = themeProvider.theme.textMuted
            label.translatesAutoresizingMaskIntoConstraints = false
            label.text = upperLabel
            view!.addSubview(label)
            label.topAnchor.constraint(equalTo: stackView.bottomAnchor, constant: labelTopPadding).isActive = true
            label.bottomAnchor.constraint(equalTo: view!.bottomAnchor).isActive = true
            label.trailingAnchor.constraint(equalTo: view!.trailingAnchor).isActive = true
        }
    }
    private func setupAccessibility() {
        if let lowerLabel, let upperLabel {
            isAccessibilityElement = true
            accessibilityLabel = lowerLabel + "; " + upperLabel + ";"
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.backButtonDisplayMode = .minimal
    }
    override func viewDidLayoutSubviews() {
        let items: [UIView] = options
        let width = view!.frame.width
        let rows = RowDataGenerator(itemSize: itemSize, availableWidth: width, itemSpacing: itemSpacing, items: items).rowData()
        if rows.count == stackView.subviews.count { return }
        for subview in stackView.subviews { subview.removeFromSuperview() }
        for items in rows {
            let row = UIStackView()
            row.spacing = itemSpacing
            row.translatesAutoresizingMaskIntoConstraints = false
            row.alignment = .center
            for item in items {
                let wrapper = UIView()
                row.addArrangedSubview(wrapper)
                wrapper.translatesAutoresizingMaskIntoConstraints = false
                wrapper.widthAnchor.constraint(equalToConstant: itemSize).isActive = true
                wrapper.heightAnchor.constraint(equalToConstant: itemSize).isActive = true
                wrapper.addSubview(item)
                item.translatesAutoresizingMaskIntoConstraints = false
                item.centerXAnchor.constraint(equalTo: wrapper.centerXAnchor).isActive = true
                item.centerYAnchor.constraint(equalTo: wrapper.centerYAnchor).isActive = true
            }
            stackView.addArrangedSubview(row)
        }
    }
}
