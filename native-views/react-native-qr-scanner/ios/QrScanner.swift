import AVFoundation
import NitroModules
import UIKit

final class QrPreviewView: UIView {
  override class var layerClass: AnyClass { AVCaptureVideoPreviewLayer.self }

  var previewLayer: AVCaptureVideoPreviewLayer {
    // swiftlint:disable:next force_cast
    layer as! AVCaptureVideoPreviewLayer
  }

  private let focusIndicator: UIView = {
    let view = UIView(frame: CGRect(x: 0, y: 0, width: 72, height: 72))
    view.layer.borderColor = UIColor.white.cgColor
    view.layer.borderWidth = 1.5
    view.layer.cornerRadius = 8
    view.isUserInteractionEnabled = false
    view.alpha = 0
    return view
  }()

  override init(frame: CGRect) {
    super.init(frame: frame)
    backgroundColor = .black
    previewLayer.videoGravity = .resizeAspectFill
    addSubview(focusIndicator)
  }

  /// Shows the tap-to-focus box at `point` and fades it out.
  func showFocusIndicator(at point: CGPoint) {
    focusIndicator.layer.removeAllAnimations()
    focusIndicator.center = point
    focusIndicator.alpha = 1
    focusIndicator.transform = CGAffineTransform(scaleX: 1.25, y: 1.25)
    UIView.animate(withDuration: 0.2) {
      self.focusIndicator.transform = .identity
    }
    UIView.animate(withDuration: 0.3, delay: 0.8, options: [.beginFromCurrentState]) {
      self.focusIndicator.alpha = 0
    }
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) is not supported")
  }
}

class HybridQrScanner: HybridQrScannerSpec {
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
  /// The configured input device. Owned by `sessionQueue`.
  private var device: AVCaptureDevice?
  /// Zoom factor when the current pinch began. Owned by `sessionQueue`.
  private var pinchStartZoom: CGFloat = 1
  private lazy var gestureHandler = GestureHandler(owner: self)
  private var observers: [NSObjectProtocol] = []
  private lazy var metadataDelegate = MetadataDelegate(owner: self)

  // Shared by `sessionQueue` (start/stop) and `metadataQueue` (detections).
  // The same text is reported again after 1 s, so a caller that reset its
  // session still receives a code that stays in view.
  private let gate = QrFrameTextGate(repeatInterval: 1.0)

  override init() {
    super.init()
    previewView.previewLayer.session = session
    let tap = UITapGestureRecognizer(target: gestureHandler, action: #selector(GestureHandler.tap(_:)))
    let pinch = UIPinchGestureRecognizer(
      target: gestureHandler, action: #selector(GestureHandler.pinch(_:)))
    previewView.addGestureRecognizer(tap)
    previewView.addGestureRecognizer(pinch)
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
    guard QrCameraControl.preferredBackCamera() != nil else {
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
    guard let device = QrCameraControl.preferredBackCamera() else {
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
      // Virtual devices start on their widest lens; begin at the familiar 1x.
      device.videoZoomFactor = QrCameraControl.wideAngleZoomFactor(for: device)
      Self.applyContinuousFocus(device)
      device.unlockForConfiguration()
    }
    self.device = device
    return nil
  }

  /// Continuous focus and exposure over the whole frame. Caller holds the lock.
  private static func applyContinuousFocus(_ device: AVCaptureDevice) {
    let center = CGPoint(x: 0.5, y: 0.5)
    if device.isFocusPointOfInterestSupported { device.focusPointOfInterest = center }
    if device.isFocusModeSupported(.continuousAutoFocus) {
      device.focusMode = .continuousAutoFocus
    }
    if device.isAutoFocusRangeRestrictionSupported {
      // Codes are held close to the lens.
      device.autoFocusRangeRestriction = .near
    }
    if device.isExposurePointOfInterestSupported { device.exposurePointOfInterest = center }
    if device.isExposureModeSupported(.continuousAutoExposure) {
      device.exposureMode = .continuousAutoExposure
    }
    device.isSubjectAreaChangeMonitoringEnabled = false
  }

  // MARK: - Gestures (main thread in, sessionQueue for the device)

  fileprivate func handleTap(_ recognizer: UITapGestureRecognizer) {
    let layerPoint = recognizer.location(in: previewView)
    let devicePoint = previewView.previewLayer.captureDevicePointConverted(fromLayerPoint: layerPoint)
    previewView.showFocusIndicator(at: layerPoint)
    sessionQueue.async { [weak self] in
      guard let self, self.running, let device = self.device,
        (try? device.lockForConfiguration()) != nil
      else { return }
      defer { device.unlockForConfiguration() }
      if device.isFocusPointOfInterestSupported, device.isFocusModeSupported(.autoFocus) {
        device.focusPointOfInterest = devicePoint
        device.focusMode = .autoFocus
      }
      if device.isExposurePointOfInterestSupported,
        device.isExposureModeSupported(.continuousAutoExposure)
      {
        device.exposurePointOfInterest = devicePoint
        device.exposureMode = .continuousAutoExposure
      }
      // Moving the camera afterwards hands focus back to continuous mode.
      device.isSubjectAreaChangeMonitoringEnabled = true
    }
  }

  fileprivate func handlePinch(_ recognizer: UIPinchGestureRecognizer) {
    let state = recognizer.state
    let scale = recognizer.scale
    sessionQueue.async { [weak self] in
      guard let self, let device = self.device else { return }
      if state == .began {
        self.pinchStartZoom = device.videoZoomFactor
        return
      }
      guard state == .changed, (try? device.lockForConfiguration()) != nil else { return }
      defer { device.unlockForConfiguration() }
      device.videoZoomFactor = QrCameraControl.clampZoom(
        self.pinchStartZoom * scale,
        minFactor: device.minAvailableVideoZoomFactor,
        maxFactor: device.maxAvailableVideoZoomFactor,
        wideFactor: QrCameraControl.wideAngleZoomFactor(for: device))
    }
  }

  private func resumeContinuousFocus() {
    sessionQueue.async { [weak self] in
      guard let self, let device = self.device, (try? device.lockForConfiguration()) != nil
      else { return }
      Self.applyContinuousFocus(device)
      device.unlockForConfiguration()
    }
  }

  private func addObservers() {
    let center = NotificationCenter.default
    if let device {
      observers.append(
        center.addObserver(
          forName: AVCaptureDevice.subjectAreaDidChangeNotification, object: device, queue: nil
        ) { [weak self] _ in
          self?.resumeContinuousFocus()
        })
    }
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

  private func nextGeneration() -> Int { gate.nextGeneration() }

  private func setCapturing(_ value: Bool) { gate.setCapturing(value) }

  private func currentGeneration() -> Int { gate.currentGeneration }

  private func emitError(_ code: String, _ message: String, _ generation: Int) {
    onError?(code, message, Double(generation))
  }

  /// Called on `metadataQueue`.
  fileprivate func handle(texts: [String]) {
    for report in gate.filter(texts, now: ProcessInfo.processInfo.systemUptime) {
      onFrameText?(report.text, Double(report.generation))
    }
  }
}

private final class GestureHandler: NSObject {
  private weak var owner: HybridQrScanner?

  init(owner: HybridQrScanner) {
    self.owner = owner
  }

  @objc func tap(_ recognizer: UITapGestureRecognizer) {
    guard recognizer.state == .ended else { return }
    owner?.handleTap(recognizer)
  }

  @objc func pinch(_ recognizer: UIPinchGestureRecognizer) {
    owner?.handlePinch(recognizer)
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
