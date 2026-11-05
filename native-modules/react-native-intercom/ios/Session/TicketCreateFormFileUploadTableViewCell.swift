// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate39TicketCreateFormFileUploadTableViewCell)
class TicketCreateFormFileUploadTableViewCell: TicketCreateFormInputTableViewCell {
    private let themeProvider = DefaultThemeProvider.shared
    private lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .leading
        stack.distribution = .fillProportionally
        stack.spacing = 8
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    private lazy var addFileButton: ICMActionButton = {
        let button = ICMActionButton(title: "", style: 0, backgroundColor: themeProvider.theme.neutralContainerEmphasis, foregroundColor: themeProvider.theme.textDefault)!
        var configuration = button.configuration
        if configuration != nil { configuration!.cornerStyle = .fixed }
        if configuration != nil { configuration!.background.cornerRadius = 10 }
        if configuration != nil { configuration!.background.strokeWidth = 0 }
        if configuration != nil { configuration!.image = ICMAssets.smallPlusIcon() }
        if configuration != nil { configuration!.imagePlacement = .trailing }
        button.configuration = configuration
        button.tintColor = themeProvider.theme.textDefault
        button.addTarget(self, action: #selector(addFileButtonTapped), for: .touchUpInside)
        button.translatesAutoresizingMaskIntoConstraints = false
        return button
    }()
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) { super.init(style: style, reuseIdentifier: reuseIdentifier) }
    required init?(coder: NSCoder) { super.init(coder: coder) }
    override var formInputView: UIView? { stackView }
    override func configure(with incoming: any TicketCreateFormCellItem) {
        showErrorIcon = false
        super.configure(with: incoming)
        addSubviews()
        backgroundColor = themeProvider.theme.base
        for view in stackView.arrangedSubviews { view.removeFromSuperview() }
        guard let item = incoming as? TicketCreateFormFileUploadCellItem else {
            stackView.addArrangedSubview(addFileButton)
            return
        }
        item.cellDelegate = self
        setTitleText(item.attribute.name)
        setDetailText(item.detail)
        cellItem = item
        for view in item.attachmentViews { stackView.addArrangedSubview(view) }
        updateAddFileButtonTitle(item)
        stackView.addArrangedSubview(addFileButton)
        validate()
    }
    private func updateAddFileButtonTitle(_ item: TicketCreateFormFileUploadCellItem) {
        let title = TranslationManager.messenger.translation(forKey: item.attribute.isMultipleFilesUploadAttribute ? "tickets_add_files" : "tickets_add_file")
        var attributes = AttributeContainer()
        attributes.font = UIFont.systemFont(ofSize: UIFontDescriptor.preferredFontDescriptor(withTextStyle: .subheadline).pointSize, weight: .semibold)
        var configuration = addFileButton.configuration
        if configuration != nil { configuration!.attributedTitle = AttributedString(title, attributes: attributes) }
        addFileButton.configuration = configuration
    }
    override func validate() {
        if let item = cellItem as? TicketCreateFormFileUploadCellItem {
            item.updateMultipleFileStates()
            item.updateValidityState()
        }
        updateValidityMessageView()
        updateAddFileButtonVisibility()
    }
    private func updateValidityMessageView() {
        guard let item = cellItem as? TicketCreateFormFileUploadCellItem else { return }
        validityMessageView.isHidden = item.validityMessage == nil
        validityMessageLabel.text = item.validityMessage
        validityMessageLabel.textColor = item.valid ? themeProvider.theme.textMuted : themeProvider.theme.textError
    }
    private func updateAddFileButtonVisibility() {
        guard let item = cellItem as? TicketCreateFormFileUploadCellItem else { return }
        addFileButton.isHidden = item.attachmentItems.count >= item.maxFileCount
    }
    override func refresh() {
        guard let snapshot = cellItem else { return }
        validate()
        invalidateIntrinsicContentSize()
        snapshot.refreshAction()
    }
    @objc func addFileButtonTapped() {
        guard let item = cellItem as? TicketCreateFormFileUploadCellItem else { return }
        let button = addFileButton
        item.delegate?.showAttachmentPicker(cellItem: item, selectionLimit: item.maxFileCount, sourceView: button)
    }
}
