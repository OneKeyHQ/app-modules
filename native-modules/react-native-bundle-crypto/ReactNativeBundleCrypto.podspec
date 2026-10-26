require "json"
require "fileutils"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

# npm omits symlinks from versioned macOS frameworks. Restore the packaged
# Catalyst slice layout before CocoaPods resolves its module and binary.
catalyst_framework = File.join(__dir__, 'ios/Frameworks/Gopenpgp.xcframework/ios-arm64-maccatalyst/Gopenpgp.framework')
if File.directory?(File.join(catalyst_framework, 'Versions/A'))
  links = { 'Versions/Current' => 'A' }
  %w[Gopenpgp Headers Modules Resources].each do |entry|
    links[entry] = "Versions/Current/#{entry}"
  end
  links.each do |entry, target|
    destination = File.join(catalyst_framework, entry)
    FileUtils.ln_s(target, destination) unless File.exist?(destination) || File.symlink?(destination)
  end
end

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
  s.exclude_files = "ios/tests/**/*"

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

  s.test_spec "Tests" do |test_spec|
    test_spec.source_files = "ios/tests/**/*.swift"
  end
end
