// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import UIKit
import AVKit

@objc(_TtC18IntercomSDKPrivate21ConversationViewModel)
class ConversationViewModel: NSObject {
    var stateSyncScheduler: DefaultScheduler
    private(set) var cells: [ConversationCellType] = []
    var finStreamingPartIndexTracker = 0
    var disableAutoScrolling = false
    var conversation: ICMConversation?
    var didTrackOpenInteraction = false
    let openCallService: any OpenCallServiceProtocol
    var dismissPrivacyPolicyButtonTapped = false
    var attributeCollectorIsFirstResponder = false
    // The original lazy backing starts nil. Its real fetcher getter is a separately tracked dependency.
    private var gifFetcherStorage: ((String, @escaping (Result<[[AnyHashable: Any]], Error>) -> Void) -> Void)?
    var typingIndicatorDetails: TypingAdminDetails?
    var showTypingIndicator = false
    var showThinkingIndicator = false
    var userLastScrolledToBottom = Date.distantPast {
        didSet {
            guard let currentView = view else { return }
            let button = currentView.scrollDownButton
            withExtendedLifetime(currentView) {
                button.unreadCount = currentView.viewModel!.unreadParts.count
            }
        }
    }
    private let scheduler: DefaultScheduler
    weak var view: ConversationViewController?
    private var audioRecorderViewModel: AudioRecorderViewModel?
    let interactor: ConversationInteractor
    let notificationCenter: any NotificationCenterInterface
    private let config: any ICMConfigProtocol
    private let queue: any Dispatcher
    private let identityStore: ICMIdentityStore
    private let messengerAppViewCache: MessengerAppViewCache
    private let videoBlockViewCache: VideoBlockViewCache
    private let metricsManager: (any ICMMetricProtocol)?
    private let audioRecorderFactory: () -> any AudioRecording
    var failedPart: ICMConversationPart?
    private var partIdsWithMetricsAdded: [String] = []
    private var conversationViewedMetricTracked = false
    private var lastSentIsTypingEventTimestamp: Date?
    private var needsViewUpdate = false
    private let applicationState: any ApplicationStateProviding
    private let userDefaultsManager: UserDefaultsManager
    private let themeProvider: any ThemeProvider
    private let persistenceStore: any ICMPersistentStoreProtocol.Type
    private let soundPlayer: any ICMSoundPlayerProtocol.Type

    init(view: ConversationViewController, interactor: ConversationInteractor,
         notificationCenter: any NotificationCenterInterface, config: any ICMConfigProtocol,
         queue: any Dispatcher, identityStore: ICMIdentityStore,
         metricsManager: (any ICMMetricProtocol)?, messengerAppViewCache: MessengerAppViewCache,
         videoBlockViewCache: VideoBlockViewCache, userDefaultsManager: UserDefaultsManager,
         stateSyncScheduler: DefaultScheduler, themeProvider: any ThemeProvider,
         persistenceStore: any ICMPersistentStoreProtocol.Type, soundPlayer: any ICMSoundPlayerProtocol.Type,
         scheduler: DefaultScheduler, audioRecorderFactory: @escaping () -> any AudioRecording,
         openCallService: any OpenCallServiceProtocol, applicationState: any ApplicationStateProviding) {
        self.view = view
        self.interactor = interactor
        self.notificationCenter = notificationCenter
        self.config = config
        self.queue = queue
        self.identityStore = identityStore
        self.metricsManager = metricsManager
        self.messengerAppViewCache = messengerAppViewCache
        self.videoBlockViewCache = videoBlockViewCache
        self.userDefaultsManager = userDefaultsManager
        self.stateSyncScheduler = stateSyncScheduler
        self.themeProvider = themeProvider
        self.persistenceStore = persistenceStore
        self.soundPlayer = soundPlayer
        self.scheduler = scheduler
        self.audioRecorderFactory = audioRecorderFactory
        self.openCallService = openCallService
        self.applicationState = applicationState
        super.init()
        setAdminTypingBlocksOnInteractor()
        interactor.finDidStreamPart = { [weak self] blocks, index in self?.finDidStream(blocks: blocks, with: index) }
        interactor.finDidEndStreaming = { [weak self] in self?.resetFinStreamingPartsIfPresent() }
        setupStateSyncing()
    }

    override init() { fatalError("init() has not been implemented") }

    deinit {
        resetFinStreamingPartsIfPresent()
        stateSyncScheduler.invalidate()
        audioRecorderViewModel = nil
    }

    func preventAutoScrolling() {
        guard let conversation else { disableAutoScrolling = false; return }
        let parts = conversation.conversationParts! as! [ICMConversationPart]
        var streaming = false
        for part in parts {
            if part.isStreamingPart { streaming = true; break }
        }
        disableAutoScrolling = streaming
    }

    var unreadParts: [ICMConversationPart] {
        if let currentView = view {
            let visible = currentView.mostRecentCellIsVisible
            if visible == nil || visible == true { return [] }
        }
        guard let conversation else { return [] }
        let parts = conversation.conversationParts! as! [ICMConversationPart]
        var result: [ICMConversationPart] = []
        for part in parts {
            if part.isPersonalPart() { continue }
            let createdAt = part.createdAt!
            if createdAt > userLastScrolledToBottom { result.append(part) }
        }
        return result
    }

    func dismissAppSheet() {
        view?.dismiss(animated: true, completion: nil)
        view?.updateComposer()
    }

    func updateFooterViews() {
        if let poweredBy = interactor.poweredByForConversation {
            view?.intercomLinkView.updateDetails(with: poweredBy)
        }
        if let notice = interactor.dataManager.privacyPolicyNotice {
            let paragraph = notice.paragraph
            view?.privacyPolicyView.setPrivacyPolicyText(paragraph)
        }
        view?.updateFooterViewVisibility(fromKeyboardNotification: false)
    }

    func cacheMessengerAppBlockView(_ blockView: MessengerAppBlockView?, id: String) {
        messengerAppViewCache.cache(blockView, id: id)
    }
    func getMessengerAppBlockView(for id: String) -> MessengerAppBlockView? {
        messengerAppViewCache.view(for: id)
    }
    func cacheVideoBlockView(_ blockView: VideoBlockView?, id: String) {
        videoBlockViewCache.cache(blockView, id: id)
    }
    func getVideoBlockView(for id: String) -> VideoBlockView? {
        videoBlockViewCache.view(for: id)
    }

    func viewDidLoad() {
        guard let currentView = view else { return }
        withExtendedLifetime(currentView) {
            startListeningForNotifications()
            switch openCallService.state {
            case .loading: currentView.showLoading(true)
            case .failure:
                currentView.showLoading(false)
                currentView.showErrorView(true)
            case .success:
                let existing: ICMConversation?
                if let id = interactor.conversationId {
                    existing = interactor.dataManager.findExistingConversation(withId: id)
                } else { existing = interactor.undeliveredConversation }
                currentView.showLoading(existing != nil)
                setupConversation()
            @unknown default: fatalError("Unexpected OpenCallService state")
            }
            interactor.updateWithExistingConversationIfNeeded()
        }
    }

    func viewWillAppear() {
        startListeningForNotifications()
        flushPendingViewUpdate()
        guard let currentView = view else { return }
        let current = conversation
        let fallback = interactor.dataManager.fallbackHeader()
        let header = current?.headerToDisplay() ?? fallback
        guard let header else { return }
        withExtendedLifetime(currentView) { currentView.navigationBarManager.updateTitleViewWith(header: header) }
    }

    func startListeningForNotifications() {
        notificationCenter.addObserver(self, selector: #selector(openCallStateDidChange(_:)),
            name: Notification.Name("IntercomOpenCallStateDidChangeNotification"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(inboxUpdated),
            name: Notification.Name("intercom-sdk-inbox-updated"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(updateViewComposerStatus),
            name: Notification.Name("IntercomModalViewControllerWillShowNotification"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(updateViewComposerStatus),
            name: Notification.Name("IntercomModalViewControllerWillHideNotification"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(updateViewComposerStatus),
            name: Notification.Name("IntercomModalViewControllerDidHideNotification"), object: nil)
        notificationCenter.addObserver(self, selector: #selector(appDidBecomeActive),
            name: UIApplication.didBecomeActiveNotification, object: nil)
    }

    @objc func updateViewComposerStatus() { view?.updateComposer() }
    @objc func appDidBecomeActive() { flushPendingViewUpdate() }

    @objc func openCallStateDidChange(_ notification: Notification) {
        switch openCallService.state {
        case .loading: return
        case .success: setupConversation()
        case .failure:
            view?.showLoading(false)
            view?.showErrorView(true)
        @unknown default: fatalError("Unexpected OpenCallService state")
        }
    }

    private func flushPendingViewUpdate() {
        guard needsViewUpdate else { return }
        let current: ICMConversation?
        if let id = interactor.conversationId {
            current = interactor.dataManager.findExistingConversation(withId: id)
        } else { current = interactor.undeliveredConversation }
        guard let current else { return }
        needsViewUpdate = false
        performViewUpdate(with: current)
        reloadCreateTicketViews()
        reloadLastMessageGroup()
    }

    @objc func inboxUpdated() {
        let current: ICMConversation?
        if let id = interactor.conversationId {
            current = interactor.dataManager.findExistingConversation(withId: id)
        } else { current = interactor.undeliveredConversation }
        guard let current else { return }
        if openCallService.state != .success && openCallService.state != .failure { return }
        if UIApplication.shared.applicationState == .active { performViewUpdate(with: current) }
        else { needsViewUpdate = true }
        if UIApplication.shared.applicationState == .active { reloadCreateTicketViews() }
    }
    func setAdminTypingBlocksOnInteractor() {
        interactor.adminDidBeginTyping = { [weak self] details in
            guard let self else { return }
            self.presentTypingIndicator(with: details)
            self.scheduler.invalidate()
            self.scheduler.schedule(timeInterval: 5, repeats: false) { [weak self] in self?.removeActivityIndicator() }
        }
        interactor.adminDidEndTyping = { [weak self] in
            do {
                let scheduler = self?.scheduler
                scheduler?.invalidate()
            }
            self?.removeActivityIndicator()
        }
        interactor.botDidBeginThinking = { [weak self] in self?.presentThinkingIndicator() }
    }

    func presentTypingIndicator(with details: TypingAdminDetails?) {
        if showTypingIndicator { return }
        showThinkingIndicator = false
        typingIndicatorDetails = details
        showTypingIndicator = true
        if let currentView = view, let conversation {
            buildCollectionViewCellTypes(from: conversation)
            let snapshot = currentView.buildSnapshot(from: cells)
            currentView.applySnapshot(snapshot, scrollToBottomIfRequired: true, completion: nil)
        }
    }

    func removeActivityIndicator() {
        if !showTypingIndicator && !showThinkingIndicator { return }
        typingIndicatorDetails = nil
        showTypingIndicator = false
        showThinkingIndicator = false
        if let currentView = view, let conversation {
            buildCollectionViewCellTypes(from: conversation)
            let snapshot = currentView.buildSnapshot(from: cells)
            currentView.applySnapshot(snapshot, scrollToBottomIfRequired: true, completion: nil)
        }
    }

    func presentThinkingIndicator() {
        scheduler.invalidate()
        scheduler.schedule(timeInterval: 60, repeats: false) { [weak self] in self?.removeActivityIndicator() }
        showTypingIndicator = false
        typingIndicatorDetails = nil
        showThinkingIndicator = true
        if let currentView = view, let conversation {
            buildCollectionViewCellTypes(from: conversation)
            let snapshot = currentView.buildSnapshot(from: cells)
            currentView.applySnapshot(snapshot, scrollToBottomIfRequired: true, completion: nil)
        }
    }
    func resetFinStreamingPartsIfPresent() {
        disableAutoScrolling = false
        finStreamingPartIndexTracker = 0
        guard let conversation else { return }
        var parts = conversation.conversationParts! as! [ICMConversationPart]
        parts.removeAll { $0.isStreamingPart }
        conversation.conversationParts = parts
    }

    func appendCursor(to blocks: [ITBBlock]) -> [ITBBlock] {
        let textBlocks = blocks.compactMap { $0 as? ITBTextBlock }
        guard let block = textBlocks.last, let original = block.attributedString else { return blocks }
        let text = NSMutableAttributedString(attributedString: original)
        let blank = NSTextAttachment()
        blank.bounds = CGRect(x: 0, y: 0, width: 3, height: 0)
        let image = ICMAssets.streamingCursorImage()!
        let cursor = NSTextAttachment(image: image)
        let cursorString = NSAttributedString(attachment: cursor)
        let blankString = NSAttributedString(attachment: blank)
        text.append(blankString)
        text.append(cursorString)
        block.attributedString = text
        return blocks
    }

    func toggleStateSyncScheduler(for input: ICMConversation) {
        if let last = input.lastPart() {
            if input.state == 1 { stateSyncScheduler.invalidate(); return }
            if last.form != nil { stateSyncScheduler.invalidate(); return }
        }
        if let timer = stateSyncScheduler.timer { _ = timer.isValid }
        else { setupStateSyncing() }
    }
    func finDidStream(blocks: [ITBBlock], with index: Int) {
        guard let retainedConversation = conversation else { return }
        let previousIndex = finStreamingPartIndexTracker
        guard retainedConversation.activeBot() != nil, index > previousIndex else { return }
        finStreamingPartIndexTracker = index
        removeActivityIndicator()
        let updatedBlocks = appendCursor(to: blocks)
        let originalParts = retainedConversation.conversationParts! as! [ICMConversationPart]
        if let streaming = originalParts.first(where: { $0.isStreamingPart }) {
            streaming.blocks = updatedBlocks
            let oldCells = cells
            buildCollectionViewCellTypes(from: retainedConversation)
            if cells != oldCells {
                if let currentView = view {
                    let snapshot = currentView.buildSnapshot(from: cells)
                    currentView.applySnapshot(snapshot, scrollToBottomIfRequired: true, completion: nil)
                }
            } else {
                let id = streaming.partId ?? ""
                guard let currentView = view else { return }
                let matching = cells.filter { cell in
                    switch cell {
                    case .message(let part), .standaloneBlock(let part):
                        let candidate = part.partId ?? ""
                        return candidate == id || candidate.hasPrefix(id + "#")
                    default: return false
                    }
                }
                currentView.update(cellTypes: matching, scrollToBottom: true)
            }
        } else {
            guard let streaming = ICMConversationPart.streamingPart(withBlocks: updatedBlocks) else { return }
            streaming.isStreamingPart = true
            var freshParts = retainedConversation.conversationParts! as! [ICMConversationPart]
            freshParts.append(streaming)
            retainedConversation.conversationParts = freshParts
            if let currentView = view, let currentConversation = conversation {
                buildCollectionViewCellTypes(from: currentConversation)
                let snapshot = currentView.buildSnapshot(from: cells)
                currentView.applySnapshot(snapshot, scrollToBottomIfRequired: true, completion: nil)
            }
        }
    }
    var successHandler: (ICMConversation) -> Void {
        { [weak self] input in
            guard let self else { return }
            if UIApplication.shared.applicationState == .active { self.performViewUpdate(with: input) }
            else { self.needsViewUpdate = true }
        }
    }

    var failureHandler: (ConversationError) -> Void {
        let capturedView = view
        return { [weak capturedView] error in
            guard let currentView = capturedView else { return }
            withExtendedLifetime(currentView) {
                currentView.showLoading(false)
                error.logErrorToOurCustomers()
                if error != .messageSendingFailure && error != .conversationAlreadyOpen {
                    currentView.showErrorView(true)
                }
                currentView.updateComposer()
            }
        }
    }
    private func formsGroup(lhs: ICMConversationPart, rhs: ICMConversationPart) -> Bool {
        if lhs.participantId != rhs.participantId { return false }
        _ = lhs.createdAt!
        _ = rhs.createdAt!
        if TimestampCalculator.shared.part(lhs, isFromDifferentDayAs: rhs) { return false }
        if !lhs.participantIsAdmin {
            let lhsTime = lhs.createdAt!.timeIntervalSince1970
            let rhsTime = rhs.createdAt!.timeIntervalSince1970
            if !(abs(lhsTime - rhsTime) < 3600) { return false }
        }
        if lhs.isTypingPart() || rhs.isTypingPart() { return false }
        if lhs.isLinkCard() || rhs.isLinkCard() { return false }
        if lhs.isLinkListCard() || rhs.isLinkListCard() { return false }
        if lhs.isEvent() || rhs.isEvent() { return false }
        if lhs.isAttributeCollector() || rhs.isAttributeCollector() { return false }
        if lhs.isInitialMessageQuickReply() || rhs.isInitialMessageQuickReply() { return false }
        if lhs.isQuickReply() || rhs.isQuickReply() { return false }
        if lhs.isArticleContextCard() || rhs.isArticleContextCard() { return false }
        if lhs.isComposerSuggestions() || rhs.isComposerSuggestions() { return false }
        return true
    }

    func groupPartsAndUpdateFlags(in input: ICMConversation) {
        let original = input.conversationParts! as! [ICMConversationPart]
        for (index, part) in original.enumerated() {
            let count = (input.conversationParts! as! [ICMConversationPart]).count
            if index < count - 1 {
                let next = (input.conversationParts! as! [ICMConversationPart])[index + 1]
                if formsGroup(lhs: part, rhs: next) {
                    if part.groupingPosition == 3 { part.groupingPosition = 0 }
                    else { part.groupingPosition = part.groupingPosition == 0 ? 0 : 1 }
                    next.groupingPosition = 2
                }
            }
        }
    }

    static func assignSenderMetadata(in input: ICMConversation) {
        let parts = input.conversationParts! as! [ICMConversationPart]
        var lastEligible: ICMConversationPart?
        for part in parts {
            part.isSenderLinePinned = false
            let eligible: Bool
            if part.shouldShowStatus() && !part.isPersonalPart() {
                if part.partStyle == 7 { eligible = true }
                else { eligible = part.containsBlocks() && !part.isEvent() && !part.isLinkCard() && !part.isLinkListCard() }
            } else { eligible = false }
            if eligible { part.senderLabel = part.resolvedSenderLabel; lastEligible = part }
            else { part.senderLabel = nil }
        }
        lastEligible?.isSenderLinePinned = true
    }
    static func cellTypes(for part: ICMConversationPart) -> [ConversationCellType] {
        guard let rawBlocks = part.blocks else { return [.message(part)] }
        let blocks = rawBlocks
        if !blocks.contains(where: { BlockSegmenter.separatedTypes.contains($0.type()) }) { return [.message(part)] }
        let ordered: [ITBBlock]
        if part.isPersonalPart() { ordered = part.reorderedBlocksForHuman() }
        else { ordered = part.blocks! }
        let segments = BlockSegmenter.segment(ordered)
        var result: [ConversationCellType] = []
        var seenMessage = false
        for (index, segment) in segments.enumerated() {
            switch segment {
            case .standalone(let block):
                let standalone = [block]
                let suffix = "s" + String(index)
                if let copy = part.copy() as? ICMConversationPart {
                    copy.blocks = standalone
                    copy.groupingPosition = 3
                    copy.hasSegmentBelow = index < segments.count - 1
                    copy.partId = (part.partId ?? "") + "#" + suffix
                    copy.isStreamingPart = false
                    copy.partMetadata = nil
                    copy.answerSources = nil
                    result.append(.standaloneBlock(copy))
                } else { result.append(.standaloneBlock(part)) }
            case .group(let group):
                var position = part.groupingPosition
                if segments.count > 1 {
                    switch position {
                    case 0: position = index == 0 ? 0 : 1
                    case 1: position = 1
                    case 2: position = index == segments.count - 1 ? 2 : 1
                    case 3: position = index == 0 ? 0 : (index == segments.count - 1 ? 2 : 1)
                    default: break
                    }
                }
                if index > 0, case .standalone = segments[index - 1] {
                    if position == 1 { position = 0 }
                    else if position == 2 { position = 3 }
                }
                if index + 1 < segments.count, case .standalone = segments[index + 1] {
                    if position == 0 { position = 3 }
                    else if position == 1 { position = 2 }
                }
                let suffix = "c" + String(index)
                if let copy = part.copy() as? ICMConversationPart {
                    copy.blocks = group
                    copy.groupingPosition = position
                    copy.hasSegmentBelow = index < segments.count - 1
                    copy.partId = (part.partId ?? "") + "#" + suffix
                    copy.isStreamingPart = false
                    if seenMessage { copy.partMetadata = nil; copy.answerSources = nil }
                    result.append(.message(copy))
                } else { result.append(.message(part)) }
                seenMessage = true
            }
        }
        clearPinExceptOnLastMessage(in: &result)
        return result
    }

    static func clearPinExceptOnLastMessage(in cells: inout [ConversationCellType]) {
        let last = cells.lastIndex { if case .message = $0 { return true }; return false }
        for (index, cell) in cells.enumerated() {
            switch cell {
            case .message(let part): if index != last { part.isSenderLinePinned = false }
            case .standaloneBlock(let part): part.isSenderLinePinned = false
            default: break
            }
        }
    }
    func firstCellIndex(for part: ICMConversationPart) -> Int? {
        guard let id = part.partId else { return nil }
        for (index, cell) in cells.enumerated() {
            switch cell {
            case .message(let candidate), .standaloneBlock(let candidate):
                guard let candidateId = candidate.partId else { continue }
                if candidateId == id || candidateId.hasPrefix(id + "#") { return index }
            default: continue
            }
        }
        return nil
    }

    func shouldShowDayDividerBeforePartIn(conversation input: ICMConversation, currentIndex: Int) -> Bool {
        if (input.conversationParts! as! [ICMConversationPart]).isEmpty { return false }
        if (input.conversationParts! as! [ICMConversationPart]).count < currentIndex { return false }
        let firstRead = (input.conversationParts! as! [ICMConversationPart])[currentIndex]
        if firstRead.isArticleContextCard() { return false }
        let current = (input.conversationParts! as! [ICMConversationPart])[currentIndex]
        if currentIndex == 0 { return TimestampCalculator.shared.partWasCreatedBeforeToday(current) }
        let previous = (input.conversationParts! as! [ICMConversationPart])[currentIndex - 1]
        if !current.displayTimestamp { return false }
        if current.partStyle != 0 { return false }
        if !previous.displayTimestamp { return false }
        return TimestampCalculator.shared.part(previous, isFromDifferentDayAs: current)
    }

    func injectDayDividerWhereNeeded(in input: ICMConversation) {
        let parts = input.conversationParts! as! [ICMConversationPart]
        for (index, part) in parts.enumerated() {
            if shouldShowDayDividerBeforePartIn(conversation: input, currentIndex: index), part.partId != nil {
                if let cellIndex = firstCellIndex(for: part) { cells.insert(.dayDivider(part), at: cellIndex) }
            }
        }
    }

    func getIndexForUnreadDividerIn(conversation input: ICMConversation) -> Int? {
        guard let currentView = view else { return nil }
        return withExtendedLifetime(currentView) {
            if currentView.isScrolledNearBottom { return nil }
            let parts = input.conversationParts! as! [ICMConversationPart]
            let unread = unreadParts
            for part in parts {
                if unread.contains(where: { $0.isEqual(part) }) { return firstCellIndex(for: part) }
            }
            return nil
        }
    }
    func buildCollectionViewCellTypes(from input: ICMConversation) {
        cells = []
        if let intro = input.teamIntro { cells.append(.greeting(intro)) }
        if let notice = input.specialNotice { cells.append(.temporaryExpectationsView(notice)) }
        if input.showTicketCardView {
            let parts = input.conversationParts! as! [ICMConversationPart]
            if parts.isEmpty || parts.allSatisfy({ $0.partStyle == 6 }) {
                cells.append(.ticket(input.ticket!))
                return
            }
        }
        groupPartsAndUpdateFlags(in: input)
        Self.assignSenderMetadata(in: input)
        let parts = input.conversationParts! as! [ICMConversationPart]
        cells.append(contentsOf: parts.flatMap { Self.cellTypes(for: $0) })
        injectDayDividerWhereNeeded(in: input)
        if let index = getIndexForUnreadDividerIn(conversation: input) { cells.insert(.unreadMessageDivider, at: index) }
        if showTypingIndicator { cells.append(.activityIndicator(typingIndicatorDetails)) }
        if showThinkingIndicator { cells.append(.activityIndicator(nil)) }
        if let footer = conversation?.footerNotice {
            cells.append(.footerNotice(FooterNoticeViewModel(title: footer.title, body: footer.subtitle, avatarDetails: footer.avatars)))
        }
    }
    func performViewUpdate(with input: ICMConversation) {
        guard let initialView = view else { return }
        withExtendedLifetime(initialView) {
            toggleStateSyncScheduler(for: input)
            var parts = input.conversationParts! as! [ICMConversationPart]
            parts.sort { !$0.isStreamingPart && $1.isStreamingPart }
            input.conversationParts = parts
            conversation = input
            addMetricsForParts(in: input)
            addMetricForViewed(conversation: input)
            let showNewButton: Bool
            if input.conversationEndedButton == nil { showNewButton = input.shouldDisplayStartNewConversationButton() }
            else if let button = input.conversationEndedButton, button.metadata != nil {
                showNewButton = input.shouldDisplayStartNewConversationButton()
            } else { showNewButton = false }
            let button = input.conversationEndedButton
            if let closed = initialView.closedConversationView { closed.updateButton(with: button, visibility: showNewButton) }
            else { initialView.addCloseConversationView(showNewConversationButton: showNewButton, buttonData: button) }
            initialView.showClosedConversationView(conversation?.preventEndUserReplies ?? false)
            let completion: () -> Void = { [weak self, input] in
                guard let dispatcher = self?.queue else { return }
                dispatcher.dispatch { [weak self, input] in
                    guard let currentView = self?.view else { return }
                    let fallback = self?.interactor.dataManager.fallbackHeader()
                    let header = input.headerToDisplay() ?? fallback
                    guard let header else { return }
                    withExtendedLifetime(currentView) { currentView.navigationBarManager.updateTitleViewWith(header: header) }
                }
            }
            if let snapshotView = view, let current = conversation {
                buildCollectionViewCellTypes(from: current)
                let snapshot = snapshotView.buildSnapshot(from: cells)
                snapshotView.applySnapshot(snapshot, scrollToBottomIfRequired: true, completion: completion)
            }
            initialView.showErrorView(false)
            initialView.updateComposer()
            let scrollButton = initialView.scrollDownButton
            scrollButton.unreadCount = initialView.viewModel!.unreadParts.count
            if shouldShowTicketBannerFor(input) {
                initialView.ticketBannerView.alpha = 1
                initialView.ticketBannerView.isHidden = false
                let rootView = initialView.view!
                UIView.transition(with: rootView, duration: 0.5, options: [], animations: { [weak initialView, input] in
                    guard let bannerView = initialView else { return }
                    withExtendedLifetime(bannerView) {
                        let collection = bannerView.conversationCollectionView
                        let height = bannerView.ticketBannerView.frame.height + 16
                        var inset = collection.contentInset
                        inset.top = height
                        collection.contentInset = inset
                        bannerView.ticketBannerView.updateStatusInformation(conversation: input)
                    }
                }, completion: nil)
            }
            let reactionParts = input.conversationParts! as! [ICMConversationPart]
            if let reactions = reactionParts.last?.reactionSet { initialView.showReactionsIfNecessary(reactionSet: reactions) }
            playAdminSoundIfNecessary(for: input)
            if !input.read && UIApplication.shared.applicationState == .active {
                input.read = true
                interactor.markConversationAsRead()
            }
            if !didTrackOpenInteraction {
                recordOpenInteraction(for: input)
                didTrackOpenInteraction = true
            }
            let quickReplyParts = input.conversationParts! as! [ICMConversationPart]
            if let last = quickReplyParts.last, last.isQuickReply() { addMetricsForQuickReply(part: last, in: input) }
            initialView.updateFooterNotice()
            updateFooterViews()
        }
    }
    func shouldShowTicketBannerFor(_ input: ICMConversation) -> Bool {
        let parts = input.conversationParts! as! [ICMConversationPart]
        if !parts.contains(where: { $0.partStyle != 6 }) { return false }
        if input.showTicketCardView { return false }
        return input.ticket != nil
    }
    func playAdminSoundIfNecessary(for input: ICMConversation) {
        do {
            guard let first = input.lastPart() else { return }
            guard input.conversationId != nil else { return }
            if input.read { return }
            if !first.participantIsAdmin { return }
        }
        if let second = input.lastPart(), second.participant?.isBot == true { soundPlayer.playSoundForOperatorResponse() }
        else { soundPlayer.playSoundForAdminResponse() }
    }

    func canRecordOpenedInteraction(for input: ICMConversation) -> Bool {
        guard let status = input.notificationStatus else { return false }
        guard input.conversationId != nil else { return false }
        return status == "delivered" || status == "renotifying"
    }

    func recordOpenInteraction(for input: ICMConversation) {
        guard canRecordOpenedInteraction(for: input) else { return }
        let bridge = interactor.apiBridge
        let interactions = ["opened"]
        let id = input.conversationId!
        bridge.recordInteractions(interactions, for: id)
    }

    func addMetricsForParts(in input: ICMConversation) {
        if (input.conversationParts! as! [ICMConversationPart]).isEmpty { return }
        let parts = input.conversationParts! as! [ICMConversationPart]
        let manager = metricsManager
        withExtendedLifetime(manager) {
            for part in parts {
                if partIdsWithMetricsAdded.contains(part.partId!) { continue }
                let ownId = identityStore.userIdentity.intercomId
                let participantId = part.participantId
                if ownId == participantId { continue }
                let info: [String: Any] = ["conversation": input, "conversation_part": part,
                    "is_attachment": part.hasAttachments(), "is_sticker": false, "has_article_card": part.hasArticleCard]
                let event = ICMMetricEvent(action: "received", object: "reply", place: "messenger",
                    context: "in_conversation", additionalInfo: info)
                manager?.addEvent(event)
                partIdsWithMetricsAdded.append(part.partId!)
            }
        }
    }

    func addMetricsForQuickReply(part: ICMConversationPart, in input: ICMConversation) {
        let options = part.replyOptionsArray! as! [ReplyOption]
        if options.isEmpty { return }
        let manager = metricsManager
        withExtendedLifetime(manager) {
            for option in options {
                let event = ICMMetricEvent(action: "received", object: "quick_replies", place: "messenger",
                    context: "in_conversation", additionalInfo: ["conversation": input, "conversation_part": part, "quick_reply_id": option.uuid])
                manager?.addEvent(event)
            }
        }
    }

    func addMetricForViewed(conversation input: ICMConversation?) {
        if conversationViewedMetricTracked { return }
        let event: ICMMetricEvent?
        if let input {
            event = ICMMetricEvent(action: "viewed", object: "conversation", place: "messenger", context: "in_conversation",
                additionalInfo: ["conversation": input])
        } else {
            event = ICMMetricEvent(action: "viewed", object: "new_conversation", place: "messenger", context: "in_conversation", additionalInfo: nil)
        }
        metricsManager?.addEvent(event)
        conversationViewedMetricTracked = true
    }
    func reloadCreateTicketViews() {
        guard let currentView = view else { return }
        withExtendedLifetime(currentView) {
            let matches = cells.filter { cell in
                switch cell {
                case .message(let part), .standaloneBlock(let part): return part.isCreateTicketPart()
                default: return false
                }
            }
            currentView.update(cellTypes: matches, scrollToBottom: false)
        }
    }
    func reloadCell(with content: ContentType) {
        guard let currentView = view else { return }
        withExtendedLifetime(currentView) {
            let matches = cells.filter { cell in
                let part: ICMConversationPart
                switch cell {
                case .message(let value), .standaloneBlock(let value): part = value
                default: return false
                }
                switch content {
                case .object(let object): return part.isEqual(object)
                case .block(let target):
                    return part.blocks!.contains { $0.blockUniqueIdentifier() == target.blockUniqueIdentifier() }
                }
            }
            currentView.update(cellTypes: matches, scrollToBottom: false)
        }
    }
    func invalidateLayoutFor(block: ITBBlock) {
        var matchingParts: [ICMConversationPart] = []
        for cell in cells {
            if case .message(let part) = cell {
                if part.blocks!.contains(where: { $0.isEqual(block) }) { matchingParts.append(part) }
            }
        }
        guard let first = matchingParts.first else { return }
        let id = first.partId!
        view?.invalidateLayoutForPart(with: id)
    }
    private func reloadLastMessageGroup() {
        var matches: [ConversationCellType] = []
        for cell in cells.reversed() {
            guard case .message(let part) = cell else { continue }
            if part.isAttributeCollector() { continue }
            matches.append(cell)
            if part.groupingPosition == 0 { break }
            if part.groupingPosition == 3 { break }
        }
        if matches.isEmpty { return }
        view?.update(cellTypes: matches, scrollToBottom: false)
    }
    func setupStateSyncing() {
        stateSyncScheduler.invalidate()
        guard let settings = config.conversationStateSyncSettings, settings.enabled else { return }
        let interval = settings.syncInterval / 1000
        stateSyncScheduler.schedule(timeInterval: interval, repeats: true) { [weak self, settings] in
            guard let self, let current = self.conversation, let last = current.lastAdminPart() else { return }
            let timeout = settings.startTimeout
            let date = last.createdAt!
            if abs(date.timeIntervalSinceNow) < timeout / 1000 { return }
            let currentInteractor = self.interactor
            let id = last.partId
            let createdAt = last.createdAt
            currentInteractor.fetchConversation(reason: .stateSync, lastAdminPartId: id,
                lastAdminPartCreatedAt: createdAt, success: nil, failure: nil)
        }
    }
}

extension ConversationViewModel {
    func reload(content: ITBBlock) {
        guard let currentView = view else { return }
        withExtendedLifetime(currentView) {
            let matches = cells.filter { cell in
                let part: ICMConversationPart
                switch cell {
                case .message(let value), .standaloneBlock(let value): part = value
                default: return false
                }
                guard let blocks = part.blocks else { return false }
                return blocks.contains { $0.isEqual(content) }
            }
            currentView.update(cellTypes: matches, scrollToBottom: false)
        }
    }

    func preventCaching(for input: URL) -> URL {
        var components = URLComponents(url: input, resolvingAgainstBaseURL: false)
        var items = components?.queryItems ?? []
        items.append(URLQueryItem(name: "preventCache", value: String(Date().timeIntervalSince1970)))
        components?.queryItems = items
        return components?.url ?? input
    }

    func playVideo(file: ITBVideoFileBlock) {
        guard let url = URL(string: file.url ?? "") else { return }
        let uncached = preventCaching(for: url)
        let player = AVPlayer(url: uncached)
        let controller = AVPlayerViewController()
        controller.player = player
        guard let currentView = view else { return }
        controller.delegate = currentView
        currentView.present(controller, animated: true) { player.play() }
    }

    func open(url: URL) {
        guard let currentView = view else { return }
        withExtendedLifetime(currentView) { LinkOpener.shared.openLink(url) }
    }

    func showTicketDetails() {
        guard let conversation, let currentView = view else { return }
        withExtendedLifetime(currentView) {
            let manager = ICMPresentationManager.sharedInstance()!
            let ticket = conversation.ticket!
            manager.presentTicket(ticket, fromTriggerSource: 0)
        }
    }

    func createTicketTapped(for ticketType: TicketType) {
        guard let conversation, let currentView = view else { return }
        withExtendedLifetime(currentView) {
            ICMPresentationManager.sharedInstance().presentCreateTicketForm(forTicketType: ticketType, conversation: conversation)
        }
    }

    func presentAlert(_ alert: UIAlertController) {
        guard let currentView = view else { return }
        currentView.view!.endEditing(true)
        currentView.present(alert, animated: true, completion: nil)
    }

    func navigateToConversation(with id: String?) {
        let conversation = ICMConversation()
        conversation.conversationId = id
        guard let currentView = view else { return }
        withExtendedLifetime(currentView) {
            let manager = ICMPresentationManager.sharedInstance()!
            let navigation = currentView.intercomNavigationController()
            manager.showConversation(conversation, from: navigation)
        }
    }

    func retryTapped(conversationPart: ICMConversationPart, sender: UIView) {
        failedPart = conversationPart
        let allowsRetry = conversationPart.sendFailureAllowsRetry
        if allowsRetry { view?.openMessageRetrySheet(sender) }
        else { view?.openMessageDeleteSheet(sender) }
    }

    func showInbox() { view?.dismiss(animated: false, completion: nil) }

    func citationsTapped(_ citations: [ITBInlineCitation], from sources: [FinAnswerSourceWrapper]) {
        let values = sources.map { $0.wrappedValue }
        view?.openCitationView(with: citations, sources: values)
    }

    func imageTapped(with block: ITBImageBlock) {
        let remote = URL(string: block.url ?? "")
        let selected: URL?
        if let upload = block.upload { selected = upload.fileURL! }
        else { selected = remote }
        guard let selected else { return }
        view?.openPreview(for: selected)
    }

    func attachmentTapped(with block: ITBAttachmentBlock) {
        guard let string = block.url, let url = URL(string: string) else { return }
        view?.openPreview(for: url)
    }
}

extension ConversationViewModel {
    func setupConversation() {
        if interactor.shouldFetchNewConversation() {
            var candidate = interactor.dataManager.matchedNewConversation()!
            let parts = candidate.conversationParts! as! [ICMConversationPart]
            if parts.isEmpty, let withArticle = interactor.conversationWithArticlePart(interactor.existingConversation) {
                candidate = withArticle
            }
            if UIApplication.shared.applicationState == .active { performViewUpdate(with: candidate) }
            else { needsViewUpdate = true }
            view?.showLoading(false)
            addMetricForViewed(conversation: nil)
            return
        }
        let partId = conversation?.lastAdminPart()?.partId
        let createdAt = conversation?.lastAdminPart()?.createdAt
        let success: () -> Void = { [weak capturedView = view] in
            if let currentView = capturedView {
                withExtendedLifetime(currentView) {
                    if let index = currentView.lastItemIndexPath {
                        currentView.conversationCollectionView.scrollToItem(at: index, at: .bottom, animated: false)
                    }
                }
            }
            capturedView?.showLoading(false)
        }
        let failure = failureHandler
        guard let id = interactor.conversationId else {
            failure(.missingConversationId)
            return
        }
        interactor.requestConversation(id: id, reason: .clickConversation, lastAdminPartId: partId,
            lastAdminPartCreatedAt: createdAt, success: success, failure: failure)
    }
}

extension ConversationViewModel: ClosedConversationViewDelegate {}

extension ConversationViewModel {
    // The original private witness name was deleted; this expresses its actual default path.
    func dismissPrivacyPolicy() {
        interactor.dismissPrivacyPolicy()
        dismissPrivacyPolicyButtonTapped = true
        updateFooterViews()
    }
}

extension ConversationViewModel {
    func submitButtonTapped(_ attribute: FormAttribute, conversationPart: ICMConversationPart,
                            successBlock: @escaping () -> Void, failureBlock: @escaping (Error) -> Void) {
        guard let id = interactor.conversationId else { return }
        let partId = conversationPart.partId!
        interactor.submitFormParams(attribute, conversationPartId: partId, conversationId: id) { result in
            switch result {
            case .success: successBlock()
            case .failure(let error): failureBlock(error)
            }
        }
    }

    private func localRatingBlock(forPartId id: String) -> ICMConversationRatingBlock? {
        let current: ICMConversation?
        if let conversationId = interactor.conversationId { current = interactor.dataManager.findExistingConversation(withId: conversationId) }
        else { current = interactor.undeliveredConversation }
        guard let current else { return nil }
        let parts = current.conversationParts! as! [ICMConversationPart]
        guard let part = parts.first(where: { $0.partId == id }), let blocks = part.blocks else { return nil }
        return blocks.first(where: { $0.type() == 15 }) as? ICMConversationRatingBlock
    }

    func rateConversation(_ rating: UInt, conversationPart: ICMConversationPart) {
        let id = conversationPart.partId!
        let block = localRatingBlock(forPartId: id)
        let previous = block?.selectedRatingIndex
        if let block {
            block.selectedRatingIndex = NSNumber(value: rating)
            if let currentView = view {
                let matches = cells.filter { cell in
                    switch cell {
                    case .message(let part), .standaloneBlock(let part): return part.partId == id
                    default: return false
                    }
                }
                currentView.update(cellTypes: matches, scrollToBottom: false)
            }
        }
        guard let conversationId = interactor.conversationId else { return }
        interactor.apiBridge.rate(conversationId: conversationId, with: rating) { [weak self, previous] error in
            guard let self else { return }
            self.queue.dispatch { [weak self, previous] in
                guard let self, let block = self.localRatingBlock(forPartId: id) else { return }
                if block.selectedRatingIndex == NSNumber(value: rating) { block.selectedRatingIndex = previous }
                if error.statusCode == 403 { block.blockRating = true }
                if let currentView = self.view {
                    let matches = self.cells.filter { cell in
                        switch cell {
                        case .message(let part), .standaloneBlock(let part): return part.partId == id
                        default: return false
                        }
                    }
                    currentView.update(cellTypes: matches, scrollToBottom: false)
                }
            }
        }
    }
}

extension ConversationViewModel {
    func addRemarkToConversation(_ remark: String, conversationPart: ICMConversationPart) {
        let id = conversationPart.partId!
        let block = localRatingBlock(forPartId: id)
        let previous = block?.remark
        if let block {
            block.remark = remark
            if let currentView = view {
                let matches = cells.filter { cell in
                    switch cell {
                    case .message(let part), .standaloneBlock(let part): return part.partId == id
                    default: return false
                    }
                }
                currentView.update(cellTypes: matches, scrollToBottom: false)
            }
        }
        guard let conversationId = interactor.conversationId else { return }
        interactor.apiBridge.addRemark(conversationId: conversationId, remark: remark) { [weak self, previous] error in
            guard let self else { return }
            self.queue.dispatch { [weak self, previous] in
                guard let self, let block = self.localRatingBlock(forPartId: id) else { return }
                block.remark = previous
                if error.statusCode == 403 { block.blockRating = true }
                if let currentView = self.view {
                    let matches = self.cells.filter { cell in
                        switch cell {
                        case .message(let part), .standaloneBlock(let part): return part.partId == id
                        default: return false
                        }
                    }
                    currentView.update(cellTypes: matches, scrollToBottom: false)
                }
            }
        }
    }

    @objc func didSelectErrorAction() {
        guard let currentView = view else { return }
        withExtendedLifetime(currentView) {
            currentView.showErrorView(false)
            currentView.showLoading(true)
            if openCallService.state.rawValue == 2 { openCallService.retry(); return }
            let current: ICMConversation?
            if let id = interactor.conversationId { current = interactor.dataManager.findExistingConversation(withId: id) }
            else { current = interactor.undeliveredConversation }
            guard current != nil else { return }
            let partId = conversation?.lastAdminPart()?.partId
            let createdAt = conversation?.lastAdminPart()?.createdAt
            let success: () -> Void = { [weak currentView] in currentView?.showLoading(false) }
            let failure = failureHandler
            guard let id = interactor.conversationId else { failure(.missingConversationId); return }
            interactor.requestConversation(id: id, reason: .reloadedByUser, lastAdminPartId: partId,
                lastAdminPartCreatedAt: createdAt, success: success, failure: failure)
        }
    }
}

extension ConversationViewModel: ICMErrorViewDelegate {}

extension ConversationViewModel {
    func handleSend(text: String?, mediaURLs: [URL]) {
        var content: [[AnyHashable: Any]]
        if let text { content = Array<[AnyHashable: Any]>._unconditionallyBridgeFromObjectiveC(ICMTextAction(text: text)?.blocks as NSArray?) }
        else { content = [] }
        let media = view?.composer.viewModel.getUploadedMediaData() ?? []
        for item in media {
            if item.metadata.isImage { content.append(interactor.buildImageBlock(dimensions: item.metadata.dimensions, publicURL: item.uploadURL) as [AnyHashable: Any]) }
            else { content.append(interactor.attachmentListBlock(for: item.metadata, publicURL: item.uploadURL) as [AnyHashable: Any]) }
        }
        if !content.isEmpty { sendContentToConversation(content, upload: nil, retryWith: nil) }
    }
    private func sendContentToConversation(_ content: [[AnyHashable: Any]], upload: ITBUpload?, retryWith: String?) {
        if retryWith == nil { soundPlayer.playSoundForSendingMessage() }
        let undelivered = interactor.undeliveredConversation
        let retryCreate = retryWith != nil && undelivered != nil
        let current: ICMConversation?
        if let id = interactor.conversationId { current = interactor.dataManager.findExistingConversation(withId: id) }
        else { current = interactor.undeliveredConversation }
        guard current != nil else {
            interactor.createConversation(with: content, upload: upload, success: successHandler, failure: failureHandler)
            return
        }
        if retryCreate {
            interactor.retryCreateConversation(success: successHandler, failure: failureHandler)
            return
        }
        let lastAdminDate: Int?
        if let conversation {
            let parts = conversation.conversationParts! as! [ICMConversationPart]
            let last = parts.reversed().first { $0.participantIsAdmin }
            lastAdminDate = last?.createdAt.map { Int($0.timeIntervalSince1970) }
        } else { lastAdminDate = nil }
        interactor.replyToConversation(with: content, upload: upload, lastAdminPartCreatedAt: lastAdminDate,
            retryWith: retryWith, failure: failureHandler)
    }
}

extension ConversationViewModel {
    func retryMessage() {
        guard let failedPart else { return }
        if let option = failedPart.selectedReplyOption {
            if failedPart.quickReplyPartId != nil {
                guard let id = failedPart.quickReplyPartId else { return }
                interactor.quickReply(with: option, partId: id, overwriting: failedPart, failure: failureHandler)
            } else {
                interactor.triggerInboundBot(with: option, overwriting: failedPart, success: successHandler, failure: failureHandler)
            }
            return
        }
        let blocks = Array<[AnyHashable: Any]>._unconditionallyBridgeFromObjectiveC(failedPart.blockDictionaries as NSArray?)
        let upload = failedPart.upload
        let current: ICMConversation?
        if let id = interactor.conversationId { current = interactor.dataManager.findExistingConversation(withId: id) }
        else { current = interactor.undeliveredConversation }
        let retryWith = current != nil ? failedPart.clientAssignedUUID : nil
        sendContentToConversation(blocks, upload: upload, retryWith: retryWith)
    }
    func quickReplyTapped(replyOption: ReplyOption, quickReplyPartId: String?, conversationPart: ICMConversationPart) {
        let action = replyOption.preActionType
        let current = interactor
        if action.rawValue == 1 {
            current.triggerInboundBot(with: replyOption, overwriting: conversationPart, success: successHandler, failure: failureHandler)
        } else {
            current.quickReply(with: replyOption, partId: quickReplyPartId, overwriting: conversationPart, failure: failureHandler)
        }
    }
}

extension ConversationViewModel {
    var composerInputs: [ComposerInput] {
        let config = config
        let gifs = config.attachmentSettings.gifsEnabled
        let attachments = config.attachmentSettings.cameraEnabled || config.attachmentSettings.mediaEnabled
        let voice = config.attachmentSettings.voiceTranscriptionEnabled
        return [gifs ? ComposerInput.gif : nil, attachments ? .attachment : nil, voice ? .mic : nil].compactMap { $0 }
    }
    func addMetricForComposerInputTapped(_ object: String) {
        let event: ICMMetricEvent?
        if conversation?.conversationId != nil {
            event = ICMMetricEvent(action: "clicked", object: object, place: "messenger", context: "in_conversation",
                additionalInfo: ["conversation": conversation as Any])
        } else {
            event = ICMMetricEvent(action: "clicked", object: object, place: "messenger", context: "in_new_conversation", additionalInfo: nil)
        }
        metricsManager?.addEvent(event)
    }
    func openGalleryAction() {
        if let view {
            let model = view.composerViewModel
            let count: Int
            switch model.mediaState { case .none: count = 0; case .hasMedia(let items): count = items.count }
            let limit = count < 10 ? 10 - count : 1
            let picker = view.mediaPicker
            let title = TranslationManager.messenger.translation(forKey: "add")
            picker.presentPhotoGallery(from: view, selectionLimit: limit, actionButtonTitle: title)
        }
        addMetricForComposerInputTapped("image_input")
    }
}

extension ConversationViewModel {
    func attachmentActions(mediaPicker: MediaPicker) -> [UIAlertAction] {
        let translations = TranslationManager.messenger
        let camera = UIAlertAction(title: translations.translation(forKey: "camera"), style: .default) { [weak self] _ in
            guard let self else { return }
            if let current = self.view { Task { @MainActor in await current.openCamera() } }
            self.addMetricForComposerInputTapped("camera_input")
        }
        let gallery = UIAlertAction(title: translations.translation(forKey: "photo_and_video_library"), style: .default) { [weak self] _ in self?.openGalleryAction() }
        let files = UIAlertAction(title: translations.translation(forKey: "tickets_add_file"), style: .default) { [weak self] _ in
            guard let self, let current = self.view else { return }
            let model = current.composerViewModel
            let count: Int
            switch model.mediaState { case .none: count = 0; case .hasMedia(let items): count = items.count }
            let limit = count < 10 ? 10 - count : 1
            current.filesPicker.presentFilePicker(from: current, selectionLimit: limit)
        }
        let cancel = UIAlertAction(title: translations.translation(forKey: "cancel"), style: .cancel, handler: nil)
        let config = config
        let cameraEnabled = config.attachmentSettings.cameraEnabled
        let mediaEnabled = config.attachmentSettings.mediaEnabled
        let filesEnabled = config.attachmentSettings.filesEnabled
        return [cameraEnabled ? camera : nil, mediaEnabled ? gallery : nil, filesEnabled ? files : nil, cancel].compactMap { $0 }
    }
}

extension ConversationViewModel {
    func voiceTranscriptionDismissed() {
        Task { @MainActor [self] in
            let recorder = self.audioRecorderViewModel
            if let recorder { await recorder.clearRecording() }
            self.audioRecorderViewModel = nil
        }
    }
}

extension ConversationViewModel {
    // Semantic source form of the original input-selection action branch.
    func composerDidSelectInput(_ input: ComposerInput) {
        switch input {
        case .attachment: view?.openAttachmentMenu()
        case .gif:
            view?.openGIFPicker()
            addMetricForComposerInputTapped("gif_input")
        case .mic:
            let recorder = audioRecorderFactory()
            Task { @MainActor [self] in
                let model = AudioRecorderViewModel(audioRecorder: recorder) { [weak self] result in
                    guard let self else { return }
                    switch result {
                    case .success(let value): self.handleVoiceTranscriptionResult(value)
                    case .failure(let error): self.handleVoiceTranscriptionError(error)
                    }
                }
                await model.requestPermission()
                await MainActor.run { [weak self] in
                    if model.hasRecordingPermission {
                        self?.audioRecorderViewModel = model
                        let current = self?.view
                        current?.showVoiceTranscription(viewModel: model)
                    } else {
                        let current = self?.view
                        current?.presentPermissionDeniedAlert(titleKey: "microphone_access_denied", messageKey: "enable_microphone_permission")
                    }
                }
            }
        }
    }
    private func handleVoiceTranscriptionResult(_ result: TranscriptionResult) {
        interactor.transcribeAudio(result.audioData, success: { [weak self] text in
            guard let self else { return }
            self.view?.hideVoiceTranscription()
            self.audioRecorderViewModel = nil
            if text.isEmpty { return }
            self.view?.composer.setText(text)
        }, failure: { [weak self] error in
            guard let self else { return }
            Task { @MainActor [self] in self.audioRecorderViewModel?.state = .error(.networkError("Failed to transcribe audio")) }
            ICMLogger.log("Voice transcription API error: \(error)", type: .error)
        })
    }
    private func handleVoiceTranscriptionError(_ error: TranscriptionError) {
        Task { @MainActor [self, error] in self.audioRecorderViewModel?.state = .error(error) }
        let description: String
        if case .fileTooLarge = error { description = TranslationManager.messenger.translation(forKey: "file_too_large_to_transcribe") }
        else { description = TranslationManager.messenger.translation(forKey: "transcription_failed") }
        ICMLogger.log("Voice transcription error: " + description, type: .error)
    }
}

extension ConversationViewModel: ICMInAppsDelegate {
    func presentConversation(_ conversation: Any?, fromFull full: Bool) { view?.dismiss(animated: false, completion: nil) }
    func dismissConversation(_ conversation: Any?, fromFull full: Bool) { view?.dismiss(animated: false, completion: nil) }
    func hide(inApp controller: Any?) { view?.dismiss(animated: false, completion: nil) }

    func openPost() {
        let current: ICMConversation?
        if let id = interactor.conversationId { current = interactor.dataManager.findExistingConversation(withId: id) }
        else { current = interactor.undeliveredConversation }
        guard let copy = current?.copy() as? ICMConversation else { return }
        copy.partDisplayStyle = 0
        guard let view else { return }
        let delegate = view.viewModel!
        guard let post = ICMPostViewController(conversation: copy, delegate: delegate, isInApp: false) else { return }
        post.modalPresentationStyle = .overFullScreen
        view.present(post, animated: false, completion: nil)
    }
}

extension ConversationViewModel {
    func getViewControllerForModalPresentation() -> UIViewController? { view }
    func shouldApplyCardStyleToMessengerApps() -> Bool { view != nil }
    func show(conversation: ICMConversation) {
        guard let view else { return }
        withExtendedLifetime(view) {
            let manager = ICMPresentationManager.sharedInstance()!
            let navigation = view.intercomNavigationController()
            manager.showConversation(conversation, from: navigation)
        }
    }
    func openArticle(articleId: String) {
        guard let view else { return }
        withExtendedLifetime(view) { ICMPresentationManager.sharedInstance()!.presentArticle(articleId, allowReactions: false, anchorLink: nil) }
    }
    func openHelpCenter() {
        guard let view else { return }
        withExtendedLifetime(view) { ICMPresentationManager.sharedInstance()!.presentHelpCenter() }
    }
    func imageTapped(with image: UIImage, title: String?) {
        guard let view else { return }
        view.previewHelper.preview(image: image, title: title, from: view)
    }
    func openAppSheet(with url: URL, params: [AnyHashable: Any], delegate: (any ICMMessengerSheetViewControllerDelegate)?) {
        guard let view else { return }
        view.openAppSheet(with: url, params: params, delegate: delegate)
    }
    func logMessengerAppMetricEvent(_ event: [String: Any]) {
        // The original conformance witness is the bare return at 0x9188.
    }
    func logSheetClosedMetric(url: URL) {
        // The original conformance witness is the bare return at 0x9188.
    }
    func presentMessengerAppInputAlert(with payload: MessengerAppTextInputPayload) {
        guard let view else { return }
        view.presentMessengerAppInputAlert(with: payload) { [weak view] in _ = view?.becomeFirstResponder() }
    }
}

extension ConversationViewModel: ConversationCellBlockDelegate {
    func invalidateLayoutForPart(with partId: String) { view?.invalidateLayoutForPart(with: partId) }
}

extension ConversationViewModel {
    var gifFetcher: (String, @escaping (Result<[[AnyHashable: Any]], Error>) -> Void) -> Void {
        if let gifFetcherStorage { return gifFetcherStorage }
        let fetcher: (String, @escaping (Result<[[AnyHashable: Any]], Error>) -> Void) -> Void = { [weak self] query, completion in
            let interactor: ConversationInteractor
            do { guard let current = self else { return }; interactor = current.interactor }
            let callback: (Result<ICMGIFResponse, APIError>) -> Void = { [weak self] result in
                switch result {
                case .success(let response):
                    let results = Array<[AnyHashable: Any]>._unconditionallyBridgeFromObjectiveC(response.results as NSArray?)
                    completion(.success(results))
                case .failure(let error):
                    completion(.failure(error))
                    let current = self?.view
                    current?.openMediaSelectionErrorAlert()
                }
            }
            interactor.apiBridge.loadGIFs(searchQuery: query, completionHandler: callback)
        }
        gifFetcherStorage = fetcher
        return fetcher
    }
    func didSelectGIF(block: [AnyHashable: Any]) {
        guard let action = ICMGIFAction(gifBlock: block) else { return }
        sendMessage(with: action)
    }
    func sendMessage(with action: any ICMAction) {
        let rawBlocks = action.blocks()
        let blocks: [[AnyHashable: Any]]
        if let rawBlocks { blocks = Array<[AnyHashable: Any]>._unconditionallyBridgeFromObjectiveC(rawBlocks as NSArray) }
        else { blocks = [] }
        let upload: ITBUpload?
        if action.responds(to: #selector(ICMAction.upload)) { upload = action.upload!() }
        else { upload = nil }
        if action is ICMMediaAction && upload == nil { view?.openMediaSelectionErrorAlert() }
        else { sendContentToConversation(blocks, upload: upload, retryWith: nil) }
    }
}

extension ConversationViewModel {
    private func composerDidType() {
        if conversation?.composerState?.displayState == 1 { return }
        let now = Date()
        if let last = lastSentIsTypingEventTimestamp, now.timeIntervalSince(last) < 1 { return }
        if let id = interactor.conversationId, let event = NexusEventFactory.createNexusEvent(with: 15, conversationId: id) {
            IntercomNexusClient.shared()!.send(event)
        }
        lastSentIsTypingEventTimestamp = now
    }
}

// Original upload-cancellation callback retains the real Interactor deletion flow.
extension ConversationViewModel: ITBUploadCancellationDelegate {
    @objc(uploadCancelled:)
    func uploadCancelled(_ upload: ITBUpload) {
        interactor.deletePart(containing: upload, completion: successHandler)
    }
}
