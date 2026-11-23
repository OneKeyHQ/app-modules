package com.margelo.nitro.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Size
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.uimanager.ThemedReactContext
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

/**
 * CameraX preview plus ML Kit QR detection. Native code reports raw text only;
 * mode handling and BC-UR assembly run in JS through `./ur`.
 */
@DoNotStrip
class HybridQrScanner(val context: ThemedReactContext) : HybridQrScannerSpec(), LifecycleOwner {
  private companion object {
    const val PERMISSION_REQUEST_CODE = 0x5152

    // Shared and never shut down: ML Kit completion listeners may still be
    // queued on it after a view is dropped.
    val analysisExecutor = Executors.newSingleThreadExecutor { runnable ->
      Thread(runnable, "QrScannerAnalysis").apply { isDaemon = true }
    }
  }

  private val previewView = PreviewView(context).apply {
    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    scaleType = PreviewView.ScaleType.FILL_CENTER
    setBackgroundColor(Color.BLACK)
  }
  override val view: View
    get() = previewView

  // Props
  override var active: Boolean = false
  override var onReady: ((generation: Double) -> Unit)? = null
  override var onFrameText: ((text: String, generation: Double) -> Unit)? = null
  override var onError: ((code: String, message: String, generation: Double) -> Unit)? = null

  // The camera follows this lifecycle: RESUMED only while capturing and the
  // host activity is resumed. Touched on the main thread only.
  private val lifecycleRegistry = LifecycleRegistry(this)
  override val lifecycle: Lifecycle
    get() = lifecycleRegistry

  private val main = Handler(Looper.getMainLooper())
  private val gate = QrFrameTextGate(repeatIntervalMs = 1_000)
  private val scanner = BarcodeScanning.getClient(
    BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build(),
  )
  private var cameraProvider: ProcessCameraProvider? = null
  private var running = false
  private var hostResumed = true
  private var dropped = false
  private val hostObserver = LifecycleEventObserver { _, event ->
    when (event) {
      Lifecycle.Event.ON_RESUME -> { hostResumed = true; syncLifecycle() }
      Lifecycle.Event.ON_PAUSE -> { hostResumed = false; syncLifecycle() }
      else -> Unit
    }
  }

  init {
    main.post {
      lifecycleRegistry.currentState = Lifecycle.State.CREATED
      (context.currentActivity as? LifecycleOwner)?.lifecycle?.addObserver(hostObserver)
    }
  }

  override fun afterUpdate() {
    val shouldRun = active
    main.post { if (shouldRun) start() else stop() }
  }

  override fun onDropView() {
    main.post {
      dropped = true
      stop()
      (context.currentActivity as? LifecycleOwner)?.lifecycle?.removeObserver(hostObserver)
      lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
      scanner.close()
    }
  }

  private fun syncLifecycle() {
    if (lifecycleRegistry.currentState == Lifecycle.State.DESTROYED) return
    lifecycleRegistry.currentState =
      if (running && hostResumed) Lifecycle.State.RESUMED else Lifecycle.State.CREATED
  }

  private fun emitError(code: String, message: String, generation: Int) {
    onError?.invoke(code, message, generation.toDouble())
  }

  private fun start() {
    if (running || dropped) return
    val generation = gate.nextGeneration()
    // Check for hardware first: a device without a camera must not prompt.
    if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
      emitError("no-camera", "No camera is available", generation)
      return
    }
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
      PackageManager.PERMISSION_GRANTED
    ) {
      val activity = context.currentActivity as? PermissionAwareActivity
      if (activity == null) {
        emitError("permission-denied", "Camera access is not allowed", generation)
        return
      }
      activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE) { code, _, results ->
        if (code != PERMISSION_REQUEST_CODE) return@requestPermissions false
        main.post {
          if (gate.currentGeneration != generation || !active || dropped) return@post
          if (results.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            start()
          } else {
            emitError("permission-denied", "Camera access was denied", generation)
          }
        }
        true
      }
      return
    }
    val future = ProcessCameraProvider.getInstance(context)
    future.addListener({
      if (gate.currentGeneration != generation || !active || dropped) return@addListener
      val provider = try {
        future.get()
      } catch (error: Exception) {
        emitError("camera-busy", error.message ?: "The camera could not start", generation)
        return@addListener
      }
      cameraProvider = provider
      bind(provider, generation)
    }, ContextCompat.getMainExecutor(context))
  }

  private fun bind(provider: ProcessCameraProvider, generation: Int) {
    val selector = when {
      provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> CameraSelector.DEFAULT_BACK_CAMERA
      provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.DEFAULT_FRONT_CAMERA
      else -> {
        emitError("no-camera", "No camera is available", generation)
        return
      }
    }
    val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
    val analysis = ImageAnalysis.Builder()
      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
      .setResolutionSelector(
        ResolutionSelector.Builder()
          .setResolutionStrategy(
            ResolutionStrategy(Size(1280, 720), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER),
          )
          .build(),
      )
      .build()
    analysis.setAnalyzer(analysisExecutor, ::analyze)
    try {
      provider.unbindAll()
      provider.bindToLifecycle(this, selector, preview, analysis)
    } catch (error: Exception) {
      emitError("camera-busy", error.message ?: "The camera could not start", generation)
      return
    }
    running = true
    gate.setCapturing(true)
    syncLifecycle()
    onReady?.invoke(generation.toDouble())
  }

  private fun stop() {
    // A new generation makes late detections from the old one stale.
    gate.nextGeneration()
    gate.setCapturing(false)
    if (!running) return
    cameraProvider?.unbindAll()
    running = false
    syncLifecycle()
  }

  /** Runs on the analysis executor; KEEP_ONLY_LATEST keeps one frame in flight. */
  @OptIn(ExperimentalGetImage::class)
  private fun analyze(proxy: ImageProxy) {
    val mediaImage = proxy.image
    if (mediaImage == null) {
      proxy.close()
      return
    }
    val input = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
    scanner.process(input)
      .addOnSuccessListener(analysisExecutor) { barcodes ->
        val texts = barcodes.mapNotNull { it.rawValue }
        if (texts.isEmpty()) return@addOnSuccessListener
        for (report in gate.filter(texts, SystemClock.elapsedRealtime())) {
          onFrameText?.invoke(report.text, report.generation.toDouble())
        }
      }
      .addOnCompleteListener(analysisExecutor) { proxy.close() }
  }
}
