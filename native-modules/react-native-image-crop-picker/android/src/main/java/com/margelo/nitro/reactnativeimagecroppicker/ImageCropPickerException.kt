package com.margelo.nitro.reactnativeimagecroppicker

// The JS wrapper parses "<code>: <message>" back into `error.code` /
// `error.message`, matching react-native-image-crop-picker.
internal class ImageCropPickerException(
  val code: String,
  message: String,
  cause: Throwable? = null,
) : Exception("$code: $message", cause) {
  companion object {
    const val E_PICKER_CANCELLED = "E_PICKER_CANCELLED"
    const val E_PICKER_IN_PROGRESS = "E_PICKER_IN_PROGRESS"
    const val E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST"
    const val E_FAILED_TO_SHOW_PICKER = "E_FAILED_TO_SHOW_PICKER"
    const val E_NO_IMAGE_DATA_FOUND = "E_NO_IMAGE_DATA_FOUND"
    const val E_CROPPER_IMAGE_NOT_FOUND = "E_CROPPER_IMAGE_NOT_FOUND"
    const val E_CANNOT_SAVE_IMAGE = "E_CANNOT_SAVE_IMAGE"
    const val E_LOW_MEMORY_ERROR = "E_LOW_MEMORY_ERROR"
    const val E_ERROR_WHILE_CLEANING_FILES = "E_ERROR_WHILE_CLEANING_FILES"

    fun cancelled() = ImageCropPickerException(E_PICKER_CANCELLED, "User cancelled image selection")

    fun inProgress() =
      ImageCropPickerException(E_PICKER_IN_PROGRESS, "Another image picker or cropper is already open")

    fun noActivity() = ImageCropPickerException(E_ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist")

    fun failedToShowPicker(cause: Throwable) =
      ImageCropPickerException(E_FAILED_TO_SHOW_PICKER, cause.message ?: "Cannot show picker", cause)

    fun noImageData(message: String = "Cannot find image data") =
      ImageCropPickerException(E_NO_IMAGE_DATA_FOUND, message)

    fun cropperImageNotFound() =
      ImageCropPickerException(E_CROPPER_IMAGE_NOT_FOUND, "Can't find the image at the specified path")

    fun cannotSaveImage(cause: Throwable? = null) =
      ImageCropPickerException(E_CANNOT_SAVE_IMAGE, "Cannot save image. Unable to write to tmp location.", cause)

    fun lowMemory(cause: Throwable) =
      ImageCropPickerException(E_LOW_MEMORY_ERROR, cause.message ?: "Out of memory", cause)

    fun cleanupFailed() = ImageCropPickerException(E_ERROR_WHILE_CLEANING_FILES, "Error while cleaning up tmp files")
  }
}
