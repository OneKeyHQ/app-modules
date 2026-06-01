package com.margelo.nitro.chartwebview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.webkit.JavascriptInterface
import com.facebook.react.uimanager.ThemedReactContext
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.atomic.AtomicInteger

/**
 * Owns one real [WebView] together with its message bridge, asset loader and
 * load state. A single instance can be **reparented** across multiple
 * [HybridChartWebview] hosts that share a `reuseKey`, so N mount points are
 * backed by ONE WebView (state preserved, no reload on hand-off).
 *
 * The host currently displaying the WebView is [owner]; all page events
 * (message / load / error) are routed to it. Hosts drive the WebView only
 * through this class — they never touch the [WebView] directly.
 */
@SuppressLint("SetJavaScriptEnabled")
class PooledChartWebView private constructor(
  context: Context,
  val key: String,
) {
  companion object {
    // Live WebView count, logged on create/destroy. With pooling, N hosts that
    // share a reuseKey must produce exactly ONE "CREATED" line — this is the
    // signal the example uses to verify the singleton.
    private val liveCount = AtomicInteger(0)

    const val ASSET_HOST = "appassets.androidplatform.net"
    private const val DEFAULT_ENTRY = "index.html"

    // See HybridChartWebview for the rationale; identical dumb-pipe bridge.
    private const val OUTBOUND_BRIDGE_JS =
      "(function(){" +
      "  if (window.__onekeyChartBridge) return; window.__onekeyChartBridge = true;" +
      "  var fwd = function(m){ try { AndroidChartBridge.postMessage(typeof m === 'string' ? m : JSON.stringify(m)); } catch (e) {} };" +
      "  window.\$onekey = window.\$onekey || {};" +
      "  window.\$onekey.\$private = window.\$onekey.\$private || {};" +
      "  window.\$onekey.\$private.request = function(m){ fwd(m); };" +
      "  window.ReactNativeWebView = window.ReactNativeWebView || {};" +
      "  window.ReactNativeWebView.postMessage = function(s){ fwd(String(s)); };" +
      "  window.addEventListener('message', function(e){" +
      "    try { var d = e && e.data; if (d && d.scope === '\$private') fwd(d); } catch (err) {}" +
      "  });" +
      "})();"

    fun create(context: Context, key: String): PooledChartWebView =
      PooledChartWebView(context.applicationContext, key)
  }

  /** The host currently displaying this WebView; page events route here. */
  var owner: HybridChartWebview? = null

  private var assetLoader: WebViewAssetLoader? = null
  private var lastLoadedUrl: String? = null
  private var lastLocalBundle: String? = null

  // Last rendered frame, used to mask the brief blank frame while the WebView's
  // surface is torn down and recreated during a reparent (move). Kept until
  // clearSnapshot() or the next capture replaces it.
  private var cachedSnapshot: Bitmap? = null
  private var overlay: ImageView? = null

  // How long the snapshot overlay stays up after a reparent, giving the WebView
  // time to draw its first frame in the new container (~5 frames).
  private val overlayHideDelayMs = 80L

  val webView: WebView = WebView(context).apply {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.databaseEnabled = true
    settings.allowFileAccess = false
    settings.allowContentAccess = false
    settings.mediaPlaybackRequiresUserGesture = false
    addJavascriptInterface(ChartBridge(), "AndroidChartBridge")
  }

  init {
    val n = liveCount.incrementAndGet()
    android.util.Log.d("ChartWebviewPool", "WebView CREATED key=$key liveCount=$n")

    if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
      WebViewCompat.addDocumentStartJavaScript(webView, OUTBOUND_BRIDGE_JS, setOf("*"))
    }

    webView.webViewClient = object : WebViewClientCompat() {
      override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest,
      ): WebResourceResponse? = assetLoader?.shouldInterceptRequest(request.url)

      override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
        super.onPageStarted(view, url, favicon)
        view.evaluateJavascript(OUTBOUND_BRIDGE_JS, null)
      }

      override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        view.evaluateJavascript(OUTBOUND_BRIDGE_JS, null)
        owner?.dispatchLoadEnd()
        // Prime the snapshot so the first move already has a frame to mask with.
        refreshSnapshotSoon()
      }

      override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceErrorCompat,
      ) {
        super.onReceivedError(view, request, error)
        if (request.isForMainFrame) {
          owner?.dispatchError(error.description?.toString() ?: "WebView error")
        }
      }
    }
  }

  // page -> native: route to the active owner on the UI thread.
  private inner class ChartBridge {
    @JavascriptInterface
    fun postMessage(message: String) {
      runOnUiThread { owner?.dispatchMessage(message) }
    }
  }

  /** Move the WebView into [container], detaching it from any previous parent. */
  fun attachTo(container: ViewGroup) {
    runOnUiThread {
      if (webView.parent === container) return@runOnUiThread
      (webView.parent as? ViewGroup)?.removeView(webView)
      container.addView(
        webView,
        FrameLayout.LayoutParams(
          FrameLayout.LayoutParams.MATCH_PARENT,
          FrameLayout.LayoutParams.MATCH_PARENT,
        ),
      )
      // Mask the reparent's blank frame with the last captured frame, then
      // refresh the cache (async) so the next move has a current frame.
      showSnapshotOverlay(container)
      refreshSnapshotSoon()
    }
  }

  /** Remove the WebView from its current parent (keeps it alive, warm). */
  fun detachFromParent() {
    runOnUiThread {
      // Capture while still attached to the window (PixelCopy needs that), so a
      // release-before-claim ordering still leaves a fresh frame to mask with.
      capturePixelCopy()
      removeOverlay()
      (webView.parent as? ViewGroup)?.removeView(webView)
    }
  }

  /** Drop the cached snapshot and remove any visible overlay. */
  fun clearSnapshot() {
    runOnUiThread {
      removeOverlay()
      cachedSnapshot = null
    }
  }

  /// Capture the current frame a moment after things settle (async).
  fun refreshSnapshotSoon() {
    webView.postDelayed({ capturePixelCopy() }, 120)
  }

  // Capture the WebView's REAL on-screen pixels (incl. GPU-rendered chart
  // canvas) via PixelCopy. `webView.draw()` on a software canvas would miss the
  // hardware-accelerated <canvas>, so the chart candles wouldn't be captured.
  // PixelCopy is async and needs the view attached to a window.
  private fun capturePixelCopy() {
    val w = webView.width
    val h = webView.height
    if (w <= 0 || h <= 0 || webView.windowToken == null) return
    val window = currentWindow() ?: return
    val location = IntArray(2)
    webView.getLocationInWindow(location)
    val src = Rect(location[0], location[1], location[0] + w, location[1] + h)
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    try {
      PixelCopy.request(
        window,
        src,
        bmp,
        { result -> if (result == PixelCopy.SUCCESS) cachedSnapshot = bmp },
        webView.handler ?: Handler(Looper.getMainLooper()),
      )
    } catch (e: Throwable) {
      // Best-effort: a failure just means no flash mask this move.
    }
  }

  // The window owning the container the WebView is attached to (the host
  // Activity). The WebView itself was created with an application context.
  private fun currentWindow(): Window? {
    val ctx = (webView.parent as? View)?.context
    (ctx as? ThemedReactContext)?.currentActivity?.window?.let { return it }
    return (ctx as? Activity)?.window
  }

  private fun showSnapshotOverlay(container: ViewGroup) {
    val snap = cachedSnapshot ?: return
    removeOverlay()
    val iv = ImageView(container.context).apply {
      setImageBitmap(snap)
      scaleType = ImageView.ScaleType.FIT_XY
      layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      )
    }
    container.addView(iv) // added last => drawn on top of the WebView
    overlay = iv
    iv.postDelayed({ removeOverlay() }, overlayHideDelayMs)
  }

  private fun removeOverlay() {
    overlay?.let { (it.parent as? ViewGroup)?.removeView(it) }
    overlay = null
  }

  /**
   * Apply the source props and (re)load only when the effective URL changes —
   * so reparenting / redundant prop re-applies never reload (which would lose
   * chart state, the whole point of pooling).
   */
  fun setSource(uri: String?, localBundle: String?, entry: String?, paramsJson: String?) {
    if (localBundle != lastLocalBundle) {
      lastLocalBundle = localBundle
      rebuildAssetLoader(localBundle)
    }
    val target = computeTargetUrl(uri, localBundle, entry, paramsJson) ?: return
    if (target == lastLoadedUrl) return
    lastLoadedUrl = target
    runOnUiThread { webView.loadUrl(target) }
  }

  fun postMessage(message: String) {
    val payload = JSONObject.quote(message)
    val js =
      "(function(){try{window.postMessage(JSON.parse($payload), '*');}" +
      "catch(e){window.postMessage($payload, '*');}})();"
    runOnUiThread { webView.evaluateJavascript(js, null) }
  }

  fun reload() {
    runOnUiThread { webView.reload() }
  }

  /** Permanently free the WebView (used for non-pooled / private instances). */
  fun destroy() {
    runOnUiThread {
      (webView.parent as? ViewGroup)?.removeView(webView)
      webView.destroy()
      val n = liveCount.decrementAndGet()
      android.util.Log.d("ChartWebviewPool", "WebView DESTROYED key=$key liveCount=$n")
    }
  }

  private fun rebuildAssetLoader(localBundle: String?) {
    if (localBundle.isNullOrEmpty()) {
      assetLoader = null
      return
    }
    assetLoader = WebViewAssetLoader.Builder()
      .setDomain(ASSET_HOST)
      .addPathHandler("/", WebViewAssetLoader.AssetsPathHandler(webView.context))
      .build()
  }

  private fun computeTargetUrl(
    uri: String?,
    localBundle: String?,
    entry: String?,
    paramsJson: String?,
  ): String? {
    if (!uri.isNullOrEmpty()) return uri
    if (localBundle.isNullOrEmpty()) return null
    val entryPath = entry?.takeIf { it.isNotEmpty() } ?: DEFAULT_ENTRY
    val query = buildQueryFromParamsJson(paramsJson)
    return buildString {
      append("https://")
      append(ASSET_HOST)
      append('/')
      append(entryPath)
      if (query.isNotEmpty()) {
        append('?')
        append(query)
      }
    }
  }

  private fun buildQueryFromParamsJson(paramsJson: String?): String {
    if (paramsJson.isNullOrEmpty()) return ""
    return try {
      val obj = JSONObject(paramsJson)
      val sb = StringBuilder()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val k = keys.next()
        val rawValue = obj.get(k)
        val value = if (rawValue == JSONObject.NULL) "" else rawValue.toString()
        if (sb.isNotEmpty()) sb.append('&')
        sb.append(encode(k))
        sb.append('=')
        sb.append(encode(value))
      }
      sb.toString()
    } catch (e: Exception) {
      ""
    }
  }

  private fun encode(s: String): String =
    URLEncoder.encode(s, "UTF-8").replace("+", "%20")

  private fun runOnUiThread(action: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) action() else webView.post { action() }
  }
}

/**
 * Warm pool of [PooledChartWebView]s keyed by `reuseKey`. Entries are created on
 * first use and kept warm (a single static chart instance, OKX-style), so the
 * next mount point that claims the same key reuses the live WebView instead of
 * recreating it. Non-pooled hosts don't use this — they own a private instance.
 */
object ChartWebviewPool {
  private val shared = HashMap<String, PooledChartWebView>()

  @Synchronized
  fun acquireShared(key: String, context: Context): PooledChartWebView =
    shared.getOrPut(key) { PooledChartWebView.create(context, key) }
}
