require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "NetworkInfo"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-network-info.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift,c}"
  s.frameworks   = 'CoreLocation', 'SystemConfiguration', 'NetworkExtension'

  install_modules_dependencies(s)
end
