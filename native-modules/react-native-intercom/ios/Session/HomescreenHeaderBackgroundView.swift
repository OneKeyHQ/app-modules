import UIKit

@objc(_TtC18IntercomSDKPrivate30HomescreenHeaderBackgroundView)
class HomescreenHeaderBackgroundView: UIView {
    private var state: HomescreenHeaderViewState?
    private var imageLoader: ImageLoader
    private lazy var imageView: ITBAnimatedImageView = {
        let image = ITBAnimatedImageView()
        image.translatesAutoresizingMaskIntoConstraints = false
        image.contentMode = .scaleAspectFill
        return image
    }()
    private lazy var fadeView: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.backgroundColor = .clear
        return view
    }()
    private let fadeGradientLayer = CAGradientLayer()
    private var fadeViewHeightConstraint: NSLayoutConstraint?
    private var colorsGradientLayer = CAGradientLayer()
    private let fallbackColor = UIColor.clear
    private var firstConfiguration = true
    init(imageLoader: ImageLoader) {
        self.imageLoader = imageLoader
        super.init(frame: .zero)
        backgroundColor = fallbackColor
        setupView()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupView() {
        addSubview(imageView)
        NSLayoutConstraint.activate([
            imageView.leadingAnchor.constraint(equalTo: leadingAnchor),
            imageView.trailingAnchor.constraint(equalTo: trailingAnchor),
            imageView.topAnchor.constraint(equalTo: topAnchor),
            imageView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        addSubview(fadeView)
        NSLayoutConstraint.activate([
            fadeView.leadingAnchor.constraint(equalTo: leadingAnchor),
            fadeView.trailingAnchor.constraint(equalTo: trailingAnchor),
            fadeView.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        let base = DefaultThemeProvider.shared.theme.base
        fadeGradientLayer.colors = [base.withAlphaComponent(0).cgColor, base.withAlphaComponent(1).cgColor]
        fadeView.layer.addSublayer(fadeGradientLayer)
        fadeViewHeightConstraint = fadeView.heightAnchor.constraint(equalToConstant: 0)
        fadeViewHeightConstraint?.isActive = true
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        fadeGradientLayer.frame = fadeView.bounds
        colorsGradientLayer.frame = bounds
    }
    func configure(for state: HomescreenHeaderViewState) {
        self.state = state
        switch state {
        case .expandedHeader(let header): configureExpandedHeader(header.background)
        case .waitingForImage(let header, let background, _):
            imageView.image = nil
            colorsGradientLayer.removeFromSuperlayer()
            backgroundColor = background
            fadeViewHeightConstraint?.constant = header.background.fadeToWhite ? (header.background.type == .image ? 80 : 160) : 0
        case .miniHeader(_, let background, _):
            imageView.image = nil
            colorsGradientLayer.removeFromSuperlayer()
            backgroundColor = background
            fadeViewHeightConstraint?.constant = 0
        case .noHeader:
            imageView.image = nil
            colorsGradientLayer.removeFromSuperlayer()
            backgroundColor = fallbackColor
            fadeViewHeightConstraint?.constant = 0
        }
        firstConfiguration = false
    }
    private func configureExpandedHeader(_ background: HomeHeaderBackground) {
        let traits = traitCollection
        let type: HomeHeaderBackgroundType
        if let dark = background.typeDark, traits.userInterfaceStyle == .dark { type = dark }
        else { type = background.type }
        let fade = traitCollection.userInterfaceStyle == .dark ? background.fadeToDark : background.fadeToWhite
        switch type {
        case .solid:
            let color = background.dynamicBackground
            imageView.image = nil
            colorsGradientLayer.removeFromSuperlayer()
            backgroundColor = color
        case .colors:
            let colors: [UIColor]?
            if traitCollection.userInterfaceStyle == .dark, let dark = background.gradientDark { colors = dark }
            else { colors = background.gradient }
            if let colors { configureColorsBackground(colors) }
            else {
                let color = background.dynamicBackground
                imageView.image = nil
                colorsGradientLayer.removeFromSuperlayer()
                backgroundColor = color
            }
        case .image: configureExpandedImage(background)
        }
        fadeViewHeightConstraint?.constant = fade ? (type == .image ? 80 : 160) : 0
    }
    private func configureColorsBackground(_ colors: [UIColor]) {
        backgroundColor = DefaultThemeProvider.shared.theme.base
        imageView.image = nil
        colorsGradientLayer.startPoint = CGPoint(x: 0, y: 0)
        colorsGradientLayer.endPoint = CGPoint(x: 1, y: 1)
        colorsGradientLayer.colors = colors.map(\.cgColor)
        colorsGradientLayer.frame = bounds
        layer.insertSublayer(colorsGradientLayer, at: 0)
    }
    private func configureExpandedImage(_ background: HomeHeaderBackground) {
        let url: URL?
        if traitCollection.userInterfaceStyle == .dark, let dark = background.imageDarkUrl { url = dark }
        else { url = background.imageUrl }
        guard let url else {
            let color = background.dynamicBackground
            imageView.image = nil
            colorsGradientLayer.removeFromSuperlayer()
            backgroundColor = color
            return
        }
        let value = imageLoader.imageLoader.cachedImage(for: url)
        if let image = ImageLoaderCachedImage(value) {
            let color = background.dynamicBackground
            configureImageBackground(image, backgroundColor: color)
            return
        }
        let color = background.dynamicBackground
        imageView.image = nil
        colorsGradientLayer.removeFromSuperlayer()
        backgroundColor = color
        let loader = ITBImageLoader.longTerm()!
        let identifier = loader.loadImage(withURLString: url.absoluteString) { [weak self] object in
            DispatchQueue.main.async {
                guard let self else { return }
                guard let image = ImageLoaderCachedImage(object) else { return }
                let color = background.dynamicBackground
                self.configureImageBackground(image, backgroundColor: color)
            }
        }
        if let identifier { let _: UUID = identifier }
    }
    private func configureImageBackground(_ image: ImageLoaderCachedImage, backgroundColor: UIColor) {
        colorsGradientLayer.removeFromSuperlayer()
        self.backgroundColor = backgroundColor
        if !firstConfiguration && imageView.image == nil { imageView.alpha = 0 }
        let view = imageView
        switch image {
        case .animatedImage(let image): view.animatedImage = image
        case .image(let image): view.image = image
        }
        UIView.animate(withDuration: 0.5) { self.imageView.alpha = 1 }
    }
    override func traitCollectionDidChange(_ previous: UITraitCollection?) {
        super.traitCollectionDidChange(previous)
        let oldStyle = previous?.userInterfaceStyle
        let currentStyle = traitCollection.userInterfaceStyle
        if oldStyle != nil && oldStyle == currentStyle { return }
        let base = DefaultThemeProvider.shared.theme.base
        fadeGradientLayer.colors = [base.withAlphaComponent(0).cgColor, base.withAlphaComponent(1).cgColor]
        if case .expandedHeader(let header) = state { configureExpandedHeader(header.background) }
    }
}
