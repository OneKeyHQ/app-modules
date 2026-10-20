package com.margelo.nitro.reactnativephotolibrary

internal class PhotoLibraryException(code: String, message: String, cause: Throwable? = null) :
  Exception("$code: $message", cause) {
  companion object {
    fun noActivity() = PhotoLibraryException("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist")
    fun inProgress() = PhotoLibraryException("E_PERMISSION_IN_PROGRESS", "Another save permission request is pending")
    fun noImageData() = PhotoLibraryException("E_NO_IMAGE_DATA_FOUND", "Cannot find image data")
    fun noLibraryPermission() = PhotoLibraryException("E_NO_LIBRARY_PERMISSION", "Adding photos is not authorized")
    fun cannotSaveImage(cause: Throwable? = null) = PhotoLibraryException("E_CANNOT_SAVE_IMAGE", "Cannot save image", cause)
  }
}
