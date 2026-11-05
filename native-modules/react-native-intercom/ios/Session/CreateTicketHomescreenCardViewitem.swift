import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate34CreateTicketHomescreenCardViewitem)
class CreateTicketHomescreenCardViewitem: HomescreenCardViewItem {
    private weak var cardDelegate: (any HomescreenCardDelegate)?
    private let themeProvider: any ThemeProvider
    private let card: CreateTicketCard

    init(card: CreateTicketCard, cardDelegate: (any HomescreenCardDelegate)?, themeProvider: any ThemeProvider) {
        self.cardDelegate = cardDelegate
        self.themeProvider = themeProvider
        self.card = card
    }
    func buildView() -> HomescreenCardView {
        let rows = buildCreateTicketViews()
        let title = card.title
        return HomescreenCardView(title: title, subviews: rows, themeProvider: DefaultThemeProvider.shared)
    }
    private func buildCreateTicketViews() -> [StandardOutlineCellItemView] {
        card.createTicketLinks.enumerated().map { index, link in
            let accessories = remoteAccessory(link.iconURL)
            let separator = card.createTicketLinks.count > 1 && index < card.createTicketLinks.count - 1
            let row = StandardOutlineCellItemView(frame: .zero, title: link.name, subtitle: nil, accessoryViews: accessories, showSeparator: separator, constrainAccessoriesToSize: CGSize(width: 16, height: 16), accessibilityLabel: link.name, themeProvider: DefaultThemeProvider.shared, action: { [weak self, link] in
                let delegate = self?.cardDelegate
                delegate?.navigate(to: .createTicket(link.ticketType))
            })
            return row
        }
    }
    private func remoteAccessory(_ url: String) -> [UIView] {
        let view = UIImageView()
        view.contentMode = .scaleAspectFit
        view.translatesAutoresizingMaskIntoConstraints = false
        let loader = ITBImageLoader.longTerm()!
        _ = loader.loadImage(withURLString: url) { [self, view] object in
            let image = (object as? UIImage)?.imageFlippedForRightToLeftLayoutDirection()
            view.image = image?.withTintColor(themeProvider.theme.primaryForeground, renderingMode: .alwaysTemplate)
        }
        return [view]
    }
}
