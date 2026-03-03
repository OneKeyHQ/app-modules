
import NitroModules
import LocalAuthentication
import Security
import ReactNativeNativeLogger

class ReactNativeCheckBiometricAuthChanged: HybridReactNativeCheckBiometricAuthChangedSpec {

  private static let keychainKey = "com.onekey.biometricAuthState"

  private static func loadDomainState() -> Data? {
    let query: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrAccount as String: keychainKey,
      kSecReturnData as String: true,
      kSecMatchLimit as String: kSecMatchLimitOne
    ]
    var result: AnyObject?
    let status = SecItemCopyMatching(query as CFDictionary, &result)
    if status == errSecSuccess {
      return result as? Data
    }
    return nil
  }

  private static func saveDomainState(_ data: Data?) {
    guard let data = data else { return }
    let query: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrAccount as String: keychainKey,
      kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlockedThisDeviceOnly
    ]
    let attrs: [String: Any] = [
      kSecValueData as String: data
    ]
    let status = SecItemUpdate(query as CFDictionary, attrs as CFDictionary)
    if status == errSecItemNotFound {
      var addQuery = query
      addQuery[kSecValueData as String] = data
      SecItemAdd(addQuery as CFDictionary, nil)
    }
  }

  func checkChanged() throws -> Promise<Bool> {
    return Promise.async {
      var changed = false
      let context = LAContext()
      _ = context.canEvaluatePolicy(.deviceOwnerAuthentication, error: nil)
      let domainState = context.evaluatedPolicyDomainState
      if domainState == nil {
          OneKeyLog.warn("Biometric", "evaluatedPolicyDomainState is nil, cannot determine biometric changes")
          return false
      }
      let oldDomainState = Self.loadDomainState()
      if let oldDomainState = oldDomainState {
          changed = oldDomainState != domainState
      } else {
          // Migrate from UserDefaults if exists
          let defaults = UserDefaults.standard
          if let legacyState = defaults.data(forKey: "biometricAuthState") {
              changed = legacyState != domainState
              defaults.removeObject(forKey: "biometricAuthState")
          } else {
              OneKeyLog.info("Biometric", "No previous biometric state stored, saving initial state")
          }
      }
      Self.saveDomainState(domainState)
      if changed {
          OneKeyLog.info("Biometric", "Biometric auth change detected")
      }
      return changed
    }
  }
}
