import UIKit

@objc(ICMFakeComposerView)
class FakeComposerView: UIView {
    private let inactiveTextColor: UIColor
    private let horizontalPadding: CGFloat
    private lazy var labelView: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.text = TranslationManager.messenger.translation(forKey: "ask_a_question_with_ellipsis")
        label.textColor = inactiveTextColor
        return label
    }()
    private lazy var containerView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 16
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    @objc init(inactiveTextColor: UIColor, horizontalPadding: CGFloat) {
        self.inactiveTextColor = inactiveTextColor
        self.horizontalPadding = horizontalPadding
        super.init(frame: .zero)
        addSubview(containerView)
        let view = containerView
        let padding = self.horizontalPadding + 0.0
        let parent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        let top = view.topAnchor.constraint(equalTo: parent.topAnchor, constant: 16)
        top.priority = .required
        top.isActive = true
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            let constraint = view.leftAnchor.constraint(equalTo: parent.leftAnchor, constant: padding)
            constraint.priority = .required
            constraint.isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            let constraint = view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -padding)
            constraint.priority = .required
            constraint.isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
        let bottomParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        let bottom = view.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -padding)
        bottom.priority = .required
        bottom.isActive = true
        containerView.addArrangedSubview(labelView)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
}
