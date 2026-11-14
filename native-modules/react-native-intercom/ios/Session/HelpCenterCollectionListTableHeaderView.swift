import UIKit

@objc(_TtC18IntercomSDKPrivate39HelpCenterCollectionListTableHeaderView)
class HelpCenterCollectionListTableHeaderView: HelpCenterTableHeaderView {
    private lazy var label: UILabel = {
        let label = UILabel()
        label.numberOfLines = 0
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private let translationManager = TranslationManager.helpCenter
    private let themeProvider: any ThemeProvider
    private lazy var dividerLine: UIView = {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.neutralBorder
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    init(collectionCount: Int, frame: CGRect, collectionName: String?, themeProvider: any ThemeProvider) {
        self.themeProvider = themeProvider
        super.init(frame: frame)
        addSubviews()
        let title: String
        if let collectionName { title = collectionName }
        else if collectionCount == 1 { title = translationManager.translation(forKey: "single_collection") }
        else { title = translationManager.translation(forKey: "multiple_collections").replacingOccurrences(of: "{total_collection}", with: String(collectionCount)) }
        label.text = title
        label.font = .preferredFont(forTextStyle: .headline)
        label.adjustsFontForContentSizeCategory = true
    }
    // ObjC dispatch inherits the base coder trap; the independent Swift entry was WMO-deleted.
    @nonobjc required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func addSubviews() {
        layer.borderWidth = 0
        addSubview(label)
        addSubview(dividerLine)
        NSLayoutConstraint.activate([
            label.trailingAnchor.constraint(equalTo: layoutMarginsGuide.trailingAnchor), label.topAnchor.constraint(equalTo: topAnchor, constant: 16),
            label.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -16), dividerLine.heightAnchor.constraint(equalToConstant: 1),
            dividerLine.trailingAnchor.constraint(equalTo: trailingAnchor), dividerLine.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        let leading = label.leadingAnchor.constraint(equalTo: layoutMarginsGuide.leadingAnchor)
        leading.priority = .init(999)
        leading.isActive = true
        let divider = dividerLine.leadingAnchor.constraint(equalTo: leadingAnchor)
        divider.priority = .init(999)
        divider.isActive = true
    }
}
