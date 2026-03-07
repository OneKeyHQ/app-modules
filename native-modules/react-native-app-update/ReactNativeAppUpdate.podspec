require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeAppUpdate"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-app-update.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{m,mm}",
    "cpp/**/*.{hpp,cpp}",
  ]

  # When ONEKEY_ALLOW_SKIP_GPG_VERIFICATION env var is set to a non-empty, non-'false' value,
  # enable the ALLOW_SKIP_GPG_VERIFICATION Swift compilation condition.
  if ENV['ONEKEY_ALLOW_SKIP_GPG_VERIFICATION'] && ENV['ONEKEY_ALLOW_SKIP_GPG_VERIFICATION'] != '' && ENV['ONEKEY_ALLOW_SKIP_GPG_VERIFICATION'] != 'false'
    s.pod_target_xcconfig = {
      'SWIFT_ACTIVE_COMPILATION_CONDITIONS' => '$(inherited) ALLOW_SKIP_GPG_VERIFICATION'
    }
  end

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'ReactNativeNativeLogger'

  load 'nitrogen/generated/ios/ReactNativeAppUpdate+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
end
