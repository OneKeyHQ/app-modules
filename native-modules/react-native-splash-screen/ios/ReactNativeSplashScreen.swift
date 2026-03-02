import NitroModules

// iOS splash screen is managed by expo-splash-screen via the launch storyboard.
// This module exists solely to provide Android < API 31 compatibility (see SplashScreenBridge.kt).
// On iOS, both methods are intentional no-ops.
class ReactNativeSplashScreen: HybridReactNativeSplashScreenSpec {

    func preventAutoHideAsync() throws -> Promise<Bool> {
        return Promise.resolved(withResult: true)
    }

    func hideAsync() throws -> Promise<Bool> {
        return Promise.resolved(withResult: true)
    }
}
