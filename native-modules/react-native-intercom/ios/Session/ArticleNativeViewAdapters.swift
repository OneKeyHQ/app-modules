import SwiftUI

struct ArticleLoadingView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> LoadingViewController {
        let image = ICMAssets.articleLoadingImage()
        let text = TranslationManager.helpCenter.translation(forKey: "loading")
        return LoadingViewController(image: image, accessibilityText: text)
    }
    func updateUIViewController(_ uiViewController: LoadingViewController, context: Context) {}
}

struct ArticleErrorView: UIViewControllerRepresentable {
    let message: String
    let onRetry: () -> Void
    func makeCoordinator() -> Coordinator { Coordinator(onRetry: onRetry) }
    func makeUIViewController(context: Context) -> ErrorViewController {
        let reload = TranslationManager.helpCenter.translation(forKey: "reload")
        let subtitle = TranslationManager.helpCenter.translation(forKey: "spaces_error_description")
        let controller = ErrorViewController(title: message, subtitle: subtitle, showActionButton: true, actionButtonTitle: reload, themeProvider: DefaultThemeProvider.shared, icon: nil)
        controller.errorDelegate = context.coordinator
        return controller
    }
    func updateUIViewController(_ uiViewController: ErrorViewController, context: Context) {}
    @objc(_TtCV18IntercomSDKPrivateP33_7C1AF274F41411F84321DFC557DAE93B9ErrorView11Coordinator)
    final class Coordinator: NSObject, ICMErrorViewDelegate {
        let onRetry: () -> Void
        init(onRetry: @escaping () -> Void) { self.onRetry = onRetry; super.init() }
        override init() { fatalError("init() has not been implemented") }
        func didSelectErrorAction() { onRetry() }
    }
}
