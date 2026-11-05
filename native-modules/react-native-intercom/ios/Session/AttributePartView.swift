import UIKit

@objc(_TtC18IntercomSDKPrivate17AttributePartView)
class AttributePartView: UIView, ConversationCellContentView {
    private var timestampView: PartStatusView?
    private var displayTimestampTimer: Timer?
    var inputViewArray: [AttributeInputView] = []
    var formIndexCounter = 0
    let content: ICMConversationPart
    weak var delegate: (any ConversationCellBlockDelegate)?
    private let themeProvider: ThemeProvider
    private lazy var inputStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.backgroundColor = themeProvider.theme.base
        stack.layoutMargins = UIEdgeInsets(top: 28, left: 20, bottom: 24, right: 20)
        stack.isLayoutMarginsRelativeArrangement = true
        stack.spacing = 20
        stack.clipsToBounds = false
        stack.layer.masksToBounds = false
        stack.layer.cornerRadius = 20
        stack.layer.borderWidth = 1
        stack.layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        return stack
    }()
    private let containerStackView: UIStackView
    private let maxWidth: CGFloat
    init(content: ICMConversationPart, delegate: (any ConversationCellBlockDelegate)?, maxWidth: CGFloat, themeProvider: ThemeProvider) {
        let container = UIStackView()
        container.axis = .vertical
        container.spacing = 4
        containerStackView = container
        self.content = content
        self.delegate = delegate
        self.maxWidth = maxWidth
        self.themeProvider = themeProvider
        timestampView = PartStatusView(content: content, appendSeenState: false, leadingInset: 0, attributedSingleImage: false, themeProvider: DefaultThemeProvider.shared)
        super.init(frame: .zero)
        backgroundColor = .clear
        translatesAutoresizingMaskIntoConstraints = false
        configure()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    func configure() {
        addSubview(containerStackView)
        containerStackView.addArrangedSubview(inputStackView)
        addInputViews()
        updateFormCounterLabel()
        addViewConstraints()
        addStatusView(in: containerStackView, leadingInset: 0, attributedSingleImage: false)
        containerStackView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleTap)))
    }
    func addInputViews() {
        let part = content
        let count = part.form!.formAttributes.count
        for index in 0...(count - 1) {
            formIndexCounter = index + 1
            let attribute = part.form!.formAttributes[index]
            let prefix = part.form!.formAttributes.count > 1 ? index + 1 : index
            let view = AttributeInputView(formAttribute: attribute, delegate: self, formIndex: prefix, themeProvider: DefaultThemeProvider.shared)
            if !attribute.submitted && part.form!.disabled { view.formInputView?.configureDisabledState() }
            inputViewArray.append(view)
            inputStackView.addArrangedSubview(view)
            if attribute.value == nil { break }
        }
    }
    func updateFormCounterLabel() {
        let first = inputViewArray.first
        let part = content
        if part.form!.formAttributes.count < 2 { return }
        guard let first else { return }
        let text = "(" + String(formIndexCounter) + "/" + String(part.form!.formAttributes.count) + ")"
        first.counterLabel.text = text
    }
    private func addViewConstraints() {
        let stack = containerStackView
        let topParent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.topAnchor.constraint(equalTo: topParent.topAnchor).isActive = true
        let bottomParent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero).isActive = true
        let leadingParent = stack.superview!
        stack.translatesAutoresizingMaskIntoConstraints = false
        let rtl = stack.effectiveUserInterfaceLayoutDirection == .rightToLeft
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.leadingAnchor.constraint(equalTo: leadingParent.leadingAnchor, constant: rtl ? -CGFloat.zero : .zero).isActive = true
        let input = inputStackView
        let upper = maxWidth > 366 ? 366 : maxWidth
        let width = maxWidth < 268 ? 268 : upper
        input.translatesAutoresizingMaskIntoConstraints = false
        let constraint = input.widthAnchor.constraint(equalToConstant: width)
        constraint.priority = .required
        constraint.isActive = true
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        updateTemporaryTimestampDisplayTimerIfNecessary(in: containerStackView)
    }
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let previous = previousTraitCollection?.userInterfaceStyle ?? .unspecified
        let current = traitCollection.userInterfaceStyle
        if previousTraitCollection == nil || previous != current { inputStackView.layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor }
    }
    @objc private func handleTap() { updateStatusVisibility(in: containerStackView) }
    private func addStatusView(in stack: UIStackView, leadingInset: CGFloat, attributedSingleImage: Bool) {
        if !content.shouldShowStatus() { return }
        let appendSeenState = content.displayPartStatus
        let theme = DefaultThemeProvider.shared
        let status = PartStatusView(content: content, appendSeenState: appendSeenState, leadingInset: leadingInset, attributedSingleImage: attributedSingleImage, themeProvider: theme)
        timestampView = status
        if content.displayPartStatus || content.displayTimestampTemporarily || content.isSenderLinePinned { stack.addArrangedSubview(status) }
    }
    private func updateTemporaryTimestampDisplayTimerIfNecessary(in stack: UIStackView) {
        if !content.displayTimestampTemporarily { return }
        displayTimestampTimer?.invalidate()
        displayTimestampTimer = Timer.scheduledTimer(withTimeInterval: 3, repeats: false) { [weak self, stack] _ in
            guard let self = self, let status = self.timestampView else { return }
            self.content.displayTimestampTemporarily = false
            stack.removeArrangedSubview(status)
            status.removeFromSuperview()
            if let delegate = self.delegate { delegate.invalidateLayoutForPart(with: self.content.partId!) }
        }
    }
    private func updateStatusVisibility(in stack: UIStackView) {
        guard let status = timestampView else { return }
        if content.isSenderLinePinned { return }
        content.displayTimestampTemporarily.toggle()
        if content.displayTimestampTemporarily { stack.addArrangedSubview(status) }
        else { stack.removeArrangedSubview(status); status.removeFromSuperview() }
        if let delegate = delegate { delegate.invalidateLayoutForPart(with: content.partId!) }
    }
}
