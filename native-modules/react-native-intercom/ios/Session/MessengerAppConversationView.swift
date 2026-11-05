import UIKit
import WebKit

@objc(_TtC18IntercomSDKPrivate28MessengerAppConversationView)
class MessengerAppConversationView: UIView, WKNavigationDelegate, WKScriptMessageHandler, ICMMessengerSheetViewControllerDelegate {
    private let content: ITBMessengerCardFallbackBlock
    private var webView: WKWebView?
    private var heightConstraint: NSLayoutConstraint?
    private let textInputPayload = MessengerAppTextInputPayload()
    private weak var blockDelegate: (any ConversationCellBlockDelegate)?
    private let dismissAppSheet: (() -> Void)?

    init?(content: ITBBlock, delegate: (any ConversationCellBlockDelegate)?, dismissAppSheet: (() -> Void)?) {
        guard let content = content as? ITBMessengerCardFallbackBlock, let url = content.fallbackURL else { return nil }
        _ = url
        self.content = content
        self.blockDelegate = delegate
        self.dismissAppSheet = dismissAppSheet
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        setupWebView(insets: content.cardInsets)
        load()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    deinit {
        if let webView = webView {
            webView.configuration.userContentController.removeAllScriptMessageHandlers()
            webView.navigationDelegate = nil
            webView.removeFromSuperview()
            self.webView = nil
        }
        heightConstraint = nil
    }

    private func load() {
        guard let url = content.fallbackURL else { return }
        let appended = url.appended(by: traitCollection.userInterfaceStyle)
        let request = URLRequest(url: appended, cachePolicy: .useProtocolCachePolicy, timeoutInterval: 60)
        webView?.load(request)
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        guard let superview = superview, superview.frame.width > 0 else { return }
        let width = superview.frame.width
        content.estimatedSize = CGSize(width: width, height: heightConstraint?.constant ?? 0)
        blockDelegate?.invalidateLayoutFor(block: content)
    }
    private func handleHeightChanged(_ height: CGFloat, withInsets insets: UIEdgeInsets) {
        let combined = (height + insets.top) + insets.bottom
        heightConstraint?.constant = combined
        let width = content.estimatedSize.width
        content.estimatedSize = CGSize(width: width, height: combined)
        blockDelegate?.reloadCell(with: .block(content))
        setNeedsLayout()
    }
    private func handleOpenSheet(withURL url: URL, params: [AnyHashable: Any]) {
        if let delegate = blockDelegate {
            let appended = url.appended(by: traitCollection.userInterfaceStyle)
            delegate.openAppSheet(with: appended, params: params, delegate: self)
        }
    }
    private func handleOpenURL(_ url: URL) {
        if let delegate = blockDelegate { delegate.open(url: url.appended(by: traitCollection.userInterfaceStyle)) }
    }
    private func handleTextInput(_ data: [String: Any]) {
        textInputPayload.title = data["label"] as? String
        textInputPayload.placeholder = data["placeholder"] as? String
        textInputPayload.currentText = data["currentValue"] as? String
        textInputPayload.inputId = data["id"] as? String
        textInputPayload.messengerAppId = "\(data["messengerAppId"] ?? "")"
        textInputPayload.completionBlock = { [weak self] alert in
            guard let text = alert.textFields?.first?.text else { return }
            guard let self = self else { return }
            self.sendText(text, to: self.webView, with: self.textInputPayload.inputId)
        }
        blockDelegate?.presentMessengerAppInputAlert(with: textInputPayload)
    }
    private func sendText(_ text: String, to webView: WKWebView?, with inputId: String?) {
        guard let webView = webView else { return }
        let arguments = MessengerAppCardJSBuilder.updateTextArguments(text: text, inputId: inputId)
        Task { @MainActor in
            webView.icm_callAsyncJavaScript("window.fallbackApp.bridgeEvent(eventName, payload);", arguments: arguments) { _, error in
                if let error = error { print("Error updating text field: \(error)") }
            }
        }
    }

    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) { processScriptMessage(message) }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {}
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        let host = content.fallbackURL?.host
        let destinationHost = navigationAction.request.url?.host
        if host == destinationHost { decisionHandler(.allow); return }
        if let url = navigationAction.request.url {
            let appended = url.appended(by: traitCollection.userInterfaceStyle)
            handleOpenURL(appended)
        }
        decisionHandler(.cancel)
    }
    func messengerSheetDidClose(_ sheet: ICMMessengerSheetViewController?) {
        let sheet = sheet!
        if let url = sheet.sheetURL { blockDelegate?.logSheetClosedMetric(url: url) }
        dismissAppSheet?()
    }
    func messengerSheetDidCloseAndTriggerReload(_ sheet: ICMMessengerSheetViewController?) {
        let sheet = sheet!
        if let url = sheet.sheetURL { blockDelegate?.logSheetClosedMetric(url: url) }
        load()
        dismissAppSheet?()
    }

    // Concrete original Conversation specialization; full private protocol ABI remains unrecovered.
    private func setupWebView(insets: UIEdgeInsets) {
        let controller = WKUserContentController()
        injectJSScripts(into: controller)
        let configuration = WKWebViewConfiguration()
        configuration.userContentController = controller
        configuration.websiteDataStore = .nonPersistent()
        let view = WKWebView(frame: .zero, configuration: configuration)
        view.isOpaque = false
        view.translatesAutoresizingMaskIntoConstraints = false
        view.navigationDelegate = self
        view.scrollView.isScrollEnabled = false
        view.scrollView.bounces = false
        view.allowsBackForwardNavigationGestures = false
        view.allowsLinkPreview = false
        view.clipsToBounds = true
        view.backgroundColor = DefaultThemeProvider.shared.theme.base
        view.accessibilityIdentifier = "intercom card webview fallback"
        view.isAccessibilityElement = true
        view.accessibilityLabel = "Messenger App Content"
        addSubview(view)
        NSLayoutConstraint.activate([
            view.leadingAnchor.constraint(equalTo: leadingAnchor, constant: insets.left),
            view.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -insets.right),
            view.topAnchor.constraint(equalTo: topAnchor, constant: insets.top),
            view.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -insets.bottom),
        ])
        let height = heightAnchor.constraint(equalToConstant: 50)
        heightConstraint = height
        height.priority = UILayoutPriority(rawValue: 999)
        height.isActive = true
        webView = view
    }
    private func injectJSScripts(into controller: WKUserContentController) {
        controller.addUserScript(WKUserScript(source: MessengerAppCardJSBuilder.viewport, injectionTime: .atDocumentEnd, forMainFrameOnly: true))
        controller.addUserScript(WKUserScript(source: MessengerAppCardJSBuilder.selection, injectionTime: .atDocumentEnd, forMainFrameOnly: true))
        controller.addUserScript(WKUserScript(source: MessengerAppCardJSBuilder.touchCallout, injectionTime: .atDocumentEnd, forMainFrameOnly: true))
        controller.addUserScript(WKUserScript(source: MessengerAppCardJSBuilder.bridgePrefix + "handleAction" + MessengerAppCardJSBuilder.bridgeSuffix, injectionTime: .atDocumentStart, forMainFrameOnly: true))
        controller.add(self, name: "handleAction")
    }
    private func parseScriptMessage(_ message: WKScriptMessage) -> [String: Any]? {
        if let dictionary = message.body as? [String: Any] { return dictionary }
        guard let string = message.body as? String, let data = string.data(using: .utf8, allowLossyConversion: false) else { return nil }
        do { return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] }
        catch { print("Error parsing Messenger Card Response JSON: \(error)"); return nil }
    }
    private func processScriptMessage(_ message: WKScriptMessage) {
        guard let envelope = parseScriptMessage(message), let payload = envelope["payload"] as? [String: Any] else { return }
        guard message.name == "handleAction", let type = envelope["type"] as? String else { return }
        switch type {
        case "INPUT_TEXT": handleTextInput(payload)
        case "OPEN_SHEET":
            guard let string = payload["url"] as? String, let url = URL(string: string), let params = payload["params"] as? [String: Any] else { return }
            if MessengerAppConstants.isSecureSheetURL(url) { handleOpenSheet(withURL: url, params: params) }
            else { ICMLogger.log("[Intercom] Blocked OPEN_SHEET action: URL must be HTTPS with a valid host, got scheme='\(url.scheme ?? "nil")' host='\(url.host ?? "nil")'.", type: .error) }
        case "HEIGHT_CHANGE_EVENT":
            if let height = payload["height"] as? CGFloat { handleHeightChanged(height, withInsets: .zero) }
        case "OPEN_URL":
            if let string = payload["url"] as? String, let url = URL(string: string) { handleOpenURL(url) }
        case "METRIC_EVENT": blockDelegate?.logMessengerAppMetricEvent(payload)
        default: return
        }
    }
}
