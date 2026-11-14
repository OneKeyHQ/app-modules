import UIKit
import WebKit

@objc(_TtC18IntercomSDKPrivate26MessengerAppHomescreenView)
class MessengerAppHomescreenView: UIView, WKScriptMessageHandler, WKNavigationDelegate, ICMMessengerSheetViewControllerDelegate {
    var webView: WKWebView?
    private var heightConstraint: NSLayoutConstraint?
    private var textInputPayload = MessengerAppTextInputPayload()
    let fallbackUrl: URL
    private let helpCenterMetricService: any HelpCenterMetricServiceProtocol
    private let metricHandler: (any ICMMetricProtocol)?
    private let themeProvider: any ThemeProvider
    private let navigate: ((HomescreenDestination) -> Void)?
    private let presentAlert: ((UIAlertController) -> Void)?
    private let dismissMessengerSheet: (() -> Void)?

    init(for url: URL, presentAlertAction: ((UIAlertController) -> Void)?, dismissMessengerSheetAction: (() -> Void)?, navigateAction: ((HomescreenDestination) -> Void)?, helpCenterMetricService: any HelpCenterMetricServiceProtocol, metricHandler: (any ICMMetricProtocol)?, themeProvider: any ThemeProvider) {
        fallbackUrl = url
        self.helpCenterMetricService = helpCenterMetricService
        self.metricHandler = metricHandler
        self.themeProvider = themeProvider
        navigate = navigateAction
        presentAlert = presentAlertAction
        dismissMessengerSheet = dismissMessengerSheetAction
        super.init(frame: .zero)
        translatesAutoresizingMaskIntoConstraints = false
        setupWebView()
        load()
    }
    required init?(coder: NSCoder) { fatalError("init(coder:) has not been implemented") }
    @available(*, unavailable)
    override init(frame: CGRect) { fatalError("init(frame:) has not been implemented") }
    deinit {
        webView?.configuration.userContentController.removeAllScriptMessageHandlers()
        webView?.navigationDelegate = nil
        webView?.removeFromSuperview()
        webView = nil
        heightConstraint = nil
    }
    func load() {
        let url = fallbackUrl.appended(by: traitCollection.userInterfaceStyle)
        let request = URLRequest(url: url, cachePolicy: .useProtocolCachePolicy, timeoutInterval: 60)
        webView?.load(request)
    }
    func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) { processScriptMessage(message) }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {}
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        let host = fallbackUrl.host
        let destinationHost = navigationAction.request.url?.host
        if host == destinationHost { decisionHandler(.allow); return }
        if let url = navigationAction.request.url { navigate?(.link(url)) }
        decisionHandler(.cancel)
    }
    func messengerSheetDidClose(_ sheet: ICMMessengerSheetViewController?) {
        addSheetClosedMetric(sheet!.sheetURL)
        dismissMessengerSheet?()
    }
    func messengerSheetDidCloseAndTriggerReload(_ sheet: ICMMessengerSheetViewController?) {
        addSheetClosedMetric(sheet!.sheetURL)
        load()
        dismissMessengerSheet?()
    }
}

private extension MessengerAppHomescreenView {
    func handleTextInput(_ payload: [String: Any]) {
        textInputPayload.title = payload["label"] as? String
        textInputPayload.placeholder = payload["placeholder"] as? String
        textInputPayload.currentText = payload["currentValue"] as? String
        textInputPayload.inputId = payload["id"] as? String
        textInputPayload.messengerAppId = "\(payload["messengerAppId"] ?? "")"
        if textInputPayload.messengerAppId == "27" {
            navigate?(.helpCenter)
            HelpCenterMetricService.addOpenedFromLegacySearchAppEvent()
            return
        }
        textInputPayload.completionBlock = { [weak self] alert in
            guard let text = alert.textFields?.first?.text else { return }
            guard let owner = self else { return }
            let webView = self?.webView
            let inputId = self?.textInputPayload.inputId
            owner.sendText(text, to: webView, with: inputId)
        }
        let alert = UIAlertController(title: textInputPayload.title, message: "", preferredStyle: .alert)
        alert.addTextField { [weak self] field in
            field.placeholder = self?.textInputPayload.placeholder
            field.text = self?.textInputPayload.currentText
        }
        let ok = TranslationManager.messenger.translation(forKey: "ok")
        alert.addAction(UIAlertAction(title: ok, style: .default) { [weak self, alert] _ in
            let completion = self?.textInputPayload.completionBlock
            completion?(alert)
        })
        let cancel = TranslationManager.messenger.translation(forKey: "cancel")
        alert.addAction(UIAlertAction(title: cancel, style: .cancel, handler: nil))
        presentAlert?(alert)
    }
    func sendText(_ text: String, to webView: WKWebView?, with inputId: String?) {
        guard let webView else { return }
        let arguments = MessengerAppCardJSBuilder.updateTextArguments(text: text, inputId: inputId)
        Task { @MainActor in
            webView.icm_callAsyncJavaScript("window.fallbackApp.bridgeEvent(eventName, payload);", arguments: arguments) { _, error in
                if let error { print("Error updating text field: \(error)") }
            }
        }
    }
    func setupWebView() {
        let controller = WKUserContentController()
        controller.addUserScript(WKUserScript(source: MessengerAppCardJSBuilder.viewport, injectionTime: .atDocumentEnd, forMainFrameOnly: true))
        controller.addUserScript(WKUserScript(source: MessengerAppCardJSBuilder.selection, injectionTime: .atDocumentEnd, forMainFrameOnly: true))
        controller.addUserScript(WKUserScript(source: MessengerAppCardJSBuilder.touchCallout, injectionTime: .atDocumentEnd, forMainFrameOnly: true))
        controller.addUserScript(WKUserScript(source: MessengerAppCardJSBuilder.bridgePrefix + "handleAction" + MessengerAppCardJSBuilder.bridgeSuffix, injectionTime: .atDocumentStart, forMainFrameOnly: true))
        controller.add(self, name: "handleAction")
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
            view.leadingAnchor.constraint(equalTo: leadingAnchor), view.trailingAnchor.constraint(equalTo: trailingAnchor),
            view.topAnchor.constraint(equalTo: topAnchor), view.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
        let height = heightAnchor.constraint(equalToConstant: 50)
        heightConstraint = height
        height.priority = .init(999)
        height.isActive = true
        webView = view
    }
    func parseScriptMessage(_ message: WKScriptMessage) -> [String: Any]? {
        if let dictionary = message.body as? [String: Any] { return dictionary }
        guard let string = message.body as? String, let data = string.data(using: .utf8, allowLossyConversion: false) else { return nil }
        do { return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] }
        catch { print("Error parsing Messenger Card Response JSON: \(error)"); return nil }
    }
    func processScriptMessage(_ message: WKScriptMessage) {
        guard let envelope = parseScriptMessage(message), let payload = envelope["payload"] as? [String: Any] else { return }
        guard message.name == "handleAction", let type = envelope["type"] as? String else { return }
        switch type {
        case "INPUT_TEXT": handleTextInput(payload)
        case "OPEN_SHEET":
            guard let string = payload["url"] as? String, let url = URL(string: string), let params = payload["params"] as? [String: Any] else { return }
            if MessengerAppConstants.isSecureSheetURL(url) { handleOpenSheet(url, params: params) }
            else { ICMLogger.log("[Intercom] Blocked OPEN_SHEET action: URL must be HTTPS with a valid host, got scheme='\(url.scheme ?? "nil")' host='\(url.host ?? "nil")'.", type: .error) }
        case "HEIGHT_CHANGE_EVENT":
            if let height = payload["height"] as? CGFloat {
                heightConstraint?.constant = height
                setNeedsLayout()
            }
        case "OPEN_URL":
            if let string = payload["url"] as? String, let url = URL(string: string) { navigate?(.link(url)) }
        case "METRIC_EVENT": handleMetricEvent(payload)
        default: return
        }
    }
    func handleOpenSheet(_ url: URL, params: [AnyHashable: Any]) {
        if let article = ICMUtils.extractArticleId(fromSheetParams: params) {
            navigate?(.article(article))
            HelpCenterMetricService.addOpenedFromMessengerHomeAppEvent()
        } else {
            navigate?(.appSheet(url, params: params, delegate: self))
            addSheetViewedMetric(url)
        }
    }
    func addSheetViewedMetric(_ url: URL?) {
        guard let url else { return }
        let event = ICMMetricEvent(action: "viewed", object: "messenger_sheet", place: "messenger", context: "home_screen", additionalInfo: ["url": url.absoluteString])
        metricHandler?.addEvent(event)
    }
    func addSheetClosedMetric(_ url: URL?) {
        guard let url else { return }
        let event = ICMMetricEvent(action: "closed", object: "messenger_sheet", place: "messenger", context: "home_screen", additionalInfo: ["url": url.absoluteString])
        metricHandler?.addEvent(event)
    }
    func handleMetricEvent(_ payload: [String: Any]) {
        guard let action = payload["action"] as? String, let object = payload["object"] as? String else { return }
        let place = payload["place"] as? String
        let metadata = payload["metadata"] as? [AnyHashable: Any]
        let event = ICMMetricEvent(action: action, object: object, place: place, context: "home_screen", additionalInfo: metadata)
        metricHandler?.addEvent(event)
    }
}
