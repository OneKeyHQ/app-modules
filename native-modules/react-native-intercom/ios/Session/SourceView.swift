import UIKit

@objc(_TtC18IntercomSDKPrivate30SourceViewTapGestureRecognizer)
class SourceViewTapGestureRecognizer: UITapGestureRecognizer {
    var source: FinAnswerSource?
    override init(target: Any?, action: Selector?) { super.init(target: target, action: action) }
}

@objc(_TtC18IntercomSDKPrivate10SourceView)
class SourceView: UIView {
    let themeProvider: DefaultThemeProvider
    let source: FinAnswerSource
    let index: Int
    let highlighted: Bool
    private lazy var indexLabel: UILabel = {
        let label = UILabel()
        label.text = String(index + 1)
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .caption2).pointSize, weight: .regular)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var imageView: UIImageView = {
        let image = UIImageView()
        image.translatesAutoresizingMaskIntoConstraints = false
        image.widthAnchor.constraint(equalToConstant: 8).isActive = true
        image.heightAnchor.constraint(equalToConstant: 8).isActive = true
        switch source.sourceType {
        case .article: image.image = ICMAssets.articleCitationIconImage()
        case .url: image.image = ICMAssets.linkIconImage()
        }
        return image
    }()
    private lazy var imageContainerView: UIView = {
        let container = UIView()
        container.translatesAutoresizingMaskIntoConstraints = false
        container.widthAnchor.constraint(equalToConstant: 16).isActive = true
        container.heightAnchor.constraint(equalToConstant: 16).isActive = true
        container.backgroundColor = highlighted ? themeProvider.theme.neutralContainerEmphasis : themeProvider.theme.neutralContainer
        container.layer.cornerRadius = 8
        container.addSubview(imageView)
        let parent = imageView.superview!
        NSLayoutConstraint(item: imageView, attribute: .centerX, relatedBy: .equal, toItem: parent,
            attribute: .centerX, multiplier: 1, constant: 0).isActive = true
        NSLayoutConstraint(item: imageView, attribute: .centerY, relatedBy: .equal, toItem: parent,
            attribute: .centerY, multiplier: 1, constant: 0).isActive = true
        return container
    }()
    private lazy var sourceLocationLabel: UILabel = {
        let label = UILabel()
        switch source.sourceType {
        case .article: label.text = TranslationManager.messenger.translation(forKey: "help_center_article")
        case .url: label.text = source.url?.host
        }
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .footnote).pointSize, weight: .regular)
        label.textColor = themeProvider.theme.textMuted
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var sourceTitleLabel: UILabel = {
        let label = UILabel()
        label.text = source.title
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .footnote).pointSize, weight: .semibold)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var infoStackView: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = .horizontal
        stack.alignment = .center
        stack.spacing = 6
        stack.distribution = .fill
        if !ICMUtils.contentSizeCategoryIsAccessibilityCategory() { stack.addArrangedSubview(imageContainerView) }
        stack.addArrangedSubview(sourceLocationLabel)
        return stack
    }()
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.axis = .vertical
        stack.alignment = .leading
        stack.spacing = 4
        stack.distribution = .fill
        return stack
    }()
    init(source: FinAnswerSource, index: Int, highlighted: Bool, themeProvider: DefaultThemeProvider) {
        self.themeProvider = themeProvider
        self.source = source
        self.index = index
        self.highlighted = highlighted
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        setupIndexLabel()
        setupMainStackView()
    }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override func layoutSubviews() {
        super.layoutSubviews()
        let theme = themeProvider.theme
        backgroundColor = highlighted ? theme.neutralContainer : theme.base
    }
    private func setupIndexLabel() {
        addSubview(indexLabel)
        let constraints = [indexLabel.centerYAnchor.constraint(equalTo: centerYAnchor),
            indexLabel.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 14)]
        NSLayoutConstraint.activate(constraints)
    }
    private func setupMainStackView() {
        addSubview(mainStackView)
        mainStackView.addArrangedSubview(infoStackView)
        mainStackView.addArrangedSubview(sourceTitleLabel)
        let constraints = [mainStackView.leadingAnchor.constraint(equalTo: indexLabel.trailingAnchor, constant: 12),
            mainStackView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -8),
            mainStackView.topAnchor.constraint(equalTo: topAnchor, constant: 22),
            mainStackView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -22)]
        NSLayoutConstraint.activate(constraints)
    }
}
