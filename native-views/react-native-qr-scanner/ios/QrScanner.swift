import AVFoundation
import NitroModules
import UIKit

final class QrPreviewView: UIView {
  override class var layerClass: AnyClass { AVCaptureVideoPreviewLayer.self }

  var previewLayer: AVCaptureVideoPreviewLayer {
    // swiftlint:disable:next force_cast
    layer as! AVCaptureVideoPreviewLayer
  }

  private var sceneGeometryObservation: NSKeyValueObservation?

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
    if #unavailable(iOS 16.0) {
      NotificationCenter.default.addObserver(
        self, selector: #selector(legacyOrientationDidChange(_:)),
        name: UIDevice.orientationDidChangeNotification, object: nil)
      UIDevice.current.beginGeneratingDeviceOrientationNotifications()
    }
  }

  deinit {
    if #unavailable(iOS 16.0) {
      NotificationCenter.default.removeObserver(self)
      DispatchQueue.main.async {
        UIDevice.current.endGeneratingDeviceOrientationNotifications()
      }
    }
  }

  override func didMoveToWindow() {
    super.didMoveToWindow()
    if #available(iOS 16.0, *) {
      // A scene can rotate without changing this view's bounds.
      sceneGeometryObservation = window?.windowScene?.observe(\.effectiveGeometry) {
        [weak self] _, _ in
        self?.scheduleOrientationUpdate()
      }
    }
    updateCaptureOrientation()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    if #unavailable(iOS 16.0) { updateCaptureOrientation() }
  }

  @objc private nonisolated func legacyOrientationDidChange(_: Notification) {
    scheduleOrientationUpdate()
  }

  private nonisolated func scheduleOrientationUpdate() {
    DispatchQueue.main.async { [weak self] in
      self?.updateCaptureOrientation()
    }
  }

  func updateCaptureOrientation() {
    guard let scene = window?.windowScene, let connection = previewLayer.connection else { return }
    let orientation: UIInterfaceOrientation
    if #available(iOS 16.0, *) {
      orientation = scene.effectiveGeometry.interfaceOrientation
    } else {
      orientation = scene.interfaceOrientation
    }
    if #available(iOS 17.0, *) {
      let videoPort = connection.inputPorts.first { $0.mediaType == .video }
      guard let input = videoPort?.input as? AVCaptureDeviceInput,
        let angle = Self.rotationAngle(
          for: orientation, isExternalCamera: input.device.deviceType == .external),
        connection.videoRotationAngle != angle,
        connection.isVideoRotationAngleSupported(angle)
      else { return }
      connection.videoRotationAngle = angle
    } else if connection.isVideoOrientationSupported,
      let captureOrientation = AVCaptureVideoOrientation(rawValue: orientation.rawValue),
      connection.videoOrientation != captureOrientation
    {
      connection.videoOrientation = captureOrientation
    }
  }

  static func rotationAngle(
    for orientation: UIInterfaceOrientation, isExternalCamera: Bool
  ) -> CGFloat? {
    // Reset to the camera's native orientation after switching from a built-in input.
    if isExternalCamera { return 0 }
    switch orientation {
    case .portrait: return 90
    case .portraitUpsideDown: return 270
    case .landscapeLeft: return 180
    case .landscapeRight: return 0
    default: return nil
    }
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
  var cameraId: String?
  var onReady: ((Double) -> Void)?
  var onFrameText: ((String, Double) -> Void)?
  var onError: ((String, String, Double) -> Void)?

  // Owned by `sessionQueue`.
  private let sessionQueue = DispatchQueue(label: "so.onekey.qrscanner.session")
  private let metadataQueue = DispatchQueue(label: "so.onekey.qrscanner.metadata")
  private let session = AVCaptureSession()
  private struct CaptureProps {
    var active = false
    var ready: ((Double) -> Void)?
    var text: ((String, Double) -> Void)?
    var error: ((String, String, Double) -> Void)?
  }

  private var captureProps = CaptureProps()
  private var disposed = false
  private var permissionPending = false
  private var output: AVCaptureMetadataOutput?
  private var metadataDelegate: MetadataDelegate?
  private var running = false
  /// The configured input device. Owned by `sessionQueue`.
  private var device: AVCaptureDevice?
  private var input: AVCaptureDeviceInput?
  /// Last `cameraId` prop value. Owned by `sessionQueue`.
  private var requestedCameraId: String?
  private var subjectAreaObserver: NSObjectProtocol?
  /// Zoom factor when the current pinch began. Owned by `sessionQueue`.
  private var pinchStartZoom: CGFloat = 1
  private lazy var gestureHandler = GestureHandler(owner: self)
  private var observers: [NSObjectProtocol] = []
  private var pinchGeneration: Int?

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
    let props = CaptureProps(active: active, ready: onReady, text: onFrameText, error: onError)
    let cameraId = cameraId
    sessionQueue.async { [weak self] in
      guard let self, !self.disposed else { return }
      let previous = self.captureProps
      let previousCamera = self.requestedCameraId
      self.captureProps = props
      self.requestedCameraId = cameraId
      if !props.active {
        self.stop()
      } else if self.running {
        self.switchCameraIfNeeded()
      } else if !previous.active || cameraId != previousCamera {
        self.start()
      }
    }
  }

  func onDropView() { dispose() }

  func dispose() {
    // Retain the scanner until its queue has released the session and observers.
    sessionQueue.async {
      guard !self.disposed else { return }
      self.disposed = true
      self.captureProps = CaptureProps()
      self.stop()
      self.removeObservers()
    }
  }

  deinit {
    removeObservers()
  }

  // MARK: - Session (sessionQueue)

  private func start() {
    guard !running, !disposed, captureProps.active, !permissionPending else { return }
    let currentGeneration = nextGeneration()
    // Check for hardware first: a device without a camera must not prompt.
    guard QrCameraControl.device(forId: requestedCameraId) != nil else {
      emitError("no-camera", "No camera is available", currentGeneration)
      return
    }
    switch AVCaptureDevice.authorizationStatus(for: .video) {
    case .authorized:
      break
    case .notDetermined:
      permissionPending = true
      AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
        self?.sessionQueue.async {
          guard let self else { return }
          self.permissionPending = false
          guard self.captureProps.active, !self.disposed else { return }
          if granted {
            self.start()
          } else {
            self.emitError("permission-denied", "Camera access was denied", self.currentGeneration())
          }
        }
      }
      return
    default:
      emitError("permission-denied", "Camera access is not allowed", currentGeneration)
      return
    }

    if let error = configureCamera(
      QrCameraControl.device(forId: requestedCameraId), generation: currentGeneration)
    {
      emitError(error.code, error.message, currentGeneration)
      return
    }
    updatePreviewOrientation()
    if observers.isEmpty { addObservers() }
    session.startRunning()
    running = session.isRunning
    setCapturing(running)
    if running {
      captureProps.ready?(Double(currentGeneration))
    } else {
      emitError("camera-busy", "The camera could not start", currentGeneration)
    }
  }

  private func stop() {
    // A new generation makes late metadata from the old one stale.
    _ = nextGeneration()
    pinchGeneration = nil
    output?.setMetadataObjectsDelegate(nil, queue: nil)
    guard running || session.isRunning else { return }
    session.stopRunning()
    running = false
  }

  /// Replaces the input and metadata output in one transaction. A fresh
  /// output/delegate gives queued callbacks immutable capture provenance.
  private func configureCamera(
    _ target: AVCaptureDevice?, generation: Int
  ) -> (code: String, message: String)? {
    guard let target else { return ("no-camera", "No camera is available") }
    let newInput: AVCaptureDeviceInput
    do {
      newInput = try AVCaptureDeviceInput(device: target)
    } catch {
      return ("camera-busy", error.localizedDescription)
    }

    let newOutput = AVCaptureMetadataOutput()
    let delegate = MetadataDelegate(owner: self, generation: generation)
    let oldPreset = session.sessionPreset
    session.beginConfiguration()
    if let output { session.removeOutput(output) }
    if let input { session.removeInput(input) }
    var installed = false
    defer {
      if !installed {
        if session.outputs.contains(newOutput) { session.removeOutput(newOutput) }
        if session.inputs.contains(newInput) { session.removeInput(newInput) }
        session.sessionPreset = oldPreset
        if let input, session.canAddInput(input) { session.addInput(input) }
        if let output, session.canAddOutput(output) { session.addOutput(output) }
      }
      session.commitConfiguration()
    }

    let presets: [AVCaptureSession.Preset] = [.hd1920x1080, .hd1280x720, .high, .medium, .low, .inputPriority]
    if let preset = presets.first(where: {
      target.supportsSessionPreset($0) && session.canSetSessionPreset($0)
    }) {
      session.sessionPreset = preset
    }
    guard session.canAddInput(newInput) else {
      return ("camera-busy", "The camera input could not be added")
    }
    session.addInput(newInput)
    guard session.canAddOutput(newOutput) else {
      return ("decoder-unavailable", "The QR detector could not be added")
    }
    session.addOutput(newOutput)
    guard newOutput.availableMetadataObjectTypes.contains(.qr) else {
      return ("decoder-unavailable", "QR detection is not available")
    }
    newOutput.setMetadataObjectsDelegate(delegate, queue: metadataQueue)
    newOutput.metadataObjectTypes = [.qr]
    output?.setMetadataObjectsDelegate(nil, queue: nil)
    output = newOutput
    metadataDelegate = delegate
    input = newInput
    let changedCamera = target.uniqueID != device?.uniqueID
    device = target
    installed = true

    if changedCamera, (try? target.lockForConfiguration()) != nil {
      target.videoZoomFactor = QrCameraControl.wideAngleZoomFactor(for: target)
      Self.applyContinuousFocus(target)
      target.unlockForConfiguration()
    }
    observeSubjectArea(of: target)
    return nil
  }

  private func switchCameraIfNeeded() {
    guard running, let target = QrCameraControl.device(forId: requestedCameraId),
      target.uniqueID != device?.uniqueID
    else { return }
    let generation = currentGeneration() + 1
    if let error = configureCamera(target, generation: generation) {
      emitError(error.code, error.message, currentGeneration())
      return
    }
    updatePreviewOrientation()
    _ = nextGeneration()
    setCapturing(true)
    captureProps.ready?(Double(generation))
  }

  private func updatePreviewOrientation() {
    DispatchQueue.main.async { [weak previewView] in
      previewView?.updateCaptureOrientation()
    }
  }

  private func observeSubjectArea(of device: AVCaptureDevice) {
    if let subjectAreaObserver { NotificationCenter.default.removeObserver(subjectAreaObserver) }
    subjectAreaObserver = NotificationCenter.default.addObserver(
      forName: AVCaptureDevice.subjectAreaDidChangeNotification, object: device, queue: nil
    ) { [weak self] _ in
      self?.resumeContinuousFocus()
    }
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
      guard let self, self.running, let device = self.device else { return }
      let generation = self.currentGeneration()
      if state == .began {
        self.pinchGeneration = generation
        self.pinchStartZoom = device.videoZoomFactor
        return
      }
      guard state == .changed, self.pinchGeneration == generation,
        (try? device.lockForConfiguration()) != nil
      else { return }
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
    observers.append(
      center.addObserver(
        forName: AVCaptureDevice.wasDisconnectedNotification, object: nil, queue: nil
      ) { [weak self] note in
        guard let self, let gone = note.object as? AVCaptureDevice else { return }
        self.sessionQueue.async {
          // An unplugged external camera falls back to the default camera.
          guard gone.uniqueID == self.device?.uniqueID else { return }
          self.switchCameraIfNeeded()
        }
      })
    observers.append(
      center.addObserver(
        forName: AVCaptureSession.wasInterruptedNotification, object: session, queue: nil
      ) { [weak self] note in
        guard let self else { return }
        let reason = (note.userInfo?[AVCaptureSessionInterruptionReasonKey] as? Int)
          .flatMap(AVCaptureSession.InterruptionReason.init(rawValue:))
        if reason == .videoDeviceInUseByAnotherClient {
          let generation = self.currentGeneration()
          self.sessionQueue.async {
            guard self.running, self.currentGeneration() == generation else { return }
            self.emitError("camera-busy", "The camera is in use by another app", generation)
          }
        }
      })
    observers.append(
      center.addObserver(
        forName: AVCaptureSession.runtimeErrorNotification, object: session, queue: nil
      ) { [weak self] note in
        guard let self else { return }
        let error = note.userInfo?[AVCaptureSessionErrorKey] as? Error
        let generation = self.currentGeneration()
        self.sessionQueue.async {
          guard self.running, self.currentGeneration() == generation else { return }
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
    if let subjectAreaObserver { NotificationCenter.default.removeObserver(subjectAreaObserver) }
    subjectAreaObserver = nil
  }

  // MARK: - Generation and events

  private func nextGeneration() -> Int { gate.nextGeneration() }

  private func setCapturing(_ value: Bool) { gate.setCapturing(value) }

  private func currentGeneration() -> Int { gate.currentGeneration }

  private func emitError(_ code: String, _ message: String, _ generation: Int) {
    guard captureProps.active, !disposed else { return }
    captureProps.error?(code, message, Double(generation))
  }

  /// Called on `metadataQueue`.
  fileprivate func handle(texts: [String], generation: Int) {
    let reports = gate.filter(
      texts, now: ProcessInfo.processInfo.systemUptime, generation: generation)
    guard !reports.isEmpty else { return }
    sessionQueue.async { [weak self] in
      guard let self, self.running, self.currentGeneration() == generation else { return }
      for report in reports {
        self.captureProps.text?(report.text, Double(report.generation))
      }
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

  private let generation: Int

  init(owner: HybridQrScanner, generation: Int) {
    self.owner = owner
    self.generation = generation
  }

  func metadataOutput(
    _ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject],
    from connection: AVCaptureConnection
  ) {
    let texts = metadataObjects.compactMap {
      ($0 as? AVMetadataMachineReadableCodeObject)?.stringValue
    }
    if !texts.isEmpty {
      owner?.handle(texts: texts, generation: generation)
    }
  }
}
