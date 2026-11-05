import SwiftUI
import WebKit

struct WebView: UIViewRepresentable {
    let request: URLRequest
    var navigationDelegate: (any WKNavigationDelegate)?
    @Binding var contentHeight: CGFloat
    @Binding var anchorToScroll: String?
    let onAnchorNotFound: ((String) -> Void)?

    func makeCoordinator() -> Coordinator {
        Coordinator(contentHeight: $contentHeight, anchorToScroll: $anchorToScroll, onAnchorNotFound: onAnchorNotFound)
    }

    func makeUIView(context: Context) -> WKWebView {
        let webView = WKWebView(frame: .zero, configuration: WKWebViewConfiguration())
        webView.scrollView.isScrollEnabled = false
        webView.scrollView.bounces = false
        if let navigationDelegate { webView.navigationDelegate = navigationDelegate }
        context.coordinator.webView = webView
        context.coordinator.contentSizeObservation = webView.scrollView.observe(\.contentSize, options: [.initial, .new]) { [weak coordinator = context.coordinator] scrollView, _ in
            DispatchQueue.main.async { [weak coordinator] in
                guard let coordinator, let webView = coordinator.webView else { return }
                webView.evaluateJavaScript("document.body.scrollHeight") { [weak coordinator] result, _ in
                    let height: CGFloat
                    if let value = result as? CGFloat, value > 0 { height = value }
                    else if let value = result as? Double, value > 0 { height = CGFloat(value) }
                    else if scrollView.contentSize.height > 0 { height = scrollView.contentSize.height }
                    else { return }
                    guard let coordinator else { return }
                    coordinator.contentHeight = height
                }
            }
        }
        webView.load(request)
        return webView
    }

    func updateUIView(_ uiView: WKWebView, context: Context) {
        context.coordinator.anchorToScroll = $anchorToScroll
        context.coordinator.onAnchorNotFound = onAnchorNotFound
        if let anchor = context.coordinator.anchorToScroll.wrappedValue,
           let webView = context.coordinator.webView {
            context.coordinator.scrollToAnchor(anchor, in: webView)
        }
    }

    @objc(_TtCV18IntercomSDKPrivate7WebView11Coordinator)
    final class Coordinator: NSObject {
        @Binding var contentHeight: CGFloat
        var anchorToScroll: Binding<String?>
        var onAnchorNotFound: ((String) -> Void)?
        weak var webView: WKWebView?
        var contentSizeObservation: NSKeyValueObservation?

        init(contentHeight: Binding<CGFloat>, anchorToScroll: Binding<String?>, onAnchorNotFound: ((String) -> Void)?) {
            _contentHeight = contentHeight
            self.anchorToScroll = anchorToScroll
            self.onAnchorNotFound = onAnchorNotFound
            super.init()
        }
        override init() { fatalError("init() has not been implemented") }
        deinit { contentSizeObservation?.invalidate() }

        func scrollToAnchor(_ anchor: String, in webView: WKWebView) {
            let script = """
            const el = document.getElementById(anchor) || document.querySelector('[name="' + anchor + '"]');
            return el ? el.getBoundingClientRect().top + window.pageYOffset : null;
            """
            webView.icm_callAsyncJavaScript(script, arguments: ["anchor": anchor]) { [weak self] result, _ in
                if let value = result as? CGFloat {
                    self?.scrollParentScrollView(to: value, from: webView)
                } else if let value = result as? Double {
                    self?.scrollParentScrollView(to: CGFloat(value), from: webView)
                } else {
                    let callback = self?.onAnchorNotFound
                    callback?(anchor)
                }
                self?.clearAnchor()
            }
        }

        func scrollParentScrollView(to offset: CGFloat, from webView: WKWebView) {
            var current = webView.superview
            while let view = current {
                if let scrollView = view as? UIScrollView, scrollView !== webView.scrollView {
                    scrollView.setContentOffset(CGPoint(x: 0, y: offset), animated: true)
                    return
                }
                current = view.superview
            }
        }
        func clearAnchor() {
            DispatchQueue.main.async { [weak self] in
                guard let binding = self?.anchorToScroll else { return }
                binding.wrappedValue = nil
            }
        }
    }
}
