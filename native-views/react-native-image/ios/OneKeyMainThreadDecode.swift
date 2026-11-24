import Foundation
import os.signpost
import QuartzCore
import SDWebImage
import UIKit

/// Lets the UI thread finish a memory miss itself when the image is small and
/// already on this device, so it binds in the mount frame instead of one frame
/// later. Everything else keeps the asynchronous pipeline.
///
/// "Small" is bounded on every input that drives decode cost: the encoded bytes,
/// the source dimensions (a tiny file can still describe a huge canvas) and the
/// target size. A per-frame time budget is the hard limit on top of those
/// filters: once a frame has spent it, later misses in that frame go async.
enum OneKeyMainThreadDecode {
  static let maximumEncodedBytes = 32 * 1024
  static let maximumSourceEdge = 512
  static let maximumTargetEdge: CGFloat = 256
  static let maximumIconEdge = 192

  struct Metrics: Equatable {
    var attempts: Int = 0
    var hits: Int = 0
    var misses: Int = 0
    var budgetRejections: Int = 0
    var lockContentionMisses: Int = 0
    var diskMisses: Int = 0
    var limitRejections: Int = 0
    var decodeFailures: Int = 0
    var windows: Int = 0
    var maxWindowAllowed: Int = 0
    var maxWindowRejected: Int = 0
    var totalDurationMs: Double = 0
    var maxDurationMs: Double = 0
    var durationsMs: [Double] = []

    var hitRate: Double {
      attempts > 0 ? Double(hits) / Double(attempts) : 0
    }

    func percentileMs(_ p: Double) -> Double {
      guard !durationsMs.isEmpty else { return 0 }
      let sorted = durationsMs.sorted()
      let index = min(max(Int(ceil(p * Double(sorted.count))) - 1, 0), sorted.count - 1)
      return sorted[index]
    }
  }

  private static let frameBudget: CFTimeInterval = 0.004
  private static let frameWindow: CFTimeInterval = 1.0 / 60.0
  private static let maxRecordedSamples = 256
  private static let signpostLog = OSLog(subsystem: "so.onekey.image", category: "SyncDecode")
  private static let lock = NSLock()
  private static var windowStart: CFTimeInterval = 0
  private static var spent: CFTimeInterval = 0
  private static var currentWindowAllowed = 0
  private static var currentWindowRejected = 0
  private static var metrics = Metrics()
  private static var summaryScheduled = false
  // Launch with `-OneKeyImageSyncDecodeDisabled YES` to compare against the async path.
  private static let enabled = !UserDefaults.standard.bool(forKey: "OneKeyImageSyncDecodeDisabled")

  /// Main thread only. Runs `work` while the current frame has budget left and
  /// charges its duration, including the time of a miss.
  static func run<T>(_ work: () -> T?) -> T? {
    guard enabled, Thread.isMainThread else { return nil }
    let start = CACurrentMediaTime()
    lock.lock()
    if start - windowStart > frameWindow {
      windowStart = start
      spent = 0
      currentWindowAllowed = 0
      currentWindowRejected = 0
      metrics.windows += 1
    }
    if !summaryScheduled {
      summaryScheduled = true
      DispatchQueue.main.asyncAfter(deadline: .now() + 3.5) {
        logSummary()
      }
    }
    if spent >= frameBudget {
      metrics.budgetRejections += 1
      currentWindowRejected += 1
      if currentWindowRejected > metrics.maxWindowRejected {
        metrics.maxWindowRejected = currentWindowRejected
      }
      lock.unlock()
      return nil
    }
    currentWindowAllowed += 1
    if currentWindowAllowed > metrics.maxWindowAllowed {
      metrics.maxWindowAllowed = currentWindowAllowed
    }
    lock.unlock()

    let signpostID = OSSignpostID(log: signpostLog)
    os_signpost(.begin, log: signpostLog, name: "MainThreadDecode", signpostID: signpostID)
    let result = work()
    let elapsed = CACurrentMediaTime() - start
    let elapsedMs = elapsed * 1000.0
    os_signpost(
      .end,
      log: signpostLog,
      name: "MainThreadDecode",
      signpostID: signpostID,
      "hit=%{public}d ms=%.3f",
      result != nil ? 1 : 0,
      elapsedMs
    )

    lock.lock()
    spent += elapsed
    metrics.attempts += 1
    if result != nil {
      metrics.hits += 1
    } else {
      metrics.misses += 1
    }
    metrics.totalDurationMs += elapsedMs
    if elapsedMs > metrics.maxDurationMs {
      metrics.maxDurationMs = elapsedMs
    }
    if metrics.durationsMs.count < maxRecordedSamples {
      metrics.durationsMs.append(elapsedMs)
    }
    lock.unlock()
    return result
  }

  static func noteLockContention() {
    lock.lock()
    metrics.lockContentionMisses += 1
    lock.unlock()
  }

  static func noteDiskMiss() {
    lock.lock()
    metrics.diskMisses += 1
    lock.unlock()
  }

  static func noteLimitRejection() {
    lock.lock()
    metrics.limitRejections += 1
    lock.unlock()
  }

  static func noteDecodeFailure() {
    lock.lock()
    metrics.decodeFailures += 1
    lock.unlock()
  }

  static func metricsSnapshot() -> Metrics {
    lock.lock()
    defer { lock.unlock() }
    return metrics
  }

  static func resetForTesting() {
    lock.lock()
    windowStart = 0
    spent = 0
    currentWindowAllowed = 0
    currentWindowRejected = 0
    metrics = Metrics()
    summaryScheduled = false
    lock.unlock()
  }

  static func formatSummary() -> String {
    let snapshot = metricsSnapshot()
    let p50 = snapshot.percentileMs(0.50)
    let p90 = snapshot.percentileMs(0.90)
    return String(
      format:
        "syncDecode attempts=%d hits=%d misses=%d hitRate=%.2f budgetRejected=%d lockContended=%d diskMiss=%d limitRejected=%d decodeFailed=%d windows=%d maxWindowAllowed=%d maxWindowRejected=%d totalMs=%.3f p50Ms=%.3f p90Ms=%.3f maxMs=%.3f",
      snapshot.attempts,
      snapshot.hits,
      snapshot.misses,
      snapshot.hitRate,
      snapshot.budgetRejections,
      snapshot.lockContentionMisses,
      snapshot.diskMisses,
      snapshot.limitRejections,
      snapshot.decodeFailures,
      snapshot.windows,
      snapshot.maxWindowAllowed,
      snapshot.maxWindowRejected,
      snapshot.totalDurationMs,
      p50,
      p90,
      snapshot.maxDurationMs
    )
  }

  static func logSummary() {
    NSLog("[OneKeyImageStartupMetrics] %@", formatSummary())
  }

  /// Decodes a small cached image into the memory cache under the exact key the
  /// view's memory probe uses. Never touches the network.
  static func promoteToMemory(url: URL, rawURL: URL, thumbnailPixelSize: CGSize) -> Bool {
    let manager = OneKeyImagePipeline.manager
    let context = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: thumbnailPixelSize,
      safetyTracker: nil,
      manager: manager,
      url: url
    )
    let cache = context[.imageCache] as? SDImageCache ?? SDImageCache.shared
    guard let key = manager.cacheKey(for: url, context: context) else {
      return false
    }
    guard let data = encodedData(url: url, key: key, cache: cache, manager: manager) else {
      return false
    }
    let metadata = OneKeyImageSafetyPolicy.metadata(from: data)
    guard
      OneKeyImageSafetyPolicy.violation(encodedByteCount: data.count, metadata: metadata) == nil,
      let metadata,
      !metadata.isAnimated,
      let width = metadata.width, let height = metadata.height,
      max(width, height) <= maximumSourceEdge
    else {
      noteLimitRejection()
      return false
    }
    guard let image = SDImageCacheDecodeImageData(data, key, [], context),
      OneKeyImageSafetyPolicy.violation(for: image) == nil
    else {
      noteDecodeFailure()
      return false
    }
    cache.storeImage(toMemory: image, forKey: key)
    OneKeyImageMemoryVariantRegistry.shared.record(
      OneKeyImageMemoryVariant(requestURL: url, thumbnailPixelSize: thumbnailPixelSize),
      for: OneKeyImageMemoryFamilyKey(rawURL: rawURL, headersJson: nil)
    )
    return true
  }

  /// The encoded bytes when they are local and within the size limit. The size is
  /// checked before the bytes are read so a large file costs one stat call.
  private static func encodedData(url: URL, key: String, cache: SDImageCache,
                                  manager: SDWebImageManager) -> Data? {
    switch url.scheme?.lowercased() {
    case "data":
      let source = url.absoluteString
      guard let comma = source.firstIndex(of: ","),
            source[..<comma].range(of: ";base64", options: .caseInsensitive) != nil else {
        noteDiskMiss()
        return nil
      }
      let payload = source[source.index(after: comma)...]
      guard payload.utf8.count <= (maximumEncodedBytes + 2) / 3 * 4 else {
        noteLimitRejection()
        return nil
      }
      guard let decoded = Data(base64Encoded: String(payload), options: .ignoreUnknownCharacters),
            !decoded.isEmpty,
            decoded.count <= maximumEncodedBytes else {
        noteLimitRejection()
        return nil
      }
      return decoded
    case "file":
      return smallFile(atPath: url.path)
    default:
      // Disk holds the source under the un-thumbnailed key; probe it before the
      // thumbnailed key to avoid an extra failed stat syscall on disk hits.
      let originalContext = OneKeyImageRequestContext.make(
        headersJson: nil,
        cachePolicy: .memoryDisk,
        thumbnailPixelSize: nil,
        safetyTracker: nil,
        manager: manager,
        url: url
      )
      let originalKey = manager.cacheKey(for: url, context: originalContext)
      let keys = originalKey == key ? [key] : [originalKey, key].compactMap { $0 }
      var sawOversizedFile = false
      for candidate in keys {
        guard let path = cache.cachePath(forKey: candidate) else { continue }
        switch inspectSmallFile(atPath: path) {
        case .data(let data):
          return data
        case .oversized:
          sawOversizedFile = true
        case .missing:
          continue
        }
      }
      if sawOversizedFile {
        noteLimitRejection()
      } else {
        noteDiskMiss()
      }
      return nil
    }
  }

  private enum FileProbeResult {
    case data(Data)
    case oversized
    case missing
  }

  private static func inspectSmallFile(atPath path: String) -> FileProbeResult {
    guard let size = (try? FileManager.default.attributesOfItem(atPath: path))?[.size] as? Int,
          size > 0 else {
      return .missing
    }
    guard size <= maximumEncodedBytes else {
      return .oversized
    }
    guard let data = try? Data(contentsOf: URL(fileURLWithPath: path)) else {
      return .missing
    }
    return .data(data)
  }

  private static func smallFile(atPath path: String) -> Data? {
    switch inspectSmallFile(atPath: path) {
    case .data(let data):
      return data
    case .oversized:
      noteLimitRejection()
      return nil
    case .missing:
      noteDiskMiss()
      return nil
    }
  }
}
