package com.margelo.nitro.qrscanner

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraInfo
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
  private var callback: CameraManager.AvailabilityCallback? = null
  private val knownIds = HashSet<String>()

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
      val manager = NitroModules.applicationContext?.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        ?: return@post
      if (listener == null) {
        callback?.let { manager.unregisterAvailabilityCallback(it) }
        callback = null
        return@post
      }
      if (callback != null) return@post
      knownIds.clear()
      knownIds.addAll(manager.cameraIdList)
      // Availability also flips when any app opens a camera; only a new or
      // vanished id is a connect or disconnect.
      val next = object : CameraManager.AvailabilityCallback() {
        override fun onCameraAvailable(cameraId: String) = check(manager)
        override fun onCameraUnavailable(cameraId: String) = check(manager)
      }
      callback = next
      manager.registerAvailabilityCallback(next, main)
    }
  }

  private fun check(manager: CameraManager) {
    val ids = try {
      manager.cameraIdList.toHashSet()
    } catch (_: Exception) {
      return
    }
    if (ids == knownIds) return
    knownIds.clear()
    knownIds.addAll(ids)
    listener?.invoke()
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
