import NitroModules
import LocalAuthentication
import Security

class ReactNativeCheckBiometricAuthChanged: HybridReactNativeCheckBiometricAuthChangedSpec {
  private static let keychainKey = "com.onekey.biometricAuthState"
  private static var identityQuery: [String: Any] {
    [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrAccount as String: keychainKey,
    ]
  }

  func checkChanged() throws -> Promise<Bool> {
    Promise.parallel {
      let defaults = UserDefaults.standard
      return try BiometricAuthStateStore.check(
        currentState: {
          let context = LAContext()
          _ = context.canEvaluatePolicy(.deviceOwnerAuthentication, error: nil)
          return context.evaluatedPolicyDomainState
        },
        read: {
          var query = Self.identityQuery
          query[kSecReturnData as String] = true
          query[kSecMatchLimit as String] = kSecMatchLimitOne
          var result: AnyObject?
          let status = SecItemCopyMatching(query as CFDictionary, &result)
          return (status, result)
        },
        update: { data in
          let attributes: [String: Any] = [kSecValueData as String: data]
          return SecItemUpdate(Self.identityQuery as CFDictionary, attributes as CFDictionary)
        },
        add: { data in
          var query = Self.identityQuery
          query[kSecAttrAccessible as String] = kSecAttrAccessibleWhenUnlockedThisDeviceOnly
          query[kSecValueData as String] = data
          return SecItemAdd(query as CFDictionary, nil)
        },
        legacyState: { defaults.data(forKey: "biometricAuthState") },
        removeLegacyState: { defaults.removeObject(forKey: "biometricAuthState") }
      )
    }
  }
}
