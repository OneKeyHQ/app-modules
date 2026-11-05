// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import UIKit

// Thirty-three named original requirements. Three WMO-deleted private
// requirements have no recoverable signature and remain outside this declaration.
protocol ConversationCellBlockDelegate: AnyObject {
    func show(conversation: ICMConversation)
    func showTicketDetails()
    func openPost()
    func quickReplyTapped(replyOption: ReplyOption, quickReplyPartId: String?, conversationPart: ICMConversationPart)
    func retryTapped(conversationPart: ICMConversationPart, sender: UIView)
    func invalidateLayoutForPart(with partId: String)
    func submitButtonTapped(_ attribute: FormAttribute, conversationPart: ICMConversationPart, successBlock: @escaping () -> Void, failureBlock: @escaping (Error) -> Void)
    func getViewControllerForModalPresentation() -> UIViewController?
    func getMessengerAppBlockView(for id: String) -> MessengerAppBlockView?
    func cacheMessengerAppBlockView(_ view: MessengerAppBlockView?, id: String)
    func getVideoBlockView(for id: String) -> VideoBlockView?
    func cacheVideoBlockView(_ view: VideoBlockView?, id: String)
    func citationsTapped(_ citations: [ITBInlineCitation], from sources: [FinAnswerSourceWrapper])
    func imageTapped(with block: ITBImageBlock)
    func imageTapped(with image: UIImage, title: String?)
    func playVideo(file: ITBVideoFileBlock)
    func invalidateLayoutFor(block: ITBBlock)
    func presentMessengerAppInputAlert(with payload: MessengerAppTextInputPayload)
    func reloadCell(with content: ContentType)
    func openAppSheet(with url: URL, params: [AnyHashable: Any], delegate: (any ICMMessengerSheetViewControllerDelegate)?)
    func dismissAppSheet()
    func shouldApplyCardStyleToMessengerApps() -> Bool
    func logMessengerAppMetricEvent(_ event: [String: Any])
    func logSheetClosedMetric(url: URL)
    func openHelpCenter()
    func openArticle(articleId: String)
    func reload(content: ITBBlock)
    func attachmentTapped(with block: ITBAttachmentBlock)
    func open(url: URL)
    func createTicketTapped(for ticketType: TicketType)
    func presentAlert(_ alert: UIAlertController)
    func rateConversation(_ rating: UInt, conversationPart: ICMConversationPart)
    func addRemarkToConversation(_ remark: String, conversationPart: ICMConversationPart)
}
