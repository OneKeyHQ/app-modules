import UIKit
@objc(_TtC18IntercomSDKPrivate19AttachmentBlockView)
class AttachmentBlockView: TextBlockView {
    private lazy var attachmentIconView: ITBAttachmentIconView = {
        let icon = ITBAttachmentIconView()
        icon.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([icon.widthAnchor.constraint(equalToConstant: 12), icon.heightAnchor.constraint(equalToConstant: 16)])
        return icon
    }()
    override init?(content: ITBBlock, citationDelegate: (any CitationTapDelegate)?, blockDelegate: (any ConversationCellBlockDelegate)?) {
        guard let attachment = content as? ITBAttachmentBlock else { return nil }
        super.init(content: content, citationDelegate: citationDelegate, blockDelegate: blockDelegate)
        translatesAutoresizingMaskIntoConstraints = false
        addSubview(attachmentIconView)
        attachmentIconView.clipColor = attachment.clipColor
        NSLayoutConstraint.activate([
            attachmentIconView.trailingAnchor.constraint(equalTo: leadingAnchor, constant: attachment.layoutInsets.left - 4),
            attachmentIconView.centerYAnchor.constraint(equalTo: centerYAnchor),
        ])
    }
}
