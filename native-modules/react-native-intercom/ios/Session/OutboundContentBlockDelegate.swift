import UIKit
import AVKit

// The three names are descriptive; the original raw values and branches survive.
@objc enum OutboundContentContext: Int { case carousel = 0, survey = 1, post = 2 }

// Original Objective-C protocol metadata has zero requirements and no parents.
@objc(_TtP18IntercomSDKPrivate12AutoMockable_)
protocol AutoMockable {}

@objc(_TtC18IntercomSDKPrivate28OutboundContentBlockDelegate)
class OutboundContentBlockDelegate: NSObject, ConversationCellBlockDelegate, AutoMockable {
    @objc var onShouldDismiss: (() -> Void)?
    private let context: OutboundContentContext
    private let helpCenterMetricService: any HelpCenterMetricServiceProtocol
    private let isHelpCenterLink: (URL) -> Bool
    private let isCustomSchemeURL: (URL) -> Bool
    private weak var viewController: UIViewController?
    private let previewHelper: PreviewHelper
    private let extractArticleId: ([AnyHashable: Any]) -> String?
    private let presentArticle: (String) -> Void
    private let presentSheet: (ICMMessengerSheetViewController) -> Void
    private let metricHandler: (any ICMMetricProtocol)?
    private var statusObservation: NSKeyValueObservation?
    private var currentPlayer: AVPlayer?

    @objc convenience init(context: OutboundContentContext) {
        self.init(context: context, helpCenterMetricService: HelpCenterMetricService(), isHelpCenterLink: { LinkOpener.shared.isHelpCenterLink($0) }, isCustomSchemeURL: Self.defaultIsCustomSchemeURL, viewController: nil, previewHelper: Self.defaultPreviewHelper(), extractArticleId: { ICMUtils.extractArticleId(fromSheetParams: $0) }, presentArticle: Self.defaultPresentArticle, presentSheet: { ICMPresentationManager.sharedInstance().presentSheet($0) }, metricHandler: ICMMetric.sharedInstance())
    }
    @objc convenience init(context: OutboundContentContext, viewController: UIViewController) {
        self.init(context: context, helpCenterMetricService: HelpCenterMetricService(), isHelpCenterLink: { LinkOpener.shared.isHelpCenterLink($0) }, isCustomSchemeURL: Self.defaultIsCustomSchemeURL, viewController: viewController, previewHelper: Self.defaultPreviewHelper(), extractArticleId: { ICMUtils.extractArticleId(fromSheetParams: $0) }, presentArticle: Self.defaultPresentArticle, presentSheet: { ICMPresentationManager.sharedInstance().presentSheet($0) }, metricHandler: ICMMetric.sharedInstance())
    }
    init(context: OutboundContentContext, helpCenterMetricService: any HelpCenterMetricServiceProtocol, isHelpCenterLink: @escaping (URL) -> Bool, isCustomSchemeURL: @escaping (URL) -> Bool, viewController: UIViewController?, previewHelper: PreviewHelper, extractArticleId: @escaping ([AnyHashable: Any]) -> String?, presentArticle: @escaping (String) -> Void, presentSheet: @escaping (ICMMessengerSheetViewController) -> Void, metricHandler: (any ICMMetricProtocol)?) {
        self.context = context
        self.helpCenterMetricService = helpCenterMetricService
        self.isHelpCenterLink = isHelpCenterLink
        self.isCustomSchemeURL = isCustomSchemeURL
        self.viewController = viewController
        self.previewHelper = previewHelper
        self.extractArticleId = extractArticleId
        self.presentArticle = presentArticle
        self.presentSheet = presentSheet
        self.metricHandler = metricHandler
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    private static func defaultPreviewHelper() -> PreviewHelper {
        let queue = AsyncQueue.main
        let manager = AttachmentFileManager(fileManager: .default, urlSession: URLSession.shared, callbackQueue: .main)
        return PreviewHelper(mainQueue: queue, attachmentFileManager: manager, onFailure: nil)
    }
    private static func defaultIsCustomSchemeURL(_ url: URL) -> Bool {
        if LinkOpener.shared.isCustomSchemeURL(url) { return true }
        guard let extracted = LinkOpener.shared.extractURLFromLinkTrackingURL(url) else { return false }
        return LinkOpener.shared.isCustomSchemeURL(extracted)
    }
    private static func defaultPresentArticle(_ articleId: String) {
        ICMPresentationManager.sharedInstance().presentArticle(articleId, allowReactions: true, anchorLink: nil)
    }
    private func openedHelpCenterMetric() {
        switch context {
        case .carousel: HelpCenterMetricService.addOpenedFromCarousel()
        case .survey: HelpCenterMetricService.addOpenedFromSurvey()
        case .post: HelpCenterMetricService.addOpenedFromPost()
        }
    }
    func openArticle(articleId: String) { Self.defaultPresentArticle(articleId) }
    func open(url: URL) {
        let custom = isCustomSchemeURL(url)
        LinkOpener.shared.openLink(url)
        if isHelpCenterLink(url) { openedHelpCenterMetric() }
        if custom { onShouldDismiss?() }
    }
    func presentAlert(_ alert: UIAlertController) { viewController?.present(alert, animated: true, completion: nil) }
    func dismissAppSheet() { viewController?.dismiss(animated: true, completion: nil) }
    func getViewControllerForModalPresentation() -> UIViewController? { viewController }

    func openAppSheet(with url: URL, params: [AnyHashable: Any], delegate: (any ICMMessengerSheetViewControllerDelegate)?) {
        if let articleId = extractArticleId(params) {
            presentArticle(articleId)
            openedHelpCenterMetric()
            return
        }
        guard let controller = ICMMessengerSheetViewController(sheetURL: url, params: params, delegate: delegate) else { return }
        presentSheet(controller)
        logSheetViewedMetric(url: url)
    }
    private func logSheetViewedMetric(url: URL) {
        guard context == .post else { return }
        let event = ICMMetricEvent(action: "viewed", object: "messenger_sheet", place: "in_app", context: "from_full", additionalInfo: ["url": url.absoluteString])
        metricHandler?.addEvent(event)
    }
    func logSheetClosedMetric(url: URL) {
        guard context == .post else { return }
        let event = ICMMetricEvent(action: "closed", object: "messenger_sheet", place: "in_app", context: "from_full", additionalInfo: ["url": url.absoluteString])
        metricHandler?.addEvent(event)
    }
    func presentMessengerAppInputAlert(with payload: MessengerAppTextInputPayload) {
        guard let presenter = viewController else { return }
        let alert = UIAlertController(title: payload.title, message: "", preferredStyle: .alert)
        alert.addTextField { field in field.text = payload.currentText; field.placeholder = payload.placeholder }
        let ok = UIAlertAction(title: TranslationManager.messenger.translation(forKey: "ok"), style: .default) { _ in payload.completionBlock?(alert) }
        let cancel = UIAlertAction(title: TranslationManager.messenger.translation(forKey: "cancel"), style: .cancel, handler: nil)
        alert.addAction(ok)
        alert.addAction(cancel)
        presenter.present(alert, animated: true, completion: nil)
    }
    func logMessengerAppMetricEvent(_ payload: [String: Any]) {
        guard let action = payload["action"] as? String, let object = payload["object"] as? String, let place = payload["place"] as? String else { return }
        let metadata = payload["metadata"] as? [String: Any]
        let event = ICMMetricEvent(action: action, object: object, place: place, context: "conversation", additionalInfo: metadata)
        metricHandler?.addEvent(event)
    }

    func playVideo(file: ITBVideoFileBlock) {
        guard let presenter = viewController else { return }
        let string = String._unconditionallyBridgeFromObjectiveC(file.url as NSString?)
        guard let url = URL(string: string) else { return }
        cleanupVideoPlayback()
        let player = AVPlayer(url: url)
        currentPlayer = player
        let controller = AVPlayerViewController()
        controller.player = player
        if let item = player.currentItem {
            statusObservation = item.observe(\.status, options: [.new]) { [weak self, url] item, _ in
                if item.status == .failed, let error = item.error {
                    self?.trackVideoPlaybackFailure(url: url, error: error)
                    self?.cleanupVideoPlayback()
                } else if item.status == .readyToPlay { self?.cleanupVideoPlayback() }
            }
        }
        presenter.present(controller, animated: true) { player.play() }
    }
    private func cleanupVideoPlayback() {
        statusObservation?.invalidate()
        statusObservation = nil
        currentPlayer = nil
    }
    private func trackVideoPlaybackFailure(url: URL, error: Error) {
        let error = error as NSError
        let event = ICMMetricEvent(action: "failed", object: "video_playback", place: "in_app", context: "conversation", additionalInfo: ["error_domain": error.domain, "error_code": error.code, "url_host": url.host ?? "unknown"])
        metricHandler?.addEvent(event)
    }
    func imageTapped(with block: ITBImageBlock) {
        guard let presenter = viewController else { return }
        let remoteURL = URL(string: block.url ?? "")
        let url: URL?
        if let upload = block.upload { let fileURL: URL = upload.fileURL; url = fileURL }
        else { url = remoteURL }
        guard let resolved = url else { return }
        Task { @MainActor [self, resolved, presenter] in
            previewHelper.preview(urls: [resolved], action: nil, from: presenter)
        }
    }
    func imageTapped(with image: UIImage, title: String?) {
        guard let presenter = viewController else { return }
        Task { @MainActor [self, image, title, presenter] in
            previewHelper.preview(image: image, title: title, from: presenter)
        }
    }

    // These eighteen exact original witness entries point to the same literal ret.
    func show(conversation: ICMConversation) {}
    func reload(content: ITBBlock) {}
    func showTicketDetails() {}
    func openHelpCenter() {}
    func createTicketTapped(for ticketType: TicketType) {}
    func openPost() {}
    func rateConversation(_ rating: UInt, conversationPart: ICMConversationPart) {}
    func addRemarkToConversation(_ remark: String, conversationPart: ICMConversationPart) {}
    func quickReplyTapped(replyOption: ReplyOption, quickReplyPartId: String?, conversationPart: ICMConversationPart) {}
    func retryTapped(conversationPart: ICMConversationPart, sender: UIView) {}
    func reloadCell(with content: ContentType) {}
    func invalidateLayoutForPart(with partId: String) {}
    func invalidateLayoutFor(block: ITBBlock) {}
    func submitButtonTapped(_ attribute: FormAttribute, conversationPart: ICMConversationPart, successBlock: @escaping () -> Void, failureBlock: @escaping (Error) -> Void) {}
    func cacheMessengerAppBlockView(_ view: MessengerAppBlockView?, id: String) {}
    func cacheVideoBlockView(_ view: VideoBlockView?, id: String) {}
    func citationsTapped(_ citations: [ITBInlineCitation], from sources: [FinAnswerSourceWrapper]) {}
    func attachmentTapped(with block: ITBAttachmentBlock) {}
    func getMessengerAppBlockView(for id: String) -> MessengerAppBlockView? { nil }
    func getVideoBlockView(for id: String) -> VideoBlockView? { nil }
    func shouldApplyCardStyleToMessengerApps() -> Bool { false }
}
