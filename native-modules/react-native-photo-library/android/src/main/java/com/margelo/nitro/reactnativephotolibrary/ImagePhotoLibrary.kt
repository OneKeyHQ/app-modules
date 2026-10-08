package com.margelo.nitro.reactnativephotolibrary

import android.Manifest
import android.app.Activity
import android.graphics.BitmapFactory
import com.facebook.react.ReactApplication
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import java.io.File
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

internal object ImagePhotoLibrary {
  private val mainHandler = Handler(Looper.getMainLooper())
  private val saveExecutor = Executors.newSingleThreadExecutor()
  private var permissionPending = false
  private const val REQUESTED = "writePermissionRequested"

  private fun context(): Context = NitroModules.applicationContext ?: throw PhotoLibraryException.noActivity()

  private fun foregroundActivity(): Activity? {
    // Nitro's global context can belong to bg; ReactHost owns the UI activity.
    return (context().applicationContext as? ReactApplication)?.reactHost?.currentReactContext?.currentActivity
      ?: NitroModules.applicationContext?.currentActivity
  }

  private fun imageMime(file: File): String {
    if (!file.isFile || file.length() <= 0 || file.length() > 64 * 1024 * 1024) {
      throw PhotoLibraryException.noImageData()
    }
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, bounds)
    val mime = bounds.outMimeType
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0 || mime?.startsWith("image/") != true) {
      throw PhotoLibraryException.noImageData()
    }
    return mime
  }

  fun permission(): PhotoSavePermission {
    if (Build.VERSION.SDK_INT >= 29) return PhotoSavePermission(PhotoSavePermissionStatus.GRANTED, true)
    val context = context()
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
      return PhotoSavePermission(PhotoSavePermissionStatus.GRANTED, true)
    }
    val requested = context.getSharedPreferences("onekey-photo-library", Context.MODE_PRIVATE).getBoolean(REQUESTED, false)
    if (!requested) return PhotoSavePermission(PhotoSavePermissionStatus.UNDETERMINED, true)
    val activity = foregroundActivity() ?: throw PhotoLibraryException.noActivity()
    return PhotoSavePermission(
      PhotoSavePermissionStatus.DENIED,
      ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE),
    )
  }

  fun getPermission(): Promise<PhotoSavePermission> {
    val promise = Promise<PhotoSavePermission>()
    mainHandler.post {
      try { promise.resolve(permission()) } catch (error: Exception) { promise.reject(error) }
    }
    return promise
  }

  fun requestPermission(): Promise<PhotoSavePermission> {
    val promise = Promise<PhotoSavePermission>()
    mainHandler.post {
      var launcher: ActivityResultLauncher<String>? = null
      var observer: DefaultLifecycleObserver? = null
      val activity = foregroundActivity() as? ComponentActivity
      var finished = false
      var ownsRequest = false
      fun finish(error: Exception? = null) {
        if (finished) return
        finished = true
        if (ownsRequest) permissionPending = false
        observer?.let { activity?.lifecycle?.removeObserver(it) }
        mainHandler.post { launcher?.unregister() }
        try {
          if (error == null) promise.resolve(permission()) else promise.reject(error)
        } catch (failure: Exception) { promise.reject(failure) }
      }
      try {
        val current = permission()
        if (current.status == PhotoSavePermissionStatus.GRANTED || !current.canAskAgain) {
          promise.resolve(current)
          return@post
        }
        if (permissionPending) {
          promise.reject(PhotoLibraryException.inProgress())
          return@post
        }
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
          promise.reject(PhotoLibraryException.noActivity())
          return@post
        }
        permissionPending = true
        ownsRequest = true
        observer = object : DefaultLifecycleObserver {
          override fun onDestroy(owner: LifecycleOwner) { finish(PhotoLibraryException.noActivity()) }
        }
        activity.lifecycle.addObserver(observer!!)
        launcher = activity.activityResultRegistry.register(
          "onekey-photo-save-${UUID.randomUUID()}", ActivityResultContracts.RequestPermission(),
        ) { finish() }
        val preferences = context().getSharedPreferences("onekey-photo-library", Context.MODE_PRIVATE)
        val previouslyRequested = preferences.getBoolean(REQUESTED, false)
        preferences.edit().putBoolean(REQUESTED, true).apply()
        try {
          launcher!!.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } catch (error: Exception) {
          preferences.edit().putBoolean(REQUESTED, previouslyRequested).apply()
          throw error
        }
      } catch (error: Exception) { finish(error) }
    }
    return promise
  }

  fun save(path: String): Promise<Unit> {
    val promise = Promise<Unit>()
    saveExecutor.execute {
      try {
        val context = context()
        if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
          throw PhotoLibraryException.noLibraryPermission()
        }
        val file = when {
          path.startsWith("/") -> File(path)
          path.startsWith("file://") -> Uri.parse(path).let { uri ->
            if (uri.authority.isNullOrEmpty() || uri.authority == "localhost") uri.path?.let { File(it) } else null
          }
          else -> null
        } ?: throw PhotoLibraryException.noImageData()
        val mime = imageMime(file)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "img"
        val name = "onekey-${UUID.randomUUID()}.$extension"
        if (Build.VERSION.SDK_INT >= 29) saveModern(context, file, mime, name) else saveLegacy(context, file, mime, name)
        promise.resolve(Unit)
      } catch (error: PhotoLibraryException) {
        promise.reject(error)
      } catch (error: Exception) {
        promise.reject(PhotoLibraryException.cannotSaveImage(error))
      }
    }
    return promise
  }

  private fun saveModern(context: Context, source: File, mime: String, name: String) {
    val resolver = context.contentResolver
    val values = ContentValues().apply {
      put(MediaStore.Images.Media.DISPLAY_NAME, name)
      put(MediaStore.Images.Media.MIME_TYPE, mime)
      put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/OneKey")
      put(MediaStore.Images.Media.IS_PENDING, 1)
    }
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
      ?: throw PhotoLibraryException.cannotSaveImage()
    try {
      val stream = resolver.openOutputStream(uri) ?: throw PhotoLibraryException.cannotSaveImage()
      stream.use { output -> source.inputStream().use { it.copyTo(output) } }
      val published = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
      if (resolver.update(uri, published, null, null) != 1) throw PhotoLibraryException.cannotSaveImage()
    } catch (error: Exception) {
      try { resolver.delete(uri, null, null) } catch (_: Exception) { }
      throw error
    }
  }

  @Suppress("DEPRECATION")
  private fun saveLegacy(context: Context, source: File, mime: String, name: String) {
    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "OneKey")
    if (!directory.isDirectory && !directory.mkdirs()) throw PhotoLibraryException.cannotSaveImage()
    val destination = File(directory, name)
    try {
      source.inputStream().use { input -> destination.outputStream().use { input.copyTo(it) } }
      val scanned = CountDownLatch(1)
      var savedUri: Uri? = null
      MediaScannerConnection.scanFile(context, arrayOf(destination.absolutePath), arrayOf(mime)) { _, uri ->
        savedUri = uri
        scanned.countDown()
      }
      if (!scanned.await(30, TimeUnit.SECONDS) || savedUri == null) throw PhotoLibraryException.cannotSaveImage()
    } catch (error: Exception) {
      destination.delete()
      throw error
    }
  }
}
