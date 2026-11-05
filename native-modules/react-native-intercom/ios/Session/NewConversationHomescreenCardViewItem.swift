import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate37NewConversationHomescreenCardViewItem)
class NewConversationHomescreenCardViewItem: HomescreenCardViewItem {
    private weak var cardDelegate: (any HomescreenCardDelegate)?
    private let themeProvider: any ThemeProvider
    private let card: NewConversationCard
    private let messagesMetricService: any MessagesMetricServiceProtocol

    init(card: NewConversationCard, cardDelegate: (any HomescreenCardDelegate)?, messagesMetricService: any MessagesMetricServiceProtocol, themeProvider: any ThemeProvider) {
        self.cardDelegate = cardDelegate
        self.themeProvider = themeProvider
        self.card = card
        self.messagesMetricService = messagesMetricService
    }
    func buildView() -> HomescreenCardView {
        let title = card.title
        let row = buildNewConversationView()
        return HomescreenCardView(title: title, subviews: [row], themeProvider: DefaultThemeProvider.shared)
    }
    private func buildNewConversationView() -> StandardOutlineCellItemView {
        let accessories = card.iconViews
        let accessibilityLabel = getAccessoryViewAccessibilityLabel()
        let title = card.label ?? ""
        let subtitle = card.subtitle
        let row = StandardOutlineCellItemView(frame: .zero, title: title, subtitle: subtitle, accessoryViews: accessories, showSeparator: false, constrainAccessoriesToSize: CGSize(width: 16, height: 16), accessibilityLabel: accessibilityLabel, themeProvider: DefaultThemeProvider.shared, action: { [weak self] in
            do {
                let service = self?.messagesMetricService
                service?.addNewConversationFromHomeMetric()
            }
            let delegate = self?.cardDelegate
            delegate?.navigate(to: .newConversation)
        })
        let label = row.titleLabel
        let font = label.font!
        if let descriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) {
            label.font = UIFont(descriptor: descriptor, size: 0)
        } else { label.font = font }
        return row
    }
    private func getAccessoryViewAccessibilityLabel() -> String? {
        guard let label = card.label else { return nil }
        if let subtitle = card.subtitle { return label + ", " + subtitle }
        return label
    }
}
