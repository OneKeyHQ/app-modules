import AVFoundation
import NitroModules
import UIKit

final class QrPreviewView: UIView {
  override class var layerClass: AnyClass { AVCaptureVideoPreviewLayer.self }

  var previewLayer: AVCaptureVideoPreviewLayer {
    // swiftlint:disable:next force_cast
    layer as! AVCaptureVideoPreviewLayer
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    backgroundColor = .black
    previewLayer.videoGravity = .resizeAspectFill
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) is not supported")
  }
}

class HybridQrScanner: HybridQrScannerSpec {
  /// The same text is reported again after this long, so a caller that reset
  /// its session still receives a code that stays in view.
  private static let repeatInterval: TimeInterval = 1.0

  var view: UIView { previewView }
  private let previewView = QrPreviewView()

  // Props
  var active: Bool = false
  var onReady: ((Double) -> Void)?
  var onFrameText: ((String, Double) -> Void)?
  var onError: ((String, String, Double) -> Void)?

  // Owned by `sessionQueue`.
  private let sessionQueue = DispatchQueue(label: "so.onekey.qrscanner.session")
  private let metadataQueue = DispatchQueue(label: "so.onekey.qrscanner.metadata")
  private let session = AVCaptureSession()
  private var isConfigured = false
  private var running = false
  private var observers: [NSObjectProtocol] = []
  private lazy var metadataDelegate = MetadataDelegate(owner: self)

  // Read on `metadataQueue` and written on `sessionQueue`; guarded by `lock`.
  private let lock = NSLock()
  private var generation = 0
  private var capturing = false
  private var lastText: String?
  private var lastTextAt: TimeInterval = 0

  override init() {
    super.init()
    previewView.previewLayer.session = session
  }

  func afterUpdate() {
    let shouldRun = active
    sessionQueue.async { [weak self] in
      guard let self else { return }
      if shouldRun { self.start() } else { self.stop() }
    }
  }

  func onDropView() {
    sessionQueue.async { [weak self] in
      self?.stop()
      self?.removeObservers()
    }
  }

  // MARK: - Session (sessionQueue)

  private func start() {
    guard !running else { return }
    let currentGeneration = nextGeneration()
    // Check for hardware first: a device without a camera must not prompt.
    guard Self.captureDevice() != nil else {
      emitError("no-camera", "No camera is available", currentGeneration)
      return
    }
    switch AVCaptureDevice.authorizationStatus(for: .video) {
    case .authorized:
      break
    case .notDetermined:
      AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
        self?.sessionQueue.async {
          guard let self, self.currentGeneration() == currentGeneration, self.active else {
            return
          }
          if granted {
            self.start()
          } else {
            self.emitError("permission-denied", "Camera access was denied", currentGeneration)
          }
        }
      }
      return
    default:
      emitError("permission-denied", "Camera access is not allowed", currentGeneration)
      return
    }

    if !isConfigured {
      if let error = configure() {
        emitError(error.code, error.message, currentGeneration)
        return
      }
      isConfigured = true
      addObservers()
    }
    session.startRunning()
    running = session.isRunning
    setCapturing(running)
    if running {
      onReady?(Double(currentGeneration))
    } else {
      emitError("camera-busy", "The camera could not start", currentGeneration)
    }
  }

  private func stop() {
    // A new generation makes late metadata from the old one stale.
    _ = nextGeneration()
    setCapturing(false)
    guard running || session.isRunning else { return }
    session.stopRunning()
    running = false
  }

  private func configure() -> (code: String, message: String)? {
    guard let device = Self.captureDevice() else {
      return ("no-camera", "No camera is available")
    }
    session.beginConfiguration()
    defer { session.commitConfiguration() }
    if session.canSetSessionPreset(.hd1920x1080) {
      session.sessionPreset = .hd1920x1080
    }
    let input: AVCaptureDeviceInput
    do {
      input = try AVCaptureDeviceInput(device: device)
    } catch {
      return ("camera-busy", error.localizedDescription)
    }
    guard session.canAddInput(input) else {
      return ("camera-busy", "The camera input could not be added")
    }
    session.addInput(input)

    let output = AVCaptureMetadataOutput()
    guard session.canAddOutput(output) else {
      return ("decoder-unavailable", "The QR detector could not be added")
    }
    session.addOutput(output)
    guard output.availableMetadataObjectTypes.contains(.qr) else {
      return ("decoder-unavailable", "QR detection is not available")
    }
    output.setMetadataObjectsDelegate(metadataDelegate, queue: metadataQueue)
    output.metadataObjectTypes = [.qr]

    if (try? device.lockForConfiguration()) != nil {
      if device.isFocusModeSupported(.continuousAutoFocus) {
        device.focusMode = .continuousAutoFocus
      }
      if device.isAutoFocusRangeRestrictionSupported {
        // Codes are held close to the lens.
        device.autoFocusRangeRestriction = .near
      }
      device.unlockForConfiguration()
    }
    return nil
  }

  private static func captureDevice() -> AVCaptureDevice? {
    AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back)
      ?? AVCaptureDevice.default(for: .video)
  }

  private func addObservers() {
    let center = NotificationCenter.default
    observers.append(
      center.addObserver(
        forName: AVCaptureSession.wasInterruptedNotification, object: session, queue: nil
      ) { [weak self] note in
        guard let self else { return }
        let reason = (note.userInfo?[AVCaptureSessionInterruptionReasonKey] as? Int)
          .flatMap(AVCaptureSession.InterruptionReason.init(rawValue:))
        if reason == .videoDeviceInUseByAnotherClient {
          self.emitError("camera-busy", "The camera is in use by another app", self.currentGeneration())
        }
      })
    observers.append(
      center.addObserver(
        forName: AVCaptureSession.runtimeErrorNotification, object: session, queue: nil
      ) { [weak self] note in
        guard let self else { return }
        let error = note.userInfo?[AVCaptureSessionErrorKey] as? Error
        self.sessionQueue.async {
          self.running = false
          self.setCapturing(false)
          self.emitError(
            "camera-busy", error?.localizedDescription ?? "The camera stopped",
            self.currentGeneration())
        }
      })
  }

  private func removeObservers() {
    observers.forEach { NotificationCenter.default.removeObserver($0) }
    observers.removeAll()
  }

  // MARK: - Generation and events

  private func nextGeneration() -> Int {
    lock.lock()
    defer { lock.unlock() }
    generation += 1
    lastText = nil
    return generation
  }

  private func setCapturing(_ value: Bool) {
    lock.lock()
    capturing = value
    lock.unlock()
  }

  private func currentGeneration() -> Int {
    lock.lock()
    defer { lock.unlock() }
    return generation
  }

  private func emitError(_ code: String, _ message: String, _ generation: Int) {
    onError?(code, message, Double(generation))
  }

  /// Called on `metadataQueue`.
  fileprivate func handle(texts: [String]) {
    let now = Date().timeIntervalSince1970
    var toEmit: [String] = []
    lock.lock()
    guard capturing else {
      lock.unlock()
      return
    }
    let currentGeneration = generation
    for text in texts where !text.isEmpty {
      if text == lastText, now - lastTextAt < Self.repeatInterval { continue }
      lastText = text
      lastTextAt = now
      toEmit.append(text)
    }
    lock.unlock()
    for text in toEmit {
      onFrameText?(text, Double(currentGeneration))
    }
  }
}

private final class MetadataDelegate: NSObject, AVCaptureMetadataOutputObjectsDelegate {
  private weak var owner: HybridQrScanner?

  init(owner: HybridQrScanner) {
    self.owner = owner
  }

  func metadataOutput(
    _ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject],
    from connection: AVCaptureConnection
  ) {
    let texts = metadataObjects.compactMap {
      ($0 as? AVMetadataMachineReadableCodeObject)?.stringValue
    }
    if !texts.isEmpty {
      owner?.handle(texts: texts)
    }
  }
}
