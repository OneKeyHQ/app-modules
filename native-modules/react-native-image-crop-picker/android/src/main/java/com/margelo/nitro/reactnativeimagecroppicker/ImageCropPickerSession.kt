package com.margelo.nitro.reactnativeimagecroppicker

import android.app.Activity
import android.content.res.Configuration
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.File
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// One picker or cropper flow. It owns the JS promise until the launched
// activities report back, and settles it exactly once.
internal class ImageCropPickerSession(
  private val activity: ComponentActivity,
  private val mode: Mode,
  private val config: ImageCropPickerConfig,
  private val promise: Promise<PickedImage>,
  private val onFinish: (ImageCropPickerSession) -> Unit,
) {
  sealed class Mode {
    object Picker : Mode()

    class Cropper(val path: String) : Mode()
  }

  // Main thread state.
  private val keyPrefix = "onekey-image-crop-picker-${UUID.randomUUID()}"
  private val launchers = mutableListOf<ActivityResultLauncher<*>>()
  private val temporaryFiles = mutableListOf<File>()
  private var pickedFilename: String? = null
  private var isAwaitingResult = false
  private var isProcessing = false
  private var isFinished = false

  // Whether a launched activity or background work is still pending.
  val isActive: Boolean
    get() = !isFinished && (isProcessing || (isAwaitingResult && !activity.isDestroyed))

  fun start() {
    when (mode) {
      is Mode.Picker -> launchPicker()
      is Mode.Cropper -> resolveCropperSource(mode.path)
    }
  }

  // Settles a session whose activity went away without calling back.
  fun abandon() {
    finish(Result.failure(ImageCropPickerException.cancelled()))
  }

  // The system Photo Picker needs no storage or media permission. On devices
  // without it, PickVisualMedia falls back to ACTION_OPEN_DOCUMENT.
  private fun launchPicker() {
    val launcher = register(ActivityResultContracts.PickVisualMedia()) { uri ->
      when {
        uri == null -> finish(Result.failure(ImageCropPickerException.cancelled()))
        config.cropping -> {
          pickedFilename = queryDisplayName(uri)
          launchCropper(uri)
        }
        else -> processPickedImage(uri)
      }
    }
    launch {
      launcher.launch(
        PickVisualMediaRequest.Builder()
          .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
          .build(),
      )
    }
  }

  private fun processPickedImage(uri: Uri) {
    val filename = queryDisplayName(uri)
    runInBackground(
      work = {
        ImageCropPickerImageProcessor.makeResult(
          activity,
          ImageCropPickerImageProcessor.decodeBitmap(activity, uri),
          config,
          null,
          filename,
        )
      },
      onComplete = ::finish,
    )
  }

  private fun resolveCropperSource(path: String) {
    runInBackground(
      work = { ImageCropPickerImageProcessor.resolveCropSource(activity, path) },
      onComplete = { result ->
        result.fold(
          onSuccess = { (source, temporaryFile) ->
            temporaryFile?.let { temporaryFiles.add(it) }
            launchCropper(source)
          },
          onFailure = { finish(Result.failure(it)) },
        )
      },
    )
  }

  private fun launchCropper(source: Uri) {
    val destination = try {
      ImageCropPickerImageProcessor.createTemporaryFile(activity, "jpg")
    } catch (error: ImageCropPickerException) {
      finish(Result.failure(error))
      return
    }
    temporaryFiles.add(destination)

    val systemIsDark = (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
      Configuration.UI_MODE_NIGHT_YES
    val intent = ImageCropperActivity.createIntent(
      activity,
      source,
      Uri.fromFile(destination),
      config,
      ImageCropperTheme.resolve(config.appearance, systemIsDark),
      // Keeps the cropper in the app's orientation, e.g. portrait on phones.
      activity.requestedOrientation,
    )
    val launcher = register(ActivityResultContracts.StartActivityForResult()) { result ->
      handleCropResult(result)
    }
    launch { launcher.launch(intent) }
  }

  private fun handleCropResult(result: ActivityResult) {
    val data = result.data
    when (result.resultCode) {
      Activity.RESULT_OK -> {
        val output = data?.let { ImageCropperActivity.getOutputUri(it) }
        if (data == null || output == null) {
          finish(Result.failure(ImageCropPickerException.noImageData()))
          return
        }
        val cropRect = CropRect(
          x = data.getIntExtra(ImageCropperActivity.EXTRA_OUTPUT_OFFSET_X, -1).toDouble(),
          y = data.getIntExtra(ImageCropperActivity.EXTRA_OUTPUT_OFFSET_Y, -1).toDouble(),
          width = data.getIntExtra(ImageCropperActivity.EXTRA_OUTPUT_WIDTH, -1).toDouble(),
          height = data.getIntExtra(ImageCropperActivity.EXTRA_OUTPUT_HEIGHT, -1).toDouble(),
        )
        val filename = pickedFilename
        runInBackground(
          work = {
            val cropped = ImageCropPickerImageProcessor.decodeBitmap(activity, output)
            ImageCropPickerImageProcessor.makeResult(
              activity,
              ImageCropPickerImageProcessor.resizeCroppedBitmap(cropped, config),
              config,
              cropRect,
              filename,
            )
          },
          onComplete = ::finish,
        )
      }
      ImageCropperActivity.RESULT_ERROR -> {
        val message = data?.getStringExtra(ImageCropperActivity.EXTRA_ERROR_MESSAGE)
          ?: "Cannot crop image"
        finish(Result.failure(ImageCropPickerException.noImageData(message)))
      }
      else -> finish(Result.failure(ImageCropPickerException.cancelled()))
    }
  }

  private fun <I, O> register(
    contract: ActivityResultContract<I, O>,
    callback: (O) -> Unit,
  ): ActivityResultLauncher<I> {
    val key = "$keyPrefix-${launchers.size}"
    val launcher = activity.activityResultRegistry.register(key, contract) { output ->
      isAwaitingResult = false
      if (!isFinished) {
        callback(output)
      }
    }
    launchers.add(launcher)
    return launcher
  }

  private fun launch(block: () -> Unit) {
    isAwaitingResult = true
    try {
      block()
    } catch (error: Exception) {
      isAwaitingResult = false
      finish(Result.failure(ImageCropPickerException.failedToShowPicker(error)))
    }
  }

  private fun <T> runInBackground(work: () -> T, onComplete: (Result<T>) -> Unit) {
    isProcessing = true
    executor.execute {
      val result = try {
        Result.success(work())
      } catch (error: ImageCropPickerException) {
        Result.failure(error)
      } catch (error: OutOfMemoryError) {
        Result.failure(ImageCropPickerException.lowMemory(error))
      } catch (error: Exception) {
        Result.failure(ImageCropPickerException.noImageData(error.message ?: "Cannot process image"))
      }
      mainHandler.post {
        isProcessing = false
        if (!isFinished) {
          onComplete(result)
        }
      }
    }
  }

  private fun finish(result: Result<PickedImage>) {
    if (isFinished) {
      return
    }
    isFinished = true
    isAwaitingResult = false

    // Unregister outside of the registry's dispatch callback.
    val finishedLaunchers = launchers.toList()
    launchers.clear()
    mainHandler.post { finishedLaunchers.forEach { it.unregister() } }

    val filesToDelete = temporaryFiles.toList()
    temporaryFiles.clear()
    if (filesToDelete.isNotEmpty()) {
      executor.execute { filesToDelete.forEach { it.delete() } }
    }

    result.fold(
      onSuccess = { promise.resolve(it) },
      onFailure = { error ->
        if ((error as? ImageCropPickerException)?.code != ImageCropPickerException.E_PICKER_CANCELLED) {
          OneKeyLog.warn(TAG, error.message ?: error.toString())
        }
        promise.reject(error)
      },
    )
    onFinish(this)
  }

  private fun queryDisplayName(uri: Uri): String? {
    val displayName = try {
      activity.contentResolver
        .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor -> if (cursor.moveToFirst()) cursor.getString(0) else null }
    } catch (error: Exception) {
      null
    }
    return displayName ?: uri.lastPathSegment
  }

  companion object {
    private const val TAG = "ImageCropPicker"
    private val mainHandler = Handler(Looper.getMainLooper())
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
  }
}
