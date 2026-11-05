import Foundation
@main struct LocaleTests {
    static func main() throws {
        let locales = TranslationManager.supportedLocales
        precondition(NSClassFromString("ICMTranslationManager") == TranslationManager.self)
        var changed = locales
        changed.removeFirst()
        precondition(TranslationManager.supportedLocales.count == 47 && changed.count == 46)
        let result: [String: Any] = ["status": "passed", "testCount": 2, "locales": locales,
            "scope": "Supported-locales getter only; translation implementation remains missing"]
        let data = try JSONSerialization.data(withJSONObject: result, options: [.sortedKeys])
        print(String(decoding: data, as: UTF8.self))
    }
}
