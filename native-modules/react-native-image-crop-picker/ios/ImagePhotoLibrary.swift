import NitroModules
import Photos

enum ImagePhotoLibrary {
  private static let saveQueue = DispatchQueue(label: "onekey.image-photo-library.save", qos: .userInitiated)

  static func permission() -> PhotoSavePermission {
    return permission(for: PHPhotoLibrary.authorizationStatus(for: .addOnly))
  }

  static func permission(for status: PHAuthorizationStatus) -> PhotoSavePermission {
    switch status {
    case .authorized, .limited:
      return PhotoSavePermission(status: .granted, canAskAgain: true)
    case .notDetermined:
      return PhotoSavePermission(status: .undetermined, canAskAgain: true)
    case .denied, .restricted:
      return PhotoSavePermission(status: .denied, canAskAgain: false)
    @unknown default:
      return PhotoSavePermission(status: .denied, canAskAgain: false)
    }
  }

  static func requestPermission() -> Promise<PhotoSavePermission> {
    let promise = Promise<PhotoSavePermission>()
    DispatchQueue.main.async {
      PHPhotoLibrary.requestAuthorization(for: .addOnly) { _ in
        promise.resolve(withResult: permission())
      }
    }
    return promise
  }

  static func save(path: String) -> Promise<Void> {
    let promise = Promise<Void>()
    saveQueue.async {
      do {
        guard permission().status == .granted else {
          throw ImageCropPickerError.noLibraryPermission
        }
        guard let url = ImageCropPickerImageProcessor.fileURL(fromPath: path) else {
          throw ImageCropPickerError.noImageData
        }
        _ = try ImageCropPickerImageProcessor.imageMetadata(at: url)
        // Add a resource only. Never enumerate assets or read back the result.
        try PHPhotoLibrary.shared().performChangesAndWait {
          PHAssetCreationRequest.forAsset().addResource(with: .photo, fileURL: url, options: nil)
        }
        promise.resolve(withResult: ())
      } catch let error as ImageCropPickerError {
        promise.reject(withError: error)
      } catch {
        promise.reject(withError: ImageCropPickerError.cannotSaveImage)
      }
    }
    return promise
  }
}
