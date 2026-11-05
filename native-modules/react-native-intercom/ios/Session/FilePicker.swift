// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import UIKit
import UniformTypeIdentifiers

protocol FilePickerDelegate: AnyObject {
    func picker(_ picker: FilePicker, didPickMedia items: [(id: UUID, type: ComposerMediaType)]) -> [UUID]
    func picker(_ picker: FilePicker, didFinishLoadingMedia id: UUID, result: Result<URL, PickerError>)
}
private struct PickedFileItem {
    let id: UUID
    let url: URL
    let type: ComposerMediaType
}

@objc(_TtC18IntercomSDKPrivate10FilePicker)
class FilePicker: NSObject, UIDocumentPickerDelegate {
    weak var delegate: (any FilePickerDelegate)?
    var allowsMultipleSelection = false
    var attachmentFileManager: AttachmentFileManager
    weak var presentingViewController: UIViewController?
    var config: any ICMConfigProtocol
    var selectionLimit = 10
    var processingTasks: [UUID: Task<(id: UUID, result: Result<URL, PickerError>), Never>] = [:]

    init(attachmentFileManager: AttachmentFileManager, config: any ICMConfigProtocol, delegate: (any FilePickerDelegate)?) {
        self.attachmentFileManager = attachmentFileManager
        self.config = config
        self.delegate = delegate
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    func presentFilePicker(from controller: UIViewController, selectionLimit: Int) {
        self.selectionLimit = selectionLimit
        allowsMultipleSelection = selectionLimit > 1
        let extensions = config.attachmentSettings.allowedFileExtensions
        let types = extensions.compactMap { UTType(filenameExtension: $0, conformingTo: .data) }
        let picker = UIDocumentPickerViewController(forOpeningContentTypes: types)
        picker.delegate = self
        picker.allowsMultipleSelection = allowsMultipleSelection
        presentingViewController = controller
        controller.present(picker, animated: true, completion: nil)
    }
    func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        controller.dismiss(animated: true, completion: nil)
        if urls.isEmpty { return }
        let limit = selectionLimit
        let manager = attachmentFileManager
        Task { @MainActor [weak self] in
            guard let self else { return }
            let items = self.createPickedFileItems(urls, limit: limit)
            guard let accepted = self.getAcceptedFileItems(items) else { return }
            await self.processAcceptedFiles(accepted, using: manager)
        }
    }
    func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        controller.dismiss(animated: true, completion: nil)
    }

    private func createPickedFileItems(_ urls: [URL], limit: Int) -> [PickedFileItem] {
        urls.prefix(limit).map { url in
            let id = UUID()
            let copiedURL = url
            let type: ComposerMediaType
            if let utType = UTType(filenameExtension: copiedURL.pathExtension, conformingTo: .data) {
                if utType.conforms(to: .image) || utType.conforms(to: .livePhoto) { type = .image }
                else if utType.conforms(to: .movie) || utType.conforms(to: .video) { type = .video }
                else { type = .file }
            } else { type = .file }
            return PickedFileItem(id: id, url: copiedURL, type: type)
        }
    }
    private func getAcceptedFileItems(_ items: [PickedFileItem]) -> [PickedFileItem]? {
        guard let delegate else { return nil }
        return withExtendedLifetime(delegate) {
            let admitted = delegate.picker(self, didPickMedia: items.map { (id: $0.id, type: $0.type) })
            return items.filter { admitted.contains($0.id) }
        }
    }
    @MainActor private func loadAndCopyFile(from source: URL, using manager: AttachmentFileManager) async -> URL? {
        await withCheckedContinuation { continuation in
            let accessing = source.startAccessingSecurityScopedResource()
            let destination = manager.fileManager.temporaryDirectory.appendingPathComponent(source.lastPathComponent)
            let callback: (Result<URL, Error>) -> Void = { result in
                if accessing { source.stopAccessingSecurityScopedResource() }
                switch result {
                case .success(let url): continuation.resume(returning: url)
                case .failure: continuation.resume(returning: nil)
                }
            }
            if source.isFileURL { manager.copyLocalFile(from: source, to: destination, completion: callback) }
            else { callback(.failure(AttachmentFileManagerError.notLocalFileURL)) }
        }
    }

    @MainActor private func createFileProcessingTask(for item: PickedFileItem, using manager: AttachmentFileManager) -> Task<(id: UUID, result: Result<URL, PickerError>), Never> {
        Task { @MainActor [weak self] in
            guard let self else { return (item.id, .failure(.failedToPersistFile(CancellationError()))) }
            if Task.isCancelled { return (item.id, .failure(.failedToPersistFile(CancellationError()))) }
            let url = await self.loadAndCopyFile(from: item.url, using: manager)
            if let url { return (item.id, .success(url)) }
            return (item.id, .failure(.failedToPersistFile(NSError(domain: "FilePicker", code: 1, userInfo: nil))))
        }
    }
    @MainActor private func processAcceptedFiles(_ items: [PickedFileItem], using manager: AttachmentFileManager) async {
        await withTaskGroup(of: (id: UUID, result: Result<URL, PickerError>).self) { group in
            for item in items {
                let task = createFileProcessingTask(for: item, using: manager)
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
