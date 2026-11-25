import AVFoundation
import CoreGraphics

/// Camera selection and zoom rules for the live scanner. Pure functions over
/// device facts, so they are unit-tested without camera hardware.
enum QrCameraControl {
  /// Zoom never goes past this multiple of the 1x (wide-angle) factor:
  /// beyond it the digital crop is too soft for dense codes.
  static let maxZoomMultiple: CGFloat = 8

  /// Back-camera device types in order of preference. Virtual devices come
  /// first: they switch to the ultra-wide lens by themselves when a code is
  /// held closer than the wide lens can focus (iPhone 13 Pro and later).
  static let preferredBackDeviceTypes: [AVCaptureDevice.DeviceType] = [
    .builtInTripleCamera,
    .builtInDualWideCamera,
    .builtInDualCamera,
    .builtInWideAngleCamera,
  ]

  /// Zoom factor at which a virtual device shows its wide-angle lens, which is
  /// what users know as 1x. `switchOverFactors[i]` is where constituent i+1
  /// takes over, so the wide lens at index n starts at `switchOverFactors[n-1]`.
  static func wideAngleZoomFactor(
    constituentTypes: [AVCaptureDevice.DeviceType], switchOverFactors: [CGFloat]
  ) -> CGFloat {
    guard let wideIndex = constituentTypes.firstIndex(of: .builtInWideAngleCamera),
      wideIndex >= 1, wideIndex - 1 < switchOverFactors.count
    else { return 1 }
    return switchOverFactors[wideIndex - 1]
  }

  /// Clamps a pinch-requested zoom to the device range and the 8x-of-1x cap.
  static func clampZoom(
    _ requested: CGFloat, minFactor: CGFloat, maxFactor: CGFloat, wideFactor: CGFloat
  ) -> CGFloat {
    let upper = max(minFactor, min(maxFactor, wideFactor * maxZoomMultiple))
    return min(max(requested, minFactor), upper)
  }

  static func preferredBackCamera() -> AVCaptureDevice? {
    for type in preferredBackDeviceTypes {
      if let device = AVCaptureDevice.default(type, for: .video, position: .back) {
        return device
      }
    }
    return AVCaptureDevice.default(for: .video)
  }

  static func frontCamera() -> AVCaptureDevice? {
    AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .front)
  }

  /// USB and other external cameras (iPadOS 17 and later).
  static func externalCameras() -> [AVCaptureDevice] {
    guard #available(iOS 17.0, *) else { return [] }
    return AVCaptureDevice.DiscoverySession(
      deviceTypes: [.external], mediaType: .video, position: .unspecified
    ).devices
  }

  /// Cameras offered to users: one back, one front, then external ones.
  static func availableCameras() -> [(device: AVCaptureDevice, position: String)] {
    var cameras: [(AVCaptureDevice, String)] = []
    if let back = preferredBackCamera(), back.position == .back { cameras.append((back, "back")) }
    if let front = frontCamera() { cameras.append((front, "front")) }
    cameras += externalCameras().map { ($0, "external") }
    return uniqueById(cameras)
  }

  /// The camera for `id`, or the default when `id` is unset or disconnected.
  static func device(forId id: String?) -> AVCaptureDevice? {
    if let id, let device = AVCaptureDevice(uniqueID: id), device.isConnected {
      return device
    }
    return preferredBackCamera()
  }

  /// Drops repeated ids, keeping the first (the default camera can also be
  /// discovered as an external one on some iPads).
  static func uniqueById<T>(_ cameras: [(T, String)], id: (T) -> String) -> [(T, String)] {
    var seen = Set<String>()
    return cameras.filter { seen.insert(id($0.0)).inserted }
  }

  private static func uniqueById(_ cameras: [(AVCaptureDevice, String)])
    -> [(device: AVCaptureDevice, position: String)]
  {
    uniqueById(cameras, id: { $0.uniqueID }).map { (device: $0.0, position: $0.1) }
  }

  static func wideAngleZoomFactor(for device: AVCaptureDevice) -> CGFloat {
    wideAngleZoomFactor(
      constituentTypes: device.constituentDevices.map(\.deviceType),
      switchOverFactors: device.virtualDeviceSwitchOverVideoZoomFactors.map {
        CGFloat(truncating: $0)
      })
  }

  /// True when the camera can focus on a tapped point of interest.
  static func supportsTapToFocus(
    isFocusPointOfInterestSupported: Bool,
    supportsAutoFocus: Bool,
    supportsContinuousAutoFocus: Bool
  ) -> Bool {
    isFocusPointOfInterestSupported && (supportsAutoFocus || supportsContinuousAutoFocus)
  }

  static func supportsTapToFocus(_ device: AVCaptureDevice) -> Bool {
    supportsTapToFocus(
      isFocusPointOfInterestSupported: device.isFocusPointOfInterestSupported,
      supportsAutoFocus: device.isFocusModeSupported(.autoFocus),
      supportsContinuousAutoFocus: device.isFocusModeSupported(.continuousAutoFocus))
  }
}
