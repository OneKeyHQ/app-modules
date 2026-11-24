package com.margelo.nitro.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Size
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
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
import java.util.concurrent.TimeUnit

/**
 * CameraX preview plus ML Kit QR detection. Native code reports raw text only;
 * mode handling and BC-UR assembly run in JS through `./ur`.
 */
@DoNotStrip
class HybridQrScanner(val context: ThemedReactContext) : HybridQrScannerSpec(), LifecycleOwner {
  private companion object {
    const val PERMISSION_REQUEST_CODE = 0x5152
    const val FOCUS_INDICATOR_DP = 72
    // A tapped focus point is held this long, then CameraX returns to
    // continuous focus over the whole frame.
    const val FOCUS_HOLD_SECONDS = 5L

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
  private val density = context.resources.displayMetrics.density
  private val focusIndicator = View(context).apply {
    val size = (FOCUS_INDICATOR_DP * density).toInt()
    layoutParams = FrameLayout.LayoutParams(size, size)
    background = GradientDrawable().apply {
      setStroke((1.5f * density).toInt().coerceAtLeast(1), Color.WHITE)
      cornerRadius = 8 * density
    }
    alpha = 0f
    isClickable = false
  }
  private val container = FrameLayout(context).apply {
    addView(previewView, FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT,
    ))
    addView(focusIndicator)
  }
  override val view: View
    get() = container

  // Props
  override var active: Boolean = false
  override var cameraId: String? = null
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
  /** The bound camera while running. Main thread only. */
  private var camera: Camera? = null
  /** Last `cameraId` prop value, and the id actually bound. Main thread only. */
  private var requestedCameraId: String? = null
  private var boundCameraId: String? = null
  private var pinchRatio = 1f
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
    installGestures()
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun installGestures() {
    val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
      override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        val zoom = camera?.cameraInfo?.zoomState?.value ?: return false
        pinchRatio = zoom.zoomRatio
        return true
      }

      override fun onScale(detector: ScaleGestureDetector): Boolean {
        val camera = camera ?: return false
        val zoom = camera.cameraInfo.zoomState.value ?: return false
        pinchRatio *= detector.scaleFactor
        pinchRatio = QrCameraZoom.clampRatio(pinchRatio, zoom.minZoomRatio, zoom.maxZoomRatio)
        camera.cameraControl.setZoomRatio(pinchRatio)
        return true
      }
    })
    val tapDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
      override fun onDown(event: MotionEvent): Boolean = true

      override fun onSingleTapUp(event: MotionEvent): Boolean {
        focusAt(event.x, event.y)
        return true
      }
    })
    container.setOnTouchListener { _, event ->
      scaleDetector.onTouchEvent(event)
      if (!scaleDetector.isInProgress) tapDetector.onTouchEvent(event)
      true
    }
  }

  private fun focusAt(x: Float, y: Float) {
    val camera = camera ?: return
    showFocusIndicator(x, y)
    val point = previewView.meteringPointFactory.createPoint(x, y)
    val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
      .setAutoCancelDuration(FOCUS_HOLD_SECONDS, TimeUnit.SECONDS)
      .build()
    // Rejected when the camera cannot meter that point; scanning goes on.
    camera.cameraControl.startFocusAndMetering(action)
  }

  private fun showFocusIndicator(x: Float, y: Float) {
    focusIndicator.animate().cancel()
    focusIndicator.x = x - focusIndicator.width / 2f
    focusIndicator.y = y - focusIndicator.height / 2f
    focusIndicator.alpha = 1f
    focusIndicator.scaleX = 1.25f
    focusIndicator.scaleY = 1.25f
    focusIndicator.animate().scaleX(1f).scaleY(1f).setDuration(200).withEndAction {
      focusIndicator.animate().alpha(0f).setStartDelay(800).setDuration(300)
    }
  }

  override fun afterUpdate() {
    val shouldRun = active
    val requested = cameraId
    main.post {
      requestedCameraId = requested
      when {
        !shouldRun -> stop()
        running -> switchCameraIfNeeded()
        else -> start()
      }
    }
  }

  /** Rebinds to a changed `cameraId` in a new capture generation. */
  private fun switchCameraIfNeeded() {
    val provider = cameraProvider ?: return
    val target = selectorFor(provider) ?: return
    if (target.second == boundCameraId) return
    bind(provider, gate.nextGeneration())
  }

  /** The requested camera when connected, else back, else front. */
  private fun selectorFor(provider: ProcessCameraProvider): Pair<CameraSelector, String>? {
    val infos = provider.availableCameraInfos
    val requested = requestedCameraId?.let { id -> infos.firstOrNull { HybridQrCameraList.cameraId(it) == id } }
    val info = requested
      ?: infos.firstOrNull { it.lensFacing == CameraSelector.LENS_FACING_BACK }
      ?: infos.firstOrNull { it.lensFacing == CameraSelector.LENS_FACING_FRONT }
      ?: infos.firstOrNull()
      ?: return null
    val id = HybridQrCameraList.cameraId(info)
    val selector = CameraSelector.Builder()
      .addCameraFilter { candidates -> candidates.filter { HybridQrCameraList.cameraId(it) == id } }
      .build()
    return selector to id
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
    val (selector, selectedId) = selectorFor(provider) ?: run {
      emitError("no-camera", "No camera is available", generation)
      return
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
      camera = provider.bindToLifecycle(this, selector, preview, analysis)
      boundCameraId = selectedId
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
    camera = null
    boundCameraId = null
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
