// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
import UIKit
import os

@objc enum AttachedFileMediaType: Int {
    case image, video, attachment
    static func mediaType(from value: String) -> AttachedFileMediaType {
        switch value {
        case "image": return .image
        case "video": return .video
        case "attachment": return .attachment
        default:
            ICMLogger.log("No mediaType has been provided for the attachment", type: .error)
            return .image
        }
    }
}
enum AttachmentError: Error, Equatable {
    case attachmentSizeLimitExceeded(limit: Int), attachmentCountExceeded(limit: Int), uploadFailed
}
protocol AttachmentViewDataSource {
    var id: String { get }
    var fileName: String { get }
    var attachmentUrl: URL { get }
    var mediaType: AttachedFileMediaType { get }
    var upload: ITBUpload? { get }
    var viewState: AttachmentView.State { get set }
    var attachmentError: AttachmentError? { get }
    // The deleted witness name is inferred from the original conformer's exact reflected field.
    var attachmentIsBeyondFileCountLimit: Bool { get set }
    var attachmentUploadDidFail: Bool { get set }
}
protocol AttachmentViewDelegate: AnyObject {
    func attachmentViewUploadCompleted(uploadId: String)
    func attachmentViewTapped(dataSource: any AttachmentViewDataSource, sourceView: UIView)
    func attachmentUploadFailed()
}
