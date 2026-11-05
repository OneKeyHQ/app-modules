import UIKit

protocol ImageProvider {
    func image(for url: URL) async throws -> UIImage
}

struct DefaultImageLoader: ImageProvider {
    enum ProviderError: Error { case unknownError }
    let imageLoader: ITBImageLoader

    func image(for url: URL) async throws -> UIImage {
        try await withCheckedThrowingContinuation { continuation in
            let token = imageLoader.loadImage(withURLString: url.absoluteString) { value in
                if let image = value as? UIImage { continuation.resume(returning: image) }
                else { continuation.resume(throwing: ProviderError.unknownError) }
            }
            if let token { _ = token as UUID }
        }
    }
}
