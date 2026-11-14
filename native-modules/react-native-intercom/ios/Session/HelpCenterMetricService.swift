import Foundation

let helpCenterSharedMetric: ICMMetric? = ICMMetric.sharedInstance()

// Six original method requirements were WMO-deleted without recoverable names.
// This declaration covers the 25 named event methods and the observed flag only.
protocol HelpCenterMetricServiceProtocol {
    var contextIsSearchBrowse: Bool { get set }
    func addArticleViewedEvent()
    func addArticleFailedEvent(errorCode: NSNumber)
    func addSuggestedArticlesPresentedToUserEvent(articlesShown: [String], numberOfArticles: Int)
    func addHappyReactionEvent()
    func addNeutralReactionEvent()
    func addSadReactionEvent(conversationOptionShown: Bool)
    func addSuggestedArticleViewedEvent(articleViewed: String)
    func addCollectionListViewedEvent(filteredHelpCenter: Bool)
    func addArticleListViewedEvent(filteredHelpCenter: Bool)
    func addCollectionListFailedEvent(errorCode: Int)
    func addArticleListFailedEvent(errorCode: Int)
    func addSearchResultsFailedEvent(errorCode: Int)
    func addFetchCollectionsDataRequestedEvent()
    func addFetchCollectionContentDataRequestedEvent()
    func addSearchResultsDataRequestedEvent()
    func addFetchCollectionsDataFailedEvent(errorCode: Int)
    func addFetchCollectionContentDataFailedEvent(errorCode: Int)
    func addSearchEvent()
    func addClickSearchEvent(searchQuery: String)
    func addNewConversationFromArticleEvent(accessToTeammate: Bool)
    func addNewConversationFromCollectionListEvent(accessToTeammate: Bool)
    func addNewConversationFromArticleListEvent(accessToTeammate: Bool)
    func addNewConversationFromSearchEvent(accessToTeammate: Bool)
    func addSearchBrowseOpenedEvents()
    func addHelpViewedMetric()
}

@objc(_TtC18IntercomSDKPrivate23HelpCenterMetricService)
class HelpCenterMetricService: NSObject, HelpCenterMetricServiceProtocol {
    @objc var contextIsSearchBrowse = false
    override init() { super.init() }

    private enum MetaData {
        case errorCode(NSNumber), conversationShown(Bool), filtered(Bool), homeScreenIndex(NSNumber)
        case articlesShown([String]), numberOfArticles(Int), articleViewed(String), searchQuery(String)
        case ticketTypeId(Int), conversationId(String), ticketStatus(String)
        var pair: (String, Any) {
            switch self {
            case .errorCode(let v): return ("error_code", v)
            case .conversationShown(let v): return ("conversation_shown", v)
            case .filtered(let v): return ("filtered", v)
            case .homeScreenIndex(let v): return ("home_screen_index", v)
            case .articlesShown(let v): return ("articles_shown", v)
            case .numberOfArticles(let v): return ("articles_count", v)
            case .articleViewed(let v): return ("article_id", v)
            case .searchQuery(let v): return ("search_query", v)
            case .ticketTypeId(let v): return ("ticket_type_id", v)
            case .conversationId(let v): return ("conversation_id", v)
            case .ticketStatus(let v): return ("ticket_status", v)
            }
        }
    }
    private func add(action: String, object: String, place: String, context: String = "no_context", metadata: [MetaData]? = nil, type: UInt? = nil) {
        let info = metadata.map { entries -> [AnyHashable: Any] in
            var result: [AnyHashable: Any] = [:]
            for entry in entries { let (key, value) = entry.pair; result[key] = value }
            return result
        }
        let event = ICMMetricEvent(action: action, object: object, place: place, context: context, additionalInfo: info)
        event?.eventType = type ?? (contextIsSearchBrowse ? 3 : 1)
        helpCenterSharedMetric?.addEvent(event)
    }
    private class func opened(place: String, context: String = "no_context") {
        let event = ICMMetricEvent(action: "opened", object: "help_center", place: place, context: context, additionalInfo: nil)
        event?.eventType = 1
        helpCenterSharedMetric?.addEvent(event)
    }

    @objc class func addOpenedFromLegacySearchAppEvent() { opened(place: "legacy_article_search_app") }
    @objc class func addOpenedFromMessengerHomeAppEvent() { opened(place: "messenger_home_app") }
    @objc class func addOpenedFromAPIEvent() { opened(place: "api") }
    @objc class func addOpenedArticleFromAPIEvent() { opened(place: "api", context: "article") }
    @objc class func addOpenedFromConversationEvent() { opened(place: "conversation") }
    @objc class func addOpenedFromConversationMessengerAppEvent() { opened(place: "conversation") }
    @objc class func addOpenedFromInAppEvent() { opened(place: "in_app") }
    @objc class func addOpenedFromPost() { opened(place: "in_app") }
    @objc class func addOpenedFromCarousel() { opened(place: "carousel_message") }
    @objc class func addOpenedFromPushNotification() { opened(place: "push") }
    @objc class func addOpenedFromSurvey() { opened(place: "survey") }

    @objc func addArticleViewedEvent() { add(action: "viewed", object: "help_center", place: "article") }
    @objc func addArticleFailedEvent(errorCode: NSNumber) { add(action: "failed", object: "help_center", place: "article", metadata: [.errorCode(errorCode)]) }
    func addSuggestedArticlesPresentedToUserEvent(articlesShown: [String], numberOfArticles: Int) { add(action: "viewed", object: "help_center", place: "home", metadata: [.articlesShown(articlesShown), .numberOfArticles(numberOfArticles)]) }
    @objc func addHappyReactionEvent() { add(action: "sent", object: "article_reaction", place: "article", context: "reaction_happy") }
    @objc func addNeutralReactionEvent() { add(action: "sent", object: "article_reaction", place: "article", context: "reaction_neutral") }
    @objc func addSadReactionEvent(conversationOptionShown: Bool) { add(action: "sent", object: "article_reaction", place: "article", context: "reaction_sad", metadata: [.conversationShown(conversationOptionShown)]) }
    func addSuggestedArticleViewedEvent(articleViewed: String) { add(action: "viewed", object: "search_browse_card", place: "home", context: "messenger", metadata: [.articleViewed(articleViewed)]) }
    func addCollectionListViewedEvent(filteredHelpCenter: Bool) { add(action: "viewed", object: "help_center", place: "collection_list", metadata: [.filtered(filteredHelpCenter)]) }
    func addArticleListViewedEvent(filteredHelpCenter: Bool) { add(action: "viewed", object: "help_center", place: "article_list", metadata: [.filtered(filteredHelpCenter)]) }
    func addCollectionListFailedEvent(errorCode: Int) { add(action: "failed", object: "help_center", place: "collection_list", metadata: [.errorCode(NSNumber(value: errorCode))]) }
    func addArticleListFailedEvent(errorCode: Int) { add(action: "failed", object: "help_center", place: "article_list", metadata: [.errorCode(NSNumber(value: errorCode))]) }
    func addSearchResultsFailedEvent(errorCode: Int) { add(action: "failed", object: "help_center", place: "search_results", metadata: [.errorCode(NSNumber(value: errorCode))]) }
    func addFetchCollectionsDataRequestedEvent() { add(action: "requested", object: "help_center_data", place: "collection_list") }
    func addFetchCollectionContentDataRequestedEvent() { add(action: "requested", object: "help_center_data", place: "article_list") }
    func addSearchResultsDataRequestedEvent() { add(action: "requested", object: "help_center_data", place: "search_results") }
    func addFetchCollectionsDataFailedEvent(errorCode: Int) { add(action: "failed", object: "help_center_data", place: "collection_list", metadata: [.errorCode(NSNumber(value: errorCode))]) }
    func addFetchCollectionContentDataFailedEvent(errorCode: Int) { add(action: "failed", object: "help_center_data", place: "article_list", metadata: [.errorCode(NSNumber(value: errorCode))]) }
    func addSearchEvent() { add(action: "searched", object: "help_center", place: "search_results") }
    func addClickSearchEvent(searchQuery: String) { add(action: "clicked", object: "help_center", place: "search_results", metadata: [.searchQuery(searchQuery)]) }
    private func newConversation(place: String, accessToTeammate: Bool) { add(action: "clicked", object: "new_conversation", place: place, context: accessToTeammate ? "style_human" : "style_bot") }
    @objc func addNewConversationFromArticleEvent(accessToTeammate: Bool) { newConversation(place: "article", accessToTeammate: accessToTeammate) }
    func addNewConversationFromCollectionListEvent(accessToTeammate: Bool) { newConversation(place: "collection_list", accessToTeammate: accessToTeammate) }
    func addNewConversationFromArticleListEvent(accessToTeammate: Bool) { newConversation(place: "article_list", accessToTeammate: accessToTeammate) }
    func addNewConversationFromSearchEvent(accessToTeammate: Bool) { newConversation(place: "search_results", accessToTeammate: accessToTeammate) }
    func addSearchBrowseOpenedEvents() {
        add(action: "clicked", object: "search_browse_card_input", place: "home", context: "messenger")
        add(action: "opened", object: "help_center", place: "search_browse", context: "no_context")
    }
    func addHelpViewedMetric() { add(action: "viewed", object: "help", place: "messenger", type: 0) }
}
