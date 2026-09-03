import Foundation
import NitroModules
import SDWebImage
import UIKit

private enum OneKeyImageLoadResult {
  case success
  case failure
  case safetyFailure
}

private final class OneKeyImageOperationBox: @unchecked Sendable {
  private let lock = NSLock()
  private var operation: SDWebImageOperation?
  private var cancellationRequested = false
  private var completionClaimed = false

  func setOperation(_ operation: SDWebImageOperation?) {
    lock.lock()
    if cancellationRequested {
      lock.unlock()
      operation?.cancel()
      return
    }
    self.operation = operation
    lock.unlock()
  }

  func cancel() {
    lock.lock()
    cancellationRequested = true
    let operation = operation
    lock.unlock()
    operation?.cancel()
  }

  func claimCompletion() -> Bool {
    lock.lock()
    defer { lock.unlock() }
    guard !completionClaimed else { return false }
    completionClaimed = true
    return true
  }
}

final class HybridOneKeyImageCache: HybridOneKeyImageCacheSpec {
  static let maximumConcurrentPreloads = 4

  func preload(sources: [OneKeyImagePreloadSource]) throws -> Promise<Bool> {
    Promise.async {
      let defaultScale = await MainActor.run { UIScreen.main.scale }
      return await withTaskGroup(of: Bool.self, returning: Bool.self) { group in
        var iterator = sources.makeIterator()
        for _ in 0..<min(Self.maximumConcurrentPreloads, sources.count) {
          guard let source = iterator.next() else { break }
          group.addTask {
            await Self.preload(source: source, defaultScale: defaultScale)
          }
        }
        var allSucceeded = true
        while let succeeded = await group.next() {
          allSucceeded = succeeded && allSucceeded
          if let source = iterator.next() {
            group.addTask {
              await Self.preload(source: source, defaultScale: defaultScale)
            }
          }
        }
        return allSucceeded
      }
    }
  }

  func clearMemory() throws -> Promise<Void> {
    Promise.async {
      SDImageCache.shared.clearMemory()
    }
  }

  func clearDisk() throws -> Promise<Void> {
    Promise.async {
      await withCheckedContinuation { continuation in
        SDImageCache.shared.clearDisk { continuation.resume() }
      }
    }
  }

  func clearAll() throws -> Promise<Void> {
    SDImageCache.shared.clearMemory()
    return try clearDisk()
  }

  private static func preload(
    source: OneKeyImagePreloadSource,
    defaultScale: CGFloat
  ) async -> Bool {
    guard OneKeyImageSafetyPolicy.preflight(source: source.uri) == nil else { return false }
    guard let rawURL = validURL(from: source.uri) else { return false }
    guard OneKeyImageSafetyPolicy.preflight(url: rawURL) == nil else { return false }
    let logicalViewSize = OneKeyImageDecodeSizing.logicalViewSize(
      width: source.resizeWidth,
      height: source.resizeHeight
    )
    let requestedScale = CGFloat(source.pixelRatio ?? Double(defaultScale))
    let scale = requestedScale.isFinite ? min(max(requestedScale, 1), 3) : 1
    let thumbnailPixelSize =
      logicalViewSize.flatMap {
        OneKeyImageDecodeSizing.thumbnailPixelSize(
          viewSize: $0,
          scale: scale,
          contentFit: .cover
        )
      } ?? OneKeyImageDecodeSizing.defaultPreloadPixelSize
    let hasCustomIdentity = OneKeyImageRequestContext.headers(from: source.headersJson) != nil
    let requestURL: URL
    if source.optimizeTos != false, let logicalViewSize {
      requestURL = OneKeyTosURL.optimized(
        rawURL: rawURL,
        displaySize: max(logicalViewSize.width, logicalViewSize.height),
        scale: scale,
        overscan: source.overscan ?? 1.1,
        hasCustomIdentity: hasCustomIdentity
      )
    } else {
      requestURL = rawURL
    }
    let result = await loadAttempt(
      url: requestURL,
      source: source,
      thumbnailPixelSize: thumbnailPixelSize
    )
    if case .failure = result, requestURL != rawURL {
      return await loadAttempt(
        url: rawURL,
        source: source,
        thumbnailPixelSize: thumbnailPixelSize
      ) == .success
    }
    return result == .success
  }

  private static func loadAttempt(
    url: URL,
    source: OneKeyImagePreloadSource,
    thumbnailPixelSize: CGSize
  ) async -> OneKeyImageLoadResult {
    let headers = OneKeyImageRequestContext.headers(from: source.headersJson)
    let safetyHandle = OneKeyImageSafetyFlightCoordinator.shared.acquire(
      url: url,
      headers: headers
    )
    let context = OneKeyImageRequestContext.make(
      headersJson: source.headersJson,
      cachePolicy: source.cachePolicy ?? .memoryDisk,
      thumbnailPixelSize: thumbnailPixelSize,
      safetyTracker: safetyHandle.tracker,
      manager: safetyHandle.manager
    )
    return await load(url: url, context: context, safetyHandle: safetyHandle)
  }

  private static func load(
    url: URL,
    context: [SDWebImageContextOption: Any],
    safetyHandle: OneKeyImageSafetyFlightHandle
  ) async -> OneKeyImageLoadResult {
    await withCheckedContinuation { continuation in
      let operationBox = OneKeyImageOperationBox()
      let operation = safetyHandle.manager.loadImage(
        with: url,
        options: OneKeyImageRequestContext.preloadOptions,
        context: context,
        progress: { receivedSize, _, _ in
          if safetyHandle.tracker.inspectReceivedByteCount(Int64(receivedSize)) {
            operationBox.cancel()
          }
        }
      ) { image, data, error, _, _, _ in
        guard operationBox.claimCompletion() else { return }
        if let data,
          let violation = OneKeyImageSafetyPolicy.violation(for: data)
        {
          safetyHandle.tracker.record(violation)
        }
        if let image,
          let violation = OneKeyImageSafetyPolicy.violation(for: image)
        {
          safetyHandle.tracker.record(violation)
        }
        let safetyViolation = safetyHandle.tracker.violation
        safetyHandle.finish()
        if safetyViolation != nil {
          continuation.resume(returning: .safetyFailure)
        } else {
          continuation.resume(returning: image != nil && error == nil ? .success : .failure)
        }
      }
      operationBox.setOperation(operation)
    }
  }

  private static func validURL(from value: String) -> URL? {
    guard !value.isEmpty,
      let url = URL(string: value),
      let scheme = url.scheme,
      !scheme.isEmpty
    else {
      return nil
    }
    if scheme == "http" || scheme == "https" {
      guard url.host?.isEmpty == false else { return nil }
    }
    return url
  }
}
