import UIKit

@objc(_TtC18IntercomSDKPrivate18HomescreenCardView)
class HomescreenCardView: UIView {
    private let themeProvider: any ThemeProvider
    private var titleText: String?
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = .vertical
        stack.alignment = .leading
        stack.spacing = 0
        stack.distribution = .fill
        return stack
    }()
    init(title: String?, subviews: [UIView], themeProvider: any ThemeProvider) {
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        titleText = title
        setupMainStackView(with: subviews)
        setupTitleLabel()
        setupAppearance()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupMainStackView(with views: [UIView]) {
        addSubview(mainStackView)
        NSLayoutConstraint.activate([
            mainStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -1),
            mainStackView.topAnchor.constraint(equalTo: topAnchor, constant: 8)
        ])
        let leading = mainStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 1)
        leading.priority = .init(999)
        leading.isActive = true
        let bottom = mainStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -8)
        bottom.priority = .init(999)
        bottom.isActive = true
        for view in views { mainStackView.addArrangedSubview(view) }
        for view in views {
            view.translatesAutoresizingMaskIntoConstraints = false
            view.widthAnchor.constraint(equalTo: mainStackView.widthAnchor).isActive = true
        }
    }
    private func setupTitleLabel() {
        guard let title = titleText else { return }
        let wrapper = UIView()
        wrapper.translatesAutoresizingMaskIntoConstraints = false
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = title
        label.font = .preferredFont(forTextStyle: .headline)
        label.numberOfLines = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? 0 : 1
        label.lineBreakMode = ICMUtils.contentSizeCategoryIsAccessibilityCategory() ? .byWordWrapping : .byTruncatingTail
        wrapper.addSubview(label)
        NSLayoutConstraint.activate([
            label.leadingAnchor.constraint(equalTo: wrapper.leadingAnchor, constant: 15),
            label.trailingAnchor.constraint(equalTo: wrapper.trailingAnchor, constant: -15),
            label.topAnchor.constraint(equalTo: wrapper.topAnchor, constant: 10),
            label.bottomAnchor.constraint(equalTo: wrapper.bottomAnchor)
        ])
        wrapper.accessibilityTraits = .header
        mainStackView.insertArrangedSubview(wrapper, at: 0)
    }
    private func setupAppearance() {
        backgroundColor = themeProvider.theme.base
        layer.borderWidth = 1
        layer.borderColor = themeProvider.theme.neutralBorder.cgColor
    }
    override func layoutSubviews() { super.layoutSubviews(); applyElevationOneShadow(cornerRadius: 12) }
    override func traitCollectionDidChange(_ previous: UITraitCollection?) {
        super.traitCollectionDidChange(previous)
        let old = previous?.userInterfaceStyle
        let current = traitCollection.userInterfaceStyle
        if old != nil && old == current { return }
        layer.borderColor = themeProvider.theme.neutralBorder.cgColor
        applyElevationOneShadow(cornerRadius: 12)
    }
}
