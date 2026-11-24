import UIKit
import React
import React_RCTAppDelegate
import ReactAppDependencyProvider
import BackgroundThread

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
  weak var window: UIWindow?
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
    return true
  }

  // iOS 27 terminates apps that have not adopted the scene lifecycle, so the
  // window is created by SceneDelegate instead of here.
  func application(
    _ application: UIApplication,
    configurationForConnecting connectingSceneSession: UISceneSession,
    options: UIScene.ConnectionOptions
  ) -> UISceneConfiguration {
    let configuration = UISceneConfiguration(
      name: "Default Configuration", sessionRole: connectingSceneSession.role)
    configuration.delegateClass = SceneDelegate.self
    return configuration
  }
}

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
  var window: UIWindow?

  func scene(
    _ scene: UIScene,
    willConnectTo session: UISceneSession,
    options connectionOptions: UIScene.ConnectionOptions
  ) {
    guard let windowScene = scene as? UIWindowScene,
      let appDelegate = UIApplication.shared.delegate as? AppDelegate,
      let factory = appDelegate.reactNativeFactory
    else { return }

    let window = UIWindow(windowScene: windowScene)
    self.window = window
    appDelegate.window = window
    factory.startReactNative(withModuleName: "example", in: window, launchOptions: nil)
  }

  func sceneDidDisconnect(_ scene: UIScene) {
    guard let appDelegate = UIApplication.shared.delegate as? AppDelegate,
      appDelegate.window === window
    else { return }
    appDelegate.window = nil
  }
}
