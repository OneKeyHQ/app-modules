import UIKit

@objc(_TtC18IntercomSDKPrivate31ConversationSpaceViewController)
class ConversationSpaceViewController: UIViewController, ICMErrorViewDelegate {
    private let dataManager: ICMDataManagerProtocol
    private let openCallService: OpenCallServiceProtocol
    private let notificationCenter: NotificationCenterInterface
    private var errorViewController: ErrorViewController?

    @objc init() {
        dataManager = ICMDataManager.shared()
        openCallService = OpenCallService.shared
        notificationCenter = NotificationCenter.default
        super.init(nibName: nil, bundle: nil)
    }

    init(dataManager: ICMDataManagerProtocol, openCallService: OpenCallServiceProtocol, notificationCenter: NotificationCenterInterface) {
        self.dataManager = dataManager
        self.openCallService = openCallService
        self.notificationCenter = notificationCenter
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    @available(*, unavailable)
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }

    deinit { notificationCenter.removeObserver(self) }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupLoadingView()
        notificationCenter.addObserver(self, selector: #selector(openCallStateDidChange(_:)), name: Notification.Name("IntercomOpenCallStateDidChangeNotification"), object: nil)
        applyOpenCallState()
    }

    private func setupLoadingView() {
        let spinner = LoadingSpinnerView()
        view.addSubview(spinner)
        spinner.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        spinner.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
        spinner.startSpinning()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: false)
    }

    @objc func didSelectErrorAction() {
        removeErrorViewController()
        setupLoadingView()
        openCallService.retry()
    }

    private func removeErrorViewController() {
        if let errorViewController {
            errorViewController.willMove(toParent: nil)
            errorViewController.view.removeFromSuperview()
            errorViewController.removeFromParent()
        }
        errorViewController = nil
    }

    @objc private func openCallStateDidChange(_ notification: Notification) { applyOpenCallState() }

    private func applyOpenCallState() {
        switch openCallService.state {
        case .loading: break
        case .success: routeToCorrectConversation()
        case .failure: showOpenCallError()
        }
    }

    private func routeToCorrectConversation() {
        let conversation = dataManager.recentOpenInboundConversation ?? ICMConversation()
        let controller = ConversationViewController(conversation: conversation)
        navigationController?.setNavigationBarHidden(false, animated: true)
        intercomNavigationController()?.fade(to: controller)
        removeFromParent()
    }

    private func showOpenCallError() {
        for childView in view.subviews { childView.removeFromSuperview() }
        let title = TranslationManager.messenger.translation(forKey: "spaces_error_title")
        let subtitle = TranslationManager.messenger.translation(forKey: "spaces_error_description")
        let reload = TranslationManager.messenger.translation(forKey: "reload")
        let controller = ErrorViewController(title: title, subtitle: subtitle, showActionButton: true, actionButtonTitle: reload, themeProvider: DefaultThemeProvider.shared, icon: nil)
        controller.errorDelegate = self
        errorViewController = controller
        guard let parentView = view else { return }
        addChild(controller)
        parentView.addSubview(controller.view)
        controller.view.frame = parentView.bounds
        controller.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        controller.didMove(toParent: self)
    }
}

extension UIViewController {
    func intercomNavigationController() -> ICMNavigationController? {
        if let controller = navigationController as? ICMNavigationController { return controller }
        if navigationController != nil { _ = String(reflecting: type(of: self)) }
        return nil
    }
}
