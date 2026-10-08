import NitroModules
import UIKit

class ReactNativeImageCropPicker: HybridReactNativeImageCropPickerSpec {
  // Main thread only.
  private var activeSession: ImageCropPickerSession?

  func openPicker(options: ImageCropPickerOptions) throws -> Promise<PickedImage> {
    return startSession(mode: .picker, config: ImageCropPickerConfig(options))
  }

  func openCropper(path: String, options: ImageCropPickerOptions) throws -> Promise<PickedImage> {
    return startSession(
      mode: .cropper(path: path),
      config: ImageCropPickerConfig(options, forceCropping: true)
    )
  }

  func clean() throws -> Promise<Void> {
    return Promise.parallel {
      try ImageCropPickerImageProcessor.cleanTemporaryDirectory()
    }
  }

  func cleanSingle(path: String) throws -> Promise<Void> {
    return Promise.parallel {
      try ImageCropPickerImageProcessor.removeFile(atPath: path)
    }
  }

  private func startSession(
    mode: ImageCropPickerSession.Mode,
    config: ImageCropPickerConfig
  ) -> Promise<PickedImage> {
    let promise = Promise<PickedImage>()
    DispatchQueue.main.async {
      self.beginSession(mode: mode, config: config, promise: promise)
    }
    return promise
  }

  private func beginSession(
    mode: ImageCropPickerSession.Mode,
    config: ImageCropPickerConfig,
    promise: Promise<PickedImage>
  ) {
    if let activeSession {
      if activeSession.isActive {
        promise.reject(withError: ImageCropPickerError.inProgress)
        return
      }
      // Its UI went away without calling back; don't block new requests on it.
      activeSession.abandon()
    }

    let session = ImageCropPickerSession(mode: mode, config: config, promise: promise) {
      [weak self] finishedSession in
      if self?.activeSession === finishedSession {
        self?.activeSession = nil
      }
    }
    activeSession = session
    session.start()
  }
}
