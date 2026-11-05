// Source recovery modifications: see docs/ARTICLE_RECOVERY.md.
import Foundation

@_objcRuntimeName(_TtC18IntercomSDKPrivate22ArticleReactionService)
final class ArticleReactionService {
    let apiBridge: SwiftAPIBridge
    let config: any ICMConfigProtocol
    var articleTitle: String?
    var articleUpdatedAt: Date?
    var currentArticleID: String?
    var conversationCreatedCallback: ((String) -> Void)?

    init(apiBridge: SwiftAPIBridge = SwiftAPIBridge(), config: any ICMConfigProtocol = ICMConfig.shared()) {
        self.apiBridge = apiBridge
        self.config = config
    }
    func fetchArticleMetadata(articleID: String, completion: @escaping (String?) -> Void) {
        currentArticleID = articleID
        apiBridge.getArticle(articleID) { [weak self] result in
            guard let self else { return }
            switch result {
            case .success(let article):
                self.articleTitle = article.articleTitle
                self.articleUpdatedAt = article.updatedAt
                DispatchQueue.main.async { completion(article.relatedConversationId) }
            case .failure:
                DispatchQueue.main.async { completion(nil) }
            }
        }
    }
    func startConversation(forArticleID articleID: String, existingConversationId: String?, metricService: HelpCenterMetricService, onConversationCreated: @escaping (String) -> Void) {
        if let existingConversationId, let conversation = ICMDataManager.shared().findExistingConversation(withId: existingConversationId) {
            ICMPresentationManager.sharedInstance().showConversation(conversation)
            return
        }
        conversationCreatedCallback = onConversationCreated
        NotificationCenter.default.addObserver(self, selector: #selector(conversationCreated(_:)), name: .IntercomDidStartNewConversation, object: nil)
        let conversation = ICMConversation()
        let metadata = ArticleMetaData(id: articleID, source: nil, title: articleTitle, createdAt: articleUpdatedAt)
        conversation.articleMetaData = metadata
        conversation.conversationId = existingConversationId
        ICMPresentationManager.sharedInstance().showConversation(conversation)
        metricService.addNewConversationFromArticleEvent(accessToTeammate: config.accessToTeammateEnabled)
    }
    @objc(conversationCreated:)
    func conversationCreated(_ notification: Notification) {
        guard let conversation = notification.object as? ICMConversation else { return }
        guard let part = (conversation.conversationParts! as! [ICMConversationPart]).first else { return }
        guard let block = part.blocks!.first as? ITBLinkBlock else { return }
        guard block.articleId == currentArticleID, let identifier = conversation.conversationId else { return }
        NotificationCenter.default.removeObserver(self, name: .IntercomDidStartNewConversation, object: nil)
        DispatchQueue.main.async { [weak self] in
            let callback = self?.conversationCreatedCallback
            callback?(identifier)
            self?.conversationCreatedCallback = nil
        }
    }

    // The third original protocol requirement was erased; this is its observed concrete callback body.
    func react(_ reaction: FeedbackReaction, articleID: String, contentID: String?, metricService: HelpCenterMetricService) {
        let index = reaction == .disappointed ? 2 : reaction == .neutral ? 1 : 0
        ICMHTTPClient.react(toArticle: articleID, articleContentId: contentID, withReactionIndex: UInt(index), allowAutoResponses: false, articleSource: metricService.contextIsSearchBrowse ? "search_browse" : nil)
        ICMFeedbackGenerator.feedback(withType: 0)
        switch reaction {
        case .disappointed: metricService.addSadReactionEvent(conversationOptionShown: config.canStartConversation())
        case .neutral: metricService.addNeutralReactionEvent()
        case .happy: metricService.addHappyReactionEvent()
        }
    }
}
