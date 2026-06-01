import Foundation
import UIKit
import WebKit

/// A dumb WebView host + message pipe. It knows nothing about TradingView: it
/// loads a `source`, injects messages into the page, and surfaces messages
/// from the page. All protocol/data logic lives in the JS business layer.
///
/// Offline content is served from a virtual same-origin via a custom
/// `onekey-chart://` URL scheme handler backed by files bundled in the app.
///
/// The WebKit delegates / scheme handler require an `NSObject`, but the Nitro
/// spec base (`HybridChartWebviewSpec_base`) is a plain Swift class, so we
/// cannot conform `HybridChartWebview` to them directly. Instead a dedicated
/// `ChartWebViewProxy: NSObject` owns the WebKit conformances and forwards
/// back to the host through a weak reference.
class HybridChartWebview: HybridChartWebviewSpec {

  // MARK: - Constants

  /// Custom scheme used for the offline virtual same-origin.
  fileprivate static let customScheme = "onekey-chart"
  /// Virtual host used when serving offline content.
  fileprivate static let virtualHost = "chart"
  /// Name of the page -> native message handler.
  fileprivate static let messageHandlerName = "onekeyChart"

  // MARK: - State

  private var webView: WKWebView!
  private var proxy: ChartWebViewProxy!
  /// Becomes true once props have settled enough to perform the first load.
  /// Guards against loading repeatedly while individual props arrive.
  private var hasLoadedOnce = false

  // MARK: - HybridView

  var view: UIView = UIView()

  // MARK: - Props (source)

  var uri: String? {
    didSet { loadIfNeeded() }
  }

  var localBundle: String? {
    didSet { loadIfNeeded() }
  }

  var entry: String? {
    didSet { loadIfNeeded() }
  }

  var paramsJson: String? {
    didSet { loadIfNeeded() }
  }

  // MARK: - Props (singleton — accepted now, ignored in the MVP)

  // MVP ignores reuseKey/pooled/isActive: every view owns its own WebView and
  // there is no shared pool / reparenting. Stored only so the props bind.
  var reuseKey: String?
  var pooled: Bool?
  var isActive: Bool?

  // MARK: - Props (events)

  var onMessage: ((_ message: String) -> Void)?
  var onLoadEnd: (() -> Void)?
  var onError: ((_ message: String) -> Void)?

  // MARK: - Init

  override init() {
    super.init()
    setupWebView()
  }

  private func setupWebView() {
    let proxy = ChartWebViewProxy(host: self)
    self.proxy = proxy

    let config = WKWebViewConfiguration()

    // Enable JavaScript. DOM storage / localStorage are on by default in
    // WKWebView, so nothing extra is required for them.
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

    // Offline virtual same-origin: serve onekey-chart:// from the app bundle.
    config.setURLSchemeHandler(proxy, forURLScheme: HybridChartWebview.customScheme)

    // page -> native bridge.
    let userContent = WKUserContentController()
    userContent.add(proxy, name: HybridChartWebview.messageHandlerName)

    // Forward the page's outbound messages into our WKScriptMessageHandler at
    // document start. Three outbound conventions are supported so this stays a
    // dumb pipe regardless of how the page was built:
    //   A) `window.$onekey.$private.request(e)` — OneKey's chart/DApp native
    //      bridge (used when the page is told it runs on a native platform).
    //   B) `window.ReactNativeWebView.postMessage(s)` (react-native-webview style).
    //   C) `window.parent.postMessage(envelope, '*')` — in a top-level WebView
    //      `parent === window`, so it surfaces as a `message` event here. Only
    //      the page's own `$private` envelopes are forwarded; the messages WE
    //      inject (native -> page) are not `$private`, so they never echo back.
    let handlerName = HybridChartWebview.messageHandlerName
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

    let webView = WKWebView(frame: view.bounds, configuration: config)
    webView.navigationDelegate = proxy
    webView.uiDelegate = proxy
    webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    webView.scrollView.bounces = false
    if #available(iOS 16.4, *) {
      webView.isInspectable = true
    }
    self.webView = webView

    view.addSubview(webView)
    webView.frame = view.bounds
  }

  // MARK: - Event forwarding (called by the proxy)

  fileprivate func handleMessage(_ message: String) {
    onMessage?(message)
  }

  fileprivate func handleLoadEnd() {
    onLoadEnd?()
  }

  fileprivate func handleError(_ message: String) {
    onError?(message)
  }

  fileprivate var currentLocalBundle: String? {
    localBundle
  }

  // MARK: - Loading

  /// The URL string currently loaded, so redundant prop re-applies (from React
  /// re-renders) don't reload the page — which would restart the chart and feed
  /// back into onLoadEnd -> re-render. Only an actual URL change reloads.
  private var lastLoadedUrl: String?

  /// Loads the configured source. Driven by prop didSet; only loads once props
  /// are present, and only when the effective URL actually changes.
  private func loadIfNeeded() {
    guard let webView = webView else { return }
    guard let urlString = computeTargetUrl() else { return }
    guard urlString != lastLoadedUrl else { return }
    guard let url = URL(string: urlString) else {
      onError?("Invalid url: \(urlString)")
      return
    }
    lastLoadedUrl = urlString
    hasLoadedOnce = true
    webView.load(URLRequest(url: url))
  }

  /// Resolves the effective URL from the current props: `uri` (remote) wins,
  /// otherwise the offline bundle served via the `onekey-chart://` scheme.
  /// Returns nil when no source is set yet.
  private func computeTargetUrl() -> String? {
    if let uri = uri, !uri.isEmpty {
      return uri
    }
    if let localBundle = localBundle, !localBundle.isEmpty {
      let entryFile = (entry?.isEmpty == false) ? entry! : "index.html"
      let query = buildQueryString(fromParamsJson: paramsJson)
      var urlString = "\(HybridChartWebview.customScheme)://\(HybridChartWebview.virtualHost)/\(entryFile)"
      if !query.isEmpty {
        urlString += "?\(query)"
      }
      return urlString
    }
    return nil
  }

  /// Parses `paramsJson` (a JSON object string) into a URL-encoded query
  /// string. Non-string values are stringified. Returns "" on any problem.
  private func buildQueryString(fromParamsJson json: String?) -> String {
    guard let json = json, !json.isEmpty,
          let data = json.data(using: .utf8),
          let obj = try? JSONSerialization.jsonObject(with: data),
          let dict = obj as? [String: Any] else {
      return ""
    }

    var allowed = CharacterSet.urlQueryAllowed
    // RFC 3986 sub-delims that are valid in a query but ambiguous as
    // separators must be percent-encoded.
    allowed.remove(charactersIn: "&=+?#")

    var pairs: [String] = []
    for (key, value) in dict {
      let stringValue: String
      switch value {
      case let s as String:
        stringValue = s
      case let b as Bool:
        stringValue = b ? "true" : "false"
      case let n as NSNumber:
        stringValue = n.stringValue
      default:
        stringValue = "\(value)"
      }
      let encodedKey = key.addingPercentEncoding(withAllowedCharacters: allowed) ?? key
      let encodedValue = stringValue.addingPercentEncoding(withAllowedCharacters: allowed) ?? stringValue
      pairs.append("\(encodedKey)=\(encodedValue)")
    }
    // Stable order for deterministic URLs.
    return pairs.sorted().joined(separator: "&")
  }

  // MARK: - Methods

  private func runOnMain(_ block: @escaping () -> Void) {
    if Thread.isMainThread {
      block()
    } else {
      DispatchQueue.main.async(execute: block)
    }
  }

  /// JS -> page. `message` is a raw JSON string built by the JS business layer.
  ///
  /// Escaping strategy: rather than splicing the JSON object literal directly
  /// into JS source (which risks breaking out of context if the payload is
  /// ever malformed), we treat `message` as a *string*, JSON-encode that
  /// string to get a safely-escaped JS string literal, and have the page
  /// `JSON.parse` it back into an object before dispatching. This keeps the
  /// injection inert regardless of the payload's contents.
  func postMessage(message: String) throws {
    runOnMain { [weak self] in
      guard let self = self, let webView = self.webView else { return }
      // Produce a JS string literal whose runtime value equals `message`.
      let jsStringLiteral = self.jsStringLiteral(from: message)
      let js = "window.postMessage(JSON.parse(\(jsStringLiteral)), '*')"
      webView.evaluateJavaScript(js, completionHandler: nil)
    }
  }

  func reload() throws {
    runOnMain { [weak self] in
      self?.webView?.reload()
    }
  }

  /// Encodes an arbitrary Swift string into a safe JS string literal
  /// (including surrounding quotes). Uses JSONSerialization so all control
  /// characters, quotes and backslashes are escaped correctly.
  private func jsStringLiteral(from raw: String) -> String {
    if let data = try? JSONSerialization.data(withJSONObject: [raw], options: []),
       let arrStr = String(data: data, encoding: .utf8) {
      // arrStr looks like ["...escaped..."]; strip the array brackets.
      var s = arrStr
      if s.hasPrefix("[") { s.removeFirst() }
      if s.hasSuffix("]") { s.removeLast() }
      return s.trimmingCharacters(in: .whitespacesAndNewlines)
    }
    // Fallback: minimal manual escaping.
    let escaped = raw
      .replacingOccurrences(of: "\\", with: "\\\\")
      .replacingOccurrences(of: "\"", with: "\\\"")
      .replacingOccurrences(of: "\n", with: "\\n")
      .replacingOccurrences(of: "\r", with: "\\r")
    return "\"\(escaped)\""
  }
}

// MARK: - ChartWebViewProxy (NSObject host for the WebKit conformances)

/// Owns every WebKit delegate / handler conformance that requires `NSObject`,
/// forwarding back to the host. Held strongly by the host; references the host
/// weakly to avoid a retain cycle.
private final class ChartWebViewProxy: NSObject {
  private weak var host: HybridChartWebview?

  init(host: HybridChartWebview) {
    self.host = host
    super.init()
  }
}

// MARK: - WKScriptMessageHandler (page -> native)

extension ChartWebViewProxy: WKScriptMessageHandler {
  func userContentController(
    _ userContentController: WKUserContentController,
    didReceive message: WKScriptMessage
  ) {
    guard message.name == HybridChartWebview.messageHandlerName else { return }
    // Surface the raw payload as a JSON string without parsing it; the JS
    // business layer is responsible for interpreting it.
    if let body = message.body as? String {
      host?.handleMessage(body)
    } else {
      // Non-string payloads (object/number) — re-serialize to a JSON string.
      if let data = try? JSONSerialization.data(withJSONObject: message.body, options: []),
         let str = String(data: data, encoding: .utf8) {
        host?.handleMessage(str)
      } else {
        host?.handleMessage("\(message.body)")
      }
    }
  }
}

// MARK: - WKNavigationDelegate (load/error events)

extension ChartWebViewProxy: WKNavigationDelegate {
  func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
    host?.handleLoadEnd()
  }

  func webView(
    _ webView: WKWebView,
    didFail navigation: WKNavigation!,
    withError error: Error
  ) {
    host?.handleError(error.localizedDescription)
  }

  func webView(
    _ webView: WKWebView,
    didFailProvisionalNavigation navigation: WKNavigation!,
    withError error: Error
  ) {
    host?.handleError(error.localizedDescription)
  }
}

// MARK: - WKUIDelegate

extension ChartWebViewProxy: WKUIDelegate {
  // Default behavior is sufficient for the MVP. Set as delegate so future
  // JS dialogs / target=_blank handling can be wired here.
}

// MARK: - WKURLSchemeHandler (offline virtual same-origin)

extension ChartWebViewProxy: WKURLSchemeHandler {
  func webView(_ webView: WKWebView, start urlSchemeTask: WKURLSchemeTask) {
    guard let url = urlSchemeTask.request.url else {
      urlSchemeTask.didFailWithError(
        NSError(domain: "ChartWebview", code: -1, userInfo: [NSLocalizedDescriptionKey: "Missing url"])
      )
      return
    }

    guard let localBundle = host?.currentLocalBundle, !localBundle.isEmpty else {
      respondNotFound(url: url, task: urlSchemeTask)
      return
    }

    // Resolve the requested path against the bundled `localBundle` directory.
    // The path component (sans query/fragment) maps 1:1 to a file in the
    // bundle subdirectory. e.g. onekey-chart://chart/assets/app.js ->
    // <bundle>/<localBundle>/assets/app.js
    var relativePath = url.path
    if relativePath.hasPrefix("/") {
      relativePath.removeFirst()
    }
    if relativePath.isEmpty {
      relativePath = "index.html"
    }

    guard let fileURL = resolveBundleFileURL(localBundle: localBundle, relativePath: relativePath) else {
      respondNotFound(url: url, task: urlSchemeTask)
      return
    }

    guard let data = try? Data(contentsOf: fileURL) else {
      respondNotFound(url: url, task: urlSchemeTask)
      return
    }

    let mimeType = mimeTypeForPath(fileURL.pathExtension)
    let headers: [String: String] = [
      "Content-Type": mimeType,
      "Content-Length": "\(data.count)",
      "Access-Control-Allow-Origin": "*",
      "Cache-Control": "no-cache",
    ]
    guard let response = HTTPURLResponse(
      url: url,
      statusCode: 200,
      httpVersion: "HTTP/1.1",
      headerFields: headers
    ) else {
      respondNotFound(url: url, task: urlSchemeTask)
      return
    }

    urlSchemeTask.didReceive(response)
    urlSchemeTask.didReceive(data)
    urlSchemeTask.didFinish()
  }

  func webView(_ webView: WKWebView, stop urlSchemeTask: WKURLSchemeTask) {
    // No long-running work to cancel; reads are synchronous and one-shot.
  }

  /// Maps `<localBundle>/<relativePath>` to a concrete file URL inside the app
  /// bundle. Guards against path traversal escaping the bundle directory.
  private func resolveBundleFileURL(localBundle: String, relativePath: String) -> URL? {
    let resourceURL = Bundle.main.resourceURL ?? Bundle.main.bundleURL
    let baseDir = resourceURL.appendingPathComponent(localBundle, isDirectory: true)
    let candidate = baseDir.appendingPathComponent(relativePath).standardizedFileURL

    // Ensure the resolved path stays within the bundle directory.
    let basePath = baseDir.standardizedFileURL.path
    guard candidate.path == basePath || candidate.path.hasPrefix(basePath + "/") else {
      return nil
    }

    let fm = FileManager.default
    var isDir: ObjCBool = false
    guard fm.fileExists(atPath: candidate.path, isDirectory: &isDir), !isDir.boolValue else {
      return nil
    }
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
