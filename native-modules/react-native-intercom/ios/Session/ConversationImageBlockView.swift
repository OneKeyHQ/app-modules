import UIKit
import Darwin

@objc(_TtC18IntercomSDKPrivate26ConversationImageBlockView)
class ConversationImageBlockView: UIView, ConversationCellContentView, ITBUploadProgressDelegate {
    let content: ITBImageBlock
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private let imageLoader: ITBImageLoader
    private let groupingPosition: Int
    private let bubbleType: BubbleType
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
        view.layer.borderWidth = 1
        view.layer.borderColor = themeProvider.theme.neutralBorder.cgColor
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
    private var loadingViewHeightConstraint: NSLayoutConstraint?
    private static let darkLoadingViewColor = UIColor(red: 0.4, green: 0.4, blue: 0.4, alpha: 0.15)
    private static let lightLoadingViewColor = UIColor(red: 1, green: 1, blue: 1, alpha: 0.15)

    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?, imageLoader: ITBImageLoader, imageView: ITBAnimatedImageView?, groupingPosition: Int, bubbleType: BubbleType, themeProvider: any ThemeProvider) {
        guard let content = content as? ITBImageBlock else { return nil }
        self.content = content
        self.delegate = delegate
        self.imageLoader = imageLoader
        self.groupingPosition = groupingPosition
        self.bubbleType = bubbleType
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
        view.layer.borderWidth = 1
        view.layer.borderColor = themeProvider.theme.neutralBorder.cgColor
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
        loadingView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -content.padding).isActive = true
        let originalWidth = content.width
        let originalHeight = content.height
        var width: CGFloat = 268
        var height: CGFloat = 268
        if originalWidth != 0 && originalHeight != 0 {
            if originalHeight < originalWidth {
                let ratio = originalHeight / originalWidth
                width = CGFloat(Darwin.fmin(Double(originalWidth), 268))
                height = width * ratio
            } else {
                let ratio = originalWidth / originalHeight
                height = CGFloat(Darwin.fmin(Double(originalHeight), 268))
                width = ratio * height
            }
        }
        if loadingView.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            loadingView.leadingAnchor.constraint(equalTo: rightAnchor, constant: -CGFloat.zero).isActive = true
        } else { loadingView.leadingAnchor.constraint(equalTo: leftAnchor).isActive = true }
        let fixedWidth = loadingView.widthAnchor.constraint(equalToConstant: width)
        fixedWidth.isActive = true
        loadingViewWidthConstraint = fixedWidth
        let fixedHeight = loadingView.heightAnchor.constraint(equalToConstant: height)
        fixedHeight.isActive = true
        loadingViewHeightConstraint = fixedHeight
        loadingView.shimmeringSpeed = width * 0.6
        loadingView.isShimmering = true
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        let radius = BubbleRadius(large: 16, small: 4)
        let supports = UIView.supportsCornerConfiguration
        loadingView.applyCornerRadiusForGrouping(groupingPosition, bubbleType: bubbleType, bubbleRadius: radius, supportsCornerConfiguration: supports)
        imageView.applyCornerRadiusForGrouping(groupingPosition, bubbleType: bubbleType, bubbleRadius: radius, supportsCornerConfiguration: supports)
        loadingView.applyBorderToRoundedCorners(borderColor: themeProvider.theme.neutralBorder)
        imageView.applyBorderToRoundedCorners(borderColor: themeProvider.theme.neutralBorder)
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
    }
    private func resizeIfNeeded(with size: CGSize) {
        if size.width == 0 || size.height == 0 { return }
        if content.width == size.width && content.height == size.height { return }
        let portraitHeight = CGFloat(Darwin.fmin(Double(size.height), 268))
        let portraitWidth = (size.width / size.height) * portraitHeight
        let landscapeWidth = CGFloat(Darwin.fmin(Double(size.width), 268))
        let landscapeHeight = landscapeWidth * (size.height / size.width)
        let portrait = !(size.height < size.width)
        loadingViewWidthConstraint?.constant = portrait ? portraitWidth : landscapeWidth
        loadingViewHeightConstraint?.constant = portrait ? portraitHeight : landscapeHeight
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
            LinkOpener.shared.openLink(url)
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
