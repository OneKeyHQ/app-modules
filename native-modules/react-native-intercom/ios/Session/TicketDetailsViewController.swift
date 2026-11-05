// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate27TicketDetailsViewController)
class TicketDetailsViewController: UIViewController, ICMContentViewController {
    @objc weak var contentDelegate: (any ICMContentViewControllerDelegate)?
    @objc var viewModel: TicketDetailsViewModel?
    let themeProvider = DefaultThemeProvider.shared
    var submittedViewConstraints: [NSLayoutConstraint] = []
    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.textColor = themeProvider.theme.textDefault
        label.font = .preferredFont(forTextStyle: .headline)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    lazy var statusHeaderView: UIView = {
        let view = UIView()
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    lazy var ticketDetailsHeaderView = TicketDetailsHeaderView(frame: .zero)
    lazy var ticketSubmittedView = TicketSubmittedView(frame: .zero)
    lazy var ticketAttributesIconView = TicketIconView(image: ICMAssets.descriptionIcon()!)
    lazy var ticketNotificationDetailsLabel: UILabel = {
        let label = UILabel()
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .semibold)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    lazy var ticketAttributeStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .fill
        stack.distribution = .fill
        stack.spacing = 16
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    lazy var ticketNotificationStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.isAccessibilityElement = true
        stack.layer.cornerRadius = 8
        stack.backgroundColor = themeProvider.theme.neutralContainer
        stack.addArrangedSubview(ticketNotificationDetailsLabel)
        stack.addArrangedSubview(bellIconImageView)
        stack.spacing = 12
        stack.layoutMargins = UIEdgeInsets(top: 16, left: 20, bottom: 16, right: 20)
        stack.isLayoutMarginsRelativeArrangement = true
        stack.isHidden = true
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    lazy var bellIconImageView: UIImageView = {
        let view = UIImageView()
        view.image = ICMAssets.bellIcon()
        view.tintColor = DefaultThemeProvider.shared.theme.textDefault
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 16).isActive = true
        view.translatesAutoresizingMaskIntoConstraints = false
        view.heightAnchor.constraint(equalToConstant: 16).isActive = true
        return view
    }()
    lazy var contentStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .fill
        stack.distribution = .fill
        stack.spacing = 20
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    lazy var scrollView: UIScrollView = {
        let view = UIScrollView()
        view.addSubview(scrollViewContentView)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        return view
    }()
    lazy var scrollViewContentView = makeContainer()
    let loadingView = LoadingSpinnerView(frame: .zero)
    lazy var loadingViewContainer = makeContainer()
    lazy var errorView: ICMErrorView? = {
        let title = TranslationManager.messenger.translation(forKey: "spaces_error_title")
        let message = TranslationManager.messenger.translation(forKey: "error_loading_ticket")
        let button = TranslationManager.messenger.translation(forKey: "reload")
        let view = ICMErrorView(title: title, message: message, showActionButton: true, actionButtonTitle: button)
        view?.translatesAutoresizingMaskIntoConstraints = false
        view?.delegate = viewModel!.errorDelegate
        return view
    }()
    lazy var conversationButtonContainer = makeContainer()
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    private func makeContainer() -> UIView {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.backButtonDisplayMode = .minimal
        view!.backgroundColor = DefaultThemeProvider.shared.theme.base
        contentDelegate?.configureContentTitleView?(titleLabel)
        statusHeaderView.addSubview(ticketDetailsHeaderView)
        pinEdges(ticketDetailsHeaderView)
        setupScrollView()
        setupSubmittedView()
        setupLoadingView()
        setupErrorView()
        viewModel!.viewDidLoad()
        contentDelegate?.contentDidLoad(withDismissEnabled: true)
        intercomNavigationController()?.displayCloseButton()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let navigationAnimation: Bool
        if #available(iOS 26.0, *) { navigationAnimation = false } else { navigationAnimation = animated }
        navigationController?.setNavigationBarHidden(false, animated: navigationAnimation)
        viewModel!.viewWillAppear()
    }
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        let model = viewModel!
        model.viewWillDisappear()
    }
    private func setupScrollView() {
        view!.addSubview(scrollView)
        contentStackView.addArrangedSubview(statusHeaderView)
        contentStackView.addArrangedSubview(ticketNotificationStackView)
        contentStackView.addArrangedSubview(ticketAttributesIconView)
        scrollViewContentView.addSubview(contentStackView)
        scrollViewContentView.addSubview(conversationButtonContainer)
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view!.bottomAnchor),
            scrollViewContentView.leadingAnchor.constraint(equalTo: scrollView.leadingAnchor),
            scrollViewContentView.trailingAnchor.constraint(equalTo: scrollView.trailingAnchor),
            scrollViewContentView.widthAnchor.constraint(equalTo: scrollView.widthAnchor),
            scrollViewContentView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            scrollViewContentView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            scrollViewContentView.heightAnchor.constraint(greaterThanOrEqualTo: view!.safeAreaLayoutGuide.heightAnchor),
            contentStackView.centerXAnchor.constraint(equalTo: scrollViewContentView.centerXAnchor),
            contentStackView.widthAnchor.constraint(equalTo: scrollViewContentView.widthAnchor, constant: -32),
            contentStackView.topAnchor.constraint(equalTo: scrollViewContentView.topAnchor),
            contentStackView.bottomAnchor.constraint(greaterThanOrEqualTo: conversationButtonContainer.topAnchor, constant: -16),
            conversationButtonContainer.leadingAnchor.constraint(equalTo: scrollViewContentView.leadingAnchor),
            conversationButtonContainer.trailingAnchor.constraint(equalTo: scrollViewContentView.trailingAnchor),
            conversationButtonContainer.bottomAnchor.constraint(equalTo: scrollViewContentView.bottomAnchor)
        ])
    }
    private func setupSubmittedView() {
        view!.addSubview(ticketSubmittedView)
        submittedViewConstraints = [ticketSubmittedView.topAnchor.constraint(equalTo: ticketDetailsHeaderView.topAnchor, constant: 45)]
        for constraint in submittedViewConstraints { constraint.isActive = true }
        pinHorizontal(ticketSubmittedView)
        ticketSubmittedView.alpha = 0
    }
    private func setupLoadingView() {
        view!.addSubview(loadingViewContainer)
        loadingViewContainer.addSubview(loadingView)
        loadingViewContainer.isHidden = true
        pinEdges(loadingViewContainer)
        pinEdges(loadingView)
    }
    private func setupErrorView() {
        guard let error = errorView else { return }
        view!.addSubview(error)
        error.isHidden = true
        NSLayoutConstraint.activate([
            error.leadingAnchor.constraint(equalTo: view!.layoutMarginsGuide.leadingAnchor),
            error.trailingAnchor.constraint(equalTo: view!.layoutMarginsGuide.trailingAnchor),
            error.centerYAnchor.constraint(equalTo: view!.centerYAnchor)
        ])
    }
    func updateNotificationDetails(_ value: NSAttributedString?) {
        ticketNotificationStackView.isHidden = value == nil
        ticketNotificationDetailsLabel.attributedText = value
        ticketNotificationStackView.isAccessibilityElement = true
        ticketNotificationStackView.accessibilityLabel = value?.string
    }
    func updateAttributes(_ attributes: [FormAttribute]) {
        ticketAttributesIconView.isHidden = attributes.compactMap(\.value).isEmpty
        let existing = ticketAttributeStackView.arrangedSubviews
        for view in existing {
            ticketAttributeStackView.removeArrangedSubview(view)
            view.removeFromSuperview()
        }
        let views: [TicketAttributeView] = attributes.compactMap { attribute in
            if attribute.formInputType == .files { return TicketAttachmentAttributeValueView(attribute: attribute) }
            return TicketStringAttributeValueView(attribute: attribute)
        }
        views.forEach { [weak stack = ticketAttributeStackView] view in stack?.addArrangedSubview(view) }
        ticketAttributesIconView.containerStackView.addArrangedSubview(ticketAttributeStackView)
    }
    func displaySubmittedView() {
        statusHeaderView.layoutIfNeeded()
        UIView.animate(withDuration: 0.5, delay: 0.5, options: UIView.AnimationOptions(rawValue: 0x20000), animations: {
            for constraint in self.submittedViewConstraints { constraint.constant = 0 }
            self.ticketSubmittedView.alpha = 1
            self.statusHeaderView.layoutIfNeeded()
        }, completion: { finished in
            if finished {
                UIView.animate(withDuration: 0.33, delay: 4, options: [], animations: {
                    self.ticketSubmittedView.alpha = 0
                    self.ticketDetailsHeaderView.alpha = 1
                })
            }
        })
    }
    @objc func showLoading() {
        if isViewLoaded {
            loadingView.startSpinning()
            loadingViewContainer.isHidden = false
        } else { contentDelegate?.showLoadingView(with: nil) }
    }
    @objc func hideLoading() {
        UIView.transition(with: view!, duration: 0.25, options: UIView.AnimationOptions(rawValue: 0x500000), animations: {
            self.loadingView.spinnerImageView.layer.removeAnimation(forKey: "spin")
            self.loadingView.isHidden = true
        }, completion: { _ in self.loadingViewContainer.isHidden = true })
    }
    @objc func reload() {}
    @objc func viewDidDismissBySwipe() {}
    @objc func closeButtonTapped() { contentDelegate?.closeContentModal() }
    func showConversationButton(title: String, icon: UIImage, target: Any, action: Selector) {
        let label = UILabel()
        label.text = TranslationManager.messenger.translation(forKey: "tickets_cta_text")
        label.textColor = themeProvider.theme.textMuted
        label.textAlignment = .center
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.numberOfLines = 0
        label.translatesAutoresizingMaskIntoConstraints = false
        let button = ICMActionButton(borderedButtonWithTitle: title)!
        button.setImage(icon, for: .normal)
        button.addTarget(target, action: action, for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        conversationButtonContainer.addSubview(label)
        conversationButtonContainer.addSubview(button)
        NSLayoutConstraint.activate([
            label.centerXAnchor.constraint(equalTo: conversationButtonContainer.centerXAnchor),
            label.leadingAnchor.constraint(equalTo: conversationButtonContainer.leadingAnchor, constant: 16),
            label.topAnchor.constraint(equalTo: conversationButtonContainer.topAnchor, constant: 16),
            label.bottomAnchor.constraint(equalTo: button.topAnchor, constant: -16),
            button.centerXAnchor.constraint(equalTo: conversationButtonContainer.centerXAnchor),
            button.bottomAnchor.constraint(equalTo: conversationButtonContainer.bottomAnchor, constant: -25)
        ])
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
    private func pinEdges(_ child: UIView) {
        child.translatesAutoresizingMaskIntoConstraints = false
        child.topAnchor.constraint(equalTo: child.superview!.topAnchor).isActive = true
        pinHorizontal(child)
        child.translatesAutoresizingMaskIntoConstraints = false
        child.bottomAnchor.constraint(equalTo: child.superview!.bottomAnchor).isActive = true
    }
}
