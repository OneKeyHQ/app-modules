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
    renderer.update(width: bounds.width, height: bounds.height)
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

  private func syncRenderer() {
    if requestedRunning, window != nil, !bounds.isEmpty {
      renderer.update(width: bounds.width, height: bounds.height)
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
    skeletonView.setRunning(true)
  }

  func stopAnimatingIndicator() {
    skeletonView.setRunning(false)
  }

  func updateFrame(_ frame: CGRect) {
    skeletonView.frame = frame
  }
}

final class HybridOneKeyImage: HybridOneKeyImageSpec, RecyclableView {
  private enum DisplayState { case loading, image, error, fallback }

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
  var overscan: Double? = 1.1 {
    didSet {
      if !Self.equalOptionalDouble(overscan, oldValue) { identityDidChange() }
    }
  }
  var loadingStrategy: OneKeyImageLoadingStrategy? = .static { didSet { applyVariant() } }
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
        self.showLoading(requestIsActive: false, letLibraryStartIndicator: false)
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
    showLoading(requestIsActive: true, letLibraryStartIndicator: true)
    onLoadStart?()
    guard Self.isCurrentRequestGeneration(generation, current: requestGeneration) else { return }

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
    let requestURL =
      (optimizeTos ?? true)
      ? OneKeyTosURL.optimized(
        rawURL: rawURL,
        displaySize: max(hostView.bounds.width, hostView.bounds.height),
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
      manager: safetyHandle.manager
    )
    hostView.sd_setImage(
      with: url,
      placeholderImage: nil,
      options: OneKeyImageRequestContext.renderOptions,
      context: context,
      progress: { [weak self] receivedSize, _, _ in
        guard safetyHandle.tracker.inspectReceivedByteCount(Int64(receivedSize)) else { return }
        DispatchQueue.main.async { [weak self] in
          guard let self, self.requestGeneration == generation else { return }
          self.hostView.sd_cancelCurrentImageLoad()
        }
      },
      completed: { [weak self] image, error, cacheType, _ in
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
        self.applyAutoplay()
        let onLoad = self.onLoad
        let onLoadEnd = self.onLoadEnd
        Self.deliverTerminalCallbacks(
          primary: {
            onLoad?(
              Double(image?.size.width ?? 0),
              Double(image?.size.height ?? 0),
              Self.cacheType(cacheType)
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
    overscan = 1.1
    loadingStrategy = .static
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

  private func showLoading(
    requestIsActive: Bool,
    letLibraryStartIndicator: Bool
  ) {
    displayState = .loading
    requestActive = requestIsActive
    hostView.image = nil
    fallbackLayer.isHidden = true
    applyLoadingAppearance(letLibraryStartIndicator: letLibraryStartIndicator)
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
    hostView.image = nil
    hostView.backgroundColor = placeholderColor
    fallbackLayer.string = stateSymbol
    fallbackLayer.fontSize = min(hostView.bounds.width, hostView.bounds.height) * 0.35
    layoutFallbackLayer()
    fallbackLayer.foregroundColor = UIColor.secondaryLabel.cgColor
    fallbackLayer.isHidden = false
  }

  private func applyVariant() {
    switch displayState {
    case .image:
      break
    case .loading:
      applyLoadingAppearance(letLibraryStartIndicator: false)
    case .error, .fallback:
      hostView.backgroundColor = placeholderColor
      fallbackLayer.string = stateSymbol
    }
  }

  private func applyLoadingAppearance(letLibraryStartIndicator: Bool) {
    let strategy = loadingStrategy ?? .static
    hostView.backgroundColor = strategy == .none ? .clear : placeholderColor

    if strategy == .skeleton {
      if hostView.sd_imageIndicator !== skeletonIndicator {
        hostView.sd_imageIndicator = skeletonIndicator
      }
      skeletonIndicator.updateFrame(hostView.bounds)
      if requestActive, !letLibraryStartIndicator {
        skeletonIndicator.startAnimatingIndicator()
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

  private var placeholderColor: UIColor {
    switch variant ?? .generic {
    case .generic: return UIColor(white: 0.91, alpha: 1)
    case .token: return UIColor(red: 0.91, green: 0.93, blue: 0.98, alpha: 1)
    case .network: return UIColor(red: 0.90, green: 0.94, blue: 0.96, alpha: 1)
    case .avatar: return UIColor(red: 0.92, green: 0.92, blue: 0.94, alpha: 1)
    }
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
