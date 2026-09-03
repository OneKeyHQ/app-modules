import SDWebImage
import SDWebImageSVGCoder
import SDWebImageWebPCoder
import XCTest

@testable import OneKeyImage

final class OneKeyImageInfrastructureTests: XCTestCase {
  func testRenderAndPreloadDoNotUseSharedCookieStorage() {
    XCTAssertTrue(OneKeyImageRequestContext.renderOptions.contains(.retryFailed))
    XCTAssertFalse(OneKeyImageRequestContext.renderOptions.contains(.handleCookies))
    XCTAssertFalse(OneKeyImageRequestContext.renderOptions.contains(.lowPriority))
    XCTAssertTrue(OneKeyImageRequestContext.preloadOptions.contains(.retryFailed))
    XCTAssertTrue(OneKeyImageRequestContext.preloadOptions.contains(.lowPriority))
    XCTAssertFalse(OneKeyImageRequestContext.preloadOptions.contains(.handleCookies))
  }

  func testExplicitCookieHeadersHaveDistinctPrivateIdentities() throws {
    let url = try XCTUnwrap(URL(string: "https://example.com/private.png"))
    let first = OneKeyImageHeadersIdentity(headers: ["Cookie": "session=first"])
    let second = OneKeyImageHeadersIdentity(headers: ["cookie": "session=second"])
    let firstKey = OneKeyImageRequestContext.filteredCacheKey(
      for: url,
      headersIdentity: first
    )
    let secondKey = OneKeyImageRequestContext.filteredCacheKey(
      for: url,
      headersIdentity: second
    )

    XCTAssertNotEqual(first, second)
    XCTAssertNotEqual(firstKey, secondKey)
    XCTAssertFalse(firstKey.contains("session=first"))
    XCTAssertFalse(secondKey.contains("session=second"))
  }

  func testOneKeyUsesDedicatedManagerAndDownloader() {
    XCTAssertFalse(OneKeyImagePipeline.manager === SDWebImageManager.shared)
    XCTAssertFalse(OneKeyImagePipeline.downloader === SDWebImageDownloader.shared)
    XCTAssertTrue(
      (OneKeyImagePipeline.manager.imageCache as AnyObject) === SDImageCache.shared
    )
    XCTAssertTrue(
      (OneKeyImagePipeline.manager.imageLoader as AnyObject) === OneKeyImagePipeline.downloader
    )

    let customManager =
      OneKeyImageRequestContext.baseContext[SDWebImageContextOption.customManager]
      as? SDWebImageManager
    XCTAssertTrue(
      customManager === OneKeyImagePipeline.manager
    )
  }

  func testURLAndHeaderIdentityIsolateCachedResponses() throws {
    let url = try XCTUnwrap(URL(string: "https://example.com/private.png"))
    let firstHeaders = ["Authorization": "Bearer first", "X-Tenant": "one"]
    let equivalentHeaders = ["x-tenant": "one", "authorization": "Bearer first"]
    let otherHeaders = ["Authorization": "Bearer second", "X-Tenant": "one"]
    let firstIdentity = OneKeyImageHeadersIdentity(headers: firstHeaders)
    let equivalentIdentity = OneKeyImageHeadersIdentity(headers: equivalentHeaders)
    let otherIdentity = OneKeyImageHeadersIdentity(headers: otherHeaders)

    XCTAssertEqual(firstIdentity, equivalentIdentity)
    XCTAssertNotEqual(firstIdentity, otherIdentity)
    XCTAssertEqual(firstIdentity.cacheKeyComponent, equivalentIdentity.cacheKeyComponent)
    XCTAssertNotEqual(firstIdentity.cacheKeyComponent, otherIdentity.cacheKeyComponent)
    XCTAssertEqual(firstIdentity.cacheKeyComponent?.count, 64)
    XCTAssertFalse(firstIdentity.cacheKeyComponent?.contains("Bearer") ?? true)

    let firstKey = OneKeyImageRequestContext.filteredCacheKey(
      for: url,
      headersIdentity: firstIdentity
    )
    let equivalentKey = OneKeyImageRequestContext.filteredCacheKey(
      for: url,
      headersIdentity: equivalentIdentity
    )
    let otherKey = OneKeyImageRequestContext.filteredCacheKey(
      for: url,
      headersIdentity: otherIdentity
    )
    XCTAssertEqual(firstKey, equivalentKey)
    XCTAssertNotEqual(firstKey, otherKey)
    XCTAssertFalse(firstKey.contains("Bearer first"))
    XCTAssertFalse(otherKey.contains("Bearer second"))
  }

  func testHeaderManagerPoolReusesIsolatesAndReclaimsManagers() {
    let pool = OneKeyImageManagerPool()
    let emptyIdentity = OneKeyImageHeadersIdentity(headers: nil)
    let firstIdentity = OneKeyImageHeadersIdentity(
      headers: ["Authorization": "Bearer first"]
    )
    let equivalentIdentity = OneKeyImageHeadersIdentity(
      headers: ["authorization": "Bearer first"]
    )
    let otherIdentity = OneKeyImageHeadersIdentity(
      headers: ["Authorization": "Bearer second"]
    )

    let empty = pool.acquire(identity: emptyIdentity)
    let first = pool.acquire(identity: firstIdentity)
    let equivalent = pool.acquire(identity: equivalentIdentity)
    let other = pool.acquire(identity: otherIdentity)

    XCTAssertTrue(empty.manager === OneKeyImagePipeline.manager)
    XCTAssertTrue(first.manager === equivalent.manager)
    XCTAssertFalse(first.manager === other.manager)
    XCTAssertFalse(first.manager === OneKeyImagePipeline.manager)
    XCTAssertFalse(
      (first.manager.imageLoader as AnyObject) === (other.manager.imageLoader as AnyObject)
    )
    XCTAssertTrue((first.manager.imageCache as AnyObject) === SDImageCache.shared)
    XCTAssertTrue((other.manager.imageCache as AnyObject) === SDImageCache.shared)
    XCTAssertEqual(pool.activeManagerCount, 2)

    first.finish()
    first.finish()
    XCTAssertEqual(pool.activeManagerCount, 2)
    equivalent.finish()
    XCTAssertEqual(pool.activeManagerCount, 1)
    other.finish()
    XCTAssertEqual(pool.activeManagerCount, 0)
    empty.finish()

    let next = pool.acquire(identity: firstIdentity)
    XCTAssertFalse(next.manager === first.manager)
    XCTAssertEqual(pool.activeManagerCount, 1)
    next.finish()
    XCTAssertEqual(pool.activeManagerCount, 0)
  }

  func testSafetyFlightsAreSeparatedByHeaderIdentity() throws {
    let pool = OneKeyImageManagerPool()
    let coordinator = OneKeyImageSafetyFlightCoordinator(managerPool: pool)
    let url = try XCTUnwrap(URL(string: "https://example.com/private.png"))
    let first = coordinator.acquire(
      url: url,
      headers: ["Authorization": "Bearer first"]
    )
    let equivalent = coordinator.acquire(
      url: url,
      headers: ["authorization": "Bearer first"]
    )
    let other = coordinator.acquire(
      url: url,
      headers: ["Authorization": "Bearer second"]
    )

    XCTAssertTrue(first.tracker === equivalent.tracker)
    XCTAssertTrue(first.manager === equivalent.manager)
    XCTAssertFalse(first.tracker === other.tracker)
    XCTAssertFalse(first.manager === other.manager)
    XCTAssertEqual(coordinator.activeFlightCount, 2)
    XCTAssertEqual(pool.activeManagerCount, 2)

    first.finish()
    equivalent.finish()
    other.finish()
    XCTAssertEqual(coordinator.activeFlightCount, 0)
    XCTAssertEqual(pool.activeManagerCount, 0)
  }

  func testConcurrentURLFlightSharesViolationAndReleasesIdempotently() throws {
    let coordinator = OneKeyImageSafetyFlightCoordinator()
    let url = try XCTUnwrap(URL(string: "https://example.com/shared.png"))
    let otherURL = try XCTUnwrap(URL(string: "https://example.com/other.png"))
    let first = coordinator.acquire(url: url)
    let second = coordinator.acquire(url: url)
    let other = coordinator.acquire(url: otherURL)

    XCTAssertTrue(first.tracker === second.tracker)
    XCTAssertFalse(first.tracker === other.tracker)
    XCTAssertEqual(coordinator.activeFlightCount, 2)
    first.tracker.record(.encodedDataTooLarge)
    XCTAssertEqual(second.tracker.violation, .encodedDataTooLarge)

    first.finish()
    first.finish()
    XCTAssertEqual(coordinator.activeFlightCount, 2)
    second.finish()
    XCTAssertEqual(coordinator.activeFlightCount, 1)
    other.finish()
    XCTAssertEqual(coordinator.activeFlightCount, 0)

    let next = coordinator.acquire(url: url)
    XCTAssertFalse(next.tracker === first.tracker)
    XCTAssertNil(next.tracker.violation)
    next.finish()
  }

  func testStaleRequestGenerationCannotContinue() {
    XCTAssertTrue(HybridOneKeyImage.isCurrentRequestGeneration(4, current: 4))
    XCTAssertFalse(HybridOneKeyImage.isCurrentRequestGeneration(4, current: 5))
    XCTAssertTrue(
      HybridOneKeyImage.canFinishTerminal(4, current: 4, terminalGeneration: nil)
    )
    XCTAssertFalse(
      HybridOneKeyImage.canFinishTerminal(4, current: 5, terminalGeneration: nil)
    )
    XCTAssertFalse(
      HybridOneKeyImage.canFinishTerminal(4, current: 4, terminalGeneration: 4)
    )

    var events: [String] = []
    HybridOneKeyImage.deliverTerminalCallbacks(
      primary: { events.append("primary-reentered") },
      onLoadEnd: { events.append("end") }
    )
    XCTAssertEqual(events, ["primary-reentered", "end"])
  }

  func testEveryCachePolicyMapsToExpectedSDCacheType() {
    let cases: [(OneKeyImageCachePolicy, SDImageCacheType)] = [
      (.memoryDisk, .all),
      (.memory, .memory),
      (.disk, .disk),
      (.none, .none),
    ]

    for (policy, expected) in cases {
      XCTAssertEqual(
        OneKeyImageRequestContext.cacheType(for: policy),
        expected,
        policy.stringValue
      )
    }
  }

  func testThumbnailUsesSafetyGenerationAndSDWebImageDerivedKey() throws {
    let size = CGSize(width: 120, height: 192)
    let url = try XCTUnwrap(URL(string: "https://example.com/image.png"))
    XCTAssertEqual(
      OneKeyImageRequestContext.filteredCacheKey(for: url),
      "https://example.com/image.png|onekey-image-safety-v2"
    )
    let thumbnailOptions = OneKeyImageRequestContext.thumbnailOptions(pixelSize: size)
    XCTAssertEqual(
      (thumbnailOptions[.imageThumbnailPixelSize] as? NSValue)?.cgSizeValue,
      size
    )
    XCTAssertEqual(thumbnailOptions[.imagePreserveAspectRatio] as? Bool, true)
  }

  func testDecoderSetExplicitlyIncludesSVGAndWebPWithoutAVIF() {
    OneKeyImageCoderRegistry.ensureWebPRegistered()
    let names = (OneKeyImageCoderRegistry.coder.coders ?? []).map {
      String(describing: type(of: $0))
    }
    XCTAssertTrue(names.contains("SDImageWebPCoder"))
    XCTAssertTrue(names.contains("SDImageSVGCoder"))
    XCTAssertFalse(names.contains { $0.localizedCaseInsensitiveContains("AVIF") })
    XCTAssertTrue(
      (OneKeyImageCoderRegistry.coder.coders ?? []).contains {
        ($0 as AnyObject) === SDImageSVGCoder.shared
      })
    XCTAssertTrue(
      (SDImageCodersManager.shared.coders ?? []).contains {
        ($0 as AnyObject) === SDImageWebPCoder.shared
      })
  }

  func testNonCoverDecodeUsesRectangularTarget() throws {
    let size = try XCTUnwrap(
      OneKeyImageDecodeSizing.thumbnailPixelSize(
        viewSize: CGSize(width: 40, height: 64),
        scale: 3,
        contentFit: .contain
      )
    )
    XCTAssertEqual(size, CGSize(width: 120, height: 192))
  }

  func testCoverAndPreloadUseLongestEdge() throws {
    let logicalSize = try XCTUnwrap(
      OneKeyImageDecodeSizing.logicalViewSize(width: 40, height: 64)
    )
    let size = try XCTUnwrap(
      OneKeyImageDecodeSizing.thumbnailPixelSize(
        viewSize: logicalSize,
        scale: 3,
        contentFit: .cover
      )
    )
    XCTAssertEqual(size, CGSize(width: 192, height: 192))
  }

  func testDecodeTargetNeverExceedsSixteenMegabytes() throws {
    let size = try XCTUnwrap(
      OneKeyImageDecodeSizing.thumbnailPixelSize(
        viewSize: CGSize(width: 5_000, height: 4_000),
        scale: 3,
        contentFit: .contain
      )
    )
    XCTAssertLessThanOrEqual(
      size.width * size.height * CGFloat(OneKeyImageDecodeSizing.bytesPerPixel),
      CGFloat(OneKeyImageDecodeSizing.maximumDecodedBytes)
    )
    XCTAssertEqual(size.width / size.height, 1.25, accuracy: 0.001)
  }

  func testInvalidOrMissingPreloadDimensionsAreNormalizedConservatively() {
    XCTAssertNil(OneKeyImageDecodeSizing.logicalViewSize(width: 0, height: nil))
    XCTAssertEqual(
      OneKeyImageDecodeSizing.logicalViewSize(width: nil, height: 64),
      CGSize(width: 64, height: 64)
    )
  }

  func testNoSizePreloadUsesCappedDecodeTarget() {
    XCTAssertEqual(HybridOneKeyImageCache.maximumConcurrentPreloads, 4)
    XCTAssertEqual(
      OneKeyImageDecodeSizing.defaultPreloadPixelSize,
      CGSize(width: 2_048, height: 2_048)
    )
    XCTAssertEqual(
      OneKeyImageDecodeSizing.defaultPreloadPixelSize.width
        * OneKeyImageDecodeSizing.defaultPreloadPixelSize.height
        * CGFloat(OneKeyImageDecodeSizing.bytesPerPixel),
      CGFloat(OneKeyImageDecodeSizing.maximumDecodedBytes)
    )
  }

  func testEncodedAndMetadataSafetyLimits() {
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: OneKeyImageSafetyPolicy.maximumEncodedBytes + 1,
        metadata: nil
      ),
      .encodedDataTooLarge
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: OneKeyImageMetadata(
          width: OneKeyImageSafetyPolicy.maximumStaticSide + 1,
          height: 1,
          frameCount: 1,
          duration: nil
        )
      ),
      .staticDimensionsTooLarge
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: OneKeyImageSafetyPolicy.maximumAnimatedEncodedBytes + 1,
        metadata: OneKeyImageMetadata(
          width: 1,
          height: 1,
          frameCount: 2,
          duration: 1
        )
      ),
      .animatedEncodedDataTooLarge
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: OneKeyImageMetadata(
          width: OneKeyImageSafetyPolicy.maximumAnimatedSide + 1,
          height: 1,
          frameCount: 2,
          duration: 1
        )
      ),
      .animatedDimensionsTooLarge
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: OneKeyImageMetadata(
          width: 1,
          height: 1,
          frameCount: OneKeyImageSafetyPolicy.maximumAnimatedFrameCount + 1,
          duration: nil
        )
      ),
      .animatedFrameCountTooLarge
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: OneKeyImageMetadata(
          width: 1,
          height: 1,
          frameCount: 2,
          duration: OneKeyImageSafetyPolicy.maximumAnimatedDuration + 0.001
        )
      ),
      .animatedDurationTooLong
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: OneKeyImageMetadata(
          width: 1,
          height: 1,
          frameCount: 2,
          duration: .nan
        )
      ),
      .animatedDurationTooLong
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: OneKeyImageMetadata(
          width: 1,
          height: 1,
          frameCount: 2,
          duration: .infinity
        )
      ),
      .animatedDurationTooLong
    )
  }

  func testAnimatedLimitsDoNotRequireDimensions() {
    let dimensionlessMetadata = { (frameCount: Int, duration: TimeInterval?) in
      OneKeyImageMetadata(
        width: nil,
        height: nil,
        frameCount: frameCount,
        duration: duration
      )
    }

    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: OneKeyImageSafetyPolicy.maximumAnimatedEncodedBytes + 1,
        metadata: dimensionlessMetadata(2, 1)
      ),
      .animatedEncodedDataTooLarge
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: dimensionlessMetadata(
          OneKeyImageSafetyPolicy.maximumAnimatedFrameCount + 1,
          nil
        )
      ),
      .animatedFrameCountTooLarge
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: dimensionlessMetadata(2, .nan)
      ),
      .animatedDurationTooLong
    )
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: 1,
        metadata: dimensionlessMetadata(2, .infinity)
      ),
      .animatedDurationTooLong
    )
  }

  func testMetadataDimensionsIncludeLargerLaterFrames() {
    let dimensions = OneKeyImageSafetyPolicy.maximumDimensions(
      width: 100,
      height: 200,
      candidateWidth: 9_000,
      candidateHeight: 300
    )
    XCTAssertEqual(dimensions.width, 9_000)
    XCTAssertEqual(dimensions.height, 300)
  }

  func testSafetyLimitsAcceptExactBoundaries() {
    XCTAssertNil(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: OneKeyImageSafetyPolicy.maximumAnimatedEncodedBytes,
        metadata: OneKeyImageMetadata(
          width: 4_000,
          height: 4_000,
          frameCount: OneKeyImageSafetyPolicy.maximumAnimatedFrameCount,
          duration: OneKeyImageSafetyPolicy.maximumAnimatedDuration
        )
      )
    )
    XCTAssertNil(
      OneKeyImageSafetyPolicy.violation(
        encodedByteCount: OneKeyImageSafetyPolicy.maximumEncodedBytes,
        metadata: OneKeyImageMetadata(
          width: 10_000,
          height: 10_000,
          frameCount: 1,
          duration: nil
        )
      )
    )
  }

  func testDataURIPayloadCalculationAndCorruptDataClassification() {
    XCTAssertNil(OneKeyImageSafetyPolicy.preflight(source: "data:image/png;base64,TWE="))
    XCTAssertEqual(
      OneKeyImageSafetyPolicy.preflight(
        source: "data:text/plain,\(String(repeating: "a", count: 8 * 1024 * 1024 + 1))"
      ),
      .dataURIPayloadTooLarge
    )
    XCTAssertNil(OneKeyImageSafetyPolicy.violation(for: Data("not an image".utf8)))
  }

  func testReceivedByteTrackerRejectsEveryOversizedObservation() {
    let tracker = OneKeyImageSafetyTracker()
    XCTAssertFalse(
      tracker.inspectReceivedByteCount(Int64(OneKeyImageSafetyPolicy.maximumEncodedBytes))
    )
    XCTAssertTrue(
      tracker.inspectReceivedByteCount(Int64(OneKeyImageSafetyPolicy.maximumEncodedBytes + 1))
    )
    XCTAssertTrue(
      tracker.inspectReceivedByteCount(Int64(OneKeyImageSafetyPolicy.maximumEncodedBytes + 2))
    )
    XCTAssertEqual(tracker.violation, .encodedDataTooLarge)
  }

  func testSafetyContextInstallsTransportGuards() {
    let tracker = OneKeyImageSafetyTracker()
    let context = OneKeyImageRequestContext.transportGuards(tracker: tracker)
    XCTAssertNotNil(context[.downloadResponseModifier] as? SDWebImageDownloaderResponseModifier)
    XCTAssertNotNil(context[.downloadDecryptor] as? SDWebImageDownloaderDecryptor)
  }

  func testSafetyFailureNeverRetriesRawTosURL() {
    XCTAssertTrue(
      HybridOneKeyImage.shouldRetryRaw(
        mayFallbackToRaw: true,
        safetyViolation: nil
      )
    )
    XCTAssertFalse(
      HybridOneKeyImage.shouldRetryRaw(
        mayFallbackToRaw: true,
        safetyViolation: .encodedDataTooLarge
      )
    )
  }

  func testAnimatedViewUsesBoundedFrameBuffer() throws {
    let image = HybridOneKeyImage()
    let imageView = try XCTUnwrap(image.view as? SDAnimatedImageView)
    XCTAssertEqual(
      imageView.maxBufferSize,
      OneKeyImageSafetyPolicy.maximumAnimatedBufferBytes
    )
    XCTAssertTrue(imageView.clearBufferWhenStopped)
  }
}
