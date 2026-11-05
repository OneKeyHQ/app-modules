// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate20FloatingActionButton)
class FloatingActionButton: UIView {
    var unreadCount = 0 {
        didSet {
            unreadIndicatorLabel.text = String(unreadCount)
            unreadIndicatorView.isHidden = unreadCount == 0
        }
    }
    let themeProvider: any ThemeProvider
    let onTap: (() -> Void)?
    let image: UIImage
    private lazy var imageView: UIImageView = {
        let view = UIImageView()
        view.image = image
        view.tintColor = themeProvider.theme.iconDefault
        return view
    }()
    private let backgroundView: UIView = {
        let view = UIView()
        view.layer.cornerRadius = 18
        view.layer.borderWidth = 1
        return view
    }()
    private let containerView: UIView = {
        let view = UIView()
        view.backgroundColor = .clear
        return view
    }()
    private lazy var unreadIndicatorView: UIView = {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.statusNotificationFill
        view.layer.cornerRadius = 8
        view.isHidden = true
        return view
    }()
    private let unreadIndicatorLabel: UILabel = {
        let label = UILabel()
        label.textColor = .white
        label.font = .systemFont(ofSize: 11, weight: .bold)
        return label
    }()

    init(image: UIImage, onTap: (() -> Void)?, themeProvider: any ThemeProvider) {
        self.image = image
        self.onTap = onTap
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        setupView()
        backgroundView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(performAction)))
        applyElevationOneShadow(cornerRadius: 8)
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    private func setupView() {
        addSubview(containerView)
        containerView.addSubview(backgroundView)
        backgroundView.addSubview(imageView)
        containerView.addSubview(unreadIndicatorView)
        unreadIndicatorView.addSubview(unreadIndicatorLabel)
        backgroundView.backgroundColor = themeProvider.theme.base
        backgroundView.layer.borderColor = themeProvider.theme.neutralBorder.cgColor
        setupConstraints()
    }

    @objc private func performAction() { onTap?() }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        if previousTraitCollection == nil || previousTraitCollection!.userInterfaceStyle != traitCollection.userInterfaceStyle {
            backgroundView.layer.borderColor = themeProvider.theme.neutralBorder.cgColor
            applyElevationOneShadow(cornerRadius: 8)
        }
    }

    private func setupConstraints() {
        containerView.translatesAutoresizingMaskIntoConstraints = false
        let containerViewTopParent = containerView.superview!
        containerView.translatesAutoresizingMaskIntoConstraints = false
        containerView.topAnchor.constraint(equalTo: containerViewTopParent.topAnchor, constant: 3.996).isActive = true
        if containerView.effectiveUserInterfaceLayoutDirection == .leftToRight {
            containerView.translatesAutoresizingMaskIntoConstraints = false
            let leftParent = containerView.superview!
            containerView.translatesAutoresizingMaskIntoConstraints = false
            containerView.leftAnchor.constraint(equalTo: leftParent.leftAnchor, constant: 3.996).isActive = true
            containerView.translatesAutoresizingMaskIntoConstraints = false
            let rightParent = containerView.superview!
            containerView.translatesAutoresizingMaskIntoConstraints = false
            containerView.rightAnchor.constraint(equalTo: rightParent.rightAnchor, constant: -3.996).isActive = true
        } else {
            containerView.translatesAutoresizingMaskIntoConstraints = false
            let rightParent = containerView.superview!
            containerView.translatesAutoresizingMaskIntoConstraints = false
            containerView.rightAnchor.constraint(equalTo: rightParent.rightAnchor, constant: -3.996).isActive = true
            containerView.translatesAutoresizingMaskIntoConstraints = false
            let leftParent = containerView.superview!
            containerView.translatesAutoresizingMaskIntoConstraints = false
            containerView.leftAnchor.constraint(equalTo: leftParent.leftAnchor, constant: 3.996).isActive = true
        }
        containerView.translatesAutoresizingMaskIntoConstraints = false
        let containerViewBottomParent = containerView.superview!
        containerView.translatesAutoresizingMaskIntoConstraints = false
        containerView.bottomAnchor.constraint(equalTo: containerViewBottomParent.bottomAnchor, constant: -3.996).isActive = true
        backgroundView.translatesAutoresizingMaskIntoConstraints = false
        let backgroundViewTopParent = backgroundView.superview!
        backgroundView.translatesAutoresizingMaskIntoConstraints = false
        backgroundView.topAnchor.constraint(equalTo: backgroundViewTopParent.topAnchor, constant: 3.996).isActive = true
        if backgroundView.effectiveUserInterfaceLayoutDirection == .leftToRight {
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            let leftParent = backgroundView.superview!
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            backgroundView.leftAnchor.constraint(equalTo: leftParent.leftAnchor, constant: 3.996).isActive = true
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            let rightParent = backgroundView.superview!
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            backgroundView.rightAnchor.constraint(equalTo: rightParent.rightAnchor, constant: -3.996).isActive = true
        } else {
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            let rightParent = backgroundView.superview!
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            backgroundView.rightAnchor.constraint(equalTo: rightParent.rightAnchor, constant: -3.996).isActive = true
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            let leftParent = backgroundView.superview!
            backgroundView.translatesAutoresizingMaskIntoConstraints = false
            backgroundView.leftAnchor.constraint(equalTo: leftParent.leftAnchor, constant: 3.996).isActive = true
        }
        backgroundView.translatesAutoresizingMaskIntoConstraints = false
        let backgroundViewBottomParent = backgroundView.superview!
        backgroundView.translatesAutoresizingMaskIntoConstraints = false
        backgroundView.bottomAnchor.constraint(equalTo: backgroundViewBottomParent.bottomAnchor, constant: -3.996).isActive = true
        backgroundView.translatesAutoresizingMaskIntoConstraints = false
        backgroundView.widthAnchor.constraint(equalToConstant: 36).isActive = true
        backgroundView.translatesAutoresizingMaskIntoConstraints = false
        backgroundView.heightAnchor.constraint(equalToConstant: 36).isActive = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.widthAnchor.constraint(equalToConstant: 16).isActive = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.heightAnchor.constraint(equalToConstant: 16).isActive = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.centerXAnchor.constraint(equalTo: imageView.superview!.centerXAnchor).isActive = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.centerYAnchor.constraint(equalTo: imageView.superview!.centerYAnchor).isActive = true
        unreadIndicatorView.translatesAutoresizingMaskIntoConstraints = false
        unreadIndicatorView.widthAnchor.constraint(equalToConstant: 16).isActive = true
        unreadIndicatorView.translatesAutoresizingMaskIntoConstraints = false
        unreadIndicatorView.heightAnchor.constraint(equalToConstant: 16).isActive = true
        unreadIndicatorView.translatesAutoresizingMaskIntoConstraints = false
        let indicatorParent = unreadIndicatorView.superview!
        let trailing: CGFloat = unreadIndicatorView.effectiveUserInterfaceLayoutDirection == .rightToLeft ? 0 : -0.0
        unreadIndicatorView.trailingAnchor.constraint(equalTo: indicatorParent.trailingAnchor, constant: trailing).isActive = true
        unreadIndicatorLabel.translatesAutoresizingMaskIntoConstraints = false
        unreadIndicatorLabel.centerXAnchor.constraint(equalTo: unreadIndicatorLabel.superview!.centerXAnchor).isActive = true
        unreadIndicatorLabel.translatesAutoresizingMaskIntoConstraints = false
        unreadIndicatorLabel.centerYAnchor.constraint(equalTo: unreadIndicatorLabel.superview!.centerYAnchor).isActive = true
    }
}
