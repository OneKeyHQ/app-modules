import UIKit
import React
import React_RCTAppDelegate
import ReactAppDependencyProvider
import BackgroundThread

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
  var window: UIWindow?

  var reactNativeDelegate: ReactNativeDelegate?
  var reactNativeFactory: RCTReactNativeFactory?

  func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
  ) -> Bool {
    OneKeyLogBridge.info("App", message: "Application started")

    DispatchQueue.global(qos: .userInitiated).async {
        // BackgroundRunnerModule.startBackgroundRunner()
    }
    let delegate = ReactNativeDelegate()
    let factory = RCTReactNativeFactory(delegate: delegate)
    delegate.dependencyProvider = RCTAppDependencyProvider()

    reactNativeDelegate = delegate
    reactNativeFactory = factory

    window = UIWindow(frame: UIScreen.main.bounds)

    factory.startReactNative(
      withModuleName: "example",
      in: window,
      launchOptions: launchOptions
    )

    return true
  }
}
