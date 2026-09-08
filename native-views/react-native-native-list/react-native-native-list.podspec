require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-native-list"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]
  s.ios.deployment_target = "15.5"
  s.module_name  = "NativeListModule"
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules.git", :tag => "#{s.version}" }
  s.source_files = ["ios/**/*.{swift,h,m,mm,cpp}"]
  s.resource_bundles = {
    "NativeListResources" => [
      "common/fonts/*.ttf",
      "ios/Resources/NativeListIcons.xcassets"
    ]
  }
  s.static_framework = true

  s.pod_target_xcconfig = {
    "PRODUCT_MODULE_NAME" => "NativeListModule",
    "DEFINES_MODULE" => "YES",
    "SWIFT_OBJC_INTEROP_MODE" => "objcxx",
    "CLANG_CXX_LANGUAGE_STANDARD" => "c++20"
  }

  s.dependency "OneKeyImage", package["peerDependencies"]["@onekeyfe/react-native-image"]
  s.dependency "React-jsi"
  s.dependency "React-callinvoker"

  load "nitrogen/generated/ios/NativeListModule+autolinking.rb"
  add_nitrogen_files(s)
  install_modules_dependencies(s)
end
