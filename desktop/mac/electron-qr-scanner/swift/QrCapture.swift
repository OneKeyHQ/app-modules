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
}

/// Supplies BGRA frames to the controller on its analysis queue.
protocol QrFrameSource: AnyObject {
  /// Starts delivering frames; returns an error code and message on failure.
  func start(deliver: @escaping (CVPixelBuffer) -> Void, ended: @escaping () -> Void)
    -> (code: String, message: String)?
  func stop()
}

/// Live camera frames through AVCaptureVideoDataOutput. Late frames are
/// discarded, so at most one frame is in analysis at a time.
final class QrCameraSource: NSObject, QrFrameSource, AVCaptureVideoDataOutputSampleBufferDelegate {
  private let session = AVCaptureSession()
  private let queue: DispatchQueue
  private let deviceId: String?
  private var deliver: ((CVPixelBuffer) -> Void)?
  private var configured = false

  init(queue: DispatchQueue, deviceId: String?) {
    self.queue = queue
    self.deviceId = deviceId
  }

  static func device(id: String?) -> AVCaptureDevice? {
    if let id, let device = AVCaptureDevice(uniqueID: id) { return device }
    return AVCaptureDevice.default(for: .video)
  }

  func start(deliver: @escaping (CVPixelBuffer) -> Void, ended: @escaping () -> Void)
    -> (code: String, message: String)?
  {
    self.deliver = deliver
    if !configured {
      guard let device = Self.device(id: deviceId) else {
        return ("no-camera", "No camera is available")
      }
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
      configured = true
    }
    session.startRunning()
    return session.isRunning ? nil : ("camera-busy", "The camera could not start")
  }

  func stop() {
    if session.isRunning { session.stopRunning() }
    deliver = nil
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
  private let lock = NSLock()
  private var running = false

  init(url: URL, loop: Bool, queue: DispatchQueue) {
    self.url = url
    self.loop = loop
    self.queue = queue
  }

  func start(deliver: @escaping (CVPixelBuffer) -> Void, ended: @escaping () -> Void)
    -> (code: String, message: String)?
  {
    let asset = AVURLAsset(url: url)
    guard asset.tracks(withMediaType: .video).first != nil else {
      return ("invalid-image", "The file has no video track")
    }
    setRunning(true)
    queue.async { [weak self] in
      guard let self else { return }
      repeat {
        guard self.readOnce(asset: asset, deliver: deliver) else { break }
      } while self.loop && self.isRunning
      if self.isRunning {
        self.setRunning(false)
        ended()
      }
    }
    return nil
  }

  func stop() { setRunning(false) }

  private var isRunning: Bool {
    lock.lock()
    defer { lock.unlock() }
    return running
  }

  private func setRunning(_ value: Bool) {
    lock.lock()
    running = value
    lock.unlock()
  }

  /// Reads the file once; false when it could not be read or was stopped.
  private func readOnce(asset: AVURLAsset, deliver: (CVPixelBuffer) -> Void) -> Bool {
    guard let track = asset.tracks(withMediaType: .video).first,
      let reader = try? AVAssetReader(asset: asset)
    else { return false }
    let output = AVAssetReaderTrackOutput(
      track: track,
      outputSettings: [
        kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA,
        // IOSurface-backed, so frames can be shared with Electron as textures.
        kCVPixelBufferIOSurfacePropertiesKey as String: [:] as [String: Any],
      ])
    output.alwaysCopiesSampleData = false
    guard reader.canAdd(output) else { return false }
    reader.add(output)
    guard reader.startReading() else { return false }
    let startedAt = ProcessInfo.processInfo.systemUptime
    var firstPts: Double?
    while isRunning, let sample = output.copyNextSampleBuffer() {
      let pts = CMSampleBufferGetPresentationTimeStamp(sample).seconds
      let base = firstPts ?? pts
      firstPts = base
      let due = startedAt + (pts - base)
      let wait = due - ProcessInfo.processInfo.systemUptime
      if wait > 0 { Thread.sleep(forTimeInterval: wait) }
      if let buffer = CMSampleBufferGetImageBuffer(sample) { deliver(buffer) }
    }
    reader.cancelReading()
    return isRunning
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
  typealias Emit = (QrCaptureEventKind, String, String, Int) -> Void
  /// token, IOSurfaceRef, width, height, generation
  typealias EmitFrame = (Int64, UnsafeMutableRawPointer, Int, Int, Int) -> Void

  let previewPool = QrPreviewPool()
  private var frameEmitter: EmitFrame?
  private var previewing = false

  private let sessionQueue = DispatchQueue(label: "so.onekey.qrscanner.desktop.session")
  private let analysisQueue = DispatchQueue(label: "so.onekey.qrscanner.desktop.analysis")
  private let gate = QrFrameTextGate(repeatInterval: 1.0)
  private let emitLock = NSLock()
  private var emitter: Emit?
  private var source: QrFrameSource?
  private let makeSource: (DispatchQueue) -> QrFrameSource
  private let usesCamera: Bool
  private var running = false

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
  private func preview(_ buffer: CVPixelBuffer) {
    emitLock.lock()
    defer { emitLock.unlock() }
    guard previewing, let frameEmitter, let lent = previewPool.lend(buffer) else { return }
    frameEmitter(
      lent.token, Unmanaged.passUnretained(lent.surface).toOpaque(),
      CVPixelBufferGetWidth(buffer), CVPixelBufferGetHeight(buffer), gate.currentGeneration)
  }

  private func emit(_ kind: QrCaptureEventKind, _ a: String, _ b: String, _ generation: Int) {
    emitLock.lock()
    defer { emitLock.unlock() }
    emitter?(kind, a, b, generation)
  }

  func start() {
    sessionQueue.async { [weak self] in self?.startOnQueue() }
  }

  func stop() {
    sessionQueue.async { [weak self] in self?.stopOnQueue() }
  }

  /// Stops capture and guarantees no callback runs after it returns.
  func destroy() {
    sessionQueue.sync { stopOnQueue() }
    emitLock.lock()
    emitter = nil
    frameEmitter = nil
    emitLock.unlock()
    // Electron holds its own reference to imported surfaces.
    previewPool.clear()
  }

  private func startOnQueue() {
    guard !running else { return }
    let generation = gate.nextGeneration()
    if usesCamera {
      guard QrCameraSource.device(id: nil) != nil else {
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
              self.startOnQueue()
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
    let source = self.source ?? makeSource(analysisQueue)
    self.source = source
    if let failure = source.start(
      deliver: { [weak self] buffer in
        self?.preview(buffer)
        self?.analyze(buffer)
      },
      ended: { [weak self] in
        guard let self else { return }
        self.gate.setCapturing(false)
        self.emit(.ended, "", "", self.gate.currentGeneration)
      })
    {
      emit(.error, failure.code, failure.message, generation)
      return
    }
    running = true
    gate.setCapturing(true)
    setPreviewing(true)
    emit(.ready, "", "", generation)
  }

  private func stopOnQueue() {
    _ = gate.nextGeneration()
    gate.setCapturing(false)
    setPreviewing(false)
    guard running else { return }
    source?.stop()
    running = false
  }

  /// Runs on the analysis queue.
  private func analyze(_ buffer: CVPixelBuffer) {
    let request = VNDetectBarcodesRequest()
    request.symbologies = [.qr]
    let handler = VNImageRequestHandler(cvPixelBuffer: buffer, options: [:])
    guard (try? handler.perform([request])) != nil else { return }
    let texts = (request.results ?? []).compactMap { $0.payloadStringValue }
    guard !texts.isEmpty else { return }
    for report in gate.filter(texts, now: ProcessInfo.processInfo.systemUptime) {
      emit(.text, report.text, "", report.generation)
    }
  }
}
