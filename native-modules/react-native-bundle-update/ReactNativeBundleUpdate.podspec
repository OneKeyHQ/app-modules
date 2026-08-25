require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeBundleUpdate"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-bundle-update.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{m,mm}",
    "cpp/**/*.{hpp,cpp}",
  ]

  # When ONEKEY_ALLOW_SKIP_GPG_VERIFICATION env var is set to a non-empty, non-'false' value,
  # enable the ALLOW_SKIP_GPG_VERIFICATION Swift compilation condition.
  # Without this flag, all skip-GPG code paths are compiled out (dead code elimination).
  if ENV['ONEKEY_ALLOW_SKIP_GPG_VERIFICATION'] && ENV['ONEKEY_ALLOW_SKIP_GPG_VERIFICATION'] != '' && ENV['ONEKEY_ALLOW_SKIP_GPG_VERIFICATION'] != 'false'
    s.pod_target_xcconfig = {
      'SWIFT_ACTIVE_COMPILATION_CONDITIONS' => '$(inherited) ALLOW_SKIP_GPG_VERIFICATION'
    }
  end

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'ReactNativeNativeLogger'
  # Shared GPG/sha256/path-safety security core. iOS verification + hashing now
  # delegates to react-native-bundle-crypto's BundleCryptoCore, which owns the
  # single in-tree Gopenpgp.xcframework — removing the duplicate framework that
  # previously caused a same-named pod conflict on iOS.
  s.dependency 'ReactNativeBundleCrypto'
  # Shared concurrent + background multi-range downloader. The iOS bundle
  # download now delegates its 8-range background download to
  # react-native-range-downloader's RangeDownloader.shared (the concurrent
  # downloader that originated here), removing the in-tree duplicate.
  s.dependency 'ReactNativeRangeDownloader'
  # >= 2.5.4 pulls the fix for the zip-slip / symlink path-traversal CVEs
  # (CVE-2023-39139 etc.) — important since OTA unzips downloaded archives.
  s.dependency 'SSZipArchive', '>= 2.5.4'
  s.dependency 'MMKV', '2.4.0'

  load 'nitrogen/generated/ios/ReactNativeBundleUpdate+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
end
