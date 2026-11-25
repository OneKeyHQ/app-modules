package com.margelo.nitro.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.RectF
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
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.view.TransformExperimental
import androidx.camera.view.transform.CoordinateTransform
import androidx.camera.view.transform.ImageProxyTransformFactory
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
  internal companion object {
    const val DECODE_ERROR_INTERVAL_MS = 1_000L
    const val FOCUS_INDICATOR_DP = 72
    // A tapped focus point is held this long, then CameraX returns to
    // continuous focus over the whole frame.
    const val FOCUS_HOLD_SECONDS = 5L

    // Shared and never shut down: ML Kit completion listeners may still be
    // queued on it after a view is dropped.
    val analysisExecutor = Executors.newSingleThreadExecutor { runnable ->
      Thread(runnable, "QrScannerAnalysis").apply { isDaemon = true }
    }

    /**
     * Maps a bounding rect in upright `InputImage` pixel coordinates into
     * normalized `[0, 1]` visible `PreviewView` coordinates under `FILL_CENTER`
     * (`cover`), optionally mirrored horizontally for the front camera.
     */
    fun mapFillCenterRect(
      left: Double,
      top: Double,
      right: Double,
      bottom: Double,
      imageWidth: Double,
      imageHeight: Double,
      viewWidth: Double,
      viewHeight: Double,
      mirrored: Boolean,
    ): Pair<Pair<Double, Double>, QrSymbolBounds> {
      val scale = maxOf(viewWidth / imageWidth, viewHeight / imageHeight)
      val offsetX = (imageWidth * scale - viewWidth) / 2.0
      val offsetY = (imageHeight * scale - viewHeight) / 2.0
      val rawLeft = (left * scale - offsetX) / viewWidth
      val rawRight = (right * scale - offsetX) / viewWidth
      val rawTop = (top * scale - offsetY) / viewHeight
      val rawBottom = (bottom * scale - offsetY) / viewHeight
      val rawCx = ((left + right) * 0.5 * scale - offsetX) / viewWidth
      val rawCy = ((top + bottom) * 0.5 * scale - offsetY) / viewHeight
      val cx = if (mirrored) 1.0 - rawCx else rawCx
      val normLeft = if (mirrored) 1.0 - rawRight else rawLeft
      val normWidth = (rawRight - rawLeft).coerceAtLeast(0.0)
      val normHeight = (rawBottom - rawTop).coerceAtLeast(0.0)
      return (cx to rawCy) to QrSymbolBounds(normLeft, rawTop, normWidth, normHeight)
    }
  }

  private fun newPreviewView() = PreviewView(context).apply {
    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    scaleType = PreviewView.ScaleType.FILL_CENTER
    setBackgroundColor(Color.BLACK)
  }
  private var previewView = newPreviewView()
  private val frozenImageView = ImageView(context).apply {
    layoutParams = FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT,
    )
    scaleType = ImageView.ScaleType.FIT_XY
    visibility = View.GONE
    isClickable = false
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
    addView(frozenImageView)
    addView(focusIndicator)
  }
  override val view: View
    get() = container

  // Props
  override var active: Boolean = false
  override var cameraId: String? = null
  override var multiSymbol: Boolean? = null
  override var frozen: Boolean? = null
  override var onReady: ((generation: Double, cameraId: String) -> Unit)? = null
  override var onFrameText: ((text: String, generation: Double) -> Unit)? = null
  override var onFrameSymbols: ((symbols: Array<QrDetectedSymbol>, generation: Double) -> Unit)? = null
  override var onError: ((code: String, message: String, generation: Double) -> Unit)? = null

  private data class CaptureCallbacks(
    val multiSymbol: Boolean = false,
    val frozen: Boolean = false,
    val ready: ((Double, String) -> Unit)? = null,
    val frame: ((String, Double) -> Unit)? = null,
    val symbols: ((Array<QrDetectedSymbol>, Double) -> Unit)? = null,
    val error: ((String, String, Double) -> Unit)? = null,
  )
  @Volatile private var callbacks = CaptureCallbacks()
  @Volatile private var boundLensFacing: Int = CameraSelector.LENS_FACING_BACK
  private var isFrozenMain = false

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
    installGestures()
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
      if (isFrozenMain) return@setOnTouchListener false
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
    val nextFrozen = frozen == true
    val nextCallbacks = CaptureCallbacks(
      multiSymbol = multiSymbol == true,
      frozen = nextFrozen,
      ready = onReady,
      frame = onFrameText,
      symbols = onFrameSymbols,
      error = onError,
    )
    main.post {
      if (dropped) return@post
      callbacks = nextCallbacks
      applyFrozen(if (shouldRun) nextFrozen else false)
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

  private var pendingFreezeSnapshot = false
  private var pendingBindLayoutListener: View.OnLayoutChangeListener? = null
  private var pendingBindLayoutHost: View? = null

  private fun captureFrozenSnapshot(): Boolean {
    val snapshot = previewView.bitmap ?: return false
    frozenImageView.setImageBitmap(snapshot)
    frozenImageView.visibility = View.VISIBLE
    pendingFreezeSnapshot = false
    return true
  }

  private fun applyFrozen(nextFrozen: Boolean) {
    if (isFrozenMain == nextFrozen) {
      if (nextFrozen && pendingFreezeSnapshot) captureFrozenSnapshot()
      return
    }
    isFrozenMain = nextFrozen
    if (nextFrozen) {
      focusIndicator.animate().cancel()
      focusIndicator.alpha = 0f
      if (!captureFrozenSnapshot()) {
        // PreviewView is not streaming yet; retry once STREAMING arrives.
        pendingFreezeSnapshot = true
      }
    } else {
      pendingFreezeSnapshot = false
      frozenImageView.visibility = View.GONE
      frozenImageView.setImageBitmap(null)
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
      applyFrozen(false)
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
    val targetPreview = newPreviewView()
    previewView = targetPreview
    container.removeView(oldPreview)
    container.addView(targetPreview, 0, FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT,
    ))
    // React Native Fabric's ReactViewGroup.requestLayout() is a no-op for
    // dynamically inserted children, so lay out targetPreview synchronously
    // using container's committed bounds when available; only defer to a
    // layout listener on `container` if container itself is still 0x0.
    val layoutAndBind = {
      val w = container.width
      val h = container.height
      if (w > 0 && h > 0) {
        targetPreview.measure(
          View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
          View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY),
        )
        targetPreview.layout(0, 0, w, h)
      }
      bindLaidOutPreview(provider, selector, targetPreview, generation)
    }
    if (container.width > 0 && container.height > 0) {
      layoutAndBind()
      return
    }
    val listener = object : View.OnLayoutChangeListener {
      override fun onLayoutChange(
        v: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int,
      ) {
        if (right - left <= 0 || bottom - top <= 0) return
        v.removeOnLayoutChangeListener(this)
        if (pendingBindLayoutListener === this) {
          pendingBindLayoutListener = null
          pendingBindLayoutHost = null
        }
        if (gate.currentGeneration != generation || !shouldRun || dropped || previewView !== targetPreview) {
          return
        }
        layoutAndBind()
      }
    }
    pendingBindLayoutListener = listener
    pendingBindLayoutHost = container
    container.addOnLayoutChangeListener(listener)
  }

  private fun bindLaidOutPreview(
    provider: ProcessCameraProvider,
    selector: CameraSelector,
    targetPreview: PreviewView,
    generation: Int,
  ) {
    val preview = Preview.Builder().build().also { it.surfaceProvider = targetPreview.surfaceProvider }
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
    analysis.setAnalyzer(analysisExecutor) { proxy -> analyze(proxy, generation) }
    lastDecodeFailureAt = null
    val bound = try {
      val groupBuilder = UseCaseGroup.Builder()
        .addUseCase(preview)
        .addUseCase(analysis)
      targetPreview.viewPort?.let { groupBuilder.setViewPort(it) }
      provider.bindToLifecycle(this, selector, groupBuilder.build())
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
    boundLensFacing = bound.cameraInfo.lensFacing
    running = true
    var ready = false
    var lastErrorCode: Int? = null
    val previewStateObserver = Observer<PreviewView.StreamState> { state ->
      if (state == PreviewView.StreamState.STREAMING && gate.currentGeneration == generation) {
        if (!ready) {
          ready = true
          gate.setCapturing(true)
          callbacks.ready?.invoke(generation.toDouble(), activeCameraId)
        }
        if (isFrozenMain && pendingFreezeSnapshot && !captureFrozenSnapshot()) {
          targetPreview.post {
            if (isFrozenMain && pendingFreezeSnapshot && gate.currentGeneration == generation) {
              captureFrozenSnapshot()
            }
          }
        }
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
    targetPreview.previewStreamState.observeForever(previewStateObserver)
    bound.cameraInfo.cameraState.observeForever(cameraStateObserver)
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
    pendingBindLayoutListener?.let { listener ->
      pendingBindLayoutHost?.removeOnLayoutChangeListener(listener)
    }
    pendingBindLayoutListener = null
    pendingBindLayoutHost = null
    // Preserve frozenImageView and isFrozenMain across temporary rebinds
    // (camera switch or host pause/resume) while `frozen` remains true;
    // `afterUpdate` and `dispose` clear the freeze explicitly.
    pinchGeneration = null
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
  @OptIn(ExperimentalGetImage::class, TransformExperimental::class)
  private fun analyze(proxy: ImageProxy, generation: Int) {
    // Capture callbacks before async ML Kit decode so session-identity guards
    // on the original callback still discard stale frames if sessionKey/mode
    // changed mid-decode (SPEC section 4).
    val frameCallbacks = callbacks
    if (frameCallbacks.frozen) {
      proxy.close()
      return
    }
    val mediaImage = proxy.image
    if (mediaImage == null) {
      proxy.close()
      return
    }
    val rotation = proxy.imageInfo.rotationDegrees
    val rotatedWidth = if (rotation == 90 || rotation == 270) proxy.height else proxy.width
    val rotatedHeight = if (rotation == 90 || rotation == 270) proxy.width else proxy.height
    val sourceTransform = if (frameCallbacks.multiSymbol && frameCallbacks.symbols != null) {
      try {
        ImageProxyTransformFactory().apply {
          isUsingRotationDegrees = true
          isUsingCropRect = false
        }.getOutputTransform(proxy)
      } catch (_: Exception) {
        null
      }
    } else {
      null
    }
    val task = try {
      val input = InputImage.fromMediaImage(mediaImage, rotation)
      scanner.process(input)
    } catch (_: RuntimeException) {
      proxy.close()
      reportDecodeFailure(generation)
      return
    }
    task
      .addOnSuccessListener(analysisExecutor) { barcodes ->
        if (callbacks.frozen) return@addOnSuccessListener

        val symbolsCallback = frameCallbacks.symbols
        if (frameCallbacks.multiSymbol && symbolsCallback != null) {
          val nonEmpty = barcodes.filter { !it.rawValue.isNullOrEmpty() }
          if (nonEmpty.isEmpty()) return@addOnSuccessListener
          val isMirrored = boundLensFacing == CameraSelector.LENS_FACING_FRONT
          main.post {
            if (gate.currentGeneration != generation || isFrozenMain) return@post
            val viewW = previewView.width.toDouble()
            val viewH = previewView.height.toDouble()
            if (viewW <= 0 || viewH <= 0 || rotatedWidth <= 0 || rotatedHeight <= 0) return@post
            val targetTransform = previewView.outputTransform
            val coordinateTransform = if (sourceTransform != null && targetTransform != null) {
              CoordinateTransform(sourceTransform, targetTransform)
            } else {
              null
            }
            val imgW = rotatedWidth.toDouble()
            val imgH = rotatedHeight.toDouble()
            val symbols = nonEmpty.mapNotNull { barcode ->
              val text = barcode.rawValue?.takeIf(String::isNotEmpty) ?: return@mapNotNull null
              val box = barcode.boundingBox
              if (box != null) {
                if (coordinateTransform != null) {
                  val mapped = RectF(box)
                  coordinateTransform.mapRect(mapped)
                  val cx = ((mapped.left + mapped.right) * 0.5) / viewW
                  val cy = ((mapped.top + mapped.bottom) * 0.5) / viewH
                  val bounds = QrSymbolBounds(
                    x = mapped.left.toDouble() / viewW,
                    y = mapped.top.toDouble() / viewH,
                    width = mapped.width().toDouble().coerceAtLeast(0.0) / viewW,
                    height = mapped.height().toDouble().coerceAtLeast(0.0) / viewH,
                  )
                  QrDetectedSymbol(
                    text = text,
                    centerX = cx,
                    centerY = cy,
                    bounds = bounds,
                  )
                } else {
                  val (center, bounds) = mapFillCenterRect(
                    left = box.left.toDouble(),
                    top = box.top.toDouble(),
                    right = box.right.toDouble(),
                    bottom = box.bottom.toDouble(),
                    imageWidth = imgW,
                    imageHeight = imgH,
                    viewWidth = viewW,
                    viewHeight = viewH,
                    mirrored = isMirrored,
                  )
                  QrDetectedSymbol(
                    text = text,
                    centerX = center.first,
                    centerY = center.second,
                    bounds = bounds,
                  )
                }
              } else {
                QrDetectedSymbol(text = text, centerX = 0.5, centerY = 0.5, bounds = null)
              }
            }.toTypedArray()
            if (symbols.isNotEmpty()) {
              symbolsCallback.invoke(symbols, generation.toDouble())
            }
          }
          return@addOnSuccessListener
        }

        val texts = barcodes.mapNotNull { it.rawValue }
        if (texts.isEmpty()) return@addOnSuccessListener
        val reports = gate.filter(texts, SystemClock.elapsedRealtime(), generation)
        if (reports.isNotEmpty()) main.post {
          if (gate.currentGeneration != generation || isFrozenMain) return@post
          for (report in reports) frameCallbacks.frame?.invoke(report.text, report.generation.toDouble())
        }
      }
      .addOnCompleteListener(analysisExecutor) { result ->
        proxy.close()
        if (!result.isSuccessful && !result.isCanceled) reportDecodeFailure(generation)
      }
  }
}
