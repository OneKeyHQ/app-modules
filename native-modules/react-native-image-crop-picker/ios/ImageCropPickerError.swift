import Foundation

enum ImageCropPickerErrorCode: String {
  case pickerCancelled = "E_PICKER_CANCELLED"
  case pickerInProgress = "E_PICKER_IN_PROGRESS"
  case failedToShowPicker = "E_FAILED_TO_SHOW_PICKER"
  case noImageDataFound = "E_NO_IMAGE_DATA_FOUND"
  case cropperImageNotFound = "E_CROPPER_IMAGE_NOT_FOUND"
  case cannotSaveImage = "E_CANNOT_SAVE_IMAGE"
  case cleanupError = "E_ERROR_WHILE_CLEANING_FILES"
}

// The JS wrapper parses the "<code>: <message>" description back into
// `error.code` / `error.message`, matching react-native-image-crop-picker.
struct ImageCropPickerError: Error, CustomStringConvertible {
  let code: ImageCropPickerErrorCode
  let message: String

  var description: String {
    return "\(code.rawValue): \(message)"
  }

  static let cancelled = ImageCropPickerError(
    code: .pickerCancelled,
    message: "User cancelled image selection"
  )

  static let inProgress = ImageCropPickerError(
    code: .pickerInProgress,
    message: "Another image picker or cropper is already open"
  )

  static let noViewController = ImageCropPickerError(
    code: .failedToShowPicker,
    message: "Cannot find a view controller to present from"
  )

  static let noImageData = ImageCropPickerError(
    code: .noImageDataFound,
    message: "Cannot find image data"
  )

  static let cropperImageNotFound = ImageCropPickerError(
    code: .cropperImageNotFound,
    message: "Can't find the image at the specified path"
  )

  static let cannotSaveImage = ImageCropPickerError(
    code: .cannotSaveImage,
    message: "Cannot save image. Unable to write to tmp location."
  )

  static let cleanupFailed = ImageCropPickerError(
    code: .cleanupError,
    message: "Error while cleaning up tmp files"
  )
}
