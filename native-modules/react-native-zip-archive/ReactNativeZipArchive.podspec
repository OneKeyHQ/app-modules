require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeZipArchive"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-zip-archive.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{m,mm}",
    "cpp/**/*.{hpp,cpp}",
  ]

  # >= 2.5.4 pulls the fix for the zip-slip / symlink path-traversal CVEs
  # (CVE-2023-39139 etc.) — important since OTA unzips downloaded archives.
  s.dependency 'SSZipArchive', '>= 2.5.4'

  load 'nitrogen/generated/ios/ReactNativeZipArchive+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
end
