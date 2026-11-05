// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate30TicketStringAttributeValueView)
class TicketStringAttributeValueView: TicketAttributeView {
    private lazy var formattedValue: String? = formatValue()
    private lazy var valueLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 0
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    @nonobjc override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    @nonobjc required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init?(attribute: FormAttribute) { super.init(attribute: attribute) }
    override var valueView: UIView { valueLabel }
    override func configureValueView() { valueLabel.text = formattedValue }
    private func formatValue() -> String? {
        switch attribute.formInputType {
        case .text, .email, .phone, .options, .website, .multiline: return attribute.value as? String
        case .integer, .float:
            guard let value = attribute.value else { return nil }
            return String(describing: value)
        case .boolean:
            guard let value = attribute.value as? Bool else { return nil }
            return TranslationManager.messenger.translation(forKey: value ? "attribute_collector_positive" : "attribute_collector_negative")
        case .date, .dateTime:
            guard let value = attribute.value as? Double else { return nil }
            let date = Date(timeIntervalSince1970: value)
            let formatter = DateFormatter()
            formatter.timeStyle = .medium
            formatter.dateStyle = .medium
            formatter.dateFormat = "d MMM yyyy, HH:mm"
            return formatter.string(from: date)
        case .unknown, .files: return nil
        }
    }
    override func setupAccessibility() {
        guard let value = formattedValue else { return }
        isAccessibilityElement = true
        accessibilityLabel = attribute.name + ";" + value
    }
}
