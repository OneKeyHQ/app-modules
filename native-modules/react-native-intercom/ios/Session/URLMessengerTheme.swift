import UIKit

extension URL {
    func appended(by style: UIUserInterfaceStyle) -> URL {
        let components: URLComponents
        if var original = URLComponents(url: self, resolvingAgainstBaseURL: false) {
            var items = original.queryItems ?? []
            items.append(URLQueryItem(name: "theme", value: style.rawValue == 2 ? "dark" : "light"))
            original.queryItems = items
            components = original
        } else { components = URLComponents() }
        return components.url ?? self
    }
}
