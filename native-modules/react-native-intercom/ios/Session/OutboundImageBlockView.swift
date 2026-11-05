import UIKit

@objc(_TtC18IntercomSDKPrivate22OutboundImageBlockView)
class OutboundImageBlockView: UIView, ConversationCellContentView, ITBUploadProgressDelegate {
    let content: ITBImageBlock
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private let imageLoader: ITBImageLoader
    private let themeProvider: any ThemeProvider
    private lazy var imageTapGesture = UITapGestureRecognizer(target: self, action: #selector(imageTapped(_:)))
    private lazy var imageView: ITBAnimatedImageView = defaultImageView()
    private lazy var failureImageView: UIImageView = {
        let image = UIImage(named: "image-loading-fail", in: ITBBlockUtils.loadBlocksAssetsBundle(), compatibleWith: nil)
        _ = image?.withRenderingMode(.alwaysOriginal)
        let view = UIImageView(image: image)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.accessibilityElementsHidden = true
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
        view.shimmeringHighlightLength = 0.99
        view.accessibilityElementsHidden = true
        view.layer.masksToBounds = true
        return view
    }()
    private lazy var progressButtonView: ITBUploadProgressButton = {
        let button = ITBUploadProgressButton()
        button.progress = content.upload!.uploadProgress
        button.addTarget(self, action: #selector(progressButtonViewTapped), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()
    private var loadingViewWidthConstraint: NSLayoutConstraint?
    private var loadingViewAspectRatioConstraint: NSLayoutConstraint?
    private var loadingViewMaxWidthConstraint: NSLayoutConstraint?
    private static let darkLoadingViewColor = UIColor(red: 0.4, green: 0.4, blue: 0.4, alpha: 0.15)
    private static let lightLoadingViewColor = UIColor(red: 1, green: 1, blue: 1, alpha: 0.15)

    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?, imageLoader: ITBImageLoader, imageView: ITBAnimatedImageView?, themeProvider: any ThemeProvider) {
        guard let content = content as? ITBImageBlock else { return nil }
        self.content = content
        self.delegate = delegate
        self.imageLoader = imageLoader
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        self.imageView = imageView ?? defaultImageView()
        translatesAutoresizingMaskIntoConstraints = false
        configure()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    deinit {}
    override var intrinsicContentSize: CGSize { loadingView.systemLayoutSizeFitting(UIView.layoutFittingCompressedSize) }

    private func defaultImageView() -> ITBAnimatedImageView {
        let view = ITBAnimatedImageView(imageLoader: imageLoader) ?? ITBAnimatedImageView()
        view.contentMode = .scaleAspectFit
        view.isUserInteractionEnabled = true
        view.backgroundColor = .clear
        view.clipsToBounds = true
        view.addGestureRecognizer(imageTapGesture)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.accessibilityElementsHidden = true
        return view
    }
    private func configure() {
        configureLoadingViewLayout()
        configureImageViewLayout()
        configureProgressButtonViewIfNecessary()
        loadImage()
        if !(content.text ?? "").isEmpty { accessibilityLabel = content.text ?? "" }
        isAccessibilityElement = true
        accessibilityLabel = "Image"
        accessibilityHint = "Double tap to open"
    }
    private func configureImageViewLayout() {
        loadingView.contentView = imageView
        imageView.backgroundColor = content.loadingViewColorType == 1 ? Self.lightLoadingViewColor : Self.darkLoadingViewColor
        imageView.topAnchor.constraint(equalTo: loadingView.topAnchor).isActive = true
        if imageView.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            imageView.rightAnchor.constraint(equalTo: loadingView.rightAnchor, constant: -CGFloat.zero).isActive = true
            imageView.leftAnchor.constraint(equalTo: loadingView.leftAnchor).isActive = true
        } else {
            imageView.leftAnchor.constraint(equalTo: loadingView.leftAnchor).isActive = true
            imageView.rightAnchor.constraint(equalTo: loadingView.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        imageView.bottomAnchor.constraint(equalTo: loadingView.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
    private func configureLoadingViewLayout() {
        addSubview(loadingView)
        loadingView.translatesAutoresizingMaskIntoConstraints = false
        loadingView.topAnchor.constraint(equalTo: topAnchor, constant: content.padding).isActive = true
        if loadingView.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            loadingView.leadingAnchor.constraint(equalTo: rightAnchor, constant: -CGFloat.zero).isActive = true
        } else { loadingView.leadingAnchor.constraint(equalTo: leftAnchor).isActive = true }
        let bottom = loadingView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -content.padding)
        bottom.priority = .defaultHigh
        bottom.isActive = true
        let width = content.width > 0 ? content.width : 268
        let fixed = loadingView.widthAnchor.constraint(equalToConstant: width)
        loadingViewWidthConstraint = fixed
        fixed.priority = .defaultHigh
        fixed.isActive = true
        let maximum = loadingView.widthAnchor.constraint(lessThanOrEqualTo: widthAnchor)
        loadingViewMaxWidthConstraint = maximum
        maximum.isActive = true
        let originalWidth = content.width
        let originalHeight = content.height
        let ratio = originalHeight > 0 && originalWidth > 0 ? originalHeight / originalWidth : 1
        let height = loadingView.heightAnchor.constraint(equalTo: loadingView.widthAnchor, multiplier: ratio)
        loadingViewAspectRatioConstraint = height
        height.isActive = true
        loadingView.shimmeringSpeed = (width > 200 ? 200 : width) * 0.6
        loadingView.isShimmering = true
    }
    private func configureProgressButtonViewIfNecessary() {
        guard let upload = content.upload, upload.uploadProgress < 1 else { return }
        upload.progressDelegate = self
        addSubview(progressButtonView)
        progressButtonView.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        progressButtonView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
    }
    private func loadImage() {
        imageView.loadImage(withURLString: content.url, actionsBeforeNetworkLoad: nil) { [weak self] in self?.layoutImage() }
    }
    private func layoutImage() {
        let image: UIImage?
        if let animated = imageView.animatedImage { image = animated.coverImage }
        else {
            guard let current = imageView.image else { layoutFailureImage(); return }
            image = current
        }
        resizeIfNeeded(with: image?.size ?? .zero)
        loadingView.isShimmering = false
        imageView.backgroundColor = .clear
    }
    private func resizeIfNeeded(with size: CGSize) {
        if size.width == 0 || size.height == 0 { return }
        if content.width == size.width && content.height == size.height { return }
        loadingViewWidthConstraint?.constant = size.width
        loadingViewAspectRatioConstraint?.isActive = false
        let ratio = size.height > 0 && size.width > 0 ? size.height / size.width : 1
        let height = loadingView.heightAnchor.constraint(equalTo: loadingView.widthAnchor, multiplier: ratio)
        loadingViewAspectRatioConstraint = height
        height.isActive = true
        invalidateIntrinsicContentSize()
        delegate?.invalidateLayoutFor(block: content)
    }
    private func layoutFailureImage() {
        addSubview(failureImageView)
        failureImageView.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        failureImageView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        failureImageView.widthAnchor.constraint(equalToConstant: 32).isActive = true
        failureImageView.heightAnchor.constraint(equalToConstant: 32).isActive = true
    }
    func uploadProgressUpdated(_ progress: CGFloat) {
        progressButtonView.progress = progress
        if progress >= 1 { hideProgressButtonView() }
    }
    func uploadFailed() { hideProgressButtonView() }
    private func hideProgressButtonView() {
        UIView.animate(withDuration: 0.2, delay: 0.2, options: [], animations: { self.progressButtonView.alpha = 0 }, completion: nil)
    }
    @objc private func progressButtonViewTapped() { content.upload!.cancelled = true }
    @objc private func imageTapped(_ sender: Any) {
        if let string = content.linkUrl, let url = URL(string: string) {
            if let delegate = delegate { delegate.open(url: url) }
            else { LinkOpener.shared.openLink(url) }
            return
        }
        if let string = content.url, let url = URL(string: string), url.isFileURL {
            delegate?.imageTapped(with: content)
            return
        }
        if imageView.animatedImage != nil { delegate?.imageTapped(with: content); return }
        if let image = imageView.image, let delegate = delegate {
            let title: String?
            if let upload = content.upload { title = upload.filename ?? "" }
            else { title = nil }
            delegate.imageTapped(with: image, title: title)
        }
    }
}
