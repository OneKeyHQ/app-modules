import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate34TicketCreateFormFileUploadCellItem)
class TicketCreateFormFileUploadCellItem: TicketCreateFormInputCellItem, TicketCreateFormCellRefreshable, AttachmentViewDelegate, Equatable {
    struct AttachmentItem { let attachment: TicketAttachment; let view: AttachmentView }
    let cellType: TicketCreateFormTableViewCell.Type
    let attribute: FormAttribute
    let detail: String?
    let maxFileCount: Int
    let isEnabled: Bool
    var valid = true
    var validityMessage: String?
    let description: String?
    var allUploadProgressComplete = true
    // Deleted private requirements use the proved concrete default transport.
    weak var delegate: TicketCreateFormViewModel?
    let refreshAction: () -> Void
    let onValueChange: ((Any?) -> Void)?
    weak var cellDelegate: (any TicketCreateFormCellRefreshable)?
    let config: any ICMConfigProtocol
    var attachmentItems: [AttachmentItem] = []
    init(cellType: TicketCreateFormTableViewCell.Type, attribute: FormAttribute, detail: String?, maxFileCount: Int, isEnabled: Bool, description: String?, delegate: TicketCreateFormViewModel?, refreshAction: @escaping () -> Void, onValueChange: ((Any?) -> Void)?, config: any ICMConfigProtocol) {
        self.cellType = cellType
        self.attribute = attribute
        self.detail = detail
        self.maxFileCount = maxFileCount
        self.isEnabled = isEnabled
        self.description = description
        self.delegate = delegate
        self.refreshAction = refreshAction
        self.onValueChange = onValueChange
        self.config = config
    }
    var attachmentViews: [AttachmentView] { attachmentItems.map { $0.view } }
    func updateMultipleFileStates() {
        guard attribute.isMultipleFilesUploadAttribute else { return }
        for (index, pair) in attachmentItems.enumerated() {
            let attachment = pair.attachment
            attachment.attachmentIsBeyondFileCountLimit = index >= maxFileCount
            if attachment.attachmentError != nil { attachment.viewState = .error }
            else if let upload = attachment.upload {
                if upload.uploadProgress >= 1 { attachment.viewState = .uploaded }
                else if upload.uploadProgress <= 0 { attachment.viewState = .uploaded }
                else { attachment.viewState = .uploading(progress: CGFloat(upload.uploadProgress)) }
            } else { attachment.viewState = .uploaded }
            pair.view.renderCurrentState()
        }
    }
    func refresh() {
        updateMultipleFileStates()
        updateValidityState()
        cellDelegate?.refresh()
    }
    func takeOverAttachments(from old: TicketCreateFormFileUploadCellItem) {
        guard !old.attachmentItems.isEmpty else { return }
        attachmentItems = old.attachmentItems
        old.attachmentItems = []
        for pair in attachmentItems { pair.view.delegate = self }
        updateMultipleFileStates()
        updateValidityState()
        updateAttributeValue()
        refreshUploadStatus()
    }
    func updateAttributeValue() {
        var values: [Int] = []
        for pair in attachmentItems {
            if let upload = pair.attachment.upload { values.append(upload.uploadId?.intValue ?? 0) }
        }
        attribute.value = values.isEmpty ? nil : values
    }
    func refreshUploadStatus() {
        allUploadProgressComplete = attachmentItems.allSatisfy { $0.attachment.viewState == .uploaded }
        delegate?.refreshSubmitButton()
    }
    func updateValidityState() {
        var passes = true
        for pair in attachmentItems {
            let attachment = pair.attachment
            if let upload = attachment.upload {
                let count = upload.uploadData.count
                if count > attachment.config.attachmentSettings.uploadSizeLimit {
                    _ = attachment.config.attachmentSettings
                    passes = false
                    break
                }
            }
            if attachment.attachmentIsBeyondFileCountLimit || attachment.attachmentUploadDidFail {
                passes = false
                break
            }
        }
        validityMessage = passes ? description : TranslationManager.messenger.translation(forKey: "upload_failed")
        valid = passes
    }
    func addAttachmentView(for upload: ITBUpload, localResourceUrl: URL) {
        let attachment = TicketAttachment(upload: upload, localResourceUrl: localResourceUrl, maxFileCount: maxFileCount, config: config)
        let view = AttachmentView(dataSource: attachment, themeProvider: DefaultThemeProvider.shared)
        view.delegate = self
        view.translatesAutoresizingMaskIntoConstraints = false
        attachmentItems.append(AttachmentItem(attachment: attachment, view: view))
        updateMultipleFileStates()
        updateValidityState()
        refreshAction()
        allUploadProgressComplete = false
        delegate?.refreshSubmitButton()
    }
    func deleteAttachment(withId id: String) {
        guard let index = attachmentItems.firstIndex(where: { $0.attachment.id == id }) else { return }
        let removed = attachmentItems.remove(at: index)
        removed.view.removeFromSuperview()
        updateMultipleFileStates()
        updateValidityState()
        updateAttributeValue()
        refreshAction()
        refreshUploadStatus()
    }
    func attachmentViewTapped(dataSource: any AttachmentViewDataSource, sourceView: UIView) {
        guard let delegate = delegate else { return }
        switch dataSource.viewState {
        case .uploading: delegate.uploadingAttachmentViewTapped(dataSource, cellItem: self, sourceView: sourceView)
        case .uploaded: delegate.uploadedAttachmentViewTapped(dataSource, cellItem: self, sourceView: sourceView)
        case .error: delegate.errorAttachmentViewTapped(dataSource, cellItem: self, sourceView: sourceView)
        }
    }
    func attachmentViewUploadCompleted(uploadId: String) {
        updateAttributeValue()
        updateValidityState()
        refreshUploadStatus()
        refreshAction()
    }
    func attachmentUploadFailed() {
        updateValidityState()
        refreshAction()
        allUploadProgressComplete = false
        delegate?.refreshSubmitButton()
    }
    static func == (lhs: TicketCreateFormFileUploadCellItem, rhs: TicketCreateFormFileUploadCellItem) -> Bool {
        lhs.attribute.isEqualToFormAttribute(rhs.attribute) && lhs.detail == rhs.detail
    }
}
