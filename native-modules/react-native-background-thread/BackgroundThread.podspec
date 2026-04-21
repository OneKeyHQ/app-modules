require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "BackgroundThread"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-background-thread.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift,cpp}", "cpp/**/*.{h,cpp}"
  s.public_header_files = "ios/BackgroundThreadManager.h", "ios/BTLogger.h"
  s.pod_target_xcconfig = { 'HEADER_SEARCH_PATHS' => '"$(PODS_TARGET_SRCROOT)/cpp"', 'DEFINES_MODULE' => 'YES' }
  s.user_target_xcconfig = { 'SWIFT_INCLUDE_PATHS' => '"$(PODS_ROOT)/Headers/Public/BackgroundThread"' }

  s.dependency 'ReactNativeNativeLogger'

  install_modules_dependencies(s)
end
