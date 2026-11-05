import UIKit

@objc(_TtC18IntercomSDKPrivate19TicketSubmittedView)
class TicketSubmittedView: UIView {
    private lazy var submittedImageView: UIImageView = {
        let image = UIImageView()
        image.image = ICMAssets.successImage()
        image.contentMode = .scaleAspectFit
        image.translatesAutoresizingMaskIntoConstraints = false
        image.widthAnchor.constraint(equalToConstant: 40).isActive = true
        image.heightAnchor.constraint(equalToConstant: 40).isActive = true
        return image
    }()
    private lazy var titleLabel = makeLabel(key: "tickets_created_confirmation_header", style: .callout)
    private lazy var detailsLabel = makeLabel(key: "tickets_submitted_confirmation_paragraph", style: .subheadline)
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView(arrangedSubviews: [submittedImageView, titleLabel, detailsLabel])
        stack.axis = .vertical
        stack.alignment = .center
        stack.distribution = .fill
        stack.spacing = 16
        stack.setCustomSpacing(11, after: titleLabel)
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    override init(frame: CGRect) {
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        backgroundColor = DefaultThemeProvider.shared.theme.base
        addSubview(containerStackView)
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        containerStackView.topAnchor.constraint(equalTo: containerStackView.superview!.topAnchor, constant: 24).isActive = true
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        if containerStackView.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            containerStackView.rightAnchor.constraint(equalTo: containerStackView.superview!.rightAnchor, constant: -35).isActive = true
            containerStackView.leftAnchor.constraint(equalTo: containerStackView.superview!.leftAnchor, constant: 35).isActive = true
        } else {
            containerStackView.leftAnchor.constraint(equalTo: containerStackView.superview!.leftAnchor, constant: 35).isActive = true
            containerStackView.rightAnchor.constraint(equalTo: containerStackView.superview!.rightAnchor, constant: -35).isActive = true
        }
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        containerStackView.bottomAnchor.constraint(equalTo: containerStackView.superview!.bottomAnchor, constant: -15).isActive = true
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func makeLabel(key: String, style: UIFont.TextStyle) -> UILabel {
        let label = UILabel()
        label.text = TranslationManager.messenger.translation(forKey: key)
        label.textAlignment = .center
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.font = .preferredFont(forTextStyle: style)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }
}
