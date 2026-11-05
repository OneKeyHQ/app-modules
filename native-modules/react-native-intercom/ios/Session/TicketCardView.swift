import UIKit

@objc(_TtC18IntercomSDKPrivate14TicketCardView)
class TicketCardView: UIView {
    private weak var delegate: ConversationViewModel?
    private let themeProvider: ThemeProvider
    private lazy var ticketIconView: UIImageView = {
        let view = UIImageView(image: ICMAssets.ticketIcon())
        view.contentMode = .scaleAspectFit
        view.tintColor = themeProvider.theme.primaryForeground
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 16).isActive = true
        view.translatesAutoresizingMaskIntoConstraints = false
        view.heightAnchor.constraint(equalToConstant: 16).isActive = true
        return view
    }()
    private lazy var ticketTitleLabel: UILabel = {
        let label = UILabel()
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .callout).pointSize, weight: .semibold)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var viewMoreButtonStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.distribution = .fill
        stack.alignment = .center
        stack.spacing = 8
        [ticketIconView, viewMoreLabel].forEach { stack.addArrangedSubview($0) }
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var statusDetailLabel = makeDetailLabel()
    private lazy var ticketStatusLabel: UILabel = {
        let label = UILabel()
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .semibold)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var serviceLevelAgreementImageView: UIImageView = {
        let image = ICMAssets.stopwatchIcon()!.withAlignmentRectInsets(UIEdgeInsets(top: -1, left: 0, bottom: 0, right: 0))
        let view = UIImageView(image: image)
        view.contentMode = .scaleAspectFit
        view.tintColor = themeProvider.theme.textDefault
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 13).isActive = true
        view.translatesAutoresizingMaskIntoConstraints = false
        view.heightAnchor.constraint(equalToConstant: 18).isActive = true
        return view
    }()
    private lazy var serviceLevelAgreementLabel = makeDetailLabel()
    private lazy var serviceLevelAgreementView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .leading
        stack.distribution = .fill
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        [serviceLevelAgreementImageView, serviceLevelAgreementLabel].forEach { stack.addArrangedSubview($0) }
        return stack
    }()
    private var statusTimelineView: TicketStatusTimelineView?
    private lazy var borderView: UIView = {
        let view = UIView()
        view.backgroundColor = themeProvider.theme.neutralBorder
        view.translatesAutoresizingMaskIntoConstraints = false
        view.heightAnchor.constraint(equalToConstant: 1).isActive = true
        return view
    }()
    private lazy var viewMoreLabel: UILabel = {
        let label = UILabel()
        let font = UIFont.preferredFont(forTextStyle: .subheadline)
        if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) { label.font = UIFont(descriptor: descriptor, size: 0) }
        else { label.font = font }
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.textColor = themeProvider.theme.primaryForeground
        label.text = TranslationManager.messenger.translation(forKey: "tickets_view_ticket")
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private let textStackView: UIStackView
    private let containerStackView: UIStackView

    init(for ticket: Ticket, delegate: ConversationViewModel?, themeProvider: ThemeProvider) {
        let text = UIStackView()
        text.axis = .vertical
        text.alignment = .center
        text.distribution = .fill
        text.spacing = 8
        text.translatesAutoresizingMaskIntoConstraints = false
        textStackView = text
        let container = UIStackView()
        container.axis = .vertical
        container.alignment = .center
        container.distribution = .fill
        container.spacing = 20
        container.translatesAutoresizingMaskIntoConstraints = false
        containerStackView = container
        self.delegate = delegate
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        setupView()
        configure(with: ticket)
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func makeDetailLabel() -> UILabel {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }
    private func setupView() {
        clipsToBounds = true
        backgroundColor = themeProvider.theme.base
        layer.cornerRadius = 20
        layer.borderWidth = 1
        layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
        setupContainerStackView()
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(cardTapped)))
    }
    private func configure(with ticket: Ticket) {
        let statuses = ticket.statusList
        let stage = ticket.currentStatus.stage
        let color = statusColor(for: stage)
        statusTimelineView = TicketStatusTimelineView(statuses: statuses, timelineColor: color)
        containerStackView.removeAllArrangedSubviews()
        configureTextStack(for: ticket)
        [statusTimelineView!, borderView, viewMoreButtonStackView].forEach { containerStackView.addArrangedSubview($0) }
        configureConstraints()
        containerStackView.layoutSubviews()
        setupAccessibility(for: ticket)
    }
    private func statusColor(for stage: TicketStatus.Stage) -> UIColor {
        let theme = DefaultThemeProvider.shared.theme
        switch stage {
        case .submitted, .inProgress: return theme.statusInProgressFill
        case .waitingOnYou: return theme.statusWaitingOnYouFill
        case .resolved: return theme.statusOnlineFill
        }
    }
    private func setupContainerStackView() {
        addSubview(containerStackView)
        pinHorizontal(containerStackView, inset: 20)
        let bottomView = containerStackView
        let bottomParent = bottomView.superview!
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        bottomView.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -20).isActive = true
        let topView = containerStackView
        let topParent = topView.superview!
        topView.translatesAutoresizingMaskIntoConstraints = false
        topView.translatesAutoresizingMaskIntoConstraints = false
        topView.topAnchor.constraint(equalTo: topParent.topAnchor, constant: 16).isActive = true
    }
    private func configureTextStack(for ticket: Ticket) {
        containerStackView.addArrangedSubview(textStackView)
        [ticketTitleLabel, ticketStatusLabel].forEach { textStackView.addArrangedSubview($0) }
        ticketTitleLabel.text = ticket.title
        let label = ticketStatusLabel
        label.text = ticket.currentStatus.titleAndTimestamp(for: effectiveUserInterfaceLayoutDirection)
        ticketStatusLabel.textColor = statusColor(for: ticket.currentStatus.stage)
        if let sla = ticket.currentStatus.slaDetails {
            serviceLevelAgreementLabel.text = sla
            textStackView.addArrangedSubview(serviceLevelAgreementView)
        } else {
            statusDetailLabel.text = ticket.currentStatus.statusDetail
            textStackView.addArrangedSubview(statusDetailLabel)
        }
    }
    private func configureConstraints() {
        let label = viewMoreLabel
        let topParent = label.superview!
        label.translatesAutoresizingMaskIntoConstraints = false
        label.translatesAutoresizingMaskIntoConstraints = false
        label.topAnchor.constraint(equalTo: topParent.topAnchor).isActive = true
        let bottomParent = label.superview!
        label.translatesAutoresizingMaskIntoConstraints = false
        label.translatesAutoresizingMaskIntoConstraints = false
        label.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero).isActive = true
        pinHorizontal(statusTimelineView!, inset: 0)
        pinHorizontal(borderView, inset: 0)
    }
    private func pinHorizontal(_ view: UIView, inset: CGFloat) {
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor, constant: inset).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -inset).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
    }
    private func setupAccessibility(for ticket: Ticket) {
        isAccessibilityElement = true
        let params = ["title": ticket.title]
        var text = TranslationManager.messenger.translation(forKey: "ticket_details_for_title")
        for (key, value) in params { text = text.replacingOccurrences(of: "{" + key + "}", with: value) }
        text += "; "
        text += ticket.currentStatus.statusDetail ?? ""
        if let sla = ticket.currentStatus.slaDetails { text += "; " + sla }
        text += "; " + (statusTimelineView!.accessibilityLabel ?? "")
        accessibilityTraits = .button
        accessibilityLabel = text
    }
    @objc private func cardTapped() { delegate?.showTicketDetails() }
}
