require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))
public_headers = [
  "ios/Intercom.h", "ios/IntercomSourceImports.h",
  "ios/Models/ICMUserAttributes.h", "ios/Models/ICMCompany.h",
  "ios/Models/ICMHelpCenterCollection.h", "ios/Models/ICMHelpCenterCollectionContent.h",
  "ios/Models/ICMHelpCenterArticleSearchResult.h", "ios/Models/ICMHelpCenterDataError.h",
  "ios/Models/ICMHelpCenterArticle.h", "ios/Models/ICMHelpCenterArticleAuthor.h",
  "ios/Models/IntercomContent.h"
]

Pod::Spec.new do |s|
  s.name = "ReactNativeIntercom"
  s.module_name = "Intercom"
  s.header_dir = "Intercom"
  s.version = package["version"]
  s.summary = package["description"]
  s.homepage = package["homepage"]
  s.license = package["license"]
  s.authors = package["author"]
  s.platforms = { :ios => "15.0" }
  s.swift_version = "5.0"
  s.source = { :git => "https://github.com/OneKeyHQ/app-modules.git", :tag => "#{s.version}" }
  s.source_files = "ios/**/*.{h,m,mm,swift}"
  s.public_header_files = public_headers
  s.private_header_files = Dir.glob(File.join(__dir__, "ios/**/*.h")).map { |path| path.delete_prefix(__dir__ + "/") } - public_headers
  s.header_mappings_dir = "ios"
  s.preserve_paths = "ios/Session/*.inc"
  s.resources = ["ios/Resources/Intercom.bundle", "ios/Resources/IntercomTranslations.bundle"]
  s.frameworks = ["UIKit", "UserNotifications", "AVFoundation", "AVKit", "WebKit", "UniformTypeIdentifiers", "QuickLook", "Security", "CoreTelephony", "SystemConfiguration", "ImageIO", "AudioToolbox", "Photos", "PhotosUI", "CoreLocation"]
  s.libraries = "xml2"
  s.pod_target_xcconfig = {
    "DEFINES_MODULE" => "YES",
    "HEADER_SEARCH_PATHS" => '$(inherited) "$(PODS_TARGET_SRCROOT)/ios" "$(SDKROOT)/usr/include/libxml2"',
    "OTHER_SWIFT_FLAGS" => "$(inherited) -Xcc -DINTERCOM_SOURCE_BUILD=1",
    "GCC_PREPROCESSOR_DEFINITIONS" => "$(inherited) INTERCOM_SOURCE_BUILD=1",
    "CLANG_CXX_LANGUAGE_STANDARD" => "c++20"
  }
  install_modules_dependencies(s)
end
