import AVFoundation
import UIKit

protocol VideoFileProcessorProtocol {
    func generateThumbnail(forVideoAtURL url: URL, completion: @escaping (Result<UIImage, Error>) -> Void)
    func compressVideo(at url: URL, completion: @escaping (Result<URL, Error>) -> Void)
}
enum VideoFileProcessorError: Error { case fileNotFound, invalidVideo, unknownError }

class VideoFileProcessor: VideoFileProcessorProtocol {
    private let queue: DispatchQueue
    private let thumbnailCache = NSCache<NSURL, UIImage>()
    static let shared = VideoFileProcessor(queue: DispatchQueue(label: "io.intercom.VideoFileProcessorQueue", qos: .userInitiated, attributes: [], autoreleaseFrequency: .inherit, target: nil))
    init(queue: DispatchQueue) { self.queue = queue }

    func generateThumbnail(forVideoAtURL url: URL, completion: @escaping (Result<UIImage, Error>) -> Void) {
        if let cached = thumbnailCache.object(forKey: url as NSURL) { completion(.success(cached)); return }
        queue.async(qos: .unspecified, flags: []) { [weak self] in
            let asset = AVURLAsset(url: url, options: nil)
            let generator = AVAssetImageGenerator(asset: asset)
            generator.appliesPreferredTrackTransform = true
            do {
                let cgImage = try generator.copyCGImage(at: CMTime(seconds: 0, preferredTimescale: 600), actualTime: nil)
                let image = UIImage(cgImage: cgImage)
                self?.thumbnailCache.setObject(image, forKey: url as NSURL)
                completion(.success(image))
            } catch { completion(.failure(error)) }
        }
    }
    func compressVideo(at url: URL, completion: @escaping (Result<URL, Error>) -> Void) {
        queue.async {
            guard FileManager.default.fileExists(atPath: url.path) else { completion(.failure(VideoFileProcessorError.fileNotFound)); return }
            let asset = AVURLAsset(url: url, options: nil)
            guard asset.isPlayable, let session = AVAssetExportSession(asset: asset, presetName: AVAssetExportPresetMediumQuality) else { completion(.failure(VideoFileProcessorError.invalidVideo)); return }
            let output = URL(fileURLWithPath: NSTemporaryDirectory(), isDirectory: true).appendingPathComponent(url.lastPathComponent + "_compressed.mp4")
            try? FileManager.default.removeItem(at: output)
            session.outputURL = output
            session.outputFileType = .mp4
            session.shouldOptimizeForNetworkUse = true
            session.exportAsynchronously {
                switch session.status {
                case .failed, .cancelled: completion(.failure(session.error ?? VideoFileProcessorError.unknownError))
                case .completed: completion(.success(output))
                default: completion(.failure(VideoFileProcessorError.unknownError))
                }
            }
        }
    }
}
