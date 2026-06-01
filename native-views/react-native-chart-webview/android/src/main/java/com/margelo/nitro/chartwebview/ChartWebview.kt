package com.margelo.nitro.chartwebview

import android.view.View
import android.widget.FrameLayout
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.uimanager.ThemedReactContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * Thin host for a chart WebView. It owns only a container [FrameLayout]; the
 * real WebView lives in a [PooledChartWebView] which can be **shared** across
 * hosts that use the same `reuseKey` (singleton via reparenting). The host:
 *
 *  - claims the backing WebView into its container when it is attached AND
 *    active (`isActive != false`), and releases (detaches) it otherwise;
 *  - forwards page events to its Nitro callbacks while it is the active owner;
 *  - delegates `postMessage` / `reload` to the backing WebView.
 *
 * Ownership arbitration: native attach is the fast claim path; `isActive`
 * (driven by JS `useIsFocused`) is the authoritative arbiter of who, among
 * hosts sharing a key, currently displays the single WebView.
 */
@DoNotStrip
class HybridChartWebview(val context: ThemedReactContext) : HybridChartWebviewSpec() {

  companion object {
    private val instanceIds = AtomicInteger(0)
  }

  private val instanceId = instanceIds.incrementAndGet()

  private val container: FrameLayout = FrameLayout(context).apply {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {
        attached = true
        reconcile()
      }

      override fun onViewDetachedFromWindow(v: View) {
        attached = false
        reconcile()
      }
    })
  }

  override val view: View = container

  /** The WebView backing this host (a shared pool entry, or a private one). */
  private var backing: PooledChartWebView? = null
  private var attached = false

  // --- Source props ---

  private var _uri: String? = null
  override var uri: String?
    get() = _uri
    set(value) { _uri = value; applySourceIfOwner() }

  private var _localBundle: String? = null
  override var localBundle: String?
    get() = _localBundle
    set(value) { _localBundle = value; applySourceIfOwner() }

  private var _entry: String? = null
  override var entry: String?
    get() = _entry
    set(value) { _entry = value; applySourceIfOwner() }

  private var _paramsJson: String? = null
  override var paramsJson: String?
    get() = _paramsJson
    set(value) { _paramsJson = value; applySourceIfOwner() }

  // --- Singleton props ---
  // `pooled` + non-empty `reuseKey` => the backing WebView is shared (keyed by
  // reuseKey) across hosts; otherwise the host owns a private WebView.
  private var _reuseKey: String? = null
  override var reuseKey: String?
    get() = _reuseKey
    set(value) { _reuseKey = value; reconcile() }

  private var _pooled: Boolean? = null
  override var pooled: Boolean?
    get() = _pooled
    set(value) { _pooled = value; reconcile() }

  // `active` (JS useIsFocused) decides which host, among those sharing a key,
  // owns the single WebView. null is treated as active (single-host case).
  private var _active: Boolean? = null
  override var active: Boolean?
    get() = _active
    set(value) { _active = value; reconcile() }

  // --- Event callbacks ---
  override var onMessage: ((message: String) -> Unit)? = null
  override var onLoadEnd: (() -> Unit)? = null
  override var onError: ((message: String) -> Unit)? = null

  // Called by the backing PooledChartWebView while this host is the owner.
  fun dispatchMessage(message: String) { onMessage?.invoke(message) }
  fun dispatchLoadEnd() { onLoadEnd?.invoke() }
  fun dispatchError(message: String) { onError?.invoke(message) }

  // --- Methods ---
  override fun postMessage(message: String) { backing?.postMessage(message) }
  override fun reload() { backing?.reload() }
  override fun clearSnapshot() { backing?.clearSnapshot() }

  // --- Ownership reconciliation ---

  private fun isPooled(): Boolean = (_pooled == true) && !_reuseKey.isNullOrEmpty()

  private fun effectiveKey(): String =
    if (isPooled()) _reuseKey!! else "private:$instanceId"

  private fun wantsOwnership(): Boolean = attached && (_active != false)

  private fun reconcile() {
    if (wantsOwnership()) claim() else release()
  }

  private fun claim() {
    val pooled = if (isPooled()) {
      ChartWebviewPool.acquireShared(effectiveKey(), context)
    } else {
      backing ?: PooledChartWebView.create(context, effectiveKey())
    }
    backing = pooled
    pooled.owner = this
    pooled.attachTo(container)
    pooled.setSource(_uri, _localBundle, _entry, _paramsJson)
  }

  private fun release() {
    val pooled = backing ?: return
    if (pooled.owner == this) {
      pooled.detachFromParent()
      pooled.owner = null
    }
    // Pooled entries stay warm in the pool; a private (non-pooled) backing is
    // kept on this host so a later re-claim reuses it without a reload.
  }

  private fun applySourceIfOwner() {
    val pooled = backing ?: return
    if (pooled.owner == this) {
      pooled.setSource(_uri, _localBundle, _entry, _paramsJson)
    }
  }
}
