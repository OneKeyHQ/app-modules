// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
import UIKit
import ImageIO
import UniformTypeIdentifiers

@objc(ICMAnimatedImage)
class AnimatedImage: NSObject {
    @objc let coverImage: UIImage
    @objc let frameCount: Int
    @objc let loopCount: Int
    private let delays: [Double]
    private let source: CGImageSource
    private let frameCache = NSCache<NSNumber, UIImage>()
    private let cumulativeDurations: [Double]
    private let framesFitInCache: Bool
    private let decodeLock = NSLock()
    private let decodeQueue = DispatchQueue(label: "io.intercom.AnimatedImage.decode", qos: .utility)
    private var hasPrewarmed = false

    @objc init?(animatedImageData: Data) {
        guard let source = CGImageSourceCreateWithData(animatedImageData as CFData, nil),
              CGImageSourceGetCount(source) >= 2,
              Self.isAnimatedContainer(source),
              let image = CGImageSourceCreateImageAtIndex(source, 0, nil) else { return nil }
        let count = CGImageSourceGetCount(source)
        var delays: [Double] = []
        for index in 0..<count { delays.append(Self.frameDelay(from: source, at: index)) }
        let cover = UIImage(cgImage: image)
        self.source = source
        frameCount = count
        loopCount = Self.loopCount(from: source)
        coverImage = cover
        self.delays = delays
        var cumulative: [Double] = []
        var duration = 0.0
        for delay in delays { duration += delay; cumulative.append(duration) }
        cumulativeDurations = cumulative
        if let cgImage = cover.cgImage {
            let cost = cgImage.bytesPerRow * cgImage.height
            framesFitInCache = cost >= 1 && cost * count <= 50_000_000
        } else { framesFitInCache = false }
        super.init()
        frameCache.totalCostLimit = 50_000_000
    }

    @objc var size: CGSize { coverImage.size }
    @objc var duration: Double { cumulativeDurations.last ?? 0 }
    @objc var preferredFrameRate: Int {
        guard var minimum = delays.first else { return 60 }
        for delay in delays.dropFirst() { if delay < minimum { minimum = delay } }
        guard minimum > 0 else { return 60 }
        return min(Int(ceil(1 / minimum)), 60)
    }

    @objc(frameAtIndex:)
    func frame(at index: Int) -> UIImage? {
        guard index >= 0 && index < frameCount else { return nil }
        if let image = frameCache.object(forKey: NSNumber(value: index)) { return image }
        return decodeFrame(at: index)
    }

    private func decodeFrame(at index: Int) -> UIImage? {
        decodeLock.lock()
        defer { decodeLock.unlock() }
        let key = NSNumber(value: index)
        if let image = frameCache.object(forKey: key) { return image }
        guard let cgImage = CGImageSourceCreateImageAtIndex(source, index, nil) else { return nil }
        let image = UIImage(cgImage: cgImage)
        frameCache.setObject(image, forKey: key, cost: cgImage.bytesPerRow * cgImage.height)
        return image
    }

    @objc func prepareForPlayback() {
        decodeLock.lock()
        if hasPrewarmed {
            hasPrewarmed = true
            decodeLock.unlock()
            return
        }
        let shouldPrewarm = framesFitInCache
        hasPrewarmed = true
        decodeLock.unlock()
        guard shouldPrewarm else { return }
        decodeQueue.async { [weak self] in
            let count = self?.frameCount ?? 0
            for index in 0..<count {
                guard let current = self else { return }
                if current.frameCache.object(forKey: NSNumber(value: index)) == nil {
                    _ = current.decodeFrame(at: index)
                }
            }
        }
    }

    @objc(frameIndexAtPlayHead:)
    func frameIndex(atPlayHead head: Double) -> Int {
        guard !cumulativeDurations.isEmpty else { return 0 }
        var low = 0
        var high = cumulativeDurations.count - 1
        while low <= high {
            let middle = low + (high - low) / 2
            if cumulativeDurations[middle] < head { low = middle + 1 }
            else { high = middle - 1 }
        }
        return max(0, min(cumulativeDurations.count - 1, low))
    }

    private static func isAnimatedContainer(_ source: CGImageSource) -> Bool {
        guard let identifier = CGImageSourceGetType(source) as String?, let type = UTType(identifier) else { return false }
        return type.conforms(to: .gif) || type.conforms(to: .png)
    }

    private static func loopCount(from source: CGImageSource) -> Int {
        guard let properties = CGImageSourceCopyProperties(source, nil) as? [String: Any] else { return 0 }
        if let gif = properties[kCGImagePropertyGIFDictionary as String] as? [String: Any],
           let count = gif[kCGImagePropertyGIFLoopCount as String] as? Int { return count }
        if let png = properties[kCGImagePropertyPNGDictionary as String] as? [String: Any],
           let count = png[kCGImagePropertyAPNGLoopCount as String] as? Int { return count }
        return 0
    }

    private static func frameDelay(from source: CGImageSource, at index: Int) -> Double {
        let properties = CGImageSourceCopyPropertiesAtIndex(source, index, nil) as? [String: Any]
        let gif = properties?[kCGImagePropertyGIFDictionary as String] as? [String: Any]
        let png = properties?[kCGImagePropertyPNGDictionary as String] as? [String: Any]
        let delay = gif?[kCGImagePropertyGIFUnclampedDelayTime as String] as? Double
            ?? gif?[kCGImagePropertyGIFDelayTime as String] as? Double
            ?? png?[kCGImagePropertyAPNGUnclampedDelayTime as String] as? Double
            ?? png?[kCGImagePropertyAPNGDelayTime as String] as? Double
            ?? 0.1
        return delay < 0.02 ? 0.1 : delay
    }

    @objc override init() { fatalError("init() has not been implemented") }
}
