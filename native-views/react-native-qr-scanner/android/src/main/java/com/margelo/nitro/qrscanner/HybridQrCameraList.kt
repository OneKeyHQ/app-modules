package com.margelo.nitro.qrscanner

import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraIdentifier
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraPresenceListener
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise

/** Lists cameras for the live scanner and reports connects and disconnects. */
@DoNotStrip
class HybridQrCameraList : HybridQrCameraListSpec() {
  private val main = Handler(Looper.getMainLooper())
  private var listener: (() -> Unit)? = null
  private var registration: Pair<ProcessCameraProvider, CameraPresenceListener>? = null
  private var pending = false
  private var knownIds = emptySet<String>()

  override fun listCameras(): Promise<Array<QrCameraInfo>> {
    val promise = Promise<Array<QrCameraInfo>>()
    val context = NitroModules.applicationContext
    if (context == null) {
      promise.resolve(emptyArray())
      return promise
    }
    val future = ProcessCameraProvider.getInstance(context)
    future.addListener({
      try {
        promise.resolve(describe(future.get().availableCameraInfos))
      } catch (error: Exception) {
        promise.reject(Exception("camera-busy: ${error.message}"))
      }
    }, ContextCompat.getMainExecutor(context))
    return promise
  }

  override fun setOnChange(listener: (() -> Unit)?) {
    main.post {
      this.listener = listener
      if (listener == null) {
        registration?.let { (provider, callback) -> provider.removeCameraPresenceListener(callback) }
        registration = null
        return@post
      }
      if (pending || registration != null) return@post
      val context = NitroModules.applicationContext ?: return@post
      val executor = ContextCompat.getMainExecutor(context)
      val future = ProcessCameraProvider.getInstance(context)
      pending = true
      future.addListener({
        pending = false
        if (this.listener == null) return@addListener
        val provider = try { future.get() } catch (_: Exception) { return@addListener }
        knownIds = provider.availableCameraInfos.map(::cameraId).toSet()
        val callback = object : CameraPresenceListener {
          override fun onCamerasAdded(cameraIdentifiers: Set<CameraIdentifier>) = check()
          override fun onCamerasRemoved(cameraIdentifiers: Set<CameraIdentifier>) = check()

          private fun check() {
            if (registration?.second !== this) return
            val ids = provider.availableCameraInfos.map(::cameraId).toSet()
            if (ids == knownIds) return
            knownIds = ids
            this@HybridQrCameraList.listener?.invoke()
          }
        }
        registration = provider to callback
        provider.addCameraPresenceListener(executor, callback)
      }, executor)
    }
  }

  companion object {
    @OptIn(ExperimentalCamera2Interop::class)
    fun cameraId(info: CameraInfo): String = Camera2CameraInfo.from(info).cameraId

    fun position(info: CameraInfo): QrCameraPosition = when (info.lensFacing) {
      CameraSelector.LENS_FACING_BACK -> QrCameraPosition.BACK
      CameraSelector.LENS_FACING_FRONT -> QrCameraPosition.FRONT
      else -> QrCameraPosition.EXTERNAL
    }

    fun describe(infos: List<CameraInfo>): Array<QrCameraInfo> {
      val picked = QrCameraCatalog.pick(infos.map { QrCameraCatalog.Candidate(cameraId(it), position(it)) })
      var external = 0
      return picked.map { candidate ->
        val label = QrCameraCatalog.label(candidate.position, external)
        if (candidate.position == QrCameraPosition.EXTERNAL) external += 1
        QrCameraInfo(candidate.id, candidate.position, label)
      }.toTypedArray()
    }
  }
}
