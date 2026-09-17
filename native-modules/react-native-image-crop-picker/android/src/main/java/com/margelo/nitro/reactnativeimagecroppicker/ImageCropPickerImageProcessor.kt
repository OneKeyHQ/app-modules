package com.margelo.nitro.reactnativeimagecroppicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal data class ImageCropPickerConfig(
  val width: Int?,
  val height: Int?,
  val cropping: Boolean,
  val includeBase64: Boolean,
  val compressImageQuality: Double?,
  val compressImageMaxWidth: Double?,
  val compressImageMaxHeight: Double?,
  val freeStyleCropEnabled: Boolean,
  val cropperCircleOverlay: Boolean,
  val cropperToolbarTitle: String?,
  val cropperActiveWidgetColor: String?,
  val cropperToolbarColor: String?,
  val cropperToolbarWidgetColor: String?,
  val cropperStatusBarLight: Boolean,
  val cropperNavigationBarLight: Boolean,
  val showCropGuidelines: Boolean,
  val showCropFrame: Boolean,
  val enableRotationGesture: Boolean,
  val hideBottomControls: Boolean,
  val disableCropperColorSetters: Boolean,
) {
  companion object {
    fun from(options: ImageCropPickerOptions, forceCropping: Boolean) = ImageCropPickerConfig(
      width = options.width?.takeIf { it.isFinite() }?.roundToInt()?.takeIf { it > 0 },
      height = options.height?.takeIf { it.isFinite() }?.roundToInt()?.takeIf { it > 0 },
      cropping = forceCropping || options.cropping == true,
      includeBase64 = options.includeBase64 == true,
      compressImageQuality = options.compressImageQuality,
      compressImageMaxWidth = options.compressImageMaxWidth,
      compressImageMaxHeight = options.compressImageMaxHeight,
      freeStyleCropEnabled = options.freeStyleCropEnabled == true,
      cropperCircleOverlay = options.cropperCircleOverlay == true,
      cropperToolbarTitle = options.cropperToolbarTitle,
      cropperActiveWidgetColor = options.cropperActiveWidgetColor,
      cropperToolbarColor = options.cropperToolbarColor,
      cropperToolbarWidgetColor = options.cropperToolbarWidgetColor,
      cropperStatusBarLight = options.cropperStatusBarLight ?: true,
      cropperNavigationBarLight = options.cropperNavigationBarLight ?: false,
      showCropGuidelines = options.showCropGuidelines ?: true,
      showCropFrame = options.showCropFrame ?: true,
      enableRotationGesture = options.enableRotationGesture == true,
      hideBottomControls = options.hideBottomControls == true,
      disableCropperColorSetters = options.disableCropperColorSetters == true,
    )
  }
}

internal object ImageCropPickerImageProcessor {
  // Bounds memory for huge photos. Crop targets are far smaller than this.
  private const val MAX_DECODED_PIXEL_SIZE = 4096

  // Matches react-native-image-crop-picker on Android.
  private const val DEFAULT_COMPRESS_QUALITY = 1.0
  private const val TEMPORARY_DIRECTORY_NAME = "react-native-image-crop-picker"

  fun temporaryDirectory(context: Context): File {
    val directory = File(context.cacheDir, TEMPORARY_DIRECTORY_NAME)
    if (!directory.isDirectory && !directory.mkdirs()) {
      throw ImageCropPickerException.cannotSaveImage()
    }
    return directory
  }

  fun createTemporaryFile(context: Context, extension: String): File =
    File(temporaryDirectory(context), "${UUID.randomUUID()}.$extension")

  fun cleanTemporaryDirectory(context: Context) {
    val directory = File(context.cacheDir, TEMPORARY_DIRECTORY_NAME)
    if (directory.exists() && !directory.deleteRecursively()) {
      throw ImageCropPickerException.cleanupFailed()
    }
  }

  fun removeFile(path: String) {
    val file = fileFromPath(path) ?: throw ImageCropPickerException.cleanupFailed()
    if (!file.delete()) {
      throw ImageCropPickerException.cleanupFailed()
    }
  }

  private fun fileFromPath(path: String): File? = when {
    path.startsWith("file://") -> Uri.parse(path).path?.let { File(it) }
    path.startsWith("/") -> File(path)
    else -> null
  }

  // Resolves an openCropper source into a Uri that uCrop can read. Returns the
  // temporary file it had to create, if any, so the caller can delete it.
  fun resolveCropSource(context: Context, path: String): Pair<Uri, File?> {
    when {
      path.startsWith("data:") -> {
        val commaIndex = path.indexOf(',')
        if (commaIndex < 0) {
          throw ImageCropPickerException.cropperImageNotFound()
        }
        val bytes = try {
          Base64.decode(path.substring(commaIndex + 1), Base64.DEFAULT)
        } catch (error: IllegalArgumentException) {
          throw ImageCropPickerException.cropperImageNotFound()
        }
        val file = createTemporaryFile(context, "img")
        try {
          file.writeBytes(bytes)
        } catch (error: IOException) {
          throw ImageCropPickerException.cannotSaveImage(error)
        }
        return Uri.fromFile(file) to file
      }
      // uCrop downloads remote sources and reads content URIs itself.
      path.startsWith("http://") || path.startsWith("https://") || path.startsWith("content://") ->
        return Uri.parse(path) to null
      else -> {
        val file = fileFromPath(path)
        if (file == null || !file.isFile) {
          throw ImageCropPickerException.cropperImageNotFound()
        }
        return Uri.fromFile(file) to null
      }
    }
  }

  // Decodes an image with its EXIF orientation applied, downsampled so its
  // longest side is at most MAX_DECODED_PIXEL_SIZE.
  fun decodeBitmap(context: Context, uri: Uri): Bitmap {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    openStream(context, uri).use { BitmapFactory.decodeStream(it, null, bounds) }
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
      throw ImageCropPickerException.noImageData("Invalid image selected")
    }

    var sampleSize = 1
    while (max(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= MAX_DECODED_PIXEL_SIZE) {
      sampleSize *= 2
    }
    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    val sampled = try {
      openStream(context, uri).use { BitmapFactory.decodeStream(it, null, options) }
    } catch (error: OutOfMemoryError) {
      throw ImageCropPickerException.lowMemory(error)
    } ?: throw ImageCropPickerException.noImageData("Invalid image selected")

    val orientation = try {
      openStream(context, uri).use {
        ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
      }
    } catch (error: IOException) {
      ExifInterface.ORIENTATION_NORMAL
    }
    val oriented = applyOrientation(sampled, orientation)
    val scale = min(
      MAX_DECODED_PIXEL_SIZE.toDouble() / oriented.width,
      MAX_DECODED_PIXEL_SIZE.toDouble() / oriented.height,
    )
    return if (scale < 1.0) {
      scaleBitmap(oriented, floorSize(oriented.width * scale), floorSize(oriented.height * scale))
    } else {
      oriented
    }
  }

  // Scales a cropped image to the requested size; smaller crops are scaled up.
  // With a locked aspect ratio the crop can still end up a few pixels off that
  // ratio, so the result is scaled to exactly the requested size, as
  // react-native-image-crop-picker did. Free-style crops keep their own aspect
  // ratio and fit inside it.
  fun resizeCroppedBitmap(bitmap: Bitmap, config: ImageCropPickerConfig): Bitmap {
    val width = config.width ?: return bitmap
    val height = config.height ?: return bitmap
    if (!config.freeStyleCropEnabled) {
      return scaleBitmap(bitmap, width, height)
    }
    val widthRatio = width.toDouble() / bitmap.width
    val heightRatio = height.toDouble() / bitmap.height
    return if (widthRatio < heightRatio) {
      scaleBitmap(bitmap, width, roundSize(bitmap.height * widthRatio))
    } else {
      scaleBitmap(bitmap, roundSize(bitmap.width * heightRatio), height)
    }
  }

  fun makeResult(
    context: Context,
    bitmap: Bitmap,
    config: ImageCropPickerConfig,
    cropRect: CropRect?,
    filename: String?,
  ): PickedImage {
    var output = bitmap
    val maxWidth = config.compressImageMaxWidth
    val maxHeight = config.compressImageMaxHeight
    val shouldResizeWidth = maxWidth != null && maxWidth < output.width
    val shouldResizeHeight = maxHeight != null && maxHeight < output.height
    if (shouldResizeWidth || shouldResizeHeight) {
      val scale = min(
        (maxWidth ?: output.width.toDouble()) / output.width,
        (maxHeight ?: output.height.toDouble()) / output.height,
      )
      output = scaleBitmap(output, floorSize(output.width * scale), floorSize(output.height * scale))
    }

    val quality = ((config.compressImageQuality ?: DEFAULT_COMPRESS_QUALITY).coerceIn(0.0, 1.0) * 100).roundToInt()
    val file = createTemporaryFile(context, "jpg")
    try {
      FileOutputStream(file).use { stream ->
        if (!output.compress(Bitmap.CompressFormat.JPEG, quality, stream)) {
          throw ImageCropPickerException.cannotSaveImage()
        }
      }
    } catch (error: IOException) {
      file.delete()
      throw ImageCropPickerException.cannotSaveImage(error)
    }

    val data = if (config.includeBase64) {
      Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    } else {
      null
    }
    return PickedImage(
      path = Uri.fromFile(file).toString(),
      size = file.length().toDouble(),
      width = output.width.toDouble(),
      height = output.height.toDouble(),
      mime = "image/jpeg",
      data = data,
      cropRect = cropRect,
      filename = filename,
    )
  }

  private fun openStream(context: Context, uri: Uri): InputStream =
    try {
      context.contentResolver.openInputStream(uri)
    } catch (error: Exception) {
      null
    } ?: throw ImageCropPickerException.noImageData()

  private fun floorSize(value: Double): Int = max(1, floor(value).toInt())

  private fun roundSize(value: Double): Int = max(1, value.roundToInt())

  private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
    if (width == bitmap.width && height == bitmap.height) {
      return bitmap
    }
    val scaled = try {
      Bitmap.createScaledBitmap(bitmap, width, height, true)
    } catch (error: OutOfMemoryError) {
      throw ImageCropPickerException.lowMemory(error)
    }
    if (scaled !== bitmap) {
      bitmap.recycle()
    }
    return scaled
  }

  private fun applyOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
      ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
      ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
      ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
        matrix.setRotate(180f)
        matrix.postScale(-1f, 1f)
      }
      ExifInterface.ORIENTATION_TRANSPOSE -> {
        matrix.setRotate(90f)
        matrix.postScale(-1f, 1f)
      }
      ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
      ExifInterface.ORIENTATION_TRANSVERSE -> {
        matrix.setRotate(-90f)
        matrix.postScale(-1f, 1f)
      }
      ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
      else -> return bitmap
    }
    val oriented = try {
      Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (error: OutOfMemoryError) {
      throw ImageCropPickerException.lowMemory(error)
    }
    if (oriented !== bitmap) {
      bitmap.recycle()
    }
    return oriented
  }
}
