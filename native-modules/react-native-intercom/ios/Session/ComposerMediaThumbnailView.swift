// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import SwiftUI
import UIKit

private enum ThumbnailLoadState { case loading, failed, loaded(UIImage) }

struct ComposerMediaThumbnailView: View {
    let mediaType: ComposerMediaType?
    let mediaURL: URL?
    let mediaError: ComposerMediaError?
    let isUploadingMedia: Bool
    let themeProvider: any ThemeProvider
    let onRemove: () -> Void
    let onPreviewTap: () -> Void
    let onErrorTap: () -> Void
    let onMediaFailure: ((ComposerMediaError) -> Void)?
    @State private var thumbnailLoadState: ThumbnailLoadState = .loading

    @ViewBuilder var body: some View {
        switch mediaType {
        case .video?:
            ComposerVideoThumbnail(thumbnail: loadedImage, themeProvider: themeProvider, state: visualState,
                onClose: close, onRetry: retryHandler, onVideoTap: mediaError != nil ? onErrorTap : onPreviewTap)
                .task(id: mediaURL) { await loadThumbnail() }
        case .file?:
            ComposerFileThumbnail(fileName: mediaURL?.lastPathComponent ?? "Unknown", themeProvider: themeProvider,
                state: fileState, onClose: close, onRetry: retryHandler, onFileTap: mediaError != nil ? onErrorTap : onPreviewTap)
        case .image?, nil:
            ComposerImageThumbnail(image: loadedImage, themeProvider: themeProvider, state: visualState,
                onClose: close, onRetry: retryHandler, onImageTap: mediaError != nil ? onErrorTap : onPreviewTap)
                .task(id: mediaURL) { await loadThumbnail() }
        }
    }
    private var loadedImage: UIImage? {
        if case .loaded(let image) = thumbnailLoadState { return image }
        return nil
    }
    private var visualState: ThumbnailState {
        if let mediaError {
            return .error(allowsRetry: mediaError != .fileTooLarge)
        }
        if case .failed = thumbnailLoadState { return .error(allowsRetry: true) }
        if isUploadingMedia { return .uploading }
        if case .loading = thumbnailLoadState { return .loadingPreview }
        return .idle
    }
    private var fileState: ThumbnailState {
        if let mediaError { return .error(allowsRetry: mediaError != .fileTooLarge) }
        return isUploadingMedia ? .uploading : .idle
    }
    private var retryHandler: (() -> Void)? {
        if mediaError != nil { return onErrorTap }
        if case .failed = thumbnailLoadState { return handleRetry }
        return nil
    }
    private func close() { onRemove(); thumbnailLoadState = .loading }
    private func handleRetry() {
        thumbnailLoadState = .loading
        Task { await loadThumbnail() }
    }

    @MainActor private func loadThumbnail() async {
        guard let url = mediaURL else { thumbnailLoadState = .loading; return }
        let image: UIImage?
        switch mediaType {
        case .video?: image = await loadVideoThumbnail(from: url)
        case .file?: return
        case .image?, nil: image = await loadImageThumbnail(from: url)
        }
        if let image { thumbnailLoadState = .loaded(image) }
        else {
            thumbnailLoadState = .failed
            onMediaFailure?(.thumbnailLoadFailed)
        }
    }
    @MainActor private func loadImageThumbnail(from url: URL) async -> UIImage? {
        await Task.detached(priority: nil) { UIImage(contentsOfFile: url.path) }.value
    }
    @MainActor private func loadVideoThumbnail(from url: URL) async -> UIImage? {
        await withCheckedContinuation { continuation in
            VideoFileProcessor.shared.generateThumbnail(forVideoAtURL: url) { result in
                switch result {
                case .success(let image): continuation.resume(returning: image)
                case .failure: continuation.resume(returning: nil)
                }
            }
        }
    }
}
