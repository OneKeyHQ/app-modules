import AVFoundation
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
  private var videoOutput: AVPlayerItemVideoOutput?
  private static let frameContext = CIContext(options: [.cacheIntermediates: false])
  private static let frameCaptureAttempts = 6
  private var identity: String?
  private var epoch = 0
  private var candidateGeneration = 0
  private var playerGeneration = 0
  private var result: Bool?
  private var completion: ((Bool) -> Void)?
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
      NotificationCenter.default.addObserver(forName: UIApplication.willResignActiveNotification, object: nil, queue: .main) { [weak self] _ in self?.releasePlayer() },
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
    releasePlayer()
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
    resumeVideo = { [weak self] in
      guard let self, self.epoch == token, self.candidateGeneration == candidate, self.player == nil else { return }
      self.playerGeneration &+= 1
      let generation = self.playerGeneration
      let asset = AVURLAsset(url: url, options: ["AVURLAssetHTTPHeaderFieldsKey": source.dictionary("headers") ?? [:]])
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
          if item.status == .failed { finished(false) }
        }
      }
      self.readyObservation = self.video.playerLayer.observe(\.isReadyForDisplay, options: [.initial, .new]) { [weak self] layer, _ in
        DispatchQueue.main.async {
          guard let self, self.playerGeneration == generation else { return }
          guard layer.isReadyForDisplay else { return }
          finished(true)
          self.captureFirstFrame(generation: generation, attemptsLeft: Self.frameCaptureAttempts)
        }
      }
    }
    observeViewport()
    updateVisibility()
  }

  /// A paused item may not have handed its preroll frame to the output yet, so copying retries
  /// on later runloop turns. If no frame arrives the paused player stays attached.
  private func captureFirstFrame(generation: Int, attemptsLeft: Int) {
    guard playerGeneration == generation, let player, let item = player.currentItem, let output = videoOutput else { return }
    let time = item.currentTime()
    if let buffer = output.copyPixelBuffer(forItemTime: time, itemTimeForDisplay: nil),
       let still = Self.frameContext.createCGImage(CIImage(cvPixelBuffer: buffer), from: CGRect(
         x: 0, y: 0, width: CVPixelBufferGetWidth(buffer), height: CVPixelBufferGetHeight(buffer))) {
      let transform = item.tracks.lazy.compactMap(\.assetTrack).first { $0.mediaType == .video }?.preferredTransform
      frameView.image = UIImage(cgImage: still, scale: 1, orientation: Self.orientation(transform))
      frameView.isHidden = false
      resumeVideo = nil
      viewportObservations.removeAll()
      releasePlayer()
      return
    }
    guard attemptsLeft > 1 else { return }
    // An exact seek re-delivers the paused preroll frame to newly attached outputs.
    if attemptsLeft == Self.frameCaptureAttempts {
      player.seek(to: time, toleranceBefore: .zero, toleranceAfter: .zero)
    }
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) { [weak self] in
      self?.captureFirstFrame(generation: generation, attemptsLeft: attemptsLeft - 1)
    }
  }

  private static func orientation(_ transform: CGAffineTransform?) -> UIImage.Orientation {
    guard let transform else { return .up }
    switch (transform.a, transform.b, transform.c, transform.d) {
    case (0, 1, -1, 0): return .right
    case (0, -1, 1, 0): return .left
    case (-1, 0, 0, -1): return .down
    default: return .up
    }
  }

  private func observeViewport() {
    viewportObservations.removeAll()
    guard resumeVideo != nil, video.window != nil else { return }
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
    if Self.intersectsViewport(video) && UIApplication.shared.applicationState == .active { resumeVideo?() }
    else { releasePlayer() }
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
    releasePlayer()
    image.recycle()
    image.view.isHidden = false
    video.isHidden = true
    frameView.image = nil
    frameView.isHidden = true
    identity = nil
    result = nil
    completion = nil
  }
  deinit {
    lifecycleObservers.forEach(NotificationCenter.default.removeObserver)
    player?.pause()
    player?.replaceCurrentItem(with: nil)
  }
}
