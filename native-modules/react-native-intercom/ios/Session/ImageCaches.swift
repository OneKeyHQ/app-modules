import Foundation
import UIKit

enum ImageCacheKey {
    static func filename(for key: String) -> String {
        var value: UInt64 = 0xcbf29ce484222325
        for byte in key.utf8 { value = (value ^ UInt64(byte)) &* 0x100000001b3 }
        return String(format: "%016llx", value)
    }

    static func key(for url: URL?, processorKey: String? = nil, stripsQuery: Bool) -> String {
        guard let url, !url.absoluteString.isEmpty else { return UUID().uuidString }
        var key = url.absoluteString
        if stripsQuery, var components = URLComponents(url: url, resolvingAgainstBaseURL: false) {
            components.query = nil
            key = components.string ?? url.absoluteString
        }
        if let processorKey, !processorKey.isEmpty { key += processorKey }
        return key
    }
}

final class MemoryImageCache {
    let cache: NSCache<NSString, AnyObject>
    private let notificationCenter: NotificationCenter
    private var backgroundObserver: NSObjectProtocol?
    private var memoryObserver: NSObjectProtocol?

    init(totalCostLimit: Int = 0, purgesOnBackground: Bool = true, notificationCenter: NotificationCenter = .default) {
        cache = NSCache()
        self.notificationCenter = notificationCenter
        cache.totalCostLimit = totalCostLimit
        memoryObserver = notificationCenter.addObserver(forName: UIApplication.didReceiveMemoryWarningNotification, object: nil, queue: nil) { [weak self] _ in
            self?.cache.removeAllObjects()
        }
        if purgesOnBackground {
            backgroundObserver = notificationCenter.addObserver(forName: UIApplication.didEnterBackgroundNotification, object: nil, queue: nil) { [weak self] _ in
                self?.cache.removeAllObjects()
            }
        }
    }

    deinit {
        if let backgroundObserver { notificationCenter.removeObserver(backgroundObserver) }
        if let memoryObserver { notificationCenter.removeObserver(memoryObserver) }
    }
}

final class DiskImageCache {
    let fileManager: FileManager
    let byteLimit: Int
    let queue: DispatchQueue
    let directoryURL: URL

    init?(name: String, byteLimit: Int, fileManager: FileManager = .default, queue: DispatchQueue = DispatchQueue(label: "io.intercom.DiskImageCache", qos: .utility)) {
        guard let root = fileManager.urls(for: .cachesDirectory, in: .userDomainMask).first else { return nil }
        self.fileManager = fileManager
        self.byteLimit = byteLimit
        self.queue = queue
        directoryURL = root.appendingPathComponent(name, isDirectory: true)
        do {
            try fileManager.createDirectory(at: directoryURL, withIntermediateDirectories: true, attributes: nil)
        } catch { return nil }
    }

    func data(forKey key: String) -> Data? {
        let url = directoryURL.appendingPathComponent(ImageCacheKey.filename(for: key))
        guard let data = try? Data(contentsOf: url) else { return nil }
        let fileManager = fileManager
        queue.async {
            try? fileManager.setAttributes([.modificationDate: Date()], ofItemAtPath: url.path)
        }
        return data
    }

    func store(_ data: Data, forKey key: String) {
        queue.async { [weak self] in
            guard let self else { return }
            let url = self.directoryURL.appendingPathComponent(ImageCacheKey.filename(for: key))
            try? data.write(to: url, options: .atomic)
            self.enforceByteLimit()
        }
    }

    private struct CachedFile {
        let url: URL
        let size: Int
        let modificationDate: Date
    }

    private func enforceByteLimit() {
        let keys: Set<URLResourceKey> = [.contentModificationDateKey, .fileSizeKey]
        guard let urls = try? fileManager.contentsOfDirectory(at: directoryURL, includingPropertiesForKeys: [.contentModificationDateKey, .fileSizeKey], options: []) else { return }
        var files: [CachedFile] = []
        for url in urls {
            guard let values = try? url.resourceValues(forKeys: keys),
                  let size = values.fileSize,
                  let date = values.contentModificationDate else { continue }
            files.append(CachedFile(url: url, size: size, modificationDate: date))
        }
        var size = files.reduce(0) { $0 + $1.size }
        guard size > byteLimit else { return }
        files.sort { $0.modificationDate < $1.modificationDate }
        for file in files {
            guard size > byteLimit else { break }
            try? fileManager.removeItem(at: file.url)
            size -= file.size
        }
    }

    func remove(forKey key: String) {
        queue.async { [weak self] in
            guard let self else { return }
            let url = self.directoryURL.appendingPathComponent(ImageCacheKey.filename(for: key))
            try? self.fileManager.removeItem(at: url)
        }
    }

    func removeAll() {
        queue.async { [weak self] in
            guard let self else { return }
            try? self.fileManager.removeItem(at: self.directoryURL)
            try? self.fileManager.createDirectory(at: self.directoryURL, withIntermediateDirectories: true, attributes: nil)
        }
    }
}
