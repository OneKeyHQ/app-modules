import UIKit

@objc(_TtC18IntercomSDKPrivate13BlockPartView)
class BlockPartView: UIView, ConversationCellContentView, CitationTapDelegate, UIContextMenuInteractionDelegate, UIGestureRecognizerDelegate {
    private weak var delegate: (any ConversationCellBlockDelegate)?
    private var timestampView: PartStatusView?
    private var displayTimestampTimer: Timer?
    let content: ICMConversationPart
    private lazy var chatBubbleStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.layoutMargins = content.isSingleMediaPart() ? .zero : UIEdgeInsets(top: 12, left: 16, bottom: 12, right: 16)
        stack.isLayoutMarginsRelativeArrangement = true
        stack.spacing = 8
        if content.isPersonalPart() {
            if content.blocksContainAParagraphBlock() { stack.backgroundColor = content.getBackgroundColor(theme: DefaultThemeProvider.shared) }
        } else if !content.isSingleMediaPart() {
            stack.backgroundColor = content.getBackgroundColor(theme: DefaultThemeProvider.shared)
        }
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.addInteraction(UIContextMenuInteraction(delegate: self))
        return stack
    }()
    private lazy var containerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.distribution = .fill
        stack.spacing = 4
        stack.alignment = content.isPersonalPart() ? .trailing : (isSingleImageBlockPart ? .leading : .fill)
        return stack
    }()
    lazy var blockStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.distribution = .fill
        stack.spacing = 12
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.alignment = content.isPersonalPart() ? .trailing : .leading
        return stack
    }()
    private lazy var chatBubbleWrapper: UIView = {
        let view = UIView()
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var contentStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 2
        stack.alignment = content.isPersonalPart() ? .trailing : .fill
        stack.distribution = .fill
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private let maxWidth: CGFloat
    private weak var singleImageBlockView: UIView?
    private var statusLeadingConstraint: NSLayoutConstraint?
    private var statusTrailingConstraint: NSLayoutConstraint?

    init(content: ICMConversationPart, maxWidth: CGFloat, delegate: (any ConversationCellBlockDelegate)?, queue: any Dispatcher) {
        self.content = content
        self.delegate = delegate
        self.maxWidth = maxWidth
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        configureLayout(content)
        if !self.content.displayPartStatus {
            let tap = UITapGestureRecognizer(target: self, action: #selector(handleTap(_:)))
            tap.delegate = self
            containerStackView.addGestureRecognizer(tap)
        }
        if content.isPersonalPart() {
            let capturedDelegate = self.delegate
            let anchor = contentStackView
            handleFailure(for: content, anchorTo: anchor) { [capturedDelegate, content] button in
                capturedDelegate?.retryTapped(conversationPart: content, sender: button)
            }
        }
    }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    deinit { displayTimestampTimer?.invalidate() }

    private func configureLayout(_ part: ICMConversationPart) {
        addSubview(containerStackView)
        containerStackView.addArrangedSubview(contentStackView)
        if part.isPersonalPart() { layoutForHuman(part) }
        else { layoutForAdmin() }
        addBlocks()
        pinEdges(containerStackView)
        let inset: CGFloat = part.isPersonalPart() ? 0 : (content.isSingleMediaPart() ? 0 : 16)
        let attributed = isAttributedSingleImagePart
        addStatusView(in: containerStackView, leadingInset: inset, attributedSingleImage: attributed)
        constrainStatusToSingleImageIfNeeded()
        configureAccessibility()
    }
    private func computedMaximumWidth() -> CGFloat {
        let doubled = (maxWidth - 8) + (maxWidth - 8)
        if content.partStyle == 7 { return maxWidth }
        if doubled < 268 { return 268 }
        if doubled > 300 { return 300 }
        return doubled
    }
    private func humanMaximumWidth() -> CGFloat {
        if content.partStyle == 7 { return maxWidth }
        return computedMaximumWidth()
    }
    private func layoutForHuman(_ part: ICMConversationPart) {
        if !part.blocksContainAParagraphBlock() { return }
        chatBubbleWrapper.addSubview(chatBubbleStackView)
        chatBubbleStackView.addArrangedSubview(blockStackView)
        chatBubbleStackView.topAnchor.constraint(equalTo: chatBubbleWrapper.topAnchor).isActive = true
        chatBubbleStackView.bottomAnchor.constraint(equalTo: chatBubbleWrapper.bottomAnchor).isActive = true
        chatBubbleWrapper.leadingAnchor.constraint(lessThanOrEqualTo: chatBubbleStackView.leadingAnchor).isActive = true
        constrainBubbleWidth(humanMaximumWidth())
        pinTrailing(contentStackView)
        let sign: CGFloat = direction(chatBubbleStackView) ? -1 : 1
        chatBubbleStackView.trailingAnchor.constraint(equalTo: chatBubbleWrapper.trailingAnchor, constant: sign * 0).isActive = true
    }
    private func layoutForAdmin() {
        contentStackView.addArrangedSubview(chatBubbleWrapper)
        chatBubbleWrapper.addSubview(chatBubbleStackView)
        chatBubbleStackView.addArrangedSubview(blockStackView)
        chatBubbleStackView.topAnchor.constraint(equalTo: chatBubbleWrapper.topAnchor).isActive = true
        chatBubbleStackView.bottomAnchor.constraint(equalTo: chatBubbleWrapper.bottomAnchor).isActive = true
        chatBubbleWrapper.leadingAnchor.constraint(lessThanOrEqualTo: chatBubbleStackView.leadingAnchor).isActive = true
        constrainBubbleWidth(computedMaximumWidth())
        pinHorizontal(contentStackView)
        let sign: CGFloat = direction(chatBubbleStackView) ? -1 : 1
        chatBubbleStackView.leadingAnchor.constraint(equalTo: chatBubbleWrapper.leadingAnchor, constant: sign * 0).isActive = true
    }
    private func constrainBubbleWidth(_ width: CGFloat) {
        let equal: Bool
        if content.isCreateTicketPart() { equal = true }
        else { equal = content.hasMessengerCard() }
        let constraint = equal ? chatBubbleStackView.widthAnchor.constraint(equalToConstant: width) : chatBubbleStackView.widthAnchor.constraint(lessThanOrEqualToConstant: width)
        constraint.isActive = true
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        updateTemporaryTimestampDisplayTimerIfNecessary(in: containerStackView)
        applyGroupingPosition()
    }
    private func applyGroupingPosition() {
        layoutIfNeeded()
        if content.isSingleMediaPart() { return }
        let grouping = chatBubbleGroupingPosition
        let stack = chatBubbleStackView
        let personal = content.isPersonalPart()
        stack.applyCornerRadiusForGrouping(grouping, bubbleType: personal ? .human : .admin, bubbleRadius: BubbleRadius(large: 20, small: 4), supportsCornerConfiguration: UIView.supportsCornerConfiguration)
    }
    private var chatBubbleGroupingPosition: Int {
        if !content.isPersonalPart() { return Int(bitPattern: content.groupingPosition) }
        let blocks: [ITBBlock] = content.blocks!
        if !blocks.contains(where: { $0.type() == 5 || $0.type() == 14 }) { return Int(bitPattern: content.groupingPosition) }
        if !content.blocksContainAParagraphBlock() { return Int(bitPattern: content.groupingPosition) }
        let groups = BlockGroupInfo(blocks: content.blocks!)
        let grouping = Int(bitPattern: content.groupingPosition)
        if groups.count >= 2 {
            if grouping == 0 { return 1 }
            if grouping == 3 { return 2 }
        }
        return grouping
    }
    private var isSingleImageBlockPart: Bool {
        let blocks: [ITBBlock] = content.blocks!
        if blocks.count != 1 { return false }
        let fresh: [ITBBlock] = content.blocks!
        return fresh.first?.type() == 5
    }
    private var isAttributedSingleImagePart: Bool {
        if !isSingleImageBlockPart { return false }
        guard let attribution = content.attribution else { return false }
        return !attribution.isEmpty
    }
    private func addBlocks() {
        let blocks: [ITBBlock] = content.isPersonalPart() ? content.reorderedBlocksForHuman() : content.blocks!
        if content.isPersonalPart() { addBlocksForHuman(blocks) }
        else { addBlocksForAdmin() }
    }
    private func addBlocksForAdmin() {
        var capturedDelegate = delegate
        let width = computedMaximumWidth()
        let views = BlockViewFactory.getViewsForBlocks(in: content, delegate: capturedDelegate, citationDelegate: self, maxWidth: width, imageDisplayContext: .context0)
        capturedDelegate = nil
        for view in views {
            blockStackView.addArrangedSubview(view)
            if content.isSingleMediaPart() { pinLeading(view) }
            else { pinHorizontal(view) }
        }
        if isSingleImageBlockPart { singleImageBlockView = views.first }
    }
    private func addBlocksForHuman(_ blocks: [ITBBlock]) {
        let groups = BlockGroupInfo(blocks: blocks)
        if !groups.images.isEmpty {
            if let attribution = content.attribution, !attribution.isEmpty, groups.images.count == 1 {
                addMediaBlock(groups.images[0], index: 0, totalGroups: groups.count)
            } else { addImageGrid(groups.images, totalGroups: groups.count, index: 0) }
        }
        let imagePrefix = groups.images.isEmpty ? 0 : 1
        for (index, block) in groups.media.enumerated() { addMediaBlock(block, index: imagePrefix + index, totalGroups: groups.count) }
        if !groups.paragraphs.isEmpty {
            contentStackView.addArrangedSubview(chatBubbleWrapper)
            for (index, block) in groups.paragraphs.enumerated() { addTextBlock(block, index: imagePrefix + groups.media.count + index, totalGroups: groups.count) }
        }
    }
    private func mappedGrouping(index: Int, totalGroups: Int) -> UInt {
        let grouping = content.groupingPosition
        if totalGroups < 2 { return grouping }
        switch grouping {
        case 0: return index == 0 ? 0 : 1
        case 3: return index == 0 ? 0 : (index == totalGroups - 1 ? 2 : 1)
        case 2: return index == totalGroups - 1 ? 2 : 1
        default: return grouping
        }
    }
    private func wrapperPart(_ block: ITBBlock, index: Int, totalGroups: Int) -> ICMConversationPart {
        let grouping = mappedGrouping(index: index, totalGroups: totalGroups)
        let part = ICMConversationPart()
        part.blocks = [block]
        part.groupingPosition = grouping
        part.participantId = content.participantId
        part.partId = content.partId
        return part
    }
    private func addMediaBlock(_ block: ITBBlock, index: Int, totalGroups: Int) {
        let wrapper = wrapperPart(block, index: index, totalGroups: totalGroups)
        var capturedDelegate = delegate
        let width = humanMaximumWidth()
        let views = BlockViewFactory.getViewsForBlocks(in: wrapper, delegate: capturedDelegate, citationDelegate: self, maxWidth: width, imageDisplayContext: .context0)
        capturedDelegate = nil
        for view in views { contentStackView.addArrangedSubview(view); pinTrailing(view) }
        if isSingleImageBlockPart { singleImageBlockView = views.first }
    }
    private func addTextBlock(_ block: ITBBlock, index: Int, totalGroups: Int) {
        let wrapper = wrapperPart(block, index: index, totalGroups: totalGroups)
        var capturedDelegate = delegate
        let width = humanMaximumWidth()
        let views = BlockViewFactory.getViewsForBlocks(in: wrapper, delegate: capturedDelegate, citationDelegate: self, maxWidth: width, imageDisplayContext: .context0)
        capturedDelegate = nil
        for view in views { blockStackView.addArrangedSubview(view); pinHorizontal(view) }
    }
    private func addImageGrid(_ blocks: [ITBBlock], totalGroups: Int, index: Int) {
        let grouping = mappedGrouping(index: index, totalGroups: totalGroups)
        let pairs: [(block: ITBImageBlock, url: String)] = blocks.compactMap { block in
            guard let image = block as? ITBImageBlock, let url = image.url else { return nil }
            return (image, url)
        }
        let urls = pairs.map { $0.url }
        let width = humanMaximumWidth()
        let callback: (Int, [String]) -> Void = { [weak self, pairs] index, _ in
            if index < 0 || index >= pairs.count { return }
            let block = pairs[index].block
            let delegate = self?.delegate
            delegate?.imageTapped(with: block)
        }
        let theme = DefaultThemeProvider.shared
        let loader = ITBImageLoader.default()!
        let grid = ImageGridView(images: urls, maxWidth: width, groupingPosition: Int(bitPattern: grouping), bubbleType: .human, themeProvider: theme, imageLoader: ImageLoader(imageLoader: loader), onImageTap: callback)
        let wrapper = ImageGridViewWrapper(rootView: grid)
        contentStackView.addArrangedSubview(wrapper)
        pinTrailing(wrapper)
    }
    private func constrainStatusToSingleImageIfNeeded() {
        if !isSingleImageBlockPart { return }
        guard let attribution = content.attribution, !attribution.isEmpty,
              let status = timestampView, status.superview != nil, let image = singleImageBlockView else { return }
        statusLeadingConstraint?.isActive = false
        statusTrailingConstraint?.isActive = false
        image.setContentHuggingPriority(.defaultHigh, for: .horizontal)
        if content.isPersonalPart() {
            let constraint = status.leadingAnchor.constraint(equalTo: image.leadingAnchor)
            constraint.priority = UILayoutPriority(500)
            constraint.isActive = true
            statusLeadingConstraint = constraint
        } else {
            let constraint = status.trailingAnchor.constraint(equalTo: image.trailingAnchor)
            constraint.priority = UILayoutPriority(500)
            constraint.isActive = true
            statusTrailingConstraint = constraint
        }
    }
    private func addStatusView(in stack: UIStackView, leadingInset: CGFloat, attributedSingleImage: Bool) {
        if !content.shouldShowStatus() { return }
        let appendSeenState = content.displayPartStatus
        let theme = DefaultThemeProvider.shared
        let status = PartStatusView(content: content, appendSeenState: appendSeenState, leadingInset: leadingInset, attributedSingleImage: attributedSingleImage, themeProvider: theme)
        timestampView = status
        if content.displayPartStatus || content.displayTimestampTemporarily || content.isSenderLinePinned { stack.addArrangedSubview(status) }
    }
    private func updateTemporaryTimestampDisplayTimerIfNecessary(in stack: UIStackView) {
        if !content.displayTimestampTemporarily { return }
        displayTimestampTimer?.invalidate()
        displayTimestampTimer = Timer.scheduledTimer(withTimeInterval: 3, repeats: false) { [weak self, stack] _ in
            guard let self = self, let status = self.timestampView else { return }
            self.content.displayTimestampTemporarily = false
            stack.removeArrangedSubview(status)
            status.removeFromSuperview()
            if let delegate = self.delegate { delegate.invalidateLayoutForPart(with: self.content.partId!) }
        }
    }
    private func updateStatusVisibility(in stack: UIStackView) {
        guard let status = timestampView else { return }
        if content.isSenderLinePinned { return }
        content.displayTimestampTemporarily.toggle()
        if content.displayTimestampTemporarily { stack.addArrangedSubview(status) }
        else { stack.removeArrangedSubview(status); status.removeFromSuperview() }
        if let delegate = delegate { delegate.invalidateLayoutForPart(with: content.partId!) }
    }
    private func handleFailure(for part: ICMConversationPart, anchorTo anchor: UIView, _ action: @escaping (UIButton) -> Void) {
        if !part.isPersonalPart() { return }
        if part.state != 2 { return }
        anchor.alpha = 0.5
        let button = UIButton()
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setImage(ICMAssets.messageFailedImage(), for: .normal)
        button.addAction(for: .touchUpInside, action)
        addSubview(button)
        NSLayoutConstraint.activate([
            button.widthAnchor.constraint(equalToConstant: 16),
            button.heightAnchor.constraint(equalToConstant: 16),
            button.centerYAnchor.constraint(equalTo: anchor.centerYAnchor),
            button.trailingAnchor.constraint(equalTo: anchor.leadingAnchor, constant: -16)
        ])
    }
    private func configureAccessibility() {
        blockStackView.isAccessibilityElement = true
        var label = ""
        if !content.isPersonalPart(), let sender = content.senderLabel, !sender.isEmpty { label += sender + ", " }
        let views = blockStackView.arrangedSubviews
        var actions: [UIAccessibilityCustomAction] = []
        for view in views {
            if let text = view.accessibilityLabel { label += text + ", " }
            actions.append(contentsOf: view.accessibilityCustomActions ?? [])
        }
        blockStackView.accessibilityLabel = label
        actions.append(UIAccessibilityCustomAction(name: "Copy text") { [weak self] _ in
            guard let self = self else { return false }
            let blocks: [ITBBlock] = self.content.blocks!
            UIPasteboard.general.string = blocks.map { $0.plainTextIncludingListItems() ?? "" }.joined(separator: " ")
            return true
        })
        blockStackView.accessibilityCustomActions = actions
    }
    func contextMenuInteraction(_ interaction: UIContextMenuInteraction, configurationForMenuAtLocation location: CGPoint) -> UIContextMenuConfiguration? {
        UIContextMenuConfiguration(identifier: nil, previewProvider: nil) { [self] _ in
            let action = UIAction(title: "Copy") { [weak self] _ in
                let content = self?.content
                guard let content = content else { return }
                let blocks: [ITBBlock] = content.blocks!
                UIPasteboard.general.string = blocks.map { $0.plainTextIncludingListItems() ?? "" }.joined(separator: " ")
            }
            return UIMenu(title: "", children: [action])
        }
    }
    @objc func handleTap(_ recognizer: UITapGestureRecognizer?) {
        updateStatusVisibility(in: containerStackView)
        constrainStatusToSingleImageIfNeeded()
    }
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        if content.isSingleMediaPart() { return false }
        if content.isSingleMessengerCard() { return false }
        return content.partStyle != 7
    }
    func didTapCitations(_ citations: [ITBInlineCitation]) {
        guard let values = content.answerSources else { return }
        let sources = values as! [FinAnswerSourceWrapper]
        delegate?.citationsTapped(citations, from: sources)
    }
    private func direction(_ view: UIView) -> Bool {
        view.effectiveUserInterfaceLayoutDirection == .rightToLeft
    }
    private func pinLeading(_ view: UIView) {
        view.translatesAutoresizingMaskIntoConstraints = false
        let parent = view.superview!
        let sign: CGFloat = direction(view) ? -1 : 1
        view.leadingAnchor.constraint(equalTo: parent.leadingAnchor, constant: sign * 0).isActive = true
    }
    private func pinTrailing(_ view: UIView) {
        view.translatesAutoresizingMaskIntoConstraints = false
        let parent = view.superview!
        let sign: CGFloat = direction(view) ? -1 : 1
        view.trailingAnchor.constraint(equalTo: parent.trailingAnchor, constant: sign * 0).isActive = true
    }
    private func pinHorizontal(_ view: UIView) {
        let rtl = view.effectiveUserInterfaceLayoutDirection != .leftToRight
        func left() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.leftAnchor.constraint(equalTo: parent.leftAnchor, constant: 0).isActive = true
        }
        func right() {
            let parent = view.superview!
            view.translatesAutoresizingMaskIntoConstraints = false
            view.translatesAutoresizingMaskIntoConstraints = false
            view.rightAnchor.constraint(equalTo: parent.rightAnchor, constant: -CGFloat.zero).isActive = true
        }
        if rtl { right(); left() }
        else { left(); right() }
    }
    private func pinEdges(_ view: UIView) {
        let topParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.topAnchor.constraint(equalTo: topParent.topAnchor, constant: 0).isActive = true
        pinHorizontal(view)
        let bottomParent = view.superview!
        view.translatesAutoresizingMaskIntoConstraints = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.bottomAnchor.constraint(equalTo: bottomParent.bottomAnchor, constant: -CGFloat.zero).isActive = true
    }
}

private struct BlockGroupInfo {
    let images: [ITBBlock]
    let media: [ITBBlock]
    let paragraphs: [ITBBlock]
    init(blocks: [ITBBlock]) {
        images = blocks.filter { $0.type() == 5 }
        media = blocks.filter { $0.type() == 14 }
        paragraphs = blocks.filter { $0.type() == 0 }
    }
    var count: Int { (images.isEmpty ? 0 : 1) + media.count + paragraphs.count }
}
