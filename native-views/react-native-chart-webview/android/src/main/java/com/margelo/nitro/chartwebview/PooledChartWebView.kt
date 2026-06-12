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
import androidx.webkit.ScriptHandler
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import org.json.JSONObject
import java.lang.ref.WeakReference
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

    // Operational log tag for this pool (lifecycle + error paths only).
    private const val TAG = "ChartWebviewPool"

    // Tiny platform transport shim: defines the single hook the shared TS bridge
    // (CHART_BRIDGE_JS) calls. The bulk of the bridge lives in the TS layer and
    // arrives via the `bridgeScript` ctor arg — this is the only platform-specific
    // piece (Android -> AndroidChartBridge).
    private const val NATIVE_POST_SHIM_JS =
      "(function(){window.__chartNativePost=function(s){" +
      "try{AndroidChartBridge.postMessage(s);}catch(e){}};})();"

    fun create(context: Context, key: String): PooledChartWebView =
      PooledChartWebView(context.applicationContext, key)
  }

  // Shim (defines __chartNativePost) + the TS bridge that uses it. Set lazily via
  // setBridgeScript once the host has the prop value — registering it in init
  // would capture an empty script, since the first reconcile (window-attach) can
  // run before the bridgeScript prop is applied. The privileged bridge exposes
  // window.$onekey.$private.request etc., so it must only ever be injected into
  // the trusted chart origin(s) we load — NOT cross-origin subframes / arbitrary
  // pages. The actual document-start registration is therefore deferred to
  // registerBridgeForOrigins(), called once the load URL (hence origin) is known.
  private var outboundBridgeJs: String = ""

  // Handler for the currently-registered document-start script, kept so it can be
  // removed and re-registered if the bridge script or the trusted origin set
  // changes (instead of silently latching the first one).
  private var bridgeScriptHandler: ScriptHandler? = null
  // The (script, origins) pair currently registered, so we can detect changes and
  // avoid redundant re-registration.
  private var registeredBridgeJs: String? = null
  private var registeredOrigins: Set<String> = emptySet()

  // True once we have a non-empty bridge script staged; gates loading (the page
  // must not boot before the bridge is registered or its first requests are lost).
  private val bridgeRegistered: Boolean
    get() = outboundBridgeJs.isNotEmpty()

  // Called by the host before the first load. Stages the document-start bridge
  // (shim + the shared TS bridgeScript) once a non-empty script is available.
  // The script is not injected into the page yet — that happens, scoped to the
  // trusted origin(s), in registerBridgeForOrigins() when the load URL is known.
  // If a second host sharing the reuseKey supplies a DIFFERENT script we update
  // it (and re-register on next load) rather than silently dropping it; in the
  // app's single-reuseKey + constant-bridge reality this branch never fires, but
  // not latching keeps it correct if that ever changes.
  fun setBridgeScript(bridgeScript: String) {
    if (bridgeScript.isEmpty()) return
    outboundBridgeJs = "$NATIVE_POST_SHIM_JS\n$bridgeScript"
  }

  // Register (or re-register) the document-start bridge scoped to exactly the
  // trusted origins we load. allowedOriginRules of setOf("*") would leak the
  // privileged bridge into every cross-origin subframe; here we pass only the
  // offline asset origin and (in online mode) the configured chart origin.
  private fun registerBridgeForOrigins(origins: Set<String>) {
    if (outboundBridgeJs.isEmpty() || origins.isEmpty()) return
    if (!WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) return
    // No-op if nothing changed (same script + same origin set already registered).
    if (bridgeScriptHandler != null &&
      registeredBridgeJs == outboundBridgeJs &&
      registeredOrigins == origins
    ) {
      return
    }
    bridgeScriptHandler?.remove()
    bridgeScriptHandler =
      WebViewCompat.addDocumentStartJavaScript(webView, outboundBridgeJs, origins)
    registeredBridgeJs = outboundBridgeJs
    registeredOrigins = origins
  }

  // The trusted origin set for the current source: the offline asset origin
  // (https://<assetHost>, default appassets.androidplatform.net) always, plus the
  // online/fallback `uri` origin when running in online mode. computeTargetUrl
  // serves offline content from `assetHost` and online content from `uri`, so
  // these are exactly the origins the privileged bridge legitimately runs in.
  private fun trustedOriginsFor(uri: String?): Set<String> {
    val origins = linkedSetOf("https://$assetHost")
    originOf(uri)?.let { origins.add(it) }
    return origins
  }

  // scheme://host[:port] of a URL, or null if it can't be parsed / isn't http(s).
  private fun originOf(url: String?): String? {
    if (url.isNullOrEmpty()) return null
    return try {
      val u = java.net.URI(url)
      val scheme = u.scheme?.lowercase() ?: return null
      if (scheme != "https" && scheme != "http") return null
      val host = u.host ?: return null
      if (u.port != -1) "$scheme://$host:${u.port}" else "$scheme://$host"
    } catch (e: Exception) {
      null
    }
  }

  /** The host currently displaying this WebView; page events route here. */
  // Weak (mirror of iOS): the pool entry is immortal, so a strong ref here would
  // pin a disposed host — and its ReactContext/Activity — forever. A GC'd host
  // reads back as null, which makes `entry.owner == this` correctly false.
  private var _ownerRef: WeakReference<HybridChartWebview>? = null
  var owner: HybridChartWebview?
    get() = _ownerRef?.get()
    set(value) {
      _ownerRef = value?.let { WeakReference(it) }
    }
  // The host that warm-booted the page and can drive its symbol / receive its
  // callbacks while there is no VISIBLE owner yet. Separate from `owner` (the
  // YIELD path clears `owner`); callbacks fall back to this so bars-state /
  // load-end aren't dropped during warm. Mirror of the iOS warmDriver.
  // Weak for the same immortal-pool reason as `owner` above.
  private var _warmDriverRef: WeakReference<HybridChartWebview>? = null
  var warmDriver: HybridChartWebview?
    get() = _warmDriverRef?.get()
    set(value) {
      _warmDriverRef = value?.let { WeakReference(it) }
    }

  // PERF (Android only): Android's in-process WebView/Chromium does NOT throttle
  // an offscreen/unowned page (unlike iOS WKWebView). Left running, the warm
  // pooled page keeps its rAF render loop + websockets + compositing alive
  // FOREVER after the user leaves the chart — pinning a CPU core, growing RAM to
  // OOM, and stealing the GPU from RN's RenderThread (every other screen stalls).
  // We pause the WebView whenever no host owns it (after the first load) and
  // resume on claim. Uses the PER-INSTANCE onPause()/onResume() — NOT the static
  // pauseTimers()/resumeTimers(), which are process-global and would also freeze
  // the app's other WebViews (inpage provider / web-embed / dapp browser).
  private var paused = false
  private var hasLoadedOnce = false

  fun markLoaded() {
    hasLoadedOnce = true
  }

  // Pause the renderer when nobody owns the shared WebView. onPause() stops
  // drawing/compositing/animations (frees the GPU so navigation is smooth again)
  // WITHOUT changing the view's visibility/attachment — we must NOT toggle
  // visibility here, that left the WebView blank/white after re-claim. Skipped
  // until the first load so we never freeze a booting page. Resumed (+ redraw)
  // on the next CLAIM so the chart paints again.
  fun pauseIfIdle() {
    if (paused || !hasLoadedOnce || owner != null) {
      return
    }
    paused = true
    android.util.Log.d(TAG, "pool[$key] PAUSE (renderer idle)")
    runOnUiThread { webView.onPause() }
  }

  fun resume() {
    if (!paused) return
    paused = false
    android.util.Log.d(TAG, "pool[$key] RESUME")
    runOnUiThread {
      webView.onResume()
      webView.invalidate()
    }
  }

  private var assetLoader: WebViewAssetLoader? = null
  private var lastLoadedUrl: String? = null
  private var lastLocalBundle: String? = null
  // The host the offline bundle is served under. Defaults to the built-in
  // appassets host (old behavior); when the app passes `assetHost` we serve the
  // bundle under the real chart origin so the WebView reuses that origin's
  // same-origin storage (zero migration). Tracked so an assetHost change rebuilds
  // the asset loader + recomputes the target URL, same as a localBundle change.
  private var assetHost: String = ASSET_HOST
  private var lastAssetHost: String = ASSET_HOST

  // Last rendered frame, used to mask the brief blank frame while the WebView's
  // surface is torn down and recreated during a reparent (move). Kept until
  // clearSnapshot() or the next capture replaces it.
  private var cachedSnapshot: Bitmap? = null
  private var overlay: ImageView? = null
  // The bitmap currently shown by `overlay` (a snapshot reused directly, not a
  // copy). Tracked so a concurrent capture doesn't recycle a bitmap still on
  // screen — see capturePixelCopy.
  private var overlaySnapshot: Bitmap? = null

  // How long the snapshot overlay stays up after a reparent, giving the WebView
  // time to draw its first frame in the new container (~5 frames).
  private val overlayHideDelayMs = 80L
  // Delay between bounded attach retries (~1 vsync at 60Hz): long enough for the
  // old parent's pending layout / removeView to flush, short enough to stay snappy.
  private val attachRetryDelayMs = 16L
  private val attachGeneration = AtomicInteger(0)

  val webView: WebView = WebView(context).apply {
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.databaseEnabled = true
    settings.allowFileAccess = false
    settings.allowContentAccess = false
    settings.mediaPlaybackRequiresUserGesture = false
    addJavascriptInterface(ChartBridge(), "AndroidChartBridge")
  }

  // Apply the app's "Enable Native Webview Debugging" dev-mode toggle, mirroring
  // how the main react-native-webview calls WebView.setWebContentsDebuggingEnabled.
  // Called by the host both on the prop change and at host claim, so the toggle is
  // honored even when this entry was created before the prop arrived.
  //
  // CAVEAT (PROCESS-GLOBAL): setWebContentsDebuggingEnabled is a STATIC method that
  // flips remote-debugging for EVERY WebView in the whole process. Once any WebView
  // (this chart, the main react-native-webview, etc.) enables it, it stays enabled
  // process-wide until the app is killed — Android exposes no per-WebView toggle and
  // no way to read the current value. So a null/false preference here cannot turn
  // debugging back OFF once another WebView (or a prior true value) turned it ON; it
  // simply does not re-enable it. We therefore only ever call the setter with `true`
  // when explicitly enabled, leaving the process-global state untouched otherwise.
  fun setInspectable(enabled: Boolean?) {
    if (enabled == true) {
      runOnUiThread { WebView.setWebContentsDebuggingEnabled(true) }
    }
  }

  init {
    val n = liveCount.incrementAndGet()
    android.util.Log.d(TAG, "WebView CREATED key=$key liveCount=$n")

    webView.webViewClient = object : WebViewClientCompat() {
      override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest,
      ): WebResourceResponse? = assetLoader?.shouldInterceptRequest(request.url)

      override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        markLoaded()
        // The bridge is delivered exclusively via the origin-scoped document-start
        // script (see registerBridgeForOrigins). We deliberately do NOT re-inject
        // it here via evaluateJavascript — that ran on every page event regardless
        // of URL and would expose the privileged bridge to untrusted pages/frames.
        (owner ?: warmDriver)?.dispatchLoadEnd()
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
      runOnUiThread {
        val target = owner ?: warmDriver
        target?.dispatchMessage(message)
      }
    }
  }

  // Robustly detach the WebView from [parent], even when [parent] is a disposed
  // host container that is mid-teardown / detached from the window and holds the
  // child in a transition / disappearing-children list — where a plain removeView()
  // is deferred and leaves webView.parent set, stranding the shared WebView and
  // blanking the next chart slot. endViewTransition() clears any pending transition
  // hold; removeViewInLayout() is the in-layout fallback if the child is still held.
  private fun forceDetach(parent: ViewGroup) {
    if (webView.parent !== parent) return
    try {
      parent.endViewTransition(webView)
    } catch (e: Throwable) {
      android.util.Log.w(TAG, "forceDetach: endViewTransition failed", e)
    }
    parent.removeView(webView)
    if (webView.parent === parent) {
      try {
        parent.removeViewInLayout(webView)
      } catch (e: Throwable) {
        android.util.Log.w(TAG, "forceDetach: removeViewInLayout failed", e)
      }
      parent.requestLayout()
    }
  }

  /** Move the WebView into [container], detaching it from any previous parent. */
  fun attachTo(container: ViewGroup) {
    val generation = attachGeneration.incrementAndGet()
    runOnUiThread {
      attachToContainer(container, generation, retriesLeft = 12)
    }
  }

  private fun attachToContainer(container: ViewGroup, generation: Int, retriesLeft: Int) {
    if (generation != attachGeneration.get()) return
    if (webView.parent === container) return

    (webView.parent as? ViewGroup)?.let { forceDetach(it) }
    val currentParent = webView.parent
    if (currentParent != null && currentParent !== container) {
      // The old container still hasn't released the WebView (a disposed host's
      // container mid-teardown / detached from window: removeView is deferred and
      // getParent() stays set). Retry — but on the TARGET container's handler,
      // which is attached to the window so its queue keeps draining, instead of the
      // detached webView/old parent whose post() runnables may never run.
      if (retriesLeft > 0) {
        // postDelayed (not a tight container.post spin): a small delay lets the
        // old parent's pending layout / removeView flush between attempts, instead
        // of re-checking on the very next vsync before anything could change.
        container.postDelayed({
          attachToContainer(container, generation, retriesLeft = retriesLeft - 1)
        }, attachRetryDelayMs)
      } else {
        // Last resort before giving up: the old parent never released the WebView
        // through the normal path. Force-detach it and try the attach once more so
        // we don't leave the WebView unparented (the blank-chart symptom).
        (currentParent as? ViewGroup)?.let { forceDetach(it) }
        if (webView.parent == null || webView.parent === container) {
          attachToContainer(container, generation, retriesLeft = 0)
        } else {
          android.util.Log.w(
            TAG,
            "Skip attach key=$key after retries because WebView parent was not cleared: $currentParent",
          )
        }
      }
      return
    }

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

  /**
   * Remove the WebView from [container] ONLY if it is still parented there.
   *
   * Called on a pooled host's final teardown (dispose): that host's container is
   * about to be dropped from the view tree, and a pooled host deliberately does
   * NOT detach on the YIELD path (to avoid racing a rapid re-claim). Without this,
   * the shared WebView stays parented to the dropped/detached container; the next
   * host's [attachTo] then keeps hitting "old parent not cleared" (removeView on a
   * detached container isn't applied synchronously) and retries forever — leaving
   * the new chart slot blank/white. The `parent === container` guard makes this
   * safe against ordering: if a new host has already re-claimed and reparented the
   * WebView, we must NOT rip it back off, so we only remove when WE still hold it.
   */
  fun detachFrom(container: ViewGroup) {
    runOnUiThread {
      if (webView.parent === container) {
        forceDetach(container)
      }
    }
  }

  /** Remove the WebView from its current parent (keeps it alive, warm). */
  fun detachFromParent() {
    runOnUiThread {
      // Capture while still attached to the window (PixelCopy needs that), so a
      // release-before-claim ordering still leaves a fresh frame to mask with.
      capturePixelCopy(null)
      removeOverlay()
      (webView.parent as? ViewGroup)?.removeView(webView)
    }
  }

  /** Drop the cached snapshot and remove any visible overlay. */
  fun clearSnapshot() {
    runOnUiThread {
      removeOverlay()
      // The pool owns cachedSnapshot outright (hosts get copies via captureNow),
      // so recycle it here instead of leaving a full-size ARGB_8888 bitmap to GC.
      cachedSnapshot?.recycle()
      cachedSnapshot = null
    }
  }

  /** The last captured frame (shown by inactive hosts as a placeholder). */
  fun snapshot(): Bitmap? = cachedSnapshot

  /// Capture the current frame a moment after things settle (async).
  fun refreshSnapshotSoon() {
    webView.postDelayed({ capturePixelCopy(null) }, 120)
  }

  /// Capture now and deliver the fresh frame (e.g. so the host that is losing
  /// ownership can update its placeholder to its very last frame). The host gets
  /// an independent COPY: the pool reuses/recycles its own `cachedSnapshot` on
  /// every internal refresh, so handing out the live buffer would let it recycle
  /// a bitmap still shown in a host placeholder. The copy is the host's to keep.
  fun captureNow(callback: (Bitmap?) -> Unit) {
    capturePixelCopy { bmp ->
      val copy = bmp?.takeIf { !it.isRecycled }?.let {
        it.copy(it.config ?: Bitmap.Config.ARGB_8888, false)
      }
      callback(copy)
    }
  }

  // Capture the WebView's REAL on-screen pixels (incl. GPU-rendered chart
  // canvas) via PixelCopy. `webView.draw()` on a software canvas would miss the
  // hardware-accelerated <canvas>, so the chart candles wouldn't be captured.
  // PixelCopy is async and needs the view attached to a window.
  private fun capturePixelCopy(callback: ((Bitmap?) -> Unit)?) {
    // Don't capture mid-reparent (overlay visible): the WebView may be blank for
    // a frame and we'd cache a black image. Keep the last good frame instead.
    if (overlay != null) { callback?.invoke(cachedSnapshot); return }
    val w = webView.width
    val h = webView.height
    if (w <= 0 || h <= 0 || webView.windowToken == null) { callback?.invoke(cachedSnapshot); return }
    val window = currentWindow() ?: run { callback?.invoke(cachedSnapshot); return }
    val location = IntArray(2)
    webView.getLocationInWindow(location)
    val src = Rect(location[0], location[1], location[0] + w, location[1] + h)
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    try {
      PixelCopy.request(
        window,
        src,
        bmp,
        { result ->
          if (result == PixelCopy.SUCCESS) {
            // Recycle the frame we're replacing to avoid piling up full-size
            // ARGB_8888 bitmaps. Guard against recycling one still referenced by a
            // live overlay/placeholder ImageView (showSnapshotOverlay reuses
            // cachedSnapshot directly): only the unused old buffer is freed.
            val old = cachedSnapshot
            cachedSnapshot = bmp
            if (old != null && old !== bmp && old !== overlaySnapshot && !old.isRecycled) {
              old.recycle()
            }
          } else {
            // Capture failed: the freshly allocated buffer is unused — free it.
            if (!bmp.isRecycled) bmp.recycle()
          }
          callback?.invoke(cachedSnapshot)
        },
        webView.handler ?: Handler(Looper.getMainLooper()),
      )
    } catch (e: Throwable) {
      // Best-effort: a failure just means no flash mask this move.
      callback?.invoke(cachedSnapshot)
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
    overlaySnapshot = snap
    iv.postDelayed({ removeOverlay() }, overlayHideDelayMs)
  }

  private fun removeOverlay() {
    overlay?.let {
      (it.parent as? ViewGroup)?.removeView(it)
      it.setImageDrawable(null)
    }
    overlay = null
    overlaySnapshot = null
  }

  /**
   * Apply the source props and (re)load only when the effective URL changes —
   * so reparenting / redundant prop re-applies never reload (which would lose
   * chart state, the whole point of pooling).
   */
  fun setSource(
    uri: String?,
    localBundle: String?,
    entry: String?,
    paramsJson: String?,
    assetHost: String?,
  ) {
    // Never load before the document-start bridge is registered — otherwise the
    // page boots without the bridge and its first $private requests are lost. The
    // host re-calls setSource once the bridgeScript prop arrives.
    if (!bridgeRegistered) return
    // Per-instance asset host: fall back to the built-in appassets host (old
    // behavior) when the app doesn't pass one. Empty string is treated as absent.
    // Sanitize untrusted values before they reach the privileged-bridge origin
    // ("https://$assetHost") and WebViewAssetLoader.setDomain — see sanitizeAssetHost.
    this.assetHost = assetHost?.takeIf { it.isNotEmpty() }
      ?.let { sanitizeAssetHost(it) } ?: ASSET_HOST
    if (localBundle != lastLocalBundle || this.assetHost != lastAssetHost) {
      lastLocalBundle = localBundle
      lastAssetHost = this.assetHost
      rebuildAssetLoader(localBundle)
    }
    // Register the document-start bridge scoped to exactly the origin(s) this load
    // uses (offline asset origin, plus the online `uri` origin when present) before
    // navigating, so the page boots with the bridge but cross-origin subframes /
    // untrusted pages don't receive it.
    registerBridgeForOrigins(trustedOriginsFor(uri))
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

  /**
   * Permanently free the WebView and its bridge/snapshot resources. Used for the
   * non-pooled (private) path on host teardown, and reachable for the pool (see
   * ChartWebviewPool.release) so an evicted entry can be torn down rather than
   * leaking a Chromium renderer + JavascriptInterface.
   */
  fun destroy() {
    runOnUiThread {
      bridgeScriptHandler?.remove()
      bridgeScriptHandler = null
      removeOverlay()
      cachedSnapshot?.recycle()
      cachedSnapshot = null
      (webView.parent as? ViewGroup)?.removeView(webView)
      webView.destroy()
      val n = liveCount.decrementAndGet()
      android.util.Log.d(TAG, "WebView DESTROYED key=$key liveCount=$n")
    }
  }

  // Validate an incoming assetHost prop before it becomes the trusted bridge
  // origin ("https://$assetHost") and WebViewAssetLoader.setDomain(assetHost). A
  // malformed value (scheme, path, '/', '@'/userinfo, whitespace, port, query)
  // could corrupt the privileged-origin allowlist or throw on the UI thread, so a
  // candidate that is not a bare hostname falls back to the built-in ASSET_HOST.
  private fun sanitizeAssetHost(candidate: String): String {
    val invalid = {
      android.util.Log.w(
        TAG,
        "Ignoring invalid assetHost '$candidate'; falling back to default $ASSET_HOST",
      )
      ASSET_HOST
    }
    // Cheap rejects first: a bare hostname has no whitespace and no '/'.
    if (candidate.any { it.isWhitespace() } || candidate.contains('/')) return invalid()
    return try {
      val uri = java.net.URI("https://$candidate")
      val bareHost =
        uri.host == candidate &&
        uri.path.isNullOrEmpty() &&
        uri.userInfo == null &&
        uri.query == null &&
        uri.port == -1
      if (bareHost) candidate else invalid()
    } catch (e: Exception) {
      invalid()
    }
  }

  private fun rebuildAssetLoader(localBundle: String?) {
    if (localBundle.isNullOrEmpty()) {
      assetLoader = null
      return
    }
    // Narrow the path handler to ONLY the offline bundle subtree
    // (/<localBundle>/, e.g. /tradingview-assets/) instead of intercepting all of
    // "/". When the bundle is served under the real chart origin (`assetHost`),
    // intercepting "/" would shadow every web path on that domain; with the narrow
    // handler, unmatched paths fall through to the network — so an `assetHost`
    // that is a real public domain keeps behaving normally off-bundle.
    // computeTargetUrl produces https://<assetHost>/<localBundle>/<entry>, which
    // stays inside this handler's path. (Path built as /<localBundle>/ so a
    // localBundle that already has a trailing slash doesn't double it.)
    val bundleDir = localBundle.trim('/')
    val bundlePath = "/$bundleDir/"
    // WebViewAssetLoader strips the registered prefix before calling the handler,
    // so AssetsPathHandler alone would resolve the remainder against the assets
    // ROOT (assets/<entry>) and lose the bundle namespace. Re-prepend <bundleDir>/
    // so we still serve from assets/<localBundle>/<entry>. Returning null on a
    // miss lets WebViewAssetLoader fall through to the network.
    val assets = WebViewAssetLoader.AssetsPathHandler(webView.context)
    val namespacedHandler =
      WebViewAssetLoader.PathHandler { suffix -> assets.handle("$bundleDir/$suffix") }
    assetLoader = WebViewAssetLoader.Builder()
      .setDomain(assetHost)
      .addPathHandler(bundlePath, namespacedHandler)
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
    // Normalize the bundle dir the SAME way rebuildAssetLoader does (trim '/'), so
    // the URL has exactly single slashes between host, bundle dir and entry. A raw
    // localBundle like "/tradingview-assets/" would otherwise yield
    // https://host//tradingview-assets//index.html, which misses the registered
    // handler prefix → falls through to the network → blank chart.
    val bundleDir = localBundle.trim('/')
    return buildString {
      append("https://")
      append(assetHost)
      append('/')
      // Serve the offline bundle from assets/<localBundle>/ (namespaced) rather
      // than the assets root, so it doesn't collide with other bundled assets
      // (e.g. web-embed). The dist uses relative asset paths (PUBLIC_URL='./'),
      // so loading the entry from a subpath resolves the rest correctly.
      append(bundleDir)
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
  // Refcount per key: incremented when a host adopts the entry, decremented when a
  // host releases it. Lets us know when an entry has no live hosts. With the app's
  // single constant reuseKey this stays >= 0 and the entry is kept warm; the count
  // exists so destroy() is *reachable* (see releaseShared) rather than dead code,
  // and so a future multi-key/LRU policy can evict safely.
  private val refCounts = HashMap<String, Int>()

  /** Get (creating if absent) the entry for [key]. Does not change the refcount —
   *  call [adopt] exactly once per host to register a live reference. */
  @Synchronized
  fun acquireShared(key: String, context: Context): PooledChartWebView =
    shared.getOrPut(key) { PooledChartWebView.create(context, key) }

  /** Register one live host reference to [key] (call once per host; idempotency is
   *  the caller's responsibility via adoptedPoolKey). */
  @Synchronized
  fun adopt(key: String) {
    refCounts[key] = (refCounts[key] ?: 0) + 1
  }

  /**
   * A host that previously adopted [key] is going away. Decrements the refcount.
   * By default the entry is kept warm even at zero (intentional single-instance
   * cache — see class doc); pass [destroyWhenIdle] = true to actually tear the
   * WebView down when the last host leaves (used for an LRU/eviction policy).
   */
  @Synchronized
  fun releaseShared(key: String, destroyWhenIdle: Boolean = false) {
    val next = (refCounts[key] ?: 0) - 1
    if (next <= 0) {
      refCounts.remove(key)
      if (destroyWhenIdle) {
        shared.remove(key)?.destroy()
      }
    } else {
      refCounts[key] = next
    }
  }
}
