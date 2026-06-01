package com.margelo.nitro.chartwebview

import android.graphics.Bitmap
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
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

  // Fabric lays its views out top-down and ignores layout requests from children
  // we add at runtime (the WebView / placeholder). Without forcing a measure +
  // layout pass here, those children stay 0x0 and the slot renders blank. This
  // requestLayout override is the standard RN fix for custom ViewGroups.
  private inner class ChartContainer(ctx: android.content.Context) : FrameLayout(ctx) {
    private val relayout = Runnable {
      measure(
        MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
      )
      layout(left, top, right, bottom)
    }

    override fun requestLayout() {
      super.requestLayout()
      post(relayout)
    }
  }

  private val container: FrameLayout = ChartContainer(context).apply {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {
        attached = true
        scheduleReconcile()
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
    set(value) { _reuseKey = value; scheduleReconcile() }

  private var _pooled: Boolean? = null
  override var pooled: Boolean?
    get() = _pooled
    set(value) { _pooled = value; scheduleReconcile() }

  // `active` (JS useIsFocused) decides which host, among those sharing a key,
  // owns the single WebView. null is treated as active (single-host case).
  private var _active: Boolean? = null
  override var active: Boolean?
    get() = _active
    set(value) { _active = value; scheduleReconcile() }

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

  // A single React commit applies props one at a time, so the intermediate
  // states are inconsistent (e.g. `pooled` re-applied while `active` is still
  // stale). Reacting to each setter synchronously makes the wrong host claim
  // mid-commit. Coalesce to ONE reconcile at the end of the run-loop, by which
  // point every prop in the commit holds its final value.
  private var reconcileScheduled = false
  private val reconcileRunnable = Runnable {
    reconcileScheduled = false
    reconcile()
  }

  private fun scheduleReconcile() {
    if (reconcileScheduled) return
    reconcileScheduled = true
    container.post(reconcileRunnable)
  }

  private fun reconcile() {
    if (isPooled()) reconcilePooled() else reconcilePrivate()
  }

  // Pooled: every host sharing the key references the one entry. The active host
  // displays the live WebView; inactive hosts display the cached frame snapshot
  // so their slot isn't blank (and the live WebView reparents on hand-off).
  private fun reconcilePooled() {
    val entry = ChartWebviewPool.acquireShared(effectiveKey(), context)
    backing = entry
    if (wantsOwnership()) {
      removePlaceholder()
      entry.owner = this
      entry.attachTo(container)
      entry.setSource(_uri, _localBundle, _entry, _paramsJson)
      // Keep a fresh frame of OUR content while we own the WebView, so that when
      // we later go inactive (and the shared WebView reloads the other slot's
      // chart) our slot freezes to its own last frame, not the other content.
      startOwnCapture()
    } else {
      // Inactive: give up ownership only if we still hold it, and freeze to our
      // own last captured frame. We do NOT detach (that races a rapid re-claim).
      val wasOwner = entry.owner == this
      if (wasOwner) entry.owner = null
      stopOwnCapture()
      showPlaceholder(ownSnapshot)
    }
  }

  // Non-pooled: a private WebView, claimed when active, kept on this host so a
  // later re-claim reuses it without a reload. No placeholder (single host).
  private fun reconcilePrivate() {
    if (!wantsOwnership()) return
    val pooled = backing ?: PooledChartWebView.create(context, effectiveKey())
    backing = pooled
    pooled.owner = this
    pooled.attachTo(container)
    pooled.setSource(_uri, _localBundle, _entry, _paramsJson)
  }

  private fun applySourceIfOwner() {
    val pooled = backing ?: return
    if (pooled.owner == this) {
      pooled.setSource(_uri, _localBundle, _entry, _paramsJson)
    }
  }

  // --- Per-host snapshot (this host's own last frame, shown while inactive) ---

  private var placeholder: ImageView? = null
  // OUR content's last frame. Because the WebView is shared and reloads other
  // slots' charts, we can't read the snapshot off the pooled entry — we keep our
  // own, captured periodically while we own the WebView.
  private var ownSnapshot: Bitmap? = null

  private val ownCaptureRunnable = object : Runnable {
    override fun run() {
      val entry = backing ?: return
      if (entry.owner != this@HybridChartWebview) return
      // Late async results are dropped if we've meanwhile yielded ownership, so
      // ownSnapshot only ever holds a frame captured while WE were showing.
      entry.captureNow { bmp ->
        if (bmp != null && entry.owner == this@HybridChartWebview) ownSnapshot = bmp
      }
      container.postDelayed(this, 1200)
    }
  }

  private fun startOwnCapture() {
    container.removeCallbacks(ownCaptureRunnable)
    container.postDelayed(ownCaptureRunnable, 500)
  }

  private fun stopOwnCapture() {
    container.removeCallbacks(ownCaptureRunnable)
  }

  private fun showPlaceholder(bmp: Bitmap?) {
    if (bmp == null) return
    val iv = placeholder ?: ImageView(context).apply {
      scaleType = ImageView.ScaleType.FIT_XY
      layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT,
      )
      container.addView(this)
      placeholder = this
    }
    iv.setImageBitmap(bmp)
    iv.bringToFront()
  }

  private fun removePlaceholder() {
    placeholder?.let { container.removeView(it) }
    placeholder = null
  }
}
