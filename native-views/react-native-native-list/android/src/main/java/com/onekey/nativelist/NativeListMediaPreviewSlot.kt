package com.margelo.nitro.nativelist

import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.Gravity
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
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

/** Uses Media3 like native Video, but never starts playback or requests audio focus. */
@OptIn(UnstableApi::class)
internal class NativeListMediaPreviewSlot(private val context: ThemedReactContext) {
  val view: FrameLayout = object : FrameLayout(context) {
    override fun onAttachedToWindow() { super.onAttachedToWindow(); observeViewport(); updateVisibility() }
    override fun onDetachedFromWindow() { stopObservingViewport(); releasePlayer(); super.onDetachedFromWindow() }
    override fun onWindowVisibilityChanged(visibility: Int) { super.onWindowVisibilityChanged(visibility); updateVisibility() }
    override fun onVisibilityAggregated(isVisible: Boolean) { super.onVisibilityAggregated(isVisible); if (isVisible) updateVisibility() else releasePlayer() }
  }
  private val image = NativeListImageSlot(context)
  private val video = AspectRatioFrameLayout(context)
  private val texture = TextureView(context)
  private var identity: String? = null
  private var epoch = 0
  private var candidateGeneration = 0
  private var playerGeneration = 0
  private var result: Boolean? = null
  private var completion: ((Boolean) -> Unit)? = null
  private var player: ExoPlayer? = null
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
    releasePlayer()
    if (attempt >= order.size) {
      result = false
      completion?.invoke(false)
      return
    }
    val finished: (Boolean) -> Unit = { loaded ->
      if (epoch == token && candidateGeneration == candidate) {
        if (loaded) {
          if (result != true) { result = true; completion?.invoke(true) }
        } else probe(source, key, fit, placeholder, order, attempt + 1, token)
      }
    }
    if (order[attempt] == "image") {
      video.visibility = View.GONE
      image.view.visibility = View.VISIBLE
      image.bind(source, key, fit = fit, placeholder = placeholder, completion = finished)
      return
    }
    image.recycle()
    image.view.visibility = View.GONE
    video.visibility = View.VISIBLE
    val uri = source.optString("uri")
    if (uri.isBlank()) { finished(false); return }
    val headers = source.optJSONObject("headers")?.let { values -> values.keys().asSequence().associateWith { values.optString(it) } } ?: emptyMap()
    resumeVideo = start@{
      if (epoch != token || candidateGeneration != candidate || player != null) return@start
      playerGeneration++
      val generation = playerGeneration
      try {
        val client = OkHttpClientProvider.getOkHttpClient().newBuilder()
          .cookieJar(JavaNetCookieJar(ForwardingCookieHandler(context))).build()
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
          override fun onRenderedFirstFrame() { if (playerGeneration == generation) finished(true) }
          override fun onPlayerError(error: PlaybackException) { if (playerGeneration == generation) finished(false) }
          override fun onVideoSizeChanged(size: VideoSize) {
            if (playerGeneration == generation && size.height > 0) video.setAspectRatio(size.width * size.pixelWidthHeightRatio / size.height)
          }
        })
        next.setMediaItem(MediaItem.fromUri(uri))
        next.prepare()
        next.pause()
      } catch (_: Exception) {
        releasePlayer()
        finished(false)
      }
    }
    observeViewport()
    updateVisibility()
  }

  private fun observeViewport() {
    if (viewportObserver != null || resumeVideo == null || !view.isAttachedToWindow) return
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
    var ancestor: View? = view
    var opaque = true
    while (ancestor != null) {
      if (ancestor.alpha <= 0.01f) { opaque = false; break }
      ancestor = ancestor.parent as? View
    }
    if (opaque && view.isAttachedToWindow && view.windowVisibility == View.VISIBLE && view.isShown &&
        view.getGlobalVisibleRect(visibleRect) && !visibleRect.isEmpty) resumeVideo?.invoke()
    else releasePlayer()
  }
  private fun releasePlayer() {
    playerGeneration++
    player?.let { it.pause(); it.clearVideoTextureView(texture); it.release() }
    player = null
  }
  fun recycle() {
    epoch++; candidateGeneration++
    resumeVideo = null
    stopObservingViewport()
    releasePlayer()
    image.recycle()
    image.view.visibility = View.VISIBLE
    video.visibility = View.GONE
    video.setAspectRatio(0f)
    identity = null; result = null; completion = null
  }
  fun dispose() { recycle(); image.dispose() }
}
