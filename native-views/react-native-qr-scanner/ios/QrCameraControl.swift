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

  static func wideAngleZoomFactor(for device: AVCaptureDevice) -> CGFloat {
    wideAngleZoomFactor(
      constituentTypes: device.constituentDevices.map(\.deviceType),
      switchOverFactors: device.virtualDeviceSwitchOverVideoZoomFactors.map {
        CGFloat(truncating: $0)
      })
  }
}
