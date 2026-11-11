import SDWebImage
import SDWebImageSVGCoder
import SDWebImageWebPCoder
import UIKit
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

  func testSharedDiskBudgetIsConfiguredForIsolatedManagerFirst() {
    let shared = SDImageCache.shared
    let age = shared.config.maxDiskAge
    let memoryCost = shared.config.maxMemoryCost
    let memoryCount = shared.config.maxMemoryCount
    let defaultDiskSize = SDImageCacheConfig.default.maxDiskSize
    let independentConfig = SDImageCacheConfig()
    independentConfig.maxDiskSize = 7 * 1024 * 1024
    let independent = SDImageCache(namespace: "independent-budget-\(UUID().uuidString)",
      diskCacheDirectory: nil, config: independentConfig)

    let isolated = OneKeyImagePipeline.makeIsolatedManager()
    XCTAssertTrue((isolated.imageCache as AnyObject) === shared)
    XCTAssertEqual(shared.config.maxDiskSize, 320 * 1024 * 1024)
    XCTAssertTrue((OneKeyImagePipeline.manager.imageCache as AnyObject) === shared)
    XCTAssertEqual(shared.config.maxDiskAge, age)
    XCTAssertEqual(shared.config.maxMemoryCost, memoryCost)
    XCTAssertEqual(shared.config.maxMemoryCount, memoryCount)
    XCTAssertEqual(SDImageCacheConfig.default.maxDiskSize, defaultDiskSize)
    XCTAssertEqual(independent.config.maxDiskSize, 7 * 1024 * 1024)
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
      "https://example.com/image.png|onekey-image-safety-v4"
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
    XCTAssertTrue(names.contains("OneKeySVGCoder"))
    XCTAssertFalse(names.contains { $0.localizedCaseInsensitiveContains("AVIF") })
    XCTAssertTrue(
      (OneKeyImageCoderRegistry.coder.coders ?? []).contains {
        $0 is SDImageSVGCoder && ($0 as AnyObject) !== SDImageSVGCoder.shared
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

  // The pre-layout memory probe is only useful if it reproduces a key some
  // earlier load stored under. These pin the probe against the two real key
  // producers: the laid-out render request (bounds + contentFit) and the
  // preload (logicalViewSize(resizeWidth, resizeHeight) + .cover).
  private func renderKey(bounds: CGSize, fit: OneKeyImageContentFit) throws -> CGSize {
    try XCTUnwrap(
      OneKeyImageDecodeSizing.thumbnailPixelSize(viewSize: bounds, scale: 3, contentFit: fit)
    )
  }

  private func preloadKey(width: Double?, height: Double?) throws -> CGSize {
    try renderKey(
      bounds: try XCTUnwrap(OneKeyImageDecodeSizing.logicalViewSize(width: width, height: height)),
      fit: .cover
    )
  }

  func testLaidOutProbeUsesExactlyTheRenderRequestKey() throws {
    let bounds = CGSize(width: 40, height: 64)
    for fit in [OneKeyImageContentFit.cover, .contain, .fill, .center] {
      XCTAssertEqual(
        OneKeyImageDecodeSizing.probeThumbnailPixelSizes(
          bounds: bounds, resizeWidth: 32, resizeHeight: 48, scale: 3, contentFit: fit
        ),
        [try renderKey(bounds: bounds, fit: fit)],
        "\(fit)"
      )
    }
  }

  func testPreLayoutSquareHintMatchesPreloadAndRenderKeys() throws {
    // A token icon: resizeWidth only, laid out square, cover. One key, shared
    // by the preload, the laid-out request and the probe.
    let sizes = OneKeyImageDecodeSizing.probeThumbnailPixelSizes(
      bounds: .zero, resizeWidth: 40, resizeHeight: nil, scale: 3, contentFit: .cover
    )
    XCTAssertEqual(sizes, [CGSize(width: 120, height: 120)])
    XCTAssertEqual(sizes, [try preloadKey(width: 40, height: nil)])
    XCTAssertEqual(sizes, [try renderKey(bounds: CGSize(width: 40, height: 40), fit: .cover)])
  }

  func testPreLayoutRectangularHintProbesRenderAndPreloadKeys() throws {
    // The README preload shape (48x64 @3) under a non-cover view: the laid-out
    // request key comes first, the preload's cover key second, no duplicates.
    let sizes = OneKeyImageDecodeSizing.probeThumbnailPixelSizes(
      bounds: .zero, resizeWidth: 48, resizeHeight: 64, scale: 3, contentFit: .contain
    )
    XCTAssertEqual(
      sizes,
      [
        try renderKey(bounds: CGSize(width: 48, height: 64), fit: .contain),
        try preloadKey(width: 48, height: 64),
      ]
    )
    XCTAssertEqual(sizes, [CGSize(width: 144, height: 192), CGSize(width: 192, height: 192)])
    // Under cover both collapse to the preload key.
    XCTAssertEqual(
      OneKeyImageDecodeSizing.probeThumbnailPixelSizes(
        bounds: .zero, resizeWidth: 48, resizeHeight: 64, scale: 3, contentFit: .cover
      ),
      [try preloadKey(width: 48, height: 64)]
    )
  }

  func testPreLayoutProbeWithoutHintHasNothingToTry() {
    XCTAssertEqual(
      OneKeyImageDecodeSizing.probeThumbnailPixelSizes(
        bounds: .zero, resizeWidth: nil, resizeHeight: nil, scale: 3, contentFit: .cover
      ),
      []
    )
    XCTAssertEqual(
      OneKeyImageDecodeSizing.probeThumbnailPixelSizes(
        bounds: .zero, resizeWidth: 0, resizeHeight: -1, scale: 3, contentFit: .cover
      ),
      []
    )
  }

  func testMemoryVariantsChooseNearestLargerThenNearestSmaller() {
    let target = memoryVariant(96)
    XCTAssertEqual(
      OneKeyImageMemoryVariantRegistry.orderedCandidates(
        variants: [memoryVariant(48), memoryVariant(72), memoryVariant(120), memoryVariant(144)],
        target: target
      ),
      [memoryVariant(120), memoryVariant(72)]
    )
  }

  func testMemoryVariantsIgnoreExactAndIncompatibleAspectRatios() {
    let target = memoryVariant(96)
    let wide = OneKeyImageMemoryVariant(
      requestURL: URL(string: "https://example.com/wide.png")!,
      thumbnailPixelSize: CGSize(width: 120, height: 60)
    )
    XCTAssertEqual(
      OneKeyImageMemoryVariantRegistry.orderedCandidates(
        variants: [target, wide, memoryVariant(120)],
        target: target,
        excluding: [target]
      ),
      [memoryVariant(120)]
    )
  }

  func testCrossSizeCenterPreviewUsesTemporaryScaling() {
    XCTAssertTrue(
      HybridOneKeyImage.shouldScaleCenterMemoryPreview(
        contentFit: .center,
        candidate: memoryVariant(48),
        target: memoryVariant(96)
      )
    )
    XCTAssertTrue(
      HybridOneKeyImage.shouldScaleCenterMemoryPreview(
        contentFit: .center,
        candidate: memoryVariant(144),
        target: memoryVariant(96)
      )
    )
    XCTAssertFalse(
      HybridOneKeyImage.shouldScaleCenterMemoryPreview(
        contentFit: .center,
        candidate: memoryVariant(96),
        target: memoryVariant(96)
      )
    )
    XCTAssertFalse(
      HybridOneKeyImage.shouldScaleCenterMemoryPreview(
        contentFit: .cover,
        candidate: memoryVariant(48),
        target: memoryVariant(96)
      )
    )
  }

  func testReplacingMemoryPreviewSkipsWholeViewFade() {
    XCTAssertFalse(
      HybridOneKeyImage.shouldAnimateLoadedImageTransition(
        cacheType: .disk,
        requestDuration: 1,
        reduceMotionEnabled: false,
        replacingMemoryPreview: true
      )
    )
    XCTAssertTrue(
      HybridOneKeyImage.shouldAnimateLoadedImageTransition(
        cacheType: .disk,
        requestDuration: 1,
        reduceMotionEnabled: false,
        replacingMemoryPreview: false
      )
    )
  }

  private func memoryVariant(_ size: Int) -> OneKeyImageMemoryVariant {
    OneKeyImageMemoryVariant(
      requestURL: URL(string: "https://example.com/image-\(size).png")!,
      thumbnailPixelSize: CGSize(width: size, height: size)
    )
  }

  func testDisplayedImageIsPreservedOnlyWhileShowingOne() {
    XCTAssertTrue(HybridOneKeyImage.shouldPreserveDisplayedImage(isShowingImage: true, hasImage: true))
    XCTAssertFalse(HybridOneKeyImage.shouldPreserveDisplayedImage(isShowingImage: true, hasImage: false))
    XCTAssertFalse(HybridOneKeyImage.shouldPreserveDisplayedImage(isShowingImage: false, hasImage: true))
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

  func testRecycledViewForgetsBothResizeHints() throws {
    let image = HybridOneKeyImage()
    image.sourceUri = "https://example.com/hinted.png"
    image.resizeWidth = 48
    image.resizeHeight = 64
    image.prepareForRecycle()
    XCTAssertNil(image.resizeWidth)
    XCTAssertNil(image.resizeHeight)
    XCTAssertNil(image.sourceUri)
  }

  func testViewAndPreloadPickTheSameTosRenditionForOneHint() throws {
    // Preload: long edge of logicalViewSize(resizeWidth, resizeHeight).
    // View: the same, before and after layout, so the request URL (and with it
    // the cache key) is identical on both sides.
    let raw = try XCTUnwrap(URL(string: "https://common.onekey-asset.com/token.png"))
    let hint = try XCTUnwrap(OneKeyImageDecodeSizing.logicalViewSize(width: 48, height: 64))
    let preloadDisplaySize = max(hint.width, hint.height)
    for bounds in [CGSize.zero, CGSize(width: 48, height: 64), CGSize(width: 30, height: 30)] {
      let viewDisplaySize = try XCTUnwrap(
        OneKeyImageDecodeSizing.tosDisplaySize(bounds: bounds, resizeWidth: 48, resizeHeight: 64)
      )
      XCTAssertEqual(viewDisplaySize, preloadDisplaySize)
      XCTAssertEqual(
        OneKeyTosURL.optimized(
          rawURL: raw, displaySize: viewDisplaySize, scale: 3, overscan: 1.1, hasCustomIdentity: false
        ),
        OneKeyTosURL.optimized(
          rawURL: raw, displaySize: preloadDisplaySize, scale: 3, overscan: 1.1, hasCustomIdentity: false
        )
      )
    }
    // Width-only hint keeps the historical behaviour (the width itself).
    XCTAssertEqual(
      OneKeyImageDecodeSizing.tosDisplaySize(bounds: .zero, resizeWidth: 40, resizeHeight: nil),
      40
    )
    // No hint: the laid-out bounds decide; nothing before layout.
    XCTAssertEqual(
      OneKeyImageDecodeSizing.tosDisplaySize(
        bounds: CGSize(width: 40, height: 64), resizeWidth: nil, resizeHeight: nil
      ),
      64
    )
    XCTAssertNil(
      OneKeyImageDecodeSizing.tosDisplaySize(bounds: .zero, resizeWidth: nil, resizeHeight: nil)
    )
  }

  func testSupersededRequestNeverPaintsRecycledView() throws {
    let image = HybridOneKeyImage()
    let imageView = try XCTUnwrap(image.view as? SDAnimatedImageView)
    let painted = UIImage()

    image.applyDisplayedImage(painted, generation: .max)
    XCTAssertNil(imageView.image)

    image.applyDisplayedImage(painted, generation: 0)
    XCTAssertIdentical(imageView.image, painted)
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

  func testRoundPropUpdatesNativeCornerRadius() throws {
    let image = HybridOneKeyImage()
    let imageView = try XCTUnwrap(image.view as? SDAnimatedImageView)
    imageView.frame = CGRect(x: 0, y: 0, width: 40, height: 40)

    image.sourceUri = "https://example.com/round.png"
    image.round = true
    image.afterUpdate()
    imageView.layoutIfNeeded()
    XCTAssertEqual(imageView.layer.cornerRadius, 20)

    image.round = nil
    image.afterUpdate()
    XCTAssertEqual(imageView.layer.cornerRadius, 20)

    image.round = false
    image.afterUpdate()
    XCTAssertEqual(imageView.layer.cornerRadius, 0)
  }

  func testRecycleKeepsRoundMaskUntilVisibleViewDetaches() throws {
    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    let image = HybridOneKeyImage()
    let imageView = try XCTUnwrap(image.view as? SDAnimatedImageView)
    imageView.frame = CGRect(x: 0, y: 0, width: 40, height: 40)
    window.addSubview(imageView)
    image.sourceUri = "https://example.com/round.png"
    image.round = true
    image.afterUpdate()
    imageView.layoutIfNeeded()

    // Fabric resets props in setter order before the view leaves the hierarchy.
    image.round = false
    image.sourceUri = nil
    image.afterUpdate()
    XCTAssertEqual(imageView.layer.cornerRadius, 20)

    image.prepareForRecycle()

    XCTAssertEqual(imageView.layer.cornerRadius, 20)

    image.sourceUri = "https://example.com/reused.png"
    image.afterUpdate()
    XCTAssertEqual(imageView.layer.cornerRadius, 0)
  }

  func testStartupPrewarmHashedKeysEvictionAndMemoryHitRecording() throws {
    let tempFile = FileManager.default.temporaryDirectory
      .appendingPathComponent("onekey-image-startup-test-\(UUID().uuidString).json")
    defer {
      OneKeyImageStartupPrewarm.shared.resetForTesting(fileURL: nil)
      try? FileManager.default.removeItem(at: tempFile)
    }
    OneKeyImageStartupPrewarm.shared.resetForTesting(fileURL: tempFile)

    let format = UIGraphicsImageRendererFormat()
    format.scale = 1
    let sampleImage = UIGraphicsImageRenderer(size: CGSize(width: 32, height: 32), format: format).image { ctx in
      UIColor.systemBlue.setFill()
      ctx.fill(CGRect(x: 0, y: 0, width: 32, height: 32))
    }
    let pngData = try XCTUnwrap(sampleImage.pngData())

    let remoteURL = try XCTUnwrap(URL(string: "https://example.com/sensitive-banner.png"))
    let avatarSeed = "0x95ad61b0a150d79219dcf64e1e6cc01f0b64c4ce"
    let avatarURL = try XCTUnwrap(URL(string: "onekey-avatar://blockie/v1/\(avatarSeed)"))
    let thumbSize = CGSize(width: 64, height: 64)

    let remoteOriginalContext = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: nil,
      safetyTracker: nil,
      manager: OneKeyImagePipeline.manager,
      url: remoteURL
    )
    let remoteOriginalKey = try XCTUnwrap(
      OneKeyImagePipeline.manager.cacheKey(for: remoteURL, context: remoteOriginalContext)
    )
    SDImageCache.shared.storeImageData(toDisk: pngData, forKey: remoteOriginalKey)

    let avatarOriginalContext = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: nil,
      safetyTracker: nil,
      manager: OneKeyImagePipeline.manager,
      url: avatarURL
    )
    let avatarOriginalKey = try XCTUnwrap(
      OneKeyImagePipeline.manager.cacheKey(for: avatarURL, context: avatarOriginalContext)
    )
    OneKeyAvatarImageLoader.cache.storeImageData(toDisk: pngData, forKey: avatarOriginalKey)

    // 1. App Lock partial flush (1 entry < minimumSettledCount) keeps recording open for Home screen.
    OneKeyImageStartupPrewarm.shared.note(
      requestURL: avatarURL,
      rawURL: avatarURL,
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: thumbSize
    )
    OneKeyImageStartupPrewarm.shared.flushForTesting(forceClose: false)

    OneKeyImageStartupPrewarm.shared.note(
      requestURL: remoteURL,
      rawURL: remoteURL,
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: thumbSize
    )
    OneKeyImageStartupPrewarm.shared.flushForTesting(forceClose: true)

    // 2. Verify privacy: persisted JSON contains NO plaintext URLs, wallet addresses, or rawURL duplicates.
    let jsonText = try String(contentsOf: tempFile, encoding: .utf8)
    XCTAssertFalse(jsonText.contains("example.com"), "Plaintext domain must not appear in startup JSON")
    XCTAssertFalse(jsonText.contains(avatarSeed), "Plaintext wallet address must not appear in startup JSON")
    XCTAssertFalse(jsonText.contains("onekey-avatar"), "Plaintext scheme must not appear in startup JSON")
    XCTAssertFalse(jsonText.contains("rawURL"), "Redundant rawURL field must not appear in startup JSON")

    let decodedEntries = try JSONDecoder().decode(
      [OneKeyImageStartupPrewarm.Entry].self,
      from: Data(jsonText.utf8)
    )
    XCTAssertEqual(decodedEntries.count, 2)
    XCTAssertEqual(decodedEntries.map(\.launch), [1, 1])

    // 3. Clear memory caches, run prewarm(), and verify claimPrewarmedImage promotes to exact runtime key.
    SDImageCache.shared.clearMemory()
    OneKeyAvatarImageLoader.cache.clearMemory()
    OneKeyImageStartupPrewarm.shared.resetForTesting(fileURL: tempFile)
    OneKeyImageStartupPrewarm.shared.prewarm()

    let prewarmMetrics = OneKeyImageStartupPrewarm.shared.metricsSnapshot()
    XCTAssertEqual(prewarmMetrics.entryCount, 2)
    XCTAssertEqual(prewarmMetrics.decodedCount, 2)

    let remoteThumbContext = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: thumbSize,
      safetyTracker: nil,
      manager: OneKeyImagePipeline.manager,
      url: remoteURL
    )
    let remoteThumbKey = try XCTUnwrap(
      OneKeyImagePipeline.manager.cacheKey(for: remoteURL, context: remoteThumbContext)
    )
    XCTAssertNotNil(
      OneKeyImageStartupPrewarm.shared.claimPrewarmedImage(
        forKey: remoteThumbKey,
        cache: SDImageCache.shared
      )
    )
    XCTAssertNotNil(SDImageCache.shared.imageFromMemoryCache(forKey: remoteThumbKey))

    // 4. Launch-generation eviction: only remoteURL is noted in launches 2, 3, 4 -> avatarURL evicted on launch 4.
    for launch in 2...4 {
      OneKeyImageStartupPrewarm.shared.resetForTesting(fileURL: tempFile)
      OneKeyImageStartupPrewarm.shared.prewarm()
      OneKeyImageStartupPrewarm.shared.note(
        requestURL: remoteURL,
        rawURL: remoteURL,
        headersJson: nil,
        cachePolicy: .memoryDisk,
        thumbnailPixelSize: thumbSize
      )
      OneKeyImageStartupPrewarm.shared.flushForTesting(forceClose: true)
      let current = try JSONDecoder().decode(
        [OneKeyImageStartupPrewarm.Entry].self,
        from: Data(contentsOf: tempFile)
      )
      if launch < 4 {
        XCTAssertEqual(current.count, 2)
      } else {
        XCTAssertEqual(current.count, 1)
        XCTAssertNil(current.first?.namespace)
        XCTAssertEqual(current.first?.launch, 4)
      }
    }
  }

  func testMainThreadDecodeLimitsBudgetAndMetrics() throws {
    OneKeyMainThreadDecode.resetForTesting()
    SDImageCache.shared.clearMemory()

    let format = UIGraphicsImageRendererFormat()
    format.scale = 1
    let smallImage = UIGraphicsImageRenderer(size: CGSize(width: 40, height: 40), format: format).image { ctx in
      UIColor.systemGreen.setFill()
      ctx.fill(CGRect(x: 0, y: 0, width: 40, height: 40))
    }
    let smallPNG = try XCTUnwrap(smallImage.pngData())
    let smallURL = try XCTUnwrap(URL(string: "https://example.com/sync-small.png"))
    let thumbSize = CGSize(width: 80, height: 80)

    let originalContext = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: nil,
      safetyTracker: nil,
      manager: OneKeyImagePipeline.manager,
      url: smallURL
    )
    let originalKey = try XCTUnwrap(
      OneKeyImagePipeline.manager.cacheKey(for: smallURL, context: originalContext)
    )
    SDImageCache.shared.storeImageData(toDisk: smallPNG, forKey: originalKey)

    let promoted = OneKeyMainThreadDecode.run { () -> Bool? in
      OneKeyMainThreadDecode.promoteToMemory(
        url: smallURL,
        rawURL: smallURL,
        thumbnailPixelSize: thumbSize
      ) ? true : nil
    }
    XCTAssertEqual(promoted, true)
    XCTAssertEqual(OneKeyMainThreadDecode.metricsSnapshot().hits, 1)

    // Oversized source dimension (> 512px) must be rejected on the main thread.
    let largeCanvasImage = UIGraphicsImageRenderer(size: CGSize(width: 600, height: 600), format: format).image { ctx in
      UIColor.black.setFill()
      ctx.fill(CGRect(x: 0, y: 0, width: 600, height: 600))
    }
    let largeCanvasPNG = try XCTUnwrap(largeCanvasImage.pngData())
    let largeCanvasURL = try XCTUnwrap(URL(string: "https://example.com/sync-large-canvas.png"))
    let largeOriginalContext = OneKeyImageRequestContext.make(
      headersJson: nil,
      cachePolicy: .memoryDisk,
      thumbnailPixelSize: nil,
      safetyTracker: nil,
      manager: OneKeyImagePipeline.manager,
      url: largeCanvasURL
    )
    let largeOriginalKey = try XCTUnwrap(
      OneKeyImagePipeline.manager.cacheKey(for: largeCanvasURL, context: largeOriginalContext)
    )
    SDImageCache.shared.storeImageData(toDisk: largeCanvasPNG, forKey: largeOriginalKey)

    let rejectedLarge = OneKeyMainThreadDecode.run { () -> Bool? in
      OneKeyMainThreadDecode.promoteToMemory(
        url: largeCanvasURL,
        rawURL: largeCanvasURL,
        thumbnailPixelSize: thumbSize
      ) ? true : nil
    }
    XCTAssertNil(rejectedLarge)
    XCTAssertGreaterThanOrEqual(OneKeyMainThreadDecode.metricsSnapshot().limitRejections, 1)
  }
}
