import Foundation

enum PickerError: Error {
    case errorLoadingResource(Error?)
    case failedToPersistFile(Error)
    case unsupportedType, cameraAccessDenied, noOriginalImage
}
