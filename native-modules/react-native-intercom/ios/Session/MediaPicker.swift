// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import UIKit
import PhotosUI
import UniformTypeIdentifiers

protocol MediaPickerDelegate: AnyObject {
    func picker(_ picker: MediaPicker, didPickMedia items: [(id: UUID, type: ComposerMediaType)]) -> [UUID]
    func picker(_ picker: MediaPicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>)
}
private struct PickedMediaItem {
    let id: UUID
    let result: PHPickerResult
    let type: UTType
    let composerType: ComposerMediaType
}
@objc(_TtC18IntercomSDKPrivate11MediaPicker)
class MediaPicker: NSObject, PHPickerViewControllerDelegate {
    weak var delegate: (any MediaPickerDelegate)?
    var attachmentFileManager: AttachmentFileManager
    var navigationController: UINavigationController?
    var actionButtonTitle: String?
    var processingTasks: [UUID: Task<(id: UUID, result: Result<URL, PickerError>), Never>] = [:]
    init(attachmentFileManager: AttachmentFileManager, delegate: (any MediaPickerDelegate)?) {
        self.attachmentFileManager = attachmentFileManager
        self.delegate = delegate
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    func presentPhotoGallery(from controller: UIViewController, selectionLimit: Int, actionButtonTitle: String?) {
        self.actionButtonTitle = actionButtonTitle
        var configuration = PHPickerConfiguration(photoLibrary: .shared())
        configuration.filter = .any(of: [.images, .videos, .livePhotos])
        configuration.selectionLimit = selectionLimit
        configuration.preferredAssetRepresentationMode = .current
        let picker = PHPickerViewController(configuration: configuration)
        picker.delegate = self
        navigationController = UINavigationController(rootViewController: picker)
        navigationController?.view!.tintColor = DefaultThemeProvider.shared.theme.iconDefault
        navigationController?.navigationBar.tintColor = DefaultThemeProvider.shared.theme.iconDefault
        navigationController?.setNavigationBarHidden(true, animated: false)
        if let navigationController { controller.present(navigationController, animated: true, completion: nil) }
    }
    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        navigationController?.dismiss(animated: true, completion: nil)
        if !results.isEmpty { processPickerResults(results) }
    }
    private func processPickerResults(_ results: [PHPickerResult]) {
        let supported: [UTType] = [.image, .video, .livePhoto, .movie]
        let unsupported = UTType("public.webp").map { [$0] } ?? []
        let manager = attachmentFileManager
        Task { @MainActor [weak self] in
            guard let self else { return }
            let (items, count) = self.classifyResults(results, supportedTypes: supported, unsupportedTypes: unsupported)
            guard let accepted = self.getAcceptedItems(items) else { return }
            await self.processAcceptedItems(accepted, using: manager)
            if count >= 1, let delegate = self.delegate {
                delegate.picker(self, didFinishLoadingMedia: UUID(), result: .failure(.unsupportedType))
            }
        }
    }
    private func classifyResults(_ results: [PHPickerResult], supportedTypes: [UTType], unsupportedTypes: [UTType]) -> ([PickedMediaItem], Int) {
        var items: [PickedMediaItem] = []
        var unsupportedCount = 0
        for result in results {
            guard let type = supportedTypes.first(where: { result.itemProvider.hasItemConformingToTypeIdentifier($0.identifier) }),
                  !unsupportedTypes.contains(type) else { unsupportedCount += 1; continue }
            let id = UUID()
            let copiedResult = result
            let copiedType = type
            let composerType: ComposerMediaType
            if type.conforms(to: .image) || type.conforms(to: .livePhoto) { composerType = .image }
            else if type.conforms(to: .movie) || type.conforms(to: .video) { composerType = .video }
            else { composerType = .file }
            items.append(PickedMediaItem(id: id, result: copiedResult, type: copiedType, composerType: composerType))
        }
        return (items, unsupportedCount)
    }
    private func getAcceptedItems(_ items: [PickedMediaItem]) -> [PickedMediaItem]? {
        guard let delegate else { return nil }
        return withExtendedLifetime(delegate) {
            let admitted = delegate.picker(self, didPickMedia: items.map { (id: $0.id, type: $0.composerType) })
            return items.filter { admitted.contains($0.id) }
        }
    }
    @MainActor private func loadAndCopyFile(from provider: NSItemProvider, typeIdentifier: String, using manager: AttachmentFileManager) async -> Result<URL, PickerError> {
        await withCheckedContinuation { continuation in
            _ = provider.loadFileRepresentation(forTypeIdentifier: typeIdentifier) { url, _ in
                guard let url else { continuation.resume(returning: .failure(.errorLoadingResource(nil))); return }
                let destination = manager.fileManager.temporaryDirectory.appendingPathComponent(url.lastPathComponent)
                let callback: (Result<URL, Error>) -> Void = { result in
                    switch result {
                    case .success(let value): continuation.resume(returning: .success(value))
                    case .failure: continuation.resume(returning: .failure(.failedToPersistFile(NSError(domain: "MediaPicker", code: 1, userInfo: nil))))
                    }
                }
                if url.isFileURL { manager.copyLocalFile(from: url, to: destination, completion: callback) }
                else { callback(.failure(AttachmentFileManagerError.notLocalFileURL)) }
            }
        }
    }
    @MainActor private func createProcessingTask(for item: PickedMediaItem, using manager: AttachmentFileManager) -> Task<(id: UUID, result: Result<URL, PickerError>), Never> {
        Task { @MainActor [weak self] in
            guard let self else { return (item.id, .failure(.failedToPersistFile(CancellationError()))) }
            if Task.isCancelled { return (item.id, .failure(.failedToPersistFile(CancellationError()))) }
            let provider = item.result.itemProvider
            let identifier = item.type.identifier
            let result = await self.loadAndCopyFile(from: provider, typeIdentifier: identifier, using: manager)
            return (item.id, result)
        }
    }
    @MainActor private func processAcceptedItems(_ items: [PickedMediaItem], using manager: AttachmentFileManager) async {
        await withTaskGroup(of: (id: UUID, result: Result<URL, PickerError>).self) { group in
            for item in items {
                let task = createProcessingTask(for: item, using: manager)
                processingTasks[item.id] = task
                group.addTask { await task.value }
            }
            for await value in group {
                if processingTasks[value.id] == nil { continue }
                guard let delegate else { continue }
                withExtendedLifetime(delegate) {
                    delegate.picker(self, didFinishLoadingMedia: value.id, result: value.result)
                    processingTasks.removeValue(forKey: value.id)
                }
            }
        }
    }
}
