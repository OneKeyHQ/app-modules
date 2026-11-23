require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "PerpDepthBar"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/native-views/react-native-perp-depth-bar.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{m,mm}",
    "ios/PerpDepthBarInterop.hpp",
    "cpp/**/*.{hpp,cpp}",
  ]
  s.exclude_files = "ios/tests/**/*"
  s.public_header_files = "ios/PerpDepthBarInterop.hpp"

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'

  load 'nitrogen/generated/ios/PerpDepthBar+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)

  s.test_spec "Tests" do |test_spec|
    test_spec.source_files = "ios/tests/**/*.swift"
    test_spec.pod_target_xcconfig = {
      "SWIFT_OBJC_INTEROP_MODE" => "objcxx"
    }
  end
end
