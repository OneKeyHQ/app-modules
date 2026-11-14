import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate35ExternalLinksHomescreenCardViewItem)
class ExternalLinksHomescreenCardViewItem: HomescreenCardViewItem {
    private weak var cardDelegate: (any HomescreenCardDelegate)?
    private let themeProvider: any ThemeProvider
    private let card: ExternalLinksCard
    private let homeMetricService: any HomeMetricServiceProtocol

    init(card: ExternalLinksCard, cardDelegate: (any HomescreenCardDelegate)?, homeMetricService: any HomeMetricServiceProtocol, themeProvider: any ThemeProvider) {
        self.cardDelegate = cardDelegate
        self.themeProvider = themeProvider
        self.card = card
        self.homeMetricService = homeMetricService
    }
    func buildView() -> HomescreenCardView {
        let rows = buildExternalLinkViews()
        let title = card.title
        return HomescreenCardView(title: title, subviews: rows, themeProvider: DefaultThemeProvider.shared)
    }
    private func buildExternalLinkViews() -> [StandardOutlineCellItemView] {
        card.links.enumerated().map { index, link in
            let icon = ICMAssets.homeExternalLinkIcon()!
            let accessories = imageAccessory(icon)
            let separator = card.links.count > 1 && index < card.links.count - 1
            let row = StandardOutlineCellItemView(frame: .zero, title: link.label, subtitle: nil, accessoryViews: accessories, showSeparator: separator, constrainAccessoriesToSize: CGSize(width: 16, height: 16), accessibilityLabel: link.label, themeProvider: DefaultThemeProvider.shared, action: { [weak self, link] in
                do {
                    let service = self?.homeMetricService
                    service?.addExternalLinkClickedMetric()
                }
                let delegate = self?.cardDelegate
                delegate?.navigate(to: .link(link.url))
            })
            return row
        }
    }
    private func imageAccessory(_ image: UIImage) -> [UIView] {
        let flipped = image.imageFlippedForRightToLeftLayoutDirection()
        let view = UIImageView(image: flipped)
        view.contentMode = .scaleAspectFit
        view.translatesAutoresizingMaskIntoConstraints = false
        view.tintColor = themeProvider.theme.primaryForeground
        return [view]
    }
}
