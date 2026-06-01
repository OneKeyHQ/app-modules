package com.margelo.nitro.chartwebview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.view.ViewGroup
import android.webkit.JavascriptInterface
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
      (webView.parent as? ViewGroup)?.removeView(webView)
      container.addView(
        webView,
        ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT,
        ),
      )
    }
  }

  /** Remove the WebView from its current parent (keeps it alive, warm). */
  fun detachFromParent() {
    runOnUiThread { (webView.parent as? ViewGroup)?.removeView(webView) }
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
