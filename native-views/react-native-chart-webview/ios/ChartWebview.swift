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

  // MARK: - HybridView

  private let container = ChartContainerView()
  var view: UIView { container }

  /// The WebView backing this host (a shared pool entry, or a private one).
  private var backing: PooledChartWebView?
  private var attached = false

  override init() {
    super.init()
    container.onWindowChange = { [weak self] attached in
      self?.attached = attached
      self?.reconcile()
    }
  }

  // MARK: - Props (source)

  var uri: String? { didSet { applySourceIfOwner() } }
  var localBundle: String? { didSet { applySourceIfOwner() } }
  var entry: String? { didSet { applySourceIfOwner() } }
  var paramsJson: String? { didSet { applySourceIfOwner() } }

  // MARK: - Props (singleton)

  // `pooled` + non-empty `reuseKey` => the backing WebView is shared (keyed by
  // reuseKey) across hosts; otherwise the host owns a private WebView.
  var reuseKey: String? { didSet { reconcile() } }
  var pooled: Bool? { didSet { reconcile() } }
  // `active` (JS useIsFocused) decides which host, among those sharing a key,
  // owns the single WebView. nil is treated as active (single-host case).
  var active: Bool? { didSet { reconcile() } }

  // MARK: - Props (events)

  var onMessage: ((_ message: String) -> Void)?
  var onLoadEnd: (() -> Void)?
  var onError: ((_ message: String) -> Void)?

  // Called by the backing PooledChartWebView while this host is the owner.
  func handleMessage(_ message: String) { onMessage?(message) }
  func handleLoadEnd() { onLoadEnd?() }
  func handleError(_ message: String) { onError?(message) }

  // MARK: - Methods

  func postMessage(message: String) throws { backing?.postMessage(message) }
  func reload() throws { backing?.reload() }

  // MARK: - Ownership reconciliation

  private func isPooled() -> Bool {
    if let key = reuseKey, !key.isEmpty, pooled == true { return true }
    return false
  }

  private func effectiveKey() -> String {
    isPooled() ? reuseKey! : "private:\(instanceId)"
  }

  private func wantsOwnership() -> Bool { attached && (active != false) }

  private func reconcile() {
    if wantsOwnership() { claim() } else { release() }
  }

  private func claim() {
    let pooled: PooledChartWebView
    if isPooled() {
      pooled = ChartWebviewPool.shared.acquireShared(key: effectiveKey())
    } else {
      pooled = backing ?? PooledChartWebView(key: effectiveKey())
    }
    backing = pooled
    pooled.owner = self
    pooled.attach(to: container)
    pooled.setSource(uri: uri, localBundle: localBundle, entry: entry, paramsJson: paramsJson)
  }

  private func release() {
    guard let pooled = backing else { return }
    if pooled.owner === self {
      pooled.detachFromParent()
      pooled.owner = nil
    }
    // Pooled entries stay warm in the pool; a private backing is kept on this
    // host so a later re-claim reuses it without a reload.
  }

  private func applySourceIfOwner() {
    guard let pooled = backing, pooled.owner === self else { return }
    pooled.setSource(uri: uri, localBundle: localBundle, entry: entry, paramsJson: paramsJson)
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

  private var webView: WKWebView!
  private var proxy: ChartWebViewProxy!
  private var lastLoadedUrl: String?

  /// Read by the scheme handler to resolve offline files.
  fileprivate var currentLocalBundle: String?

  init(key: String) {
    self.key = key
    PooledChartWebView.liveCount += 1
    NSLog("[ChartWebviewPool] WebView CREATED key=\(key) liveCount=\(PooledChartWebView.liveCount)")
    setupWebView()
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

    // See the Android host for the rationale; identical dumb-pipe bridge.
    let handlerName = ChartWebviewConst.messageHandlerName
    let bridgeScript = """
    (function () {
      if (window.__onekeyChartBridge) return; window.__onekeyChartBridge = true;
      var fwd = function (m) {
        window.webkit.messageHandlers.\(handlerName).postMessage(typeof m === 'string' ? m : JSON.stringify(m));
      };
      window.$onekey = window.$onekey || {};
      window.$onekey.$private = window.$onekey.$private || {};
      window.$onekey.$private.request = function (m) { fwd(m); };
      window.ReactNativeWebView = { postMessage: function (s) { fwd(String(s)); } };
      window.addEventListener('message', function (e) {
        try {
          var d = e && e.data;
          if (d && d.scope === '$private') fwd(d);
        } catch (err) {}
      });
    })();
    """
    let userScript = WKUserScript(
      source: bridgeScript,
      injectionTime: .atDocumentStart,
      forMainFrameOnly: false
    )
    userContent.addUserScript(userScript)
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
    runOnMain { [weak self] in
      guard let self = self, let webView = self.webView else { return }
      if webView.superview !== container {
        webView.removeFromSuperview()
        webView.frame = container.bounds
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        container.addSubview(webView)
      }
    }
  }

  /// Remove the WebView from its current parent (keeps it alive, warm).
  func detachFromParent() {
    runOnMain { [weak self] in self?.webView?.removeFromSuperview() }
  }

  // MARK: - Loading

  /// Apply the source props and (re)load only when the effective URL changes —
  /// so reparenting / redundant prop re-applies never reload (which would lose
  /// chart state, the whole point of pooling).
  func setSource(uri: String?, localBundle: String?, entry: String?, paramsJson: String?) {
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

  func acquireShared(key: String) -> PooledChartWebView {
    if let existing = entries[key] { return existing }
    let created = PooledChartWebView(key: key)
    entries[key] = created
    return created
  }
}
