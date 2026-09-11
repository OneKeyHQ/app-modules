// OneKey patch: Share local avatar work and persistent SDWebImage caching across
// render/preload requests, including managers isolated for HTTP headers.
import Foundation
import SDWebImage
import UIKit

final class OneKeyAvatarImageLoader: NSObject, SDImageLoader {
  static let shared = OneKeyAvatarImageLoader()
  static let cache: SDImageCache = {
    let config = SDImageCacheConfig()
    config.maxDiskAge = 30 * 24 * 60 * 60
    config.maxDiskSize = 32 * 1024 * 1024
    config.maxMemoryCost = 8 * 1024 * 1024
    // OneKey patch: Keep several large-account wallets resident while the byte
    // budget remains the hard memory bound.
    config.maxMemoryCount = 512
    return SDImageCache(namespace: "onekey-avatar-blockie-v1", diskCacheDirectory: nil, config: config)
  }()

  private final class Subscription: NSObject, SDWebImageOperation {
    let id = UUID()
    let options: SDWebImage.SDWebImageOptions
    let context: [SDWebImageContextOption: Any]?
    private let lock = NSLock()
    private var completion: SDImageLoaderCompletedBlock?
    private var cancellation: (() -> Void)?
    private var terminal = false

    init(options: SDWebImage.SDWebImageOptions, context: [SDWebImageContextOption: Any]?,
      completion: SDImageLoaderCompletedBlock?) {
      self.options = options; self.context = context; self.completion = completion
    }

    var isTerminal: Bool {
      lock.lock(); defer { lock.unlock() }; return terminal
    }

    func onCancel(_ block: @escaping () -> Void) {
      lock.lock()
      let alreadyTerminal = terminal
      if !alreadyTerminal { cancellation = block }
      lock.unlock()
      if alreadyTerminal { block() }
    }

    func finish(image: UIImage?, data: Data?, error: Error?) {
      lock.lock()
      guard !terminal else { lock.unlock(); return }
      terminal = true
      let callback = completion
      completion = nil; cancellation = nil
      lock.unlock()
      callback?(image, data, error, true)
    }

    func cancel() {
      lock.lock()
      guard !terminal else { lock.unlock(); return }
      terminal = true
      let callback = completion, cancel = cancellation
      completion = nil; cancellation = nil
      lock.unlock()
      cancel?()
      // Preload's checked continuation must also terminate on cancellation.
      DispatchQueue.global(qos: .userInitiated).async {
        callback?(nil, nil, URLError(.cancelled), true)
      }
    }
  }

  private final class Flight {
    let id = UUID()
    let descriptor: OneKeyBlockieDescriptor
    let url: URL
    var subscriptions: [UUID: Subscription] = [:]
    var operation: BlockOperation?
    init(descriptor: OneKeyBlockieDescriptor, url: URL) {
      self.descriptor = descriptor; self.url = url
    }
  }

  private let state = DispatchQueue(label: "onekey.avatar.state")
  private let workers: OperationQueue = {
    let queue = OperationQueue()
    queue.name = "onekey.avatar.generate"
    queue.qualityOfService = .userInitiated
    queue.maxConcurrentOperationCount = 2
    return queue
  }()
  private var flights: [String: Flight] = [:]
  private var diskWrites = 0

  func canRequestImage(for url: URL?) -> Bool {
    // Own invalid local-avatar URLs too: fail locally instead of using HTTP.
    url?.scheme == "onekey-avatar"
  }

  func shouldBlockFailedURL(with url: URL, error: Error) -> Bool { false }

  func requestImage(with url: URL?, options: SDWebImage.SDWebImageOptions,
    context: [SDWebImageContextOption: Any]?, progress: SDImageLoaderProgressBlock?,
    completed: SDImageLoaderCompletedBlock?) -> SDWebImageOperation? {
    let subscriber = Subscription(options: options, context: context, completion: completed)
    state.async {
      guard !subscriber.isTerminal else { return }
      guard let url, let descriptor = OneKeyBlockieDescriptor(url: url) else {
        subscriber.finish(image: nil, data: nil, error: URLError(.badURL))
        return
      }
      let key = descriptor.cacheKey
      let flight = self.flights[key] ?? Flight(descriptor: descriptor, url: url)
      self.flights[key] = flight
      flight.subscriptions[subscriber.id] = subscriber
      subscriber.onCancel { [weak self, weak flight] in
        guard let self, let flight else { return }
        self.state.async {
          guard self.flights[key] === flight else { return }
          flight.subscriptions.removeValue(forKey: subscriber.id)
          if flight.subscriptions.isEmpty {
            self.flights.removeValue(forKey: key)
            flight.operation?.cancel()
          }
        }
      }
      guard flight.operation == nil else { return }
      let operation = BlockOperation { [weak self, weak flight] in
        guard let self, let flight else { return }
        self.load(flight)
      }
      flight.operation = operation
      self.workers.addOperation(operation)
    }
    return subscriber
  }

  private func load(_ flight: Flight) {
    let key = flight.descriptor.cacheKey
    let cancelled = { flight.operation?.isCancelled != false }
    guard !cancelled() else { return }
    let queryTypes = state.sync { cacheTypes(flight, field: .originalQueryCacheType) }
    // Query again inside the merged worker to close the manager-cache-query vs
    // completed previous flight race. These disk operations never run on UI.
    var data: Data?
    if queryTypes.memory, let image = Self.cache.imageFromMemoryCache(forKey: key) {
      data = image.pngData()
    }
    if data == nil, queryTypes.disk { data = Self.cache.diskImageData(forKey: key) }
    var original = data.flatMap { UIImage(data: $0) }
    // A damaged persistent entry is a local cache miss, not a permanent avatar failure.
    if original == nil {
      data = OneKeyBlockie.png(seed: flight.descriptor.seed, isCancelled: cancelled)
      original = data.flatMap { UIImage(data: $0) }
    }
    guard !cancelled() else { return }
    guard let data, let original else {
      finish(flight, data: nil, error: URLError(.cannotDecodeContentData)); return
    }
    // Synchronous store on this worker seals the cache-fill window before any
    // subscriber (or a new isolated manager) can observe a completed flight.
    var storedMemory = false, storedDisk = false
    while !cancelled() {
      var subscribers: [Subscription]?
      let storeTypes = state.sync { () -> (memory: Bool, disk: Bool) in
        guard flights[key] === flight else { subscribers = []; return (false, false) }
        let required = cacheTypes(flight, field: .originalStoreCacheType)
        if (!required.memory || storedMemory) && (!required.disk || storedDisk) {
          flights.removeValue(forKey: key)
          subscribers = Array(flight.subscriptions.values)
        }
        return required
      }
      if let subscribers { deliver(subscribers, flight: flight, data: data, error: nil); return }
      if storeTypes.memory && !storedMemory {
        Self.cache.storeImage(toMemory: original, forKey: key)
        storedMemory = true
      }
      if storeTypes.disk && !storedDisk {
        Self.cache.storeImageData(toDisk: data, forKey: key)
        storedDisk = true
        state.async {
          self.diskWrites += 1
          // SDWebImage also cleans on background/termination; bound active-session
          // growth without scanning the directory after every small PNG write.
          if self.diskWrites % 128 == 0 { Self.cache.deleteOldFiles(completionBlock: nil) }
        }
      }
    }
  }

  private func cacheTypes(_ flight: Flight, field: SDWebImageContextOption) -> (memory: Bool, disk: Bool) {
    var memory = false, disk = false
    for subscriber in flight.subscriptions.values where !subscriber.isTerminal {
      let raw = (subscriber.context?[field] as? NSNumber)?.intValue ?? SDImageCacheType.all.rawValue
      memory = memory || raw == SDImageCacheType.memory.rawValue || raw == SDImageCacheType.all.rawValue
      disk = disk || raw == SDImageCacheType.disk.rawValue || raw == SDImageCacheType.all.rawValue
    }
    return (memory, disk)
  }

  private func finish(_ flight: Flight, data: Data?, error: Error?) {
    let subscribers: [Subscription] = state.sync {
      guard flights[flight.descriptor.cacheKey] === flight else { return [] }
      flights.removeValue(forKey: flight.descriptor.cacheKey)
      return Array(flight.subscriptions.values)
    }
    deliver(subscribers, flight: flight, data: data, error: error)
  }

  private func deliver(_ subscribers: [Subscription], flight: Flight, data: Data?, error: Error?) {
    for subscriber in subscribers where !subscriber.isTerminal {
      let image: UIImage? = data.flatMap {
        SDWebImage.SDImageLoaderDecodeImageData($0, flight.url,
          .init(rawValue: subscriber.options.rawValue), subscriber.context)
      }
      subscriber.finish(image: image, data: data,
        error: error ?? (image == nil ? URLError(.cannotDecodeContentData) : nil))
    }
  }
}
