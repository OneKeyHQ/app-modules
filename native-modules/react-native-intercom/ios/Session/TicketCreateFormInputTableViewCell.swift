// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate34TicketCreateFormInputTableViewCell)
class TicketCreateFormInputTableViewCell: TicketCreateFormTableViewCell, TicketCreateFormCellRefreshable {
    var cellItem: (any TicketCreateFormInputCellItem)?
    var showErrorIcon = true
    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        let font = UIFont.preferredFont(forTextStyle: .subheadline)
        if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) { label.font = UIFont(descriptor: descriptor, size: 0) }
        else { label.font = font }
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    lazy var errorIcon: UIImageView = {
        let icon = UIImageView()
        icon.image = ICMAssets.infoIcon()
        icon.contentMode = .scaleAspectFit
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.widthAnchor.constraint(equalToConstant: 12).isActive = true
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.heightAnchor.constraint(equalToConstant: 12).isActive = true
        return icon
    }()
    lazy var validityMessageLabel = makeLabel(error: true)
    lazy var validityMessageView: UIView = {
        let view = UIView()
        if showErrorIcon {
            view.addSubview(errorIcon)
            errorIcon.translatesAutoresizingMaskIntoConstraints = false
            let offset: CGFloat = errorIcon.effectiveUserInterfaceLayoutDirection == .rightToLeft ? -0.0 : 0.0
            errorIcon.leadingAnchor.constraint(equalTo: errorIcon.superview!.leadingAnchor, constant: offset).isActive = true
            errorIcon.translatesAutoresizingMaskIntoConstraints = false
            errorIcon.topAnchor.constraint(equalTo: errorIcon.superview!.topAnchor, constant: 3).isActive = true
        }
        view.addSubview(validityMessageLabel)
        if showErrorIcon {
            validityMessageLabel.leadingAnchor.constraint(equalTo: errorIcon.trailingAnchor, constant: 6).isActive = true
            view.heightAnchor.constraint(greaterThanOrEqualTo: errorIcon.heightAnchor).isActive = true
        } else {
            validityMessageLabel.translatesAutoresizingMaskIntoConstraints = false
            validityMessageLabel.topAnchor.constraint(equalTo: validityMessageLabel.superview!.topAnchor).isActive = true
            validityMessageLabel.translatesAutoresizingMaskIntoConstraints = false
            let offset: CGFloat = validityMessageLabel.effectiveUserInterfaceLayoutDirection == .rightToLeft ? -0.0 : 0.0
            validityMessageLabel.leadingAnchor.constraint(equalTo: validityMessageLabel.superview!.leadingAnchor, constant: offset).isActive = true
            validityMessageLabel.translatesAutoresizingMaskIntoConstraints = false
            validityMessageLabel.bottomAnchor.constraint(equalTo: validityMessageLabel.superview!.bottomAnchor).isActive = true
        }
        return view
    }()
    lazy var detailLabel = makeLabel(error: false)
    private let themeProvider = DefaultThemeProvider.shared
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    private func makeLabel(error: Bool) -> UILabel {
        let label = UILabel()
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.textColor = error ? themeProvider.theme.textError : themeProvider.theme.textMuted
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }
    var formInputView: UIView? { nil }
    func validate() {}
    func refresh() { validate() }
    // Only the original setters survived; no unproved source getter is supplied.
    func setTitleText(_ incoming: String?) {
        let label = titleLabel
        if let item = cellItem, item.attribute.required { label.text = (incoming ?? "") + " *" }
        else { label.text = incoming }
    }
    func setDetailText(_ incoming: String?) {
        detailLabel.text = incoming
        detailLabel.isHidden = (incoming ?? "").isEmpty
    }
    func addSubviews() {
        guard let input = formInputView else { return }
        containerStackView.addArrangedSubview(titleLabel)
        pinInputHorizontal(titleLabel)
        containerStackView.addArrangedSubview(validityMessageView)
        pinInputHorizontal(validityMessageView)
        containerStackView.addArrangedSubview(input)
        pinInputHorizontal(input)
        containerStackView.addArrangedSubview(detailLabel)
        pinInputHorizontal(detailLabel)
    }
    private func pinInputHorizontal(_ child: UIView) {
        child.translatesAutoresizingMaskIntoConstraints = false
        if child.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor).isActive = true
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor).isActive = true
        } else {
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor).isActive = true
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor).isActive = true
        }
    }
    override func prepareForReuse() {
        super.prepareForReuse()
        titleLabel.removeFromSuperview()
        validityMessageView.removeFromSuperview()
        formInputView?.removeFromSuperview()
        detailLabel.removeFromSuperview()
    }
}
