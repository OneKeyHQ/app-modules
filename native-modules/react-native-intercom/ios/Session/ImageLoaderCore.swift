import Foundation
import UIKit

@objc(ICMImageLoaderCore)
class ImageLoaderCore: NSObject {
    private let memoryCache: MemoryImageCache
    private let diskCache: DiskImageCache?
    private let downloader: ImageDownloader
    private let dispatcher: Dispatcher
    private let stripsQueryFromCacheKey: Bool

    init(memoryCache: MemoryImageCache, diskCache: DiskImageCache?, downloader: ImageDownloader, dispatcher: Dispatcher = AsyncQueue.main, stripsQueryFromCacheKey: Bool) {
        self.memoryCache = memoryCache
        self.diskCache = diskCache
        self.downloader = downloader
        self.dispatcher = dispatcher
        self.stripsQueryFromCacheKey = stripsQueryFromCacheKey
        super.init()
    }

    private static func defaultDownloader() -> ImageDownloader {
        let configuration = URLSessionConfiguration.default
        configuration.urlCache = nil
        configuration.requestCachePolicy = .reloadIgnoringLocalCacheData
        return ImageDownloader(session: URLSession(configuration: configuration))
    }

    @objc static func makeDefaultCore() -> ImageLoaderCore {
        ImageLoaderCore(memoryCache: MemoryImageCache(totalCostLimit: 0, purgesOnBackground: true), diskCache: DiskImageCache(name: "com.intercom.image-cache", byteLimit: 10_000_000), downloader: defaultDownloader(), stripsQueryFromCacheKey: true)
    }

    @objc static func makeLongTermCore() -> ImageLoaderCore {
        ImageLoaderCore(memoryCache: MemoryImageCache(totalCostLimit: 0, purgesOnBackground: false), diskCache: DiskImageCache(name: "icm-long-term-image-cache", byteLimit: 5_000_000), downloader: defaultDownloader(), stripsQueryFromCacheKey: false)
    }

    private final class DiskPreflightState { var suppliedData = false }

    @objc(loadImageWithURLString:completion:)
    @discardableResult
    func loadImage(withURLString string: String?, completion: ((Any?) -> Void)?) -> UUID {
        let url = string.flatMap(URL.init(string:))
        let key = ImageCacheKey.key(for: url, stripsQuery: stripsQueryFromCacheKey)
        if let image = memoryCache.cache.object(forKey: key as NSString) {
            deliver(image, to: completion)
            return UUID()
        }
        guard let url else {
            deliver(nil, to: completion)
            return UUID()
        }
        let state = DiskPreflightState()
        return downloader.download(url: url, key: key, preflight: { [weak self] in
            guard let data = self?.diskCache?.data(forKey: key) else { return nil }
            state.suppliedData = true
            return data
        }) { [weak self] data in
            guard let self else { return }
            if let cached = self.memoryCache.cache.object(forKey: key as NSString) {
                self.deliver(cached, to: completion)
                return
            }
            let image: AnyObject?
            if let data {
                image = AnimatedImage(animatedImageData: data) ?? UIImage(data: data)
            } else { image = nil }
            guard let image else {
                self.diskCache?.remove(forKey: key)
                self.deliver(nil, to: completion)
                return
            }
            self.memoryCache.cache.setObject(image, forKey: key as NSString, cost: Self.cost(of: image))
            if !state.suppliedData, let data { self.diskCache?.store(data, forKey: key) }
            self.deliver(image, to: completion)
        }
    }

    private func deliver(_ image: Any?, to completion: ((Any?) -> Void)?) {
        guard let completion else { return }
        if dispatcher.queue === DispatchQueue.main && Thread.isMainThread { completion(image) }
        else { dispatcher.dispatch { completion(image) } }
    }

    @objc(cachedImageForURL:)
    func cachedImage(for url: URL?) -> Any? {
        let key = ImageCacheKey.key(for: url, stripsQuery: stripsQueryFromCacheKey)
        return memoryCache.cache.object(forKey: key as NSString)
    }

    @objc(cachedImageForKey:)
    func cachedImage(forKey key: String) -> Any? { cachedImage(for: URL(string: key)) }

    @objc(cacheImage:forKey:)
    func cacheImage(_ image: Any?, forKey key: String) {
        guard let image else { return }
        let object = image as AnyObject
        memoryCache.cache.setObject(object, forKey: key as NSString, cost: Self.cost(of: object))
    }

    @objc(imageForKey:generationBlock:)
    func image(forKey key: String, generationBlock: (() -> UIImage?)?) -> UIImage? {
        guard !key.isEmpty else { return nil }
        if let image = cachedImage(for: URL(string: key)) as? UIImage { return image }
        guard let image = generationBlock?() else { return nil }
        memoryCache.cache.setObject(image, forKey: key as NSString, cost: Self.cost(of: image))
        return image
    }

    private static func cost(of object: AnyObject) -> Int {
        let image: UIImage?
        if let uiImage = object as? UIImage { image = uiImage }
        else if let animated = object as? AnimatedImage { image = animated.coverImage }
        else { image = nil }
        guard let cgImage = image?.cgImage else { return 0 }
        return cgImage.bytesPerRow * cgImage.height
    }

    @objc func cancelLoad(_ identifier: UUID) { downloader.cancel(identifier) }
    @objc func clearCache() {
        memoryCache.cache.removeAllObjects()
        diskCache?.removeAll()
    }

    @objc override init() { fatalError("init() has not been implemented") }
}
