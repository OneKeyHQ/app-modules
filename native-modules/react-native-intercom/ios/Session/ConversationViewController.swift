import UIKit
import SwiftUI
import AVKit

private enum ScrollDirection { case up, down }

@objc(_TtC18IntercomSDKPrivate26ConversationViewController)
class ConversationViewController: IntercomCollectionViewController, UICollectionViewDelegate, AVPlayerViewControllerDelegate {
    let themeProvider = DefaultThemeProvider.shared
    var viewModel: ConversationViewModel?
    lazy var navigationBarManager: NavigationBarManager = makeNavigationBarManager()
    lazy var collapsedHeaderView: CollapsedConversationHeaderView = makeCollapsedHeaderView()
    private var scrollDirection: ScrollDirection?
    private var lastContentOffset: CGFloat = 0
    private var lastHorizontalSafeAreaInsets: (CGFloat, CGFloat) = (0, 0)
    let footerNotice: FooterNoticeView = {
        let view = FooterNoticeView()
        view.style = .compact
        return view
    }()
    var collectionViewBottomConstraint: NSLayoutConstraint?
    var pinnedPartId: String?
    lazy var keyboardHelper = KeyboardHelper()
    lazy var collectionViewDataSource: UICollectionViewDiffableDataSource<Section, ConversationCellType> = configureCollectionViewDataSource()
    lazy var previewHelper: PreviewHelper = makePreviewHelper()
    lazy var scrollDownButton: FloatingActionButton = makeScrollDownButton()
    lazy var filesPicker: FilePicker = makeFilesPicker()
    lazy var mediaPicker: MediaPicker = makeMediaPicker()
    lazy var cameraPicker: CameraPicker = makeCameraPicker()
    let spinner = LoadingSpinnerView()
    var isTyping = false
    lazy var ticketBannerView: TicketBannerView = makeTicketBannerView()
    lazy var loadingView: UIView = makeLoadingView()
    var closedConversationView: ClosedConversationView?
    var isDisplayingReactionsView = false
    lazy var composerViewModel: ComposerViewModel = makeComposerViewModel()
    lazy var composer: ComposerViewWrapper = makeComposer()
    lazy var footerBackdrop: UIView = makeFooterBackdrop()
    lazy var footerStack: ObservableStackView = makeFooterStack()
    lazy var conversationCollectionView: UICollectionView = configureConversationCollectionView()
    var errorViewController: ErrorViewController?
    lazy var intercomLinkView: IntercomLinkView = makeIntercomLinkView()
    lazy var privacyPolicyView = PrivacyPolicyView(delegate: viewModel, themeProvider: DefaultThemeProvider.shared)
    lazy var voiceTranscriptionOverlay: UIView = {
        let overlay = UIView()
        overlay.backgroundColor = .clear
        overlay.translatesAutoresizingMaskIntoConstraints = false
        overlay.isHidden = true
        return overlay
    }()
    var voiceTranscriptionController: UIHostingController<VoiceTranscriptionUIViewRepresentable>?

    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nibNameOrNil, bundle: nibBundleOrNil)
    }
    required init?(coder: NSCoder) { super.init(coder: coder) }

    var lastItemIndexPath: IndexPath? {
        if conversationCollectionView.numberOfSections < 1 { return nil }
        let lastSection = conversationCollectionView.numberOfSections - 1
        if conversationCollectionView.numberOfItems(inSection: lastSection) < 1 { return nil }
        let lastItem = conversationCollectionView.numberOfItems(inSection: lastSection) - 1
        return IndexPath(item: lastItem, section: lastSection)
    }
    var mostRecentCellIsVisible: Bool? {
        guard let last = lastItemIndexPath else { return nil }
        return conversationCollectionView.indexPathsForVisibleItems.contains(last)
    }
    var hasScrolledPastMostRecentMessage: Bool {
        guard let last = lastItemIndexPath else { return false }
        let height = conversationCollectionView.contentSize.height
        if !(conversationCollectionView.frame.height <= height) { return false }
        return !conversationCollectionView.indexPathsForVisibleItems.contains(last)
    }
    var isScrolledNearBottom: Bool {
        let collection = conversationCollectionView
        let height = collection.contentSize.height
        if !(height > 0) { return true }
        let offset = collection.contentOffset.y
        let frameHeight = collection.frame.size.height
        _ = collection.contentInset
        let bottom = collection.contentInset.bottom
        return (height - 40) <= ((offset + frameHeight) + bottom)
    }
}

extension ConversationViewController {
    @objc(initWithConversation:)
    convenience init(conversation: ICMConversation) {
        self.init()
        let httpClient = ICMHTTPClient.self
        let persistence = ICMPersistentStore.self
        let dataManager = ICMDataManager.shared()!
        let config = ICMConfig.shared()!
        let notificationCenter = NotificationCenter.default
        let processor = VideoFileProcessor.shared
        let interactor = ConversationInteractor(apiBridge: SwiftAPIBridge(httpClient: httpClient, persistentStore: persistence),
            dataManager: dataManager, config: config, notificationCenter: notificationCenter,
            conversation: conversation, videoFileProcessor: processor)
        let viewNotificationCenter = NotificationCenter.default
        let viewConfig = ICMConfig.shared()!
        let queue = AsyncQueue.main
        let identity: ICMIdentityStore = ICMIdentityStore.currentIdentity()
        let metrics = ICMMetric.sharedInstance()
        let messengerCache = MessengerAppViewCache()
        let videoCache = VideoBlockViewCache()
        let stateSyncScheduler = DefaultScheduler(timer: nil, queue: AsyncQueue.main)
        let theme = DefaultThemeProvider.shared
        let sound = ICMSoundPlayer.self
        let scheduler = DefaultScheduler(timer: nil, queue: AsyncQueue.main)
        let openCall = OpenCallService.shared
        let model = ConversationViewModel(view: self, interactor: interactor,
            notificationCenter: viewNotificationCenter, config: viewConfig, queue: queue, identityStore: identity,
            metricsManager: metrics, messengerAppViewCache: messengerCache, videoBlockViewCache: videoCache,
            userDefaultsManager: UserDefaultsManager(), stateSyncScheduler: stateSyncScheduler,
            themeProvider: theme, persistenceStore: persistence, soundPlayer: sound, scheduler: scheduler,
            audioRecorderFactory: { AudioRecorder(fileManager: FileManager.default, audioSession: AVAudioSession.sharedInstance()) },
            openCallService: openCall, applicationState: LiveApplicationState())
        viewModel = model
    }
}

extension ConversationViewController {
    private func makeNavigationBarManager() -> NavigationBarManager {
        let collapsed = collapsedHeaderView
        let navigation = intercomNavigationController()
        let firstIsConversation = firstViewControllerIsConversation
        let config = ICMConfig.shared()!
        let dataManager = ICMDataManager.shared()!
        let center = NotificationCenter.default
        let metric = ICMMetric.sharedInstance()
        let metricService = NavigationBarManagerMetricService(metricInstance: metric)
        let theme = DefaultThemeProvider.shared
        return NavigationBarManager(collapsedHeaderView: collapsed, navigationController: navigation, delegate: self,
            shouldUseConversationFirstNavigationBar: firstIsConversation, dataManager: dataManager, config: config,
            notificationCenter: center, navigationBarMetricService: metricService, themeProvider: theme)
    }
    private func makeCollapsedHeaderView() -> CollapsedConversationHeaderView {
        let header = CollapsedConversationHeaderView(.zero, header: nil, themeProvider: DefaultThemeProvider.shared)
        header.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(handleProfileTap(with:))))
        header.translatesAutoresizingMaskIntoConstraints = false
        return header
    }
    @objc(handleProfileTapWithGestureRecognizer:)
    private func handleProfileTap(with gesture: UITapGestureRecognizer) {
        let model = viewModel!
        guard let conversation = model.conversation, let header = conversation.headerToDisplay(), let current = model.view else { return }
        let freshConversation = model.conversation
        current.openHeaderSheet(with: header, conversation: freshConversation)
    }
}

extension ConversationViewController {
    private func makeFooterStack() -> ObservableStackView {
        let stack = ObservableStackView()
        stack.axis = .vertical
        stack.spacing = 8
        stack.alignment = .center
        stack.sizeDidChange = { [weak self, weak stack] _ in
            guard let self, let stack else { return }
            let center = self.composer.center
            let converted = stack.convert(center, from: stack as any UICoordinateSpace)
            let origin = stack.bounds.origin.y
            let bottom = abs(converted.y - origin) + (self.isDisplayingReactionsView ? 52 : 0)
            let collection = self.conversationCollectionView
            var content = collection.contentInset
            content.bottom = bottom
            collection.contentInset = content
            var indicator = self.conversationCollectionView.verticalScrollIndicatorInsets
            indicator.bottom = bottom
            self.conversationCollectionView.verticalScrollIndicatorInsets = indicator
        }
        return stack
    }
    private func makeLoadingView() -> UIView {
        let container = UIView()
        container.backgroundColor = DefaultThemeProvider.shared.theme.base
        container.translatesAutoresizingMaskIntoConstraints = false
        container.addSubview(spinner)
        spinner.centerXAnchor.constraint(equalTo: container.safeAreaLayoutGuide.centerXAnchor).isActive = true
        spinner.centerYAnchor.constraint(equalTo: container.centerYAnchor).isActive = true
        spinner.startSpinning()
        return container
    }
    private func makeIntercomLinkView() -> IntercomLinkView {
        let link = IntercomLinkView(themeProvider: DefaultThemeProvider.shared)
        link.onTap = { [weak self] in
            let model: ConversationViewModel?
            do { guard let current = self else { return }; model = current.viewModel }
            withExtendedLifetime(model!) {
                MessagesMetricService(metricInstance: ICMMetric.sharedInstance()).addWeRunOnIntercomClickedMetric()
            }
        }
        link.translatesAutoresizingMaskIntoConstraints = false
        return link
    }
}

extension ConversationViewController {
    private func configureConversationCollectionViewLayout() -> UICollectionViewLayout {
        let layout = CollectionViewChatLayout()
        layout.settings.interItemSpacing = 2
        layout.settings.additionalInsets = UIEdgeInsets(top: 0, left: 8, bottom: 0, right: 8)
        layout.keepContentOffsetAtBottomOnBatchUpdates = true
        layout.processOnlyVisibleItemsOnAnimatedBatchUpdates = false
        layout.keepContentAtBottomOfVisibleArea = true
        return layout
    }
    private func configureConversationCollectionView() -> UICollectionView {
        let layout = configureConversationCollectionViewLayout()
        let collection = UICollectionView(frame: .zero, collectionViewLayout: layout)
        collection.translatesAutoresizingMaskIntoConstraints = false
        collection.register(ConversationCollectionViewCell.self,
            forCellWithReuseIdentifier: _typeName(ConversationCollectionViewCell.self, qualified: false))
        collection.delegate = self
        return collection
    }
}

extension ConversationViewController {
    private func configureCollectionViewDataSource() -> UICollectionViewDiffableDataSource<Section, ConversationCellType> {
        UICollectionViewDiffableDataSource<Section, ConversationCellType>(collectionView: conversationCollectionView) { [weak self] collection, index, type in
            guard let self else { return UICollectionViewCell() }
            let reused = collection.dequeueReusableCell(withReuseIdentifier: _typeName(ConversationCollectionViewCell.self, qualified: false), for: index)
            let cell = (reused as? ConversationCollectionViewCell) ?? ConversationCollectionViewCell()
            let width = collection.contentSize.width - 16
            let model = self.viewModel
            withExtendedLifetime(model) { cell.configure(with: type, availableWidth: width, delegate: model) }
            return cell
        }
    }
    func buildSnapshot(from items: [ConversationCellType]) -> NSDiffableDataSourceSnapshot<Section, ConversationCellType> {
        var snapshot = NSDiffableDataSourceSnapshot<Section, ConversationCellType>()
        var main: [ConversationCellType] = []
        var bottom: [ConversationCellType] = []
        for item in items {
            switch item {
            case .footerNotice: bottom.append(item)
            case .message(let part):
                if part.hasReplyOptions() { bottom.append(item) }
                else { main.append(item) }
            default: main.append(item)
            }
        }
        if !main.isEmpty { snapshot.appendSections([.main]); snapshot.appendItems(main, toSection: .main) }
        if !bottom.isEmpty { snapshot.appendSections([.bottom]); snapshot.appendItems(bottom, toSection: .bottom) }
        reloadLastTwoMessages(for: items, in: &snapshot)
        return snapshot
    }
    private func reloadLastTwoMessages(for items: [ConversationCellType], in snapshot: inout NSDiffableDataSourceSnapshot<Section, ConversationCellType>) {
        let messages = items.filter { item in
            guard case .message(let part) = item else { return false }
            return !part.isAttributeCollector()
        }
        let last = Array(messages.suffix(2))
        let pinned = pinnedStatusItemsToReload(from: items, excluding: last)
        snapshot.reloadItems(last + pinned)
    }
}

extension ConversationViewController {
    private func pinnedStatusItemsToReload(from items: [ConversationCellType], excluding: [ConversationCellType]) -> [ConversationCellType] {
        let first = items.firstIndex { item in
            guard case .message(let part) = item else { return false }
            return part.isSenderLinePinned && part.partId != nil
        }
        let current: String?
        if let first {
            guard case .message(let part) = items[first] else { preconditionFailure() }
            precondition(part.isSenderLinePinned)
            current = part.partId!
        } else { current = nil }
        let previous = pinnedPartId
        if previous == current { pinnedPartId = current; return [] }
        let ids = Set([previous, current].compactMap { $0 })
        let matches = items.filter { item in
            guard case .message(let part) = item, let id = part.partId, ids.contains(id) else { return false }
            return !excluding.contains(item)
        }
        pinnedPartId = current
        return matches
    }

    func applySnapshot(_ snapshot: NSDiffableDataSourceSnapshot<Section, ConversationCellType>,
                       scrollToBottomIfRequired requested: Bool, completion: (() -> Void)?) {
        DispatchQueue.main.async { [weak self, snapshot] in
            guard let self else { return }
            let near = self.isScrolledNearBottom
            let source = self.collectionViewDataSource
            source.apply(snapshot, animatingDifferences: false) { [self] in
                if requested && near { self.conversationCollectionView.scrollToBottom(animated: false) }
                completion?()
            }
        }
    }
    func update(cellTypes: [ConversationCellType], scrollToBottom: Bool) {
        DispatchQueue.main.async { [weak self, cellTypes] in
            guard let self else { return }
            let source = self.collectionViewDataSource
            let current = source.snapshot()
            let filtered = cellTypes.filter { current.itemIdentifiers.contains($0) }
            if filtered.isEmpty { return }
            var updated = current.adoptingLatestItems(from: cellTypes)
            updated.reloadItems(filtered)
            self.applySnapshot(updated, scrollToBottomIfRequired: scrollToBottom, completion: nil)
        }
    }
}

extension NSDiffableDataSourceSnapshot where SectionIdentifierType == IntercomCollectionViewController.Section, ItemIdentifierType == ConversationCellType {
    func adoptingLatestItems(from incoming: [ConversationCellType]) -> Self {
        var result = Self()
        let sections = sectionIdentifiers
        for section in sections {
            result.appendSections([section])
            let latest = itemIdentifiers(inSection: section).map { old in incoming.first(where: { $0 == old }) ?? old }
            result.appendItems(latest, toSection: section)
        }
        return result
    }
}

extension ConversationViewController {
    func showLoading(_ show: Bool) {
        let time = show ? 0.0 : 0.3
        UIView.animate(withDuration: time, delay: time, options: [], animations: { [self] in
            self.loadingView.alpha = show ? 1 : 0
        }, completion: { [self] _ in self.loadingView.isHidden = !show })
    }
    private func setupLoadingView() {
        view!.addSubview(loadingView)
        view!.bringSubviewToFront(loadingView)
        loadingView.alpha = 0
        let constraints = [loadingView.leadingAnchor.constraint(equalTo: view!.leadingAnchor),
            loadingView.trailingAnchor.constraint(equalTo: view!.trailingAnchor),
            loadingView.topAnchor.constraint(equalTo: view!.topAnchor),
            loadingView.bottomAnchor.constraint(equalTo: view!.bottomAnchor)]
        NSLayoutConstraint.activate(constraints)
    }
    func showErrorView(_ show: Bool) {
        let existing = errorViewController
        if show && existing != nil { return }
        if !show {
            if let existing {
                existing.willMove(toParent: nil)
                existing.view!.removeFromSuperview()
                existing.removeFromParent()
            }
            errorViewController = nil
            let item = navigationItem
            item.titleView = collapsedHeaderView
            return
        }
        navigationItem.titleView = nil
        let title = TranslationManager.messenger.translation(forKey: "spaces_error_title")
        let subtitle = TranslationManager.messenger.translation(forKey: "error_loading_conversation")
        let reload = TranslationManager.messenger.translation(forKey: "reload")
        let error = ErrorViewController(title: title, subtitle: subtitle, showActionButton: true,
            actionButtonTitle: reload, themeProvider: DefaultThemeProvider.shared, icon: nil)
        error.errorDelegate = viewModel!
        errorViewController = error
        guard let root = view else { return }
        addChild(error)
        root.addSubview(error.view!)
        error.view!.frame = root.bounds
        error.view!.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        error.didMove(toParent: self)
    }
}

extension ConversationViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        title = TranslationManager.messenger.translation(forKey: "conversations")
        conversationCollectionView.delegate = self
        view!.backgroundColor = DefaultThemeProvider.shared.theme.base
        setupView()
        viewModel!.viewDidLoad()
        navigationBarManager.navigationController?.displayCloseButton()
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if #available(iOS 26, *) { navigationController?.setNavigationBarHidden(false, animated: false) }
        else { navigationController?.setNavigationBarHidden(false, animated: animated) }
        navigationItem.backButtonDisplayMode = .minimal
        viewModel!.viewWillAppear()
    }
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        let model = viewModel!
        model.notificationCenter.removeObserver(model)
        FormAttribute.clearAll()
        model.interactor.dataManager.clearUndeliveredConversations()
    }
    override func viewWillTransition(to size: CGSize, with coordinator: any UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        coordinator.animate(alongsideTransition: { [self] _ in
            self.conversationCollectionView.collectionViewLayout.invalidateLayout()
            self.conversationCollectionView.reloadData()
        }, completion: nil)
    }
    override func viewSafeAreaInsetsDidChange() {
        super.viewSafeAreaInsetsDidChange()
        let left = view!.safeAreaInsets.left
        let right = view!.safeAreaInsets.right
        if left == lastHorizontalSafeAreaInsets.0 && right == lastHorizontalSafeAreaInsets.1 { return }
        lastHorizontalSafeAreaInsets = (left, right)
        conversationCollectionView.collectionViewLayout.invalidateLayout()
        conversationCollectionView.reloadData()
    }
}

extension ConversationViewController {
    func updateFooterViewVisibility(fromKeyboardNotification: Bool) {
        if !fromKeyboardNotification && !keyboardHelper.isKeyboardHidden { return }
        let model = viewModel!
        let notice = model.interactor.dataManager.privacyPolicyNotice
        let privacy: Bool
        let link: Bool
        if let notice, !notice.isDismissed, !model.dismissPrivacyPolicyButtonTapped,
           !model.cells.contains(where: { if case .message(let part) = $0 { return part.isPersonalPart() }; return false }) {
            privacy = true
            link = false
        } else {
            let powered = viewModel!.interactor.poweredByForConversation
            let present = powered != nil
            let vertical = withExtendedLifetime(powered) { traitCollection.verticalSizeClass }
            privacy = false
            link = present && vertical != .compact
        }
        if fromKeyboardNotification { applyFooterVisibility(privacy: privacy, link: link) }
        else { UIView.animate(withDuration: 0.3, animations: { [self] in self.applyFooterVisibility(privacy: privacy, link: link) }) }
    }
    // Source expression of the original shared body at 0x221c10.
    private func applyFooterVisibility(privacy: Bool, link: Bool) {
        privacyPolicyView.alpha = privacy ? 1 : 0
        if privacyPolicyView.isHidden != !privacy { privacyPolicyView.isHidden = !privacy }
        intercomLinkView.alpha = link ? 1 : 0
        if intercomLinkView.isHidden != !link { intercomLinkView.isHidden = !link }
    }
    func updateFooterNotice() {
        guard let conversation = viewModel!.conversation, let notice = conversation.footerNotice else { return }
        let title = notice.title
        let subtitle = notice.subtitle
        let avatars = notice.avatars
        footerNotice.configure(with: FooterNoticeViewModel(title: title, body: subtitle, avatarDetails: avatars))
    }
    var shouldShowFooterNoticePill: Bool {
        let present: Bool
        if let notice = viewModel!.conversation?.footerNotice {
            _ = FooterNoticeViewModel(title: notice.title, body: notice.subtitle, avatarDetails: notice.avatars)
            present = true
        } else { present = false }
        let hidden = keyboardHelper.isKeyboardHidden
        let accessibility = traitCollection.preferredContentSizeCategory.isAccessibilityCategory
        let vertical = traitCollection.verticalSizeClass
        return present && hidden && !accessibility && vertical != .compact
    }
}

extension ConversationViewController {
    private func setupView() {
        setupCollectionView()
        setupTicketBannerView()
        conversationCollectionView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(dismissKeyboardOnTap(_:))))
        let swipe = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeDown))
        swipe.direction = .down
        footerStack.addGestureRecognizer(swipe)
        setupFooterStack()
        view!.addSubview(voiceTranscriptionOverlay)
        let overlay = voiceTranscriptionOverlay
        let input = composer
        overlay.translatesAutoresizingMaskIntoConstraints = false
        overlay.topAnchor.constraint(equalTo: input.topAnchor).isActive = true
        pinHorizontal(overlay, to: input, safe: false)
        overlay.bottomAnchor.constraint(equalTo: input.bottomAnchor, constant: -CGFloat.zero).isActive = true
        setupScrollDownButton()
        setupFooterNotice()
        setupKeyboardEvents()
        setupLoadingView()
        navigationItem.titleView = collapsedHeaderView
    }
    private func setupCollectionView() {
        view!.addSubview(conversationCollectionView)
        let collection = conversationCollectionView
        collection.translatesAutoresizingMaskIntoConstraints = false
        collection.topAnchor.constraint(equalTo: collection.superview!.safeAreaLayoutGuide.topAnchor).isActive = true
        pinHorizontal(collection, safe: true)
        var inset = collection.contentInset
        inset.top = 16
        collection.contentInset = inset
        var indicator = collection.verticalScrollIndicatorInsets
        indicator.top = 16
        collection.verticalScrollIndicatorInsets = indicator
        if let last = lastItemIndexPath { collection.scrollToItem(at: last, at: .bottom, animated: true) }
    }
    private func setupTicketBannerView() {
        view!.addSubview(ticketBannerView)
        pinHorizontal(ticketBannerView, safe: false)
        ticketBannerView.translatesAutoresizingMaskIntoConstraints = false
        ticketBannerView.topAnchor.constraint(equalTo: conversationCollectionView.topAnchor).isActive = true
    }
    private func setupFooterStack() {
        let input = composer
        if let hosting = input.hostingController { addChild(hosting); hosting.didMove(toParent: self) }
        view!.addSubview(footerStack)
        footerStack.addArrangedSubview(input)
        footerStack.addArrangedSubview(intercomLinkView)
        footerStack.addArrangedSubview(privacyPolicyView)
        pinHorizontal(input, safe: false)
        pinHorizontal(privacyPolicyView, safe: true)
        pinHorizontal(footerStack, safe: true, inset: 8)
        footerStack.translatesAutoresizingMaskIntoConstraints = false
        footerStack.bottomAnchor.constraint(equalTo: view!.keyboardLayoutGuide.topAnchor, constant: -16).isActive = true
        let middle = conversationCollectionView.bottomAnchor.constraint(equalTo: composer.centerYAnchor)
        middle.priority = .defaultHigh
        middle.isActive = true
        let top = conversationCollectionView.bottomAnchor.constraint(equalTo: footerStack.topAnchor)
        top.priority = .defaultLow
        top.isActive = true
    }
    private func setupScrollDownButton() {
        view!.insertSubview(scrollDownButton, belowSubview: footerStack)
        scrollDownButton.translatesAutoresizingMaskIntoConstraints = false
        scrollDownButton.bottomAnchor.constraint(equalTo: footerStack.topAnchor).isActive = true
        scrollDownButton.translatesAutoresizingMaskIntoConstraints = false
        scrollDownButton.centerXAnchor.constraint(equalTo: scrollDownButton.superview!.safeAreaLayoutGuide.centerXAnchor).isActive = true
        scrollDownButton.alpha = 0
    }
    private func setupFooterNotice() {
        view!.insertSubview(footerNotice, belowSubview: footerStack)
        footerNotice.translatesAutoresizingMaskIntoConstraints = false
        footerNotice.bottomAnchor.constraint(equalTo: footerStack.topAnchor, constant: -8).isActive = true
        footerNotice.translatesAutoresizingMaskIntoConstraints = false
        footerNotice.centerXAnchor.constraint(equalTo: footerNotice.superview!.safeAreaLayoutGuide.centerXAnchor).isActive = true
        pinHorizontal(footerNotice, safe: true)
        footerNotice.onTap = { [weak self] in self?.handleScrollDownButtonTapped() }
        footerNotice.alpha = 0
    }
    // Source expression of the original inlined physical-edge constraints.
    private func pinHorizontal(_ child: UIView, to target: UIView? = nil, safe: Bool, inset: CGFloat = 0) {
        let rtl = child.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = target ?? child.superview!
            child.translatesAutoresizingMaskIntoConstraints = false
            child.translatesAutoresizingMaskIntoConstraints = false
            child.leftAnchor.constraint(equalTo: safe ? parent.safeAreaLayoutGuide.leftAnchor : parent.leftAnchor, constant: inset).isActive = true
        }
        func right() {
            let parent = target ?? child.superview!
            child.translatesAutoresizingMaskIntoConstraints = false
            child.translatesAutoresizingMaskIntoConstraints = false
            child.rightAnchor.constraint(equalTo: safe ? parent.safeAreaLayoutGuide.rightAnchor : parent.rightAnchor, constant: -inset).isActive = true
        }
        if rtl { right(); left() } else { left(); right() }
    }
}

extension ConversationViewController {
    private func setupKeyboardEvents() {
        keyboardHelper.callbacks[.willHide] = { [weak self] notification in
            guard let self else { return }
            let near = self.isScrolledNearBottom
            UIView.animate(withDuration: notification.timeInterval, delay: 0,
                options: notification.animationOptions.union(UIView.AnimationOptions(rawValue: 0x84)),
                animations: { [weak self] in self?.updateFooterViewVisibility(fromKeyboardNotification: true) }, completion: nil)
            if near {
                DispatchQueue.main.asyncAfter(deadline: .now() + notification.timeInterval) { [weak self] in
                    let collection: UICollectionView
                    do { guard let current = self else { return }; collection = current.conversationCollectionView }
                    collection.scrollToBottom(animated: true)
                }
            }
        }
        keyboardHelper.callbacks[.willShow] = { [weak self] notification in
            guard let self else { return }
            UIView.animate(withDuration: notification.timeInterval, delay: 0,
                options: notification.animationOptions.union(UIView.AnimationOptions(rawValue: 0x84)), animations: { [weak self] in
                    guard let self else { return }
                    self.applyFooterVisibility(privacy: false, link: false)
                    self.conversationCollectionView.scrollToBottom(animated: false)
                    self.adjustScrollDownButtonVisibilityForScroll(self.conversationCollectionView)
                }, completion: nil)
        }
        keyboardHelper.callbacks[.didHide] = { [weak self] _ in
            guard let self else { return }
            self.adjustScrollDownButtonVisibilityForScroll(self.conversationCollectionView)
        }
        keyboardHelper.callbacks[.didShow] = { [weak self] _ in
            guard let self else { return }
            self.adjustScrollDownButtonVisibilityForScroll(self.conversationCollectionView)
        }
    }
    @objc(dismissKeyboardOnTap:) private func dismissKeyboardOnTap(_ gesture: UITapGestureRecognizer?) { view!.endEditing(true) }
    @objc private func handleSwipeDown() { view!.endEditing(true) }

    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        scrollDirection = scrollView.contentOffset.y < lastContentOffset ? .up : .down
        lastContentOffset = scrollView.contentOffset.y
        adjustScrollDownButtonVisibilityForScroll(scrollView)
        if isScrolledNearBottom { viewModel!.userLastScrolledToBottom = .now }
    }
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView) { viewModel!.preventAutoScrolling() }
    private func adjustScrollDownButtonVisibilityForScroll(_ scrollView: UIScrollView) {
        if !hasScrolledPastMostRecentMessage {
            if scrollDownButton.alpha == 1 { animateScrollControl(scrollDownButton, visible: false) }
            if footerNotice.alpha == 1 { animateScrollControl(footerNotice, visible: false) }
            return
        }
        guard scrollDirection == .up else { return }
        let control: UIView = shouldShowFooterNoticePill ? footerNotice : scrollDownButton
        if control.alpha != 0 { return }
        control.layer.removeAllAnimations()
        control.alpha = 0
        control.transform = CGAffineTransform(translationX: 0, y: 50).scaledBy(x: 0.8, y: 0.8)
        animateScrollControl(control, visible: true, removeAnimations: false)
    }
    // Source expression of the original shared spring visibility body.
    private func animateScrollControl(_ control: UIView, visible: Bool, removeAnimations: Bool = true, completion: (() -> Void)? = nil) {
        if removeAnimations { control.layer.removeAllAnimations() }
        let animation = UIViewPropertyAnimator(duration: 0.48, dampingRatio: 0.75) {
            control.alpha = visible ? 1 : 0
            control.transform = visible ? .identity : CGAffineTransform(translationX: 0, y: 50).scaledBy(x: 0.8, y: 0.8)
        }
        animation.addCompletion { _ in completion?() }
        animation.startAnimation()
    }
    private func handleScrollDownButtonTapped() {
        let unread = viewModel!.cells.first { if case .unreadMessageDivider = $0 { return true }; return false }
        if let unread, let index = indexPath(for: unread) {
            if conversationCollectionView.hasScrolledPastItem(at: index) { conversationCollectionView.scrollToBottom(animated: true) }
            else { conversationCollectionView.scrollToItem(at: index, at: .top, animated: true) }
        } else { conversationCollectionView.scrollToBottom(animated: true) }
        viewModel!.userLastScrolledToBottom = .now
    }
    private func indexPath(for type: ConversationCellType) -> IndexPath? {
        let sections = collectionViewDataSource.snapshot().sectionIdentifiers
        for section in sections {
            if let item = collectionViewDataSource.snapshot().itemIdentifiers(inSection: section).firstIndex(of: type) {
                return IndexPath(row: item, section: section.rawValue)
            }
        }
        return nil
    }
}

extension ConversationViewController {
    private func makeFooterBackdrop() -> UIView {
        let backdrop = UIView()
        backdrop.isHidden = true
        return backdrop
    }
    private func makeScrollDownButton() -> FloatingActionButton {
        let image = ICMAssets.roundedChevronDown()!
        let onTap: () -> Void = { [weak self] in self?.handleScrollDownButtonTapped() }
        let button = FloatingActionButton(image: image, onTap: onTap, themeProvider: DefaultThemeProvider.shared)
        button.alpha = 0
        return button
    }
}

extension ConversationViewController {
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        conversationCollectionView.collectionViewLayout.invalidateLayout()
        if footerNotice.alpha == 1 { animateScrollControl(footerNotice, visible: false) }
        if scrollDownButton.alpha == 1 { animateScrollControl(scrollDownButton, visible: false) }
        updateFooterViewVisibility(fromKeyboardNotification: false)
    }
    func openHeaderSheet(with header: ConversationHeader, conversation: ICMConversation?) {
        guard let expanded = header.expanded else { return }
        let width = view!.bounds.width
        let currentView = view!
        let preferredWidth: CGFloat?
        if width > 0 { preferredWidth = currentView.bounds.width }
        else { preferredWidth = currentView.window?.bounds.width }
        let navigation = Self.makeHeaderSheetNavigationController(expandedHeader: expanded,
            displayActiveIndicator: header.displayActiveIndicator, conversation: conversation, preferredWidth: preferredWidth)
        present(navigation, animated: true, completion: nil)
    }
    static func makeHeaderSheetNavigationController(expandedHeader: ExpandedConversationHeader,
        displayActiveIndicator: Bool, conversation: ICMConversation?, preferredWidth: CGFloat?) -> UINavigationController {
        let config = ICMConfig.shared()!
        let theme = DefaultThemeProvider.shared
        let header = SheetConversationHeaderViewController(expandedHeader: expandedHeader, displayActiveIndicator: displayActiveIndicator,
            conversation: conversation, preferredWidth: preferredWidth, config: config, themeProvider: theme)
        let navigation = UINavigationController(rootViewController: header)
        navigation.isNavigationBarHidden = true
        navigation.modalPresentationStyle = .pageSheet
        guard let sheet = navigation.sheetPresentationController else { return navigation }
        if #available(iOS 16, *) {
            sheet.detents = [.custom(identifier: .init("conversationHeaderContentFit")) { [weak header] context in
                guard let header else { return context.maximumDetentValue }
                let content = header.contentView
                let width = header.availableWidth
                let fitted = content.systemLayoutSizeFitting(CGSize(width: width, height: UIView.layoutFittingCompressedSize.height),
                    withHorizontalFittingPriority: .required, verticalFittingPriority: .fittingSizeLevel).height
                let maximum = context.maximumDetentValue
                return maximum < fitted ? maximum : fitted
            }]
        } else { sheet.detents = [.medium(), .large()] }
        sheet.prefersGrabberVisible = true
        sheet.prefersEdgeAttachedInCompactHeight = true
        return navigation
    }
}

extension ConversationViewController {
    private func makePreviewHelper() -> PreviewHelper {
        let failure: ([URL], Error?) -> Void = { [weak self] _, _ in self?.openMediaSelectionErrorAlert() }
        let queue = AsyncQueue.main
        let fileManager = FileManager.default
        let session = URLSession.shared
        let callbackQueue = DispatchQueue.main
        let manager = AttachmentFileManager(fileManager: fileManager, urlSession: session, callbackQueue: callbackQueue)
        return PreviewHelper(mainQueue: queue, attachmentFileManager: manager, onFailure: failure)
    }
    private func makeTicketBannerView() -> TicketBannerView {
        let conversation = ICMConversation()
        let theme = DefaultThemeProvider.shared
        let banner = TicketBannerView(frame: .zero, conversation: conversation, themeProvider: theme)
        banner.translatesAutoresizingMaskIntoConstraints = false
        banner.isHidden = true
        return banner
    }
    private func makeComposerViewModel() -> ComposerViewModel {
        let upload: (URL, @escaping (Result<(uploadURL: String, upload: ITBUpload), Error>) -> Void) -> Void = { [weak self] url, completion in
            guard let self, let model = self.viewModel else {
                completion(.failure(NSError(domain: "ConversationViewController", code: -1,
                    userInfo: [NSLocalizedDescriptionKey: "ViewModel not available"])))
                return
            }
            withExtendedLifetime(self) { model.interactor.preUploadFile(url: url, completion: completion) }
        }
        let config = ICMConfig.shared()!
        return ComposerViewModel(config: config, onMediaPreUpload: upload)
    }
}

extension ConversationViewController {
    func openPreview(for url: URL) {
        let title = TranslationManager.messenger.translation(forKey: "surveys_done_button")
        let helper = previewHelper
        // The original preview action at 0x9188 is intentionally empty.
        let action = PreviewAction(mode: .view, buttonType: .text(title: title), handler: { _ in })
        helper.preview(urls: [url], action: action, from: self)
    }
}

extension ConversationViewController: FilePickerDelegate, MediaPickerDelegate, CameraPickerDelegate {
    private func makeFilesPicker() -> FilePicker {
        let manager = makeAttachmentFileManager()
        let config = ICMConfig.shared()!
        return FilePicker(attachmentFileManager: manager, config: config, delegate: self)
    }
    private func makeMediaPicker() -> MediaPicker { MediaPicker(attachmentFileManager: makeAttachmentFileManager(), delegate: self) }
    private func makeCameraPicker() -> CameraPicker { CameraPicker(attachmentFileManager: makeAttachmentFileManager(), delegate: self) }
    // The original default manager constructor is shared at 0x21697c.
    private func makeAttachmentFileManager() -> AttachmentFileManager {
        let files = FileManager.default
        let session = URLSession.shared
        let queue = DispatchQueue.main
        return AttachmentFileManager(fileManager: files, urlSession: session, callbackQueue: queue)
    }
    func picker(_ picker: FilePicker, didPickMedia items: [(id: UUID, type: ComposerMediaType)]) -> [UUID] {
        admitPickedMedia(items)
    }
    func picker(_ picker: MediaPicker, didPickMedia items: [(id: UUID, type: ComposerMediaType)]) -> [UUID] {
        admitPickedMedia(items)
    }
    private func admitPickedMedia(_ items: [(id: UUID, type: ComposerMediaType)]) -> [UUID] {
        var accepted: [UUID] = []
        for item in items {
            if composerViewModel.stagePlaceholderMedia(id: item.id, type: item.type) { accepted.append(item.id) }
        }
        return accepted
    }
    func picker(_ picker: CameraPicker, didPickMedia id: UUID, type: ComposerMediaType) -> Bool {
        composerViewModel.stagePlaceholderMedia(id: id, type: type)
    }
    func picker(_ picker: FilePicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>) {
        handlePickedMedia(id: id, result: result)
    }
    func picker(_ picker: MediaPicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>) {
        handlePickedMedia(id: id, result: result)
    }
    func picker(_ picker: CameraPicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>) {
        handlePickedMedia(id: id, result: result)
    }
    private func handlePickedMedia(id: UUID, result: Result<URL, PickerError>) {
        switch result {
        case .success(let url): composerViewModel.updatePlaceholderMedia(id: id, url: url)
        case .failure(let error):
            composerViewModel.removeMedia(id: id)
            if case .cameraAccessDenied = error {
                presentPermissionDeniedAlert(titleKey: "camera_access_denied", messageKey: "enable_camera_permission")
            } else { openMediaSelectionErrorAlert() }
        }
    }
}

extension ConversationViewController {
    func openMediaSelectionErrorAlert() {
        let translations = TranslationManager.messenger
        let title = translations.translation(forKey: "error_selecting_media_title")
        let message = translations.translation(forKey: "error_selecting_media_title_description")
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: translations.translation(forKey: "ok"), style: .default, handler: nil))
        present(alert, animated: true, completion: nil)
    }
    func presentPermissionDeniedAlert(titleKey: String, messageKey: String) {
        let name = ICMUtils.applicationName()!
        let parameters = ["app_name": name]
        let translations = TranslationManager.messenger
        let title = translations.translation(forKey: titleKey, params: parameters)
        let message = translations.translation(forKey: messageKey)
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: translations.translation(forKey: "cancel"), style: .cancel, handler: nil))
        alert.addAction(UIAlertAction(title: translations.translation(forKey: "settings"), style: .default) { _ in
            guard let url = URL(string: UIApplication.openSettingsURLString) else { return }
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        })
        present(alert, animated: true, completion: nil)
    }
    func openFileTooBigAlert(with limit: String, for id: UUID) {
        let translations = TranslationManager.messenger
        let title = translations.translation(forKey: "failed_to_send")
        let message = translations.translation(forKey: "file_too_big", params: ["limit": limit])
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: translations.translation(forKey: "delete"), style: .destructive) { [weak self] _ in
            let model: ComposerViewModel
            do { guard let current = self else { return }; model = current.composerViewModel }
            model.removeMedia(id: id)
        })
        view!.endEditing(true)
        present(alert, animated: true, completion: nil)
    }
    func openUploadFailureAlert(for id: UUID) {
        let translations = TranslationManager.messenger
        let alert = UIAlertController(title: translations.translation(forKey: "upload_file_load_error"), message: nil, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: translations.translation(forKey: "try_again"), style: .default) { [weak self] _ in
            let model: ComposerViewModel
            do { guard let current = self else { return }; model = current.composerViewModel }
            model.retryUpload(id: id)
        })
        alert.addAction(UIAlertAction(title: translations.translation(forKey: "delete"), style: .destructive) { [weak self] _ in
            guard let self else { return }
            self.removePickerTasks(for: id)
            self.composerViewModel.removeMedia(id: id)
        })
        present(alert, animated: true, completion: nil)
    }
    // The original shared inline cancellation sequence removes entries before canceling.
    private func removePickerTasks(for id: UUID) {
        let mediaTask = mediaPicker.processingTasks.removeValue(forKey: id)
        mediaTask?.cancel()
        let fileTask = filesPicker.processingTasks.removeValue(forKey: id)
        fileTask?.cancel()
        cameraPicker.cancelProcessing(id: id)
    }
}

extension ConversationViewController {
    func openMessageRetrySheet(_ sender: UIView) {
        let translations = TranslationManager.messenger
        let alert = UIAlertController(title: translations.translation(forKey: "failed_delivery"), message: nil, preferredStyle: .actionSheet)
        let retry = UIAlertAction(title: translations.translation(forKey: "try_again"), style: .default) { [weak model = viewModel] _ in model?.retryMessage() }
        let cancel = UIAlertAction(title: translations.translation(forKey: "cancel"), style: .cancel, handler: nil)
        let delete = UIAlertAction(title: translations.translation(forKey: "delete"), style: .destructive) { [weak model = viewModel] _ in
            guard let model, let part = model.failedPart else { return }
            model.interactor.delete(part: part, completion: model.successHandler)
        }
        alert.addAction(retry)
        alert.addAction(cancel)
        alert.addAction(delete)
        alert.popoverPresentationController?.sourceView = sender.superview
        alert.popoverPresentationController?.sourceRect = sender.frame
        present(alert, animated: true, completion: nil)
    }
    func openMessageDeleteSheet(_ sender: UIView) {
        let translations = TranslationManager.messenger
        let alert = UIAlertController(title: translations.translation(forKey: "start_conversation_error"), message: nil, preferredStyle: .actionSheet)
        let cancel = UIAlertAction(title: translations.translation(forKey: "cancel"), style: .cancel, handler: nil)
        let delete = UIAlertAction(title: translations.translation(forKey: "delete"), style: .destructive) { [weak model = viewModel] _ in
            guard let model, let part = model.failedPart else { return }
            model.interactor.delete(part: part, completion: model.successHandler)
        }
        alert.addAction(cancel)
        alert.addAction(delete)
        alert.popoverPresentationController?.sourceView = sender.superview
        alert.popoverPresentationController?.sourceRect = sender.frame
        present(alert, animated: true, completion: nil)
    }
}

extension ConversationViewController {
    @objc func updateComposer() {
        let collection = conversationCollectionView
        let height = collection.contentSize.height
        let wasAtBottom: Bool
        if height <= 0 { wasAtBottom = true }
        else {
            let offset = collection.contentOffset.y
            let frame = collection.frame.height
            _ = collection.contentInset
            let bottom = collection.contentInset.bottom
            wasAtBottom = height <= ((offset + frame) + bottom)
        }
        let input = composer
        let model = viewModel!
        let translated = TranslationManager.messenger.translation(forKey: "message_placeholder")
        input.placeholderText = model.conversation?.composerState?.placeholder ?? translated
        let enabledModel = viewModel!
        input.isEnabled = enabledModel.conversation?.composerState?.displayState != 1
        let hidden = input.isHidden
        let visibilityModel = viewModel!
        if hidden != (visibilityModel.conversation?.messageComposerShouldBeDisplayed() ?? false) { return }
        let show = visibilityModel.conversation?.messageComposerShouldBeDisplayed() ?? false
        animateVisibility(input, to: show) { [weak self] in
            guard let self else { return }
            let backdrop = self.footerBackdrop
            let model = self.viewModel!
            let visible = !(model.conversation?.messageComposerShouldBeDisplayed() ?? false)
            self.animateVisibility(backdrop, to: visible) { [weak self] in
                guard let self else { return }
                self.view!.endEditing(true)
                if wasAtBottom {
                    DispatchQueue.main.async { [weak self] in
                        let collection: UICollectionView
                        do { guard let current = self else { return }; collection = current.conversationCollectionView }
                        collection.scrollToBottom(animated: true)
                    }
                }
            }
        }
    }
    // Source expression of the original UIView visibility animator at 0x3263b4.
    private func animateVisibility(_ target: UIView, to visible: Bool, completion: (() -> Void)?) {
        if visible != target.isHidden { return }
        if visible { target.alpha = 0; target.isHidden = false }
        let animation = UIViewPropertyAnimator(duration: 0.3, curve: .easeInOut) { target.alpha = visible ? 1 : 0 }
        animation.addCompletion { position in
            if position == .end {
                if !visible { target.isHidden = true }
                completion?()
            }
        }
        animation.startAnimation()
    }
    func addCloseConversationView(showNewConversationButton: Bool, buttonData: ConversationEndedButton?) {
        let delegate: (any ClosedConversationViewDelegate)? = viewModel
        let manager = ICMDataManager.shared()!
        let theme = DefaultThemeProvider.shared
        let closed = ClosedConversationView(buttonData: buttonData, delegate: delegate,
            startNewConversationButtonEnabled: showNewConversationButton, dataManager: manager, themeProvider: theme)
        closed.isHidden = true
        closed.backgroundColor = .clear
        footerStack.insertArrangedSubview(closed, at: 0)
        pinHorizontal(closed, safe: false)
        closedConversationView = closed
    }
    func showClosedConversationView(_ show: Bool) {
        closedConversationView?.isHidden = !show
        footerStack.backgroundColor = show ? themeProvider.theme.base : .clear
        if !show { return }
        if let last = lastItemIndexPath { conversationCollectionView.scrollToItem(at: last, at: .bottom, animated: true) }
        view!.endEditing(true)
    }
}

extension ConversationViewController {
    func showVoiceTranscription(viewModel: AudioRecorderViewModel) {
        view!.endEditing(true)
        let dismiss: () -> Void = { [weak self] in self?.hideVoiceTranscription() }
        let representable = VoiceTranscriptionUIViewRepresentable(viewModel: viewModel, onDismiss: dismiss, themeProvider: themeProvider)
        let host = UIHostingController(rootView: representable)
        host.view!.backgroundColor = .clear
        host.view!.translatesAutoresizingMaskIntoConstraints = false
        voiceTranscriptionController = host
        addChild(host)
        voiceTranscriptionOverlay.addSubview(host.view!)
        let content = host.view!
        let overlay = voiceTranscriptionOverlay
        content.translatesAutoresizingMaskIntoConstraints = false
        content.topAnchor.constraint(equalTo: overlay.topAnchor).isActive = true
        content.translatesAutoresizingMaskIntoConstraints = false
        content.leftAnchor.constraint(equalTo: overlay.leftAnchor).isActive = true
        content.translatesAutoresizingMaskIntoConstraints = false
        content.bottomAnchor.constraint(equalTo: overlay.bottomAnchor, constant: -CGFloat.zero).isActive = true
        content.translatesAutoresizingMaskIntoConstraints = false
        content.rightAnchor.constraint(equalTo: overlay.rightAnchor, constant: -CGFloat.zero).isActive = true
        host.didMove(toParent: self)
        composer.alpha = 0
        voiceTranscriptionOverlay.isHidden = false
    }
    func hideVoiceTranscription() {
        voiceTranscriptionOverlay.isHidden = true
        composer.alpha = 1
        voiceTranscriptionController?.willMove(toParent: nil)
        voiceTranscriptionController?.view!.removeFromSuperview()
        voiceTranscriptionController?.removeFromParent()
        voiceTranscriptionController = nil
        viewModel!.voiceTranscriptionDismissed()
    }
}

extension ConversationViewController {
    @MainActor func openCamera() async {
        let permitted = await cameraPicker.openCamera(from: self)
        if !permitted { presentPermissionDeniedAlert(titleKey: "camera_access_denied", messageKey: "enable_camera_permission") }
    }
    private func makeComposer() -> ComposerViewWrapper {
        let composerModel = composerViewModel
        let inputs = viewModel!.composerInputs
        let model = viewModel!
        let initial = model.interactor.initialMessage
        let translated = TranslationManager.messenger.translation(forKey: "message_placeholder")
        let placeholder = model.conversation?.composerState?.placeholder ?? translated
        let wrapper = ComposerViewWrapper(viewModel: composerModel, composerInputs: inputs, initialText: initial,
            placeholderText: placeholder, isEnabled: true, themeProvider: DefaultThemeProvider.shared)
        wrapper.isHidden = true
        wrapper.delegate = viewModel
        wrapper.translatesAutoresizingMaskIntoConstraints = false
        wrapper.onAttachmentAction = { [weak self] action in
            guard let self, let model = self.viewModel else { return }
            switch action {
            case .camera:
                if let current = model.view { Task { @MainActor in await current.openCamera() } }
                model.addMetricForComposerInputTapped("camera_input")
            case .photos: model.openGalleryAction()
            case .files:
                guard let current = model.view else { return }
                let input = current.composerViewModel
                let count: Int
                switch input.mediaState { case .none: count = 0; case .hasMedia(let items): count = items.count }
                let limit = count < 10 ? 10 - count : 1
                current.filesPicker.presentFilePicker(from: current, selectionLimit: limit)
            case .gif: model.composerDidSelectInput(.gif)
            case .mic: model.composerDidSelectInput(.mic)
            }
        }
        wrapper.onMediaTap = { [weak self] url in self?.openPreview(for: url) }
        wrapper.onMediaErrorTap = { [weak self] id, error in
            guard let self else { return }
            if error == .fileTooLarge {
                let limit = ICMConfig.shared()!.attachmentSettings.uploadSizeLimit
                let formatted: String
                do {
                    let model = self.viewModel!
                    let formatter = ByteCountFormatter()
                    formatter.allowedUnits = .useMB
                    formatter.countStyle = .binary
                    formatter.includesUnit = false
                    formatted = withExtendedLifetime(model) { formatter.string(fromByteCount: Int64(limit)) }
                }
                self.openFileTooBigAlert(with: formatted, for: id)
            } else { self.openUploadFailureAlert(for: id) }
        }
        wrapper.onMediaRemove = { [weak self] id in self?.removePickerTasks(for: id) }
        return wrapper
    }
}

extension ConversationViewController {
    func invalidateLayoutForPart(with id: String) {
        let snapshot = collectionViewDataSource.snapshot()
        let items = snapshot.itemIdentifiers(inSection: .main)
        guard let index = items.firstIndex(where: { type in
            guard case .message(let part) = type, let partId = part.partId else { return false }
            return partId == id
        }) else { return }
        let context = ChatLayoutInvalidationContext()
        let item = IndexPath(item: index, section: 0)
        context.invalidateItems(at: [item])
        conversationCollectionView.collectionViewLayout.invalidateLayout(with: context)
    }
}

extension ConversationViewController {
    func openAttachmentMenu() {
        let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
        if let popover = alert.popoverPresentationController {
            let wrapper = composer
            if let hostView = wrapper.hostingController?.view { popover.sourceView = hostView }
            else { popover.sourceView = wrapper }
        }
        let actions: [UIAlertAction]
        do {
            let model = viewModel!
            let picker = mediaPicker
            actions = withExtendedLifetime(picker) { model.attachmentActions(mediaPicker: picker) }
        }
        for action in actions { alert.addAction(action) }
        present(alert, animated: true, completion: nil)
    }
}

extension ConversationViewController {
    func openAppSheet(with url: URL, params: [AnyHashable: Any], delegate: (any ICMMessengerSheetViewControllerDelegate)?) {
        if let article = ICMUtils.extractArticleId(fromSheetParams: params) {
            ICMPresentationManager.sharedInstance()!.presentArticle(article, allowReactions: true, anchorLink: nil)
            let metrics = HelpCenterMetricService()
            withExtendedLifetime(metrics) { HelpCenterMetricService.addOpenedFromConversationMessengerAppEvent() }
            return
        }
        guard let sheet = ICMMessengerSheetViewController(sheetURL: url, params: params, delegate: delegate) else { return }
        ICMPresentationManager.sharedInstance()!.presentSheet(sheet)
    }
}

extension UIViewController {
    func presentMessengerAppInputAlert(with payload: MessengerAppTextInputPayload, completion: @escaping () -> Void) {
        let alert = UIAlertController(title: payload.title, message: nil, preferredStyle: .alert)
        alert.addTextField { field in
            field.placeholder = payload.placeholder
            field.text = payload.currentText
        }
        let ok = UIAlertAction(title: TranslationManager.messenger.translation(forKey: "ok"), style: .default) { _ in
            payload.completionBlock?(alert)
            completion()
        }
        let cancel = UIAlertAction(title: TranslationManager.messenger.translation(forKey: "cancel"), style: .cancel) { _ in completion() }
        alert.addAction(ok)
        alert.addAction(cancel)
        present(alert, animated: true, completion: nil)
    }
}

extension ConversationViewController {
    func openCitationView(with citations: [ITBInlineCitation], sources: [FinAnswerSource]) {
        let delegate = viewModel
        let theme = DefaultThemeProvider.shared
        let citation = CitationViewController(citations: citations, sources: sources, delegate: delegate, themeProvider: theme)
        let navigation = UINavigationController(rootViewController: citation)
        navigation.isNavigationBarHidden = true
        navigation.modalPresentationStyle = .pageSheet
        navigation.sheetPresentationController?.detents = [.medium()]
        present(navigation, animated: true, completion: nil)
    }
}

extension ConversationViewController: ICMReactionsViewControllerDelegate {
    func showReactionsIfNecessary(reactionSet: ICMReactionsSet) {
        if isDisplayingReactionsView { return }
        let theme = themeProvider
        let reactions = ICMReactionsViewController(reactionSet: reactionSet, separatorColor: theme.theme.neutralBorder,
            reactionsLabel: nil, delegate: self)
        guard let content = reactions?.view else { return }
        content.translatesAutoresizingMaskIntoConstraints = false
        content.backgroundColor = theme.theme.base
        addChild(reactions!)
        view!.addSubview(content)
        reactions!.didMove(toParent: self)
        let constraints = [content.bottomAnchor.constraint(equalTo: footerStack.topAnchor),
            content.leadingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.leadingAnchor),
            content.trailingAnchor.constraint(equalTo: view!.safeAreaLayoutGuide.trailingAnchor),
            content.heightAnchor.constraint(equalToConstant: 52)]
        for constraint in constraints { constraint.isActive = true }
        isDisplayingReactionsView = true
        footerStack.setNeedsLayout()
    }
    func reactionsViewController(_ controller: ICMReactionsViewController!, reactedWith option: ICMReactionsOption!) {
        let model = viewModel!
        let index = option.index?.intValue ?? 0
        if model.conversation == nil { return }
        guard let id = model.interactor.conversationId else { return }
        let client = model.interactor.apiBridge.httpClient
        client.react(toConversation: id, withReactionIndex: UInt(index))
    }
}

extension ConversationViewController {
    func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
        if velocity.y > 1.5 { view!.endEditing(true) }
        let target = targetContentOffset.pointee
        let size = scrollView.bounds.size
        let visible = CGRect(origin: target, size: size)
        let videos = videoViews(in: scrollView.subviews)
        for video in videos {
            let rect = video.convert(video.bounds, to: scrollView as any UICoordinateSpace)
            if rect.intersects(visible) { video.isPausedOffscreen = false }
            else if !video.isPausedOffscreen {
                video.isPausedOffscreen = true
                video.webView.pauseAllMediaPlayback(completionHandler: nil)
            }
        }
    }
    // The original traversal stops descending once it finds a real VideoBlockView.
    private func videoViews(in views: [UIView]) -> [VideoBlockView] {
        views.flatMap { child in
            if let video = child as? VideoBlockView { return [video] }
            return videoViews(in: child.subviews)
        }
    }
}

extension ConversationViewController {
    func openGIFPicker() {
        let fetcher: (String, @escaping (Result<[[AnyHashable: Any]], Error>) -> Void) -> Void
        do { let model = viewModel!; fetcher = model.gifFetcher }
        let picker = GIFPickerViewController(fetchGIFs: fetcher)
        let navigation = UINavigationController(rootViewController: picker)
        navigation.modalPresentationStyle = traitCollection.horizontalSizeClass == .regular ? .overFullScreen : .automatic
        navigation.setNavigationBarHidden(true, animated: false)
        picker.delegate = self
        present(navigation, animated: true, completion: nil)
    }
}
