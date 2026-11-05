import Foundation

@_objcRuntimeName(_TtC18IntercomSDKPrivate18WebHeadersInjector)
final class WebHeadersInjector {
    let kICMHelpCenterWebview = "iOSIntercomWebView"
    let kICMHelpCenterMobileClient = "MobileClient"
    let kICMHelpCenterReactionsHidden = "MobileClientReactionsHidden"
    let kICMHelpCenterDisplayType = "MobileClientDisplayType"
    let kICMHeaderlessHelpCenter = "iOSIntercomHeaderless"

    // The original default header injection was inlined into its request builder.
    func injectDefaultHeaders(into request: inout URLRequest) {
        request.setValue(kICMHelpCenterWebview, forHTTPHeaderField: kICMHelpCenterMobileClient)
        request.setValue("true", forHTTPHeaderField: kICMHelpCenterReactionsHidden)
        request.setValue(kICMHeaderlessHelpCenter, forHTTPHeaderField: kICMHelpCenterDisplayType)
        injectUserSessionCookie(into: &request)
    }

    func injectUserSessionCookie(into request: inout URLRequest) {
        let identity = ICMIdentityStore.currentIdentity()
        guard let encrypted = identity.userIdentity.encryptedUserId else { return }
        guard let url = request.url, let host = url.host else { return }
        let name = "intercom-session-" + (identity.appIdentity.appId ?? "unknown")
        guard let cookie = HTTPCookie(properties: [.domain: host, .path: "/", .name: name, .value: encrypted]) else { return }
        for (key, value) in HTTPCookie.requestHeaderFields(with: [cookie]) {
            request.setValue(value, forHTTPHeaderField: key)
        }
        request.httpShouldHandleCookies = true
        withExtendedLifetime(identity) {}
    }
}
