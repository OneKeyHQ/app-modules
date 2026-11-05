import UIKit

@objc(_TtC18IntercomSDKPrivate18VideoFileBlockView)
class VideoFileBlockView: UIView, ConversationCellContentView {
    let content: ITBVideoFileBlock
    private let themeProvider: any ThemeProvider
    private let videoFileProcessor: any VideoFileProcessorProtocol
    private let notificationCenter: any NotificationCenterInterface
    private weak var conversationCellBlockDelegate: (any ConversationCellBlockDelegate)?
    private lazy var imageView: ITBAnimatedImageView = {
        let view = ITBAnimatedImageView()
        view.contentMode = .scaleAspectFill
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = themeProvider.theme.base.withAlphaComponent(0.4)
        view.clipsToBounds = true
        return view
    }()
    private lazy var playButton: VideoPlayButton = {
        let button = VideoPlayButton(symbolPointSize: 16, inset: 20, onTap: self.playVideo)
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
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
    init?(content: ITBBlock, notificationCenter: any NotificationCenterInterface, delegate: (any ConversationCellBlockDelegate)?, videoFileProcessor: any VideoFileProcessorProtocol, themeProvider: any ThemeProvider) {
        guard let content = content as? ITBVideoFileBlock else { return nil }
        self.content = content
        self.notificationCenter = notificationCenter
        self.conversationCellBlockDelegate = delegate
        self.videoFileProcessor = videoFileProcessor
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        configureImageView()
        configureLoadingView()
        notificationCenter.addObserver(self, selector: #selector(orientationChanged), name: UIDevice.orientationDidChangeNotification, object: nil)
        configurePlayButton()
        configureThumbnail()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    deinit {}

    private func configureImageView() {
        addSubview(imageView)
        NSLayoutConstraint.activate([
            imageView.topAnchor.constraint(equalTo: topAnchor),
            imageView.leadingAnchor.constraint(equalTo: leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: trailingAnchor),
            imageView.bottomAnchor.constraint(equalTo: bottomAnchor),
        ])
        let size: CGSize
        if content.width == 0 { size = CGSize(width: 268, height: 151) }
        else { size = CGSize(width: content.width, height: content.height) }
        let a = 268 / size.width
        let b = 391 / size.height
        let factor = b < a ? b : a
        let scaled = size.width == 0 || size.height == 0 ? size : CGSize(width: size.width * factor, height: size.height * factor)
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.widthAnchor.constraint(equalToConstant: scaled.width).isActive = true
        imageView.translatesAutoresizingMaskIntoConstraints = false
        imageView.heightAnchor.constraint(equalToConstant: scaled.height).isActive = true
    }
    private func configureLoadingView() {
        addSubview(loadingView)
        NSLayoutConstraint.activate([
            loadingView.topAnchor.constraint(equalTo: topAnchor),
            loadingView.leadingAnchor.constraint(equalTo: leadingAnchor),
            loadingView.trailingAnchor.constraint(equalTo: trailingAnchor),
            loadingView.bottomAnchor.constraint(equalTo: bottomAnchor),
        ])
        loadingView.contentView = imageView
        loadingView.isShimmering = true
    }
    private func configurePlayButton() {
        addSubview(playButton)
        NSLayoutConstraint.activate([
            playButton.widthAnchor.constraint(equalToConstant: 64),
            playButton.heightAnchor.constraint(equalToConstant: 64),
            playButton.centerXAnchor.constraint(equalTo: centerXAnchor),
            playButton.centerYAnchor.constraint(equalTo: centerYAnchor),
        ])
        playButton.tintColor = themeProvider.theme.primaryForeground
    }
    private func configureThumbnail() {
        if let url = content.thumbnailUrl {
            imageView.loadImage(withURLString: url, actionsBeforeNetworkLoad: nil) { [weak self] in self?.loadingView.isShimmering = false }
        } else { generateThumbnail() }
    }
    private func generateThumbnail() {
        let string = content.url ?? ""
        guard let url = URL(string: string) else { return }
        videoFileProcessor.generateThumbnail(forVideoAtURL: url) { [weak self] result in
            DispatchQueue.main.async { [weak self] in
                if case .success(let image) = result { self?.imageView.image = image }
                self?.loadingView.isShimmering = false
            }
        }
    }
    @objc private func orientationChanged() {
        content.estimatedSize = .zero
        conversationCellBlockDelegate?.reload(content: content)
    }
    @objc private func playVideo() { conversationCellBlockDelegate?.playVideo(file: content) }
}
