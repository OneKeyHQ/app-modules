import Foundation

enum MessengerAppCardJSBuilder {
    static let viewport = "var meta = document.createElement('meta');\nmeta.setAttribute('name', 'viewport');\nmeta.setAttribute('initial-scale', '1.0');\nmeta.setAttribute('maximum-scale', '1.0');\nmeta.setAttribute('minimum-scale', '1.0');\nmeta.setAttribute('user-scalable', 'no');\ndocument.getElementsByTagName('head')[0].appendChild(meta);"
    static let selection = "document.documentElement.style.webkitUserSelect='none';"
    static let touchCallout = "document.documentElement.style.webkitTouchCallout='none';"
    static let bridgePrefix = "iOSHost = { handleAction: function(action) {window.webkit.messageHandlers."
    static let bridgeSuffix = ".postMessage(action);}}"

    static func updateTextArguments(text: String, inputId: String?) -> [String: Any] {
        let encoded = text.addingPercentEncoding(withAllowedCharacters: .alphanumerics) ?? ""
        return ["eventName": "UPDATE_TEXT", "payload": ["value": encoded, "id": inputId ?? ""]]
    }
}
