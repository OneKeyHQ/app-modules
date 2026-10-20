require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativePhotoLibrary"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-photo-library.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{m,mm}",
    "cpp/**/*.{hpp,cpp}",
  ]

  s.exclude_files = "ios/tests/**/*"
  s.frameworks = "Photos", "ImageIO", "UniformTypeIdentifiers"

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'ReactNativeNativeLogger'

  load 'nitrogen/generated/ios/ReactNativePhotoLibrary+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
  s.test_spec "Tests" do |test_spec|
    test_spec.source_files = "ios/tests/**/*.swift"
  end
end
