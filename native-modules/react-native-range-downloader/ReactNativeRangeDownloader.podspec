require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeRangeDownloader"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-range-downloader.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{h}",
    "ios/**/*.{m,mm}",
    "cpp/**/*.{hpp,cpp}",
  ]

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'ReactNativeNativeLogger'
  s.dependency 'SniConnect', package["version"]
  s.public_header_files = "ios/FirmwareArchiveMinizipBridge.h"
  s.pod_target_xcconfig = {
    'HEADER_SEARCH_PATHS' => '"$(PODS_ROOT)/SSZipArchive/SSZipArchive/minizip"',
  }

  s.dependency 'SSZipArchive', '>= 2.5.4'

  load 'nitrogen/generated/ios/ReactNativeRangeDownloader+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
end
