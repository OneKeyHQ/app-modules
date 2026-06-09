require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeBundleCrypto"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-bundle-crypto.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{m,mm}",
    "cpp/**/*.{hpp,cpp}",
  ]

  # Vendored Gopenpgp framework: GPG cleartext/detached signature verification.
  # This is the single in-tree copy used by bundle-crypto and by modules that
  # delegate to BundleCryptoCore instead of vendoring their own framework.
  s.vendored_frameworks = 'ios/Frameworks/Gopenpgp.xcframework'

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'ReactNativeNativeLogger'

  load 'nitrogen/generated/ios/ReactNativeBundleCrypto+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
end
