require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeJpush"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-jpush.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/include/*.h",
    "ios/jcore/*.{h,m}",
    "ios/jpush/*.{h,m}",
    "ios/bridge/*.{h,m}",
    "ios/*.swift",
    "cpp/**/*.{hpp,cpp}",
  ]

  s.exclude_files = "ios/tests/**/*"

  s.public_header_files = [
    "ios/include/*.h",
    "ios/jcore/*.h",
    "ios/jpush/*.h",
    "ios/bridge/RCTJPushEventQueue.h",
    "ios/bridge/RCTJPushModule.h",
  ]

  s.frameworks = [
    "UIKit",
    "CFNetwork",
    "CoreFoundation",
    "CoreTelephony",
    "CoreLocation",
    "SystemConfiguration",
    "CoreGraphics",
    "Foundation",
    "Security",
    "Network",
    "UserNotifications",
  ]
  s.libraries = ["z", "resolv"]

  s.pod_target_xcconfig = {
    "HEADER_SEARCH_PATHS" => "\"$(PODS_TARGET_SRCROOT)/ios/include\" \"$(PODS_TARGET_SRCROOT)/ios/jcore\" \"$(PODS_TARGET_SRCROOT)/ios/jpush\" \"$(PODS_TARGET_SRCROOT)/ios/bridge\""
  }

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'ReactNativeNativeLogger'

  load 'nitrogen/generated/ios/ReactNativeJpush+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)

  s.test_spec "Tests" do |test_spec|
    test_spec.source_files = "ios/tests/**/*.{m,swift}"
  end
end
