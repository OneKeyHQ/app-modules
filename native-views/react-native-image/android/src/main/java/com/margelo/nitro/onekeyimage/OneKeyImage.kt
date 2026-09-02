package com.margelo.nitro.onekeyimage

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.skeleton.OneKeySkeletonRenderer
import com.margelo.nitro.views.RecyclableView
import kotlin.math.ceil

private class OneKeyImageHostView(context: ThemedReactContext) : ImageView(context) {
  private val fallbackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.rgb(110, 110, 120)
    textAlign = Paint.Align.CENTER
  }
  private val skeleton = OneKeySkeletonRenderer { postInvalidateOnAnimation() }
  var drawStateSymbol = false
  var stateSymbol = "◇"
  var autoplayEnabled = false
  private var aggregatedVisible = true
  var skeletonRequested = false
    set(value) {
      field = value
      syncPlayback()
    }
  var onReadyForRequest: (() -> Unit)? = null

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    skeleton.updateBounds(w, h)
    fallbackPaint.textSize = minOf(w, h) * 0.35f
    if (w > 0 && h > 0) onReadyForRequest?.invoke()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (drawStateSymbol) {
      val baseline = height / 2f - (fallbackPaint.ascent() + fallbackPaint.descent()) / 2f
      canvas.drawText(stateSymbol, width / 2f, baseline, fallbackPaint)
    }
    if (skeletonRequested) skeleton.draw(canvas)
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    syncPlayback()
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    syncPlayback()
  }

  override fun onVisibilityAggregated(isVisible: Boolean) {
    super.onVisibilityAggregated(isVisible)
    aggregatedVisible = isVisible
    syncPlayback()
  }

  fun updateSkeletonStyle() {
    skeleton.updateStyle(null, 3.0)
    skeleton.updateBounds(width, height)
  }

  fun syncPlayback() {
    val canRun = isAttachedToWindow && aggregatedVisible
    if (skeletonRequested && canRun) skeleton.start() else skeleton.stop()
    val animated = drawable as? Animatable
    if (autoplayEnabled && canRun) animated?.start() else animated?.stop()
  }
}

internal fun DataSource.toOneKeyImageCacheType(): OneKeyImageCacheType = when (this) {
  DataSource.MEMORY_CACHE -> OneKeyImageCacheType.MEMORY
  DataSource.DATA_DISK_CACHE, DataSource.RESOURCE_DISK_CACHE -> OneKeyImageCacheType.DISK
  DataSource.LOCAL, DataSource.REMOTE -> OneKeyImageCacheType.NONE
}

internal fun oneKeyImageRequestSignature(
  rawUrl: String,
  sourceHeadersJson: String?,
  recyclingKey: String?,
  cachePolicy: OneKeyImageCachePolicy?,
  contentFit: OneKeyImageContentFit?,
  optimizeTos: Boolean?,
  overscan: Double?,
  width: Int,
  height: Int,
  density: Float,
): String = listOf(
  rawUrl,
  sourceHeadersJson.orEmpty(),
  recyclingKey.orEmpty(),
  cachePolicy?.name.orEmpty(),
  (contentFit ?: OneKeyImageContentFit.COVER).name,
  optimizeTos.toString(),
  overscan.toString(),
  width.toString(),
  height.toString(),
  density.toString(),
).joinToString("|")

@DoNotStrip
class HybridOneKeyImage(private val context: ThemedReactContext) :
  HybridOneKeyImageSpec(), RecyclableView {
  private enum class DisplayState { LOADING, IMAGE, ERROR, FALLBACK }

  private val hostView = OneKeyImageHostView(context)
  private var loadRunnable: Runnable? = null
  private var currentTarget: CustomViewTarget<OneKeyImageHostView, Drawable>? = null
  private var generation = 0L
  private var lastSignature: String? = null
  private var disposed = false
  private var suppressPropEffects = false
  private var requestActive = false
  private var displayState = DisplayState.LOADING
  private var displayRunnable: Runnable? = null
  private var fallbackRunnable: Runnable? = null

  override val view: View = hostView

  override var sourceUri: String? = null
    set(value) {
      if (field == value) return
      field = value
      requestIdentityChanged()
    }
  override var sourceHeadersJson: String? = null
    set(value) {
      if (field == value) return
      field = value
      requestIdentityChanged()
    }
  override var variant: OneKeyImageVariant? = OneKeyImageVariant.GENERIC
    set(value) {
      field = value
      if (!suppressPropEffects) applyVariant()
    }
  override var contentFit: OneKeyImageContentFit? = OneKeyImageContentFit.COVER
    set(value) {
      val changed = field != value
      field = value
      if (suppressPropEffects) return
      applyContentFit()
      if (changed) requestIdentityChanged()
    }
  override var cachePolicy: OneKeyImageCachePolicy? = OneKeyImageCachePolicy.MEMORY_DISK
    set(value) {
      if (field == value) return
      field = value
      requestIdentityChanged()
    }
  override var autoplay: Boolean? = false
    set(value) {
      field = value
      if (!suppressPropEffects) applyAutoplay()
    }
  override var recyclingKey: String? = null
    set(value) {
      if (field == value) return
      field = value
      requestIdentityChanged()
    }
  override var optimizeTos: Boolean? = true
    set(value) {
      if (field == value) return
      field = value
      requestIdentityChanged()
    }
  override var overscan: Double? = 1.1
    set(value) {
      if (field == value) return
      field = value
      requestIdentityChanged()
    }
  override var loadingStrategy: OneKeyImageLoadingStrategy? = OneKeyImageLoadingStrategy.STATIC
    set(value) {
      field = value
      if (!suppressPropEffects) applyVariant()
    }
  override var onLoadStart: (() -> Unit)? = null
  override var onLoad: ((width: Double, height: Double, cacheType: OneKeyImageCacheType) -> Unit)? = null
  override var onDisplay: (() -> Unit)? = null
  override var onError: ((message: String) -> Unit)? = null
  override var onLoadEnd: (() -> Unit)? = null

  init {
    OneKeyImageGlideRegistry.ensureRegistered(context)
    hostView.updateSkeletonStyle()
    hostView.onReadyForRequest = { scheduleLoad() }
    applyContentFit()
    applyVariant()
  }

  override fun afterUpdate() {
    super.afterUpdate()
    scheduleLoad()
  }

  override fun reload() {
    lastSignature = null
    scheduleLoad(force = true)
  }

  override fun cancel() {
    cancelCurrent(invalidateGeneration = true)
    showLoading(requestIsActive = false)
  }

  override fun prepareForRecycle() {
    resetForReuse()
  }

  override fun onDropView() {
    resetForReuse()
    disposed = true
    hostView.onReadyForRequest = null
    super.onDropView()
  }

  override fun dispose() {
    disposed = true
    cancelCurrent(invalidateGeneration = true)
    hostView.onReadyForRequest = null
    super.dispose()
  }

  private fun requestIdentityChanged() {
    if (suppressPropEffects || disposed) return
    lastSignature = null
    cancelCurrent(invalidateGeneration = true)
    if (sourceUri.isNullOrBlank()) showFallback() else showLoading(requestIsActive = false)
    scheduleLoad()
  }

  private fun resetForReuse() {
    cancelCurrent(invalidateGeneration = true)
    lastSignature = null
    suppressPropEffects = true
    sourceUri = null
    sourceHeadersJson = null
    variant = OneKeyImageVariant.GENERIC
    contentFit = OneKeyImageContentFit.COVER
    cachePolicy = OneKeyImageCachePolicy.MEMORY_DISK
    autoplay = false
    recyclingKey = null
    optimizeTos = true
    overscan = 1.1
    loadingStrategy = OneKeyImageLoadingStrategy.STATIC
    onLoadStart = null
    onLoad = null
    onDisplay = null
    onError = null
    onLoadEnd = null
    suppressPropEffects = false
    requestActive = false
    displayState = DisplayState.LOADING
    hostView.setImageDrawable(null)
    hostView.drawStateSymbol = false
    hostView.skeletonRequested = false
    hostView.setBackgroundColor(Color.TRANSPARENT)
    applyContentFit()
    applyAutoplay()
    hostView.invalidate()
  }

  private fun scheduleLoad(force: Boolean = false) {
    if (disposed) return
    loadRunnable?.let(hostView::removeCallbacks)
    val runnable = Runnable { startLoad(force) }
    loadRunnable = runnable
    hostView.post(runnable)
  }

  private fun startLoad(force: Boolean) {
    if (disposed) return
    if (hostView.width <= 0 || hostView.height <= 0) {
      cancelCurrent(invalidateGeneration = true)
      lastSignature = null
      showLoading(requestIsActive = false)
      return
    }
    val rawUrl = sourceUri?.takeIf { it.isNotBlank() }
    if (rawUrl == null) {
      cancelCurrent(invalidateGeneration = true)
      lastSignature = null
      showFallback()
      return
    }
    val signature = oneKeyImageRequestSignature(
      rawUrl = rawUrl,
      sourceHeadersJson = sourceHeadersJson,
      recyclingKey = recyclingKey,
      cachePolicy = cachePolicy,
      contentFit = contentFit,
      optimizeTos = optimizeTos,
      overscan = overscan,
      width = hostView.width,
      height = hostView.height,
      density = hostView.resources.displayMetrics.density,
    )
    if (!force && signature == lastSignature) return
    lastSignature = signature

    cancelCurrent(invalidateGeneration = true)
    val requestGeneration = generation
    onLoadStart?.invoke()

    val customIdentity = OneKeyImageModel.headers(sourceHeadersJson).isNotEmpty()
    val optimizedUrl = if (optimizeTos != false) {
      val density = hostView.resources.displayMetrics.density.coerceAtLeast(1f)
      OneKeyTosUrl.optimized(
        rawUrl,
        ceil(maxOf(hostView.width, hostView.height) / density.toDouble()).toInt(),
        density,
        overscan ?: 1.1,
        customIdentity,
      )
    } else rawUrl
    val decodeDimensions = OneKeyImageDecodeDimensions.forRender(hostView.width, hostView.height)
    performRequest(
      requestUrl = optimizedUrl,
      rawUrl = rawUrl,
      requestGeneration = requestGeneration,
      mayFallbackToRaw = optimizedUrl != rawUrl,
      headersJson = sourceHeadersJson,
      policy = cachePolicy ?: OneKeyImageCachePolicy.MEMORY_DISK,
      decodeDimensions = decodeDimensions,
    )
  }

  private fun performRequest(
    requestUrl: String,
    rawUrl: String,
    requestGeneration: Long,
    mayFallbackToRaw: Boolean,
    headersJson: String?,
    policy: OneKeyImageCachePolicy,
    decodeDimensions: OneKeyImageDecodeDimensions,
  ) {
    if (requestGeneration != generation || disposed) return
    clearCurrentTarget()
    var resolvedCacheType = OneKeyImageCacheType.NONE
    var failureMessage = "Image request failed: $requestUrl"
    var safetyFailure = false
    val target = object : CustomViewTarget<OneKeyImageHostView, Drawable>(hostView) {
      override fun onResourceLoading(placeholder: Drawable?) {
        if (requestGeneration != generation || disposed) return
        showLoading(requestIsActive = true)
      }

      override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        if (requestGeneration != generation || disposed) return
        requestActive = false
        displayState = DisplayState.IMAGE
        hostView.skeletonRequested = false
        hostView.drawStateSymbol = false
        hostView.setBackgroundColor(Color.TRANSPARENT)
        hostView.setImageDrawable(resource)
        applyAutoplay()
        val loadCallback = onLoad
        val loadEndCallback = onLoadEnd
        OneKeyTerminalEventPair.dispatch(
          primary = loadCallback?.let { callback ->
            {
              callback(
                resource.intrinsicWidth.coerceAtLeast(0).toDouble(),
                resource.intrinsicHeight.coerceAtLeast(0).toDouble(),
                resolvedCacheType,
              )
            }
          },
          onLoadEnd = loadEndCallback,
        )
        if (requestGeneration != generation || disposed) return
        scheduleOnDisplay(requestGeneration)
      }

      override fun onResourceCleared(placeholder: Drawable?) {
        if (requestGeneration == generation && currentTarget === this) {
          requestActive = false
          hostView.skeletonRequested = false
          hostView.setImageDrawable(null)
        }
      }

      override fun onLoadFailed(errorDrawable: Drawable?) {
        if (requestGeneration != generation || disposed) return
        if (mayFallbackToRaw && !safetyFailure) {
          // Glide forbids clear()/into() while executing a Target or listener
          // callback. Move the raw retry to the host queue so the failed
          // optimized request has fully left its callback first.
          val failedTarget = this
          val runnable = Runnable {
            fallbackRunnable = null
            if (
              requestGeneration == generation &&
              !disposed &&
              currentTarget === failedTarget
            ) {
              performRequest(
                requestUrl = rawUrl,
                rawUrl = rawUrl,
                requestGeneration = requestGeneration,
                mayFallbackToRaw = false,
                headersJson = headersJson,
                policy = policy,
                decodeDimensions = decodeDimensions,
              )
            }
          }
          fallbackRunnable?.let(hostView::removeCallbacks)
          fallbackRunnable = runnable
          hostView.post(runnable)
          return
        }
        finishWithError(failureMessage, requestGeneration)
      }
    }
    currentTarget = target
    Glide.with(hostView)
      .asDrawable()
      .load(OneKeyImageModel.build(requestUrl, headersJson))
      .apply(requestOptions(policy))
      .override(decodeDimensions.width, decodeDimensions.height)
      .listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
          error: GlideException?,
          model: Any?,
          target: Target<Drawable>,
          isFirstResource: Boolean,
        ): Boolean {
          failureMessage = error?.message ?: failureMessage
          safetyFailure = OneKeyImageSafety.isSafetyFailure(error)
          return false
        }

        override fun onResourceReady(
          resource: Drawable,
          model: Any,
          target: Target<Drawable>?,
          dataSource: DataSource,
          isFirstResource: Boolean,
        ): Boolean {
          resolvedCacheType = dataSource.toOneKeyImageCacheType()
          return false
        }
      })
      .into(target)
  }

  private fun requestOptions(policy: OneKeyImageCachePolicy): RequestOptions {
    val options = RequestOptions()
      .dontTransform()
      .downsample(OneKeyImageSafeDownsampleStrategy())
    return when (policy) {
      OneKeyImageCachePolicy.MEMORY_DISK -> options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
      OneKeyImageCachePolicy.MEMORY -> options.diskCacheStrategy(DiskCacheStrategy.NONE)
      OneKeyImageCachePolicy.DISK -> options
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .skipMemoryCache(true)
      OneKeyImageCachePolicy.NONE -> options
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
    }
  }

  private fun finishWithError(message: String, requestGeneration: Long) {
    if (requestGeneration != generation || disposed) return
    showError()
    val errorCallback = onError
    val loadEndCallback = onLoadEnd
    OneKeyTerminalEventPair.dispatch(
      primary = errorCallback?.let { callback -> { callback(message) } },
      onLoadEnd = loadEndCallback,
    )
  }

  private fun cancelCurrent(invalidateGeneration: Boolean) {
    loadRunnable?.let(hostView::removeCallbacks)
    loadRunnable = null
    displayRunnable?.let(hostView::removeCallbacks)
    displayRunnable = null
    fallbackRunnable?.let(hostView::removeCallbacks)
    fallbackRunnable = null
    clearCurrentTarget()
    requestActive = false
    hostView.skeletonRequested = false
    if (invalidateGeneration) generation++
  }

  private fun clearCurrentTarget() {
    val target = currentTarget ?: return
    // Clear the field first so onResourceCleared from our own cancellation
    // cannot erase state belonging to the next generation/request.
    currentTarget = null
    Glide.with(hostView).clear(target)
  }

  private fun scheduleOnDisplay(requestGeneration: Long) {
    displayRunnable?.let(hostView::removeCallbacks)
    val runnable = Runnable {
      displayRunnable = null
      if (
        requestGeneration == generation &&
        !disposed &&
        displayState == DisplayState.IMAGE
      ) {
        onDisplay?.invoke()
      }
    }
    displayRunnable = runnable
    hostView.postOnAnimation(runnable)
  }

  private fun showLoading(requestIsActive: Boolean) {
    displayState = DisplayState.LOADING
    requestActive = requestIsActive
    hostView.setImageDrawable(null)
    hostView.drawStateSymbol = false
    applyLoadingAppearance()
  }

  private fun showError() {
    showTerminalState(DisplayState.ERROR)
  }

  private fun showFallback() {
    showTerminalState(DisplayState.FALLBACK)
  }

  private fun showTerminalState(state: DisplayState) {
    displayState = state
    requestActive = false
    hostView.setImageDrawable(null)
    hostView.skeletonRequested = false
    hostView.drawStateSymbol = true
    hostView.stateSymbol = stateSymbol()
    hostView.setBackgroundColor(placeholderColor())
    hostView.invalidate()
  }

  private fun applyVariant() {
    when (displayState) {
      DisplayState.IMAGE -> Unit
      DisplayState.LOADING -> applyLoadingAppearance()
      DisplayState.ERROR, DisplayState.FALLBACK -> {
        hostView.setBackgroundColor(placeholderColor())
        hostView.stateSymbol = stateSymbol()
        hostView.invalidate()
      }
    }
  }

  private fun applyLoadingAppearance() {
    val strategy = loadingStrategy ?: OneKeyImageLoadingStrategy.STATIC
    hostView.setBackgroundColor(
      if (strategy == OneKeyImageLoadingStrategy.NONE) Color.TRANSPARENT else placeholderColor(),
    )
    hostView.skeletonRequested =
      requestActive && strategy == OneKeyImageLoadingStrategy.SKELETON
    hostView.invalidate()
  }

  private fun stateSymbol(): String = when (variant ?: OneKeyImageVariant.GENERIC) {
    OneKeyImageVariant.GENERIC -> "◇"
    OneKeyImageVariant.TOKEN -> "◈"
    OneKeyImageVariant.NETWORK -> "◎"
    OneKeyImageVariant.AVATAR -> "●"
  }

  private fun placeholderColor(): Int = when (variant ?: OneKeyImageVariant.GENERIC) {
    OneKeyImageVariant.GENERIC -> Color.rgb(232, 232, 232)
    OneKeyImageVariant.TOKEN -> Color.rgb(232, 237, 249)
    OneKeyImageVariant.NETWORK -> Color.rgb(229, 240, 245)
    OneKeyImageVariant.AVATAR -> Color.rgb(234, 234, 240)
  }

  private fun applyContentFit() {
    hostView.scaleType = when (contentFit ?: OneKeyImageContentFit.COVER) {
      OneKeyImageContentFit.COVER -> ImageView.ScaleType.CENTER_CROP
      OneKeyImageContentFit.CONTAIN -> ImageView.ScaleType.FIT_CENTER
      OneKeyImageContentFit.FILL -> ImageView.ScaleType.FIT_XY
      OneKeyImageContentFit.CENTER -> ImageView.ScaleType.CENTER
    }
  }

  private fun applyAutoplay() {
    hostView.autoplayEnabled = autoplay == true
    hostView.syncPlayback()
  }
}

internal object OneKeyTerminalEventPair {
  fun dispatch(primary: (() -> Unit)?, onLoadEnd: (() -> Unit)?) {
    try {
      primary?.invoke()
    } finally {
      onLoadEnd?.invoke()
    }
  }
}
