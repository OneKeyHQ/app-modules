import AVFoundation
import CoreVideo
import Foundation
import Vision

/// Event kinds delivered through the C callback.
enum QrCaptureEventKind: Int32 {
  case ready = 0
  case text = 1
  case error = 2
  case ended = 3
  case symbols = 4
}

enum QrCaptureTermination {
  case eof
  case failure(code: String, message: String)
}

/// Supplies BGRA frames to the controller on its analysis queue.
protocol QrFrameSource: AnyObject {
  /// Starts delivering frames; returns an error code and message on failure.
  func start(
    deliver: @escaping (CVPixelBuffer) -> Void,
    terminated: @escaping (QrCaptureTermination) -> Void
  )
    -> (code: String, message: String)?
  func stop()
}

/// Live camera frames through AVCaptureVideoDataOutput. Late frames are
/// discarded, so at most one frame is in analysis at a time.
final class QrCameraSource: NSObject, QrFrameSource, AVCaptureVideoDataOutputSampleBufferDelegate {
  private static let focusHoldSeconds: TimeInterval = 5.0
  private let session = AVCaptureSession()
  private let queue: DispatchQueue
  private let deviceId: String?
  private(set) var openedDeviceId: String?
  private var device: AVCaptureDevice?
  private var deliver: ((CVPixelBuffer) -> Void)?
  private var observers: [NSObjectProtocol] = []
  private var focusRestoreWorkItem: DispatchWorkItem?

  init(queue: DispatchQueue, deviceId: String?) {
    self.queue = queue
    self.deviceId = deviceId
  }

  var hasDevice: Bool { Self.device(id: deviceId) != nil }

  static func device(id: String?) -> AVCaptureDevice? {
    if let id, let device = AVCaptureDevice(uniqueID: id), device.isConnected { return device }
    return AVCaptureDevice.default(for: .video)
  }

  /// Cameras for the picker: the built-in camera faces the user ("front");
  /// USB and Continuity cameras are "external".
  static func availableCameras() -> [(id: String, position: String, label: String)] {
    let types: [AVCaptureDevice.DeviceType]
    if #available(macOS 14.0, *) {
      types = [.builtInWideAngleCamera, .external, .continuityCamera]
    } else {
      types = [.builtInWideAngleCamera, .externalUnknown]
    }
    let devices = AVCaptureDevice.DiscoverySession(
      deviceTypes: types, mediaType: .video, position: .unspecified
    ).devices
    var seen = Set<String>()
    return devices.compactMap { device in
      guard seen.insert(device.uniqueID).inserted else { return nil }
      let continuity: Bool
      if #available(macOS 13.0, *) { continuity = device.isContinuityCamera } else { continuity = false }
      let position = device.deviceType == .builtInWideAngleCamera && !continuity ? "front" : "external"
      return (device.uniqueID, position, device.localizedName)
    }
  }

  func start(
    deliver: @escaping (CVPixelBuffer) -> Void,
    terminated: @escaping (QrCaptureTermination) -> Void
  )
    -> (code: String, message: String)?
  {
    guard let device = Self.device(id: deviceId) else {
      return ("no-camera", "No camera is available")
    }
    self.deliver = deliver
    do {
      session.beginConfiguration()
      defer { session.commitConfiguration() }
      if session.canSetSessionPreset(.hd1280x720) { session.sessionPreset = .hd1280x720 }
      do {
        let input = try AVCaptureDeviceInput(device: device)
        guard session.canAddInput(input) else {
          return ("camera-busy", "The camera input could not be added")
        }
        session.addInput(input)
      } catch {
        return ("camera-busy", error.localizedDescription)
      }
      let output = AVCaptureVideoDataOutput()
      output.alwaysDiscardsLateVideoFrames = true
      output.videoSettings = [
        kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA,
        kCVPixelBufferIOSurfacePropertiesKey as String: [:] as [String: Any],
      ]
      output.setSampleBufferDelegate(self, queue: queue)
      guard session.canAddOutput(output) else {
        return ("decoder-unavailable", "The frame output could not be added")
      }
      session.addOutput(output)
    }
    let center = NotificationCenter.default
    observers = [
      center.addObserver(
        forName: AVCaptureDevice.wasDisconnectedNotification, object: device, queue: nil
      ) { _ in
        terminated(.failure(code: "no-camera", message: "The camera disconnected"))
      },
      center.addObserver(
        forName: AVCaptureSession.runtimeErrorNotification, object: session, queue: nil
      ) { note in
        let error = note.userInfo?[AVCaptureSessionErrorKey] as? Error
        terminated(.failure(code: "camera-busy", message: error?.localizedDescription ?? "The camera stopped"))
      },
      center.addObserver(
        forName: AVCaptureSession.wasInterruptedNotification, object: session, queue: nil
      ) { _ in
        terminated(.failure(code: "camera-busy", message: "The camera was interrupted"))
      },
    ]
    session.startRunning()
    guard session.isRunning else {
      stop()
      return ("camera-busy", "The camera could not start")
    }
    self.device = device
    openedDeviceId = device.uniqueID
    if (try? device.lockForConfiguration()) != nil {
      Self.applyContinuousFocus(device)
      device.unlockForConfiguration()
    }
    return nil
  }

  func stop() {
    focusRestoreWorkItem?.cancel()
    focusRestoreWorkItem = nil
    observers.forEach { NotificationCenter.default.removeObserver($0) }
    observers.removeAll()
    device = nil
    if session.isRunning { session.stopRunning() }
  }

  private static func applyContinuousFocus(_ device: AVCaptureDevice) {
    let center = CGPoint(x: 0.5, y: 0.5)
    if device.isFocusPointOfInterestSupported { device.focusPointOfInterest = center }
    if device.isFocusModeSupported(.continuousAutoFocus) {
      device.focusMode = .continuousAutoFocus
    }
    if device.isExposurePointOfInterestSupported { device.exposurePointOfInterest = center }
    if device.isExposureModeSupported(.continuousAutoExposure) {
      device.exposureMode = .continuousAutoExposure
    }
  }

  func focus(x: Double, y: Double, restoreOn restoreQueue: DispatchQueue) -> Bool {
    guard session.isRunning, let device,
      device.isFocusModeSupported(.autoFocus) || device.isFocusModeSupported(.continuousAutoFocus),
      (try? device.lockForConfiguration()) != nil
    else { return false }
    focusRestoreWorkItem?.cancel()
    focusRestoreWorkItem = nil
    let point = CGPoint(
      x: min(1, max(0, x.isFinite ? x : 0.5)),
      y: min(1, max(0, y.isFinite ? y : 0.5)))
    if device.isFocusPointOfInterestSupported {
      device.focusPointOfInterest = point
    }
    if device.isFocusModeSupported(.autoFocus) {
      device.focusMode = .autoFocus
    } else if device.isFocusModeSupported(.continuousAutoFocus) {
      device.focusMode = .continuousAutoFocus
    }
    if device.isExposurePointOfInterestSupported {
      device.exposurePointOfInterest = point
    }
    if device.isExposureModeSupported(.autoExpose) {
      device.exposureMode = .autoExpose
    } else if device.isExposureModeSupported(.continuousAutoExposure) {
      device.exposureMode = .continuousAutoExposure
    }
    device.unlockForConfiguration()

    if device.isFocusModeSupported(.continuousAutoFocus)
      || device.isExposureModeSupported(.continuousAutoExposure)
    {
      let work = DispatchWorkItem { [weak self, weak device] in
        guard let self, self.session.isRunning, let device, self.device === device,
          (try? device.lockForConfiguration()) != nil
        else { return }
        defer { device.unlockForConfiguration() }
        Self.applyContinuousFocus(device)
      }
      focusRestoreWorkItem = work
      restoreQueue.asyncAfter(deadline: .now() + Self.focusHoldSeconds, execute: work)
    }
    return true
  }

  func captureOutput(
    _ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer,
    from connection: AVCaptureConnection
  ) {
    guard let buffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }
    deliver?(buffer)
  }
}

/// Frames from a local video file, paced by presentation time. Used to test
/// animated codes without a person in front of the camera.
final class QrFileSource: QrFrameSource {
  private let url: URL
  private let loop: Bool
  private let queue: DispatchQueue
  private let cancelled = DispatchSemaphore(value: 0)
  private let lock = NSLock()
  private var stopped = false

  init(url: URL, loop: Bool, queue: DispatchQueue) {
    self.url = url
    self.loop = loop
    self.queue = queue
  }

  func start(
    deliver: @escaping (CVPixelBuffer) -> Void,
    terminated: @escaping (QrCaptureTermination) -> Void
  )
    -> (code: String, message: String)?
  {
    let asset = AVURLAsset(url: url)
    guard asset.tracks(withMediaType: .video).first != nil else {
      return ("invalid-image", "The file has no video track")
    }
    queue.async { [weak self] in
      guard let self else { return }
      repeat {
        guard let outcome = self.readOnce(asset: asset, deliver: deliver), !self.isStopped else {
          return
        }
        if case .eof = outcome, self.loop { continue }
        terminated(outcome)
        return
      } while !self.isStopped
    }
    return nil
  }

  func stop() {
    lock.lock()
    stopped = true
    lock.unlock()
    cancelled.signal()
  }

  private var isStopped: Bool {
    lock.lock()
    defer { lock.unlock() }
    return stopped
  }

  /// Returns nil when the caller explicitly stops playback.
  private func readOnce(
    asset: AVURLAsset, deliver: (CVPixelBuffer) -> Void
  ) -> QrCaptureTermination? {
    guard let track = asset.tracks(withMediaType: .video).first,
      let reader = try? AVAssetReader(asset: asset)
    else { return .failure(code: "invalid-image", message: "The video could not be read") }
    let output = AVAssetReaderTrackOutput(
      track: track,
      outputSettings: [
        kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA,
        // IOSurface-backed, so frames can be shared with Electron as textures.
        kCVPixelBufferIOSurfacePropertiesKey as String: [:] as [String: Any],
      ])
    output.alwaysCopiesSampleData = false
    guard reader.canAdd(output) else {
      return .failure(code: "decoder-unavailable", message: "The video output could not be added")
    }
    reader.add(output)
    guard reader.startReading() else {
      return .failure(code: "invalid-image", message: "The video reader could not start")
    }
    defer { reader.cancelReading() }
    let startedAt = ProcessInfo.processInfo.systemUptime
    var firstPts: Double?
    while !isStopped, let sample = output.copyNextSampleBuffer() {
      let pts = CMSampleBufferGetPresentationTimeStamp(sample).seconds
      let base = firstPts ?? pts
      firstPts = base
      let due = startedAt + (pts - base)
      let wait = due - ProcessInfo.processInfo.systemUptime
      if wait.isFinite, wait > 0 {
        if cancelled.wait(timeout: .now() + wait) == .success { break }
      }
      guard !isStopped else { break }
      if let buffer = CMSampleBufferGetImageBuffer(sample) { deliver(buffer) }
    }
    if isStopped { return nil }
    if reader.status == .completed, firstPts != nil { return .eof }
    return .failure(code: "invalid-image", message: "The video reader stopped before end of file")
  }
}

/// Frames lent to the preview consumer. A slot holds the CVPixelBuffer (and so
/// its IOSurface) until the consumer releases the token. When every slot is in
/// use, new preview frames are skipped rather than queued.
final class QrPreviewPool {
  static let slotCount = 3

  private let lock = NSLock()
  private var slots: [Int64: CVPixelBuffer] = [:]
  private var nextToken: Int64 = 1

  /// A token and the frame's IOSurface, or nil when the pool is full or the
  /// frame is not IOSurface-backed.
  func lend(_ buffer: CVPixelBuffer) -> (token: Int64, surface: IOSurfaceRef)? {
    guard let surface = CVPixelBufferGetIOSurface(buffer)?.takeUnretainedValue() else {
      return nil
    }
    lock.lock()
    defer { lock.unlock() }
    guard slots.count < Self.slotCount else { return nil }
    let token = nextToken
    nextToken += 1
    slots[token] = buffer
    return (token, surface)
  }

  func release(_ token: Int64) {
    lock.lock()
    slots[token] = nil
    lock.unlock()
  }

  var lentCount: Int {
    lock.lock()
    defer { lock.unlock() }
    return slots.count
  }

  func clear() {
    lock.lock()
    slots.removeAll()
    lock.unlock()
  }
}

/// One capture: a frame source, Vision QR detection per frame and the shared
/// text gate. Every callback goes through `emit`, which stops after `destroy`.
final class QrCaptureController {
  typealias Emit = (QrCaptureEventKind, String, String, Int, Int64) -> Void
  /// token, IOSurfaceRef, width, height, generation, analysis revision
  typealias EmitFrame = (Int64, UnsafeMutableRawPointer, Int, Int, Int, Int64) -> Void

  let previewPool = QrPreviewPool()
  private var frameEmitter: EmitFrame?
  private var previewing = false
  private var multiSymbol = false
  private var paused = false
  private var analysisRevision: Int64 = 0

  private let sessionQueue = DispatchQueue(label: "so.onekey.qrscanner.desktop.session")
  private let analysisQueue = DispatchQueue(label: "so.onekey.qrscanner.desktop.analysis")
  private let gate = QrFrameTextGate(repeatInterval: 1.0)
  private let emitLock = NSLock()
  private var emitter: Emit?
  private var source: QrFrameSource?
  private let makeSource: (DispatchQueue) -> QrFrameSource
  private let usesCamera: Bool

  init(
    usesCamera: Bool, makeSource: @escaping (DispatchQueue) -> QrFrameSource,
    emit: @escaping Emit, emitFrame: EmitFrame?
  ) {
    self.usesCamera = usesCamera
    self.makeSource = makeSource
    self.emitter = emit
    self.frameEmitter = emitFrame
  }

  private func setPreviewing(_ value: Bool) {
    emitLock.lock()
    previewing = value
    emitLock.unlock()
  }

  /// Runs on the analysis queue, before Vision, so preview latency does not
  /// include detection time.
  private func preview(_ buffer: CVPixelBuffer, generation: Int, revision: Int64) {
    emitLock.lock()
    defer { emitLock.unlock() }
    guard previewing, gate.currentGeneration == generation, revision == analysisRevision,
      let frameEmitter, let lent = previewPool.lend(buffer) else { return }
    frameEmitter(
      lent.token, Unmanaged.passUnretained(lent.surface).toOpaque(),
      CVPixelBufferGetWidth(buffer), CVPixelBufferGetHeight(buffer), generation, revision)
  }

  private func emit(
    _ kind: QrCaptureEventKind, _ a: String, _ b: String, _ generation: Int,
    revision: Int64? = nil
  ) {
    emitLock.lock()
    defer { emitLock.unlock() }
    guard gate.currentGeneration == generation else { return }
    if let revision, paused || revision != analysisRevision { return }
    emitter?(kind, a, b, generation, revision ?? analysisRevision)
  }

  func setMultiSymbol(_ enabled: Bool) {
    emitLock.lock()
    multiSymbol = enabled
    emitLock.unlock()
  }

  func setPaused(_ value: Bool) -> Int64 {
    emitLock.lock()
    defer { emitLock.unlock() }
    if paused != value {
      paused = value
      analysisRevision += 1
      gate.resetRepeatFilter()
    }
    return analysisRevision
  }

  func start() -> Int {
    let generation = gate.nextGeneration()
    sessionQueue.async { [weak self] in self?.startOnQueue(generation: generation) }
    return generation
  }

  func stop() {
    _ = gate.nextGeneration()
    sessionQueue.async { [weak self] in self?.stopOnQueue() }
  }

  func focus(x: Double, y: Double) -> Bool {
    sessionQueue.sync {
      (source as? QrCameraSource)?.focus(x: x, y: y, restoreOn: sessionQueue) ?? false
    }
  }

  /// Stops capture and guarantees no callback runs after it returns.
  func destroy() {
    _ = gate.nextGeneration()
    sessionQueue.sync { stopOnQueue() }
    emitLock.lock()
    emitter = nil
    frameEmitter = nil
    emitLock.unlock()
    // Electron holds its own reference to imported surfaces.
    previewPool.clear()
  }

  private func startOnQueue(generation: Int) {
    guard gate.currentGeneration == generation else { return }
    stopOnQueue()
    let source = makeSource(analysisQueue)
    if usesCamera {
      guard let camera = source as? QrCameraSource, camera.hasDevice else {
        emit(.error, "no-camera", "No camera is available", generation)
        return
      }
      switch AVCaptureDevice.authorizationStatus(for: .video) {
      case .authorized:
        break
      case .notDetermined:
        AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
          self?.sessionQueue.async {
            guard let self, self.gate.currentGeneration == generation else { return }
            if granted {
              self.startOnQueue(generation: generation)
            } else {
              self.emit(.error, "permission-denied", "Camera access was denied", generation)
            }
          }
        }
        return
      default:
        emit(.error, "permission-denied", "Camera access is not allowed", generation)
        return
      }
    }
    if let failure = source.start(
      deliver: { [weak self] buffer in
        self?.processFrame(buffer, generation: generation)
      },
      terminated: { [weak self] outcome in
        self?.sessionQueue.async { [weak self] in
          guard let self, self.source != nil, self.gate.currentGeneration == generation else { return }
          self.stopOnQueue()
          switch outcome {
          case .eof:
            self.emit(.ended, "", "", generation)
          case .failure(let code, let message):
            self.emit(.error, code, message, generation)
          }
        }
      })
    {
      emit(.error, failure.code, failure.message, generation)
      return
    }
    self.source = source
    guard gate.currentGeneration == generation else {
      stopOnQueue()
      return
    }
    emit(.ready, (source as? QrCameraSource)?.openedDeviceId ?? "", "", generation)
    gate.setCapturing(true)
    setPreviewing(true)
  }

  private func stopOnQueue() {
    gate.setCapturing(false)
    setPreviewing(false)
    source?.stop()
    source = nil
  }

  /// Runs on the analysis queue.
  private func processFrame(_ buffer: CVPixelBuffer, generation: Int) {
    guard gate.currentGeneration == generation else { return }
    emitLock.lock()
    let isMulti = multiSymbol
    let isPaused = paused
    let revision = analysisRevision
    emitLock.unlock()
    preview(buffer, generation: generation, revision: revision)
    if isPaused { return }

    let request = VNDetectBarcodesRequest()
    request.symbologies = [.qr]
    let handler = VNImageRequestHandler(cvPixelBuffer: buffer, options: [:])
    guard (try? handler.perform([request])) != nil else { return }
    let results = request.results ?? []

    if isMulti {
      let symbols: [[String: Any]] = results.compactMap { observation in
        guard let text = observation.payloadStringValue, !text.isEmpty else { return nil }
        // Vision boundingBox uses a bottom-left origin; flip Y to top-left [0, 1].
        let box = observation.boundingBox
        return [
          "text": text,
          "centerX": Double(box.midX),
          "centerY": Double(1.0 - box.midY),
          "bounds": [
            "x": Double(box.minX),
            "y": Double(1.0 - box.maxY),
            "width": Double(box.width),
            "height": Double(box.height),
          ],
        ]
      }
      guard !symbols.isEmpty else { return }
      let payload: [String: Any] = [
        "frameWidth": CVPixelBufferGetWidth(buffer),
        "frameHeight": CVPixelBufferGetHeight(buffer),
        "symbols": symbols,
      ]
      guard let data = try? JSONSerialization.data(withJSONObject: payload),
        let json = String(data: data, encoding: .utf8)
      else { return }
      emit(.symbols, json, "", generation, revision: revision)
      return
    }

    let texts = results.compactMap { $0.payloadStringValue }
    guard !texts.isEmpty else { return }
    // Do not let a retired frame update the single-symbol repeat filter.
    emitLock.lock()
    guard !paused, revision == analysisRevision, gate.currentGeneration == generation else {
      emitLock.unlock()
      return
    }
    let reports = gate.filter(texts, now: ProcessInfo.processInfo.systemUptime, generation: generation)
    emitLock.unlock()
    for report in reports {
      emit(.text, report.text, "", report.generation, revision: revision)
    }
  }
}
