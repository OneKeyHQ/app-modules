import CryptoKit
import Foundation
import QuartzCore
import SDWebImage
import UIKit

/// Refills the decoded-image memory cache for the first screen of a cold process.
///
/// `HybridOneKeyImage` binds a memory-cached image in the frame it mounts, but a
/// new process starts with an empty memory cache, so first-screen images that are
/// already on disk still paint one frame late. First-screen requests are recorded
/// as hashed cache identities and replayed from the disk cache, off the main
/// thread, at the next process start. The replay never touches the network and no
/// plaintext URL, wallet address, or inline base64 payload is written to disk.
final class OneKeyImageStartupPrewarm {
  static let shared = OneKeyImageStartupPrewarm()
  private init() {}

  struct Identity: Hashable {
    let keyHash: String
    let diskFile: String
    let thumbDiskFile: String?
    let namespace: String?
    let width: Double
    let height: Double
  }

  struct Entry: Codable, Equatable {
    let keyHash: String
    let diskFile: String
    let thumbDiskFile: String?
    let namespace: String?
    let width: Double
    let height: Double
    var launch: Int

    var identity: Identity {
      Identity(
        keyHash: keyHash,
        diskFile: diskFile,
        thumbDiskFile: thumbDiskFile,
        namespace: namespace,
        width: width,
        height: height
      )
    }

    private enum CodingKeys: String, CodingKey {
      case keyHash
      case diskFile
      case thumbDiskFile
      case namespace
      case width
      case height
      case launch
      // Legacy v1 keys accepted on read only for seamless migration.
      case url
    }

    init(
      keyHash: String,
      diskFile: String,
      thumbDiskFile: String?,
      namespace: String?,
      width: Double,
      height: Double,
      launch: Int = 0
    ) {
      self.keyHash = keyHash
      self.diskFile = diskFile
      self.thumbDiskFile = thumbDiskFile == diskFile ? nil : thumbDiskFile
      self.namespace = namespace
      self.width = width
      self.height = height
      self.launch = launch
    }

    init(from decoder: Decoder) throws {
      let container = try decoder.container(keyedBy: CodingKeys.self)
      let width = try container.decode(Double.self, forKey: .width)
      let height = try container.decode(Double.self, forKey: .height)
      let launch = try container.decodeIfPresent(Int.self, forKey: .launch) ?? 0
      if let keyHash = try container.decodeIfPresent(String.self, forKey: .keyHash),
         let diskFile = try container.decodeIfPresent(String.self, forKey: .diskFile) {
        let thumb = try container.decodeIfPresent(String.self, forKey: .thumbDiskFile)
        let namespace = try container.decodeIfPresent(String.self, forKey: .namespace)
        self.init(
          keyHash: keyHash,
          diskFile: diskFile,
          thumbDiskFile: thumb,
          namespace: namespace,
          width: width,
          height: height,
          launch: launch
        )
        return
      }
      // Migrate legacy plaintext entry in memory; next persist writes hashed fields only.
      if let legacyURLString = try container.decodeIfPresent(String.self, forKey: .url),
         let legacyURL = URL(string: legacyURLString),
         let resolved = OneKeyImageStartupPrewarm.resolveIdentity(
           requestURL: legacyURL,
           thumbnailPixelSize: CGSize(width: width, height: height)
         ) {
        self.init(
          keyHash: resolved.keyHash,
          diskFile: resolved.diskFile,
          thumbDiskFile: resolved.thumbDiskFile,
          namespace: resolved.namespace,
          width: width,
          height: height,
          launch: launch
        )
        return
      }
      throw DecodingError.dataCorrupted(
        DecodingError.Context(codingPath: decoder.codingPath, debugDescription: "Invalid startup entry")
      )
    }

    func encode(to encoder: Encoder) throws {
      var container = encoder.container(keyedBy: CodingKeys.self)
      try container.encode(keyHash, forKey: .keyHash)
      try container.encode(diskFile, forKey: .diskFile)
      if let thumbDiskFile, thumbDiskFile != diskFile {
        try container.encode(thumbDiskFile, forKey: .thumbDiskFile)
      }
      if let namespace {
        try container.encode(namespace, forKey: .namespace)
      }
      try container.encode(width, forKey: .width)
      try container.encode(height, forKey: .height)
      if launch > 0 {
        try container.encode(launch, forKey: .launch)
      }
    }
  }

  struct PrewarmMetrics: Equatable {
    var entryCount: Int = 0
    var decodedCount: Int = 0
    var durationMs: Double = 0
  }

  static let window: TimeInterval = 6
  static let maxWindowDuration: TimeInterval = 60
  static let minimumSettledCount = 2
  static let limit = 48
  static let maxLaunchAge = 3
  private static let avatarNamespace = "avatar"
  private static let prewarmKeyPrefix = "onekey-prewarm-v1:"
  private static let defaultFile: URL? = FileManager.default
    .urls(for: .cachesDirectory, in: .userDomainMask).first?
    .appendingPathComponent("onekey-image-startup-v1.json")
  private static let writer = DispatchQueue(label: "onekey.image.startup-prewarm", qos: .utility)

  private let lock = NSLock()
  private var seen: [Entry] = []
  private var previous: [Entry] = []
  private var maxLaunch = 0
  private var loadedPreviousFromDisk = false
  private var diskClearGeneration: UInt64 = 0
  private var recording: Bool?
  private var firstRequestAt: CFTimeInterval = 0
  private var flushWorkItem: DispatchWorkItem?
  private var customFileURL: URL?
  private var metrics = PrewarmMetrics()

  /// Generated avatars are roughly 20 KB as base64; larger inline sources are
  /// not worth persisting a second time.
  private static let maximumInlineSourceLength = 65_536

  private var activeFileURL: URL? {
    customFileURL ?? Self.defaultFile
  }

  static func sha256Hex(_ value: String) -> String {
    SHA256.hash(data: Data(value.utf8)).map { String(format: "%02x", $0) }.joined()
  }

  static func prewarmMemoryKey(forKeyHash keyHash: String) -> String {
    prewarmKeyPrefix + keyHash
  }

  static func resolveIdentity(
    requestURL: URL,
    thumbnailPixelSize: CGSize
  ) -> Identity? {
    let manager = OneKeyImagePipeline.manager
    let context = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: thumbnailPixelSize,
      safetyTracker: nil,
      manager: manager,
      url: requestURL
    )
    let originalContext = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: nil,
      safetyTracker: nil,
      manager: manager,
      url: requestURL
    )
    let cache = context[.imageCache] as? SDImageCache ?? SDImageCache.shared
    guard let key = manager.cacheKey(for: requestURL, context: context),
          let originalKey = manager.cacheKey(for: requestURL, context: originalContext),
          let originalPath = cache.cachePath(forKey: originalKey)
    else {
      return nil
    }
    let diskFile = (originalPath as NSString).lastPathComponent
    guard !diskFile.isEmpty, !diskFile.contains("/"), !diskFile.contains("..") else {
      return nil
    }
    let thumbDiskFile: String? = {
      guard originalKey != key, let thumbPath = cache.cachePath(forKey: key) else { return nil }
      let name = (thumbPath as NSString).lastPathComponent
      guard !name.isEmpty, name != diskFile, !name.contains("/"), !name.contains("..") else {
        return nil
      }
      return name
    }()
    let isAvatar = requestURL.scheme?.lowercased() == "onekey-avatar"
    return Identity(
      keyHash: sha256Hex(key),
      diskFile: diskFile,
      thumbDiskFile: thumbDiskFile,
      namespace: isAvatar ? avatarNamespace : nil,
      width: Double(thumbnailPixelSize.width),
      height: Double(thumbnailPixelSize.height)
    )
  }

  /// Claims a prewarmed image from `cache` for `key` and promotes it under the
  /// exact runtime `key` so subsequent probes hit `imageFromMemoryCache(forKey:)` directly.
  func claimPrewarmedImage(forKey key: String, cache: SDImageCache) -> UIImage? {
    let prewarmKey = Self.prewarmMemoryKey(forKeyHash: Self.sha256Hex(key))
    guard let image = cache.imageFromMemoryCache(forKey: prewarmKey) else {
      return nil
    }
    cache.storeImage(toMemory: image, forKey: key)
    cache.removeImageFromMemory(forKey: prewarmKey)
    return image
  }

  /// Records a rendered first-screen request (both memory hits and disk/network
  /// completions). Requests with custom headers use isolated managers and are not replayed.
  func note(requestURL: URL, rawURL: URL, headersJson: String?,
            cachePolicy: OneKeyImageCachePolicy?, thumbnailPixelSize: CGSize?) {
    guard let size = thumbnailPixelSize, size.width >= 1, size.height >= 1,
          cachePolicy == nil || cachePolicy == .memoryDisk,
          OneKeyImageRequestContext.headers(from: headersJson) == nil,
          let scheme = requestURL.scheme?.lowercased(),
          scheme == "https" || scheme == "http" || scheme == "onekey-avatar"
            || scheme == "file"
            || (scheme == "data"
              && requestURL.absoluteString.utf8.count <= Self.maximumInlineSourceLength),
          let identity = Self.resolveIdentity(requestURL: requestURL, thumbnailPixelSize: size)
    else { return }

    let needsLocalDiskSeed = scheme == "file" || scheme == "data"
    let capturedURL = requestURL

    lock.lock()
    if recording == false {
      lock.unlock()
      return
    }
    let now = CACurrentMediaTime()
    if recording == nil {
      recording = true
      firstRequestAt = now
    }
    let alreadySeen = seen.contains { $0.identity == identity }
    let currentGeneration = diskClearGeneration
    var shouldSchedule = false
    if !alreadySeen, seen.count < Self.limit {
      seen.append(
        Entry(
          keyHash: identity.keyHash,
          diskFile: identity.diskFile,
          thumbDiskFile: identity.thumbDiskFile,
          namespace: identity.namespace,
          width: identity.width,
          height: identity.height,
          launch: 0
        )
      )
      shouldSchedule = true
    }
    if shouldSchedule {
      flushWorkItem?.cancel()
      let item = DispatchWorkItem { [weak self] in
        self?.persist(forceClose: false, expectedGeneration: currentGeneration)
      }
      flushWorkItem = item
      Self.writer.asyncAfter(deadline: .now() + Self.window, execute: item)
    }
    lock.unlock()

    if needsLocalDiskSeed, !alreadySeen {
      Self.writer.async { [weak self] in
        self?.seedLocalSourceToDiskIfNeeded(url: capturedURL, expectedGeneration: currentGeneration)
      }
    }
  }

  private func isCurrentGeneration(_ expectedGeneration: UInt64) -> Bool {
    lock.lock()
    defer { lock.unlock() }
    return diskClearGeneration == expectedGeneration
  }

  private func seedLocalSourceToDiskIfNeeded(url: URL, expectedGeneration: UInt64) {
    guard isCurrentGeneration(expectedGeneration) else { return }
    let manager = OneKeyImagePipeline.manager
    let originalContext = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: nil,
      safetyTracker: nil,
      manager: manager,
      url: url
    )
    let cache = originalContext[.imageCache] as? SDImageCache ?? SDImageCache.shared
    guard let originalKey = manager.cacheKey(for: url, context: originalContext),
          let path = cache.cachePath(forKey: originalKey),
          !FileManager.default.fileExists(atPath: path)
    else { return }
    let data: Data?
    switch url.scheme?.lowercased() {
    case "data":
      let source = url.absoluteString
      guard let comma = source.firstIndex(of: ","),
            source[..<comma].range(of: ";base64", options: .caseInsensitive) != nil
      else { return }
      let payload = source[source.index(after: comma)...]
      guard payload.utf8.count <= Self.maximumInlineSourceLength else { return }
      data = Data(base64Encoded: String(payload), options: .ignoreUnknownCharacters)
    case "file":
      guard let size = (try? FileManager.default.attributesOfItem(atPath: url.path))?[.size] as? Int,
            size > 0, size <= OneKeyImageSafetyPolicy.maximumEncodedBytes
      else { return }
      data = try? Data(contentsOf: url)
    default:
      data = nil
    }
    guard let data, !data.isEmpty,
          OneKeyImageSafetyPolicy.violation(for: data) == nil,
          isCurrentGeneration(expectedGeneration)
    else { return }
    cache.storeImageData(toDisk: data, forKey: originalKey)
  }

  func clearDiskCache() {
    lock.lock()
    diskClearGeneration &+= 1
    flushWorkItem?.cancel()
    flushWorkItem = nil
    seen = []
    previous = []
    maxLaunch = 0
    loadedPreviousFromDisk = false
    let file = activeFileURL
    lock.unlock()
    Self.writer.sync {
      if let file {
        try? FileManager.default.removeItem(at: file)
      }
    }
  }

  private func persist(forceClose: Bool, expectedGeneration: UInt64? = nil) {
    lock.lock()
    if let expectedGeneration, diskClearGeneration != expectedGeneration {
      lock.unlock()
      return
    }
    guard recording == true else {
      lock.unlock()
      return
    }
    let generationAtStart = diskClearGeneration
    let file = activeFileURL
    if !loadedPreviousFromDisk {
      loadedPreviousFromDisk = true
      if previous.isEmpty,
         let file,
         let data = try? Data(contentsOf: file),
         let loaded = try? JSONDecoder().decode([Entry].self, from: data) {
        previous = loaded
        maxLaunch = max(maxLaunch, loaded.map(\.launch).max() ?? 0)
      }
    }
    let elapsed = CACurrentMediaTime() - firstRequestAt
    let shouldClose =
      forceClose
      || seen.count >= Self.minimumSettledCount
      || seen.count >= Self.limit
      || elapsed >= Self.maxWindowDuration

    let seenIdentities = Set(seen.map(\.identity))
    let previousIdentities = previous.map(\.identity)
    let currentIdentities = seen.map(\.identity)
    let allPreviousAtMaxLaunch = previous.allSatisfy { $0.launch == maxLaunch }
    let steadyStateUnchanged =
      !previous.isEmpty && currentIdentities == previousIdentities && allPreviousAtMaxLaunch

    let nextLaunch = steadyStateUnchanged ? maxLaunch : (maxLaunch + 1)
    let currentStamped = seen.map { entry -> Entry in
      var copy = entry
      copy.launch = nextLaunch
      return copy
    }
    let retainedPrevious = previous.filter { entry in
      !seenIdentities.contains(entry.identity) && (nextLaunch - entry.launch) < Self.maxLaunchAge
    }
    let merged = Array((currentStamped + retainedPrevious).prefix(Self.limit))

    if shouldClose {
      recording = false
      flushWorkItem = nil
      seen = []
      previous = merged
      maxLaunch = nextLaunch
    }
    lock.unlock()

    guard !steadyStateUnchanged,
          isCurrentGeneration(generationAtStart),
          let file,
          let data = try? JSONEncoder().encode(merged) else {
      return
    }
    try? data.write(to: file, options: .atomic)
  }

  /// Runs on a background queue at process start.
  func prewarm() {
    let start = CACurrentMediaTime()
    lock.lock()
    let file = activeFileURL
    lock.unlock()
    guard let file, let data = try? Data(contentsOf: file),
          let entries = try? JSONDecoder().decode([Entry].self, from: data) else { return }
    let highestLaunch = entries.map(\.launch).max() ?? 0
    lock.lock()
    loadedPreviousFromDisk = true
    if previous.isEmpty {
      previous = entries
      maxLaunch = max(maxLaunch, highestLaunch)
    }
    lock.unlock()

    var decodedCount = 0
    let boundedEntries = entries.prefix(Self.limit)
    for entry in boundedEntries {
      autoreleasepool {
        guard entry.width >= 1, entry.height >= 1,
              !entry.diskFile.contains("/"), !entry.diskFile.contains("..")
        else { return }
        let cache =
          entry.namespace == Self.avatarNamespace
          ? OneKeyAvatarImageLoader.cache
          : SDImageCache.shared
        let baseDir = cache.diskCachePath
        let candidates = [entry.diskFile, entry.thumbDiskFile].compactMap { $0 }
        var encoded: Data?
        for fileName in candidates {
          guard !fileName.contains("/"), !fileName.contains("..") else { continue }
          let fullPath = (baseDir as NSString).appendingPathComponent(fileName)
          if let size = (try? FileManager.default.attributesOfItem(atPath: fullPath))?[.size] as? Int,
             size > 0, size <= OneKeyImageSafetyPolicy.maximumEncodedBytes,
             let fileData = try? Data(contentsOf: URL(fileURLWithPath: fullPath)) {
            encoded = fileData
            break
          }
        }
        guard let encoded else { return }
        let metadata = OneKeyImageSafetyPolicy.metadata(from: encoded)
        guard
          OneKeyImageSafetyPolicy.violation(encodedByteCount: encoded.count, metadata: metadata) == nil
        else { return }
        let size = CGSize(width: entry.width, height: entry.height)
        let context = OneKeyImageRequestContext.make(
          headersJson: nil,
          cachePolicy: .memoryDisk,
          thumbnailPixelSize: size,
          safetyTracker: nil,
          manager: OneKeyImagePipeline.manager,
          url: nil
        )
        guard let image = SDImageCacheDecodeImageData(encoded, entry.diskFile, [], context),
              OneKeyImageSafetyPolicy.violation(for: image) == nil
        else { return }
        cache.storeImage(
          toMemory: image,
          forKey: Self.prewarmMemoryKey(forKeyHash: entry.keyHash)
        )
        decodedCount += 1
      }
    }
    let durationMs = (CACurrentMediaTime() - start) * 1000.0
    lock.lock()
    metrics = PrewarmMetrics(
      entryCount: boundedEntries.count,
      decodedCount: decodedCount,
      durationMs: durationMs
    )
    lock.unlock()
    NSLog(
      "[OneKeyImageStartupMetrics] imagePrewarm entries=%d decoded=%d totalMs=%.3f",
      boundedEntries.count,
      decodedCount,
      durationMs
    )
  }

  func metricsSnapshot() -> PrewarmMetrics {
    lock.lock()
    defer { lock.unlock() }
    return metrics
  }

  func flushForTesting(forceClose: Bool = true) {
    Self.writer.sync {
      self.lock.lock()
      self.flushWorkItem?.cancel()
      self.flushWorkItem = nil
      self.lock.unlock()
      self.persist(forceClose: forceClose)
    }
  }

  func resetForTesting(fileURL: URL? = nil, keepPreviousFromDisk: Bool = false) {
    Self.writer.sync {
      self.lock.lock()
      self.flushWorkItem?.cancel()
      self.flushWorkItem = nil
      self.seen = []
      if !keepPreviousFromDisk {
        self.previous = []
        self.maxLaunch = 0
        self.loadedPreviousFromDisk = false
      }
      self.recording = nil
      self.firstRequestAt = 0
      self.customFileURL = fileURL
      self.metrics = PrewarmMetrics()
      self.lock.unlock()
    }
  }
}

@objc(OneKeyImageStartupPrewarmBridge)
internal final class OneKeyImageStartupPrewarmBridge: NSObject {
  /// Called off the main thread at process start; see `OneKeyNamedSVGStartupLoader`.
  @objc(prewarmStartupSet)
  static func prewarmStartupSet() {
    OneKeyImageStartupPrewarm.shared.prewarm()
  }
}
