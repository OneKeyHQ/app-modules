class ReactNativeCheckBiometricAuthChanged: HybridReactNativeCheckBiometricAuthChangedSpec {
    public func multiply(a: Double, b: Double) throws -> Double {
        return a * b
    }

    public func checkChanged() throws -> Bool {
        var changed = false
        let context = LAContext()
        _ = context.canEvaluatePolicy(.deviceOwnerAuthentication, error: nil)
        let domainState = context.evaluatedPolicyDomainState
        let defaults = UserDefaults.standard
        let oldDomainState = defaults.data(forKey: "biometricAuthState")
        if let oldDomainState = oldDomainState {
            changed = oldDomainState != domainState
        }
        defaults.set(domainState, forKey: "biometricAuthState")
        defaults.synchronize()
        return changed
    }
}
