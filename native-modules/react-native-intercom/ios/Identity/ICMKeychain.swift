// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
import Security

@objc(_TtP18IntercomSDKPrivate18ICMKeychainStoring_)
protocol ICMKeychainStoring: NSObjectProtocol {
    @objc(stringForKey:) func string(forKey key: String) -> String?
    @objc(setString:forKey:) func setString(_ value: String, forKey key: String) -> Bool
    @objc(deleteValueForKey:) func deleteValue(forKey key: String) -> Bool
    @objc func clearAll() -> Bool
}

@objc(_TtC18IntercomSDKPrivate11ICMKeychain)
final class ICMKeychain: NSObject, ICMKeychainStoring {
    private let serviceIdentifier: String
    private static let defaultServiceIdentifier = (Bundle.main.bundleIdentifier ?? "io.intercom.sdk") + ".intercom-sdk.identity"
    @objc static let shared = ICMKeychain(serviceIdentifier: defaultServiceIdentifier)

    @objc(initWithServiceIdentifier:)
    init(serviceIdentifier: String) {
        self.serviceIdentifier = serviceIdentifier
        super.init()
    }

    override init() {
        fatalError("init() has not been implemented")
    }

    private func baseQuery(forKey key: String) -> [String: Any] {
        [kSecClass as String: kSecClassGenericPassword,
         kSecAttrService as String: serviceIdentifier,
         kSecAttrAccount as String: key]
    }

    private func data(forKey key: String) -> Data? {
        var query = baseQuery(forKey: key)
        query[kSecReturnData as String] = true
        query[kSecMatchLimit as String] = kSecMatchLimitOne
        var result: CFTypeRef?
        guard SecItemCopyMatching(query as CFDictionary, &result) == errSecSuccess else { return nil }
        return result as? Data
    }

    @objc(stringForKey:)
    func string(forKey key: String) -> String? {
        guard let value = data(forKey: key) else { return nil }
        return String(data: value, encoding: .utf8)
    }

    @objc(setString:forKey:)
    func setString(_ value: String, forKey key: String) -> Bool {
        guard let valueData = value.data(using: .utf8) else { return false }
        return setData(valueData, forKey: key)
    }

    private func setData(_ data: Data, forKey key: String) -> Bool {
        let query = baseQuery(forKey: key)
        let attributes: [String: Any] = [kSecValueData as String: data,
                                       kSecAttrAccessible as String: kSecAttrAccessibleAfterFirstUnlock]
        let status = SecItemUpdate(query as CFDictionary, attributes as CFDictionary)
        if status == errSecItemNotFound {
            let item = query.merging(attributes) { _, newValue in newValue }
            return SecItemAdd(item as CFDictionary, nil) == errSecSuccess
        }
        return status == errSecSuccess
    }

    @objc(deleteValueForKey:)
    func deleteValue(forKey key: String) -> Bool {
        let status = SecItemDelete(baseQuery(forKey: key) as CFDictionary)
        return status == errSecSuccess || status == errSecItemNotFound
    }

    @objc func clearAll() -> Bool {
        let query: [String: Any] = [kSecClass as String: kSecClassGenericPassword,
                                   kSecAttrService as String: serviceIdentifier]
        let status = SecItemDelete(query as CFDictionary)
        return status == errSecSuccess || status == errSecItemNotFound
    }
}
