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
    // Substring of the chart's render-ready bridge message (method
    // 'tradingview_renderReady'); cheaper than JSON-parsing every page message.
    private const val RENDER_READY_MARKER = "tradingview_renderReady"
    // Safety-net: reveal the live chart even if the render-ready signal is missed.
    private const val REVEAL_FALLBACK_MS = 2000L
    // The one host currently holding a snapshot over a switch, waiting for the
    // render-ready signal. Shared so render-ready (which arrives on whichever host
    // owns the WebView at that instant — not necessarily the awaiting one) reveals
    // the correct host regardless of the owner-handoff timing.
    //
    // WeakReference (iOS uses a `weak` static for the same reason): a strong static
    // would leak the host — and its ReactContext/Activity — if the host unmounts
    // mid-reveal. dispose() also clears it eagerly on detach.
    private var pendingRevealHostRef: java.lang.ref.WeakReference<HybridChartWebview>? = null
    private var pendingRevealHost: HybridChartWebview?
      get() = pendingRevealHostRef?.get()
      set(value) {
        pendingRevealHostRef = value?.let { java.lang.ref.WeakReference(it) }
      }
  }

  private val instanceId = instanceIds.incrementAndGet()

  // Fabric lays its views out top-down and ignores layout requests from children
  // we add at runtime (the WebView / placeholder). Without forcing a measure +
  // layout pass here, those children stay 0x0 and the slot renders blank. This
  // requestLayout override is the standard RN fix for custom ViewGroups.
  private inner class ChartContainer(ctx: android.content.Context) : FrameLayout(ctx), HostAware {
    // Lets the view manager reach this host from the dropped View (onDropViewInstance)
    // to run teardown — see TeardownChartWebviewManager.
    override val chartHost: HybridChartWebview get() = this@HybridChartWebview

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
  // The pooled key this host has refcounted via ChartWebviewPool.adopt (null when
  // not pooled). Makes adopt/release idempotent per host across many reconciles.
  private var adoptedPoolKey: String? = null

  // --- Source props ---

  // Source/bridge setters call applySource (synchronous) — NOT scheduleReconcile,
  // which caused an infinite reconcile loop (reconcile -> setSource -> prop
  // re-apply -> reconcile ...). applySource sets warmDriver + warm-boots the page
  // even when this host is not the visible owner, so post-attach bridge arrival
  // still makes this host the warm driver (page->native callbacks fall back to it).
  private var _uri: String? = null
  override var uri: String?
    get() = _uri
    set(value) { _uri = value; applySource() }

  private var _localBundle: String? = null
  override var localBundle: String?
    get() = _localBundle
    set(value) { _localBundle = value; applySource() }

  private var _entry: String? = null
  override var entry: String?
    get() = _entry
    set(value) { _entry = value; applySource() }

  private var _paramsJson: String? = null
  override var paramsJson: String?
    get() = _paramsJson
    set(value) { _paramsJson = value; applySource() }

  // Document-start bridge JS (single source of truth in the TS layer). Stored and
  // handed to the pooled WebView when we claim it, before its first load.
  private var _bridgeScript: String? = null
  override var bridgeScript: String?
    get() = _bridgeScript
    set(value) { _bridgeScript = value; applySource() }

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
    set(value) {
      val wasActive = _active != false
      _active = value
      // Becoming active: cover with our last frame NOW (synchronously, before the
      // JS symbol-change fires this commit) and wait for the chart's render-ready,
      // so the switch holds the snapshot until the new content paints. Doing it
      // here rather than in the coalesced reconcile is essential — a cached symbol
      // can render + signal before the posted reconcile even runs. ownSnapshot is
      // non-null only for pooled hosts that have been shown before (first show
      // just reveals the live WebView as it loads).
      if (value != false && !wasActive && ownSnapshot != null) {
        showPlaceholder(ownSnapshot)
        awaitContentReveal()
      }
      scheduleReconcile()
    }

  // --- Debugging ---
  // Make the backing WebView inspectable via chrome://inspect. Driven by the
  // app's "Enable Native Webview Debugging" dev-mode toggle, mirroring the main
  // react-native-webview (which calls WebView.setWebContentsDebuggingEnabled).
  // Stored and applied to the backing WebView both here (on prop change) and at
  // host claim, since `backing` is null until the first reconcile assigns it.
  // CAVEAT: setWebContentsDebuggingEnabled is PROCESS-GLOBAL — see
  // PooledChartWebView.setInspectable.
  private var _webviewDebuggingEnabled: Boolean? = null
  override var webviewDebuggingEnabled: Boolean?
    get() = _webviewDebuggingEnabled
    set(value) {
      _webviewDebuggingEnabled = value
      backing?.setInspectable(value)
    }

  // --- Event callbacks ---
  override var onMessage: ((message: String) -> Unit)? = null
  override var onLoadEnd: (() -> Unit)? = null
  override var onError: ((message: String) -> Unit)? = null

  // Called by the backing PooledChartWebView while this host is the owner.
  fun dispatchMessage(message: String) {
    // The chart reports it has painted the new symbol after a switch; that's our
    // cue to drop the snapshot we held over the switch and reveal the live chart.
    if (message.contains(RENDER_READY_MARKER)) onContentRendered()
    onMessage?.invoke(message)
  }
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
    val key = effectiveKey()
    val entry = ChartWebviewPool.acquireShared(key, context)
    backing = entry
    // Apply this host's debug preference to the (possibly freshly created) backing
    // WebView. Mirrors the main react-native-webview's setWebContentsDebuggingEnabled.
    entry.setInspectable(_webviewDebuggingEnabled)
    // Refcount the pool entry once per host (reconcile runs many times). Balanced
    // by releaseShared in dispose(). If the reuseKey changed, release the old one.
    if (adoptedPoolKey != key) {
      adoptedPoolKey?.let { ChartWebviewPool.releaseShared(it) }
      ChartWebviewPool.adopt(key)
      adoptedPoolKey = key
    }
    // WARM-BOOT (mirror of iOS): boot the shared offline page + register as the
    // warm DRIVER as soon as ANY referencing host has the bridge, even when not
    // the visible owner (offscreen prewarm runs with attached=false/active=false).
    // page->native callbacks fall back to warmDriver when there's no owner, so the
    // bars-state / load-end signals aren't dropped during warm — otherwise the
    // chart stays on the loading mask. Idempotent: setSource dedupes on same URL,
    // setBridgeScript no-ops once registered.
    val bs = _bridgeScript
    if (!bs.isNullOrEmpty()) {
      entry.setBridgeScript(bs)
      entry.warmDriver = this
      entry.setSource(_uri, _localBundle, _entry, _paramsJson)
    }
    if (wantsOwnership()) {
      entry.owner = this
      // PERF: a host is now showing the chart — make sure the renderer is running.
      entry.resume()
      entry.attachTo(container)
      // Keep a fresh frame of OUR content while we own the WebView, so that when
      // we later go inactive (and the shared WebView reloads the other slot's
      // chart) our slot freezes to its own last frame, not the other content.
      startOwnCapture()
      // The snapshot hold/reveal over a switch is driven by the `active` setter +
      // render-ready, not here. Just keep a held snapshot on top of the freshly
      // attached WebView, or clear a stale one if we're not holding.
      if (awaitingReveal) placeholder?.bringToFront() else removePlaceholder()
    } else {
      // Inactive: give up ownership only if we still hold it, and freeze to our
      // own last captured frame. We do NOT detach (that races a rapid re-claim).
      val wasOwner = entry.owner == this
      if (wasOwner) entry.owner = null
      stopOwnCapture()
      showPlaceholder(ownSnapshot)
      // PERF: if nobody owns the shared WebView now, pause its renderer so it
      // doesn't keep burning a CPU core + GPU + RAM in the background (Android
      // doesn't auto-throttle offscreen WebViews like iOS does). Resumed on the
      // next CLAIM. No-op until the page's first load completed.
      entry.pauseIfIdle()
    }
  }

  // Non-pooled: a private WebView, claimed when active, kept on this host so a
  // later re-claim reuses it without a reload. No placeholder (single host).
  private fun reconcilePrivate() {
    if (!wantsOwnership()) return
    val pooled = backing ?: PooledChartWebView.create(context, effectiveKey())
    backing = pooled
    pooled.setInspectable(_webviewDebuggingEnabled)
    pooled.owner = this
    pooled.setBridgeScript(_bridgeScript ?: "")
    pooled.attachTo(container)
    pooled.setSource(_uri, _localBundle, _entry, _paramsJson)
  }

  // Apply the source synchronously when a source/bridge prop changes. For a
  // pooled host this ALSO registers it as the warmDriver and warm-boots the page
  // even when it is not the visible owner, so page->native callbacks (bars-state /
  // load-end) fall back to it when no host owns the pool. No scheduleReconcile —
  // that looped. backing is null until the first reconcile assigns it (the
  // reuseKey/pooled/active setters still scheduleReconcile), so warmDriver is set
  // on the first prop change after the pool entry is acquired.
  private fun applySource() {
    val pooled = backing ?: return
    val bs = _bridgeScript
    if (isPooled() && !bs.isNullOrEmpty()) {
      pooled.setBridgeScript(bs)
      pooled.warmDriver = this
      pooled.setSource(_uri, _localBundle, _entry, _paramsJson)
    } else if (pooled.owner == this) {
      pooled.setBridgeScript(_bridgeScript ?: "")
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

  // --- Reveal coordination: hold the snapshot over a switch until the chart
  // reports the new symbol has painted (with a safety-net timeout). ---

  private var awaitingReveal = false
  private val revealFallbackRunnable = Runnable { doReveal() }

  private fun awaitContentReveal() {
    awaitingReveal = true
    pendingRevealHost = this
    container.removeCallbacks(revealFallbackRunnable)
    // Safety net: if the render-ready signal never arrives, reveal anyway so the
    // slot can't get stuck on the snapshot.
    container.postDelayed(revealFallbackRunnable, REVEAL_FALLBACK_MS)
  }

  // The chart signalled the post-switch render is done (see dispatchMessage).
  // Reveal whichever host is holding a snapshot — render-ready may land on a
  // different host than the one waiting, depending on owner-handoff timing.
  private fun onContentRendered() {
    pendingRevealHost?.doReveal()
  }

  private fun doReveal() {
    if (!awaitingReveal) return
    awaitingReveal = false
    if (pendingRevealHost == this) pendingRevealHost = null
    container.removeCallbacks(revealFallbackRunnable)
    removePlaceholder()
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

  // Host teardown. Called by the view manager's onDropViewInstance (the only
  // reliable "host is gone" signal — onViewDetachedFromWindow fires on every
  // navigation, not just final teardown). Tears down the NON-pooled (private)
  // WebView so it doesn't leak a Chromium renderer + JavascriptInterface per
  // mount/unmount. Pooled (shared) backing is intentionally left alive in the
  // warm pool — other hosts may still share it; its lifetime is the pool's.
  override fun dispose() {
    stopOwnCapture()
    container.removeCallbacks(reconcileRunnable)
    container.removeCallbacks(revealFallbackRunnable)
    // Don't leak this host (and its ReactContext/Activity) via the static
    // pendingRevealHost if we unmount mid-reveal.
    if (pendingRevealHost == this) pendingRevealHost = null
    val entry = backing
    if (entry != null && entry.owner == this) entry.owner = null
    if (entry != null && entry.warmDriver == this) entry.warmDriver = null
    // Balance the pool adopt() if this host ever joined a pooled key. Kept warm by
    // default (single-instance cache); the release path makes destroy() reachable.
    adoptedPoolKey?.let { ChartWebviewPool.releaseShared(it) }
    adoptedPoolKey = null
    // A private (non-pooled) instance is owned solely by this host — tear it down
    // so it doesn't leak a Chromium renderer + JavascriptInterface.
    if (entry != null && !isPooled()) entry.destroy()
    backing = null
    removePlaceholder()
    ownSnapshot = null
  }
}
