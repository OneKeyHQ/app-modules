require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "SniConnect"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-sni-connect.git", :tag => "#{s.version}" }

  s.static_framework = true

  s.source_files = [
    "ios/*.{swift}",
    "ios/*.{m,mm}"
  ]

  s.dependency 'React-Core'
  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'EMASCurl', '1.5.5'

  s.pod_target_xcconfig = {
    "HEADER_SEARCH_PATHS" => "\"$(PODS_ROOT)/Headers/Private/React-Core\"",
    "DEFINES_MODULE" => "YES"
  }


  install_modules_dependencies(s)
end
