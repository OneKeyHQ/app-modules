import UIKit

@objc(_TtC18IntercomSDKPrivate24TicketStatusTimelineView)
class TicketStatusTimelineView: UIStackView {
    private let statuses: [TicketStatus]
    private let timelineColor: UIColor?
    init(statuses: [TicketStatus], timelineColor: UIColor?) {
        self.statuses = statuses
        self.timelineColor = timelineColor
        super.init(frame: .zero)
        setupView()
        setupAccessibility()
    }
    required init(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func setupView() {
        axis = .horizontal
        alignment = .top
        distribution = .fillEqually
        spacing = 3
        translatesAutoresizingMaskIntoConstraints = false
        let values = statuses
        let views = values.enumerated().map { index, status in
            TicketStatusTimelineItemView(status: status, timelineColor: timelineColor, isFirst: index == 0, isLast: index == statuses.count - 1, themeProvider: DefaultThemeProvider.shared)
        }
        views.forEach { addArrangedSubview($0) }
    }
    private func setupAccessibility() {
        guard let status = statuses.first(where: { $0.isCurrentStatus }), let date = status.createdDate else { return }
        guard let timestamp = TimestampCalculator.shared.timestampText(for: date, format: .short, recentTimeMessage: .justNow) else { return }
        isAccessibilityElement = true
        let params = ["current_status_title": status.title, "date": timestamp]
        var text = TranslationManager.messenger.translation(forKey: "ticket_current_status_with_date")
        for (key, value) in params { text = text.replacingOccurrences(of: "{" + key + "}", with: value) }
        accessibilityLabel = text
    }
    @objc(estimatedHeightFor:within:)
    class func estimatedHeight(for part: ICMConversationPart, within width: CGFloat) -> CGFloat {
        guard let ticket = part.ticket else { return 0 }
        let statuses = ticket.statusList
        guard !statuses.isEmpty else { return 0 }
        let constrainedWidth = width / CGFloat(statuses.count) - CGFloat(statuses.count - 1) * 3
        let heights = statuses.map { status -> CGFloat in
            var height: CGFloat = 12
            guard let titleFont = ICMUtils.font(ofSize: 13, weight: 5) else { return height }
            guard let dateFont = ICMUtils.font(ofSize: 13, weight: 0) else { return height }
            height += status.title.height(withConstrainedWidth: constrainedWidth, font: titleFont, maxLines: 0)
            if let date = status.createdDate, let timestamp = TimestampCalculator.shared.timestampText(for: date, format: .short, recentTimeMessage: .justNow) {
                height += timestamp.height(withConstrainedWidth: constrainedWidth, font: dateFont, maxLines: 0)
            }
            return height
        }
        return heights.max() ?? 0
    }
}

extension String {
    func height(withConstrainedWidth width: CGFloat, font: UIFont, maxLines: CGFloat) -> CGFloat {
        let rect = (self as NSString).boundingRect(with: CGSize(width: width, height: .greatestFiniteMagnitude), options: .usesLineFragmentOrigin, attributes: [.font: font], context: nil)
        var height = ceil(CGRectGetHeight(rect))
        if maxLines > 0 {
            let lines = height / font.lineHeight
            if Int(lines) > Int(maxLines) { height = maxLines * (CGRectGetHeight(rect) / lines) }
        }
        return height
    }
}
