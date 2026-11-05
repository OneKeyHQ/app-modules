import Foundation
import Combine

@_objcRuntimeName(_TtC18IntercomSDKPrivate17ComposerViewModel)
class ComposerViewModel: ObservableObject {
    @Published var text = ""
    @Published var mediaState: ComposerMediaState = .none
    @Published var isEnabled = true
    let config: any ICMConfigProtocol
    var onMediaPreUpload: ((URL, @escaping (Result<(uploadURL: String, upload: ITBUpload), Error>) -> Void) -> Void)?

    init(config: any ICMConfigProtocol,
         onMediaPreUpload: ((URL, @escaping (Result<(uploadURL: String, upload: ITBUpload), Error>) -> Void) -> Void)?) {
        self.config = config
        self.onMediaPreUpload = onMediaPreUpload
    }

    var canSend: Bool {
        guard isEnabled else { return false }
        if !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty { return mediaReady }
        switch mediaState {
        case .none: return false
        case .hasMedia: return mediaReady
        }
    }

    var mediaReady: Bool {
        let items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        return items.allSatisfy { $0.uploadState == .uploaded }
    }

    func stagePlaceholderMedia(id: UUID, type: ComposerMediaType) -> Bool {
        let items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        guard items.count < 10 else { return false }
        addMediaItem(MediaItem(id: id, url: nil, type: type, uploadState: .uploading, uploadURL: nil, uploadMetadata: nil))
        return true
    }

    func addMediaItem(_ item: MediaItem) {
        var items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        items.append(item)
        mediaState = .hasMedia(items)
    }

    func updatePlaceholderMedia(id: UUID, url: URL) {
        var items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        if let index = items.firstIndex(where: { $0.id == id }) {
            items[index].url = url
            mediaState = .hasMedia(items)
        }
        processMediaItem(id: id)
    }

    func removeMedia(id: UUID) {
        var items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        guard let index = items.firstIndex(where: { $0.id == id }) else { return }
        items.remove(at: index)
        mediaState = items.isEmpty ? .none : .hasMedia(items)
    }

    func retryUpload(id: UUID) {
        var items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        if let index = items.firstIndex(where: { $0.id == id }) {
            items[index].uploadState = .uploading
            mediaState = .hasMedia(items)
        }
        performPreUpload(for: id)
    }

    func markItemAsFailed(id: UUID, error: ComposerMediaError) {
        var items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        if let index = items.firstIndex(where: { $0.id == id }) {
            items[index].uploadState = .failed(error)
            mediaState = .hasMedia(items)
        }
        reorderFailedItemsToFront()
    }

    private func reorderFailedItemsToFront() {
        let items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        guard items.count >= 2 else { return }
        let failed = items.filter { if case .failed = $0.uploadState { return true }; return false }
        let remaining = items.filter { if case .failed = $0.uploadState { return false }; return true }
        let reordered = failed + remaining
        if reordered.map({ $0.id }) != items.map({ $0.id }) { mediaState = .hasMedia(reordered) }
    }

    func validateMedia(atURL url: URL) -> ComposerMediaError? {
        let attributes: [FileAttributeKey: Any]
        do { attributes = try FileManager.default.attributesOfItem(atPath: url.path) }
        catch { return nil }
        guard let size = attributes[.size] as? Int else { return nil }
        if size > config.attachmentSettings.uploadSizeLimit { return .fileTooLarge }
        return nil
    }

    private func processMediaItem(id: UUID) {
        let items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        guard let item = items.first(where: { $0.id == id }), let url = item.url else { return }
        if let error = validateMedia(atURL: url) { markItemAsFailed(id: id, error: error) }
        else { performPreUpload(for: id) }
    }

    private func performPreUpload(for id: UUID) {
        let items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        guard let item = items.first(where: { $0.id == id }), let url = item.url else { return }
        onMediaPreUpload?(url) { [weak self] result in
            guard let self else { return }
            switch result {
            case .failure: self.markItemAsFailed(id: id, error: .uploadFailed)
            case .success(let value):
                let metadata = UploadMetadata(from: value.upload)
                var current: [MediaItem]
                switch self.mediaState { case .none: current = []; case .hasMedia(let values): current = values }
                guard let index = current.firstIndex(where: { $0.id == id }) else { return }
                current[index].uploadState = .uploaded
                current[index].uploadURL = value.uploadURL
                current[index].uploadMetadata = metadata
                self.mediaState = .hasMedia(current)
            }
        }
    }

    func getUploadedMediaData() -> [(uploadURL: String, metadata: UploadMetadata)] {
        let items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        return items.filter { $0.uploadState == .uploaded }.compactMap {
            guard let url = $0.uploadURL, let metadata = $0.uploadMetadata else { return nil }
            return (url, metadata)
        }
    }

    func getUploadedMediaLocalURLs() -> [URL] {
        let items: [MediaItem]
        switch mediaState { case .none: items = []; case .hasMedia(let values): items = values }
        return items.filter { $0.uploadState == .uploaded }.compactMap { $0.url }
    }
}
