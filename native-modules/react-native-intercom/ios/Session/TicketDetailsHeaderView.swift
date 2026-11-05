// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate23TicketDetailsHeaderView)
class TicketDetailsHeaderView: UIView {
    private lazy var assigneeView: ICMMultipleAvatarView = {
        let view = ICMMultipleAvatarView(width: 64)!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 64).isActive = true
        view.translatesAutoresizingMaskIntoConstraints = false
        view.heightAnchor.constraint(equalToConstant: 64).isActive = true
        return view
    }()
    private lazy var teamAssigneeView: ICMActiveAdminsAvatarView = {
        let view = ICMActiveAdminsAvatarView(height: 64, overlap: 5.12, maxCount: 3)!
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var currentStatusView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 8
        return stack
    }()
    private lazy var statusTitleLabel: UILabel = {
        let label = UILabel()
        label.font = .systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .callout).pointSize, weight: .semibold)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }()
    private lazy var currentStatusDetailLabel = makeLabel(style: .callout)
    private lazy var serviceLevelAgreementImageView: UIImageView = {
        let image = ICMAssets.stopwatchIcon()!.withAlignmentRectInsets(UIEdgeInsets(top: -1, left: 0, bottom: 0, right: 0))
        let view = UIImageView(image: image)
        view.contentMode = .scaleAspectFit
        view.tintColor = DefaultThemeProvider.shared.theme.iconDefault
        view.translatesAutoresizingMaskIntoConstraints = false
        view.widthAnchor.constraint(equalToConstant: 13).isActive = true
        view.heightAnchor.constraint(equalToConstant: 18).isActive = true
        return view
    }()
    private lazy var serviceLevelAgreementLabel = makeLabel(style: .subheadline)
    private lazy var serviceLevelAgreementView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.alignment = .top
        stack.distribution = .fill
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { stack.addArrangedSubview(serviceLevelAgreementLabel) }
        else {
            stack.addArrangedSubview(serviceLevelAgreementImageView)
            stack.addArrangedSubview(serviceLevelAgreementLabel)
        }
        return stack
    }()
    private var statusTimelineView: TicketStatusTimelineView?
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .center
        stack.distribution = .fill
        stack.spacing = 12
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    override init(frame: CGRect) {
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(containerStackView)
        pinEdges(containerStackView, insets: UIEdgeInsets(top: 24, left: 8, bottom: 0, right: 8))
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    private func makeLabel(style: UIFont.TextStyle) -> UILabel {
        let label = UILabel()
        label.font = .preferredFont(forTextStyle: style)
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.textAlignment = .center
        label.translatesAutoresizingMaskIntoConstraints = false
        return label
    }
    func update(with ticket: Ticket) {
        containerStackView.removeAllArrangedSubviews()
        if let assignee = ticket.assignee {
            containerStackView.addArrangedSubview(assigneeView)
            assigneeView.bindAdmins([assignee])
        } else { containerStackView.addArrangedSubview(teamAssigneeView) }
        updateCurrentStatus(ticket.currentStatus)
        if let sla = ticket.currentStatus.slaDetails {
            containerStackView.addArrangedSubview(serviceLevelAgreementView)
            serviceLevelAgreementLabel.text = sla
        }
        updateAccessibility(ticket.currentStatus)
        updateStatusTimeline(ticket)
    }
    private func statusColor(_ stage: TicketStatus.Stage) -> UIColor {
        switch stage {
        case .submitted, .inProgress: return DefaultThemeProvider.shared.theme.statusInProgressFill
        case .waitingOnYou: return DefaultThemeProvider.shared.theme.statusWaitingOnYouFill
        case .resolved: return DefaultThemeProvider.shared.theme.statusOnlineFill
        }
    }
    private func updateCurrentStatus(_ status: TicketStatus) {
        containerStackView.addArrangedSubview(currentStatusView)
        currentStatusView.addArrangedSubview(statusTitleLabel)
        statusTitleLabel.textColor = statusColor(status.stage)
        statusTitleLabel.text = status.titleAndTimestamp(for: effectiveUserInterfaceLayoutDirection)
        if let detail = status.statusDetail {
            currentStatusView.addArrangedSubview(currentStatusDetailLabel)
            currentStatusDetailLabel.text = detail
        }
    }
    private func updateAccessibility(_ status: TicketStatus) {
        isAccessibilityElement = true
        var text = TranslationManager.messenger.translation(forKey: "ticket_current_status").replacingOccurrences(of: "{current_status_title}", with: status.stage.details)
        if let date = status.createdDate, let timestamp = TimestampCalculator.shared.timestampText(for: date, format: .short, recentTimeMessage: .justNow) {
            text += ", " + timestamp
        } else { text += "; " }
        if let detail = status.statusDetail {
            text += TranslationManager.messenger.translation(forKey: "ticket_status_details").replacingOccurrences(of: "{status_details}", with: detail)
        }
        if let sla = status.slaDetails {
            text += TranslationManager.messenger.translation(forKey: "ticket_sla_details").replacingOccurrences(of: "{sla_details}", with: sla)
        }
        accessibilityLabel = text
    }
    private func updateStatusTimeline(_ ticket: Ticket) {
        if ICMUtils.contentSizeCategoryIsAccessibilityCategory() { return }
        let statuses = ticket.statusList
        let stage = ticket.currentStatus.stage
        let color = statusColor(stage)
        statusTimelineView = TicketStatusTimelineView(statuses: statuses, timelineColor: color)
        let wrapper = UIView()
        wrapper.translatesAutoresizingMaskIntoConstraints = false
        wrapper.addSubview(statusTimelineView!)
        pinEdges(statusTimelineView!, insets: UIEdgeInsets(top: 12, left: 0, bottom: 0, right: 0))
        containerStackView.addArrangedSubview(wrapper)
        pinHorizontal(wrapper, insets: .zero)
    }
    private func pinHorizontal(_ child: UIView, insets: UIEdgeInsets) {
        child.translatesAutoresizingMaskIntoConstraints = false
        if child.effectiveUserInterfaceLayoutDirection == .rightToLeft {
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor, constant: -insets.right).isActive = true
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor, constant: insets.left).isActive = true
        } else {
            child.leftAnchor.constraint(equalTo: child.superview!.leftAnchor, constant: insets.left).isActive = true
            child.rightAnchor.constraint(equalTo: child.superview!.rightAnchor, constant: -insets.right).isActive = true
        }
    }
    private func pinEdges(_ child: UIView, insets: UIEdgeInsets) {
        child.translatesAutoresizingMaskIntoConstraints = false
        child.topAnchor.constraint(equalTo: child.superview!.topAnchor, constant: insets.top).isActive = true
        pinHorizontal(child, insets: insets)
        child.translatesAutoresizingMaskIntoConstraints = false
        child.bottomAnchor.constraint(equalTo: child.superview!.bottomAnchor, constant: -insets.bottom).isActive = true
    }
}
