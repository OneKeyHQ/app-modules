import UIKit

@objc(_TtC18IntercomSDKPrivate29TicketCreateFormTableViewCell)
class TicketCreateFormTableViewCell: UITableViewCell {
    lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .leading
        stack.distribution = .fill
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    func configure(with item: any TicketCreateFormCellItem) {
        contentView.addSubview(containerStackView)
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        containerStackView.topAnchor.constraint(equalTo: containerStackView.superview!.topAnchor).isActive = true
        pinHorizontal(containerStackView, left: 16, right: 16)
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        containerStackView.bottomAnchor.constraint(equalTo: containerStackView.superview!.bottomAnchor, constant: -24).isActive = true
    }
    fileprivate func pinHorizontal(_ child: UIView, left: CGFloat = 0, right: CGFloat = 0) {
        child.translatesAutoresizingMaskIntoConstraints = false
        if child.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor, constant: -right).isActive = true
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor, constant: left).isActive = true
        } else {
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor, constant: left).isActive = true
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor, constant: -right).isActive = true
        }
    }
}

@objc(_TtC18IntercomSDKPrivate35TicketCreateFormActionTableViewCell)
class TicketCreateFormActionTableViewCell: TicketCreateFormTableViewCell, TicketCreateFormActionCellItemDelegate {
    var buttonTitle: String?
    var buttonImage: UIImage?
    var buttonAction: (() -> Void)?
    lazy var actionButton: UIButton = buildActionButton()
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func configure(with item: any TicketCreateFormCellItem) {
        super.configure(with: item)
        guard let item = item as? TicketCreateFormActionCellItem else { return }
        item.cellDelegate = self
        buttonTitle = item.buttonTitle
        buttonImage = item.buttonImage
        buttonAction = item.buttonAction
        containerStackView.addArrangedSubview(actionButton)
        pinHorizontal(actionButton)
    }
    func buildActionButton() -> UIButton { UIButton() }
    func refresh() {
        if let button = actionButton as? ICMSubmitActionButton { DispatchQueue.main.async { button.resetButtonState() } }
    }
    func setActionButtonState(_ enabled: Bool, isLoading: Bool) {
        actionButton.isEnabled = enabled
        if let button = actionButton as? ICMSubmitActionButton {
            if isLoading { button.showLoadingState() } else { button.resetButtonState() }
        }
    }
}

@objc(_TtC18IntercomSDKPrivate35TicketCreateFormCancelTableViewCell)
class TicketCreateFormCancelTableViewCell: TicketCreateFormActionTableViewCell {
    let themeProvider = DefaultThemeProvider.shared
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildActionButton() -> UIButton {
        let button = UIButton()
        button.addTarget(self, action: #selector(cancelButtonTapped), for: .touchUpInside)
        button.setTitle(buttonTitle, for: .normal)
        button.setTitleColor(themeProvider.theme.textDefault, for: .normal)
        if let label = button.titleLabel {
            let font = UIFont.preferredFont(forTextStyle: .subheadline)
            if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) { label.font = UIFont(descriptor: descriptor, size: 0) }
            else { label.font = font }
        }
        button.translatesAutoresizingMaskIntoConstraints = false
        button.heightAnchor.constraint(equalToConstant: 24).isActive = true
        return button
    }
    @objc func cancelButtonTapped() { buttonAction?() }
}

@objc(_TtC18IntercomSDKPrivate35TicketCreateFormSubmitTableViewCell)
class TicketCreateFormSubmitTableViewCell: TicketCreateFormActionTableViewCell {
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override func buildActionButton() -> UIButton {
        let button = ICMSubmitActionButton()
        button.addTarget(self, action: #selector(submitButtonTapped), for: .touchUpInside)
        button.setTitle(buttonTitle, for: .normal)
        button.setImage(buttonImage, for: .normal)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.heightAnchor.constraint(equalToConstant: 40).isActive = true
        return button
    }
    @objc func submitButtonTapped() { buttonAction?() }
}
