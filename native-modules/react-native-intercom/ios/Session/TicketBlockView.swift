import UIKit

@objc(_TtC18IntercomSDKPrivate15TicketBlockView)
class TicketBlockView: ITBBlockView, ConversationCellContentView {
    let content: TicketBlock
    private let themeProvider: any ThemeProvider
    private weak var conversationCellBlockDelegate: (any ConversationCellBlockDelegate)?
    private lazy var tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(viewTapped))
    private lazy var ticketIconView: UIImageView = {
        let view = UIImageView(image: ICMAssets.ticketIcon())
        view.contentMode = .scaleAspectFit
        view.tintColor = themeProvider.theme.primaryForeground
        view.translatesAutoresizingMaskIntoConstraints = false
        return view
    }()
    private lazy var ticketTitleLabel: UILabel = {
        let label = UILabel()
        let preferred = UIFont.preferredFont(forTextStyle: .subheadline)
        if let descriptor = preferred.fontDescriptor.withSymbolicTraits(.traitBold) {
            label.font = UIFont(descriptor: descriptor, size: 0)
        } else { label.font = preferred }
        label.textColor = themeProvider.theme.primaryForeground
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        return label
    }()
    private lazy var ticketTitleTypeLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.preferredFont(forTextStyle: .subheadline)
        label.textColor = themeProvider.theme.textMuted
        label.numberOfLines = 0
        label.lineBreakMode = .byWordWrapping
        label.translatesAutoresizingMaskIntoConstraints = false
        label.adjustsFontForContentSizeCategory = true
        return label
    }()
    private lazy var ticketTitleContainerStackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .leading
        stack.distribution = .fill
        stack.spacing = 4
        stack.clipsToBounds = true
        return stack
    }()

    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?, ticket: Ticket?, themeProvider: any ThemeProvider) {
        guard let content = content as? TicketBlock else { return nil }
        self.content = content
        self.themeProvider = themeProvider
        conversationCellBlockDelegate = delegate
        super.init(frame: .zero)
        configure(with: content)
        translatesAutoresizingMaskIntoConstraints = false
        if content.ticketType.isArchived || ticket != nil {
            isUserInteractionEnabled = false
            alpha = 0.5
            tapGestureRecognizer.isEnabled = false
        }
    }

    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }

    override func configure(with block: ITBBlock!) {
        addGestureRecognizer(tapGestureRecognizer)
        addSubview(ticketIconView)
        addSubview(ticketTitleContainerStackView)
        guard let block else { fatalError("Unexpectedly found nil while unwrapping an Optional value") }
        updateAccessibility(with: block)
        guard let ticketBlock = block as? TicketBlock else { return }
        ticketTitleLabel.text = ticketBlock.title
        ticketTitleTypeLabel.text = ticketBlock.ticketTypeTitle
        layoutView()
    }

    private func updateAccessibility(with block: ITBBlock) {
        isAccessibilityElement = true
        if block is TicketBlock {
            accessibilityLabel = TranslationManager.messenger.translation(forKey: "ticket_create_ticket_with_type_title",
                params: ["ticket_type_title": content.ticketTypeTitle])
        } else {
            accessibilityLabel = TranslationManager.messenger.translation(forKey: "tickets_create_ticket")
        }
    }

    private func layoutView() {
        backgroundColor = themeProvider.theme.base
        layoutTicketTitleContainerStackView()
        layoutTicketIconView()
        layer.cornerRadius = 20
        layer.borderWidth = 1
        layer.borderColor = DefaultThemeProvider.shared.theme.neutralBorder.cgColor
    }

    private func layoutTicketTitleContainerStackView() {
        let stack = ticketTitleContainerStackView
        stack.addArrangedSubview(ticketTitleLabel)
        stack.addArrangedSubview(ticketTitleTypeLabel)
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.topAnchor.constraint(equalTo: stack.superview!.topAnchor, constant: 16).isActive = true
        _ = stack.effectiveUserInterfaceLayoutDirection
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.bottomAnchor.constraint(equalTo: stack.superview!.bottomAnchor, constant: -16).isActive = true
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 16).isActive = true
        stack.translatesAutoresizingMaskIntoConstraints = false
        stack.trailingAnchor.constraint(lessThanOrEqualTo: ticketIconView.leadingAnchor, constant: 8).isActive = true
    }

    private func layoutTicketIconView() {
        let icon = ticketIconView
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.widthAnchor.constraint(equalToConstant: 16).isActive = true
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.heightAnchor.constraint(equalToConstant: 18).isActive = true
        let trailing: CGFloat = icon.effectiveUserInterfaceLayoutDirection == .rightToLeft ? 16 : -16
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.trailingAnchor.constraint(equalTo: icon.superview!.trailingAnchor, constant: trailing).isActive = true
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.centerYAnchor.constraint(equalTo: icon.superview!.centerYAnchor).isActive = true
    }

    @objc private func viewTapped() {
        guard let delegate = conversationCellBlockDelegate else { return }
        delegate.createTicketTapped(for: content.ticketType)
    }
}
