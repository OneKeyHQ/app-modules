import CryptoKit
import Foundation
import ImageIO
import SDWebImage
import UIKit

/// Native-only process cache. Players remain owned by their view slots.
public final class OneKeyMediaPreviewCache {
  public static let shared = OneKeyMediaPreviewCache()
  public static func targetEdge(_ pixels: CGFloat) -> Int {
    [128, 256, 512, 1024].first { CGFloat($0) >= pixels } ?? 1024
  }
  public final class Subscription {
    fileprivate var cancelled = false
    fileprivate let key: String
    fileprivate let image: (UIImage) -> Void
    fileprivate let extract: (UUID) -> Void
    fileprivate let revoked: () -> Void
    fileprivate let failure: () -> Void
    fileprivate init(key: String, image: @escaping (UIImage) -> Void, extract: @escaping (UUID) -> Void,
      revoked: @escaping () -> Void, failure: @escaping () -> Void) {
      self.key = key; self.image = image; self.extract = extract; self.revoked = revoked; self.failure = failure
    }
  }
  private final class Flight {
    let key: String
    let storageKey: String
    let policy: String
    let memoryGeneration: Int
    let diskGeneration: Int
    var subscribers: [Subscription] = []
    var lease: UUID?
    var lookupWorkItem: DispatchWorkItem?
    var timeout: DispatchWorkItem?
    var ready = false
    init(key: String, storageKey: String, policy: String, memory: Int, disk: Int) {
      self.key = key; self.storageKey = storageKey; self.policy = policy
      memoryGeneration = memory; diskGeneration = disk
    }
  }
  private let cache: SDImageCache
  private let io = DispatchQueue(label: "onekey.media-preview.io", qos: .userInitiated)
  private var flights: [String: Flight] = [:]
  private var queue: [String] = []
  private var active = 0
  private var pendingPersistenceBytes = 0
  private var memoryGeneration = 0
  private var diskGeneration = 0
  init(namespace: String = "onekey-media-preview-v1") {
    let config = SDImageCacheConfig()
    config.maxMemoryCost = 16 * 1024 * 1024; config.maxMemoryCount = 64
    config.maxDiskSize = 64 * 1024 * 1024; config.maxDiskAge = 30 * 24 * 60 * 60
    config.diskCacheExpireType = .accessDate
    cache = SDImageCache(namespace: namespace, diskCacheDirectory: nil, config: config)
  }
  static func storageKey(uri: String, headers: [String: String], edge: Int, fit: String) -> String {
    let normalized: [(String, String)] = headers.map { ($0.key.lowercased(), $0.value) }
    let fields: [(String, String)] = normalized.sorted {
      $0.0 == $1.0 ? $0.1 < $1.1 : $0.0 < $1.0
    }
    let values = ["onekey-media-preview-v1", uri, String(edge), fit] + fields.flatMap { [$0.0, $0.1] }
    let canonical = values.map { "\($0.utf8.count):\($0)" }.joined()
    return SHA256.hash(data: Data(canonical.utf8)).map { String(format: "%02x", $0) }.joined()
  }
  @discardableResult
  public func request(uri: String, headers: [String: String], edge: Int, fit: String, policy: String,
    onImage: @escaping (UIImage) -> Void, onExtract: @escaping (UUID) -> Void,
    onRevoked: @escaping () -> Void, onFailure: @escaping () -> Void) -> Subscription {
    dispatchPrecondition(condition: .onQueue(.main))
    let storageKey = Self.storageKey(uri: uri, headers: headers,
      edge: Self.targetEdge(CGFloat(max(1, edge))), fit: fit)
    let key = "\(storageKey)|\(policy)"
    let subscriber = Subscription(key: key, image: onImage, extract: onExtract, revoked: onRevoked, failure: onFailure)
    if let flight = flights[key] { flight.subscribers.append(subscriber); return subscriber }
    guard flights.count < 130 else {
      DispatchQueue.main.async { if !subscriber.cancelled { subscriber.failure() } }
      return subscriber
    }
    let flight = Flight(key: key, storageKey: storageKey, policy: policy, memory: memoryGeneration, disk: diskGeneration)
    flight.subscribers = [subscriber]; flights[key] = flight
    if permitsMemory(policy), let image = cache.imageFromMemoryCache(forKey: storageKey) {
      DispatchQueue.main.async { [weak self] in self?.finish(flight, image: image) }; return subscriber
    }
    let lookup = DispatchWorkItem { [weak self, weak flight] in
      guard let self, let flight, flight.lookupWorkItem?.isCancelled == false else { return }
      var image: UIImage?
      if self.permitsDisk(policy), let path = self.cache.cachePath(forKey: storageKey) {
        let url = URL(fileURLWithPath: path)
        let accessDate = try? url.resourceValues(forKeys: [.contentAccessDateKey]).contentAccessDate
        if let accessDate, accessDate > Date().addingTimeInterval(-self.cache.config.maxDiskAge),
          let file = FileHandle(forReadingAtPath: path) {
          defer { try? file.close() }
          if let data = try? file.read(upToCount: 8 * 1024 * 1024 + 1), data.count <= 8 * 1024 * 1024,
            let source = CGImageSourceCreateWithData(data as CFData, nil),
            let properties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as? [CFString: Any],
            let width = properties[kCGImagePropertyPixelWidth] as? Int,
            let height = properties[kCGImagePropertyPixelHeight] as? Int,
            (1...1024).contains(width), (1...1024).contains(height) {
            let options = [kCGImageSourceShouldCacheImmediately: true] as CFDictionary
            image = CGImageSourceCreateImageAtIndex(source, 0, options).map { UIImage(cgImage: $0) }
          }
          if image != nil {
            try? (url as NSURL).setResourceValue(Date(), forKey: .contentAccessDateKey)
          }
        }
        if image == nil { self.cache.removeImageFromDisk(forKey: storageKey) }
      }
      DispatchQueue.main.async {
        guard self.flights[key] === flight else { return }
        if let image, flight.diskGeneration == self.diskGeneration {
          if self.permitsMemory(policy), flight.memoryGeneration == self.memoryGeneration {
            self.cache.storeImage(toMemory: image, forKey: storageKey)
          }
          self.finish(flight, image: image)
        } else { flight.ready = true; self.queue.append(key); self.pump() }
      }
    }
    flight.lookupWorkItem = lookup
    io.async(execute: lookup)
    return subscriber
  }
  public func cancel(_ subscription: Subscription?) {
    dispatchPrecondition(condition: .onQueue(.main))
    guard let subscription else { return }
    subscription.cancelled = true
    guard let flight = flights[subscription.key],
      let index = flight.subscribers.firstIndex(where: { $0 === subscription }) else { return }
    let owned = index == 0 && flight.lease != nil
    flight.subscribers.remove(at: index)
    if owned {
      flight.timeout?.cancel(); flight.timeout = nil; flight.lease = nil; active -= 1; subscription.revoked()
    }
    if flight.subscribers.isEmpty {
      flight.lookupWorkItem?.cancel()
      flights.removeValue(forKey: flight.key); queue.removeAll { $0 == flight.key }
    } else if owned { queue.insert(flight.key, at: 0) }
    pump()
  }
  public func fail(_ lease: UUID) {
    dispatchPrecondition(condition: .onQueue(.main))
    guard let flight = flights.values.first(where: { $0.lease == lease }) else { return }; finish(flight, image: nil)
  }
  /// Producers submit bounded, orientation-normalized pixels, never an active player.
  public func complete(_ lease: UUID, image: UIImage) {
    dispatchPrecondition(condition: .onQueue(.main))
    guard let flight = flights.values.first(where: { $0.lease == lease }) else { return }
    guard image.size.width > 0, image.size.height > 0,
      image.size.width * image.scale <= 1024, image.size.height * image.scale <= 1024 else {
      finish(flight, image: nil); return
    }
    if permitsMemory(flight.policy), flight.memoryGeneration == memoryGeneration {
      cache.storeImage(toMemory: image, forKey: flight.storageKey)
    }
    finish(flight, image: image)
    let cost = Int(image.size.width * image.scale) * Int(image.size.height * image.scale) * 4
    guard permitsDisk(flight.policy), pendingPersistenceBytes + cost <= 16 * 1024 * 1024 else { return }
    pendingPersistenceBytes += cost
    io.async { [weak self] in
      guard let self else { return }
      let data = image.pngData()
      DispatchQueue.main.async {
        guard flight.diskGeneration == self.diskGeneration, let data, data.count <= 8 * 1024 * 1024 else {
          self.pendingPersistenceBytes -= cost; return
        }
        // Writes and clears share this queue, closing the generation-check/write race.
        self.io.async {
          self.cache.storeImageData(toDisk: data, forKey: flight.storageKey)
          self.cache.deleteOldFiles(completionBlock: nil)
          DispatchQueue.main.async { self.pendingPersistenceBytes -= cost }
        }
      }
    }
  }
  public func clearMemory() {
    dispatchPrecondition(condition: .onQueue(.main)); memoryGeneration &+= 1; cache.clearMemory()
  }
  public func clearDisk(completion: @escaping () -> Void) {
    dispatchPrecondition(condition: .onQueue(.main)); diskGeneration &+= 1
    io.async { [self] in
      let cleared = DispatchSemaphore(value: 0)
      cache.clearDisk { cleared.signal() }
      cleared.wait()
      DispatchQueue.main.async { completion() }
    }
  }
  private func permitsMemory(_ policy: String) -> Bool { policy == "memory" || policy == "memory-disk" }
  private func permitsDisk(_ policy: String) -> Bool { policy == "disk" || policy == "memory-disk" }
  private func pump() {
    while active < 2, !queue.isEmpty {
      let key = queue.removeFirst()
      guard let flight = flights[key], flight.ready, flight.lease == nil, let owner = flight.subscribers.first else { continue }
      let lease = UUID(); flight.lease = lease; active += 1
      let timeout = DispatchWorkItem { [weak self, weak flight] in
        guard let self, let flight, flight.lease == lease else { return }; self.finish(flight, image: nil)
      }
      flight.timeout = timeout; DispatchQueue.main.asyncAfter(deadline: .now() + 10, execute: timeout); owner.extract(lease)
    }
  }
  private func finish(_ flight: Flight, image: UIImage?) {
    guard flights[flight.key] === flight else { return }
    flight.lookupWorkItem?.cancel()
    flights.removeValue(forKey: flight.key); queue.removeAll { $0 == flight.key }
    if flight.lease != nil { active -= 1; flight.subscribers.first?.revoked() }
    flight.timeout?.cancel(); flight.lease = nil
    let subscribers = flight.subscribers; flight.subscribers = []
    for subscriber in subscribers where !subscriber.cancelled { if let image { subscriber.image(image) } else { subscriber.failure() } }
    pump()
  }
}
