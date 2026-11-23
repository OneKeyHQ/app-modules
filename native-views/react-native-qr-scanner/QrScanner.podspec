require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "QrScanner"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-views/react-native-qr-scanner.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{h,m,mm}",
    "cpp/**/*.{hpp,cpp}",
  ]
  s.exclude_files = "ios/tests/**/*"

  # QrZXingReader.h is the only Objective-C header Swift needs; it exposes
  # CoreGraphics types only, keeping ZXing headers out of this module.
  s.public_header_files = ["ios/QrZXingReader.h"]
  s.frameworks = ["AVFoundation", "Vision", "CoreImage", "ImageIO"]

  # Fallback decoder for still images. Pinned; see docs/SPEC.md.
  s.dependency 'zxing-cpp', '2.2.0'
  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'

  load 'nitrogen/generated/ios/QrScanner+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)

  s.test_spec "Tests" do |test_spec|
    test_spec.source_files = "ios/tests/**/*.swift"
    test_spec.resources = "ios/tests/fixtures/*"
  end
end
