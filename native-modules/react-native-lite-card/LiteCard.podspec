require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "LiteCard"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/native-modules.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{h,m,mm}",
    "cpp/**/*.{hpp,cpp}",
    "keys/**/*.{h,c}",
  ]
  s.vendored_framework = "ios/GPChannelSDKCore.xcframework"
  
  # Configure Swift and Objective-C interop
  s.swift_version = "5.0"
  s.pod_target_xcconfig = {
    'SWIFT_OBJC_BRIDGING_HEADER' => '$(PODS_TARGET_SRCROOT)/ios/LiteCard-Bridging-Header.h'
  }
  
  # Public headers for module mapping
  s.public_header_files = [
    "ios/Classes/OKLiteManager.h",
    "ios/Classes/OKNFCLiteDefine.h",
    "ios/Classes/OKNFCBridge.h"
  ]

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'

  load 'nitrogen/generated/ios/LiteCard+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
end
