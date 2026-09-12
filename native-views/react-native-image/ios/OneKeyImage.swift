import Foundation
import NitroModules
import SDWebImage
import Skeleton
import UIKit

private final class OneKeyImageHostView: SDAnimatedImageView {
  var onLayout: (() -> Void)?
  var onWindowChanged: ((Bool) -> Void)?

  override func layoutSubviews() {
    super.layoutSubviews()
    onLayout?()
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    onWindowChanged?(window != nil)
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
  private var requestStartedAt: CFTimeInterval?

  var view: UIView { hostView }

  var sourceUri: String? {
    didSet { if sourceUri != oldValue { identityDidChange() } }
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
  var resizeWidth: Double? {
    didSet {
      if !Self.equalOptionalDouble(resizeWidth, oldValue) { identityDidChange() }
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
        if !self.showMemoryCachedImageIfAvailable(requestIsActive: false) {
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
      showLoading(requestIsActive: false, letLibraryStartIndicator: false)
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
    if showMemoryCachedImageIfAvailable(requestIsActive: true) { return }
    showLoading(requestIsActive: true, letLibraryStartIndicator: true)

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
    let displaySize = resizeWidth.flatMap { $0.isFinite && $0 > 0 ? CGFloat($0) : nil }
      ?? max(hostView.bounds.width, hostView.bounds.height)
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
      mayFallbackToRaw: requestURL != rawURL
    )
  }

  private func performRequest(
    url: URL,
    rawURL: URL,
    generation: UInt64,
    mayFallbackToRaw: Bool
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
      placeholderImage: nil,
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
              mayFallbackToRaw: false
            )
            return
          }
          self.finishWithError(safetyViolation ?? error, generation: generation)
          return
        }
        guard self.claimTerminal(generation) else { return }
        self.requestActive = false
        self.displayState = .image
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
    resizeWidth = nil
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
    hostView.image = image
  }

  private func showLoading(
    requestIsActive: Bool,
    letLibraryStartIndicator: Bool
  ) {
    displayState = .loading
    requestActive = requestIsActive
    resetImageTransition()
    hostView.image = nil
    fallbackLayer.isHidden = true
    applyLoadingAppearance(letLibraryStartIndicator: letLibraryStartIndicator)
  }

  private func showMemoryCachedImageIfAvailable(requestIsActive: Bool) -> Bool {
    guard cachePolicy == .memory || cachePolicy == .memoryDisk,
          let sourceUri,
          let rawURL = URL(string: sourceUri) else {
      return false
    }
    let rawScreenScale: CGFloat = hostView.window?.screen.scale ?? UIScreen.main.scale
    let screenScale = min(max(rawScreenScale, 1), 3)
    let hasCustomIdentity = OneKeyImageRequestContext.headers(from: sourceHeadersJson) != nil
    let displaySize = resizeWidth.flatMap { $0.isFinite && $0 > 0 ? CGFloat($0) : nil }
      ?? max(hostView.bounds.width, hostView.bounds.height)
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
    let thumbnailPixelSize = OneKeyImageDecodeSizing.thumbnailPixelSize(
      viewSize: hostView.bounds.size,
      scale: screenScale,
      contentFit: contentFit ?? .cover
    )
    func memoryCachedImage(for url: URL) -> UIImage? {
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
    guard let image = memoryCachedImage(for: requestURL)
      ?? (requestURL == rawURL ? nil : memoryCachedImage(for: rawURL)) else {
      return false
    }
    let generation = requestGeneration
    if requestIsActive, !claimTerminal(generation) { return true }
    displayState = .image
    requestActive = false
    skeletonIndicator.stopAnimatingIndicator()
    fallbackLayer.isHidden = true
    resetImageTransition()
    applyDisplayedImage(image, generation: generation)
    hostView.backgroundColor = .clear
    if requestIsActive {
      applyLoadedImageTransition(cacheType: .memory)
    }
    applyAutoplay()
    if requestIsActive {
      let onLoad = onLoad
      let onLoadEnd = onLoadEnd
      Self.deliverTerminalCallbacks(
        primary: {
          onLoad?(Double(image.size.width), Double(image.size.height), .memory)
        },
        onLoadEnd: { onLoadEnd?() }
      )
      guard requestGeneration == generation else { return true }
      pendingDisplayGeneration = generation
      schedulePendingDisplayIfNeeded()
    }
    return true
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
