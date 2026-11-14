import UIKit

struct TicketsListViewCellItem {
    let title: String
    var subtitle: String?
    var ticketNumber: String?
    var ticketStatus: String?
    let stage: TicketStatus.Stage
    let useBoldTitle: Bool
    let unreadDotViewIsHidden: Bool
    let showDividerLine: Bool

    init(ticket: Ticket, showDividerLine: Bool, canShowUnreadDot: Bool) {
        title = ticket.title
        stage = ticket.currentStatus.stage
        let read = ticket.read
        let accessibility = ICMUtils.contentSizeCategoryIsAccessibilityCategory()
        let id = ticket.publicTicketId
        if accessibility {
            ticketNumber = id.map { "#" + $0 }
            ticketStatus = ticket.currentStatus.title
            subtitle = nil
        } else {
            subtitle = id.map { "#" + $0 + " • " + ticket.currentStatus.title } ?? ticket.currentStatus.title
            ticketNumber = nil
            ticketStatus = nil
        }
        useBoldTitle = !read
        unreadDotViewIsHidden = read || !canShowUnreadDot
        self.showDividerLine = showDividerLine
    }
}

@objc(_TtC18IntercomSDKPrivate19TicketsListViewCell)
class TicketsListViewCell: UITableViewCell {
    @objc var themeProvider: ThemeProvider = DefaultThemeProvider.shared
    private enum Sizes { static let mainStackViewSpacing = ICMUtils.scale(toAccessibilitySizing: 12) }
    private lazy var mainStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .center
        stack.spacing = Sizes.mainStackViewSpacing
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var iconView: UIImageView = {
        let icon = UIImageView()
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.widthAnchor.constraint(equalToConstant: 32).isActive = true
        icon.heightAnchor.constraint(equalTo: icon.widthAnchor).isActive = true
        return icon
    }()
    private lazy var labelsStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .leading
        stack.spacing = 5
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.textColor = themeProvider.theme.textDefault
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var subtitleLabel: UILabel = {
        let label = UILabel()
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var ticketStatusLabel: UILabel = {
        let label = UILabel()
        label.textColor = themeProvider.theme.textMuted
        label.font = .preferredFont(forTextStyle: .subheadline)
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var accessoryViewContainer: UIView = {
        let container = UIView()
        container.backgroundColor = .clear
        container.translatesAutoresizingMaskIntoConstraints = false
        container.heightAnchor.constraint(equalToConstant: 16).isActive = true
        container.widthAnchor.constraint(equalTo: container.heightAnchor).isActive = true
        container.addSubview(chevronView)
        container.addSubview(unreadDotView)
        NSLayoutConstraint.activate([
            chevronView.centerXAnchor.constraint(equalTo: container.centerXAnchor),
            chevronView.centerYAnchor.constraint(equalTo: container.centerYAnchor),
            unreadDotView.centerXAnchor.constraint(equalTo: container.centerXAnchor),
            unreadDotView.centerYAnchor.constraint(equalTo: container.centerYAnchor)
        ])
        return container
    }()
    private lazy var chevronView: UIImageView = {
        let chevron = UIImageView()
        chevron.image = ICMAssets.helpCenterChevron()!.imageFlippedForRightToLeftLayoutDirection()
        chevron.tintColor = themeProvider.theme.primaryForeground
        chevron.translatesAutoresizingMaskIntoConstraints = false
        chevron.widthAnchor.constraint(equalToConstant: 16).isActive = true
        chevron.heightAnchor.constraint(equalTo: chevron.widthAnchor).isActive = true
        return chevron
    }()
    private lazy var unreadDotView: UIView = {
        let dot = UIView()
        dot.backgroundColor = themeProvider.theme.statusNotificationFill
        dot.translatesAutoresizingMaskIntoConstraints = false
        dot.layer.cornerRadius = 4
        dot.layer.masksToBounds = true
        dot.isHidden = true
        dot.translatesAutoresizingMaskIntoConstraints = false
        dot.heightAnchor.constraint(equalToConstant: 8).isActive = true
        dot.widthAnchor.constraint(equalToConstant: 8).isActive = true
        return dot
    }()
    private lazy var dividerLine: UIView = {
        let divider = UIView()
        divider.backgroundColor = themeProvider.theme.neutralContainer
        divider.translatesAutoresizingMaskIntoConstraints = false
        divider.heightAnchor.constraint(equalToConstant: 1).isActive = true
        return divider
    }()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupMainStackView()
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { setupForAccessibilitySizes() }
        else { setupForRegularSizes() }
        backgroundColor = themeProvider.theme.base
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func setupMainStackView() {
        contentView.addSubview(mainStackView)
        NSLayoutConstraint.activate([
            mainStackView.leadingAnchor.constraint(equalTo: contentView.layoutMarginsGuide.leadingAnchor),
            mainStackView.trailingAnchor.constraint(equalTo: contentView.layoutMarginsGuide.trailingAnchor),
            mainStackView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 15),
            mainStackView.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -15)
        ])
    }
    private func setupForAccessibilitySizes() {
        labelsStackView.spacing = Sizes.mainStackViewSpacing
        for label in [titleLabel, subtitleLabel, ticketStatusLabel] { labelsStackView.addArrangedSubview(label) }
        mainStackView.addArrangedSubview(labelsStackView)
        mainStackView.addArrangedSubview(accessoryViewContainer)
    }
    private func setupForRegularSizes() {
        labelsStackView.spacing = ICMUtils.scale(toAccessibilitySizing: 4)
        labelsStackView.addArrangedSubview(titleLabel)
        labelsStackView.addArrangedSubview(subtitleLabel)
        mainStackView.addArrangedSubview(iconView)
        mainStackView.addArrangedSubview(labelsStackView)
        mainStackView.addArrangedSubview(accessoryViewContainer)
    }
    @objc(configureWithTicket:showDividerLine:canShowUnreadDot:)
    func configure(ticket: Ticket, showDividerLine: Bool, canShowUnreadDot: Bool) {
        configure(for: TicketsListViewCellItem(ticket: ticket, showDividerLine: showDividerLine, canShowUnreadDot: canShowUnreadDot))
    }
    func configure(for item: TicketsListViewCellItem) {
        titleLabel.text = item.title
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() {
            subtitleLabel.isHidden = item.ticketNumber == nil
            subtitleLabel.text = item.ticketNumber
            ticketStatusLabel.text = item.ticketStatus
        } else { subtitleLabel.text = item.subtitle }
        switch item.stage {
        case .submitted, .inProgress: iconView.image = ICMAssets.ticketSubmittedIcon()
        case .waitingOnYou: iconView.image = ICMAssets.ticketWaitingOnYouIcon()
        case .resolved: iconView.image = ICMAssets.ticketResolvedIcon()
        }
        switch item.stage {
        case .submitted, .inProgress: iconView.tintColor = themeProvider.theme.primaryForeground
        case .waitingOnYou: iconView.tintColor = themeProvider.theme.statusWaitingOnYouFill
        case .resolved: iconView.tintColor = themeProvider.theme.statusOnlineFill
        }
        titleLabel.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: item.useBoldTitle ? .semibold : .regular)
        unreadDotView.isHidden = item.unreadDotViewIsHidden
        chevronView.isHidden = !unreadDotView.isHidden
        if item.showDividerLine && dividerLine.superview == nil { addDividerLine() }
    }
    private func addDividerLine() {
        contentView.addSubview(dividerLine)
        NSLayoutConstraint.activate([
            dividerLine.leadingAnchor.constraint(equalTo: contentView.layoutMarginsGuide.leadingAnchor),
            dividerLine.trailingAnchor.constraint(equalTo: contentView.layoutMarginsGuide.trailingAnchor),
            dividerLine.bottomAnchor.constraint(equalTo: contentView.bottomAnchor)
        ])
    }
    override func prepareForReuse() {
        super.prepareForReuse()
        titleLabel.text = nil
        subtitleLabel.text = nil
        subtitleLabel.isHidden = false
        ticketStatusLabel.text = nil
        iconView.image = nil
        iconView.tintColor = themeProvider.theme.primaryForeground
        titleLabel.font = .preferredFont(forTextStyle: .subheadline)
        chevronView.isHidden = false
        unreadDotView.isHidden = true
        dividerLine.removeFromSuperview()
    }
}
