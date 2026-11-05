import UIKit

@objc(_TtC18IntercomSDKPrivate30HomescreenHeaderViewController)
class HomescreenHeaderViewController: UIViewController {
    weak var delegate: (any HomescreenHeaderViewDelegate)?
    var closeButtonOverlap: CGFloat = 0 {
        didSet {
            let inset = closeButtonOverlap + 8
            closeButtonInset = inset >= 0 ? inset : 0
            closeButtonInsetConstraint?.constant = -closeButtonInset
        }
    }
    var state: HomescreenHeaderViewState?
    private var viewModel: HomescreenHeaderViewModel?
    private var imageLoader: ImageLoader
    private let themeProvider: any ThemeProvider
    private lazy var textElementsStackView: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = .vertical
        stack.alignment = .leading
        stack.spacing = 2
        stack.distribution = .fill
        return stack
    }()
    private lazy var graphicElementsStackView: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = .horizontal
        stack.alignment = .center
        stack.distribution = .fill
        stack.spacing = 4
        return stack
    }()
    private lazy var loadingElementsStackView: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = .vertical
        stack.alignment = .leading
        stack.spacing = 10
        stack.distribution = .fill
        let first = createLabelShimmer()
        NSLayoutConstraint.activate([first.heightAnchor.constraint(equalToConstant: 32), first.widthAnchor.constraint(equalToConstant: 100)])
        let second = createLabelShimmer()
        NSLayoutConstraint.activate([second.heightAnchor.constraint(equalToConstant: 32), second.widthAnchor.constraint(equalToConstant: 150)])
        stack.addArrangedSubview(first)
        stack.addArrangedSubview(second)
        return stack
    }()
    private lazy var logoImageView: ITBAnimatedImageView = {
        let image = ITBAnimatedImageView()
        image.translatesAutoresizingMaskIntoConstraints = false
        image.contentMode = .scaleAspectFit
        image.setContentCompressionResistancePriority(.init(250), for: .horizontal)
        return image
    }()
    lazy var avatarsView: ICMActiveAdminsAvatarView = {
        let avatars = ICMActiveAdminsAvatarView(height: 40, overlap: 3.2, maxCount: 3)!
        avatars.translatesAutoresizingMaskIntoConstraints = false
        avatars.updateWhenTeamPresenceUpdated = true
        avatars.alpha = 0
        return avatars
    }()
    private lazy var teamGreetingLabel = makeMultilineLabel()
    private lazy var teamIntroLabel = makeMultilineLabel()
    private lazy var miniHeaderLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .body).pointSize, weight: .semibold)
        label.numberOfLines = 3
        label.adjustsFontSizeToFitWidth = true
        label.minimumScaleFactor = 0.7
        label.lineBreakMode = .byTruncatingTail
        return label
    }()
    private var logoConstraintRatio: NSLayoutConstraint?
    private var noHeaderHeightConstraint: NSLayoutConstraint?
    private var closeButtonInsetConstraint: NSLayoutConstraint?
    private var closeButtonInset: CGFloat = 8
    init(delegate: (any HomescreenHeaderViewDelegate)?, viewModelInitializer: ((HomescreenHeaderViewController) -> HomescreenHeaderViewModel)?, imageLoader: ImageLoader, themeProvider: any ThemeProvider) {
        self.delegate = delegate
        self.imageLoader = imageLoader
        self.themeProvider = themeProvider
        super.init(nibName: nil, bundle: nil)
        if let viewModelInitializer { viewModel = viewModelInitializer(self) }
        else {
            let config = ICMConfig.shared()!
            let center = NotificationCenter.default
            let interactor = HomescreenHeaderInteractor(config: config, notificationCenter: center)
            let loader = ImageLoader(imageLoader: ITBImageLoader.longTerm()!)
            let nextConfig = ICMConfig.shared()!
            viewModel = HomescreenHeaderViewModel(view: self, interactor: interactor, imageLoader: loader, config: nextConfig)
        }
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    private func makeMultilineLabel() -> UILabel {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        return label
    }
    private func createLabelShimmer() -> ShimmeringView {
        let content = UIView()
        content.translatesAutoresizingMaskIntoConstraints = false
        content.backgroundColor = themeProvider.theme.neutralBorder
        let shimmer = ShimmeringView(frame: .zero)
        shimmer.translatesAutoresizingMaskIntoConstraints = false
        shimmer.contentView = content
        shimmer.layer.cornerRadius = 12
        shimmer.layer.masksToBounds = true
        shimmer.isShimmering = true
        return shimmer
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.backButtonDisplayMode = .minimal
        setupView()
        viewModel!.viewDidLoad()
        NotificationCenter.default.addObserver(self, selector: #selector(preferredContentSizeChanged(_:)), name: UIContentSizeCategory.didChangeNotification, object: nil)
        ICMUtils.enableRTLSupport(forContainerClass: type(of: self))
    }
    private func setupView() {
        let constraint = view!.heightAnchor.constraint(equalToConstant: 239)
        noHeaderHeightConstraint = constraint
        constraint.priority = .init(999)
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        let width = view!.bounds.width
        preferredContentSize = view!.systemLayoutSizeFitting(CGSize(width: width, height: UIView.layoutFittingCompressedSize.height))
    }
    private func font(for style: UIFont.TextStyle) -> UIFont {
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { return .systemFont(ofSize: 40, weight: .semibold) }
        return .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: style).pointSize, weight: .semibold)
    }
    private var largeTitleFont: UIFont { font(for: .largeTitle) }
    private var title1Font: UIFont { font(for: .title1) }
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        if let text = teamIntroLabel.text {
            let rect = (text as NSString).boundingRect(with: CGSize(width: textElementsStackView.frame.width, height: .greatestFiniteMagnitude), options: [.usesLineFragmentOrigin, .usesFontLeading], attributes: [.font: largeTitleFont], context: nil)
            _ = largeTitleFont.lineHeight
            _ = rect.height
        }
        if let text = teamGreetingLabel.text {
            let rect = (text as NSString).boundingRect(with: CGSize(width: textElementsStackView.frame.width, height: .greatestFiniteMagnitude), options: [.usesLineFragmentOrigin, .usesFontLeading], attributes: [.font: largeTitleFont], context: nil)
            _ = largeTitleFont.lineHeight
            _ = rect.height
        }
        teamGreetingLabel.font = title1Font
        teamIntroLabel.font = title1Font
    }
    @objc private func preferredContentSizeChanged(_ notification: Notification) { view!.layoutIfNeeded() }
    override func traitCollectionDidChange(_ previous: UITraitCollection?) {
        super.traitCollectionDidChange(previous)
        let old = previous?.userInterfaceStyle
        let current = traitCollection.userInterfaceStyle
        if old != nil && old == current { return }
        if case .expandedHeader(let header) = state { configureExpandedHeader(header, overrideForegroundColor: nil) }
    }
    private func removeCurrentElements() {
        graphicElementsStackView.removeAllArrangedSubviews()
        graphicElementsStackView.removeFromSuperview()
        textElementsStackView.removeAllArrangedSubviews()
        textElementsStackView.removeFromSuperview()
        miniHeaderLabel.removeFromSuperview()
        loadingElementsStackView.removeFromSuperview()
    }
    private func setupGraphicElements() {
        view!.addSubview(graphicElementsStackView)
        NSLayoutConstraint.activate([
            graphicElementsStackView.topAnchor.constraint(equalTo: view!.topAnchor, constant: 34),
            graphicElementsStackView.heightAnchor.constraint(equalToConstant: 40),
            logoImageView.heightAnchor.constraint(lessThanOrEqualToConstant: 32)
        ])
        let trailing = graphicElementsStackView.trailingAnchor.constraint(equalTo: view!.trailingAnchor, constant: -closeButtonInset)
        closeButtonInsetConstraint = trailing
        trailing.isActive = true
        let leading = graphicElementsStackView.leadingAnchor.constraint(equalTo: view!.leadingAnchor)
        leading.priority = .init(999)
        leading.isActive = true
    }
    private func setupTextElements() {
        textElementsStackView.addArrangedSubview(teamGreetingLabel)
        textElementsStackView.addArrangedSubview(teamIntroLabel)
        view!.addSubview(textElementsStackView)
        NSLayoutConstraint.activate([
            textElementsStackView.bottomAnchor.constraint(equalTo: view!.bottomAnchor, constant: -32),
            textElementsStackView.leadingAnchor.constraint(equalTo: view!.leadingAnchor),
            textElementsStackView.trailingAnchor.constraint(equalTo: view!.trailingAnchor)
        ])
        let top = textElementsStackView.topAnchor.constraint(equalTo: graphicElementsStackView.bottomAnchor, constant: 50)
        top.priority = .init(999)
        top.isActive = true
    }
    private func setupMiniHeader() {
        view!.addSubview(miniHeaderLabel)
        NSLayoutConstraint.activate([
            miniHeaderLabel.topAnchor.constraint(equalTo: view!.topAnchor, constant: 45),
            miniHeaderLabel.bottomAnchor.constraint(equalTo: view!.bottomAnchor, constant: -50)
        ])
        let trailing = miniHeaderLabel.trailingAnchor.constraint(equalTo: view!.trailingAnchor, constant: -closeButtonInset)
        closeButtonInsetConstraint = trailing
        trailing.isActive = true
        let leading = miniHeaderLabel.leadingAnchor.constraint(equalTo: view!.leadingAnchor)
        leading.priority = .init(999)
        leading.isActive = true
    }
    func configureForNoHeader() {
        removeCurrentElements()
        view!.addSubview(loadingElementsStackView)
        NSLayoutConstraint.activate([
            loadingElementsStackView.bottomAnchor.constraint(equalTo: view!.bottomAnchor, constant: -32),
            loadingElementsStackView.leadingAnchor.constraint(equalTo: view!.leadingAnchor, constant: -17),
            loadingElementsStackView.trailingAnchor.constraint(equalTo: view!.trailingAnchor, constant: 17)
        ])
        noHeaderHeightConstraint?.isActive = true
    }
    func configureMiniHeader(_ text: String?, backgroundColor: UIColor, foregroundColor: UIColor) {
        removeCurrentElements()
        setupMiniHeader()
        noHeaderHeightConstraint?.isActive = false
        miniHeaderLabel.text = text
        miniHeaderLabel.textColor = foregroundColor
    }
    func configureExpandedHeader(_ header: HomeHeader, overrideForegroundColor: UIColor?) {
        removeCurrentElements()
        setupGraphicElements()
        setupTextElements()
        noHeaderHeightConstraint?.isActive = false
        let content = header.content
        let model = viewModel!
        let url = model.logo(for: traitCollection.userInterfaceStyle)
        if let url {
            if let image = ImageLoaderCachedImage(imageLoader.imageLoader.cachedImage(for: url)) {
                switch image {
                case .animatedImage(let image): logoImageView.animatedImage = image
                case .image(let image): logoImageView.image = image
                }
                updateLogoConstraintRatio()
            }
            graphicElementsStackView.removeAllArrangedSubviews()
            graphicElementsStackView.addArrangedSubview(logoImageView)
            graphicElementsStackView.addArrangedSubview(UIView())
            graphicElementsStackView.addArrangedSubview(avatarsView)
        } else {
            logoImageView.image = nil
            for view in graphicElementsStackView.arrangedSubviews { graphicElementsStackView.removeArrangedSubview(view); view.removeFromSuperview() }
            graphicElementsStackView.removeAllArrangedSubviews()
            graphicElementsStackView.addArrangedSubview(avatarsView)
        }
        var accessibility = ""
        if let greeting = content.greeting {
            teamGreetingLabel.textColor = overrideForegroundColor ?? textColor(for: content)
            teamGreetingLabel.alpha = overrideForegroundColor != nil ? 1 : greeting.opacity
            teamGreetingLabel.text = greeting.content
            accessibility += greeting.content
        }
        if let intro = content.introduction {
            teamIntroLabel.textColor = overrideForegroundColor ?? textColor(for: content)
            teamIntroLabel.alpha = overrideForegroundColor != nil ? 1 : intro.opacity
            teamIntroLabel.text = intro.content
            if !accessibility.isEmpty { accessibility += ", " }
            accessibility += intro.content
        }
        view!.layoutIfNeeded()
        textElementsStackView.isAccessibilityElement = true
        textElementsStackView.accessibilityLabel = accessibility
    }
    private func textColor(for content: HomeHeaderContent) -> UIColor {
        let theme = themeProvider.theme
        let style = traitCollection.userInterfaceStyle
        let color: TextColorType
        if style == .dark, let dark = content.textColorTypeDark { color = dark }
        else { color = content.textColorType }
        return color == .light ? theme.textAlwaysDark : theme.textAlwaysLight
    }
    private func updateLogoConstraintRatio() {
        guard let first = logoImageView.image else { return }
        let width = first.size.width
        guard let second = logoImageView.image else { return }
        let height = second.size.height
        let ratio = width / height
        logoConstraintRatio?.isActive = false
        logoConstraintRatio = nil
        let constraint = logoImageView.widthAnchor.constraint(equalTo: logoImageView.heightAnchor, multiplier: ratio)
        logoConstraintRatio = constraint
        constraint.isActive = true
    }
    var accessibilityFocusTarget: UIView? {
        switch state {
        case .expandedHeader?, .waitingForImage?:
            if let label = textElementsStackView.accessibilityLabel, !label.isEmpty { return textElementsStackView }
        case .miniHeader?:
            if let text = miniHeaderLabel.text, !text.isEmpty { return miniHeaderLabel }
        case .noHeader?, nil: break
        }
        return nil
    }
    func configureAvatarViewVisibility(for content: HomeHeaderContent?) {
        if content?.showAvatars == true { UIView.animate(withDuration: 0.3) { self.avatarsView.alpha = 1 } }
    }

}
