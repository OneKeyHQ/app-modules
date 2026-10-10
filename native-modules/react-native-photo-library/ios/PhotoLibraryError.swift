import Foundation

struct PhotoLibraryError: Error, CustomStringConvertible {
  let code: String
  let message: String
  var description: String { "\(code): \(message)" }

  static let noImageData = PhotoLibraryError(code: "E_NO_IMAGE_DATA_FOUND", message: "Cannot find image data")
  static let noLibraryPermission = PhotoLibraryError(code: "E_NO_LIBRARY_PERMISSION", message: "Adding photos is not authorized")
  static let cannotSaveImage = PhotoLibraryError(code: "E_CANNOT_SAVE_IMAGE", message: "Cannot save image")
}
