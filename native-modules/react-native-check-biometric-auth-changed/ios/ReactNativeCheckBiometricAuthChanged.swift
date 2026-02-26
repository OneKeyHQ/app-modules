
import NitroModules
import LocalAuthentication
import ReactNativeNativeLogger

class ReactNativeCheckBiometricAuthChanged: HybridReactNativeCheckBiometricAuthChangedSpec {
  func checkChanged() throws -> Promise<Bool> {
    return Promise.async {
      var changed = false
      let context = LAContext()
      _ = context.canEvaluatePolicy(.deviceOwnerAuthentication, error: nil)
      let domainState = context.evaluatedPolicyDomainState
      let defaults = UserDefaults.standard
      let oldDomainState = defaults.data(forKey: "biometricAuthState")
      if let oldDomainState = oldDomainState {
          changed = oldDomainState != domainState
      } else {
          OneKeyLog.info("Biometric", "No previous biometric state stored, saving initial state")
      }
      defaults.set(domainState, forKey: "biometricAuthState")
      defaults.synchronize()
      if changed {
          OneKeyLog.warn("Biometric", "Biometric auth change detected")
      }
      return changed
    }
  }
}
