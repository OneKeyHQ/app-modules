// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate34TicketAttachmentAttributeValueView)
class TicketAttachmentAttributeValueView: TicketAttributeView, AttachmentViewDelegate {
    private lazy var stackView: UIStackView = {
        let stack = UIStackView()
        stack.axis = .vertical
        stack.alignment = .leading
        stack.distribution = .fillEqually
        stack.spacing = 12
        stack.translatesAutoresizingMaskIntoConstraints = false
        return stack
    }()
    let fileUploadAttributeValues: [TicketFileUploadAttributeValue]
    override init?(attribute: FormAttribute) {
        guard let dictionaries = attribute.value as? [[String: Any]] else { return nil }
        let values = dictionaries.compactMap(TicketFileUploadAttributeValue.init(withDictionary:))
        guard !values.isEmpty else { return nil }
        fileUploadAttributeValues = values
        super.init(attribute: attribute)
    }
    @nonobjc override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    @nonobjc required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override var valueView: UIView { stackView }
    override var topPadding: CGFloat { 8 }
    override func configureValueView() {
        for value in fileUploadAttributeValues {
            let view = AttachmentView(dataSource: value, themeProvider: DefaultThemeProvider.shared)
            view.delegate = self
            view.translatesAutoresizingMaskIntoConstraints = false
            stackView.addArrangedSubview(view)
        }
    }
    func attachmentViewTapped(dataSource: any AttachmentViewDataSource, sourceView: UIView) { LinkOpener.shared.openLink(dataSource.attachmentUrl) }
    func attachmentViewUploadCompleted(uploadId: String) {}
    func attachmentUploadFailed() {}
}
