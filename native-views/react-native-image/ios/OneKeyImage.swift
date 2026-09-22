import Foundation
import NitroModules
import SDWebImage
import Skeleton
import UIKit

private final class OneKeyImageHostView: SDAnimatedImageView {
  var onLayout: (() -> Void)?
  var onWindowChanged: ((Bool) -> Void)?
  var round = false {
    didSet { updateRoundMask() }
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    updateRoundMask()
    onLayout?()
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    onWindowChanged?(window != nil)
  }

  private func updateRoundMask() {
    layer.cornerRadius = round ? min(bounds.width, bounds.height) / 2 : 0
  }
}

private final class OneKeyImageSkeletonView: UIView {
  private lazy var renderer = OneKeySkeletonRenderer(hostLayer: layer)
  private var requestedRunning = false
  private var colors: [UIColor]?

  override init(frame: CGRect) {
    super.init(frame: frame)
    isUserInteractionEnabled = false
    backgroundColor = .clear
    isHidden = true
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    renderer.update(width: bounds.width, height: bounds.height, colors: colors)
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    syncRenderer()
  }

  func setRunning(_ running: Bool) {
    requestedRunning = running
    isHidden = !running
    syncRenderer()
  }

  func updateColors(_ colors: [UIColor]) {
    self.colors = colors
    renderer.update(width: bounds.width, height: bounds.height, colors: colors)
  }

  private func syncRenderer() {
    if requestedRunning, window != nil, !bounds.isEmpty {
      renderer.update(width: bounds.width, height: bounds.height, colors: colors)
      renderer.start()
    } else {
      renderer.stop()
    }
  }
}

private final class OneKeyImageSkeletonIndicator: NSObject, SDWebImageIndicator {
  private let skeletonView = OneKeyImageSkeletonView()

  var indicatorView: UIView { skeletonView }

  func startAnimatingIndicator() {
    skeletonView.setRunning(!UIAccessibility.isReduceMotionEnabled)
  }

  func stopAnimatingIndicator() {
    skeletonView.setRunning(false)
  }

  func updateFrame(_ frame: CGRect) {
    skeletonView.frame = frame
  }

  func updateColors(_ colors: [UIColor]) {
    skeletonView.updateColors(colors)
  }
}

final class HybridOneKeyImage: HybridOneKeyImageSpec, RecyclableView {
  private enum DisplayState { case loading, image, error, fallback }
  private enum MemoryProbeResult { case miss, preview, exact }
  private static let fadeDelayThreshold: CFTimeInterval = 0.1
  private static let fadeDuration: TimeInterval = 0.14

  private let hostView = OneKeyImageHostView()
  private let skeletonIndicator = OneKeyImageSkeletonIndicator()
  private let fallbackLayer = CATextLayer()
  private var loadWorkItem: DispatchWorkItem?
  private var displayWorkItem: DispatchWorkItem?
  private var requestGeneration: UInt64 = 0
  private var terminalGeneration: UInt64?
  private var activeSafetyHandle: OneKeyImageSafetyFlightHandle?
  private var lastRequestSignature: String?
  private var pendingDisplayGeneration: UInt64?
  private var attached = false
  private var requestActive = false
  private var isResetting = false
  private var displayState = DisplayState.loading
  /// `.loading` with the previous image kept on screen as the placeholder
  /// (see `showLoading(preservedImage:)`).
  private var isPreservingDisplayedImage = false
  private var requestStartedAt: CFTimeInterval?

  var view: UIView { hostView }

  var sourceUri: String? {
    didSet {
      if sourceUri != oldValue { identityDidChange() }
    }
  }
  var sourceHeadersJson: String? {
    didSet { if sourceHeadersJson != oldValue { identityDidChange() } }
  }
  var variant: OneKeyImageVariant? = .generic { didSet { applyVariant() } }
  var contentFit: OneKeyImageContentFit? = .cover {
    didSet {
      applyContentFit()
      if contentFit != oldValue { identityDidChange() }
    }
  }
  var cachePolicy: OneKeyImageCachePolicy? = .memoryDisk {
    didSet { if cachePolicy != oldValue { identityDidChange() } }
  }
  var autoplay: Bool? = true { didSet { applyAutoplay() } }
  var recyclingKey: String? {
    didSet { if recyclingKey != oldValue { identityDidChange() } }
  }
  var optimizeTos: Bool? = true {
    didSet { if optimizeTos != oldValue { identityDidChange() } }
  }
  var round: Bool? = false
  var resizeWidth: Double? {
    didSet {
      if !Self.equalOptionalDouble(resizeWidth, oldValue) { identityDidChange() }
    }
  }
  var resizeHeight: Double? {
    didSet {
      if !Self.equalOptionalDouble(resizeHeight, oldValue) { identityDidChange() }
    }
  }
  var overscan: Double? = 1.1 {
    didSet {
      if !Self.equalOptionalDouble(overscan, oldValue) { identityDidChange() }
    }
  }
  var loadingStrategy: OneKeyImageLoadingStrategy? = .static { didSet { applyVariant() } }
  var placeholderColor: String? { didSet { applyVariant() } }
  var onLoadStart: (() -> Void)?
  var onLoad: ((_ width: Double, _ height: Double, _ cacheType: OneKeyImageCacheType) -> Void)?
  var onDisplay: (() -> Void)?
  var onError: ((_ message: String) -> Void)?
  var onLoadEnd: (() -> Void)?

  override init() {
    super.init()
    hostView.clipsToBounds = true
    hostView.maxBufferSize = OneKeyImageSafetyPolicy.maximumAnimatedBufferBytes
    hostView.clearBufferWhenStopped = true
    hostView.onLayout = { [weak self] in
      guard let self else { return }
      self.skeletonIndicator.updateFrame(self.hostView.bounds)
      self.layoutFallbackLayer()
      self.scheduleLoad()
    }
    hostView.onWindowChanged = { [weak self] attached in
      guard let self else { return }
      self.attached = attached
      self.applyAutoplay()
      if attached {
        self.scheduleLoad()
        self.schedulePendingDisplayIfNeeded()
      }
    }
    fallbackLayer.alignmentMode = .center
    fallbackLayer.contentsScale = UIScreen.main.scale
    hostView.layer.addSublayer(fallbackLayer)
    applyContentFit()
    applyAutoplay()
    applyVariant()
  }

  deinit {
    cancelCurrentRequest(invalidateGeneration: true)
  }

  func afterUpdate() {
    // Fabric can reset `round` before clearing `sourceUri` while removing a view.
    // Apply the shape once per committed prop batch so the outgoing frame keeps
    // its clipping, while mounted and recycled views still receive the new value.
    if let sourceUri, !sourceUri.isEmpty {
      hostView.round = round == true
    }
    scheduleLoad()
  }

  func reload() throws {
    runOnMainSync {
      self.lastRequestSignature = nil
      self.scheduleLoad(force: true)
    }
  }

  func cancel() throws {
    runOnMainSync {
      self.cancelCurrentRequest(invalidateGeneration: true)
      self.lastRequestSignature = nil
      self.showLoading(requestIsActive: false, letLibraryStartIndicator: false)
    }
  }

  func prepareForRecycle() {
    runOnMainSync { self.resetForReuse() }
  }

  func onDropView() {
    runOnMainSync {
      self.cancelCurrentRequest(invalidateGeneration: true)
      self.lastRequestSignature = nil
      self.showLoading(requestIsActive: false, letLibraryStartIndicator: false)
    }
  }

  private func scheduleLoad(force: Bool = false) {
    guard !isResetting else { return }
    loadWorkItem?.cancel()
    let work = DispatchWorkItem { [weak self] in self?.startLoad(force: force) }
    loadWorkItem = work
    DispatchQueue.main.async(execute: work)
  }

  private func identityDidChange() {
    guard !isResetting else { return }
    runOnMainSync {
      self.cancelCurrentRequest(invalidateGeneration: true)
      self.lastRequestSignature = nil
      if let source = self.sourceUri, !source.isEmpty {
        if self.showMemoryCachedImageIfAvailable(requestIsActive: false) == .miss {
          self.showLoading(requestIsActive: false, letLibraryStartIndicator: false)
        }
      } else {
        self.showFallback()
      }
      self.scheduleLoad()
    }
  }

  private func startLoad(force: Bool) {
    guard !hostView.bounds.isEmpty else {
      cancelCurrentRequest(invalidateGeneration: true)
      lastRequestSignature = nil
      // A memory-cached image shown from `identityDidChange` before layout
      // stays up; layout re-runs this load with real bounds and confirms it.
      if !Self.shouldPreserveDisplayedImage(
        isShowingImage: displayState == .image,
        hasImage: hostView.image != nil
      ) {
        showLoading(requestIsActive: false, letLibraryStartIndicator: false)
      }
      return
    }
    guard let rawString = sourceUri, !rawString.isEmpty else {
      cancelCurrentRequest(invalidateGeneration: true)
      lastRequestSignature = nil
      showFallback()
      return
    }

    let rawScreenScale: CGFloat = hostView.window?.screen.scale ?? UIScreen.main.scale
    let screenScale = min(max(rawScreenScale, 1), 3)
    let signatureParts: [String] = [
      rawString,
      sourceHeadersJson ?? "",
      recyclingKey ?? "",
      cachePolicy?.stringValue ?? "memory-disk",
      contentFit?.stringValue ?? "cover",
      String(optimizeTos ?? true),
      resizeWidth.map(String.init(describing:)) ?? "",
      resizeHeight.map(String.init(describing:)) ?? "",
      String(overscan ?? 1.1),
      String(Int(hostView.bounds.width.rounded())),
      String(Int(hostView.bounds.height.rounded())),
      String(Double(screenScale)),
    ]
    let signature = signatureParts.joined(separator: "|")
    if !force, signature == lastRequestSignature { return }
    lastRequestSignature = signature

    cancelCurrentRequest(invalidateGeneration: true)
    let generation = requestGeneration
    requestStartedAt = CACurrentMediaTime()
    onLoadStart?()
    guard Self.isCurrentRequestGeneration(generation, current: requestGeneration) else { return }
    if showMemoryCachedImageIfAvailable(requestIsActive: true) == .exact { return }
    // The pre-layout probe may have shown this identity from another thumbnail
    // key (a preload's, or an earlier layout size). Keep it on screen as the
    // placeholder while the exact rendition loads: blanking to a skeleton and
    // redrawing is worse than the skeleton it replaced.
    let preservedImage =
      Self.shouldPreserveDisplayedImage(
        isShowingImage: displayState == .image,
        hasImage: hostView.image != nil
      ) ? hostView.image : nil
    showLoading(
      requestIsActive: true,
      letLibraryStartIndicator: true,
      preservedImage: preservedImage
    )

    if let violation = OneKeyImageSafetyPolicy.preflight(source: rawString) {
      finishWithError(violation, generation: generation)
      return
    }
    guard let rawURL = URL(string: rawString) else {
      finishWithError(URLError(.badURL), generation: generation)
      return
    }
    if let violation = OneKeyImageSafetyPolicy.preflight(url: rawURL) {
      finishWithError(violation, generation: generation)
      return
    }

    let hasCustomIdentity = OneKeyImageRequestContext.headers(from: sourceHeadersJson) != nil
    let displaySize = OneKeyImageDecodeSizing.tosDisplaySize(
      bounds: hostView.bounds.size,
      resizeWidth: resizeWidth,
      resizeHeight: resizeHeight
    ) ?? 0
    let requestURL =
      (optimizeTos ?? true)
      ? OneKeyTosURL.optimized(
        rawURL: rawURL,
        displaySize: displaySize,
        scale: screenScale,
        overscan: overscan ?? 1.1,
        hasCustomIdentity: hasCustomIdentity
      )
      : rawURL
    performRequest(
      url: requestURL,
      rawURL: rawURL,
      generation: generation,
      mayFallbackToRaw: requestURL != rawURL,
      placeholderImage: preservedImage
    )
  }

  private func performRequest(
    url: URL,
    rawURL: URL,
    generation: UInt64,
    mayFallbackToRaw: Bool,
    placeholderImage: UIImage? = nil
  ) {
    guard Self.isCurrentRequestGeneration(generation, current: requestGeneration) else { return }
    let rawScreenScale: CGFloat = hostView.window?.screen.scale ?? UIScreen.main.scale
    let screenScale = min(max(rawScreenScale, 1), 3)
    let thumbnailPixelSize = OneKeyImageDecodeSizing.thumbnailPixelSize(
      viewSize: hostView.bounds.size,
      scale: screenScale,
      contentFit: contentFit ?? .cover
    )
    let headers = OneKeyImageRequestContext.headers(from: sourceHeadersJson)
    let safetyHandle = OneKeyImageSafetyFlightCoordinator.shared.acquire(
      url: url,
      headers: headers
    )
    activeSafetyHandle = safetyHandle
    let context = OneKeyImageRequestContext.make(
      headersJson: sourceHeadersJson,
      cachePolicy: cachePolicy ?? .memoryDisk,
      thumbnailPixelSize: thumbnailPixelSize,
      safetyTracker: safetyHandle.tracker,
      // OneKey patch: Rendering and preload use the same local-avatar loader.
      // manager: safetyHandle.manager
      manager: safetyHandle.manager,
      url: url
    )
    hostView.sd_internalSetImage(
      with: url,
      // SDWebImage resets the image view when it starts a load; hand it the
      // preserved image so the view keeps showing it until the load lands.
      placeholderImage: placeholderImage,
      options: OneKeyImageRequestContext.renderOptions,
      context: context,
      setImageBlock: { [weak self] image, _, _, _ in
        self?.applyDisplayedImage(image, generation: generation)
      },
      progress: { [weak self] receivedSize, _, _ in
        guard safetyHandle.tracker.inspectReceivedByteCount(Int64(receivedSize)) else { return }
        DispatchQueue.main.async { [weak self] in
          guard let self, self.requestGeneration == generation else { return }
          self.hostView.sd_cancelCurrentImageLoad()
        }
      },
      completed: { [weak self] image, _, error, cacheType, finished, _ in
        // Progressive loads deliver partial images before the terminal callback.
        guard finished else { return }
        guard let self, self.requestGeneration == generation else {
          safetyHandle.finish()
          return
        }
        if let image,
          let violation = OneKeyImageSafetyPolicy.violation(for: image)
        {
          safetyHandle.tracker.record(violation)
        }
        let safetyViolation = safetyHandle.tracker.violation
        self.finishSafetyHandle(safetyHandle)
        if image == nil || error != nil || safetyViolation != nil {
          if Self.shouldRetryRaw(
            mayFallbackToRaw: mayFallbackToRaw,
            safetyViolation: safetyViolation
          ) {
            self.performRequest(
              url: rawURL,
              rawURL: rawURL,
              generation: generation,
              mayFallbackToRaw: false,
              placeholderImage: self.hostView.image
            )
            return
          }
          self.finishWithError(safetyViolation ?? error, generation: generation)
          return
        }
        if let image,
          Self.canUseAsMemoryPreview(image),
          let thumbnailPixelSize,
          self.cachePolicy == .memory || self.cachePolicy == .memoryDisk || self.cachePolicy == nil
        {
          OneKeyImageMemoryVariantRegistry.shared.record(
            OneKeyImageMemoryVariant(
              requestURL: url,
              thumbnailPixelSize: thumbnailPixelSize
            ),
            for: OneKeyImageMemoryFamilyKey(
              rawURL: rawURL,
              headersJson: self.sourceHeadersJson
            )
          )
        }
        guard self.claimTerminal(generation) else { return }
        self.requestActive = false
        self.displayState = .image
        self.isPreservingDisplayedImage = false
        self.skeletonIndicator.stopAnimatingIndicator()
        self.fallbackLayer.isHidden = true
        self.hostView.backgroundColor = .clear
        let resolvedCacheType = Self.cacheType(cacheType)
        self.applyLoadedImageTransition(cacheType: resolvedCacheType)
        self.applyAutoplay()
        let onLoad = self.onLoad
        let onLoadEnd = self.onLoadEnd
        Self.deliverTerminalCallbacks(
          primary: {
            onLoad?(
              Double(image?.size.width ?? 0),
              Double(image?.size.height ?? 0),
              resolvedCacheType
            )
          },
          onLoadEnd: { onLoadEnd?() }
        )
        guard self.requestGeneration == generation else { return }
        self.pendingDisplayGeneration = generation
        self.schedulePendingDisplayIfNeeded()
      }
    )
  }

  private func finishWithError(_ error: Error?, generation: UInt64) {
    guard claimTerminal(generation) else { return }
    showError()
    let onError = onError
    let onLoadEnd = onLoadEnd
    Self.deliverTerminalCallbacks(
      primary: { onError?(error?.localizedDescription ?? "Image request failed") },
      onLoadEnd: { onLoadEnd?() }
    )
  }

  private func claimTerminal(_ generation: UInt64) -> Bool {
    guard
      Self.canFinishTerminal(
        generation,
        current: requestGeneration,
        terminalGeneration: terminalGeneration
      )
    else {
      return false
    }
    terminalGeneration = generation
    return true
  }

  private func cancelCurrentRequest(invalidateGeneration: Bool) {
    loadWorkItem?.cancel()
    loadWorkItem = nil
    displayWorkItem?.cancel()
    displayWorkItem = nil
    pendingDisplayGeneration = nil
    hostView.sd_cancelCurrentImageLoad()
    activeSafetyHandle?.finish()
    activeSafetyHandle = nil
    requestActive = false
    skeletonIndicator.stopAnimatingIndicator()
    requestStartedAt = nil
    resetImageTransition()
    if invalidateGeneration { requestGeneration &+= 1 }
  }

  private func finishSafetyHandle(_ handle: OneKeyImageSafetyFlightHandle) {
    handle.finish()
    if activeSafetyHandle === handle {
      activeSafetyHandle = nil
    }
  }

  private func schedulePendingDisplayIfNeeded() {
    guard let generation = pendingDisplayGeneration else { return }
    displayWorkItem?.cancel()
    let work = DispatchWorkItem { [weak self] in
      guard let self,
        self.pendingDisplayGeneration == generation,
        self.requestGeneration == generation,
        self.displayState == .image,
        self.hostView.image != nil,
        self.attached,
        self.hostView.window != nil
      else {
        return
      }
      self.pendingDisplayGeneration = nil
      self.displayWorkItem = nil
      self.onDisplay?()
    }
    displayWorkItem = work
    DispatchQueue.main.async(execute: work)
  }

  private func resetForReuse() {
    cancelCurrentRequest(invalidateGeneration: true)
    isResetting = true
    sourceUri = nil
    sourceHeadersJson = nil
    variant = .generic
    contentFit = .cover
    cachePolicy = .memoryDisk
    autoplay = true
    recyclingKey = nil
    optimizeTos = true
    round = false
    resizeWidth = nil
    resizeHeight = nil
    overscan = 1.1
    loadingStrategy = .static
    placeholderColor = nil
    onLoadStart = nil
    onLoad = nil
    onDisplay = nil
    onError = nil
    onLoadEnd = nil
    isResetting = false
    lastRequestSignature = nil
    showLoading(requestIsActive: false, letLibraryStartIndicator: false)
  }

  private func runOnMainSync(_ action: () -> Void) {
    if Thread.isMainThread {
      action()
    } else {
      DispatchQueue.main.sync(execute: action)
    }
  }

  // OneKey patch: SDWebImage assigns `image` from inside its own completion, which
  // runs before this object's generation guard and never re-checks whether the view
  // still belongs to the request that started the load. Fabric recycles a single
  // OneKeyImage UIView across unrelated images, so a superseded completion could
  // repaint a slot that now shows a different image. Every write goes through here.
  func applyDisplayedImage(_ image: UIImage?, generation: UInt64) {
    guard Self.isCurrentRequestGeneration(generation, current: requestGeneration) else {
      return
    }
    // Also the placeholder write path (SDWebImage hands the preserved image
    // back through setImageBlock when the load starts), so the preservation
    // flag is NOT touched here; it ends with the terminal callbacks.
    hostView.image = image
  }

  private func showLoading(
    requestIsActive: Bool,
    letLibraryStartIndicator: Bool,
    preservedImage: UIImage? = nil
  ) {
    displayState = .loading
    requestActive = requestIsActive
    resetImageTransition()
    fallbackLayer.isHidden = true
    if let preservedImage {
      // Same identity, different thumbnail key: the image already on screen
      // stands in for the placeholder, so no blank frame and no indicator.
      // The indicator is unmounted, not just stopped: SDWebImage restarts a
      // mounted `sd_imageIndicator` when the load begins, and `applyVariant`
      // must not reinstall it while this state lasts.
      isPreservingDisplayedImage = true
      hostView.image = preservedImage
      skeletonIndicator.stopAnimatingIndicator()
      if hostView.sd_imageIndicator != nil {
        hostView.sd_imageIndicator = nil
      }
      hostView.backgroundColor = .clear
      return
    }
    isPreservingDisplayedImage = false
    hostView.image = nil
    applyLoadingAppearance(letLibraryStartIndicator: letLibraryStartIndicator)
  }

  /// Whether the image currently on screen should stay up while a load for the
  /// SAME identity runs (bounds not laid out yet, or the laid-out request key
  /// differs from the one the pre-layout probe hit). Pure for testability.
  static func shouldPreserveDisplayedImage(isShowingImage: Bool, hasImage: Bool) -> Bool {
    isShowingImage && hasImage
  }

  private func showMemoryCachedImageIfAvailable(requestIsActive: Bool) -> MemoryProbeResult {
    // JS leaves `cachePolicy` unset for the common case and Fabric then hands
    // Nitro a nil, which the request path already treats as memory-disk; the
    // probe must agree, otherwise a nil policy skips the memory lookup entirely
    // and every mount pays the async pipeline round trip (an icon skeleton
    // frame) for an image that is already decoded in memory.
    let effectivePolicy = cachePolicy ?? .memoryDisk
    guard effectivePolicy == .memory || effectivePolicy == .memoryDisk,
          let sourceUri,
          let rawURL = URL(string: sourceUri) else {
      return .miss
    }
    let rawScreenScale: CGFloat = hostView.window?.screen.scale ?? UIScreen.main.scale
    let screenScale = min(max(rawScreenScale, 1), 3)
    let hasCustomIdentity = OneKeyImageRequestContext.headers(from: sourceHeadersJson) != nil
    let displaySize = OneKeyImageDecodeSizing.tosDisplaySize(
      bounds: hostView.bounds.size,
      resizeWidth: resizeWidth,
      resizeHeight: resizeHeight
    ) ?? 0
    let requestURL =
      (optimizeTos ?? true)
      ? OneKeyTosURL.optimized(
        rawURL: rawURL,
        displaySize: displaySize,
        scale: screenScale,
        overscan: overscan ?? 1.1,
        hasCustomIdentity: hasCustomIdentity
      )
      : rawURL
    // The thumbnail size is part of the cache key. Laid out, it is this view's
    // own request key; before layout it comes from the resize hints, under
    // this view's contentFit and under `.cover` (a preload's key). With no
    // bounds and no hint the un-thumbnailed key is the only thing to try.
    let thumbnailCandidates: [CGSize?] = {
      let sizes = OneKeyImageDecodeSizing.probeThumbnailPixelSizes(
        bounds: hostView.bounds.size,
        resizeWidth: resizeWidth,
        resizeHeight: resizeHeight,
        scale: screenScale,
        contentFit: contentFit ?? .cover
      )
      return sizes.isEmpty ? [nil] : sizes
    }()
    func memoryCachedImage(for url: URL, thumbnailPixelSize: CGSize?) -> UIImage? {
      let context = OneKeyImageRequestContext.make(
        headersJson: sourceHeadersJson,
        cachePolicy: cachePolicy ?? .memoryDisk,
        thumbnailPixelSize: thumbnailPixelSize,
        safetyTracker: nil,
        manager: OneKeyImagePipeline.manager,
        url: url
      )
      let cache = context[.imageCache] as? SDImageCache ?? SDImageCache.shared
      guard let key = OneKeyImagePipeline.manager.cacheKey(for: url, context: context) else {
        return nil
      }
      return cache.imageFromMemoryCache(forKey: key)
    }

    var exactProbes: [(variant: OneKeyImageMemoryVariant?, url: URL, size: CGSize?)] = []
    for url in requestURL == rawURL ? [requestURL] : [requestURL, rawURL] {
      for size in thumbnailCandidates {
        exactProbes.append(
          (
            variant: size.map {
              OneKeyImageMemoryVariant(requestURL: url, thumbnailPixelSize: $0)
            },
            url: url,
            size: size
          )
        )
      }
    }
    let family = OneKeyImageMemoryFamilyKey(rawURL: rawURL, headersJson: sourceHeadersJson)
    let exactVariants = Set(exactProbes.compactMap(\.variant))
    for probe in exactProbes {
      guard let image = memoryCachedImage(for: probe.url, thumbnailPixelSize: probe.size) else {
        if let variant = probe.variant {
          OneKeyImageMemoryVariantRegistry.shared.remove(variant, for: family)
        }
        continue
      }
      if let variant = probe.variant, Self.canUseAsMemoryPreview(image) {
        OneKeyImageMemoryVariantRegistry.shared.record(variant, for: family)
      }
      displayMemoryCachedImage(image, requestIsActive: requestIsActive, terminal: true)
      return .exact
    }

    if let target = exactProbes.compactMap(\.variant).first {
      let candidates = OneKeyImageMemoryVariantRegistry.shared.candidates(
        for: family,
        target: target,
        excluding: exactVariants
      )
      for candidate in candidates {
        guard
          let image = memoryCachedImage(
            for: candidate.requestURL,
            thumbnailPixelSize: candidate.thumbnailPixelSize
          ),
          Self.canUseAsMemoryPreview(image)
        else {
          OneKeyImageMemoryVariantRegistry.shared.remove(candidate, for: family)
          continue
        }
        displayMemoryCachedImage(image, requestIsActive: false, terminal: false)
        OneKeyImageMemoryVariantRegistry.shared.record(candidate, for: family)
        return .preview
      }
    }
    return .miss
  }

  private func displayMemoryCachedImage(
    _ image: UIImage,
    requestIsActive: Bool,
    terminal: Bool
  ) {
    let generation = requestGeneration
    if terminal, requestIsActive, !claimTerminal(generation) { return }
    displayState = .image
    isPreservingDisplayedImage = false
    requestActive = false
    skeletonIndicator.stopAnimatingIndicator()
    fallbackLayer.isHidden = true
    resetImageTransition()
    applyDisplayedImage(image, generation: generation)
    hostView.backgroundColor = .clear
    if terminal, requestIsActive {
      applyLoadedImageTransition(cacheType: .memory)
    }
    applyAutoplay()
    if terminal, requestIsActive {
      let onLoad = onLoad
      let onLoadEnd = onLoadEnd
      Self.deliverTerminalCallbacks(
        primary: {
          onLoad?(Double(image.size.width), Double(image.size.height), .memory)
        },
        onLoadEnd: { onLoadEnd?() }
      )
      guard requestGeneration == generation else { return }
      pendingDisplayGeneration = generation
      schedulePendingDisplayIfNeeded()
    }
  }

  private static func canUseAsMemoryPreview(_ image: UIImage) -> Bool {
    !(image is SDAnimatedImage) && image.images == nil
  }

  private func showError() {
    showTerminalState(.error)
  }

  private func showFallback() {
    showTerminalState(.fallback)
  }

  private func showTerminalState(_ state: DisplayState) {
    displayState = state
    requestActive = false
    isPreservingDisplayedImage = false
    skeletonIndicator.stopAnimatingIndicator()
    resetImageTransition()
    hostView.image = nil
    let hidesTerminalState = loadingStrategy == .none
    hostView.backgroundColor = hidesTerminalState ? .clear : resolvedPlaceholderColor
    fallbackLayer.string = stateSymbol
    fallbackLayer.fontSize = min(hostView.bounds.width, hostView.bounds.height) * 0.35
    layoutFallbackLayer()
    fallbackLayer.foregroundColor = UIColor.secondaryLabel.cgColor
    fallbackLayer.isHidden = hidesTerminalState
  }

  private func applyVariant() {
    switch displayState {
    case .image:
      break
    case .loading:
      // A preserved image is the placeholder; re-applying the loading look
      // would cover it with the skeleton / placeholder color again.
      if isPreservingDisplayedImage { break }
      applyLoadingAppearance(letLibraryStartIndicator: false)
    case .error, .fallback:
      let hidesTerminalState = loadingStrategy == .none
      hostView.backgroundColor = hidesTerminalState ? .clear : resolvedPlaceholderColor
      fallbackLayer.string = stateSymbol
      fallbackLayer.isHidden = hidesTerminalState
    }
  }

  private func applyLoadingAppearance(letLibraryStartIndicator: Bool) {
    let strategy = loadingStrategy ?? .static
    hostView.backgroundColor = strategy == .none ? .clear : resolvedPlaceholderColor

    if strategy == .skeleton {
      skeletonIndicator.updateColors(skeletonGradientColors)
      if hostView.sd_imageIndicator !== skeletonIndicator {
        hostView.sd_imageIndicator = skeletonIndicator
      }
      skeletonIndicator.updateFrame(hostView.bounds)
      if requestActive, !letLibraryStartIndicator, !UIAccessibility.isReduceMotionEnabled {
        skeletonIndicator.startAnimatingIndicator()
      } else if UIAccessibility.isReduceMotionEnabled {
        skeletonIndicator.stopAnimatingIndicator()
      }
    } else {
      skeletonIndicator.stopAnimatingIndicator()
      if hostView.sd_imageIndicator != nil {
        hostView.sd_imageIndicator = nil
      }
    }
  }

  private func layoutFallbackLayer() {
    let height = max(fallbackLayer.fontSize * 1.3, 1)
    fallbackLayer.frame = CGRect(
      x: 0,
      y: (hostView.bounds.height - height) / 2,
      width: hostView.bounds.width,
      height: height
    )
  }

  private var resolvedPlaceholderColor: UIColor {
    if let color = Self.color(from: placeholderColor) { return color }
    // OneKey patch: fall back to a semantic system fill instead of a light-only color.
    // switch variant ?? .generic {
    // case .generic: return UIColor(white: 0.91, alpha: 1)
    // case .token: return UIColor(red: 0.91, green: 0.93, blue: 0.98, alpha: 1)
    // case .network: return UIColor(red: 0.90, green: 0.94, blue: 0.96, alpha: 1)
    // case .avatar: return UIColor(red: 0.92, green: 0.92, blue: 0.94, alpha: 1)
    // }
    return .secondarySystemFill
  }

  private var skeletonGradientColors: [UIColor] {
    let base = resolvedPlaceholderColor
    let resolvedBase = base.resolvedColor(with: hostView.traitCollection)
    var red: CGFloat = 0
    var green: CGFloat = 0
    var blue: CGFloat = 0
    var alpha: CGFloat = 0
    guard resolvedBase.getRed(&red, green: &green, blue: &blue, alpha: &alpha) else {
      return [base, base]
    }
    let average = (red + green + blue) / 3
    let delta: CGFloat = average < 0.5 ? 18.0 / 255.0 : -12.0 / 255.0
    let highlight = UIColor(
      red: min(max(red + delta, 0), 1),
      green: min(max(green + delta, 0), 1),
      blue: min(max(blue + delta, 0), 1),
      alpha: alpha
    )
    return [base, highlight]
  }

  private func applyLoadedImageTransition(cacheType: OneKeyImageCacheType) {
    resetImageTransition()
    guard cacheType != .memory,
          let requestStartedAt,
          CACurrentMediaTime() - requestStartedAt >= Self.fadeDelayThreshold,
          !UIAccessibility.isReduceMotionEnabled else {
      return
    }
    hostView.alpha = 0
    UIView.animate(
      withDuration: Self.fadeDuration,
      delay: 0,
      options: [.allowUserInteraction, .beginFromCurrentState, .curveEaseOut]
    ) {
      self.hostView.alpha = 1
    }
  }

  private func resetImageTransition() {
    hostView.layer.removeAllAnimations()
    hostView.alpha = 1
  }

  private static func color(from value: String?) -> UIColor? {
    guard var hex = value?.trimmingCharacters(in: .whitespacesAndNewlines),
          hex.hasPrefix("#") else {
      return nil
    }
    hex.removeFirst()
    guard hex.count == 6 || hex.count == 8,
          let parsed = UInt64(hex, radix: 16) else {
      return nil
    }
    let red = CGFloat((parsed >> (hex.count == 8 ? 24 : 16)) & 0xFF) / 255
    let green = CGFloat((parsed >> (hex.count == 8 ? 16 : 8)) & 0xFF) / 255
    let blue = CGFloat((parsed >> (hex.count == 8 ? 8 : 0)) & 0xFF) / 255
    let alpha = hex.count == 8 ? CGFloat(parsed & 0xFF) / 255 : 1
    return UIColor(red: red, green: green, blue: blue, alpha: alpha)
  }

  private var stateSymbol: String {
    switch variant ?? .generic {
    case .generic: return "◇"
    case .token: return "◈"
    case .network: return "◎"
    case .avatar: return "●"
    }
  }

  private func applyContentFit() {
    switch contentFit ?? .cover {
    case .cover: hostView.contentMode = .scaleAspectFill
    case .contain: hostView.contentMode = .scaleAspectFit
    case .fill: hostView.contentMode = .scaleToFill
    case .center: hostView.contentMode = .center
    }
  }

  private func applyAutoplay() {
    hostView.autoPlayAnimatedImage = autoplay ?? true
    if autoplay == false || !attached {
      hostView.stopAnimating()
    } else {
      hostView.startAnimating()
    }
  }

  private static func cacheType(_ cacheType: SDImageCacheType) -> OneKeyImageCacheType {
    switch cacheType {
    case .none: return .none
    case .memory: return .memory
    case .disk, .all: return .disk
    @unknown default: return .none
    }
  }

  private static func equalOptionalDouble(_ lhs: Double?, _ rhs: Double?) -> Bool {
    switch (lhs, rhs) {
    case (nil, nil): return true
    case (let lhs?, let rhs?): return lhs == rhs || (lhs.isNaN && rhs.isNaN)
    default: return false
    }
  }

  static func isCurrentRequestGeneration(_ generation: UInt64, current: UInt64) -> Bool {
    generation == current
  }

  static func canFinishTerminal(
    _ generation: UInt64,
    current: UInt64,
    terminalGeneration: UInt64?
  ) -> Bool {
    generation == current && terminalGeneration != generation
  }

  static func deliverTerminalCallbacks(
    primary: () -> Void,
    onLoadEnd: () -> Void
  ) {
    primary()
    onLoadEnd()
  }

  static func shouldRetryRaw(
    mayFallbackToRaw: Bool,
    safetyViolation: OneKeyImageSafetyViolation?
  ) -> Bool {
    mayFallbackToRaw && safetyViolation == nil
  }
}
