import AVFoundation
import NitroModules

/// Lists the cameras the live scanner can switch between and reports when
/// one is connected or disconnected.
class HybridQrCameraList: HybridQrCameraListSpec {
  private var listener: (() -> Void)?
  private var observers: [NSObjectProtocol] = []
  private let lock = NSLock()

  func listCameras() throws -> Promise<[QrCameraInfo]> {
    let promise = Promise<[QrCameraInfo]>()
    DispatchQueue.global(qos: .userInitiated).async {
      let cameras = QrCameraControl.availableCameras().map {
        QrCameraInfo(
          id: $0.device.uniqueID,
          position: QrCameraPosition(fromString: $0.position) ?? .external,
          label: $0.device.localizedName)
      }
      promise.resolve(withResult: cameras)
    }
    return promise
  }

  func setOnChange(listener: (() -> Void)?) throws {
    lock.lock()
    defer { lock.unlock() }
    self.listener = listener
    if listener == nil {
      observers.forEach { NotificationCenter.default.removeObserver($0) }
      observers.removeAll()
      return
    }
    guard observers.isEmpty else { return }
    for name in [AVCaptureDevice.wasConnectedNotification, AVCaptureDevice.wasDisconnectedNotification] {
      observers.append(
        NotificationCenter.default.addObserver(forName: name, object: nil, queue: nil) {
          [weak self] _ in
          self?.notify()
        })
    }
  }

  private func notify() {
    lock.lock()
    let listener = self.listener
    lock.unlock()
    listener?()
  }

  deinit {
    observers.forEach { NotificationCenter.default.removeObserver($0) }
  }
}
