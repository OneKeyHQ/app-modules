// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit

@_objcRuntimeName(_TtC18IntercomSDKPrivate16TicketAttachment)
class TicketAttachment: AttachmentViewDataSource {
    let id: String
    let fileName: String
    let attachmentUrl: URL
    let mediaType: AttachedFileMediaType
    let upload: ITBUpload?
    let maxFileCount: Int
    var viewState: AttachmentView.State = .uploaded
    var attachmentIsBeyondFileCountLimit = false
    var attachmentUploadDidFail = false {
        didSet {
            if attachmentError != nil { viewState = .error }
            else if let upload = upload {
                if upload.uploadProgress >= 1 { viewState = .uploaded }
                else if upload.uploadProgress <= 0 { viewState = .uploaded }
                else { viewState = .uploading(progress: CGFloat(upload.uploadProgress)) }
            } else { viewState = .uploaded }
        }
    }
    let config: any ICMConfigProtocol
    init(upload: ITBUpload, localResourceUrl: URL, maxFileCount: Int, config: any ICMConfigProtocol) {
        self.config = config
        self.upload = upload
        id = upload.localId
        fileName = upload.filename
        attachmentUrl = localResourceUrl
        mediaType = upload.isImage() ? .image : (upload.isVideo() ? .video : .attachment)
        self.maxFileCount = maxFileCount
    }
    var attachmentError: AttachmentError? {
        if let upload = upload {
            let count = upload.uploadData.count
            if count > config.attachmentSettings.uploadSizeLimit {
                return .attachmentSizeLimitExceeded(limit: config.attachmentSettings.uploadSizeLimit / 1048576)
            }
        }
        if attachmentIsBeyondFileCountLimit { return .attachmentCountExceeded(limit: maxFileCount) }
        if attachmentUploadDidFail { return .uploadFailed }
        return nil
    }
}
