package com.margelo.nitro.reactnativeimagecroppicker

fun main() {
  val privateDetails = "content://photos/private-selection /data/user/0/app/cache/private-photo.jpg"
  var assertions = 0
  fun expect(error: ImageCropPickerException, code: String, message: String) {
    check(error.code == code)
    check(error.message == "$code: $message")
    check(error.message?.contains(privateDetails) == false)
    assertions += 3
  }

  expect(ImageCropPickerException.noImageData(), "E_NO_IMAGE_DATA_FOUND", "Cannot find image data")
  expect(
    ImageCropPickerException.failedToShowPicker(IllegalStateException(privateDetails)),
    "E_FAILED_TO_SHOW_PICKER", "Cannot show picker",
  )
  expect(
    ImageCropPickerException.lowMemory(OutOfMemoryError(privateDetails)),
    "E_LOW_MEMORY_ERROR", "Out of memory",
  )
  expect(
    ImageCropPickerException.cannotSaveImage(java.io.IOException(privateDetails)),
    "E_CANNOT_SAVE_IMAGE", "Cannot save image. Unable to write to tmp location.",
  )
  expect(ImageCropPickerException.cancelled(), "E_PICKER_CANCELLED", "User cancelled image selection")
  println("ImageCropPickerException: $assertions assertions passed")
}
