import AVFoundation
import NitroModules
import UIKit

final class QrPreviewView: UIView {
  override class var layerClass: AnyClass { AVCaptureVideoPreviewLayer.self }

  var previewLayer: AVCaptureVideoPreviewLayer {
    // swiftlint:disable:next force_cast
    layer as! AVCaptureVideoPreviewLayer
  }

  static let focusBoxSize: CGFloat = 73

  private var sceneGeometryObservation: NSKeyValueObservation?
  private var deviceOrientationObserver: NSObjectProtocol?
  private var generatingDeviceOrientationNotifications = false
  private var legacyOrientationTimer: Timer?
  private var focusFadeWorkItem: DispatchWorkItem?
  private var captureRunning = false

  let focusIndicator: UIView = {
    let view = UIView(frame: CGRect(x: 0, y: 0, width: focusBoxSize, height: focusBoxSize))
    view.backgroundColor = .clear
    view.layer.borderColor = UIColor.yellow.cgColor
    view.layer.borderWidth = 1
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

  deinit {
    focusFadeWorkItem?.cancel()
    legacyOrientationTimer?.invalidate()
    if let deviceOrientationObserver {
      NotificationCenter.default.removeObserver(deviceOrientationObserver)
    }
    if generatingDeviceOrientationNotifications {
      let endNotifications = {
        UIDevice.current.endGeneratingDeviceOrientationNotifications()
      }
      if Thread.isMainThread {
        MainActor.assumeIsolated(endNotifications)
      } else {
        DispatchQueue.main.async(execute: endNotifications)
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
    } else {
      updateLegacyOrientationTimer()
    }
    updateDeviceOrientationObserver()
    updateCaptureOrientation()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    updateCaptureOrientation()
  }

  func setCaptureRunning(_ running: Bool) {
    captureRunning = running
    updateLegacyOrientationTimer()
  }

  /// Holds the last rendered frame on screen without stopping the capture session.
  func setFrozen(_ frozen: Bool) {
    previewLayer.connection?.isEnabled = !frozen
    if frozen {
      focusIndicator.layer.removeAllAnimations()
      focusIndicator.alpha = 0
    }
  }

  private func updateDeviceOrientationObserver() {
    if let deviceOrientationObserver {
      NotificationCenter.default.removeObserver(deviceOrientationObserver)
      self.deviceOrientationObserver = nil
    }
    if generatingDeviceOrientationNotifications {
      UIDevice.current.endGeneratingDeviceOrientationNotifications()
      generatingDeviceOrientationNotifications = false
    }
    guard window != nil else { return }
    UIDevice.current.beginGeneratingDeviceOrientationNotifications()
    generatingDeviceOrientationNotifications = true
    deviceOrientationObserver = NotificationCenter.default.addObserver(
      forName: UIDevice.orientationDidChangeNotification, object: nil, queue: .main
    ) { [weak self] _ in
      MainActor.assumeIsolated {
        self?.updateCaptureOrientation()
        self?.scheduleFollowUpOrientationUpdates()
      }
    }
  }

  private func scheduleFollowUpOrientationUpdates() {
    for delay in [0.1, 0.3] {
      DispatchQueue.main.asyncAfter(deadline: .now() + delay) { [weak self] in
        self?.updateCaptureOrientation()
      }
    }
  }

  private func updateLegacyOrientationTimer() {
    if #available(iOS 16.0, *) { return }
    guard captureRunning, window?.windowScene != nil else {
      legacyOrientationTimer?.invalidate()
      legacyOrientationTimer = nil
      return
    }
    guard legacyOrientationTimer == nil else { return }
    // A rotation can commit after the device notification and leave our bounds unchanged.
    let timer = Timer(timeInterval: 0.1, repeats: true) { [weak self] timer in
      guard let self else {
        timer.invalidate()
        return
      }
      MainActor.assumeIsolated {
        self.updateCaptureOrientation()
      }
    }
    timer.tolerance = 0.02
    RunLoop.main.add(timer, forMode: .common)
    legacyOrientationTimer = timer
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
    let captureOrientation = Self.videoOrientation(for: orientation)
    if #available(iOS 17.0, *) {
      let videoPort = connection.inputPorts.first { $0.mediaType == .video }
      guard let input = videoPort?.input as? AVCaptureDeviceInput else { return }
      if input.device.deviceType == .external {
        applyRotationAngle(0, to: connection)
        return
      }
      #if compiler(>=6.4) && !targetEnvironment(macCatalyst)
      if #available(iOS 27.0, *) {
        guard let captureOrientation else { return }
        let coordinator = AVCaptureDevice.RotationCoordinator(device: input.device, previewLayer: nil)
        let angle = coordinator.videoRotationAngleRelative(toDeviceOrientation: captureOrientation)
        applyRotationAngle(angle, to: connection)
        return
      }
      #endif
    }
    // The typed orientation lets AVFoundation handle the built-in camera on older systems/SDKs.
    if let captureOrientation, connection.isVideoOrientationSupported,
      connection.videoOrientation != captureOrientation
    {
      connection.videoOrientation = captureOrientation
    }
  }

  @available(iOS 17.0, *)
  private func applyRotationAngle(_ angle: CGFloat, to connection: AVCaptureConnection) {
    guard connection.videoRotationAngle != angle, connection.isVideoRotationAngleSupported(angle)
    else { return }
    connection.videoRotationAngle = angle
  }

  static func videoOrientation(for orientation: UIInterfaceOrientation) -> AVCaptureVideoOrientation? {
    switch orientation {
    case .portrait: return .portrait
    case .portraitUpsideDown: return .portraitUpsideDown
    case .landscapeLeft: return .landscapeLeft
    case .landscapeRight: return .landscapeRight
    default: return nil
    }
  }

  /// Shows the legacy yellow tap-to-focus square at `point` and fades it out.
  func showFocusIndicator(at point: CGPoint) {
    focusFadeWorkItem?.cancel()
    focusIndicator.layer.removeAllAnimations()
    focusIndicator.transform = .identity
    focusIndicator.bounds = CGRect(x: 0, y: 0, width: Self.focusBoxSize, height: Self.focusBoxSize)
    focusIndicator.center = point
    focusIndicator.alpha = 0
    focusIndicator.transform = CGAffineTransform(scaleX: 2.0, y: 2.0)
    UIView.animate(withDuration: 0.2, delay: 0, options: [.curveEaseOut]) {
      self.focusIndicator.transform = .identity
      self.focusIndicator.alpha = 1
    }
    let fadeWork = DispatchWorkItem { [weak self] in
      guard let self else { return }
      UIView.animate(withDuration: 0.2) {
        self.focusIndicator.alpha = 0
      }
    }
    focusFadeWorkItem = fadeWork
    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0, execute: fadeWork)
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
  var multiSymbol: Bool?
  var frozen: Bool?
  var focusRequest: QrFocusRequest?
  var onPreviewTap: ((Double) -> Void)?
  var onReady: ((Double, String) -> Void)?
  var onFrameText: ((String, Double) -> Void)?
  var onFrameSymbols: (([QrDetectedSymbol], Double) -> Void)?
  var onError: ((String, String, Double) -> Void)?

  // Owned by `sessionQueue`.
  private let sessionQueue = DispatchQueue(label: "so.onekey.qrscanner.session")
  private let metadataQueue = DispatchQueue(label: "so.onekey.qrscanner.metadata")
  private let session = AVCaptureSession()
  private struct CaptureProps {
    var active = false
    var multiSymbol = false
    var frozen = false
    var previewTap: ((Double) -> Void)?
    var ready: ((Double, String) -> Void)?
    var text: ((String, Double) -> Void)?
    var symbols: (([QrDetectedSymbol], Double) -> Void)?
    var error: ((String, String, Double) -> Void)?
  }

  private var captureProps = CaptureProps()
  private var lastFocusRequestId = -Double.infinity
  private let frameRevisionLock = NSLock()
  private var frameRevision = 0
  private var readyCapture: UUID?
  // Owned by sessionQueue; timestamps use the capture output clock.
  private var metadataResumeTime = CMTime.invalid

  private func setReadyCapture(_ isReady: Bool) {
    frameRevisionLock.lock()
    readyCapture = isReady ? UUID() : nil
    frameRevisionLock.unlock()
  }

  private func currentReadyCapture() -> UUID? {
    frameRevisionLock.lock()
    defer { frameRevisionLock.unlock() }
    return readyCapture
  }

  private func captureTime() -> CMTime {
    let clock: CMClock?
    if #available(iOS 15.4, *) {
      clock = session.synchronizationClock
    } else {
      clock = session.masterClock
    }
    return clock.map { CMClockGetTime($0) } ?? .invalid
  }

  private func updateFrameRevision() {
    frameRevisionLock.lock()
    defer { frameRevisionLock.unlock() }
    frameRevision += 1
  }

  private func currentFrameRevision() -> Int {
    frameRevisionLock.lock()
    defer { frameRevisionLock.unlock() }
    return frameRevision
  }
  /// Mirrors `captureProps.frozen` for main-thread gesture checks.
  private var isFrozenMain = false
  private var disposed = false
  private var permissionPending = false
  private var output: AVCaptureMetadataOutput?
  private var metadataDelegate: MetadataDelegate?
  private var running = false {
    didSet {
      if #available(iOS 16.0, *) { return }
      guard running != oldValue else { return }
      DispatchQueue.main.async { [weak previewView, running] in
        previewView?.setCaptureRunning(running)
      }
    }
  }
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
    let props = CaptureProps(
      active: active,
      multiSymbol: multiSymbol ?? false,
      frozen: frozen ?? false,
      previewTap: onPreviewTap,
      ready: onReady,
      text: onFrameText,
      symbols: onFrameSymbols,
      error: onError)
    let cameraId = cameraId
    let focusRequest = focusRequest
    let focusCapture = currentReadyCapture()
    DispatchQueue.main.async { [weak self] in
      guard let self else { return }
      self.isFrozenMain = props.frozen
      if props.active {
        self.previewView.setFrozen(props.frozen)
      }
    }
    sessionQueue.async { [weak self] in
      guard let self, !self.disposed else { return }
      let previous = self.captureProps
      let previousCamera = self.requestedCameraId
      let newFocus = focusRequest.flatMap { request -> QrFocusRequest? in
        guard request.id.isFinite, request.id > self.lastFocusRequestId else { return nil }
        self.lastFocusRequestId = request.id
        return request
      }
      if previous.frozen != props.frozen {
        self.updateFrameRevision()
        self.gate.resetRepeatFilter()
        if !props.frozen { self.metadataResumeTime = self.captureTime() }
      }
      let canFocus = self.running && props.active && !props.frozen && cameraId == previousCamera
      self.captureProps = props
      self.requestedCameraId = cameraId
      if !props.active {
        self.stop()
      } else if self.running {
        self.switchCameraIfNeeded()
      } else if !previous.active || cameraId != previousCamera {
        self.start()
      }
      if canFocus, let focusCapture, focusCapture == self.currentReadyCapture(),
        let request = newFocus, (0...1).contains(request.x), (0...1).contains(request.y)
      {
        let generation = self.currentGeneration()
        let revision = self.currentFrameRevision()
        DispatchQueue.main.async { [weak self] in
          guard let self, !self.isFrozenMain else { return }
          let bounds = self.previewView.bounds
          guard bounds.width > 0, bounds.height > 0 else { return }
          self.focusAt(
            CGPoint(x: CGFloat(request.x) * bounds.width, y: CGFloat(request.y) * bounds.height),
            generation: generation, revision: revision, readyCapture: focusCapture, fromGesture: false)
        }
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
    setReadyCapture(false)
    metadataResumeTime = .invalid
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
    if observers.isEmpty { addObservers() }
    session.startRunning()
    running = session.isRunning
    setCapturing(running)
    if running, let device {
      setReadyCapture(true)
      updatePreviewOrientation()
      captureProps.ready?(Double(currentGeneration), device.uniqueID)
    } else {
      emitError("camera-busy", "The camera could not start", currentGeneration)
    }
  }

  private func stop() {
    setReadyCapture(false)
    // A new generation makes late metadata from the old one stale.
    _ = nextGeneration()
    pinchGeneration = nil
    output?.setMetadataObjectsDelegate(nil, queue: nil)
    DispatchQueue.main.async { [weak self] in
      self?.isFrozenMain = false
      self?.previewView.setFrozen(false)
    }
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
      var restoredInput = false
      if !installed {
        if session.outputs.contains(newOutput) { session.removeOutput(newOutput) }
        if session.inputs.contains(newInput) { session.removeInput(newInput) }
        session.sessionPreset = oldPreset
        if let input, session.canAddInput(input) {
          session.addInput(input)
          restoredInput = true
        }
        if let output, session.canAddOutput(output) { session.addOutput(output) }
      }
      session.commitConfiguration()
      if installed || restoredInput { updatePreviewOrientation() }
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
    setReadyCapture(false)
    if let error = configureCamera(target, generation: generation) {
      setReadyCapture(running)
      emitError(error.code, error.message, currentGeneration())
      return
    }
    _ = nextGeneration()
    metadataResumeTime = .invalid
    setCapturing(true)
    setReadyCapture(true)
    captureProps.ready?(Double(generation), target.uniqueID)
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
    if device.isAutoFocusRangeRestrictionSupported {
      device.autoFocusRangeRestriction = .none
    }
    if device.isFocusPointOfInterestSupported { device.focusPointOfInterest = center }
    if device.isFocusModeSupported(.continuousAutoFocus) {
      device.focusMode = .continuousAutoFocus
    }
    if device.isExposurePointOfInterestSupported { device.exposurePointOfInterest = center }
    if device.isExposureModeSupported(.continuousAutoExposure) {
      device.exposureMode = .continuousAutoExposure
    }
    device.isSubjectAreaChangeMonitoringEnabled = false
  }

  // MARK: - Gestures (main thread in, sessionQueue for the device)

  fileprivate func handleTap(_ recognizer: UITapGestureRecognizer) {
    guard !isFrozenMain, let readyCapture = currentReadyCapture() else { return }
    focusAt(
      recognizer.location(in: previewView), generation: currentGeneration(),
      revision: currentFrameRevision(), readyCapture: readyCapture, fromGesture: true)
  }

  private func focusAt(
    _ layerPoint: CGPoint, generation: Int, revision: Int, readyCapture: UUID, fromGesture: Bool
  ) {
    let devicePoint = previewView.previewLayer.captureDevicePointConverted(fromLayerPoint: layerPoint)
    sessionQueue.async { [weak self] in
      guard let self, self.running, !self.captureProps.frozen,
        self.currentGeneration() == generation, self.currentFrameRevision() == revision,
        self.currentReadyCapture() == readyCapture, let device = self.device
      else { return }
      if fromGesture { self.captureProps.previewTap?(Double(generation)) }
      guard
        QrCameraControl.supportsTapToFocus(device),
        (try? device.lockForConfiguration()) != nil
      else { return }
      defer { device.unlockForConfiguration() }
      if device.isAutoFocusRangeRestrictionSupported {
        device.autoFocusRangeRestriction = .none
      }
      if device.isFocusPointOfInterestSupported {
        if device.isFocusModeSupported(.autoFocus) {
          device.focusPointOfInterest = devicePoint
          device.focusMode = .autoFocus
        } else if device.isFocusModeSupported(.continuousAutoFocus) {
          device.focusPointOfInterest = devicePoint
          device.focusMode = .continuousAutoFocus
        }
      }
      if device.isExposurePointOfInterestSupported {
        if device.isExposureModeSupported(.autoExpose) {
          device.exposurePointOfInterest = devicePoint
          device.exposureMode = .autoExpose
        } else if device.isExposureModeSupported(.continuousAutoExposure) {
          device.exposurePointOfInterest = devicePoint
          device.exposureMode = .continuousAutoExposure
        }
      }
      // Moving the camera afterwards hands focus back to continuous mode.
      device.isSubjectAreaChangeMonitoringEnabled = true
      DispatchQueue.main.async { [weak self] in
        guard let self, !self.isFrozenMain, self.currentGeneration() == generation,
          self.currentFrameRevision() == revision, self.currentReadyCapture() == readyCapture
        else { return }
        self.previewView.showFocusIndicator(at: layerPoint)
      }
    }
  }

  fileprivate func handlePinch(_ recognizer: UIPinchGestureRecognizer) {
    guard !isFrozenMain else { return }
    let state = recognizer.state
    let scale = recognizer.scale
    sessionQueue.async { [weak self] in
      guard let self, self.running, !self.captureProps.frozen, let device = self.device else {
        return
      }
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
  fileprivate func handle(codes: [AVMetadataMachineReadableCodeObject], generation: Int) {
    guard !codes.isEmpty else { return }
    let revision = currentFrameRevision()
    sessionQueue.async { [weak self] in
      guard let self, self.running, !self.captureProps.frozen,
        self.currentGeneration() == generation, self.currentFrameRevision() == revision
      else { return }

      let cutoff = self.metadataResumeTime
      let currentCodes = cutoff.isNumeric
        ? codes.filter { $0.time.isNumeric && CMTimeCompare($0.time, cutoff) >= 0 }
        : codes
      guard !currentCodes.isEmpty else { return }

      if self.captureProps.multiSymbol, let symbolsCallback = self.captureProps.symbols {
        DispatchQueue.main.async { [weak self] in
          guard let self, !self.isFrozenMain, self.currentFrameRevision() == revision else { return }
          let layer = self.previewView.previewLayer
          let layerBounds = layer.bounds
          guard layerBounds.width > 0, layerBounds.height > 0 else { return }
          let symbols: [QrDetectedSymbol] = currentCodes.compactMap { code in
            guard let text = code.stringValue, !text.isEmpty else { return nil }
            let transformed =
              layer.transformedMetadataObject(for: code) as? AVMetadataMachineReadableCodeObject
            let rect = transformed?.bounds ?? CGRect(
              x: layerBounds.midX, y: layerBounds.midY, width: 0, height: 0)
            let normX = Double(rect.minX / layerBounds.width)
            let normY = Double(rect.minY / layerBounds.height)
            let normW = Double(rect.width / layerBounds.width)
            let normH = Double(rect.height / layerBounds.height)
            return QrDetectedSymbol(
              text: text,
              centerX: Double(rect.midX / layerBounds.width),
              centerY: Double(rect.midY / layerBounds.height),
              bounds: QrSymbolBounds(x: normX, y: normY, width: normW, height: normH))
          }
          guard !symbols.isEmpty else { return }
          self.sessionQueue.async { [weak self] in
            guard let self, self.running, !self.captureProps.frozen,
              self.currentGeneration() == generation, self.currentFrameRevision() == revision
            else { return }
            symbolsCallback(symbols, Double(generation))
          }
        }
        return
      }

      let texts = currentCodes.compactMap { $0.stringValue }
      guard !texts.isEmpty else { return }
      let reports = self.gate.filter(
        texts, now: ProcessInfo.processInfo.systemUptime, generation: generation)
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
    let codes = metadataObjects.compactMap { $0 as? AVMetadataMachineReadableCodeObject }
    if !codes.isEmpty {
      owner?.handle(codes: codes, generation: generation)
    }
  }
}
