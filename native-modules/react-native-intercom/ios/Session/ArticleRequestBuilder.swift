// Source recovery modifications: see docs/ARTICLE_RECOVERY.md.
import Foundation

struct ArticleRequestBuilder {
    func injectedArticleRequest(articleID: String, theme: String) -> URLRequest {
        let base: String
        if !ICMConfig.shared().helpCenterUrlString.isEmpty {
            base = ICMConfig.shared().helpCenterUrlString
        } else {
            base = "https://intercom.help/SDKTestApp"
        }
        let url = URL(string: base + "/articles/" + articleID + "?theme=" + theme)!
        var request = URLRequest(url: url, cachePolicy: .useProtocolCachePolicy, timeoutInterval: 60)
        WebHeadersInjector().injectDefaultHeaders(into: &request)
        return request
    }
}
