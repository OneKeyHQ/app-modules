import UIKit

protocol MessengerAppHomescreenCardDelegate: HomescreenCardDelegate {
    func displayAlertController(_ controller: UIAlertController)
    func dismissMessengerSheet()
}

@_objcRuntimeName(_TtC18IntercomSDKPrivate34MessengerAppHomescreenCardViewItem)
class MessengerAppHomescreenCardViewItem: HomescreenCardViewItem {
    private weak var messengerCardDelegate: (any MessengerAppHomescreenCardDelegate)?
    private let card: MessengerAppCard
    var messengerAppView: MessengerAppHomescreenView?

    init(card: MessengerAppCard, messengerCardDelegate: (any MessengerAppHomescreenCardDelegate)?) {
        self.messengerCardDelegate = messengerCardDelegate
        self.card = card
    }
    func buildView() -> HomescreenCardView {
        let alertDelegate = messengerCardDelegate
        let alert: (UIAlertController) -> Void = { [weak alertDelegate] controller in alertDelegate?.displayAlertController(controller) }
        let dismissDelegate = messengerCardDelegate
        let dismiss: () -> Void = { [weak dismissDelegate] in dismissDelegate?.dismissMessengerSheet() }
        let navigationDelegate = messengerCardDelegate
        let navigation: (HomescreenDestination) -> Void = { [weak navigationDelegate] destination in navigationDelegate?.navigate(to: destination) }
        let service = HelpCenterMetricService()
        let metric = ICMMetric.sharedInstance()
        let url = card.fallbackURL
        let provider = DefaultThemeProvider.shared
        let view = MessengerAppHomescreenView(for: url, presentAlertAction: alert, dismissMessengerSheetAction: dismiss, navigateAction: navigation, helpCenterMetricService: service, metricHandler: metric, themeProvider: provider)
        messengerAppView = view
        let title = card.title
        return HomescreenCardView(title: title, subviews: [view], themeProvider: provider)
    }
}
