import NitroModules
import Photos
import ImageIO
import UniformTypeIdentifiers

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

  static func fileURL(_ path: String) -> URL? {
    if path.hasPrefix("/") { return URL(fileURLWithPath: path) }
    guard path.hasPrefix("file://"), let url = URL(string: path), url.isFileURL,
          url.host == nil || url.host == "" || url.host == "localhost" else { return nil }
    return url
  }

  static func validateImage(at url: URL) throws {
    let values = try? url.resourceValues(forKeys: [.fileSizeKey, .isRegularFileKey])
    let size = values?.fileSize ?? 0
    guard values?.isRegularFile == true, size > 0, size <= 64 * 1024 * 1024,
          let source = CGImageSourceCreateWithURL(url as CFURL, [kCGImageSourceShouldCache: false] as CFDictionary),
          let identifier = CGImageSourceGetType(source),
          let type = UTType(identifier as String), type.conforms(to: .image),
          let properties = CGImageSourceCopyPropertiesAtIndex(source, 0, nil) as? [CFString: Any],
          let width = (properties[kCGImagePropertyPixelWidth] as? NSNumber)?.intValue,
          let height = (properties[kCGImagePropertyPixelHeight] as? NSNumber)?.intValue,
          width > 0, height > 0 else { throw PhotoLibraryError.noImageData }
  }

  static func save(path: String) -> Promise<Void> {
    let promise = Promise<Void>()
    saveQueue.async {
      do {
        guard permission().status == .granted else {
          throw PhotoLibraryError.noLibraryPermission
        }
        guard let url = fileURL(path) else {
          throw PhotoLibraryError.noImageData
        }
        try validateImage(at: url)
        // Add a resource only. Never enumerate assets or read back the result.
        try PHPhotoLibrary.shared().performChangesAndWait {
          PHAssetCreationRequest.forAsset().addResource(with: .photo, fileURL: url, options: nil)
        }
        promise.resolve(withResult: ())
      } catch let error as PhotoLibraryError {
        promise.reject(withError: error)
      } catch {
        promise.reject(withError: PhotoLibraryError.cannotSaveImage)
      }
    }
    return promise
  }
}
