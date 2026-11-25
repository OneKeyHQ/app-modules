package com.margelo.nitro.qrscanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Size
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Surface
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
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
import androidx.lifecycle.Observer
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
    const val DECODE_ERROR_INTERVAL_MS = 1_000L
    const val FOCUS_INDICATOR_DP = 73
    // A tapped focus point is held this long, then CameraX returns to
    // continuous focus over the whole frame.
    const val FOCUS_HOLD_SECONDS = 5L

    // Shared and never shut down: ML Kit completion listeners may still be
    // queued on it after a view is dropped.
    val analysisExecutor = Executors.newSingleThreadExecutor { runnable ->
      Thread(runnable, "QrScannerAnalysis").apply { isDaemon = true }
    }
  }

  private fun newPreviewView() = PreviewView(context).apply {
    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    scaleType = PreviewView.ScaleType.FILL_CENTER
    setBackgroundColor(Color.BLACK)
  }
  private var previewView = newPreviewView()
  private val density = context.resources.displayMetrics.density
  private val focusIndicator = View(context).apply {
    val size = (FOCUS_INDICATOR_DP * density).toInt()
    layoutParams = FrameLayout.LayoutParams(size, size)
    background = GradientDrawable().apply {
      setStroke((1f * density).toInt().coerceAtLeast(1), Color.YELLOW)
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
  override var onReady: ((generation: Double, cameraId: String) -> Unit)? = null
  override var onFrameText: ((text: String, generation: Double) -> Unit)? = null
  override var onError: ((code: String, message: String, generation: Double) -> Unit)? = null

  private data class CaptureCallbacks(
    val ready: ((Double, String) -> Unit)? = null,
    val frame: ((String, Double) -> Unit)? = null,
    val error: ((String, String, Double) -> Unit)? = null,
  )
  @Volatile private var callbacks = CaptureCallbacks()

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
  private var previewUseCase: Preview? = null
  private var analysisUseCase: ImageAnalysis? = null
  private var streamObserver: Observer<PreviewView.StreamState>? = null
  private var cameraObserver: Observer<CameraState>? = null
  private var pinchRatio = 1f
  private var pinchGeneration: Int? = null
  private var shouldRun = false
  private var starting = false
  private class PermissionWait(var cancel: (() -> Unit)? = null)
  private var permissionWait: PermissionWait? = null
  private var permissionDenied = false
  private var lastDecodeFailureAt: Long? = null
  private var hostLifecycle: Lifecycle? = null
  private var running = false
  private var hostResumed = true
  private var dropped = false
  private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
  private var displayListenerRegistered = false
  private var lastTargetRotation: Int? = null
  private val displayListener = object : DisplayManager.DisplayListener {
    override fun onDisplayAdded(displayId: Int) = Unit
    override fun onDisplayRemoved(displayId: Int) = Unit
    override fun onDisplayChanged(displayId: Int) {
      val display = container.display ?: return
      if (display.displayId == displayId) {
        updateTargetRotation(display.rotation)
      }
    }
  }
  private val hideFocusRunnable = Runnable {
    focusIndicator.animate()
      .setStartDelay(0)
      .alpha(0f)
      .setDuration(200)
      .withEndAction(null)
      .start()
  }
  private val hostObserver = LifecycleEventObserver { _, event ->
    when (event) {
      Lifecycle.Event.ON_RESUME -> { hostResumed = true; start() }
      Lifecycle.Event.ON_PAUSE -> { hostResumed = false; stop() }
      Lifecycle.Event.ON_DESTROY -> dispose()
      else -> Unit
    }
  }

  init {
    main.post {
      lifecycleRegistry.currentState = Lifecycle.State.CREATED
      hostLifecycle = (context.currentActivity as? LifecycleOwner)?.lifecycle
      hostResumed = hostLifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) ?: true
      hostLifecycle?.addObserver(hostObserver)
    }
    container.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
      container.display?.rotation?.let(::updateTargetRotation)
    }
    installGestures()
  }

  private fun updateTargetRotation(rotation: Int) {
    if (lastTargetRotation == rotation) return
    lastTargetRotation = rotation
    previewUseCase?.targetRotation = rotation
    analysisUseCase?.targetRotation = rotation
  }

  @SuppressLint("ClickableViewAccessibility")
  private fun installGestures() {
    val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
      override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        val zoom = camera?.cameraInfo?.zoomState?.value ?: return false
        pinchGeneration = gate.currentGeneration
        pinchRatio = zoom.zoomRatio
        return true
      }

      override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (pinchGeneration != gate.currentGeneration) return false
        val camera = camera ?: return false
        val zoom = camera.cameraInfo.zoomState.value ?: return false
        pinchRatio *= detector.scaleFactor
        pinchRatio = QrCameraZoom.clampRatio(pinchRatio, zoom.minZoomRatio, zoom.maxZoomRatio)
        camera.cameraControl.setZoomRatio(pinchRatio)
        return true
      }
    }).apply { isQuickScaleEnabled = false; isStylusScaleEnabled = false }
    val tapDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
      override fun onDown(event: MotionEvent): Boolean = true

      override fun onSingleTapUp(event: MotionEvent): Boolean {
        focusAt(event.x, event.y)
        return true
      }
    }).apply { setOnDoubleTapListener(null) }
    var multiTouch = false
    container.setOnTouchListener { _, event ->
      when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> {
          multiTouch = false
          container.parent?.requestDisallowInterceptTouchEvent(true)
        }
        MotionEvent.ACTION_POINTER_DOWN -> {
          multiTouch = true
          val cancel = MotionEvent.obtain(event)
          cancel.action = MotionEvent.ACTION_CANCEL
          tapDetector.onTouchEvent(cancel)
          cancel.recycle()
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
          container.parent?.requestDisallowInterceptTouchEvent(false)
      }
      scaleDetector.onTouchEvent(event)
      if (!multiTouch) tapDetector.onTouchEvent(event)
      true
    }
  }

  private fun focusAt(x: Float, y: Float) {
    val camera = camera ?: return
    val point = previewView.meteringPointFactory.createPoint(x, y)
    val afOnly = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
    if (!camera.cameraInfo.isFocusMeteringSupported(afOnly)) return
    val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
      .setAutoCancelDuration(FOCUS_HOLD_SECONDS, TimeUnit.SECONDS)
      .build()
    showFocusIndicator(x, y)
    // Rejected when the camera cannot meter that point; scanning goes on.
    camera.cameraControl.startFocusAndMetering(action)
  }

  private fun showFocusIndicator(x: Float, y: Float) {
    main.removeCallbacks(hideFocusRunnable)
    focusIndicator.animate().cancel()
    focusIndicator.x = x - focusIndicator.width / 2f
    focusIndicator.y = y - focusIndicator.height / 2f
    focusIndicator.alpha = 0f
    focusIndicator.scaleX = 2f
    focusIndicator.scaleY = 2f
    focusIndicator.animate()
      .setStartDelay(0)
      .alpha(1f)
      .scaleX(1f)
      .scaleY(1f)
      .setDuration(200)
      .withEndAction {
        main.postDelayed(hideFocusRunnable, 800)
      }
      .start()
  }

  override fun afterUpdate() {
    val shouldRun = active
    val requested = cameraId
    val nextCallbacks = CaptureCallbacks(onReady, onFrameText, onError)
    main.post {
      if (dropped) return@post
      callbacks = nextCallbacks
      val changed = requested != requestedCameraId || shouldRun != this.shouldRun
      requestedCameraId = requested
      this.shouldRun = shouldRun
      when {
        !shouldRun -> {
          cancelPermissionWait()
          permissionDenied = false
          stop()
        }
        running -> switchCameraIfNeeded()
        changed -> start()
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

  override fun onDropView() = dispose()

  override fun dispose() {
    main.post {
      if (dropped) return@post
      dropped = true
      callbacks = CaptureCallbacks()
      cancelPermissionWait()
      stop()
      hostLifecycle?.removeObserver(hostObserver)
      hostLifecycle = null
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
    callbacks.error?.invoke(code, message, generation.toDouble())
  }

  private fun cancelPermissionWait() {
    permissionWait?.cancel?.invoke()
    permissionWait = null
  }

  private fun start() {
    if (running || dropped || !shouldRun || !hostResumed || starting || permissionWait != null) return
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
      if (permissionDenied) return
      val wait = PermissionWait()
      permissionWait = wait
      wait.cancel = QrCameraPermission.request(activity, container::hasWindowFocus) { result ->
        main.post {
          if (permissionWait !== wait) return@post
          permissionWait = null
          if (!shouldRun || dropped) return@post
          when (result) {
            QrCameraPermission.Result.GRANTED -> start()
            QrCameraPermission.Result.DENIED -> {
              permissionDenied = true
              emitError("permission-denied", "Camera access was denied", gate.currentGeneration)
            }
            QrCameraPermission.Result.CANCELLED ->
              emitError("camera-busy", "The camera permission prompt could not open", gate.currentGeneration)
          }
        }
      }
      return
    }
    starting = true
    val future = ProcessCameraProvider.getInstance(context)
    future.addListener({
      if (gate.currentGeneration != generation || !shouldRun || dropped) return@addListener
      starting = false
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
    val selector = selectorFor(provider)?.first ?: run {
      emitError("no-camera", "No camera is available", generation)
      return
    }
    unbind()
    val oldPreview = previewView
    previewView = newPreviewView()
    container.removeView(oldPreview)
    container.addView(previewView, 0, FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT,
    ))
    val initialRotation = container.display?.rotation ?: Surface.ROTATION_0
    lastTargetRotation = initialRotation
    val preview = Preview.Builder()
      .setTargetRotation(initialRotation)
      .build()
      .also { it.surfaceProvider = previewView.surfaceProvider }
    val analysis = ImageAnalysis.Builder()
      .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
      .setTargetRotation(initialRotation)
      .setResolutionSelector(
        ResolutionSelector.Builder()
          .setResolutionStrategy(
            ResolutionStrategy(Size(1280, 720), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER),
          )
          .build(),
      )
      .build()
    analysis.setAnalyzer(analysisExecutor) { proxy -> analyze(proxy, generation) }
    lastDecodeFailureAt = null
    val bound = try {
      provider.bindToLifecycle(this, selector, preview, analysis)
    } catch (error: Exception) {
      analysis.clearAnalyzer()
      provider.unbind(preview, analysis)
      emitError("camera-busy", error.message ?: "The camera could not start", generation)
      return
    }
    previewUseCase = preview
    analysisUseCase = analysis
    camera = bound
    val activeCameraId = HybridQrCameraList.cameraId(bound.cameraInfo)
    boundCameraId = activeCameraId
    running = true
    var ready = false
    var lastErrorCode: Int? = null
    val previewStateObserver = Observer<PreviewView.StreamState> { state ->
      if (state == PreviewView.StreamState.STREAMING &&
        gate.currentGeneration == generation && !ready
      ) {
        ready = true
        gate.setCapturing(true)
        callbacks.ready?.invoke(generation.toDouble(), activeCameraId)
      }
    }
    val cameraStateObserver = Observer<CameraState> { state ->
      if (gate.currentGeneration == generation) {
        val error = state.error
        if (error != null && error.code != lastErrorCode) {
          emitError("camera-busy", error.cause?.message ?: "The camera is unavailable", generation)
        }
        lastErrorCode = error?.code
      }
    }
    streamObserver = previewStateObserver
    cameraObserver = cameraStateObserver
    previewView.previewStreamState.observeForever(previewStateObserver)
    bound.cameraInfo.cameraState.observeForever(cameraStateObserver)
    if (!displayListenerRegistered) {
      displayManager?.registerDisplayListener(displayListener, main)
      displayListenerRegistered = true
    }
    syncLifecycle()
  }

  private fun stop() {
    // A new generation makes late detections from the old one stale.
    gate.nextGeneration()
    starting = false
    unbind()
  }

  private fun unbind() {
    gate.setCapturing(false)
    pinchGeneration = null
    main.removeCallbacks(hideFocusRunnable)
    if (displayListenerRegistered) {
      displayManager?.unregisterDisplayListener(displayListener)
      displayListenerRegistered = false
    }
    lastTargetRotation = null
    streamObserver?.let { previewView.previewStreamState.removeObserver(it) }
    cameraObserver?.let { camera?.cameraInfo?.cameraState?.removeObserver(it) }
    streamObserver = null
    cameraObserver = null
    val useCases = listOfNotNull(previewUseCase, analysisUseCase)
    analysisUseCase?.clearAnalyzer()
    previewUseCase = null
    analysisUseCase = null
    camera = null
    boundCameraId = null
    running = false
    syncLifecycle()
    if (useCases.isNotEmpty()) cameraProvider?.unbind(*useCases.toTypedArray())
  }

  private fun reportDecodeFailure(generation: Int) {
    main.post {
      if (!running || gate.currentGeneration != generation) return@post
      val now = SystemClock.elapsedRealtime()
      val last = lastDecodeFailureAt
      if (last != null && now - last < DECODE_ERROR_INTERVAL_MS) return@post
      lastDecodeFailureAt = now
      emitError("decoder-unavailable", "The camera QR decoder could not read a frame", generation)
    }
  }

  /** Runs on the analysis executor; KEEP_ONLY_LATEST keeps one frame in flight. */
  @OptIn(ExperimentalGetImage::class)
  private fun analyze(proxy: ImageProxy, generation: Int) {
    val mediaImage = proxy.image
    if (mediaImage == null) {
      proxy.close()
      return
    }
    val frameCallback = callbacks.frame
    val task = try {
      val input = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
      scanner.process(input)
    } catch (_: RuntimeException) {
      proxy.close()
      reportDecodeFailure(generation)
      return
    }
    task
      .addOnSuccessListener(analysisExecutor) { barcodes ->
        val texts = barcodes.mapNotNull { it.rawValue }
        if (texts.isEmpty()) return@addOnSuccessListener
        val reports = gate.filter(texts, SystemClock.elapsedRealtime(), generation)
        if (reports.isNotEmpty()) main.post {
          if (gate.currentGeneration != generation) return@post
          for (report in reports) frameCallback?.invoke(report.text, report.generation.toDouble())
        }
      }
      .addOnCompleteListener(analysisExecutor) { result ->
        proxy.close()
        if (!result.isSuccessful && !result.isCanceled) reportDecodeFailure(generation)
      }
  }
}
