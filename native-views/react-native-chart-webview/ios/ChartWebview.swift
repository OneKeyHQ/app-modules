import Foundation
import UIKit
import WebKit

// MARK: - Constants (shared)

private enum ChartWebviewConst {
  /// Custom scheme used for the offline virtual same-origin.
  static let customScheme = "onekey-chart"
  /// Virtual host used when serving offline content.
  static let virtualHost = "chart"
  /// Name of the page -> native message handler.
  static let messageHandlerName = "onekeyChart"
}

// MARK: - ChartContainerView (window-attach detection)

/// The host's `view`. Reports when it is attached to / detached from a window so
/// the host can claim / release the shared WebView at the right time.
final class ChartContainerView: UIView {
  var onWindowChange: ((_ attached: Bool) -> Void)?

  override func didMoveToWindow() {
    super.didMoveToWindow()
    onWindowChange?(window != nil)
  }
}

// MARK: - HybridChartWebview (thin host)

/// Thin host for a chart WebView. It owns only a container view; the real
/// WKWebView lives in a `PooledChartWebView` which can be **shared** across
/// hosts that use the same `reuseKey` (singleton via reparenting).
///
/// The host claims the backing WebView into its container when it is attached
/// AND active (`active != false`), releases it otherwise, forwards page events
/// to its Nitro callbacks while it is the active owner, and delegates
/// `postMessage` / `reload` to the backing WebView.
class HybridChartWebview: HybridChartWebviewSpec {

  private static var instanceIds = 0
  private let instanceId: Int = {
    HybridChartWebview.instanceIds += 1
    return HybridChartWebview.instanceIds
  }()

  // Substring of the chart's render-ready bridge message (method
  // 'tradingview_renderReady'); cheaper than JSON-parsing every page message.
  private static let renderReadyMarker = "tradingview_renderReady"
  // Safety-net: reveal the live chart even if the render-ready signal is missed.
  private static let revealFallback: TimeInterval = 2.0
  // The one host currently holding a snapshot over a switch, waiting for the
  // render-ready signal. Shared so render-ready (which arrives on whichever host
  // owns the WebView at that instant — not necessarily the awaiting one) reveals
  // the correct host regardless of the owner-handoff timing.
  private static weak var pendingRevealHost: HybridChartWebview?

  // MARK: - HybridView

  private let container = ChartContainerView()
  var view: UIView { container }

  /// The WebView backing this host (a shared pool entry, or a private one).
  private var backing: PooledChartWebView?
  private var attached = false
  // The pooled key this host has refcounted via the pool (nil when not pooled).
  // Makes adopt/release idempotent per host across many reconciles.
  private var adoptedPoolKey: String?

  override init() {
    super.init()
    container.onWindowChange = { [weak self] attached in
      self?.attached = attached
      self?.scheduleReconcile()
    }
  }

  // Host teardown. ARC deallocates the host when the view manager drops it; this is
  // the reliable "host is gone" signal. Tears down the NON-pooled (private) WebView
  // so it doesn't leak, balances the pool refcount for the pooled path, and clears
  // the static pendingRevealHost if we go away mid-reveal.
  deinit {
    revealFallbackWork?.cancel()
    if HybridChartWebview.pendingRevealHost === self {
      HybridChartWebview.pendingRevealHost = nil
    }
    let entry = backing
    if let key = adoptedPoolKey {
      // Pooled: balance the adopt(). Kept warm by default; release makes the
      // pool's destroy() reachable.
      ChartWebviewPool.shared.releaseShared(key: key)
    } else {
      // Private: this host solely owns the WebView — tear it down.
      entry?.destroy()
    }
  }

  // MARK: - Props (source)

  var uri: String? { didSet { applySourceIfOwner() } }
  var localBundle: String? { didSet { applySourceIfOwner() } }
  var entry: String? { didSet { applySourceIfOwner() } }
  var paramsJson: String? { didSet { applySourceIfOwner() } }

  // Document-start bridge JS (single source of truth in the TS layer). Handed to
  // the pooled WebView when we claim it, before its first load. Re-applying the
  // source on change triggers a deferred load if this prop lands after the source.
  var bridgeScript: String? { didSet { applySourceIfOwner() } }

  // MARK: - Props (singleton)

  // `pooled` + non-empty `reuseKey` => the backing WebView is shared (keyed by
  // reuseKey) across hosts; otherwise the host owns a private WebView.
  var reuseKey: String? { didSet { scheduleReconcile() } }
  var pooled: Bool? { didSet { scheduleReconcile() } }
  // `active` (JS useIsFocused) decides which host, among those sharing a key,
  // owns the single WebView. nil is treated as active (single-host case).
  var active: Bool? {
    didSet {
      // Becoming active: cover with our last frame NOW (synchronously, before the
      // JS symbol-change fires this commit) and wait for the chart's render-ready,
      // so the switch holds the snapshot until the new content paints. Doing it
      // here rather than in the coalesced reconcile is essential — a cached symbol
      // can render + signal before the async reconcile even runs. ownSnapshot is
      // non-nil only for pooled hosts shown before (first show reveals live).
      let wasActive = (oldValue ?? true) != false
      if active != false, !wasActive, let snapshot = ownSnapshot {
        showPlaceholder(snapshot)
        awaitContentReveal()
      }
      scheduleReconcile()
    }
  }

  // MARK: - Props (events)

  var onMessage: ((_ message: String) -> Void)?
  var onLoadEnd: (() -> Void)?
  var onError: ((_ message: String) -> Void)?

  // Called by the backing PooledChartWebView while this host is the owner.
  func handleMessage(_ message: String) {
    // The chart reports it has painted the new symbol after a switch; that's our
    // cue to drop the snapshot we held over the switch and reveal the live chart.
    if message.contains(HybridChartWebview.renderReadyMarker) { onContentRendered() }
    onMessage?(message)
  }
  func handleLoadEnd() { onLoadEnd?() }
  func handleError(_ message: String) { onError?(message) }

  // MARK: - Methods

  func postMessage(message: String) throws { backing?.postMessage(message) }
  func reload() throws { backing?.reload() }
  func clearSnapshot() throws { backing?.clearSnapshot() }

  // MARK: - Ownership reconciliation

  private func isPooled() -> Bool {
    if let key = reuseKey, !key.isEmpty, pooled == true { return true }
    return false
  }

  private func effectiveKey() -> String {
    isPooled() ? reuseKey! : "private:\(instanceId)"
  }

  private func wantsOwnership() -> Bool { attached && (active != false) }

  // A single React commit applies props one at a time, so the intermediate
  // states are inconsistent (e.g. `pooled` re-applied while `active` is still
  // stale). Reacting to each setter synchronously makes the wrong host claim
  // mid-commit. Coalesce to ONE reconcile at the end of the run-loop, by which
  // point every prop in the commit holds its final value.
  private var reconcileScheduled = false

  private func scheduleReconcile() {
    if reconcileScheduled { return }
    reconcileScheduled = true
    DispatchQueue.main.async { [weak self] in
      self?.reconcileScheduled = false
      self?.reconcile()
    }
  }

  private func reconcile() {
    if isPooled() { reconcilePooled() } else { reconcilePrivate() }
  }

  // Pooled: every host sharing the key references the one entry. The active host
  // displays the live WebView; inactive hosts display the cached frame snapshot
  // so their slot isn't blank (and the live WebView reparents on hand-off).
  private func reconcilePooled() {
    let key = effectiveKey()
    let pooled = ChartWebviewPool.shared.acquireShared(key: key)
    backing = pooled
    // Refcount the entry once per host (reconcile runs many times). Balanced by
    // releaseShared in deinit. If the reuseKey changed, release the old one first.
    if adoptedPoolKey != key {
      if let old = adoptedPoolKey { ChartWebviewPool.shared.releaseShared(key: old) }
      ChartWebviewPool.shared.adopt(key: key)
      adoptedPoolKey = key
    }
    if wantsOwnership() {
      pooled.owner = self
      // Register the document-start bridge before the first load (the prop is set
      // by now; the pool may have been created earlier by a window-attach reconcile).
      pooled.setBridgeScript(bridgeScript ?? "")
      pooled.attach(to: container)
      pooled.setSource(uri: uri, localBundle: localBundle, entry: entry, paramsJson: paramsJson)
      // Keep a fresh frame of OUR content while we own the WebView, so that when
      // we later go inactive (and the shared WebView reloads the other slot's
      // chart) our slot freezes to its own last frame, not the other content.
      startOwnCapture()
      // The snapshot hold/reveal over a switch is driven by the `active` setter +
      // render-ready, not here. Just keep a held snapshot on top of the freshly
      // attached WebView, or clear a stale one if we're not holding.
      if awaitingReveal {
        if let ph = placeholder { container.bringSubviewToFront(ph) }
      } else {
        removePlaceholder()
      }
    } else {
      // Inactive: give up ownership only if we still hold it, and freeze to our
      // own last captured frame. We do NOT detach (that races a rapid re-claim).
      let wasOwner = pooled.owner === self
      if wasOwner { pooled.owner = nil }
      stopOwnCapture()
      showPlaceholder(ownSnapshot)
    }
  }

  // OUR content's last frame. Because the WebView is shared and reloads other
  // slots' charts, we keep our own snapshot, captured periodically while we own
  // the WebView (late results dropped once we yield, so it only holds our frame).
  private var ownSnapshot: UIImage?
  private var capturing = false

  private func startOwnCapture() {
    if capturing { return }
    capturing = true
    scheduleOwnCapture()
  }

  private func stopOwnCapture() { capturing = false }

  private func scheduleOwnCapture() {
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.6) { [weak self] in
      guard let self = self, self.capturing,
            let entry = self.backing, entry.owner === self else { return }
      entry.captureNow { [weak self] image in
        guard let self = self else { return }
        if let image = image, entry.owner === self { self.ownSnapshot = image }
      }
      if self.capturing { self.scheduleOwnCapture() }
    }
  }

  // Non-pooled: a private WebView, claimed when active, kept on this host so a
  // later re-claim reuses it without a reload. No placeholder (single host).
  private func reconcilePrivate() {
    guard wantsOwnership() else { return }
    let pooled = backing ?? PooledChartWebView(key: effectiveKey())
    backing = pooled
    pooled.owner = self
    pooled.setBridgeScript(bridgeScript ?? "")
    pooled.attach(to: container)
    pooled.setSource(uri: uri, localBundle: localBundle, entry: entry, paramsJson: paramsJson)
  }

  private func applySourceIfOwner() {
    guard let pooled = backing, pooled.owner === self else { return }
    // Register the bridge before any load setSource may trigger (setSource defers
    // loading until the bridge is registered).
    pooled.setBridgeScript(bridgeScript ?? "")
    pooled.setSource(uri: uri, localBundle: localBundle, entry: entry, paramsJson: paramsJson)
  }

  // MARK: - Snapshot placeholder (shown while this host is inactive)

  private var placeholder: UIImageView?

  private func showPlaceholder(_ image: UIImage?) {
    guard let image = image else { return }
    let iv: UIImageView
    if let existing = placeholder {
      iv = existing
    } else {
      iv = UIImageView()
      iv.contentMode = .scaleToFill
      iv.frame = container.bounds
      iv.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      container.addSubview(iv)
      placeholder = iv
    }
    iv.image = image
    container.bringSubviewToFront(iv)
  }

  private func removePlaceholder() {
    placeholder?.removeFromSuperview()
    placeholder = nil
  }

  // MARK: - Reveal coordination (hold the snapshot over a switch until the chart
  // reports the new symbol has painted, with a safety-net timeout)

  private var awaitingReveal = false
  private var revealFallbackWork: DispatchWorkItem?

  private func awaitContentReveal() {
    awaitingReveal = true
    HybridChartWebview.pendingRevealHost = self
    revealFallbackWork?.cancel()
    let work = DispatchWorkItem { [weak self] in self?.doReveal() }
    revealFallbackWork = work
    // Safety net: reveal even if the render-ready signal is missed.
    DispatchQueue.main.asyncAfter(
      deadline: .now() + HybridChartWebview.revealFallback, execute: work)
  }

  // The chart signalled the post-switch render is done (see handleMessage).
  // Reveal whichever host is holding a snapshot — render-ready may land on a
  // different host than the one waiting, depending on owner-handoff timing.
  private func onContentRendered() {
    HybridChartWebview.pendingRevealHost?.doReveal()
  }

  private func doReveal() {
    guard awaitingReveal else { return }
    awaitingReveal = false
    if HybridChartWebview.pendingRevealHost === self {
      HybridChartWebview.pendingRevealHost = nil
    }
    revealFallbackWork?.cancel()
    revealFallbackWork = nil
    removePlaceholder()
  }
}

// MARK: - PooledChartWebView (owns the WKWebView)

/// Owns one real `WKWebView` together with its message bridge, custom-scheme
/// handler and load state. A single instance can be **reparented** across
/// multiple hosts that share a `reuseKey`, so N mount points are backed by ONE
/// WebView (state preserved, no reload on hand-off). Page events are routed to
/// the current `owner`.
final class PooledChartWebView {
  // Live WebView count, logged on create/destroy — the signal the example uses
  // to verify the singleton (N hosts sharing a key => one "CREATED" line).
  private static var liveCount = 0

  let key: String
  weak var owner: HybridChartWebview?

  // The page's user-content controller, kept so the document-start bridge can be
  // added lazily (see setBridgeScript) once the host has the prop value.
  private var userContent: WKUserContentController?
  private var bridgeRegistered = false
  // The bridge script currently registered, so a differing script from a second
  // host sharing a reuseKey is re-registered rather than silently dropped (fix #4).
  private var registeredBridgeScript: String?

  private var webView: WKWebView!
  private var proxy: ChartWebViewProxy!
  private var lastLoadedUrl: String?
  private var attachGeneration = 0

  /// Read by the scheme handler to resolve offline files.
  fileprivate var currentLocalBundle: String?

  // Last rendered frame, used to mask the brief blank frame the reparent (move)
  // can produce. WKWebView renders out-of-process, so a *synchronous* capture
  // returns blank — we keep an asynchronously-captured snapshot fresh instead
  // (after load and after each move) and show it as an overlay on the next move.
  private var cachedSnapshot: UIImage?
  private var overlay: UIImageView?

  init(key: String) {
    self.key = key
    PooledChartWebView.liveCount += 1
    NSLog("[ChartWebviewPool] WebView CREATED key=\(key) liveCount=\(PooledChartWebView.liveCount)")
    setupWebView()
  }

  // Called by the host before the first load. Registers the document-start bridge
  // (shim + the shared TS bridgeScript) once a non-empty script is available;
  // subsequent calls are no-ops. Done lazily because the first reconcile
  // (window-attach) can run before the bridgeScript prop is applied.
  func setBridgeScript(_ bridgeScript: String) {
    guard !bridgeScript.isEmpty, let userContent = userContent else { return }
    // Re-register if a second host sharing the reuseKey supplies a DIFFERENT script
    // instead of silently dropping it (fix #4). In the app's single-reuseKey +
    // constant-bridge reality this never fires; not latching keeps it correct.
    if bridgeRegistered, registeredBridgeScript == bridgeScript { return }
    if bridgeRegistered {
      // WKUserScripts can't be removed individually; drop all and re-add ours.
      userContent.removeAllUserScripts()
    }
    bridgeRegistered = true
    registeredBridgeScript = bridgeScript
    let handlerName = ChartWebviewConst.messageHandlerName
    let shim = "(function(){window.__chartNativePost=function(s){"
      + "window.webkit.messageHandlers.\(handlerName).postMessage(s);};})();"
    let userScript = WKUserScript(
      source: shim + "\n" + bridgeScript,
      injectionTime: .atDocumentStart,
      // Main frame only: the privileged bridge (window.$onekey.$private.request etc.)
      // must not be exposed to cross-origin iframes. The chart page runs in the main
      // frame, so it still gets the full bridge (fix #1).
      forMainFrameOnly: true
    )
    userContent.addUserScript(userScript)
  }

  private func setupWebView() {
    let proxy = ChartWebViewProxy(pooled: self)
    self.proxy = proxy

    let config = WKWebViewConfiguration()
    let preferences = WKPreferences()
    preferences.javaScriptCanOpenWindowsAutomatically = false
    config.preferences = preferences
    if #available(iOS 14.0, *) {
      let pagePrefs = WKWebpagePreferences()
      pagePrefs.allowsContentJavaScript = true
      config.defaultWebpagePreferences = pagePrefs
    } else {
      config.preferences.javaScriptEnabled = true
    }

    config.setURLSchemeHandler(proxy, forURLScheme: ChartWebviewConst.customScheme)

    let userContent = WKUserContentController()
    userContent.add(proxy, name: ChartWebviewConst.messageHandlerName)
    // The document-start bridge is added later via setBridgeScript (the TS prop
    // isn't available yet at construction).
    self.userContent = userContent
    config.userContentController = userContent

    let webView = WKWebView(frame: .zero, configuration: config)
    webView.navigationDelegate = proxy
    webView.uiDelegate = proxy
    webView.scrollView.bounces = false
    if #available(iOS 16.4, *) {
      webView.isInspectable = true
    }
    self.webView = webView
  }

  // MARK: - Reparenting

  /// Move the WebView into `container`, detaching it from any previous parent.
  func attach(to container: UIView) {
    attachGeneration += 1
    let generation = attachGeneration
    runOnMain { [weak self] in
      guard let self = self, let webView = self.webView else { return }
      guard generation == self.attachGeneration else { return }
      guard webView.superview !== container else { return }
      webView.removeFromSuperview()
      webView.frame = container.bounds
      webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
      container.addSubview(webView)
      self.showSnapshotOverlay(in: container)
      // Capture a fresh snapshot (async) so the NEXT move has a current frame.
      self.refreshSnapshotSoon()
    }
  }

  /// Remove the WebView from its current parent (keeps it alive, warm).
  func detachFromParent() {
    runOnMain { [weak self] in
      self?.removeOverlay()
      self?.webView?.removeFromSuperview()
    }
  }

  /// Permanently tear down the WebView and its bridge. Used for the non-pooled
  /// (private) path on host teardown, and reachable for the pool (see
  /// ChartWebviewPool.releaseShared). Stops loading, detaches the view, and
  /// removes the script message handler so the proxy retain is released.
  func destroy() {
    runOnMain { [weak self] in
      guard let self = self else { return }
      self.removeOverlay()
      self.cachedSnapshot = nil
      if let webView = self.webView {
        webView.stopLoading()
        webView.navigationDelegate = nil
        webView.uiDelegate = nil
        webView.removeFromSuperview()
      }
      // Drop the page -> native handler so the user-content controller stops
      // retaining the proxy (and re-entrancy can't deliver to a torn-down owner).
      self.userContent?.removeScriptMessageHandler(forName: ChartWebviewConst.messageHandlerName)
      self.userContent?.removeAllUserScripts()
      self.userContent = nil
      self.webView = nil
      PooledChartWebView.liveCount -= 1
      NSLog("[ChartWebviewPool] WebView DESTROYED key=\(self.key) liveCount=\(PooledChartWebView.liveCount)")
    }
  }

  /// Drop the cached snapshot and remove any visible overlay.
  func clearSnapshot() {
    runOnMain { [weak self] in
      self?.removeOverlay()
      self?.cachedSnapshot = nil
    }
  }

  /// The last captured frame (shown by inactive hosts as a placeholder).
  func snapshot() -> UIImage? { cachedSnapshot }

  /// Asynchronously capture the current frame shortly after things settle.
  func refreshSnapshotSoon() {
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) { [weak self] in
      self?.takeSnapshotNow(nil)
    }
  }

  /// Capture now and deliver the fresh frame (so the host losing ownership can
  /// upgrade its placeholder to its very last frame).
  func captureNow(_ callback: @escaping (UIImage?) -> Void) {
    takeSnapshotNow(callback)
  }

  private func takeSnapshotNow(_ callback: ((UIImage?) -> Void)?) {
    // Don't capture mid-reparent (overlay visible): the page may be blank for a
    // frame and we'd cache a black image. Keep the last good frame instead.
    guard overlay == nil, let webView = webView, webView.superview != nil,
          webView.bounds.width > 0, webView.bounds.height > 0 else {
      callback?(cachedSnapshot)
      return
    }
    let config = WKSnapshotConfiguration()
    config.afterScreenUpdates = false
    webView.takeSnapshot(with: config) { [weak self] image, _ in
      if let image = image { self?.cachedSnapshot = image }
      callback?(self?.cachedSnapshot)
    }
  }

  private func showSnapshotOverlay(in container: UIView) {
    guard let snap = cachedSnapshot else { return }
    removeOverlay()
    let iv = UIImageView(image: snap)
    iv.frame = container.bounds
    iv.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    iv.contentMode = .scaleToFill
    container.addSubview(iv) // added last => on top of the WebView
    overlay = iv
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.08) { [weak self] in
      self?.removeOverlay()
    }
  }

  private func removeOverlay() {
    overlay?.removeFromSuperview()
    overlay = nil
  }

  // MARK: - Loading

  /// Apply the source props and (re)load only when the effective URL changes —
  /// so reparenting / redundant prop re-applies never reload (which would lose
  /// chart state, the whole point of pooling).
  func setSource(uri: String?, localBundle: String?, entry: String?, paramsJson: String?) {
    // Never load before the document-start bridge is registered — otherwise the
    // page boots without the bridge and its first $private requests are lost. The
    // host re-calls setSource once the bridgeScript prop arrives.
    guard bridgeRegistered else { return }
    currentLocalBundle = localBundle
    guard let urlString = computeTargetUrl(uri: uri, localBundle: localBundle, entry: entry, paramsJson: paramsJson) else { return }
    guard urlString != lastLoadedUrl else { return }
    guard let url = URL(string: urlString) else {
      owner?.handleError("Invalid url: \(urlString)")
      return
    }
    lastLoadedUrl = urlString
    runOnMain { [weak self] in self?.webView?.load(URLRequest(url: url)) }
  }

  private func computeTargetUrl(uri: String?, localBundle: String?, entry: String?, paramsJson: String?) -> String? {
    if let uri = uri, !uri.isEmpty { return uri }
    if let localBundle = localBundle, !localBundle.isEmpty {
      let entryFile = (entry?.isEmpty == false) ? entry! : "index.html"
      let query = buildQueryString(fromParamsJson: paramsJson)
      var urlString = "\(ChartWebviewConst.customScheme)://\(ChartWebviewConst.virtualHost)/\(entryFile)"
      if !query.isEmpty { urlString += "?\(query)" }
      return urlString
    }
    return nil
  }

  private func buildQueryString(fromParamsJson json: String?) -> String {
    guard let json = json, !json.isEmpty,
          let data = json.data(using: .utf8),
          let obj = try? JSONSerialization.jsonObject(with: data),
          let dict = obj as? [String: Any] else {
      return ""
    }
    var allowed = CharacterSet.urlQueryAllowed
    allowed.remove(charactersIn: "&=+?#")
    var pairs: [String] = []
    for (key, value) in dict {
      let stringValue: String
      switch value {
      case let s as String: stringValue = s
      case let b as Bool: stringValue = b ? "true" : "false"
      case let n as NSNumber: stringValue = n.stringValue
      default: stringValue = "\(value)"
      }
      let encodedKey = key.addingPercentEncoding(withAllowedCharacters: allowed) ?? key
      let encodedValue = stringValue.addingPercentEncoding(withAllowedCharacters: allowed) ?? stringValue
      pairs.append("\(encodedKey)=\(encodedValue)")
    }
    return pairs.sorted().joined(separator: "&")
  }

  // MARK: - Bridge methods

  func postMessage(_ message: String) {
    runOnMain { [weak self] in
      guard let self = self, let webView = self.webView else { return }
      let jsStringLiteral = self.jsStringLiteral(from: message)
      let js = "window.postMessage(JSON.parse(\(jsStringLiteral)), '*')"
      webView.evaluateJavaScript(js, completionHandler: nil)
    }
  }

  func reload() {
    runOnMain { [weak self] in self?.webView?.reload() }
  }

  private func jsStringLiteral(from raw: String) -> String {
    if let data = try? JSONSerialization.data(withJSONObject: [raw], options: []),
       let arrStr = String(data: data, encoding: .utf8) {
      var s = arrStr
      if s.hasPrefix("[") { s.removeFirst() }
      if s.hasSuffix("]") { s.removeLast() }
      return s.trimmingCharacters(in: .whitespacesAndNewlines)
    }
    let escaped = raw
      .replacingOccurrences(of: "\\", with: "\\\\")
      .replacingOccurrences(of: "\"", with: "\\\"")
      .replacingOccurrences(of: "\n", with: "\\n")
      .replacingOccurrences(of: "\r", with: "\\r")
    return "\"\(escaped)\""
  }

  private func runOnMain(_ block: @escaping () -> Void) {
    if Thread.isMainThread { block() } else { DispatchQueue.main.async(execute: block) }
  }
}

// MARK: - ChartWebViewProxy (NSObject host for the WebKit conformances)

/// Owns every WebKit delegate / handler conformance that requires `NSObject`,
/// forwarding back to the pooled WebView's current owner. Held strongly by the
/// PooledChartWebView; references it weakly to avoid a retain cycle.
private final class ChartWebViewProxy: NSObject {
  private weak var pooled: PooledChartWebView?

  init(pooled: PooledChartWebView) {
    self.pooled = pooled
    super.init()
  }
}

// MARK: - WKScriptMessageHandler (page -> native)

extension ChartWebViewProxy: WKScriptMessageHandler {
  func userContentController(
    _ userContentController: WKUserContentController,
    didReceive message: WKScriptMessage
  ) {
    guard message.name == ChartWebviewConst.messageHandlerName else { return }
    let owner = pooled?.owner
    if let body = message.body as? String {
      owner?.handleMessage(body)
    } else if let data = try? JSONSerialization.data(withJSONObject: message.body, options: []),
              let str = String(data: data, encoding: .utf8) {
      owner?.handleMessage(str)
    } else {
      owner?.handleMessage("\(message.body)")
    }
  }
}

// MARK: - WKNavigationDelegate (load/error events)

extension ChartWebViewProxy: WKNavigationDelegate {
  func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
    pooled?.owner?.handleLoadEnd()
    // Prime the snapshot so the first move already has a frame to mask with.
    pooled?.refreshSnapshotSoon()
  }

  func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
    pooled?.owner?.handleError(error.localizedDescription)
  }

  func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
    pooled?.owner?.handleError(error.localizedDescription)
  }
}

// MARK: - WKUIDelegate

extension ChartWebViewProxy: WKUIDelegate {}

// MARK: - WKURLSchemeHandler (offline virtual same-origin)

extension ChartWebViewProxy: WKURLSchemeHandler {
  func webView(_ webView: WKWebView, start urlSchemeTask: WKURLSchemeTask) {
    guard let url = urlSchemeTask.request.url else {
      urlSchemeTask.didFailWithError(
        NSError(domain: "ChartWebview", code: -1, userInfo: [NSLocalizedDescriptionKey: "Missing url"])
      )
      return
    }

    guard let localBundle = pooled?.currentLocalBundle, !localBundle.isEmpty else {
      respondNotFound(url: url, task: urlSchemeTask)
      return
    }

    var relativePath = url.path
    if relativePath.hasPrefix("/") { relativePath.removeFirst() }
    if relativePath.isEmpty { relativePath = "index.html" }

    guard let fileURL = resolveBundleFileURL(localBundle: localBundle, relativePath: relativePath),
          let data = try? Data(contentsOf: fileURL) else {
      respondNotFound(url: url, task: urlSchemeTask)
      return
    }

    let headers: [String: String] = [
      "Content-Type": mimeTypeForPath(fileURL.pathExtension),
      "Content-Length": "\(data.count)",
      "Access-Control-Allow-Origin": "*",
      "Cache-Control": "no-cache",
    ]
    guard let response = HTTPURLResponse(url: url, statusCode: 200, httpVersion: "HTTP/1.1", headerFields: headers) else {
      respondNotFound(url: url, task: urlSchemeTask)
      return
    }

    urlSchemeTask.didReceive(response)
    urlSchemeTask.didReceive(data)
    urlSchemeTask.didFinish()
  }

  func webView(_ webView: WKWebView, stop urlSchemeTask: WKURLSchemeTask) {}

  private func resolveBundleFileURL(localBundle: String, relativePath: String) -> URL? {
    let resourceURL = Bundle.main.resourceURL ?? Bundle.main.bundleURL
    let baseDir = resourceURL.appendingPathComponent(localBundle, isDirectory: true)
    let candidate = baseDir.appendingPathComponent(relativePath).standardizedFileURL
    let basePath = baseDir.standardizedFileURL.path
    guard candidate.path == basePath || candidate.path.hasPrefix(basePath + "/") else { return nil }
    let fm = FileManager.default
    var isDir: ObjCBool = false
    guard fm.fileExists(atPath: candidate.path, isDirectory: &isDir), !isDir.boolValue else { return nil }
    return candidate
  }

  private func respondNotFound(url: URL, task: WKURLSchemeTask) {
    let body = "Not Found".data(using: .utf8) ?? Data()
    let response = HTTPURLResponse(
      url: url,
      statusCode: 404,
      httpVersion: "HTTP/1.1",
      headerFields: ["Content-Type": "text/plain", "Access-Control-Allow-Origin": "*"]
    )!
    task.didReceive(response)
    task.didReceive(body)
    task.didFinish()
  }

  private func mimeTypeForPath(_ ext: String) -> String {
    switch ext.lowercased() {
    case "html", "htm": return "text/html; charset=utf-8"
    case "js", "mjs": return "application/javascript; charset=utf-8"
    case "css": return "text/css; charset=utf-8"
    case "json", "map": return "application/json; charset=utf-8"
    case "wasm": return "application/wasm"
    case "woff2": return "font/woff2"
    case "woff": return "font/woff"
    case "ttf": return "font/ttf"
    case "otf": return "font/otf"
    case "eot": return "application/vnd.ms-fontobject"
    case "svg": return "image/svg+xml"
    case "png": return "image/png"
    case "jpg", "jpeg": return "image/jpeg"
    case "gif": return "image/gif"
    case "webp": return "image/webp"
    case "ico": return "image/x-icon"
    case "txt": return "text/plain; charset=utf-8"
    default: return "application/octet-stream"
    }
  }
}

// MARK: - ChartWebviewPool (warm pool keyed by reuseKey)

/// Warm pool of `PooledChartWebView`s keyed by `reuseKey`. Entries are created
/// on first use and kept warm (a single static chart instance, OKX-style), so
/// the next mount point that claims the same key reuses the live WebView instead
/// of recreating it. Non-pooled hosts don't use this — they own a private one.
final class ChartWebviewPool {
  static let shared = ChartWebviewPool()
  private var entries: [String: PooledChartWebView] = [:]
  // Refcount per key: incremented on adopt, decremented on release. Lets us know
  // when an entry has no live hosts. With the app's single constant reuseKey this
  // stays warm; the count exists so destroy() is reachable (see releaseShared) and
  // so a future multi-key/LRU policy can evict safely.
  private var refCounts: [String: Int] = [:]
  // Serializes all dictionary access. Android guards the pool with @Synchronized;
  // here concurrent acquire/release could otherwise corrupt the dict or create
  // duplicate WebViews for one key (fix #3).
  private let lock = NSLock()

  /// Get (creating if absent) the entry for `key`. Does NOT change the refcount —
  /// call `adopt` exactly once per host (idempotency via `adoptedPoolKey`).
  func acquireShared(key: String) -> PooledChartWebView {
    lock.lock()
    defer { lock.unlock() }
    if let existing = entries[key] { return existing }
    let created = PooledChartWebView(key: key)
    entries[key] = created
    return created
  }

  /// Register one live host reference to `key` (call once per host).
  func adopt(key: String) {
    lock.lock()
    defer { lock.unlock() }
    refCounts[key, default: 0] += 1
  }

  /// A host that previously adopted `key` is going away. Decrements the refcount.
  /// Kept warm by default (intentional single-instance cache); pass
  /// `destroyWhenIdle: true` to tear the WebView down when the last host leaves.
  func releaseShared(key: String, destroyWhenIdle: Bool = false) {
    lock.lock()
    let next = (refCounts[key] ?? 0) - 1
    var toDestroy: PooledChartWebView?
    if next <= 0 {
      refCounts[key] = nil
      if destroyWhenIdle {
        toDestroy = entries.removeValue(forKey: key)
      }
    } else {
      refCounts[key] = next
    }
    lock.unlock()
    // destroy() hops to main and isn't part of the dict invariant — call outside
    // the lock.
    toDestroy?.destroy()
  }
}
