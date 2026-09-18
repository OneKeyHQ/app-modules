package com.margelo.nitro.reactnativeimagecroppicker

import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise

@DoNotStrip
class ReactNativeImageCropPicker : HybridReactNativeImageCropPickerSpec() {
  private val mainHandler = Handler(Looper.getMainLooper())

  // Main thread only.
  private var activeSession: ImageCropPickerSession? = null

  override fun openPicker(options: ImageCropPickerOptions): Promise<PickedImage> =
    startSession(
      ImageCropPickerSession.Mode.Picker,
      ImageCropPickerConfig.from(options, forceCropping = false),
    )

  override fun openCropper(path: String, options: ImageCropPickerOptions): Promise<PickedImage> =
    startSession(
      ImageCropPickerSession.Mode.Cropper(path),
      ImageCropPickerConfig.from(options, forceCropping = true),
    )

  override fun clean(): Promise<Unit> =
    Promise.parallel {
      val context = NitroModules.applicationContext ?: throw ImageCropPickerException.cleanupFailed()
      ImageCropPickerImageProcessor.cleanTemporaryDirectory(context)
    }

  override fun cleanSingle(path: String): Promise<Unit> =
    Promise.parallel { ImageCropPickerImageProcessor.removeFile(path) }

  private fun startSession(
    mode: ImageCropPickerSession.Mode,
    config: ImageCropPickerConfig,
  ): Promise<PickedImage> {
    val promise = Promise<PickedImage>()
    mainHandler.post {
      activeSession?.let { session ->
        if (session.isActive) {
          promise.reject(ImageCropPickerException.inProgress())
          return@post
        }
        // Its activity went away without calling back; don't block new requests on it.
        session.abandon()
      }

      val activity = NitroModules.applicationContext?.currentActivity as? ComponentActivity
      if (activity == null || activity.isFinishing || activity.isDestroyed) {
        promise.reject(ImageCropPickerException.noActivity())
        return@post
      }

      val session = ImageCropPickerSession(activity, mode, config, promise) { finishedSession ->
        if (activeSession === finishedSession) {
          activeSession = null
        }
      }
      activeSession = session
      session.start()
    }
    return promise
  }
}
