require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeNativeLogger"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/native-logger.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "cpp/**/*.{hpp,cpp}",
  ]

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'CocoaLumberjack/Swift', '~> 3.8'

  load 'nitrogen/generated/ios/ReactNativeNativeLogger+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
end
