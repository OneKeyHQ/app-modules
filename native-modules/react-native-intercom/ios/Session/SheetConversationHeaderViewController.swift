import UIKit

@objc(_TtC18IntercomSDKPrivate37SheetConversationHeaderViewController)
class SheetConversationHeaderViewController: UIViewController, ICMContentViewController {
    @objc weak var contentDelegate: ICMContentViewControllerDelegate?
    private let themeProvider: ThemeProvider
    private let expandedHeader: ExpandedConversationHeader
    private let displayActiveIndicator: Bool
    private let conversation: ICMConversation?
    private let preferredWidth: CGFloat?
    private let config: ICMConfigProtocol
    private var lastResolvedSheetHeight: CGFloat?
    private lazy var scrollView: UIScrollView = {
        let view = UIScrollView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    lazy var contentView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var converstionHeaderView: ExpandedConversationHeaderView = {
        let width = availableWidth
        let header = expandedHeader
        let active = displayActiveIndicator
        let initialConfig = ICMConfig.shared()
        let config = ICMConfig.shared()!
        let theme = DefaultThemeProvider.shared
        let adjusted = width - 32
        let view = ExpandedConversationHeaderView(.zero, expandedHeader: header, displayActiveIndicator: active,
                                                  maxWidth: adjusted >= 0 ? adjusted : 0, config: config,
                                                  themeProvider: theme)
        withExtendedLifetime(initialConfig) {}
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var temporaryExpectationsView: TemporaryExpectationsView? = {
        guard let conversation, let notice = conversation.specialNotice, !notice.isEmpty else { return nil }
        let view = TemporaryExpectationsView(themeProvider: DefaultThemeProvider.shared)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.configure(with: notice)
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()

    init(expandedHeader: ExpandedConversationHeader, displayActiveIndicator: Bool, conversation: ICMConversation?,
         preferredWidth: CGFloat?, config: ICMConfigProtocol, themeProvider: ThemeProvider) {
        self.expandedHeader = expandedHeader
        self.displayActiveIndicator = displayActiveIndicator
        self.conversation = conversation
        self.preferredWidth = preferredWidth
        self.config = config
        self.themeProvider = themeProvider
        super.init(nibName: nil, bundle: nil)
        view!.backgroundColor = self.themeProvider.theme.base
        setupScrollView()
        setupHeaderView()
        setupTemporaryExpectationsView()
        let lastView: UIView = temporaryExpectationsView ?? converstionHeaderView
        contentView.bottomAnchor.constraint(greaterThanOrEqualTo: lastView.bottomAnchor, constant: 24).isActive = true
    }

    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    var availableWidth: CGFloat {
        if view!.window != nil || preferredWidth == nil { return view!.bounds.width }
        return preferredWidth!
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.backButtonDisplayMode = .minimal
        contentDelegate?.contentDidLoad(withDismissEnabled: true)
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        if #available(iOS 16.0, *) {
            let content = contentView
            let width = availableWidth
            let height = content.systemLayoutSizeFitting(CGSize(width: width, height: UIView.layoutFittingCompressedSize.height),
                                                        withHorizontalFittingPriority: .required,
                                                        verticalFittingPriority: .fittingSizeLevel).height
            if lastResolvedSheetHeight == height { return }
            lastResolvedSheetHeight = height
            sheetPresentationController?.animateChanges {
                self.sheetPresentationController?.invalidateDetents()
            }
        }
    }

    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let previousStyle = previousTraitCollection?.userInterfaceStyle
        let currentStyle = traitCollection.userInterfaceStyle
        if previousStyle == nil || previousStyle != currentStyle {
            temporaryExpectationsView?.layer.borderColor = themeProvider.theme.neutralBorder.cgColor
        }
    }

    @objc func reload() {}
    @objc func viewDidDismissBySwipe() {}
    @objc func closeButtonTapped() { contentDelegate?.closeContentModal() }

    private func setupScrollView() {
        view!.addSubview(scrollView)
        scrollView.addSubview(contentView)
        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: view!.topAnchor),
            scrollView.leadingAnchor.constraint(equalTo: view!.leadingAnchor),
            scrollView.trailingAnchor.constraint(equalTo: view!.trailingAnchor),
            scrollView.bottomAnchor.constraint(equalTo: view!.bottomAnchor),
            contentView.topAnchor.constraint(equalTo: scrollView.contentLayoutGuide.topAnchor),
            contentView.leadingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.leadingAnchor),
            contentView.trailingAnchor.constraint(equalTo: scrollView.contentLayoutGuide.trailingAnchor),
            contentView.bottomAnchor.constraint(equalTo: scrollView.contentLayoutGuide.bottomAnchor),
            contentView.widthAnchor.constraint(equalTo: scrollView.frameLayoutGuide.widthAnchor)
        ])
    }

    private func setupHeaderView() {
        contentView.addSubview(converstionHeaderView)
        converstionHeaderView.translatesAutoresizingMaskIntoConstraints = false
        converstionHeaderView.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint(item: converstionHeaderView, attribute: .centerX, relatedBy: .equal,
                           toItem: converstionHeaderView.superview!, attribute: .centerX, multiplier: 1, constant: 0).isActive = true
        converstionHeaderView.translatesAutoresizingMaskIntoConstraints = false
        converstionHeaderView.translatesAutoresizingMaskIntoConstraints = false
        converstionHeaderView.topAnchor.constraint(equalTo: converstionHeaderView.superview!.topAnchor, constant: 24).isActive = true
        converstionHeaderView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16).isActive = true
        converstionHeaderView.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16).isActive = true
    }

    private func setupTemporaryExpectationsView() {
        guard let notice = temporaryExpectationsView else { return }
        contentView.addSubview(notice)
        let leading = notice.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16)
        leading.priority = UILayoutPriority(999)
        let trailing = notice.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16)
        trailing.priority = UILayoutPriority(999)
        NSLayoutConstraint.activate([
            notice.topAnchor.constraint(equalTo: converstionHeaderView.bottomAnchor, constant: 32),
            notice.centerXAnchor.constraint(equalTo: contentView.centerXAnchor),
            notice.widthAnchor.constraint(lessThanOrEqualToConstant: 400),
            leading, trailing
        ])
    }
}
