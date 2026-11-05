import UIKit

@objc(_TtC18IntercomSDKPrivate14TicketIconView)
class TicketIconView: UIView {
    let icon: UIImage
    lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .top
        stack.distribution = .fill
        stack.spacing = 16
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    init(image: UIImage) {
        icon = image
        super.init(frame: .zero)
        addSubview(containerStackView)
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        containerStackView.topAnchor.constraint(equalTo: containerStackView.superview!.topAnchor).isActive = true
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        if containerStackView.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            containerStackView.rightAnchor.constraint(equalTo: containerStackView.superview!.rightAnchor, constant: -8).isActive = true
            containerStackView.leftAnchor.constraint(equalTo: containerStackView.superview!.leftAnchor, constant: 8).isActive = true
        } else {
            containerStackView.leftAnchor.constraint(equalTo: containerStackView.superview!.leftAnchor, constant: 8).isActive = true
            containerStackView.rightAnchor.constraint(equalTo: containerStackView.superview!.rightAnchor, constant: -8).isActive = true
        }
        containerStackView.translatesAutoresizingMaskIntoConstraints = false
        containerStackView.bottomAnchor.constraint(equalTo: containerStackView.superview!.bottomAnchor, constant: -16).isActive = true
        translatesAutoresizingMaskIntoConstraints = false
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
}
