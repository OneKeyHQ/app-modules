import WebKit

@objc(_TtC18IntercomSDKPrivate33ScrollDetectingNavigationDelegate)
final class ScrollDetectingNavigationDelegate: NSObject, WKNavigationDelegate {
    var onDidStart: (() -> Void)?
    var onContentReady: ((String?) -> Void)?
    var onDidFinish: ((String?) -> Void)?
    var onDidFail: ((Error) -> Void)?
    var onAnchorLinkTapped: ((String) -> Void)?
    let revealTimeout: Double
    var httpErrorCode: Int?
    var hasRevealedContent = false
    var contentSizeObservation: NSKeyValueObservation?
    var revealTimeoutWorkItem: DispatchWorkItem?

    init(revealTimeout: Double?) {
        self.revealTimeout = revealTimeout ?? 5
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }
    func resetReveal() {
        hasRevealedContent = false
        contentSizeObservation = nil
        revealTimeoutWorkItem?.cancel()
    }
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        httpErrorCode = nil
        resetReveal()
        onDidStart?()
    }
    func webView(_ webView: WKWebView, didCommit navigation: WKNavigation!) {
        contentSizeObservation = webView.scrollView.observe(\.contentSize, options: [.new]) { [weak webView, weak self] _, change in
            guard let webView, let value = change.newValue, value.height > 0 else { return }
            self?.revealContent(in: webView)
        }
        let workItem = DispatchWorkItem { [weak self, weak webView] in
            guard let self, let webView else { return }
            if webView.scrollView.contentSize.height > 0 { self.revealContent(in: webView) }
            else {
                self.resetReveal()
                self.onDidFail?(NSError(domain: NSURLErrorDomain, code: -1001, userInfo: nil))
            }
        }
        revealTimeoutWorkItem = workItem
        DispatchQueue.main.asyncAfter(deadline: .now() + revealTimeout, execute: workItem)
    }
    func revealContent(in webView: WKWebView) {
        if hasRevealedContent { return }
        hasRevealedContent = true
        contentSizeObservation = nil
        revealTimeoutWorkItem?.cancel()
        webView.evaluateJavaScript("document.querySelector('h1')?.innerText") { [weak self] result, _ in
            let title = (result as? String)?.trimmingCharacters(in: .whitespacesAndNewlines)
            let callback = self?.onContentReady
            callback?(title?.isEmpty == true ? nil : title)
        }
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        revealContent(in: webView)
        webView.evaluateJavaScript("window.alexandriaArticleContentId") { [weak self] result, _ in
            let callback = self?.onDidFinish
            callback?(result as? String)
        }
    }
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        resetReveal()
        onDidFail?(error)
    }
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        if (error as NSError).code == -999 { return }
        onDidFail?(error)
    }
    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        if !navigationResponse.isForMainFrame { decisionHandler(.allow); return }
        if let response = navigationResponse.response as? HTTPURLResponse, response.statusCode == 200 {
            decisionHandler(.allow)
            return
        }
        if let response = navigationResponse.response as? HTTPURLResponse { httpErrorCode = response.statusCode }
        decisionHandler(.cancel)
        let code = httpErrorCode ?? 500
        let key = code == 404 || code == 401 ? "page_not_found" : "something_went_wrong_try_again"
        let message = TranslationManager.helpCenter.translation(forKey: key)
        let error = NSError(domain: "HTTPError", code: code, userInfo: [NSLocalizedDescriptionKey: message])
        onDidFail?(error)
    }
    func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        if navigationAction.navigationType != .linkActivated { decisionHandler(.allow); return }
        guard let target = navigationAction.request.url else { decisionHandler(.cancel); return }
        if let current = webView.url, target.host == current.host, target.path == current.path, let fragment = target.fragment {
            decisionHandler(.cancel)
            onAnchorLinkTapped?(fragment)
            return
        }
        decisionHandler(.cancel)
        LinkOpener.shared.openLink(target)
    }
}
