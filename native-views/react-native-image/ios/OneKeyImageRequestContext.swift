import CryptoKit
import Foundation
import ImageIO
import SDWebImage
import SDWebImageSVGCoder
import SDWebImageWebPCoder
import UIKit

enum OneKeyImageSafetyViolation: LocalizedError, Equatable, Sendable {
  case encodedDataTooLarge
  case dataURIPayloadTooLarge
  case staticDimensionsTooLarge
  case animatedDimensionsTooLarge
  case animatedEncodedDataTooLarge
  case animatedFrameCountTooLarge
  case animatedDurationTooLong

  var errorDescription: String? {
    switch self {
    case .encodedDataTooLarge:
      return "Image encoded data exceeds 32 MiB"
    case .dataURIPayloadTooLarge:
      return "Image data URI payload exceeds 8 MiB"
    case .staticDimensionsTooLarge:
      return "Static image dimensions exceed the safety limit"
    case .animatedDimensionsTooLarge:
      return "Animated image dimensions exceed the safety limit"
    case .animatedEncodedDataTooLarge:
      return "Animated image encoded data exceeds 16 MiB"
    case .animatedFrameCountTooLarge:
      return "Animated image frame count exceeds 1000"
    case .animatedDurationTooLong:
      return "Animated image duration exceeds 60 seconds"
    }
  }
}

struct OneKeyImageMetadata: Equatable, Sendable {
  let width: Int?
  let height: Int?
  let frameCount: Int?
  let duration: TimeInterval?

  var isAnimated: Bool { (frameCount ?? 1) > 1 }
}

struct OneKeyImageHeadersIdentity: Hashable, Sendable {
  private struct Field: Hashable, Sendable {
    let name: String
    let value: String
  }

  private let fields: [Field]

  init(headers: [String: String]?) {
    fields = (headers ?? [:])
      .map { Field(name: $0.key.lowercased(), value: $0.value) }
      .sorted {
        if $0.name == $1.name { return $0.value < $1.value }
        return $0.name < $1.name
      }
  }

  var isEmpty: Bool { fields.isEmpty }

  var cacheKeyComponent: String? {
    guard !fields.isEmpty else { return nil }
    let canonicalValue = fields.map {
      "\($0.name.utf8.count):\($0.name)\($0.value.utf8.count):\($0.value)"
    }.joined()
    let digest = SHA256.hash(data: Data(canonicalValue.utf8))
    return digest.map { String(format: "%02x", $0) }.joined()
  }
}

private struct OneKeyImageSafetyFlightIdentity: Hashable, Sendable {
  let url: URL
  let headers: OneKeyImageHeadersIdentity
}

final class OneKeyImageSafetyTracker: @unchecked Sendable {
  private let lock = NSLock()
  private var storedViolation: OneKeyImageSafetyViolation?

  var violation: OneKeyImageSafetyViolation? {
    lock.lock()
    defer { lock.unlock() }
    return storedViolation
  }

  @discardableResult
  func record(_ violation: OneKeyImageSafetyViolation) -> Bool {
    lock.lock()
    defer { lock.unlock() }
    guard storedViolation == nil else { return false }
    storedViolation = violation
    return true
  }

  @discardableResult
  func inspectReceivedByteCount(_ byteCount: Int64) -> Bool {
    guard byteCount > Int64(OneKeyImageSafetyPolicy.maximumEncodedBytes) else { return false }
    record(.encodedDataTooLarge)
    return true
  }
}

final class OneKeyImageSafetyFlightHandle: @unchecked Sendable {
  let tracker: OneKeyImageSafetyTracker
  var manager: SDWebImageManager { managerLease.manager }

  private let coordinator: OneKeyImageSafetyFlightCoordinator
  private let identity: OneKeyImageSafetyFlightIdentity
  private let managerLease: OneKeyImageManagerLease
  private let lock = NSLock()
  private var finished = false

  fileprivate init(
    coordinator: OneKeyImageSafetyFlightCoordinator,
    identity: OneKeyImageSafetyFlightIdentity,
    tracker: OneKeyImageSafetyTracker,
    managerLease: OneKeyImageManagerLease
  ) {
    self.coordinator = coordinator
    self.identity = identity
    self.tracker = tracker
    self.managerLease = managerLease
  }

  func finish() {
    lock.lock()
    guard !finished else {
      lock.unlock()
      return
    }
    finished = true
    lock.unlock()
    coordinator.release(identity: identity, tracker: tracker)
    managerLease.finish()
  }

  deinit {
    finish()
  }
}

final class OneKeyImageSafetyFlightCoordinator: @unchecked Sendable {
  static let shared = OneKeyImageSafetyFlightCoordinator()

  private struct Flight {
    let tracker: OneKeyImageSafetyTracker
    var referenceCount: Int
  }

  private let lock = NSLock()
  private let managerPool: OneKeyImageManagerPool
  private var flights: [OneKeyImageSafetyFlightIdentity: Flight] = [:]

  init(managerPool: OneKeyImageManagerPool = .shared) {
    self.managerPool = managerPool
  }

  func acquire(
    url: URL,
    headers: [String: String]? = nil
  ) -> OneKeyImageSafetyFlightHandle {
    let headersIdentity = OneKeyImageHeadersIdentity(headers: headers)
    let identity = OneKeyImageSafetyFlightIdentity(url: url, headers: headersIdentity)
    let managerLease = managerPool.acquire(identity: headersIdentity)
    lock.lock()
    let tracker: OneKeyImageSafetyTracker
    if var flight = flights[identity] {
      flight.referenceCount += 1
      flights[identity] = flight
      tracker = flight.tracker
    } else {
      tracker = OneKeyImageSafetyTracker()
      flights[identity] = Flight(tracker: tracker, referenceCount: 1)
    }
    lock.unlock()
    return OneKeyImageSafetyFlightHandle(
      coordinator: self,
      identity: identity,
      tracker: tracker,
      managerLease: managerLease
    )
  }

  fileprivate func release(
    identity: OneKeyImageSafetyFlightIdentity,
    tracker: OneKeyImageSafetyTracker
  ) {
    lock.lock()
    defer { lock.unlock() }
    guard var flight = flights[identity], flight.tracker === tracker else { return }
    if flight.referenceCount == 1 {
      flights.removeValue(forKey: identity)
    } else {
      flight.referenceCount -= 1
      flights[identity] = flight
    }
  }

  var activeFlightCount: Int {
    lock.lock()
    defer { lock.unlock() }
    return flights.count
  }
}

enum OneKeyImageSafetyPolicy {
  static let maximumEncodedBytes = 32 * 1024 * 1024
  static let maximumDataURIDecodedBytes = 8 * 1024 * 1024
  static let maximumAnimatedEncodedBytes = 16 * 1024 * 1024
  static let maximumStaticPixelArea = 100_000_000
  static let maximumStaticSide = 32_768
  static let maximumAnimatedPixelArea = 16_000_000
  static let maximumAnimatedSide = 8_192
  static let maximumAnimatedFrameCount = 1_000
  static let maximumAnimatedDuration: TimeInterval = 60
  static let maximumAnimatedBufferBytes: UInt = 16 * 1024 * 1024

  static func preflight(source: String) -> OneKeyImageSafetyViolation? {
    guard source.range(of: "data:", options: [.anchored, .caseInsensitive]) != nil,
      let comma = source.firstIndex(of: ",")
    else {
      return nil
    }
    let header = source[..<comma]
    let payload = source[source.index(after: comma)...]
    let payloadByteCount = payload.utf8.count
    let isBase64 = header.range(of: ";base64", options: .caseInsensitive) != nil
    let decodedByteCount =
      isBase64 ? base64DecodedByteCount(payload) : payloadByteCount
    guard let decodedByteCount else {
      // Invalid base64 is a normal corrupt-data failure. The raw character
      // ceiling still guarantees the decoder cannot allocate over 8 MiB.
      let maximumEncodedLength = ((maximumDataURIDecodedBytes + 2) / 3) * 4
      return payloadByteCount > maximumEncodedLength ? .dataURIPayloadTooLarge : nil
    }
    return decodedByteCount > maximumDataURIDecodedBytes ? .dataURIPayloadTooLarge : nil
  }

  static func preflight(url: URL) -> OneKeyImageSafetyViolation? {
    guard url.isFileURL,
      let values = try? url.resourceValues(forKeys: [.fileSizeKey, .totalFileAllocatedSizeKey])
    else {
      return nil
    }
    let byteCount = max(values.fileSize ?? 0, values.totalFileAllocatedSize ?? 0)
    return byteCount > maximumEncodedBytes ? .encodedDataTooLarge : nil
  }

  static func violation(
    encodedByteCount: Int,
    metadata: OneKeyImageMetadata?
  ) -> OneKeyImageSafetyViolation? {
    guard encodedByteCount <= maximumEncodedBytes else { return .encodedDataTooLarge }
    guard let metadata else { return nil }

    if metadata.isAnimated {
      if encodedByteCount > maximumAnimatedEncodedBytes {
        return .animatedEncodedDataTooLarge
      }
      if let frameCount = metadata.frameCount,
        frameCount > maximumAnimatedFrameCount
      {
        return .animatedFrameCountTooLarge
      }
      if let duration = metadata.duration,
        !duration.isFinite || duration > maximumAnimatedDuration
      {
        return .animatedDurationTooLong
      }

      if let width = metadata.width,
        let height = metadata.height,
        width > 0,
        height > 0,
        width > maximumAnimatedSide || height > maximumAnimatedSide
          || Double(width) * Double(height) > Double(maximumAnimatedPixelArea)
      {
        return .animatedDimensionsTooLarge
      }
    } else if let width = metadata.width,
      let height = metadata.height,
      width > 0,
      height > 0,
      width > maximumStaticSide || height > maximumStaticSide
        || Double(width) * Double(height) > Double(maximumStaticPixelArea)
    {
      return .staticDimensionsTooLarge
    }
    return nil
  }

  static func violation(for data: Data) -> OneKeyImageSafetyViolation? {
    violation(encodedByteCount: data.count, metadata: metadata(from: data))
  }

  static func violation(for image: UIImage) -> OneKeyImageSafetyViolation? {
    let pixelWidth = image.cgImage?.width ?? Int(ceil(image.size.width * image.scale))
    let pixelHeight = image.cgImage?.height ?? Int(ceil(image.size.height * image.scale))
    if let animatedImage = image as? SDAnimatedImage {
      let frameCount = Int(animatedImage.animatedImageFrameCount)
      let encodedByteCount = animatedImage.animatedImageData?.count ?? 0
      var duration: TimeInterval = 0
      if frameCount <= maximumAnimatedFrameCount {
        for index in 0..<frameCount {
          duration += animatedImage.animatedImageDuration(at: UInt(index))
          if duration > maximumAnimatedDuration { break }
        }
      }
      return violation(
        encodedByteCount: encodedByteCount,
        metadata: OneKeyImageMetadata(
          width: pixelWidth,
          height: pixelHeight,
          frameCount: frameCount,
          duration: duration
        )
      )
    }
    let frameCount = image.images?.count
    return violation(
      encodedByteCount: 0,
      metadata: OneKeyImageMetadata(
        width: pixelWidth,
        height: pixelHeight,
        frameCount: frameCount,
        duration: frameCount == nil ? nil : image.duration
      )
    )
  }

  static func metadata(from data: Data) -> OneKeyImageMetadata? {
    guard !data.isEmpty,
      let source = CGImageSourceCreateWithData(
        data as CFData,
        [kCGImageSourceShouldCache: false] as CFDictionary
      ),
      CGImageSourceGetCount(source) > 0,
      let firstProperties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as NSDictionary?
    else {
      return nil
    }
    let sourceProperties = CGImageSourceCopyProperties(source, nil) as NSDictionary?
    var dimensions = maximumDimensions(
      width: (sourceProperties?[kCGImagePropertyPixelWidth] as? NSNumber)?.intValue,
      height: (sourceProperties?[kCGImagePropertyPixelHeight] as? NSNumber)?.intValue,
      candidateWidth: (firstProperties[kCGImagePropertyPixelWidth] as? NSNumber)?.intValue,
      candidateHeight: (firstProperties[kCGImagePropertyPixelHeight] as? NSNumber)?.intValue
    )
    let frameCount = CGImageSourceGetCount(source)
    var duration: TimeInterval?
    if frameCount <= maximumAnimatedFrameCount {
      var total: TimeInterval = 0
      var hasDuration = false
      for index in 0..<frameCount {
        guard
          let properties =
            CGImageSourceCopyPropertiesAtIndex(source, index, nil) as NSDictionary?
        else {
          continue
        }
        dimensions = maximumDimensions(
          width: dimensions.width,
          height: dimensions.height,
          candidateWidth: (properties[kCGImagePropertyPixelWidth] as? NSNumber)?.intValue,
          candidateHeight: (properties[kCGImagePropertyPixelHeight] as? NSNumber)?.intValue
        )
        if frameCount > 1, let frameDuration = frameDuration(from: properties) {
          hasDuration = true
          total += frameDuration
          if !total.isFinite || total > maximumAnimatedDuration { break }
        }
      }
      duration = hasDuration ? total : nil
    }
    return OneKeyImageMetadata(
      width: dimensions.width,
      height: dimensions.height,
      frameCount: frameCount,
      duration: duration
    )
  }

  static func maximumDimensions(
    width: Int?,
    height: Int?,
    candidateWidth: Int?,
    candidateHeight: Int?
  ) -> (width: Int?, height: Int?) {
    (
      [width, candidateWidth].compactMap { $0 }.max(),
      [height, candidateHeight].compactMap { $0 }.max()
    )
  }

  private static func base64DecodedByteCount(_ payload: Substring) -> Int? {
    var characterCount = 0
    var paddingCount = 0
    var sawPadding = false
    for byte in payload.utf8 {
      if byte == 9 || byte == 10 || byte == 13 || byte == 32 { continue }
      if byte == 61 {
        sawPadding = true
        paddingCount += 1
        guard paddingCount <= 2 else { return nil }
      } else {
        guard !sawPadding,
          (byte >= 65 && byte <= 90)
            || (byte >= 97 && byte <= 122)
            || (byte >= 48 && byte <= 57)
            || byte == 43
            || byte == 47
        else {
          return nil
        }
      }
      characterCount += 1
    }
    guard characterCount > 0 else { return 0 }
    let remainder = characterCount % 4
    guard remainder != 1, paddingCount == 0 || remainder == 0 else { return nil }
    let unpaddedBytes =
      (characterCount / 4) * 3
      + (remainder == 2 ? 1 : remainder == 3 ? 2 : 0)
    return unpaddedBytes - paddingCount
  }

  private static func frameDuration(from properties: NSDictionary) -> TimeInterval? {
    if let dictionary = properties[kCGImagePropertyGIFDictionary] as? NSDictionary {
      return duration(
        in: dictionary,
        unclampedKey: kCGImagePropertyGIFUnclampedDelayTime,
        clampedKey: kCGImagePropertyGIFDelayTime
      )
    }
    if let dictionary = properties[kCGImagePropertyPNGDictionary] as? NSDictionary {
      return duration(
        in: dictionary,
        unclampedKey: kCGImagePropertyAPNGUnclampedDelayTime,
        clampedKey: kCGImagePropertyAPNGDelayTime
      )
    }
    if let dictionary = properties[kCGImagePropertyWebPDictionary] as? NSDictionary {
      return duration(
        in: dictionary,
        unclampedKey: kCGImagePropertyWebPUnclampedDelayTime,
        clampedKey: kCGImagePropertyWebPDelayTime
      )
    }
    return nil
  }

  private static func duration(
    in dictionary: NSDictionary,
    unclampedKey: CFString,
    clampedKey: CFString
  ) -> TimeInterval? {
    let unclamped = (dictionary[unclampedKey] as? NSNumber)?.doubleValue
    if let unclamped, !unclamped.isFinite || unclamped > 0 { return unclamped }
    let clamped = (dictionary[clampedKey] as? NSNumber)?.doubleValue
    return clamped.flatMap { !($0.isFinite) || $0 > 0 ? $0 : nil }
  }
}

enum OneKeyImageCoderRegistry {
  private static let svgCoder = SDImageSVGCoder.shared
  private static let webPCoder = SDImageWebPCoder.shared

  static let coder: SDImageCodersManager = {
    // Keep OneKey's decoder set independent from Expo's global registrations.
    // SDImageCodersManager starts with ImageIO, GIF and APNG coders.
    let manager = SDImageCodersManager()
    manager.addCoder(svgCoder)
    manager.addCoder(webPCoder)
    return manager
  }()

  private static let globalRegistration: Void = {
    // SDAnimatedImage resolves its animated coder through the global manager,
    // even when a request-local coder is provided in the SDWebImage context.
    let global = SDImageCodersManager.shared
    let isAlreadyRegistered = (global.coders ?? []).contains {
      ($0 as AnyObject) === webPCoder
    }
    if !isAlreadyRegistered {
      global.addCoder(webPCoder)
    }
  }()

  static func ensureWebPRegistered() {
    _ = globalRegistration
  }
}

enum OneKeyImagePipeline {
  static let downloader = SDWebImageDownloader(config: nil)
  static let manager = SDWebImageManager(cache: SDImageCache.shared, loader: downloader)

  static func makeIsolatedManager() -> SDWebImageManager {
    SDWebImageManager(
      cache: SDImageCache.shared,
      loader: SDWebImageDownloader(config: nil)
    )
  }
}

final class OneKeyImageManagerLease: @unchecked Sendable {
  let manager: SDWebImageManager

  private let pool: OneKeyImageManagerPool?
  private let identity: OneKeyImageHeadersIdentity
  private let lock = NSLock()
  private var finished = false

  fileprivate init(
    manager: SDWebImageManager,
    pool: OneKeyImageManagerPool?,
    identity: OneKeyImageHeadersIdentity
  ) {
    self.manager = manager
    self.pool = pool
    self.identity = identity
  }

  func finish() {
    lock.lock()
    guard !finished else {
      lock.unlock()
      return
    }
    finished = true
    lock.unlock()
    pool?.release(identity: identity, manager: manager)
  }

  deinit {
    finish()
  }
}

final class OneKeyImageManagerPool: @unchecked Sendable {
  static let shared = OneKeyImageManagerPool()

  private struct Entry {
    let manager: SDWebImageManager
    var referenceCount: Int
  }

  private let defaultManager: SDWebImageManager
  private let lock = NSLock()
  private var entries: [OneKeyImageHeadersIdentity: Entry] = [:]

  init(defaultManager: SDWebImageManager = OneKeyImagePipeline.manager) {
    self.defaultManager = defaultManager
  }

  func acquire(identity: OneKeyImageHeadersIdentity) -> OneKeyImageManagerLease {
    guard !identity.isEmpty else {
      return OneKeyImageManagerLease(manager: defaultManager, pool: nil, identity: identity)
    }

    lock.lock()
    let manager: SDWebImageManager
    if var entry = entries[identity] {
      entry.referenceCount += 1
      entries[identity] = entry
      manager = entry.manager
    } else {
      manager = OneKeyImagePipeline.makeIsolatedManager()
      entries[identity] = Entry(manager: manager, referenceCount: 1)
    }
    lock.unlock()
    return OneKeyImageManagerLease(manager: manager, pool: self, identity: identity)
  }

  fileprivate func release(
    identity: OneKeyImageHeadersIdentity,
    manager: SDWebImageManager
  ) {
    lock.lock()
    defer { lock.unlock() }
    guard var entry = entries[identity], entry.manager === manager else { return }
    if entry.referenceCount == 1 {
      entries.removeValue(forKey: identity)
    } else {
      entry.referenceCount -= 1
      entries[identity] = entry
    }
  }

  var activeManagerCount: Int {
    lock.lock()
    defer { lock.unlock() }
    return entries.count
  }
}

enum OneKeyImageDecodeSizing {
  static let maximumDecodedBytes = 16 * 1024 * 1024
  static let bytesPerPixel = 4
  static let maximumPixelArea = CGFloat(maximumDecodedBytes / bytesPerPixel)
  static let defaultPreloadPixelSize = CGSize(width: 2_048, height: 2_048)

  static func logicalViewSize(width: Double?, height: Double?) -> CGSize? {
    let validWidth = width.flatMap { value -> CGFloat? in
      guard value.isFinite, value > 0 else { return nil }
      return CGFloat(value)
    }
    let validHeight = height.flatMap { value -> CGFloat? in
      guard value.isFinite, value > 0 else { return nil }
      return CGFloat(value)
    }
    switch (validWidth, validHeight) {
    case (let width?, let height?): return CGSize(width: width, height: height)
    case (let width?, nil): return CGSize(width: width, height: width)
    case (nil, let height?): return CGSize(width: height, height: height)
    case (nil, nil): return nil
    }
  }

  static func thumbnailPixelSize(
    viewSize: CGSize,
    scale: CGFloat,
    contentFit: OneKeyImageContentFit
  ) -> CGSize? {
    guard viewSize.width.isFinite,
      viewSize.height.isFinite,
      viewSize.width > 0,
      viewSize.height > 0
    else {
      return nil
    }
    let pixelScale = min(max(scale.isFinite ? scale : 1, 1), 3)
    let requestedSize: CGSize
    switch contentFit {
    case .cover:
      // The source aspect ratio is unknown before decode. A square based on the
      // longest edge avoids under-decoding the common cover case.
      let edge = ceil(max(viewSize.width, viewSize.height) * pixelScale)
      requestedSize = CGSize(width: edge, height: edge)
    case .contain, .fill, .center:
      requestedSize = CGSize(
        width: ceil(viewSize.width * pixelScale),
        height: ceil(viewSize.height * pixelScale)
      )
    }
    return cappedToMaximumArea(requestedSize)
  }

  private static func cappedToMaximumArea(_ size: CGSize) -> CGSize {
    let area = size.width * size.height
    guard area > maximumPixelArea else { return size }
    let ratio = sqrt(maximumPixelArea / area)
    return CGSize(
      width: max(floor(size.width * ratio), 1),
      height: max(floor(size.height * ratio), 1)
    )
  }
}

enum OneKeyImageRequestContext {
  static let safetyCacheGeneration = "onekey-image-safety-v2"
  static let renderOptions: SDWebImage.SDWebImageOptions = [.retryFailed]
  static let preloadOptions: SDWebImage.SDWebImageOptions = [
    .retryFailed, .lowPriority,
  ]

  static var baseContext: [SDWebImageContextOption: Any] {
    OneKeyImageCoderRegistry.ensureWebPRegistered()
    return [
      .imageCoder: OneKeyImageCoderRegistry.coder,
      .animatedImageClass: SDAnimatedImage.self,
      .customManager: OneKeyImagePipeline.manager,
    ]
  }

  static func headers(from json: String?) -> [String: String]? {
    guard let json,
      let data = json.data(using: .utf8),
      let value = try? JSONSerialization.jsonObject(with: data) as? [String: Any]
    else {
      return nil
    }
    var headers: [String: String] = [:]
    for (key, rawValue) in value {
      if let string = rawValue as? String { headers[key] = string }
    }
    return headers.isEmpty ? nil : headers
  }

  static func make(
    headersJson: String?,
    cachePolicy: OneKeyImageCachePolicy,
    thumbnailPixelSize: CGSize?,
    safetyTracker: OneKeyImageSafetyTracker?,
    manager: SDWebImageManager
  ) -> [SDWebImageContextOption: Any] {
    var context = baseContext
    context[.customManager] = manager
    if let safetyTracker {
      for (key, value) in transportGuards(tracker: safetyTracker) {
        context[key] = value
      }
    }
    let headers = headers(from: headersJson)
    let headersIdentity = OneKeyImageHeadersIdentity(headers: headers)
    if let headers {
      context[.downloadRequestModifier] = SDWebImageDownloaderRequestModifier(headers: headers)
    }
    context[.cacheKeyFilter] = SDWebImageCacheKeyFilter { url in
      filteredCacheKey(
        for: url,
        headersIdentity: headersIdentity
      )
    }
    if let thumbnailPixelSize {
      for (key, value) in thumbnailOptions(pixelSize: thumbnailPixelSize) {
        context[key] = value
      }
    }

    let cacheType = cacheType(for: cachePolicy)
    context[.queryCacheType] = cacheType.rawValue
    context[.storeCacheType] = cacheType.rawValue
    context[.originalQueryCacheType] = cacheType.rawValue
    context[.originalStoreCacheType] = cacheType.rawValue
    return context
  }

  static func transportGuards(
    tracker: OneKeyImageSafetyTracker
  ) -> [SDWebImageContextOption: Any] {
    [
      .downloadResponseModifier: SDWebImageDownloaderResponseModifier { response in
        guard !tracker.inspectReceivedByteCount(response.expectedContentLength) else {
          return nil
        }
        return response
      },
      .downloadDecryptor: SDWebImageDownloaderDecryptor { data, _ in
        if let violation = OneKeyImageSafetyPolicy.violation(for: data) {
          tracker.record(violation)
          return nil
        }
        return data
      },
    ]
  }

  static func filteredCacheKey(
    for url: URL,
    headersIdentity: OneKeyImageHeadersIdentity = OneKeyImageHeadersIdentity(headers: nil)
  ) -> String {
    var identity = url.absoluteString
    if let headersComponent = headersIdentity.cacheKeyComponent {
      identity += "|headers:\(headersComponent)"
    }
    return "\(identity)|\(safetyCacheGeneration)"
  }

  static func thumbnailOptions(pixelSize: CGSize) -> [SDWebImageContextOption: Any] {
    guard pixelSize.width > 0, pixelSize.height > 0 else { return [:] }
    return [
      .imageThumbnailPixelSize: NSValue(cgSize: pixelSize),
      .imagePreserveAspectRatio: true,
    ]
  }

  static func cacheType(for policy: OneKeyImageCachePolicy) -> SDImageCacheType {
    switch policy {
    case .memoryDisk: return .all
    case .memory: return .memory
    case .disk: return .disk
    case .none: return .none
    }
  }
}
