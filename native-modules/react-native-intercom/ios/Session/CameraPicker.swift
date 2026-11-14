import UIKit
import AVFoundation
import UniformTypeIdentifiers

protocol CameraPickerDelegate: AnyObject {
    func picker(_ picker: CameraPicker, didPickMedia id: UUID, type: ComposerMediaType) -> Bool
    func picker(_ picker: CameraPicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>)
}
@objc(_TtC18IntercomSDKPrivate12CameraPicker)
class CameraPicker: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    weak var delegate: (any CameraPickerDelegate)?
    var attachmentFileManager: AttachmentFileManager
    weak var presentingViewController: UIViewController?
    var currentProcessingTask: Task<Void, Never>?
    var currentProcessingId: UUID?
    init(attachmentFileManager: AttachmentFileManager, delegate: (any CameraPickerDelegate)?) {
        self.attachmentFileManager = attachmentFileManager
        self.delegate = delegate
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    @MainActor func openCamera(from controller: UIViewController) async -> Bool {
        let picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.mediaTypes = [UTType.image.identifier, UTType.movie.identifier]
        picker.delegate = self
        presentingViewController = controller
        guard await AVCaptureDevice.requestAccess(for: .video) else { return false }
        controller.present(picker, animated: true, completion: nil)
        return true
    }
    func cancelProcessing(id: UUID) {
        guard currentProcessingId == id else { return }
        currentProcessingTask?.cancel()
        currentProcessingTask = nil
        currentProcessingId = nil
    }
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        picker.dismiss(animated: true, completion: nil)
        let type: ComposerMediaType
        if let identifier = info[.mediaType] as? String, let utType = UTType(identifier) {
            if utType.conforms(to: .image) || utType.conforms(to: .livePhoto) { type = .image }
            else if utType.conforms(to: .movie) || utType.conforms(to: .video) { type = .video }
            else { type = .file }
        } else if info[.originalImage] != nil { type = .image }
        else if info[.mediaURL] != nil { type = .video }
        else { return }
        let id = UUID()
        do {
            guard let delegate else { return }
            guard delegate.picker(self, didPickMedia: id, type: type) else { return }
        }
        currentProcessingId = id
        if let image = info[.originalImage] as? UIImage {
            let manager = attachmentFileManager
            let callback: (Result<URL, Error>) -> Void = { [weak self] in self?.handleSelectedMedia(result: $0, mediaId: id) }
            let filename = UUID().uuidString + ".jpg"
            let destination = manager.fileManager.temporaryDirectory.appendingPathComponent(filename)
            guard let data = image.jpegData(compressionQuality: 0.8) else {
                callback(.failure(AttachmentFileManagerError.imageConversionFailed)); return
            }
            do { try data.write(to: destination, options: []); callback(.success(destination)) }
            catch { callback(.failure(error)) }
        } else if let url = info[.mediaURL] as? URL {
            let manager = attachmentFileManager
            manager.saveLocalCopyOfFiles(at: [url]) { [weak self] result in
                switch result {
                case .failure(let error): self?.handleSelectedMedia(result: .failure(error), mediaId: id)
                case .success(let urls):
                    if let url = urls.first { self?.handleSelectedMedia(result: .success(url), mediaId: id) }
                    else { self?.handleSelectedMedia(result: .failure(AttachmentFileManagerError.downloadFailed), mediaId: id) }
                }
            }
        } else {
            Task { @MainActor [weak self] in
                guard let self, let delegate = self.delegate else { return }
                delegate.picker(self, didFinishLoadingMedia: id, result: .failure(.noOriginalImage))
            }
        }
    }
    private func handleSelectedMedia(result: Result<URL, Error>, mediaId: UUID) {
        guard currentProcessingId == mediaId else { return }
        currentProcessingTask = Task { @MainActor [weak self] in
            if Task.isCancelled { return }
            guard let self else { return }
            do {
                guard let delegate = self.delegate else { return }
                let mapped = result.mapError { PickerError.failedToPersistFile($0) }
                if !Task.isCancelled { delegate.picker(self, didFinishLoadingMedia: mediaId, result: mapped) }
            }
            self.currentProcessingTask = nil
            self.currentProcessingId = nil
        }
    }
}
