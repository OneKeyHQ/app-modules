import UIKit

// The original static routing functions survive; their namespace nominal shape is not established.
enum ConversationCellViewFactory {
    private static let activityIndicatorHeight = UIFontMetrics.default.scaledValue(for: 44)
    private static let separatedTypes: Set<Int> = {
        var types = Set<Int>()
        types.insert(23)
        types.insert(9)
        return types
    }()

    // The sole recovered composite consumer is the real ViewModel; its deleted inputtable ABI remains open.
    static func getView(for type: ConversationCellType, delegate: ConversationViewModel?, maxWidth: CGFloat) -> UIView? {
        switch type {
        case .message(let part):
            return PartViewFactory.getView(for: part, delegate: delegate, maxWidth: maxWidth, themeProvider: DefaultThemeProvider.shared)
        case .greeting(let text):
            let view = GreetingView(themeProvider: DefaultThemeProvider.shared)
            view.configure(with: text)
            return view
        case .temporaryExpectationsView(let text):
            let view = TemporaryExpectationsView(themeProvider: DefaultThemeProvider.shared)
            view.translatesAutoresizingMaskIntoConstraints = false
            view.configure(with: text)
            return view
        case .ticket(let ticket):
            let view = TicketCardView(for: ticket, delegate: delegate, themeProvider: DefaultThemeProvider.shared)
            view.translatesAutoresizingMaskIntoConstraints = false
            return view
        case .dayDivider(let part):
            let view = DayTimestampDividerView()
            view.translatesAutoresizingMaskIntoConstraints = false
            let label = view.timeLabel!
            label.text = TimestampCalculator.shared.dayText(for: part)
            return view
        case .footerNotice(let model):
            let view = FooterNoticeView()
            view.style = .expanded
            view.configure(with: model)
            view.translatesAutoresizingMaskIntoConstraints = false
            return view
        case .activityIndicator(let details):
            let manager = ICMDataManager.shared()!
            let view = DotIndicatorView(frame: .zero, context: .conversation, dataManager: manager)
            if let details = details { view.configure(with: details) }
            let height = activityIndicatorHeight
            view.translatesAutoresizingMaskIntoConstraints = false
            let constraint = view.heightAnchor.constraint(equalToConstant: height)
            constraint.priority = .required
            constraint.isActive = true
            return view
        case .standaloneBlock(let part):
            return standaloneBlockView(for: part, delegate: delegate)
        case .unreadMessageDivider:
            let view = UnreadMessageDivider()
            view.translatesAutoresizingMaskIntoConstraints = false
            return view
        }
    }

    private static func standaloneBlockView(for part: ICMConversationPart, delegate: ConversationViewModel?) -> UIView? {
        guard let values = part.blocks else { return nil }
        let blocks: [ITBBlock] = values
        guard let block = blocks.first(where: { separatedTypes.contains($0.type()) }) else { return nil }
        switch block.type() {
        case 23:
            guard let table = block as? TableBlock else { return nil }
            return TableCardView(for: table, delegate: delegate, themeProvider: DefaultThemeProvider.shared)
        case 9:
            guard let code = block as? CodeBlock else { return nil }
            let expand: () -> Void = { [weak delegate, code] in
                guard let delegate = delegate, let presenter = delegate.getViewControllerForModalPresentation() else { return }
                let controller = CodeBlockExpandedViewController(content: code, blockDelegate: delegate, themeProvider: DefaultThemeProvider.shared)
                controller.modalPresentationStyle = .overFullScreen
                controller.modalTransitionStyle = .crossDissolve
                presenter.present(controller, animated: true, completion: nil)
            }
            let theme = DefaultThemeProvider.shared
            let copier = CodeBlockCopier()
            let scheduler = DefaultScheduler(timer: nil, queue: AsyncQueue.main)
            return CodeBlockView(content: code, blockDelegate: delegate, maxWidth: nil, themeProvider: theme, copier: copier, scheduler: scheduler, mode: .inline(expand))
        default: return nil
        }
    }
}
