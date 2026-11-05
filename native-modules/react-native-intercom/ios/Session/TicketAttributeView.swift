// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate19TicketAttributeView)
class TicketAttributeView: UIView {
    let attribute: FormAttribute
    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 0
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .semibold)
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = attribute.name
        return label
    }()
    init?(attribute: FormAttribute) {
        self.attribute = attribute
        super.init(frame: .zero)
        guard attribute.value != nil else { return nil }
        backgroundColor = DefaultThemeProvider.shared.theme.base
        addSubviews()
        configureValueView()
        setupAccessibility()
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    var valueView: UIView { UIView() }
    var topPadding: CGFloat { 4 }
    func configureValueView() {}
    func setupAccessibility() {}
    private func addSubviews() {
        addSubview(titleLabel)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.topAnchor.constraint(equalTo: titleLabel.superview!.topAnchor).isActive = true
        pinHorizontal(titleLabel)
        addSubview(valueView)
        let value = valueView
        let title = titleLabel
        let padding = topPadding
        value.translatesAutoresizingMaskIntoConstraints = false
        value.topAnchor.constraint(equalTo: title.bottomAnchor, constant: padding).isActive = true
        let finalValue = valueView
        pinHorizontal(finalValue)
        finalValue.translatesAutoresizingMaskIntoConstraints = false
        finalValue.bottomAnchor.constraint(equalTo: finalValue.superview!.bottomAnchor).isActive = true
    }
    private func pinHorizontal(_ child: UIView) {
        child.translatesAutoresizingMaskIntoConstraints = false
        if child.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor).isActive = true
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor).isActive = true
        } else {
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor).isActive = true
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor).isActive = true
        }
    }
}
