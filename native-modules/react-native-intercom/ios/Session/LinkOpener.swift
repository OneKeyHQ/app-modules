// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import UIKit
import os

@objc(_TtP18IntercomSDKPrivate18LinkOpenerProtocol_)
protocol LinkOpenerProtocol {
    func isExternalLink(_ url: URL) -> Bool
    func openLink(_ url: URL)
    func openLink(_ url: URL, sourceBlockIsPartOfAIAnswer: Bool)
}

@objc(ICMLinkOpener)
class LinkOpener: NSObject, LinkOpenerProtocol {
    private let bundleProvider: () -> Bundle
    private let helpCenterUrlStringsProvider: () -> [String]
    private let urlTracker: (String) -> Void
    private let urlOpener: (URL) -> Void
    private let appDelegateProvider: () -> UIApplicationDelegate?
    private let sceneActivityDeliverer: (NSUserActivity) -> Bool
    private let sceneURLDeliverer: (URL) -> Bool
    private static let allowedRegularURLSchemes: Set<String> = ["http", "https"]

    @objc static let shared = LinkOpener(bundleProvider: { .main })
    @objc init(bundleProvider: @escaping () -> Bundle = { .main },
               helpCenterUrlStringsProvider: @escaping () -> [String] = { (ICMConfig.shared().helpCenterUrlStrings ?? []) as! [String] },
               urlTracker: @escaping (String) -> Void = { ICMHTTPClient.trackURL($0) },
               urlOpener: @escaping (URL) -> Void = { url in
                   UIApplication.shared.open(url, options: [:]) { success in
                       if !success { ICMLogger.debugLog("INFO - cannot open URL: " + url.absoluteString) }
                   }
               },
               appDelegateProvider: @escaping () -> UIApplicationDelegate? = { UIApplication.shared.delegate },
               sceneActivityDeliverer: @escaping (NSUserActivity) -> Bool = LinkOpener.deliverActivityToConnectedScene,
               sceneURLDeliverer: @escaping (URL) -> Bool = LinkOpener.deliverURLToConnectedScene) {
        self.bundleProvider = bundleProvider
        self.helpCenterUrlStringsProvider = helpCenterUrlStringsProvider
        self.urlTracker = urlTracker
        self.urlOpener = urlOpener
        self.appDelegateProvider = appDelegateProvider
        self.sceneActivityDeliverer = sceneActivityDeliverer
        self.sceneURLDeliverer = sceneURLDeliverer
        super.init()
    }

    @objc func openLink(_ url: URL) {
        if isIntercomTrackingURL(url) { openIntercomTrackingURL(url) }
        else if isCustomSchemeURL(url) { openCustomSchemeURL(url) }
        else if isUniversalLink(url) { openUniversalLink(url) }
        else if isHelpCenterLink(url) { openHelpCenterLink(url, allowArticleReactions: true) }
        else { openRegularURL(url) }
    }
    @objc func openLink(_ url: URL, sourceBlockIsPartOfAIAnswer: Bool) {
        if isIntercomTrackingURL(url) { openIntercomTrackingURL(url) }
        else if isCustomSchemeURL(url) { openCustomSchemeURL(url) }
        else if isUniversalLink(url) { openUniversalLink(url) }
        else if isHelpCenterLink(url) { openHelpCenterLink(url, allowArticleReactions: !sourceBlockIsPartOfAIAnswer) }
        else { openRegularURL(url) }
    }
    @objc func isExternalLink(_ url: URL) -> Bool {
        !isHelpCenterLink(url) && !isUniversalLink(url) && !isCustomSchemeURL(url)
    }
    @objc func isHelpCenterLink(_ url: URL) -> Bool {
        var effective = url
        if isIntercomTrackingURL(effective), let extracted = extractURLFromLinkTrackingURL(effective) { effective = extracted }
        let strings = helpCenterUrlStringsProvider()
        for string in strings {
            guard let candidate = URL(string: string) else { continue }
            if effective.host == candidate.host && effective.absoluteString.contains(string) { return true }
        }
        return false
    }
    @objc func isCustomSchemeURL(_ url: URL) -> Bool {
        guard let scheme = url.scheme, !scheme.isEmpty else { return false }
        guard let types = bundleProvider().object(forInfoDictionaryKey: "CFBundleURLTypes") as? [[String: Any]] else { return false }
        for type in types {
            guard let schemes = type["CFBundleURLSchemes"] as? [String] else { continue }
            for candidate in schemes where scheme.caseInsensitiveCompare(candidate) == .orderedSame { return true }
        }
        return false
    }
    @objc func extractedURLIsCustomSchemeURL(_ url: URL) -> Bool {
        guard let extracted = extractURLFromLinkTrackingURL(url) else { return false }
        return isCustomSchemeURL(extracted)
    }
    @objc func isUniversalLink(_ url: URL) -> Bool {
        ICMLogger.debugLog("Checking universal link: [" + String(describing: url) + "]")
        var effective = url
        if isIntercomTrackingURL(effective), let extracted = extractURLFromLinkTrackingURL(effective) { effective = extracted }
        guard let host = effective.host, !host.isEmpty else { return false }
        guard let domains = bundleProvider().object(forInfoDictionaryKey: "IntercomUniversalLinkDomains") as? [String] else { return false }
        for entry in domains {
            let domain = extractDomainHost(from: entry)
            ICMLogger.debugLog("Comparing URL [" + String(describing: effective) + "] to Domain [" + domain + "]")
            if host.caseInsensitiveCompare(domain) == .orderedSame
                || host.caseInsensitiveCompare("www." + domain) == .orderedSame
                || ("www." + host).caseInsensitiveCompare(domain) == .orderedSame {
                ICMLogger.debugLog("URL [" + String(describing: effective) + "] is a universal link")
                return true
            }
        }
        ICMLogger.debugLog("URL [" + String(describing: effective) + "] is NOT a universal link")
        return false
    }
    private func valueForParam(_ parameter: String, inQueryString query: String?) -> String? {
        guard let query else { return nil }
        for component in query.components(separatedBy: "&") {
            let parts = component.components(separatedBy: "=")
            if parts.count >= 2 && parts[0].lowercased() == parameter.lowercased() {
                return parts.dropFirst().joined(separator: "")
            }
        }
        return nil
    }
    @objc func isIntercomTrackingURL(_ url: URL) -> Bool {
        guard let host = url.host else { return false }
        if host.contains("via.intercom.io") || host.contains("via.eu.intercom.io") || host.contains("via.au.intercom.io") {
            if let value = valueForParam("url", inQueryString: url.query), !value.isEmpty { return true }
        }
        return false
    }
    @objc func extractURLFromLinkTrackingURL(_ url: URL) -> URL? {
        guard let parameter = valueForParam("url", inQueryString: url.query) else { return nil }
        if let decoded = parameter.removingPercentEncoding, let result = URL(string: decoded) { return result }
        return handleCyrillicCharacters(parameter)
    }
    private func handleCyrillicCharacters(_ string: String) -> URL? {
        guard let decoded = string.removingPercentEncoding,
              let encoded = decoded.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) else { return nil }
        return URL(string: encoded)
    }
    @objc func extractDomainHost(from string: String) -> String {
        guard let url = URL(string: string) else { return string }
        if url.scheme != nil, let host = url.host { return host }
        return url.absoluteString
    }
    @objc func doesLinkContainHelpCenterID(_ string: String) -> Bool {
        guard let expression = try? NSRegularExpression(pattern: "^\\d+", options: .caseInsensitive) else { return false }
        return expression.rangeOfFirstMatch(in: string, options: [], range: NSRange(location: 0, length: string.utf16.count)).location == 0
    }
    @objc func extractHelpCenterIDFromLink(_ string: String) -> String {
        guard let expression = try? NSRegularExpression(pattern: "\\d+", options: .caseInsensitive),
              let match = expression.firstMatch(in: string, options: [], range: NSRange(location: 0, length: string.utf16.count)),
              let range = Range(match.range, in: string) else { return string }
        return String(string[range])
    }
    @objc func openHelpCenterLink(_ url: URL, allowArticleReactions: Bool) {
        let component = url.lastPathComponent
        guard doesLinkContainHelpCenterID(component) else {
            ICMPresentationManager.sharedInstance().presentHelpCenter()
            return
        }
        let identifier = extractHelpCenterIDFromLink(component)
        let components = url.pathComponents
        let parent = components[components.count - 2]
        if parent == "articles" {
            ICMPresentationManager.sharedInstance().presentArticle(identifier, allowReactions: allowArticleReactions, anchorLink: url.fragment)
        } else if parent == "collections" {
            ICMPresentationManager.sharedInstance().presentHelpCenterCollection(identifier)
        } else {
            ICMPresentationManager.sharedInstance().presentHelpCenter()
        }
    }
    private func openRegularURL(_ url: URL) {
        if let scheme = url.scheme?.lowercased(), Self.allowedRegularURLSchemes.contains(scheme) {
            urlOpener(url)
        } else {
            ICMLogger.log("[Intercom] Blocked URL with disallowed scheme '" + (url.scheme ?? "nil") + "'. Only http and https are permitted.", type: .error)
        }
    }
    private func openIntercomTrackingURL(_ url: URL) {
        urlTracker(url.absoluteString)
        if let extracted = extractURLFromLinkTrackingURL(url) { openLink(extracted) }
    }
    private func openCustomSchemeURL(_ url: URL) {
        if let delegate = appDelegateProvider(), delegate.responds(to: #selector(UIApplicationDelegate.application(_:open:options:))) {
            NotificationCenter.default.post(name: NSNotification.Name("kICMIntercomDeepLinkNotification"), object: nil)
            _ = delegate.application?(UIApplication.shared, open: url, options: [:])
            return
        }
        if sceneURLDeliverer(url) {
            NotificationCenter.default.post(name: NSNotification.Name("kICMIntercomDeepLinkNotification"), object: nil)
        } else { openRegularURL(url) }
    }
    private func openUniversalLink(_ url: URL) {
        ICMLogger.debugLog("Opening universal Link: " + String(describing: url))
        let activity = NSUserActivity(activityType: NSUserActivityTypeBrowsingWeb)
        activity.webpageURL = url
        if deliverActivityToAppDelegate(activity) || sceneActivityDeliverer(activity) {
            NotificationCenter.default.post(name: NSNotification.Name("kICMIntercomDeepLinkNotification"), object: nil)
        } else {
            ICMLogger.debugLog("Method continueUserActivity could not open universal link. Opening as regular URL")
            openRegularURL(url)
        }
    }
    private func deliverActivityToAppDelegate(_ activity: NSUserActivity) -> Bool {
        guard let delegate = appDelegateProvider(),
              delegate.responds(to: #selector(UIApplicationDelegate.application(_:continue:restorationHandler:))) else { return false }
        return delegate.application?(UIApplication.shared, continue: activity, restorationHandler: { _ in }) ?? false
    }
    private static func deliverActivityToConnectedScene(_ activity: NSUserActivity) -> Bool {
        let scenes = ICMUtils.foregroundOrderedWindowScenes()!
        for scene in scenes {
            if let delegate = scene.delegate, delegate.responds(to: #selector(UISceneDelegate.scene(_:continue:))) {
                ICMLogger.debugLog("Delivering universal link to scene delegate: " + String(describing: type(of: delegate)))
                delegate.scene?(scene, continue: activity)
                return true
            }
        }
        return false
    }
    private static func deliverURLToConnectedScene(_ url: URL) -> Bool {
        let scenes = ICMUtils.foregroundOrderedWindowScenes()!
        for scene in scenes {
            if let delegate = scene.delegate, delegate.responds(to: #selector(UISceneDelegate.scene(_:openURLContexts:))) {
                ICMLogger.debugLog("Delivering custom scheme URL to scene delegate: " + String(describing: type(of: delegate)))
                scene.open(url, options: nil, completionHandler: nil)
                return true
            }
        }
        return false
    }
}
