package com.margelo.nitro.nativelist

import android.graphics.Bitmap
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.Gravity
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import com.facebook.react.modules.network.ForwardingCookieHandler
import com.facebook.react.modules.network.OkHttpClientProvider
import com.facebook.react.uimanager.ThemedReactContext
import okhttp3.JavaNetCookieJar
import org.json.JSONObject
import com.margelo.nitro.onekeyimage.OneKeyMediaPreviewCache
import java.util.UUID

/**
 * Uses Media3 like native Video, but never starts playback or requests audio focus. A player
 * lives only until its first frame is captured; the tile then shows that still frame.
 */
@OptIn(UnstableApi::class)
internal class NativeListMediaPreviewSlot(private val context: ThemedReactContext) {
  val view: FrameLayout = object : FrameLayout(context) {
    override fun onAttachedToWindow() { super.onAttachedToWindow(); observeViewport(); updateVisibility() }
    override fun onDetachedFromWindow() { stopObservingViewport(); cancelPreview(); super.onDetachedFromWindow() }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) { super.onSizeChanged(w, h, oldw, oldh); updateVisibility() }
    override fun onWindowVisibilityChanged(visibility: Int) { super.onWindowVisibilityChanged(visibility); updateVisibility() }
    override fun onVisibilityAggregated(isVisible: Boolean) { super.onVisibilityAggregated(isVisible); if (isVisible) updateVisibility() else cancelPreview() }
  }
  private val image = NativeListImageSlot(context)
  private val video = AspectRatioFrameLayout(context)
  private val texture = TextureView(context)
  private val frame = ImageView(context)
  private var identity: String? = null
  private var epoch = 0
  private var candidateGeneration = 0
  private var playerGeneration = 0
  private var result: Boolean? = null
  private var completion: ((Boolean) -> Unit)? = null
  private var pendingCompletion: Runnable? = null
  private var player: ExoPlayer? = null
  private var subscription: OneKeyMediaPreviewCache.Subscription? = null
  private var extractionLease: UUID? = null
  private var requestedEdge: Int? = null
  private val previewCache by lazy { OneKeyMediaPreviewCache.get(context) }
  private var resumeVideo: (() -> Unit)? = null
  private var viewportObserver: ViewTreeObserver? = null
  private val visibleRect = Rect()
  private val scrollListener = ViewTreeObserver.OnScrollChangedListener { updateVisibility() }
  private val layoutListener = ViewTreeObserver.OnGlobalLayoutListener { updateVisibility() }
  // Native property animations can move a retained pager page without a layout pass.
  private val drawListener = ViewTreeObserver.OnPreDrawListener { updateVisibility(); true }

  init {
    view.isClickable = false
    view.isFocusable = false
    view.descendantFocusability = android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS
    video.addView(texture, FrameLayout.LayoutParams(-1, -1))
    frame.scaleType = ImageView.ScaleType.FIT_XY
    frame.visibility = View.GONE
    video.addView(frame, FrameLayout.LayoutParams(-1, -1))
    view.addView(image.view, FrameLayout.LayoutParams(-1, -1))
    view.addView(video, FrameLayout.LayoutParams(-1, -1, Gravity.CENTER))
    video.visibility = View.GONE
    video.setBackgroundColor(android.graphics.Color.BLACK)
    texture.isOpaque = false
  }

  fun bind(source: JSONObject, key: String, fit: String? = null,
           placeholder: String = "#0000000F", probeOrder: List<String> = listOf("image"),
           completion: ((Boolean) -> Unit)? = null) {
    val next = nativeListCanonical(JSONObject().put("source", source).put("key", key)
      .put("fit", fit ?: "").put("order", org.json.JSONArray(probeOrder)).put("placeholder", placeholder))
    this.completion = completion
    if (identity == next) {
      result?.let { completion?.invoke(it) }
      updateVisibility()
      return
    }
    recycle()
    identity = next
    this.completion = completion
    video.resizeMode = if ((fit ?: source.optString("contentFit")) == "cover") AspectRatioFrameLayout.RESIZE_MODE_ZOOM else AspectRatioFrameLayout.RESIZE_MODE_FIT
    probe(source, key, fit, placeholder, probeOrder, 0, epoch)
  }

  private fun probe(source: JSONObject, key: String, fit: String?, placeholder: String,
                    order: List<String>, attempt: Int, token: Int) {
    if (token != epoch) return
    candidateGeneration++
    val candidate = candidateGeneration
    resumeVideo = null
    stopObservingViewport()
    cancelPreview()
    if (attempt >= order.size) {
      result = false
      completion?.invoke(false)
      return
    }
    fun finished(loaded: Boolean, expectedPlayerGeneration: Int? = null) {
      if (epoch != token || candidateGeneration != candidate) return
      pendingCompletion?.let(view::removeCallbacks)
      // Glide forbids clearing/rebinding a request from either terminal callback.
      // Media3 callbacks also defer release so the emitting player is not reentered.
      val task = Runnable {
        pendingCompletion = null
        if (epoch != token || candidateGeneration != candidate ||
            (expectedPlayerGeneration != null && playerGeneration != expectedPlayerGeneration)) return@Runnable
        if (loaded) {
          if (result != true) { result = true; completion?.invoke(true) }
        } else probe(source, key, fit, placeholder, order, attempt + 1, token)
      }
      pendingCompletion = task
      view.post(task)
    }
    if (order[attempt] == "image") {
      video.visibility = View.GONE
      image.view.visibility = View.VISIBLE
      image.bind(source, key, fit = fit, placeholder = placeholder, completion = { loaded -> finished(loaded) })
      return
    }
    image.recycle()
    image.view.visibility = View.GONE
    video.visibility = View.VISIBLE
    val uri = source.optString("uri")
    if (uri.isBlank()) { finished(false); return }
    val headers = source.optJSONObject("headers")?.let { values -> values.keys().asSequence().associateWith { values.optString(it) } } ?: emptyMap()
    val fitKey = if ((fit ?: source.optString("contentFit")) == "cover") "cover" else "contain"
    resumeVideo = start@{
      if (epoch != token || candidateGeneration != candidate || subscription != null || frame.drawable != null ||
          video.width <= 0 || video.height <= 0) return@start
      val edge = OneKeyMediaPreviewCache.targetEdge(maxOf(view.width, view.height))
      requestedEdge = edge
      subscription = previewCache.request(uri, headers, edge, fitKey, source.optString("cachePolicy", "memory-disk"),
        onImage = { still ->
          if (epoch == token && candidateGeneration == candidate) {
            subscription = null
            video.setAspectRatio(still.width.toFloat() / still.height)
            frame.setImageBitmap(still); frame.visibility = View.VISIBLE
            stopObservingViewport(); finished(true)
          }
        }, onExtract = { lease ->
          if (epoch == token && candidateGeneration == candidate) {
            extractionLease = lease
            updateVisibility()
            if (extractionLease == lease && requestedEdge == edge) startPlayer(uri, headers, token, candidate, edge, lease)
          }
        }, onRevoked = { extractionLease = null; releasePlayer() },
        onFailure = {
          if (epoch == token && candidateGeneration == candidate) { subscription = null; finished(false) }
        })
    }
    observeViewport()
    updateVisibility()
  }

  private fun startPlayer(uri: String, headers: Map<String, String>, token: Int, candidate: Int, edge: Int, lease: UUID) {
      playerGeneration++
      val generation = playerGeneration
      try {
        val client = OkHttpClientProvider.getOkHttpClient().newBuilder()
          .cookieJar(previewCache.trackCookies(lease, JavaNetCookieJar(ForwardingCookieHandler(context)))).build()
        val http = OkHttpDataSource.Factory(client).setDefaultRequestProperties(headers)
        if (headers.keys.none { it.equals("User-Agent", ignoreCase = true) }) http.setUserAgent(Util.getUserAgent(context, context.packageName))
        val sources = DefaultMediaSourceFactory(DefaultDataSource.Factory(context, http))
        val buffers = DefaultLoadControl.Builder()
          .setBufferDurationsMs(250, 1000, 250, 250)
          .setTargetBufferBytes(2 * 1024 * 1024)
          .setPrioritizeTimeOverSizeThresholds(false)
          .setBackBuffer(0, false).build()
        val next = ExoPlayer.Builder(context.applicationContext)
          .setMediaSourceFactory(sources).setLoadControl(buffers).build()
        player = next
        next.volume = 0f
        next.playWhenReady = false
        next.setAudioAttributes(androidx.media3.common.AudioAttributes.DEFAULT, false)
        next.setVideoTextureView(texture)
        next.addListener(object : Player.Listener {
          override fun onRenderedFirstFrame() {
            if (playerGeneration != generation) return
            captureFirstFrame(token, candidate, generation, FIRST_FRAME_CAPTURE_ATTEMPTS, edge, lease)
          }
          override fun onPlayerError(error: PlaybackException) {
            view.post { if (playerGeneration == generation) previewCache.fail(lease) }
          }
          override fun onVideoSizeChanged(size: VideoSize) {
            if (playerGeneration == generation && size.height > 0) video.setAspectRatio(size.width * size.pixelWidthHeightRatio / size.height)
          }
        })
        next.setMediaItem(MediaItem.fromUri(uri))
        next.prepare()
        next.pause()
      } catch (_: Exception) {
        previewCache.fail(lease)
      }
  }

  /** TextureView requires UI access; request bounded pixels before background encoding. */
  private fun captureFirstFrame(token: Int, candidate: Int, generation: Int, attemptsLeft: Int, edge: Int, lease: UUID) {
    view.postOnAnimation {
      if (epoch != token || candidateGeneration != candidate || playerGeneration != generation) return@postOnAnimation
      val still = if (texture.isAvailable && texture.width > 0 && texture.height > 0) {
        val scale = minOf(1.0, edge.toDouble() / maxOf(texture.width, texture.height))
        runCatching { texture.getBitmap(maxOf(1, (texture.width * scale).toInt()), maxOf(1, (texture.height * scale).toInt())) }.getOrNull()
      } else null
      if (still == null || !hasVisiblePixels(still)) {
        still?.recycle()
        if (attemptsLeft > 1) captureFirstFrame(token, candidate, generation, attemptsLeft - 1, edge, lease)
        else previewCache.fail(lease)
        return@postOnAnimation
      }
      releasePlayer()
      previewCache.complete(lease, still)
    }
  }

  private fun hasVisiblePixels(bitmap: Bitmap): Boolean {
    val columns = intArrayOf(bitmap.width / 4, bitmap.width / 2, bitmap.width * 3 / 4)
    val rows = intArrayOf(bitmap.height / 4, bitmap.height / 2, bitmap.height * 3 / 4)
    return columns.any { x -> rows.any { y -> (bitmap.getPixel(x, y) ushr 24) != 0 } }
  }

  private fun observeViewport() {
    if (viewportObserver != null || resumeVideo == null || frame.drawable != null || !view.isAttachedToWindow) return
    view.viewTreeObserver.takeIf { it.isAlive }?.let {
      it.addOnScrollChangedListener(scrollListener)
      it.addOnGlobalLayoutListener(layoutListener)
      it.addOnPreDrawListener(drawListener)
      viewportObserver = it
    }
  }
  private fun stopObservingViewport() {
    viewportObserver?.takeIf { it.isAlive }?.let {
      it.removeOnScrollChangedListener(scrollListener)
      it.removeOnGlobalLayoutListener(layoutListener)
      it.removeOnPreDrawListener(drawListener)
    }
    viewportObserver = null
  }
  private fun updateVisibility() {
    requestedEdge?.let { previous ->
      if (video.width > 0 && video.height > 0 && OneKeyMediaPreviewCache.targetEdge(maxOf(view.width, view.height)) != previous) {
        cancelPreview(); requestedEdge = null
        frame.setImageDrawable(null); frame.visibility = View.GONE; result = null
        observeViewport()
      }
    }
    var ancestor: View? = view
    var opaque = true
    while (ancestor != null) {
      if (ancestor.alpha <= 0.01f) { opaque = false; break }
      ancestor = ancestor.parent as? View
    }
    if (opaque && view.isAttachedToWindow && view.windowVisibility == View.VISIBLE && view.isShown &&
        view.getGlobalVisibleRect(visibleRect) && !visibleRect.isEmpty) resumeVideo?.invoke()
    else cancelPreview()
  }
  private fun cancelPreview() {
    val previous = subscription; subscription = null
    previewCache.cancel(previous); extractionLease = null; releasePlayer()
  }
  private fun releasePlayer() {
    playerGeneration++
    player?.let { it.pause(); it.clearVideoTextureView(texture); it.release() }
    player = null
  }
  fun recycle() {
    epoch++; candidateGeneration++
    pendingCompletion?.let(view::removeCallbacks)
    pendingCompletion = null
    resumeVideo = null
    stopObservingViewport()
    cancelPreview()
    image.recycle()
    image.view.visibility = View.VISIBLE
    video.visibility = View.GONE
    video.setAspectRatio(0f)
    requestedEdge = null
    frame.setImageDrawable(null)
    frame.visibility = View.GONE
    identity = null; result = null; completion = null
  }

  private companion object {
    const val FIRST_FRAME_CAPTURE_ATTEMPTS = 6
  }
  fun dispose() { recycle(); image.dispose() }
}
