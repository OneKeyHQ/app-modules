import UIKit
import WebKit

@objc(_TtC18IntercomSDKPrivate14VideoBlockView)
class VideoBlockView: UIView, ConversationCellContentView, WKNavigationDelegate {
    var content: ITBVideoBlock
    private let notificationCenter: any NotificationCenterInterface
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private var webViewHeightConstraint: NSLayoutConstraint?
    var isPausedOffscreen = false
    lazy var webView: WKWebView = {
        let view = WKWebView()
        view.scrollView.scrollsToTop = false
        view.scrollView.isScrollEnabled = false
        view.navigationDelegate = self
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var failureImageView: UIImageView = {
        let image = UIImage(named: "image-loading-fail", in: ITBBlockUtils.loadBlocksAssetsBundle(), compatibleWith: nil)
        _ = image?.withRenderingMode(.alwaysOriginal)
        let view = UIImageView(image: image)
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var loadingView: ShimmeringView = {
        let view = ShimmeringView()
        view.accessibilityIdentifier = "shimmerView"
        view.translatesAutoresizingMaskIntoConstraints = false
        view.shimmeringBeginFadeDuration = 0.1
        view.shimmeringPauseDuration = 0.1
        view.shimmeringOpacity = 1
        view.shimmeringAnimationOpacity = 0.2
        view.shimmeringHighlightLength = 0.98
        return view
    }()

    init?(content: ITBBlock, notificationCenter: any NotificationCenterInterface = NotificationCenter.default, delegate: (any ConversationCellBlockDelegate)?) {
        guard let content = content as? ITBVideoBlock else { return nil }
        self.content = content
        self.notificationCenter = notificationCenter
        self.delegate = delegate
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configureWebView()
        loadVideo()
        configureLoadingView()
        configureFailureImage()
        notificationCenter.addObserver(self, selector: #selector(orientationChanged), name: UIDevice.orientationDidChangeNotification, object: nil)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    deinit { notificationCenter.removeObserver(self) }

    private func configureWebView() {
        addSubview(webView)
        let height = webView.heightAnchor.constraint(equalToConstant: content.videoSize.height)
        height.priority = UILayoutPriority(rawValue: 999)
        height.isActive = true
        webViewHeightConstraint = height
        NSLayoutConstraint.activate([
            webView.topAnchor.constraint(equalTo: topAnchor),
            webView.bottomAnchor.constraint(equalTo: bottomAnchor),
            webView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 10),
            webView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -10),
        ])
    }

    private func loadVideo() {
        let provider = content.provider!
        let videoId = content.videoId!
        guard let template = VideoHTMLTemplateFactory.getHTMLTemplate(provider: provider, videoId: videoId) else { return }
        let identifier = Bundle.main.bundleIdentifier ?? "com.intercom.sdk"
        let baseURL = URL(string: ("https://" + identifier).lowercased())
        webView.loadHTMLString(template, baseURL: baseURL)
    }

    private func configureLoadingView() {
        addSubview(loadingView)
        NSLayoutConstraint.activate([
            loadingView.centerXAnchor.constraint(equalTo: webView.centerXAnchor),
            loadingView.centerYAnchor.constraint(equalTo: webView.centerYAnchor),
            loadingView.heightAnchor.constraint(equalTo: webView.heightAnchor),
            loadingView.widthAnchor.constraint(equalTo: webView.widthAnchor),
        ])
        loadingView.contentView = webView
        loadingView.isShimmering = true
    }

    private func configureFailureImage() {
        addSubview(failureImageView)
        NSLayoutConstraint.activate([
            failureImageView.centerXAnchor.constraint(equalTo: centerXAnchor),
            failureImageView.centerYAnchor.constraint(equalTo: centerYAnchor),
            failureImageView.heightAnchor.constraint(equalToConstant: 32),
            failureImageView.widthAnchor.constraint(equalToConstant: 32),
        ])
        failureImageView.isHidden = true
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        guard let superview = superview else { return }
        let width = superview.frame.width - 20
        let height = width * 0.5625
        let previousSize = content.videoSize
        if previousSize.width == width && previousSize.height == height { return }
        webViewHeightConstraint?.constant = height
        content.videoSize = CGSize(width: width, height: height)
        delegate?.reload(content: content)
    }
    @objc private func orientationChanged() {
        content.videoSize = .zero
        delegate?.reload(content: content)
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) { loadingView.isShimmering = false }
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        loadingView.isShimmering = false
        failureImageView.isHidden = false
    }
}
