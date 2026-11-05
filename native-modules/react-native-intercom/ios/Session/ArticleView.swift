import SwiftUI

struct ArticleView: View {
    let articleID: String
    let metricService: HelpCenterMetricService
    let allowsReactions: Bool
    let reactionService: ArticleReactionService
    let initialAnchorLink: String?
    let requestBuilder = ArticleRequestBuilder()
    @Environment(\.colorScheme) private var colorScheme
    @State private var _articleContentId: String?
    @State private var _articleTitle: String?
    @AccessibilityFocusState private var isTitleFocused: Bool
    @State private var _loadingState: LoadingState = .loading
    @State private var _navigationDelegate = ScrollDetectingNavigationDelegate(revealTimeout: nil)
    @State private var _reloadTrigger = UUID()
    @State private var _relatedConversationId: String?
    @State private var _webViewContentHeight: CGFloat = 0
    @State private var _pendingAnchorScroll: String?
    @State private var _initialAnchorMissed = false
    enum LoadingState: Equatable { case error(String), loading, loaded }
    var body: some View {
        ZStack(alignment: .center) {
            Color(uiColor: DefaultThemeProvider.shared.theme.base).ignoresSafeArea(.all, edges: .all)
            VStack(alignment: .center, spacing: 0) {
                if let title = _articleTitle {
                    Color.clear.frame(height: 1, alignment: .center)
                        .accessibilityElement(children: .ignore).accessibilityLabel(title).accessibilityAddTraits(.isHeader)
                        .accessibilityFocused($isTitleFocused)
                        .onChange(of: isTitleFocused) { value in if !value { _articleTitle = nil } }
                }
                ScrollView(.vertical, showsIndicators: true) {
                    WebView(request: requestBuilder.injectedArticleRequest(articleID: articleID, theme: colorScheme == .dark ? "dark" : "light"), navigationDelegate: _navigationDelegate, contentHeight: $_webViewContentHeight, anchorToScroll: $_pendingAnchorScroll, onAnchorNotFound: { anchor in
                        if let initialAnchorLink, anchor == initialAnchorLink { _initialAnchorMissed = true }
                    }).id(_reloadTrigger).frame(minHeight: _webViewContentHeight, alignment: .center)
                        .opacity(_loadingState == .loaded ? 1 : 0).onAppear(perform: setupCallbacks)
                }
                if allowsReactions, _loadingState == .loaded, _articleContentId != nil {
                    VStack(alignment: .center, spacing: 0) {
                        Divider().frame(maxWidth: .infinity, alignment: .center)
                        ReactionView(onReactionSelected: { reaction in
                            reactionService.react(reaction, articleID: articleID, contentID: _articleContentId, metricService: metricService)
                        }, onStartConversation: {
                            reactionService.startConversation(forArticleID: articleID, existingConversationId: _relatedConversationId, metricService: metricService) { _relatedConversationId = $0 }
                        }, config: ICMConfig.shared(), existingConversationId: _relatedConversationId, themeProvider: DefaultThemeProvider.shared)
                    }
                }
            }
            if _loadingState == .loading {
                Color.clear.overlay(ArticleLoadingView(), alignment: .center).onDisappear {
                    if _loadingState == .loaded { isTitleFocused = true }
                }
            }
            if case .error(let message) = _loadingState {
                Color.clear.overlay(ArticleErrorView(message: message, onRetry: {
                    _loadingState = .loading
                    _reloadTrigger = UUID()
                }), alignment: .center)
            }
        }
    }
    private func setupCallbacks() {
        _navigationDelegate.onDidStart = {
            _webViewContentHeight = 0
            _articleTitle = nil
            _initialAnchorMissed = false
            _loadingState = .loading
        }
        _navigationDelegate.onDidFinish = { contentId in
            _articleContentId = contentId
            if _initialAnchorMissed, let initialAnchorLink {
                _initialAnchorMissed = false
                _pendingAnchorScroll = initialAnchorLink
            }
        }
        _navigationDelegate.onContentReady = { title in
            _articleTitle = title
            if let initialAnchorLink { _pendingAnchorScroll = initialAnchorLink }
            withAnimation(.easeOut(duration: 0.2)) { _loadingState = .loaded }
        }
        _navigationDelegate.onDidFail = { error in
            _loadingState = .error(TranslationManager.helpCenter.translation(forKey: "spaces_error_description"))
            metricService.addArticleFailedEvent(errorCode: NSNumber(value: (error as NSError).code))
        }
        _navigationDelegate.onAnchorLinkTapped = { _pendingAnchorScroll = $0 }
        addArticleMetric()
        reactionService.fetchArticleMetadata(articleID: articleID) { _relatedConversationId = $0 }
    }
    private func addArticleMetric() {
        let event = ICMMetricEvent(action: "viewed", object: "article", place: "mobile_sdk", context: "from_help_center_webview", additionalInfo: ["source": "programmatic"])
        event?.eventType = 1
        ICMMetric.sharedInstance()?.addEvent(event)
    }
}
