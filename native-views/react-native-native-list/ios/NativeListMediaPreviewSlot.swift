import AVFoundation
import OneKeyImage
import UIKit

private final class NativeListPausedVideoView: UIView {
  var onWindowChanged: (() -> Void)?
  var onLayoutChanged: (() -> Void)?
  override class var layerClass: AnyClass { AVPlayerLayer.self }
  var playerLayer: AVPlayerLayer { layer as! AVPlayerLayer }
  override func didMoveToWindow() { super.didMoveToWindow(); onWindowChanged?() }
  override func layoutSubviews() { super.layoutSubviews(); onLayoutChanged?() }
}

/// Uses the same AVPlayer source semantics as native Video, always paused and muted. A player
/// lives only until its first frame is copied; the tile then shows that still frame.
final class NativeListMediaPreviewSlot {
  let view = UIView()
  private let image = NativeListImageSlot()
  private let video = NativeListPausedVideoView()
  private let frameView = UIImageView()
  private final class PendingFrame {
    private let lock = NSLock()
    private var buffer: CVPixelBuffer?
    init(_ buffer: CVPixelBuffer) { self.buffer = buffer }
    func take() -> CVPixelBuffer? { lock.lock(); defer { lock.unlock() }; let value = buffer; buffer = nil; return value }
    func cancel() { lock.lock(); buffer = nil; lock.unlock() }
  }
  private var pendingFrame: PendingFrame?
  private var requestedEdge: Int?
  private var subscription: OneKeyMediaPreviewCache.Subscription?
  private var extractionLease: UUID?
  private static let frameWorker = DispatchQueue(label: "onekey.native-list.frame", qos: .userInitiated)
  private var videoOutput: AVPlayerItemVideoOutput?
  private static let frameContext = CIContext(options: [.cacheIntermediates: false])
  private static let frameCaptureAttempts = 6
  private var identity: String?
  private var epoch = 0
  private var candidateGeneration = 0
  private var playerGeneration = 0
  private var result: Bool?
  private var completion: ((Bool) -> Void)?
  private(set) var createdPlayerCount = 0
  private var player: AVPlayer?
  private var itemObservation: NSKeyValueObservation?
  private var readyObservation: NSKeyValueObservation?
  private var resumeVideo: (() -> Void)?
  private var lifecycleObservers: [NSObjectProtocol] = []
  private var viewportObservations: [NSKeyValueObservation] = []

  init() {
    view.isUserInteractionEnabled = false
    for child in [image.view, video] {
      child.frame = view.bounds
      child.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      view.addSubview(child)
    }
    video.clipsToBounds = true
    video.isHidden = true
    frameView.frame = video.bounds
    frameView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    frameView.isHidden = true
    video.addSubview(frameView)
    video.onWindowChanged = { [weak self] in self?.observeViewport(); self?.updateVisibility() }
    video.onLayoutChanged = { [weak self] in self?.updateVisibility() }
    lifecycleObservers = [
      NotificationCenter.default.addObserver(forName: UIApplication.willResignActiveNotification, object: nil, queue: .main) { [weak self] _ in self?.cancelPreview() },
      NotificationCenter.default.addObserver(forName: UIApplication.didBecomeActiveNotification, object: nil, queue: .main) { [weak self] _ in self?.updateVisibility() },
    ]
  }

  func bind(
    _ source: [String: Any], key: String, fit: String? = nil,
    placeholder: String = "#0000000F", probeOrder: [String] = ["image"],
    completion: ((Bool) -> Void)? = nil
  ) {
    let next = NativeListImageSlot.signature([
      "source": source, "key": key, "fit": fit ?? "", "order": probeOrder,
      "placeholder": placeholder,
    ])
    self.completion = completion
    if identity == next {
      if let result { completion?(result) }
      updateVisibility()
      return
    }
    recycle()
    identity = next
    self.completion = completion
    let cover = (fit ?? source.string("contentFit")) == "cover"
    video.playerLayer.videoGravity = cover ? .resizeAspectFill : .resizeAspect
    frameView.contentMode = cover ? .scaleAspectFill : .scaleAspectFit
    probe(source, key: key, fit: fit, placeholder: placeholder, order: probeOrder, attempt: 0, token: epoch)
  }

  private func probe(
    _ source: [String: Any], key: String, fit: String?, placeholder: String,
    order: [String], attempt: Int, token: Int
  ) {
    guard token == epoch else { return }
    candidateGeneration &+= 1
    let candidate = candidateGeneration
    resumeVideo = nil
    viewportObservations.removeAll()
    cancelPreview()
    guard attempt < order.count else {
      result = false
      completion?(false)
      return
    }
    let finished: (Bool) -> Void = { [weak self] loaded in
      guard let self, self.epoch == token, self.candidateGeneration == candidate else { return }
      if loaded {
        guard self.result != true else { return }
        self.result = true
        self.completion?(true)
      } else {
        self.probe(source, key: key, fit: fit, placeholder: placeholder, order: order, attempt: attempt + 1, token: token)
      }
    }
    if order[attempt] == "image" {
      video.isHidden = true
      image.view.isHidden = false
      image.bind(source, key: key, fit: fit, placeholder: placeholder, completion: finished)
      return
    }
    image.recycle()
    image.view.isHidden = true
    video.isHidden = false
    guard let url = URL(string: source.string("uri")), !source.string("uri").isEmpty else { finished(false); return }
    let headers = (source.dictionary("headers") ?? [:]).compactMapValues { $0 as? String }
    let fitKey = coverFit(fit ?? source.string("contentFit"))
    resumeVideo = { [weak self] in
      guard let self, self.epoch == token, self.candidateGeneration == candidate,
        self.subscription == nil, self.frameView.image == nil, !self.video.bounds.isEmpty else { return }
      let edge = OneKeyMediaPreviewCache.targetEdge(max(self.video.bounds.width, self.video.bounds.height) * (self.video.window?.screen.scale ?? 1))
      self.requestedEdge = edge
      self.subscription = OneKeyMediaPreviewCache.shared.request(uri: url.absoluteString, headers: headers,
        edge: edge, fit: fitKey, policy: source.string("cachePolicy", default: "memory-disk"),
        onImage: { [weak self] still in
          guard let self, self.epoch == token, self.candidateGeneration == candidate else { return }
          self.subscription = nil
          self.frameView.image = still; self.frameView.isHidden = false
          self.viewportObservations.removeAll()
          finished(true)
        }, onExtract: { [weak self] lease in
          guard let self, self.epoch == token, self.candidateGeneration == candidate else { return }
          self.extractionLease = lease
          self.updateVisibility()
          guard self.extractionLease == lease, self.requestedEdge == edge else { return }
          self.startPlayer(url: url, headers: headers, edge: edge, lease: lease)
        }, onRevoked: { [weak self] in
          self?.extractionLease = nil; self?.pendingFrame?.cancel(); self?.pendingFrame = nil; self?.releasePlayer()
        },
        onFailure: { [weak self] in
          guard let self, self.epoch == token, self.candidateGeneration == candidate else { return }
          self.subscription = nil; finished(false)
        })
    }
    observeViewport()
    updateVisibility()
  }

  private func coverFit(_ fit: String) -> String { fit == "cover" ? "cover" : "contain" }

  private func startPlayer(url: URL, headers: [String: String], edge: Int, lease: UUID) {
    createdPlayerCount += 1
    self.playerGeneration &+= 1
    let generation = self.playerGeneration
    let asset = AVURLAsset(url: url, options: ["AVURLAssetHTTPHeaderFieldsKey": headers])
    let item = AVPlayerItem(asset: asset)
    item.preferredForwardBufferDuration = 1
    let output = AVPlayerItemVideoOutput(pixelBufferAttributes: [
      kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA,
    ])
    item.add(output)
    self.videoOutput = output
    let player = AVPlayer(playerItem: item)
    player.isMuted = true
    player.pause()
    self.player = player
    self.video.playerLayer.player = player
    self.itemObservation = item.observe(\.status, options: [.initial, .new]) { [weak self] item, _ in
      DispatchQueue.main.async {
        guard let self, self.playerGeneration == generation else { return }
        if item.status == .failed { OneKeyMediaPreviewCache.shared.fail(lease) }
      }
    }
    self.readyObservation = self.video.playerLayer.observe(\.isReadyForDisplay, options: [.initial, .new]) { [weak self] layer, _ in
      DispatchQueue.main.async {
        guard let self, self.playerGeneration == generation, layer.isReadyForDisplay else { return }
        self.readyObservation = nil
        self.captureFirstFrame(generation: generation, attemptsLeft: Self.frameCaptureAttempts, edge: edge, lease: lease)
      }
    }
  }

  /// Capture only the buffer on UI; CI normalizes orientation and size on a worker.
  private func captureFirstFrame(generation: Int, attemptsLeft: Int, edge: Int, lease: UUID) {
    guard playerGeneration == generation, let player, let item = player.currentItem, let output = videoOutput else { return }
    let time = item.currentTime()
    if let buffer = output.copyPixelBuffer(forItemTime: time, itemTimeForDisplay: nil) {
      let transform = item.tracks.lazy.compactMap(\.assetTrack).first { $0.mediaType == .video }?.preferredTransform ?? .identity
      releasePlayer()
      let pending = PendingFrame(buffer)
      pendingFrame = pending
      Self.frameWorker.async { [weak self] in
        guard let buffer = pending.take() else { return }
        var image = CIImage(cvPixelBuffer: buffer).transformed(by: transform)
        let origin = image.extent.origin
        image = image.transformed(by: CGAffineTransform(translationX: -origin.x, y: -origin.y))
        let scale = min(1, CGFloat(edge) / max(image.extent.width, image.extent.height))
        image = image.transformed(by: CGAffineTransform(scaleX: scale, y: scale))
        let still = Self.frameContext.createCGImage(image, from: image.extent).map { UIImage(cgImage: $0) }
        DispatchQueue.main.async {
          guard let self, self.extractionLease == lease else { return }
          if let still { OneKeyMediaPreviewCache.shared.complete(lease, image: still) }
          else { OneKeyMediaPreviewCache.shared.fail(lease) }
        }
      }
      return
    }
    guard attemptsLeft > 1 else { OneKeyMediaPreviewCache.shared.fail(lease); return }
    if attemptsLeft == Self.frameCaptureAttempts { player.seek(to: time, toleranceBefore: .zero, toleranceAfter: .zero) }
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) { [weak self] in
      self?.captureFirstFrame(generation: generation, attemptsLeft: attemptsLeft - 1, edge: edge, lease: lease)
    }
  }

  private func observeViewport() {
    viewportObservations.removeAll()
    guard resumeVideo != nil, frameView.image == nil, video.window != nil else { return }
    var ancestor = video.superview
    while let current = ancestor {
      if let scrollView = current as? UIScrollView {
        viewportObservations.append(scrollView.observe(\.contentOffset, options: [.new]) { [weak self] _, _ in self?.updateVisibility() })
        viewportObservations.append(scrollView.observe(\.bounds, options: [.new]) { [weak self] _, _ in self?.updateVisibility() })
      }
      // Pager or host layouts may translate a retained page without scrolling it.
      viewportObservations.append(current.layer.observe(\.transform, options: [.new]) { [weak self] _, _ in self?.updateVisibility() })
      viewportObservations.append(current.layer.observe(\.position, options: [.new]) { [weak self] _, _ in self?.updateVisibility() })
      viewportObservations.append(current.layer.observe(\.isHidden, options: [.new]) { [weak self] _, _ in self?.updateVisibility() })
      viewportObservations.append(current.layer.observe(\.opacity, options: [.new]) { [weak self] _, _ in self?.updateVisibility() })
      ancestor = current.superview
    }
  }

  var hasLivePlayer: Bool { player != nil }
  var capturedFrame: UIImage? { frameView.image }

  /// Retained pager pages can have a window while lying outside its clipped viewport.
  static func intersectsViewport(_ view: UIView) -> Bool {
    guard let window = view.window, !view.bounds.isEmpty else { return false }
    var visible = view.convert(view.bounds, to: window).intersection(window.bounds)
    var ancestor: UIView? = view
    while let current = ancestor {
      if current.isHidden || current.alpha <= 0.01 { return false }
      if current.clipsToBounds || current is UIScrollView {
        visible = visible.intersection(current.convert(current.bounds, to: window))
      }
      if visible.isNull || visible.isEmpty { return false }
      ancestor = current.superview
    }
    return true
  }

  private func updateVisibility() {
    if let requestedEdge, let window = video.window, !video.bounds.isEmpty {
      let edge = OneKeyMediaPreviewCache.targetEdge(max(video.bounds.width, video.bounds.height) * window.screen.scale)
      if edge != requestedEdge {
        cancelPreview(); self.requestedEdge = nil
        frameView.image = nil; frameView.isHidden = true; result = nil
        observeViewport()
      }
    }
    if Self.intersectsViewport(video) && UIApplication.shared.applicationState == .active { resumeVideo?() }
    else { cancelPreview() }
  }
  private func cancelPreview() {
    let previous = subscription
    subscription = nil
    OneKeyMediaPreviewCache.shared.cancel(previous)
    extractionLease = nil
    pendingFrame?.cancel(); pendingFrame = nil
    releasePlayer()
  }
  private func releasePlayer() {
    playerGeneration &+= 1
    itemObservation = nil
    readyObservation = nil
    videoOutput = nil
    player?.pause()
    video.playerLayer.player = nil
    player?.replaceCurrentItem(with: nil)
    player = nil
  }
  func recycle() {
    epoch &+= 1
    candidateGeneration &+= 1
    resumeVideo = nil
    viewportObservations.removeAll()
    cancelPreview()
    image.recycle()
    image.view.isHidden = false
    video.isHidden = true
    requestedEdge = nil
    frameView.image = nil
    frameView.isHidden = true
    identity = nil
    result = nil
    completion = nil
  }
  deinit {
    pendingFrame?.cancel()
    let pending = subscription
    DispatchQueue.main.async { OneKeyMediaPreviewCache.shared.cancel(pending) }
    lifecycleObservers.forEach(NotificationCenter.default.removeObserver)
    player?.pause()
    player?.replaceCurrentItem(with: nil)
  }
}
