import UIKit

// Concrete default composite transport retains the proven identity without inventing its deleted private requirement.
enum PartViewFactory {
    static func getView(for part: ICMConversationPart, delegate: ConversationViewModel?, maxWidth: CGFloat, themeProvider: ThemeProvider) -> (any ConversationCellContentView)? {
        switch part.partStyle {
        case 1:
            let theme = DefaultThemeProvider.shared
            let config = ICMConfig.shared()!
            let view = NotePartView(content: part, themeProvider: theme, config: config)
            view.delegate = delegate
            view.setUpView()
            return view
        case 2:
            let theme = DefaultThemeProvider.shared
            let config = ICMConfig.shared()!
            let view = PostPartView(content: part, themeProvider: theme, config: config)
            view.delegate = delegate
            view.setUpView()
            return view
        case 3, 5:
            return QuickReplyPartView(content: part, delegate: delegate, maxWidth: maxWidth, themeProvider: themeProvider)
        case 4:
            let form = part.form!
            if !form.formAttributes.isEmpty {
                return AttributePartView(content: part, delegate: delegate, maxWidth: maxWidth, themeProvider: DefaultThemeProvider.shared)
            }
        case 6:
            return TicketStatusUpdateView(for: part, themeProvider: DefaultThemeProvider.shared)
        case 7:
            return BlockPartView(content: part, maxWidth: maxWidth, delegate: delegate, queue: AsyncQueue.main)
        case 9:
            return MergedConversationEventPartView(for: part, delegate: delegate, themeProvider: DefaultThemeProvider.shared)
        default: break
        }
        if part.isEvent() {
            return EventPartView(content: part, maxWidth: maxWidth, themeProvider: DefaultThemeProvider.shared)
        }
        if part.isLinkCard() {
            return LinkPartView(content: part, themeProvider: DefaultThemeProvider.shared, delegate: delegate)
        }
        if part.isConversationRatingCard() {
            return ConversationRatingPartView(content: part, maxWidth: maxWidth, delegate: delegate, queue: AsyncQueue.main)
        }
        if part.isLinkListCard() {
            return LinkListPartView(content: part, delegate: delegate)
        }
        if part.containsBlocks() {
            return BlockPartView(content: part, maxWidth: maxWidth, delegate: delegate, queue: AsyncQueue.main)
        }
        return DefaultPartView(content: part, delegate: delegate)
    }
}
