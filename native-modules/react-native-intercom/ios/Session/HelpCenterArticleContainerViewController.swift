import UIKit
import SwiftUI

@objc(_TtC18IntercomSDKPrivate40HelpCenterArticleContainerViewController)
class HelpCenterArticleContainerViewController: UIViewController, ICMContentViewControllerDelegate {
    private let themeProvider: any ThemeProvider
    private let requestedArticleId: String?
    private var articleHostingController: UIHostingController<AnyView>?
    private let metricService: any HelpCenterMetricServiceProtocol
    private let dataManager: any ICMDataManagerProtocol
    init(withArticle id: String, metricService: any HelpCenterMetricServiceProtocol, dataManager: any ICMDataManagerProtocol, themeProvider: any ThemeProvider) {
        self.themeProvider = themeProvider
        requestedArticleId = id
        self.metricService = metricService
        self.dataManager = dataManager
        super.init(nibName: nil, bundle: nil)
    }
    // Only the shipped four-argument allocation body is recovered; the erased two-argument entry remains a private ABI boundary.
    @objc(initWithArticle:metricService:)
    convenience init(withArticle id: String, metricService: HelpCenterMetricService) {
        self.init(withArticle: id, metricService: metricService, dataManager: ICMDataManager.shared(), themeProvider: DefaultThemeProvider.shared)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        view!.backgroundColor = DefaultThemeProvider.shared.theme.base
        navigationItem.backButtonDisplayMode = .minimal
        intercomNavigationController()?.displayCloseButton()
        view!.accessibilityIdentifier = "intercom article container view controller"
        setupArticleView()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        let navigationAnimated: Bool
        if #available(iOS 26.0, *) { navigationAnimated = false } else { navigationAnimated = animated }
        navigationController?.setNavigationBarHidden(false, animated: navigationAnimated)
    }
    private func setupArticleView() {
        guard let metric = metricService as? HelpCenterMetricService else { return }
        guard let id = requestedArticleId else { return }
        let article = ArticleView(articleID: id, metricService: metric, allowsReactions: true, reactionService: ArticleReactionService(), initialAnchorLink: nil)
            .onAppear { [weak self] in
                let manager = self?.dataManager
                manager?.helpCenterArticleViewed = true
            }
        let host = UIHostingController(rootView: AnyView(article))
        addChild(host)
        view!.addSubview(host.view!)
        host.view!.frame = view!.bounds
        host.view!.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        host.didMove(toParent: self)
        articleHostingController = host
    }
    // These seven delegate bodies are original no-ops, including the non-invoked optional callbacks.
    func contentDidLoad(withDismissEnabled enabled: Bool) {}
    func contentDidLoad(withDismissEnabled enabled: Bool, swipeDismissalRequiresConfirmation confirmation: Bool, didAttemptDismiss attempt: (() -> Void)?) {}
    func showLoadingView(with image: UIImage?) {}
    func contentLoadingDidFail(_ title: String?, showActionButton show: Bool, errorMessageSubtitle subtitle: String?, actionButtonTitle buttonTitle: String?) {}
    func closeContentModal() {}
    func configureDismissEnabled(_ enabled: Bool) {}
    func configureDismissEnabled(_ enabled: Bool, swipeDismissalRequiresConfirmation confirmation: Bool, didAttemptDismiss attempt: (() -> Void)?) {}
}
