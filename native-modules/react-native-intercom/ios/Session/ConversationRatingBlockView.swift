import UIKit

@objc(_TtC18IntercomSDKPrivate27ConversationRatingBlockView)
class ConversationRatingBlockView: UIView, ConversationCellContentView {
    enum ViewState { case ratingRequired, remarkRequired, feedbackComplete, ratingExpired }
    let content: ICMConversationRatingBlock
    private let conversationPart: ICMConversationPart
    private weak var delegate: ConversationCellBlockDelegate?
    private var ratingButtonViews: [ITBAnimatedImageView] = []
    private var currentViewState: ViewState?
    private let themeProvider: ThemeProvider
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.distribution = .fill
        stack.alignment = .center
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var textView = HyperLinkTextView()
    private lazy var ratingButtonsStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.distribution = .fill
        stack.spacing = 18
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var tellUsMoreButton: UIButton = {
        let button = UIButton(type: .custom)
        button.addTarget(self, action: #selector(tellUsMoreButtonTapped), for: .touchUpInside)
        button.layer.cornerRadius = 4
        button.backgroundColor = themeProvider.theme.primaryBackground
        let font = UIFont.preferredFont(forTextStyle: .footnote)
        let background = themeProvider.theme.primaryBackground
        let color = ColorHelper.suitableForegroundColor(for: background, theme: themeProvider.theme)
        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        button.setAttributedTitle(NSAttributedString(string: translate("tell_us_more"), attributes: [.font: font, .foregroundColor: color, .paragraphStyle: paragraph]), for: .normal)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.heightAnchor.constraint(greaterThanOrEqualToConstant: 44).isActive = true
        return button
    }()
    init?(content: ITBBlock, conversationPart: ICMConversationPart, delegate: ConversationCellBlockDelegate?, themeProvider: ThemeProvider) {
        guard let content = content as? ICMConversationRatingBlock else { return nil }
        self.content = content
        self.conversationPart = conversationPart
        self.delegate = delegate
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configureContainerStackView()
        configureCurrentViewState()
        configureRatingButtons()
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func translate(_ key: String) -> String { TranslationManager.messenger.translation(forKey: key) }
    private func configureContainerStackView() {
        addSubview(containerStackView)
        containerStackView.addArrangedSubview(textView)
        containerStackView.addArrangedSubview(ratingButtonsStackView)
        containerStackView.addArrangedSubview(tellUsMoreButton)
        containerStackView.setCustomSpacing(8, after: ratingButtonsStackView)
        NSLayoutConstraint.activate([
            containerStackView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 8),
            containerStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -8),
            containerStackView.topAnchor.constraint(equalTo: topAnchor, constant: 6),
            tellUsMoreButton.leadingAnchor.constraint(equalTo: containerStackView.leadingAnchor, constant: 24),
            tellUsMoreButton.trailingAnchor.constraint(equalTo: containerStackView.trailingAnchor, constant: -24)
        ])
        let bottom = containerStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -14)
        bottom.priority = UILayoutPriority(999)
        bottom.isActive = true
    }
    private func configureCurrentViewState() {
        if content.blockRating && content.selectedRatingIndex == nil { currentViewState = .ratingExpired }
        else if content.selectedRatingIndex == nil { currentViewState = .ratingRequired }
        else if content.remark != nil { currentViewState = .feedbackComplete }
        else if content.blockRating { currentViewState = .feedbackComplete }
        else { currentViewState = .remarkRequired }
        updateViewForCurrentViewState()
    }
    private func centeredCalloutString(forKey key: String, color: UIColor) -> NSAttributedString {
        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        return NSAttributedString(string: translate(key), attributes: [.font: UIFont.preferredFont(forTextStyle: .callout), .foregroundColor: color, .paragraphStyle: paragraph])
    }
    private func updateViewForCurrentViewState() {
        guard let state = currentViewState else { return }
        switch state {
        case .ratingRequired:
            textView.attributedText = centeredCalloutString(forKey: "rate_your_conversation", color: themeProvider.theme.textDefault)
            ratingButtonsStackView.isHidden = false
            tellUsMoreButton.isHidden = true
        case .remarkRequired:
            textView.attributedText = centeredCalloutString(forKey: "rate_your_conversation", color: themeProvider.theme.textDefault)
            ratingButtonsStackView.isHidden = false
            tellUsMoreButton.isHidden = false
        case .feedbackComplete:
            textView.attributedText = configureFeedbackCompleteString()
            ratingButtonsStackView.isHidden = true
            tellUsMoreButton.isHidden = true
        case .ratingExpired:
            textView.attributedText = centeredCalloutString(forKey: "rating_window_closed", color: themeProvider.theme.textMuted)
            ratingButtonsStackView.isHidden = true
            tellUsMoreButton.isHidden = true
        }
    }
    private func configureRatingButtons() {
        guard let options = content.ratingOptions else { return }
        ratingButtonViews = options.compactMap { [weak self] option in
            guard let view = ITBAnimatedImageView(imageLoader: ITBImageLoader.longTerm()) else { return nil }
            view.layer.shouldRasterize = true
            view.layer.rasterizationScale = self?.icmDisplayScale ?? 1
            view.contentMode = .scaleAspectFit
            view.accessibilityHint = option.emoji ?? ""
            view.isUserInteractionEnabled = true
            view.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ConversationRatingBlockView.ratingButtonTapped(_:))))
            view.translatesAutoresizingMaskIntoConstraints = false
            view.isAccessibilityElement = true
            view.accessibilityLabel = option.emoji ?? ""
            view.accessibilityTraits = .button
            view.widthAnchor.constraint(equalToConstant: 31).isActive = true
            view.heightAnchor.constraint(equalToConstant: 31).isActive = true
            return view
        }
        for view in ratingButtonViews { ratingButtonsStackView.addArrangedSubview(view) }
        updateRatingButtons()
    }
    private func updateRatingButtons() {
        guard let options = content.ratingOptions else { return }
        for (offset, view) in ratingButtonViews.enumerated() {
            let option = options[offset]
            guard let index = option.index as? Int else { continue }
            view.tag = index
            view.loadImage(withURLString: option.imageUrl ?? "", actionsBeforeNetworkLoad: nil, completion: { [weak self] in
                let selected = self?.content.selectedRatingIndex
                if selected == nil || (index >= 0 && selected!.uintValue == UInt(index)) {
                    view.alpha = 1
                    view.transform = CGAffineTransform.identity.scaledBy(x: 1, y: 1)
                    view.accessibilityLabel = (options[offset].emoji ?? "") + " selected"
                } else {
                    view.image = self?.greyscaleEmoji(for: option, in: view)
                    view.alpha = 0.9
                    view.transform = CGAffineTransform.identity.scaledBy(x: 0.85, y: 0.85)
                    view.accessibilityLabel = (options[offset].emoji ?? "") + " unselected"
                }
            })
        }
    }
    private func greyscaleEmoji(for option: ICMConversationRatingOption, in view: ITBAnimatedImageView) -> UIImage? {
        let loader = ITBImageLoader.default()!
        let key = (option.imageUrl ?? "") + "-grayscale"
        if let image = loader.cachedImage(forKey: key) as? UIImage { return image }
        let image = ITBImageUtils.grayscaleImage(view.image)
        let currentLoader = ITBImageLoader.default()!
        currentLoader.cacheImage(image, forKey: (option.imageUrl ?? "") + "-grayscale")
        return image
    }
    private func configureFeedbackCompleteString() -> NSAttributedString? {
        guard let selected = content.selectedRatingIndex else { return nil }
        let index = selected.intValue
        guard let options = content.ratingOptions,
              let option = options.first(where: { ($0.index as? Int) == index }) else { return nil }
        let emoji = option.emoji ?? ""
        let first = translate("you_rated_the_conversation")
        let second = translate("thanks_for_letting_us_know")
        let text = NSMutableAttributedString(string: first + " " + emoji, attributes: [.font: UIFont.preferredFont(forTextStyle: .callout), .foregroundColor: themeProvider.theme.textDefault])
        text.append(NSAttributedString(string: "\n" + second, attributes: [.font: UIFont.preferredFont(forTextStyle: .footnote), .foregroundColor: themeProvider.theme.textMuted]))
        let paragraph = NSMutableParagraphStyle()
        paragraph.alignment = .center
        paragraph.lineSpacing = 5
        text.addAttribute(.paragraphStyle, value: paragraph, range: NSRange(location: 0, length: text.string.utf16.count))
        return text
    }
    @objc private func ratingButtonTapped(_ gesture: UITapGestureRecognizer) {
        guard let view = gesture.view else { return }
        if content.blockRating { return }
        guard let delegate else { return }
        delegate.rateConversation(UInt(view.tag), conversationPart: conversationPart)
    }
    @objc private func tellUsMoreButtonTapped() {
        let capturedDelegate = delegate
        let part = conversationPart
        let alert = UIAlertController(title: translate("tell_us_more"), message: nil, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: translate("cancel"), style: .cancel, handler: nil))
        alert.addTextField { field in
            field.autocorrectionType = .yes
            field.autocapitalizationType = .words
        }
        alert.addAction(UIAlertAction(title: translate("ok"), style: .default) { _ in
            if let field = alert.textFields?.first, let text = field.text, !text.isEmpty {
                capturedDelegate?.addRemarkToConversation(text, conversationPart: part)
            }
        })
        delegate?.presentAlert(alert)
    }
    override func didMoveToWindow() {
        super.didMoveToWindow()
        if !(traitCollection.displayScale <= 0) { updateRasterizationScale() }
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        if traitCollection.displayScale <= 0 { return }
        if previousTraitCollection == nil || previousTraitCollection!.displayScale != traitCollection.displayScale {
            updateRasterizationScale()
        }
    }
    private func updateRasterizationScale() {
        let scale = icmDisplayScale
        for view in ratingButtonViews where view.layer.rasterizationScale != scale {
            view.layer.rasterizationScale = scale
        }
    }
}
