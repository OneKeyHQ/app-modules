import UIKit

// Raw values and the outbound branch are observed in the original factory.
// The original private case names are not retained in the binary.
@objc enum ImageBlockDisplayContext: Int {
    case context0 = 0
    case outbound = 1
    case context2 = 2
}

// Only specialized static functions survive for this private namespace.
enum BlockViewFactory {
    static func getViewsForBlocks(in content: ICMConversationPart, delegate: (any ConversationCellBlockDelegate)?, citationDelegate: (any CitationTapDelegate)?, maxWidth: CGFloat?, imageDisplayContext: ImageBlockDisplayContext) -> [any ConversationCellContentView] {
        let blocks: [ITBBlock] = content.blocks
        return blocks.compactMap { block -> (any ConversationCellContentView)? in
            switch block.type() {
            case 3, 4:
                return ListBlockView(content: block, citationDelegate: citationDelegate, blockDelegate: delegate)
            case 5:
                if imageDisplayContext.rawValue == 1 {
                    let imageLoader = ITBImageLoader.default()!
                    let themeProvider = DefaultThemeProvider.shared
                    return OutboundImageBlockView(content: block, delegate: delegate, imageLoader: imageLoader, imageView: nil, themeProvider: themeProvider)
                }
                let personal = content.isPersonalPart()
                let singleImage = content.isSingleImagePart()
                let grouping = singleImage ? Int(bitPattern: content.groupingPosition) : 3
                let imageLoader = ITBImageLoader.default()!
                let themeProvider = DefaultThemeProvider.shared
                return ConversationImageBlockView(content: block, delegate: delegate, imageLoader: imageLoader, imageView: nil, groupingPosition: grouping, bubbleType: personal ? .human : .admin, themeProvider: themeProvider)
            case 6, 7, 8:
                return ButtonBlockView(content: block, delegate: delegate)
            case 9:
                guard let code = block as? CodeBlock else {
                    return TextBlockView(content: block, citationDelegate: citationDelegate, blockDelegate: delegate)
                }
                let expand: (() -> Void)?
                if delegate?.getViewControllerForModalPresentation() != nil {
                    expand = { [weak delegate, code] in
                        guard let presenter = delegate?.getViewControllerForModalPresentation() else { return }
                        let controller = CodeBlockExpandedViewController(content: code, blockDelegate: delegate, themeProvider: DefaultThemeProvider.shared)
                        controller.modalPresentationStyle = .overFullScreen
                        controller.modalTransitionStyle = .crossDissolve
                        presenter.present(controller, animated: true, completion: nil)
                    }
                } else { expand = nil }
                return CodeBlockView(content: code, blockDelegate: delegate, maxWidth: maxWidth, themeProvider: DefaultThemeProvider.shared, mode: .inline(expand))
            case 10:
                return getVideoBlockView(content: content, block: block, delegate: delegate)
            case 12:
                return LinkBlockView(content: block, delegate: delegate)
            case 14:
                if content.isPersonalPart() {
                    return FileAttachmentBlockView(content: block, delegate: delegate, width: maxWidth, groupingPosition: Int(bitPattern: content.groupingPosition), bubbleType: .human, themeProvider: DefaultThemeProvider.shared)
                }
                return AttachmentBlockView(content: block, citationDelegate: citationDelegate, blockDelegate: delegate)
            case 15:
                return ConversationRatingBlockView(content: block, conversationPart: content, delegate: delegate, themeProvider: DefaultThemeProvider.shared)
            case 16:
                print("-=- typing", separator: " ", terminator: "\n")
                return DefaultBlockView(content: block)
            case 17:
                return LinkListBlockView(content: block, delegate: delegate)
            case 18:
                return getMessengerAppBlockView(content: content, block: block, delegate: delegate)
            case 19:
                return VideoFileBlockView(content: block, notificationCenter: NotificationCenter.default, delegate: delegate, videoFileProcessor: VideoFileProcessor.shared, themeProvider: DefaultThemeProvider.shared)
            case 20:
                return ImageTextBlockView(content: block, imageLoader: ITBImageLoader.default()!, themeProvider: DefaultThemeProvider.shared)
            case 22:
                return TicketBlockView(content: block, delegate: delegate, ticket: content.ticket, themeProvider: DefaultThemeProvider.shared)
            default:
                return TextBlockView(content: block, citationDelegate: citationDelegate, blockDelegate: delegate)
            }
        }
    }

    static func getVideoBlockView(content: ICMConversationPart, block: ITBBlock, delegate: (any ConversationCellBlockDelegate)?) -> VideoBlockView? {
        guard let delegate = delegate, let partId = content.partId else {
            return VideoBlockView(content: block, notificationCenter: NotificationCenter.default, delegate: delegate)
        }
        let identifier = String._unconditionallyBridgeFromObjectiveC(block.blockUniqueIdentifier() as NSString?)
        let key = partId + "_" + identifier
        if let cached = delegate.getVideoBlockView(for: key) {
            if let video = block as? ITBVideoBlock, video !== cached.content {
                video.videoSize = cached.content.videoSize
                cached.content = video
            }
            return cached
        }
        let view = VideoBlockView(content: block, notificationCenter: NotificationCenter.default, delegate: delegate)
        delegate.cacheVideoBlockView(view, id: key)
        return view
    }

    static func getMessengerAppBlockView(content: ICMConversationPart, block: ITBBlock, delegate: (any ConversationCellBlockDelegate)?) -> MessengerAppBlockView? {
        guard let delegate = delegate else {
            return MessengerAppBlockView(content: block, delegate: nil, singleBlockView: content.isSingleMessengerCard())
        }
        let identifier = String._unconditionallyBridgeFromObjectiveC(block.blockUniqueIdentifier() as NSString?)
        if let cached = delegate.getMessengerAppBlockView(for: identifier) { return cached }
        let view = MessengerAppBlockView(content: block, delegate: delegate, singleBlockView: content.isSingleMessengerCard())
        let cacheIdentifier = String._unconditionallyBridgeFromObjectiveC(block.blockUniqueIdentifier() as NSString?)
        delegate.cacheMessengerAppBlockView(view, id: cacheIdentifier)
        return view
    }
}
