// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import Foundation

@objc(_TtC18IntercomSDKPrivate30TicketFileUploadAttributeValue)
class TicketFileUploadAttributeValue: NSObject, DictionaryInitializable, AttachmentViewDataSource {
    @objc let id: String
    @objc let fileName: String
    @objc let attachmentUrl: URL
    @objc let mediaType: AttachedFileMediaType
    @objc let upload: ITBUpload? = nil
    var viewState: AttachmentView.State = .uploaded
    var attachmentError: AttachmentError?
    @objc var attachmentIsBeyondFileCountLimit = false
    @objc var attachmentUploadDidFail = false

    @objc(initWithDictionary:)
    required init?(withDictionary dictionary: [String: Any]) {
        guard let id = dictionary["id"] as? Int,
              let name = dictionary["name"] as? String,
              let rawURL = dictionary["url"] as? String, let url = URL(string: rawURL),
              let media = dictionary["media_type"] as? String else { return nil }
        self.id = id.description
        fileName = name
        attachmentUrl = url
        mediaType = AttachedFileMediaType.mediaType(from: media)
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    override var description: String {
        let type: String
        switch mediaType {
        case .image: type = "image"
        case .video: type = "video"
        case .attachment: type = "attachment"
        }
        return "id: " + id + ", fileName: " + fileName + ", attachmentUrl: " + attachmentUrl.absoluteString + ", mediaType: " + type
    }
}
