import NitroModules
import ReactNativeNativeLogger

class ReactNativeSplashScreen: HybridReactNativeSplashScreenSpec {

    func preventAutoHideAsync() throws -> Promise<Bool> {
        OneKeyLog.debug("SplashScreen", "preventAutoHideAsync - iOS uses expo-splash-screen")
        return Promise.resolved(withResult: true)
    }

    func hideAsync() throws -> Promise<Bool> {
        OneKeyLog.debug("SplashScreen", "hideAsync - iOS uses expo-splash-screen")
        return Promise.resolved(withResult: true)
    }
}
