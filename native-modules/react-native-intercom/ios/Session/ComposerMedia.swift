// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import Foundation
import CoreGraphics

enum ComposerInput: CaseIterable, Hashable { case attachment, gif, mic }
enum ComposerMediaState: Equatable { case hasMedia([MediaItem]), none }
enum ComposerMediaError: Equatable { case fileTooLarge, uploadFailed, thumbnailLoadFailed }
enum ComposerMediaType: Hashable { case image, video, file }
enum MediaUploadState: Equatable { case failed(ComposerMediaError), uploading, uploaded }

struct UploadMetadata: Equatable {
    let uploadId: String
    let filename: String
    let mimeType: String
    let fileSize: Int
    let dimensions: CGSize
    let duration: Double
    let isImage: Bool
    let isVideo: Bool

    init(from upload: ITBUpload) {
        uploadId = String._unconditionallyBridgeFromObjectiveC(upload.uploadId?.stringValue as NSString?)
        filename = String._unconditionallyBridgeFromObjectiveC(upload.filename as NSString?)
        mimeType = String._unconditionallyBridgeFromObjectiveC(upload.mimeType as NSString?)
        fileSize = Data._unconditionallyBridgeFromObjectiveC(upload.uploadData as NSData?).count
        dimensions = upload.dimensions
        duration = upload.duration
        isImage = upload.isImage()
        isVideo = upload.isVideo()
    }
}

struct MediaItem: Identifiable, Equatable {
    let id: UUID
    var url: URL?
    let type: ComposerMediaType
    var uploadState: MediaUploadState
    var uploadURL: String?
    var uploadMetadata: UploadMetadata?
}
