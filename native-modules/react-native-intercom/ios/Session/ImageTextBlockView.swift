import UIKit

@objc(_TtC18IntercomSDKPrivate18ImageTextBlockView)
class ImageTextBlockView: UIView, ConversationCellContentView {
    let content: ICMImageTextBlock
    private let themeProvider: ThemeProvider
    private let imageLoader: ITBImageLoader
    private lazy var imageView: ITBAnimatedImageView = {
        let view = ITBAnimatedImageView(imageLoader: imageLoader) ?? ITBAnimatedImageView()
        view.contentMode = .scaleAspectFit
        view.clipsToBounds = true
        view.translatesAutoresizingMaskIntoConstraints = false
        view.accessibilityElementsHidden = true
        return view
    }()
    private lazy var titleLabel = makeLabel(weight: .semibold)
    private lazy var textLabel = makeLabel(weight: .regular)
    private lazy var labelsStack: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 4
        return stack
    }()
    private lazy var contentStack: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.spacing = 16
        stack.alignment = .top
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
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
    private lazy var failureImageView: UIImageView = {
        let bundle = ITBBlockUtils.loadBlocksAssetsBundle()
        let image = UIImage(named: "image-loading-fail", in: bundle, compatibleWith: nil)?.withRenderingMode(.alwaysOriginal)
        let view = UIImageView(image: image)
        view.translatesAutoresizingMaskIntoConstraints = false
        view.accessibilityElementsHidden = true
        return view
    }()

    init?(content: ITBBlock, imageLoader: ITBImageLoader, themeProvider: ThemeProvider) {
        guard let content = content as? ICMImageTextBlock else { return nil }
        self.content = content
        self.imageLoader = imageLoader
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        setup()
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    private func makeLabel(weight: UIFont.Weight) -> UILabel {
        let label = UILabel()
        label.numberOfLines = 0
        label.backgroundColor = .clear
        label.font = .systemFont(ofSize: 17, weight: weight)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }
    private func setup() {
        setupViewHierarchy()
        setupConstraints()
        isAccessibilityElement = true
        accessibilityIdentifier = "intercom image text block view"
        configureLabels(content)
        loadingView.shimmeringSpeed = content.width * 0.6
        imageView.backgroundColor = .clear
        loadImage(from: content.url ?? "")
    }
    private func setupViewHierarchy() {
        if !(content.title ?? "").isEmpty { labelsStack.addArrangedSubview(titleLabel) }
        labelsStack.addArrangedSubview(textLabel)
        contentStack.addArrangedSubview(imageView)
        contentStack.addArrangedSubview(labelsStack)
        addSubview(contentStack)
        addSubview(loadingView)
    }
    private func configureLabels(_ content: ICMImageTextBlock) {
        textLabel.text = content.text ?? ""
        textLabel.textColor = content.textColor
        if !(content.title ?? "").isEmpty {
            titleLabel.text = content.title ?? ""
            titleLabel.textColor = content.titleTextColor
            accessibilityLabel = (content.title ?? "") + ", " + (content.text ?? "")
        } else {
            accessibilityLabel = content.text ?? ""
        }
    }
    private func setupConstraints() {
        NSLayoutConstraint.activate([
            contentStack.leadingAnchor.constraint(equalTo: leadingAnchor),
            contentStack.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -24),
            contentStack.topAnchor.constraint(equalTo: topAnchor),
            contentStack.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        let image = imageView
        let width = content.width
        let height = content.height
        image.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            image.widthAnchor.constraint(equalToConstant: width),
            image.heightAnchor.constraint(equalToConstant: height)
        ])
        loadingView.centerXAnchor.constraint(equalTo: imageView.centerXAnchor).isActive = true
        loadingView.centerYAnchor.constraint(equalTo: imageView.centerYAnchor).isActive = true
        loadingView.widthAnchor.constraint(equalTo: imageView.widthAnchor).isActive = true
        loadingView.heightAnchor.constraint(equalTo: imageView.heightAnchor).isActive = true
    }
    private func layoutFailureImage() {
        let image = failureImageView
        if image.superview == nil { addSubview(image) }
        image.centerXAnchor.constraint(equalTo: loadingView.centerXAnchor).isActive = true
        image.centerYAnchor.constraint(equalTo: loadingView.centerYAnchor).isActive = true
        image.widthAnchor.constraint(equalToConstant: 32).isActive = true
        image.heightAnchor.constraint(equalToConstant: 32).isActive = true
    }
    private func loadImage(from url: String) {
        imageView.loadImage(withURLString: url, actionsBeforeNetworkLoad: { [weak self] in
            self?.prepareForImageLoading()
        }, completion: { [weak self] in
            self?.handleImageLoadingCompletion()
        })
    }
    private func prepareForImageLoading() {
        imageView.alpha = 0
        loadingView.isShimmering = true
    }
    private func handleImageLoadingCompletion() {
        imageView.backgroundColor = .clear
        if imageView.image != nil || imageView.animatedImage != nil {
            UIView.animate(withDuration: 0.3, delay: 0, options: [], animations: {
                self.loadingView.isShimmering = false
                self.imageView.alpha = 1
            }, completion: nil)
        } else {
            loadingView.isShimmering = false
            layoutFailureImage()
        }
    }
}
