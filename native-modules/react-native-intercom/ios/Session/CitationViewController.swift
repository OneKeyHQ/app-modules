import UIKit

@objc(_TtC18IntercomSDKPrivate22CitationViewController)
class CitationViewController: UIViewController {
    let themeProvider: DefaultThemeProvider
    let sources: [FinAnswerSource]
    let citations: [ITBInlineCitation]
    // The two WMO-deleted SourceTapDelegate requirements remain a private ABI gap.
    weak var delegate: ConversationViewModel?
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.text = TranslationManager.messenger.translation(forKey: "sources")
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .regular)
        label.textColor = themeProvider.theme.textDefault
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        return label
    }()
    private lazy var closeButton: UIButton = {
        let button = UIButton()
        let image = UIGraphicsImageRenderer(size: CGSize(width: 8, height: 8)).image { _ in
            ICMAssets.closeImage()?.draw(in: CGRect(x: 0, y: 0, width: 8, height: 8))
        }.withRenderingMode(.alwaysTemplate)
        var configuration = UIButton.Configuration.filled()
        configuration.image = image
        configuration.baseBackgroundColor = themeProvider.theme.neutralContainer
        configuration.baseForegroundColor = themeProvider.theme.iconMuted
        configuration.cornerStyle = .capsule
        button.configuration = configuration
        button.translatesAutoresizingMaskIntoConstraints = false
        button.widthAnchor.constraint(equalToConstant: 30).isActive = true
        button.heightAnchor.constraint(equalToConstant: 30).isActive = true
        button.addAction(for: .touchUpInside) { [weak self] _ in self?.dismiss(animated: true, completion: nil) }
        return button
    }()
    private lazy var scrollView: UIScrollView = {
        let view = UIScrollView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var stackView: UIStackView = {
        let view = UIStackView()
        view.translatesAutoresizingMaskIntoConstraints = false
        view.axis = .vertical
        view.alignment = .leading
        view.distribution = .fill
        return view
    }()
    init(citations: [ITBInlineCitation], sources: [FinAnswerSource], delegate: ConversationViewModel?, themeProvider: DefaultThemeProvider) {
        self.themeProvider = themeProvider
        self.sources = sources
        self.citations = citations
        self.delegate = delegate
        super.init(nibName: nil, bundle: nil)
    }
    override init(nibName: String?, bundle: Bundle?) { fatalError("init(nibName:bundle:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override func viewDidLoad() {
        super.viewDidLoad()
        view!.backgroundColor = themeProvider.theme.base
        navigationItem.backButtonDisplayMode = .minimal
        setupTitleBar()
        setupScrollView()
        setupStackView()
        buildSourceViews()
    }
    private func setupTitleBar() {
        view!.addSubview(titleLabel)
        view!.addSubview(closeButton)
        let constraints = [titleLabel.leadingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.leadingAnchor, constant: 32),
            closeButton.trailingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.trailingAnchor, constant: -16),
            closeButton.topAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.topAnchor, constant: 10),
            titleLabel.centerYAnchor.constraint(equalTo: closeButton.centerYAnchor)]
        NSLayoutConstraint.activate(constraints)
    }
    private func setupScrollView() {
        view!.addSubview(scrollView)
        pinHorizontal(scrollView, safe: true)
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 10).isActive = true
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.bottomAnchor.constraint(equalTo: scrollView.superview!.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
    private func setupStackView() {
        scrollView.addSubview(stackView)
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.topAnchor.constraint(equalTo: stackView.superview!.topAnchor).isActive = true
        pinHorizontal(stackView, safe: false)
        stackView.translatesAutoresizingMaskIntoConstraints = false
        stackView.bottomAnchor.constraint(equalTo: stackView.superview!.bottomAnchor, constant: -CGFloat.zero).isActive = true
        stackView.widthAnchor.constraint(equalTo: scrollView.widthAnchor).isActive = true
    }
    private func buildSourceViews() {
        for (index, source) in sources.enumerated() {
            let row = addSourceView(for: source, at: index)
            stackView.addArrangedSubview(row)
            row.widthAnchor.constraint(equalTo: row.superview!.widthAnchor).isActive = true
            if index == sources.count - 1 { continue }
            let separator = UIView()
            separator.translatesAutoresizingMaskIntoConstraints = false
            separator.backgroundColor = themeProvider.theme.neutralBorder
            separator.heightAnchor.constraint(equalToConstant: 1).isActive = true
            stackView.addArrangedSubview(separator)
            separator.widthAnchor.constraint(equalTo: separator.superview!.widthAnchor).isActive = true
        }
    }
    private func addSourceView(for source: FinAnswerSource, at index: Int) -> UIView {
        let highlighted = citations.contains { citation in
            guard let href = citation.href else { return false }
            guard source.url != nil else { return false }
            return href == source.url!.absoluteString
        }
        let tap = SourceViewTapGestureRecognizer(target: self, action: #selector(sourceTapped(_:)))
        tap.source = source
        let theme = DefaultThemeProvider.shared
        let row = SourceView(source: source, index: index, highlighted: highlighted, themeProvider: theme)
        row.addGestureRecognizer(tap)
        return row
    }
    @objc(sourceTapped:) private func sourceTapped(_ recognizer: SourceViewTapGestureRecognizer) {
        guard let source = recognizer.source else { return }
        switch source.sourceType {
        case .article(let id):
            guard let model = delegate, let current = model.view else { return }
            withExtendedLifetime(current) { ICMPresentationManager.sharedInstance()!.presentArticle(id, allowReactions: false, anchorLink: nil) }
        case .url:
            guard let url = source.url else { return }
            guard let model = delegate, let current = model.view else { return }
            withExtendedLifetime(current) { LinkOpener.shared.openLink(url) }
        }
    }
    // Source expression of the original inlined physical-edge constraints.
    private func pinHorizontal(_ child: UIView, safe: Bool) {
        let rtl = child.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = child.superview!
            child.translatesAutoresizingMaskIntoConstraints = false
            child.translatesAutoresizingMaskIntoConstraints = false
            child.leftAnchor.constraint(equalTo: safe ? parent.safeAreaLayoutGuide.leftAnchor : parent.leftAnchor).isActive = true
        }
        func right() {
            let parent = child.superview!
            child.translatesAutoresizingMaskIntoConstraints = false
            child.translatesAutoresizingMaskIntoConstraints = false
            child.rightAnchor.constraint(equalTo: safe ? parent.safeAreaLayoutGuide.rightAnchor : parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
    }
}
