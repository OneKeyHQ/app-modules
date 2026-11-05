// Source recovery modifications: see docs/ARTICLE_RECOVERY.md.
import SwiftUI

@objc(_TtC18IntercomSDKPrivate24ArticleHostingController)
final class ArticleHostingController: UIViewController, ICMContentViewController {
    @objc weak var contentDelegate: (any ICMContentViewControllerDelegate)?
    let articleID: String
    let metricService: HelpCenterMetricService
    let allowsReactions: Bool
    let anchorLink: String?
    var hostingController: UIHostingController<AnyView>?

    @objc(initWithArticleID:metricService:allowsReactions:anchorLink:)
    init(articleID: String, metricService: HelpCenterMetricService, allowsReactions: Bool, anchorLink: String?) {
        self.articleID = articleID
        self.metricService = metricService
        self.allowsReactions = allowsReactions
        self.anchorLink = anchorLink
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        embedSwiftUIView()
        contentDelegate?.contentDidLoad(withDismissEnabled: true)
    }
    private func embedSwiftUIView() {
        let article = ArticleView(articleID: articleID, metricService: metricService, allowsReactions: allowsReactions, reactionService: ArticleReactionService(), initialAnchorLink: anchorLink)
        let host = UIHostingController(rootView: AnyView(article))
        host.view!.translatesAutoresizingMaskIntoConstraints = false
        addChild(host)
        view!.addSubview(host.view!)
        pinEdges(host.view!)
        host.didMove(toParent: self)
        hostingController = host
    }
    private func pinEdges(_ child: UIView) {
        child.translatesAutoresizingMaskIntoConstraints = false
        child.topAnchor.constraint(equalTo: child.superview!.topAnchor).isActive = true
        child.translatesAutoresizingMaskIntoConstraints = false
        if child.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor).isActive = true
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor).isActive = true
        } else {
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor).isActive = true
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor).isActive = true
        }
        child.translatesAutoresizingMaskIntoConstraints = false
        child.bottomAnchor.constraint(equalTo: child.superview!.bottomAnchor).isActive = true
    }
    @objc func reload() {}
    @objc func closeButtonTapped() { contentDelegate?.closeContentModal() }
    @objc func viewDidDismissBySwipe() {}
}
