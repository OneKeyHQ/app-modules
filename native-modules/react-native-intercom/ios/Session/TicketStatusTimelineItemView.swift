import UIKit

@objc(_TtC18IntercomSDKPrivate28TicketStatusTimelineItemView)
class TicketStatusTimelineItemView: UIStackView {
    private let status: TicketStatus
    private let timelineColor: UIColor?
    private let isFirst: Bool
    private let isLast: Bool
    private let themeProvider: ThemeProvider
    private lazy var lineView: UIView = {
        let fill = timelineColor ?? themeProvider.theme.primaryForeground
        let base = themeProvider.theme.neutralContainerEmphasis
        let view = TimelineLineView(fillColor: fill, baseColor: base)
        if status.isCurrentStatus { view.state = status.stage == .resolved ? .done : .inProgress }
        else { view.state = status.incomplete ? .done : .notStarted }
        view.translatesAutoresizingMaskIntoConstraints = false
        let height = view.heightAnchor.constraint(equalToConstant: 4)
        height.priority = .required
        height.isActive = true
        view.layer.cornerRadius = isFirst || isLast ? 2 : 0
        view.clipsToBounds = true
        if isFirst {
            view.layer.maskedCorners = effectiveUserInterfaceLayoutDirection == .leftToRight ? [.layerMinXMinYCorner, .layerMinXMaxYCorner] : [.layerMaxXMinYCorner, .layerMaxXMaxYCorner]
        } else if isLast {
            view.layer.maskedCorners = effectiveUserInterfaceLayoutDirection == .leftToRight ? [.layerMaxXMinYCorner, .layerMaxXMaxYCorner] : [.layerMinXMinYCorner, .layerMinXMaxYCorner]
        } else { view.layer.maskedCorners = [] }
        return view
    }()
    init(status: TicketStatus, timelineColor: UIColor?, isFirst: Bool, isLast: Bool, themeProvider: ThemeProvider) {
        self.status = status
        self.timelineColor = timelineColor
        self.isFirst = isFirst
        self.isLast = isLast
        self.themeProvider = themeProvider
        super.init(frame: .zero)
        axis = .vertical
        alignment = .center
        spacing = 2
        translatesAutoresizingMaskIntoConstraints = false
        addArrangedSubview(lineView)
        pinHorizontal(lineView)
        setCustomSpacing(8, after: lineView)
    }
    required init(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    private func pinHorizontal(_ view: UIView) {
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
    }
}
