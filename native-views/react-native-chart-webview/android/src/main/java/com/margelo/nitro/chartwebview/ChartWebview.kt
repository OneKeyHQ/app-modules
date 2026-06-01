package com.margelo.nitro.chartwebview

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Looper
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.webkit.WebResourceErrorCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext
import org.json.JSONObject
import java.net.URLEncoder

@DoNotStrip
class HybridChartWebview(val context: ThemedReactContext) : HybridChartWebviewSpec() {

  companion object {
    // Virtual same-origin host that WebViewAssetLoader serves. The page believes
    // it is loaded from `https://appassets.androidplatform.net/...` so that
    // localStorage / fetch / same-origin policies behave as on a real https site,
    // while the bytes actually come from the APK `assets/` folder offline.
    private const val ASSET_HOST = "appassets.androidplatform.net"
    private const val DEFAULT_ENTRY = "index.html"

    // Injected into every page so the page's outbound messages are forwarded to
    // the native @JavascriptInterface bridge. Three outbound conventions are
    // supported so this stays a dumb pipe regardless of how the page was built:
    //   A) `window.${'$'}onekey.${'$'}private.request(e)` — OneKey's chart/DApp native
    //      bridge (used when the page is told it runs on a native platform).
    //   B) `window.ReactNativeWebView.postMessage(s)` (react-native-webview style).
    //   C) `window.parent.postMessage(envelope, '*')` — in a top-level WebView
    //      `parent === window`, so it surfaces as a `message` event here. Only
    //      the page's own `$private` envelopes are forwarded; the messages WE
    //      inject (native -> page) are not `$private`, so they never echo back.
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
  }

  // --- View ---
  @SuppressLint("SetJavaScriptEnabled")
  private val webView: WebView = WebView(context).apply {
    settings.javaScriptEnabled = true
    // localStorage / sessionStorage used by the chart bundle.
    settings.domStorageEnabled = true
    settings.databaseEnabled = true
    // We serve everything through the virtual https host, so direct file:// and
    // content:// access stays disabled for safety.
    settings.allowFileAccess = false
    settings.allowContentAccess = false
    settings.mediaPlaybackRequiresUserGesture = false

    addJavascriptInterface(ChartBridge(), "AndroidChartBridge")
  }

  override val view: View = webView

  init {
    // Install the outbound bridge BEFORE any page script runs, in every frame.
    // `addDocumentStartJavaScript` is far more reliable than an onPageStarted
    // `evaluateJavascript` (which can race the page's own early code), so the
    // chart's `window.$onekey.$private.request(...)` resolves to our injection.
    if (WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT)) {
      WebViewCompat.addDocumentStartJavaScript(webView, OUTBOUND_BRIDGE_JS, setOf("*"))
    }
  }

  // The asset loader is (re)built when `localBundle` changes so the virtual path
  // prefix matches the bundle folder name inside `assets/`.
  private var assetLoader: WebViewAssetLoader? = null

  // The URL currently loaded, so redundant prop re-applies don't reload (see
  // loadSource). Cleared by reload() to force an explicit reload.
  private var lastLoadedUrl: String? = null

  init {
    webView.webViewClient = object : WebViewClientCompat() {
      override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest,
      ): WebResourceResponse? {
        // Route virtual-host requests to the APK assets; everything else
        // (remote `uri` mode) falls through to the network.
        return assetLoader?.shouldInterceptRequest(request.url)
      }

      override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
        super.onPageStarted(view, url, favicon)
        // Install the outbound bridge as early as possible so messages emitted
        // during the page's own startup are not lost.
        view.evaluateJavascript(OUTBOUND_BRIDGE_JS, null)
      }

      override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        // Re-assert in case the document replaced `window.ReactNativeWebView`.
        view.evaluateJavascript(OUTBOUND_BRIDGE_JS, null)
        onLoadEnd?.invoke()
      }

      override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceErrorCompat,
      ) {
        super.onReceivedError(view, request, error)
        // Only surface main-frame failures; sub-resource errors are noisy and
        // the JS business layer cannot act on them anyway.
        if (request.isForMainFrame) {
          onError?.invoke(error.description?.toString() ?: "WebView error")
        }
      }
    }
  }

  // page -> native dumb pipe. `@JavascriptInterface` callbacks arrive on a
  // WebView JS worker thread, so hop back to the UI thread before invoking the
  // Nitro callback.
  private inner class ChartBridge {
    @JavascriptInterface
    fun postMessage(message: String) {
      runOnUiThread { onMessage?.invoke(message) }
    }
  }

  // --- Source props ---
  // Exactly one source mode is expected: `uri` (remote) takes precedence,
  // otherwise `localBundle` is served from the virtual host. Any change to a
  // source-related prop re-evaluates and (re)loads.

  private var _uri: String? = null
  override var uri: String?
    get() = _uri
    set(value) {
      _uri = value
      loadSource()
    }

  private var _localBundle: String? = null
  override var localBundle: String?
    get() = _localBundle
    set(value) {
      _localBundle = value
      rebuildAssetLoader()
      loadSource()
    }

  private var _entry: String? = null
  override var entry: String?
    get() = _entry
    set(value) {
      _entry = value
      loadSource()
    }

  private var _paramsJson: String? = null
  override var paramsJson: String?
    get() = _paramsJson
    set(value) {
      _paramsJson = value
      loadSource()
    }

  // --- Singleton props (accepted, ignored in the MVP) ---
  // MVP: every view owns its own WebView; the shared singleton pool is not
  // implemented yet. We only persist the values so the prop diffing stays happy.
  private var _reuseKey: String? = null
  override var reuseKey: String?
    get() = _reuseKey
    set(value) { _reuseKey = value }

  private var _pooled: Boolean? = null
  override var pooled: Boolean?
    get() = _pooled
    set(value) { _pooled = value }

  private var _isActive: Boolean? = null
  override var isActive: Boolean?
    get() = _isActive
    set(value) { _isActive = value }

  // --- Event callbacks ---
  override var onMessage: ((message: String) -> Unit)? = null
  override var onLoadEnd: (() -> Unit)? = null
  override var onError: ((message: String) -> Unit)? = null

  // --- Methods ---

  // native -> page dumb pipe. `message` is already a valid JSON string built by
  // the JS business layer. We `JSON.parse` it inside the page so the page
  // receives a structured object on `window.postMessage(obj, '*')`, matching the
  // web `window.postMessage` contract the chart expects.
  override fun postMessage(message: String) {
    val payload = JSONObject.quote(message) // safely embed the raw JSON string
    val js =
      "(function(){try{window.postMessage(JSON.parse($payload), '*');}" +
      "catch(e){window.postMessage($payload, '*');}})();"
    runOnUiThread { webView.evaluateJavascript(js, null) }
  }

  override fun reload() {
    runOnUiThread { webView.reload() }
  }

  // --- Internals ---

  private fun rebuildAssetLoader() {
    val bundle = _localBundle
    if (bundle.isNullOrEmpty()) {
      assetLoader = null
      return
    }
    // Serve the APK `assets/` root at the virtual host root, so the offline
    // bundle's root-relative refs (`/<hash>/...`, depth-independent) all resolve.
    // `localBundle` here only flags offline mode; the files land at assets root
    // (example sourceSets merges the tradingview-assets dir into assets/).
    assetLoader = WebViewAssetLoader.Builder()
      .setDomain(ASSET_HOST)
      .addPathHandler("/", WebViewAssetLoader.AssetsPathHandler(context))
      .build()
  }

  private fun loadSource() {
    val target = computeTargetUrl() ?: return
    // Idempotency guard: source-related prop setters all funnel here, and a React
    // re-render can re-apply unchanged props. Reloading the page on every such
    // call would restart the chart (and feed back into onLoadEnd -> re-render).
    // Only (re)load when the effective URL actually changes.
    if (target == lastLoadedUrl) return
    lastLoadedUrl = target
    runOnUiThread { webView.loadUrl(target) }
  }

  // Resolve the effective URL from the current props: `uri` (remote) wins,
  // otherwise the offline bundle served at the virtual host root. Returns null
  // when no source is set yet.
  private fun computeTargetUrl(): String? {
    val remote = _uri
    if (!remote.isNullOrEmpty()) return remote

    val bundle = _localBundle
    if (bundle.isNullOrEmpty()) return null

    val entryPath = _entry?.takeIf { it.isNotEmpty() } ?: DEFAULT_ENTRY
    val query = buildQueryFromParamsJson(_paramsJson)
    // Served at the virtual host root (see rebuildAssetLoader): no localBundle
    // path segment, so the bundle's root-relative refs resolve.
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

  // Parse the `paramsJson` object string into a URL query string. Values are
  // URL-encoded; non-string values are stringified. Invalid JSON yields no query.
  private fun buildQueryFromParamsJson(paramsJson: String?): String {
    if (paramsJson.isNullOrEmpty()) return ""
    return try {
      val obj = JSONObject(paramsJson)
      val sb = StringBuilder()
      val keys = obj.keys()
      while (keys.hasNext()) {
        val key = keys.next()
        val rawValue = obj.get(key)
        val value = if (rawValue == JSONObject.NULL) "" else rawValue.toString()
        if (sb.isNotEmpty()) sb.append('&')
        sb.append(encode(key))
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
    if (Looper.myLooper() == Looper.getMainLooper()) {
      action()
    } else {
      webView.post { action() }
    }
  }
}
