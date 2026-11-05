import Foundation

@objc(ICMTranslationManager)
final class TranslationManager: NSObject {
    private let localeProvider: () -> NSLocale?
    @objc static let messenger = TranslationManager(localeProvider: { ICMConfig.shared().intercomLocale as NSLocale? })
    @objc static let helpCenter = TranslationManager(localeProvider: { ICMConfig.shared().helpCenterLocale as NSLocale? })
    @objc var translationLocale: NSLocale? { localeProvider() }

    @objc init(localeProvider: @escaping () -> NSLocale?) {
        self.localeProvider = localeProvider
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    @objc(translationForKey:)
    func translation(forKey key: String) -> String {
        let locale = localeProvider()
        var table = locale?.localeIdentifier.lowercased() ?? "en"
        guard let bundle = ICMUtils.translationsBundle() else { return key }
        if bundle.path(forResource: table, ofType: "strings") == nil { table = "en" }
        let text = bundle.localizedString(forKey: key, value: "", table: table)
        if text == key { return bundle.localizedString(forKey: key, value: "", table: "en") }
        return text
    }
    @objc(translationForKey:params:)
    func translation(forKey key: String, params: [String: String]) -> String {
        var text = translation(forKey: key)
        for (name, value) in params { text = text.replacingOccurrences(of: "{" + name + "}", with: value) }
        return text
    }

    @objc static let supportedLocales = [
        "ar", "bg", "bn", "bs", "ca", "cs", "da", "de", "de-form", "el", "en", "es",
        "et", "fa-IR", "fi", "fr", "he", "hi", "hr", "hu", "hy", "id", "it", "ja",
        "ka", "ko", "lt", "lv", "mn", "ms", "nl", "nb", "pl", "pt-PT", "pt-BR",
        "ro", "ru", "sl", "sr", "sv", "sw", "th", "tr", "uk", "vi", "zh-Hant", "zh-Hans"
    ]
}
